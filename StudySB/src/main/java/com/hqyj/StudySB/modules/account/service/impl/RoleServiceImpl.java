package com.hqyj.StudySB.modules.account.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hqyj.StudySB.modules.account.dao.RoleDao;
import com.hqyj.StudySB.modules.account.entity.Role;
import com.hqyj.StudySB.modules.account.service.RoleService;
import com.hqyj.StudySB.modules.common.vo.Result;
import com.hqyj.StudySB.modules.common.vo.Result.ResultStatus;
import com.hqyj.StudySB.modules.common.vo.SearchVo;

@Service
public class RoleServiceImpl implements RoleService {

	@Autowired
	private RoleDao roleDao;

	@Override
	public List<Role> getRoles() {
		return Optional.ofNullable(roleDao.getRoles()).orElse(Collections.emptyList());
	}

	@Override
	@Transactional
	public Result<Role> editRole(Role role) {
		Role roleTemp = roleDao.getRoleByRoleName(role.getRoleName());
		if (roleTemp != null && roleTemp.getRoleId() != role.getRoleId()) {
			return new Result<Role>(ResultStatus.FAILD.status, "Role name is repeat.");
		}

		if (role.getRoleId() > 0) {
			roleDao.updateRole(role);
		} else {
			roleDao.addRole(role);
		}

		return new Result<Role>(ResultStatus.SUCCESS.status, "success", role);
	}

	@Override
	@Transactional
	public Result<Role> deleteRole(int roleId) {
		roleDao.deleteRole(roleId);
		return new Result<Role>(ResultStatus.SUCCESS.status, "");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public PageInfo<Role> getRoles(SearchVo searchVo) {
		searchVo.initSearchVo();
		PageHelper.startPage(searchVo.getCurrentPage(), searchVo.getPageSize());
		return new PageInfo(Optional.ofNullable(roleDao.getRolesBySearchVo(searchVo)).orElse(Collections.emptyList()));
	}

	@Override
	public List<Role> getRolesByUserId(int userId) {
		return roleDao.getRolesByUserId(userId);
	}

	@Override
	public List<Role> getRolesByResourceId(int resourceId) {
		return roleDao.getRolesByResourceId(resourceId);
	}

	@Override
	public Role getRoleById(int roleId) {
		return roleDao.getRoleById(roleId);
	}
}
