package com.hqyj.StudySB.modules.test.service;

import com.hqyj.StudySB.modules.test.entity.Country;

public interface CountryService {
	Country getCountryByCountryId(int countryId);
	
	Country getCountryByCountryName(String countryName);
}
