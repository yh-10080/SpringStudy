package com.hqyj.StudySB.modules.account.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hqyj.StudySB.config.ResourceConfigBean;
import com.hqyj.StudySB.modules.account.dao.UserDao;
import com.hqyj.StudySB.modules.account.dao.UserRoleDao;
import com.hqyj.StudySB.modules.account.entity.Role;
import com.hqyj.StudySB.modules.account.entity.User;
import com.hqyj.StudySB.modules.account.service.UserService;
import com.hqyj.StudySB.modules.common.vo.Result;
import com.hqyj.StudySB.modules.common.vo.Result.ResultStatus;
import com.hqyj.StudySB.modules.common.vo.SearchVo;
import com.hqyj.StudySB.utils.FileUtil;
import com.hqyj.StudySB.utils.MD5Util;


@Service
public class UserServiceImpl implements UserService {

	private final static Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
	
	@Autowired
	private UserDao userDao;
	@Autowired
	private UserRoleDao userRoleDao;
	
	@Autowired
	private ResourceConfigBean resourceConfigBean;

	@Override
	public User getUserByUserName(String userName) {
		return userDao.getUserByUserName(userName);
	}

	@Override
	public Result<User> login(User user) {
//		User userTemp = userDao.getUserByUserName(user.getUserName());
//		if (userTemp == null || !userTemp.getPassword().equals(MD5Util.getMD5(user.getPassword()))) {
//			return new Result<User>(ResultStatus.FAILD.status, "User name or password error.");
//		}

		try {
			Subject subject = SecurityUtils.getSubject();
			
			UsernamePasswordToken usernamePasswordToken = 
					new UsernamePasswordToken(user.getUserName(), MD5Util.getMD5(user.getPassword()));
			usernamePasswordToken.setRememberMe(user.getRememberMe());
			
			subject.login(usernamePasswordToken);
			subject.checkRoles();
			
			//调用shiro中的session
			Session session = subject.getSession();
			//获取shiro中包装的user
			User userTemp = (User) subject.getPrincipal();
			//添加属性
			session.setAttribute("user", userTemp);
			
		} catch (Exception e) {
			e.printStackTrace();
			return new Result<User>(ResultStatus.FAILD.status, "User name or password error.");
		}
		return new Result<User>(ResultStatus.SUCCESS.status, "Login success.", user);
	}

	@Override
	public PageInfo<User> getUsersBySearchVo(SearchVo searchVo) {
		searchVo.initSearchVo();
		PageHelper.startPage(searchVo.getCurrentPage(), searchVo.getPageSize());
		return new PageInfo<User>(
				Optional.ofNullable(userDao.getUsersBySearchVo(searchVo)).orElse(Collections.emptyList()));
	}

	@Override
	public User getUserByUserId(int userId) {
		return userDao.getUserByUserId(userId);
	}

	@Override
	public Result<Object> deleteUser(int userId) {
		userDao.deleteUser(userId);
		userRoleDao.deletUserRoleByUserId(userId);
		return new Result<Object>(ResultStatus.SUCCESS.status, "Delete success.");
	}

	@Override
	@Transactional
	public Result<User> editUser(User user) {
		User userTemp = getUserByUserName(user.getUserName());
		if (userTemp != null && userTemp.getUserId() != user.getUserId()) {
			return new Result<User>(ResultStatus.FAILD.status, "User name is repeat.");
		}
		user.setPassword(MD5Util.getMD5(user.getPassword()));
		user.setCreateDate(new Date());
		if (user.getUserId() > 0) {//代表update
			userDao.updateUser(user);
			userRoleDao.deletUserRoleByUserId(user.getUserId());
		} else {//代表新增
			userDao.insertUser(user);
		}

		List<Role> roles = user.getRoles();
		if (roles != null && roles.size() > 0) {
			for (Role role : roles) {
				userRoleDao.addUserRole( role.getRoleId(),user.getUserId());
			}
		}

		return new Result<User>(ResultStatus.SUCCESS.status, "Edit success.", user);
	}

	@Override
	public Result<String> uploadUserImage(MultipartFile userImage) {
		
		if (userImage.isEmpty()) {
			return new Result<>(ResultStatus.FAILD.status, "User image is empty.");
		}
		if (!FileUtil.isImage(userImage)) {//判断传来的文件是否是img文件
			return new Result<>(ResultStatus.FAILD.status, "File is not a image.");
		}
		
		String originalFilename = userImage.getOriginalFilename();
		String relatedPath = resourceConfigBean.getResourcePath() + originalFilename;
		String destPath = String.format("%s%s", resourceConfigBean.getLocalPathForWindow(), originalFilename);
		try {
			File destFile = new File(destPath);
			userImage.transferTo(destFile);
			
		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
			LOGGER.debug(e.getMessage());
			return new Result<>(ResultStatus.FAILD.status, "File upload error.");
		}
		
		return new Result<>(ResultStatus.SUCCESS.status, "File upload success.", relatedPath);
	}

	@Override
	@Transactional
	public Result<User> updateUserProfile(User user) {
		User userTemp = getUserByUserName(user.getUserName());
		if (userTemp != null && userTemp.getUserId() != user.getUserId()) {
			return new Result<User>(ResultStatus.FAILD.status, "User name is repeat.");
		}

		userDao.updateUser(user);

		return new Result<User>(ResultStatus.SUCCESS.status, "Edit success.", user);
	}

	@Override
	public void logout() {
		//使用subject实现退出
		Subject subject = SecurityUtils.getSubject();
		subject.logout();
		
	}

}
