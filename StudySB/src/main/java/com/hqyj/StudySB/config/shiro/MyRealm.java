package com.hqyj.StudySB.config.shiro;

import java.util.List;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hqyj.StudySB.modules.account.entity.Resource;
import com.hqyj.StudySB.modules.account.entity.Role;
import com.hqyj.StudySB.modules.account.entity.User;
import com.hqyj.StudySB.modules.account.service.ResourceService;
import com.hqyj.StudySB.modules.account.service.RoleService;
import com.hqyj.StudySB.modules.account.service.UserService;

@Component
public class MyRealm extends AuthorizingRealm {

	@Autowired
	private UserService userService;
	@Autowired
	private RoleService roleService;
	@Autowired
	private ResourceService resourceService;

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		//资源授权
		SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();

		//获取参数，得到用户名
		User user = (User) principals.getPrimaryPrincipal();
		if (user == null) {
			throw new UnknownAccountException("This user name do not exist.");
		}

		List<Role> roles = roleService.getRolesByUserId(user.getUserId());
		for (Role role : roles) {
			simpleAuthorizationInfo.addRole(role.getRoleName());
			List<Resource> resources = resourceService.getResourcesByRoleId(role.getRoleId());
			for (Resource resource : resources) {
				simpleAuthorizationInfo.addStringPermission(resource.getPermission());
			}
		}

		return simpleAuthorizationInfo;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		//身份验证
		String userName = (String) token.getPrincipal();
		User user = userService.getUserByUserName(userName);
		if (user == null) {
			throw new UnknownAccountException("This user name do not exist.");
		}

		// 身份验证器，包装用户名和密码
		return new SimpleAuthenticationInfo(user, user.getPassword(), getName());
	}

}
