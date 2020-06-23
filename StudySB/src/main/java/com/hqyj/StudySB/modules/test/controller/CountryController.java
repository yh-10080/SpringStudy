package com.hqyj.StudySB.modules.test.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hqyj.StudySB.modules.test.entity.Country;
import com.hqyj.StudySB.modules.test.service.CountryService;

@RestController
@RequestMapping("/api")
public class CountryController {
	@Autowired
	private CountryService countryService;
	
	/**
	 * 
	 * 127.0.0.1/api/country/522
	 */
	@RequestMapping("/country/{countryId}")
	public Country getCountryByCountryId(@PathVariable int countryId) {
		//注意获取地址栏处的参数值，一定要加上注解@PathVariable
		return countryService.getCountryByCountryId(countryId);
		
	}
	/**
	 * 
	 * 127.0.0.1/api/country?countryName=China
	 */
	@RequestMapping("/country")
	public Country getCountryByCountryId(@RequestParam String countryName) {
		return countryService.getCountryByCountryName(countryName);
		
	}
}
