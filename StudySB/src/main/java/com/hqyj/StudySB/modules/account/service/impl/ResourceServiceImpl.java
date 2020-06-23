package com.hqyj.StudySB.modules.account.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hqyj.StudySB.modules.account.dao.ResourceDao;
import com.hqyj.StudySB.modules.account.dao.RoleResourceDao;
import com.hqyj.StudySB.modules.account.entity.Resource;
import com.hqyj.StudySB.modules.account.entity.Role;
import com.hqyj.StudySB.modules.account.service.ResourceService;
import com.hqyj.StudySB.modules.common.vo.Result;
import com.hqyj.StudySB.modules.common.vo.Result.ResultStatus;
import com.hqyj.StudySB.modules.common.vo.SearchVo;

@Service
public class ResourceServiceImpl implements ResourceService {
	@Autowired
	private ResourceDao resourceDao;
	@Autowired
	private RoleResourceDao roleResourceDao;

	@Override
	@Transactional
	public Result<Resource> editResource(Resource resource) {
		Resource resourceTemp = resourceDao.getResourceByPermission(resource.getPermission());
		if (resourceTemp != null && resourceTemp.getResourceId() != resource.getResourceId()) {
			return new Result<Resource>(ResultStatus.FAILD.status, "Resource permission is repeat.");
		}

		// 添加 resource
		if (resource.getResourceId() > 0) {
			resourceDao.updateResource(resource);
		} else {
			resourceDao.addResource(resource);
		}

		// 添加 roleResource
		roleResourceDao.deletRoleResourceByResourceId(resource.getResourceId());
		if (resource.getRoles() != null && !resource.getRoles().isEmpty()) {
			for (Role role : resource.getRoles()) {
				roleResourceDao.addRoleResource(role.getRoleId(), resource.getResourceId());
			}
		}

		return new Result<Resource>(ResultStatus.SUCCESS.status, "success", resource);
	}

	@Override
	@Transactional
	public Result<Resource> deleteResource(int resourceId) {
		roleResourceDao.deletRoleResourceByResourceId(resourceId);
		resourceDao.deleteResource(resourceId);
		return new Result<Resource>(ResultStatus.SUCCESS.status, "");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public PageInfo<Resource> getResources(SearchVo searchVo) {
		searchVo.initSearchVo();
		PageHelper.startPage(searchVo.getCurrentPage(), searchVo.getPageSize());
		return new PageInfo(
				Optional.ofNullable(resourceDao.getResourcesBySearchVo(searchVo)).orElse(Collections.emptyList()));
	}

	@Override
	public List<Resource> getResourcesByRoleId(int roleId) {
		return resourceDao.getResourcesByRoleId(roleId);
	}

	@Override
	public Resource getResourceById(int resourceId) {
		return resourceDao.getResourceById(resourceId);
	}
}
