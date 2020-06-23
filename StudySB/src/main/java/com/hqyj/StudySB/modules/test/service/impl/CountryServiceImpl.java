package com.hqyj.StudySB.modules.test.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hqyj.StudySB.modules.test.dao.CountryDao;
import com.hqyj.StudySB.modules.test.entity.Country;
import com.hqyj.StudySB.modules.test.service.CountryService;

@Service
public class CountryServiceImpl implements CountryService{
	@Autowired
	private CountryDao countryDao;

	@Override
	public Country getCountryByCountryId(int countryId) {
		return countryDao.getCountryByCountryId(countryId);
	}

	@Override
	public Country getCountryByCountryName(String countryName) {
		return countryDao.getCountryByCountryName(countryName);
	}
	
	
}
