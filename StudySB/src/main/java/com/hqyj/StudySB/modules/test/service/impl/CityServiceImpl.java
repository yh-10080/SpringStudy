package com.hqyj.StudySB.modules.test.service.impl;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hqyj.StudySB.modules.common.vo.Result;
import com.hqyj.StudySB.modules.common.vo.Result.ResultStatus;
import com.hqyj.StudySB.modules.common.vo.SearchVo;
import com.hqyj.StudySB.modules.test.dao.CityDao;
import com.hqyj.StudySB.modules.test.entity.City;
import com.hqyj.StudySB.modules.test.service.CityService;
import com.hqyj.StudySB.utils.RedisUtils;

@Service
public class CityServiceImpl implements CityService {

	@Autowired
	private CityDao cityDao;
	
	@Autowired
	private RedisUtils redisUtils;
	
	@Override
	public List<City> getCitiesByCountryId(int countryId) {
		
		return Optional.ofNullable(cityDao.getCitiesByCountryId2(countryId))
				.orElse(Collections.emptyList());
	//	return cityDao.getCitiesByCountryId(countryId);
	}

	@Override
	public City getCityByName(String cityName, String localCityName) {
		return cityDao.getCityByName(cityName, localCityName);
	}



	@Override
	public PageInfo<City> getCitiesByPage(int currentPage, int pageSize, int countryId) {
		//将两参数设置到分页插件中，让插件知道这两个参数
		PageHelper.startPage(currentPage, pageSize);
		return new PageInfo<City>(Optional.ofNullable(cityDao.getCitiesByCountryId2(countryId))
				.orElse(Collections.emptyList()));
	}

	@Override
	public PageInfo<City> getCitiesBySearchVo(SearchVo searchVo) {
		searchVo.initSearchVo();
		PageHelper.startPage(searchVo.getCurrentPage(), searchVo.getPageSize());
		return new PageInfo<City>(
				Optional.of(cityDao.getCitiesBySearchVo(searchVo))
				.orElse(Collections.emptyList()));
	}
	
	@Override
	public Object migrateCitiesByCountryId(int countryId) {
		
		List<City> cities = getCitiesByCountryId(countryId);
		redisUtils.set("cities", cities);
		return redisUtils.get("cities");
	}

	@Override
	public Result<City> insertCity(City city) {
		city.setDateCreated(new Date());
		cityDao.insertCity(city);
		return new Result<City>(ResultStatus.SUCCESS.status, "Insert success", city);
	}

	@Override
	public Result<City> updateCity(City city) {
		cityDao.updateCity(city);
		
		return new Result<City>(ResultStatus.SUCCESS.status, "update success", city);
	}

	@Override
	@Transactional(noRollbackFor=ArithmeticException.class)//进行事务管理，要是遇到算术异常不进行数据的回滚
	public Result<Object> deleteCity(int cityId) {
		cityDao.deleteCity(cityId);
	//	int a = 1/0;
		return new Result<Object>(ResultStatus.SUCCESS.status, "delete success");
	}


}
