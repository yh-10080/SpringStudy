package com.hqyj.StudySB.modules.test.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hqyj.StudySB.config.ResourceConfigBean;
import com.hqyj.StudySB.modules.test.entity.City;
import com.hqyj.StudySB.modules.test.entity.Country;
import com.hqyj.StudySB.modules.test.service.CityService;
import com.hqyj.StudySB.modules.test.service.CountryService;
import com.hqyj.StudySB.modules.test.vo.ApplicationTest;
@Controller
@RequestMapping("/test")
public class TestController {
	private final static Logger LOGGER = LoggerFactory.getLogger(TestController.class);
	
	@Autowired
	private ApplicationTest applicationTest;
	
	@Autowired
	private CityService cityService;
	
	@Autowired
	private CountryService countryService;
	
	@Autowired
	private ResourceConfigBean resourceConfigBean;
	/**
	 * 127.0.0.1/test/download
	 * 下载文件 
	 */
	@RequestMapping("/download")
	@ResponseBody
	public ResponseEntity<Resource> download(@RequestParam String fileName) {
		try {
			String resourcePath = resourceConfigBean.getResourcePath() + fileName;
//			Resource resource = new UrlResource(Paths.get("D:\\upload\\" + fileName).toUri());
			Resource resource = new UrlResource(ResourceUtils.getURL(resourcePath));
			
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
					.header(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=%s", fileName))
					.body(resource);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * 将文件以BufferedInputStream的方式读取到byte[]里面，然后用OutputStream.write输出文件
	 */
	@RequestMapping("/download1")
	public void downloadFile1(HttpServletRequest request, 
			HttpServletResponse response, @RequestParam String fileName) {
		String filePath = "D:/upload" + File.separator + fileName;
		File downloadFile = new File(filePath);
		
		if (downloadFile.exists()) {
			response.setContentType("application/octet-stream");
			response.setContentLength((int)downloadFile.length());
			response.setHeader(HttpHeaders.CONTENT_DISPOSITION, 
					String.format("attachment; filename=\"%s\"", fileName));
			
			byte[] buffer = new byte[1024];
			FileInputStream fis = null;
			BufferedInputStream bis = null;
			try {
				fis = new FileInputStream(downloadFile);
				bis = new BufferedInputStream(fis);
				OutputStream os = response.getOutputStream();
				int i = bis.read(buffer);
				while (i != -1) {
					os.write(buffer, 0, i);
					i = bis.read(buffer);
				}
			} catch (Exception e) {
				LOGGER.debug(e.getMessage());
				e.printStackTrace();
			} finally {
				try {
					if (fis != null) {
						fis.close();
					}
					if (bis != null) {
						bis.close();
					}
				} catch (Exception e2) {
					LOGGER.debug(e2.getMessage());
					e2.printStackTrace();
				}
			}
		}
	}

	/**
	 * 以包装类 IOUtils 输出文件
	 */
	@RequestMapping("/download2")
	public void downloadFile2(HttpServletRequest request, 
			HttpServletResponse response, @RequestParam String fileName) {
		String filePath = "D:/upload" + File.separator + fileName;
		File downloadFile = new File(filePath);
		
		try {
			if (downloadFile.exists()) {
				response.setContentType("application/octet-stream");
				response.setContentLength((int)downloadFile.length());
				response.setHeader(HttpHeaders.CONTENT_DISPOSITION, 
						String.format("attachment; filename=\"%s\"", fileName));
				
				InputStream is = new FileInputStream(downloadFile);
				IOUtils.copy(is, response.getOutputStream());
				response.flushBuffer();
			}
		} catch (Exception e) {
			LOGGER.debug(e.getMessage());
			e.printStackTrace();
		}
	}
	
	@Value("${com.thornbird.name}")
	private String name;
	@Value("${com.thornbird.age}")
	private int age;
	@Value("${com.thornbird.desc}")
	private String desc;
	@Value("${com.thornbird.random}")
	private String random;
	@Value("${com.thorBbird.aaa}")
	private String aaa;
	
	
	
	/**
	 * 
	 * 127.0.0.1/test/log
	 */
	@RequestMapping("/log")
	@ResponseBody
	public String logTest() {
		LOGGER.trace("This is trace log");
		LOGGER.debug("This is debug log");
		LOGGER.info("This is info log");
		LOGGER.warn("This is warn log");
		LOGGER.error("This is error log");
		return "This is log test";
	}
	
	/**
	 * 
	 * 127.0.0.1/test/config
	 */
	@RequestMapping("/config")
	@ResponseBody
	public String configInfo() {
		StringBuffer sb = new StringBuffer();
		sb.append(name).append("-----")
			.append(age).append("---")
			.append(desc).append("---")
			.append(random).append("---")
			.append(aaa).append("---");
		return sb.toString();
	}
	
	/**
	 * 127.0.0.1/test/index
	 */
	@RequestMapping("index")
	public String indePage(ModelMap modelMap) {
		
		int countryId = 522;
		List<City> cities = cityService.getCitiesByCountryId(countryId);
		
		Country country = countryService.getCountryByCountryId(countryId);
		//使用ModelMap modelMap属性，来包装碎片
		modelMap.addAttribute("checked", true);
		modelMap.addAttribute("currentNumber", 99);
		modelMap.addAttribute("changeType", "checkbox");
		modelMap.addAttribute("baiduUrl", "/test/log");
		modelMap.addAttribute("city", cities.get(0));
		modelMap.addAttribute("shopLogo",
								"http://cdn.duitang.com/uploads/item/201308/13/20130813115619_EJCWm.thumb.700_0.jpeg");
		modelMap.addAttribute("country", country);
		modelMap.addAttribute("cities", cities);
		modelMap.addAttribute("updateCityUri", "/api/city");
		//modelMap.addAttribute("template", "test/index");
		return "index";
	}
	
	/**
	 * 
	 * 127.0.0.1/test/desc?key=fuck
	 */
	@RequestMapping("/desc")
	@ResponseBody
	public String testDesc(HttpServletRequest requset,@RequestParam String key) {
		String key2 = requset.getParameter("key");
		return "This is test module desc.112233"+key+"==="+key2;
	}
	
	/**
	 * 127.0.0.1/test/file
	 * 使用MultipartFile来处理前端传来的文件类型
	 */
	@PostMapping(value="/file",consumes="multipart/form-data")
	public String uploadFile(@RequestParam MultipartFile file,RedirectAttributes redirectAttributes) {
	//D:upload windows支持的路径是"\"，但是Java支持的是"/"所以这里的路径两个//第一个是转义符转变/为\
		if (file.isEmpty()) {
			redirectAttributes.addFlashAttribute("message", "please select file");
			return "redirect:/test/index";
		}
		String resourcePath = resourceConfigBean.getResourcePath()+file.getOriginalFilename();
		 try {
			 String destFilePath = ResourceUtils.getURL(resourcePath).getPath();
			 File destFile = new File(destFilePath);
			file.transferTo(destFile);//将读取的文件放入在目的地址
		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("message", "failed success");
			return "redirect:/test/index";
		}
		
		 redirectAttributes.addFlashAttribute("message", "upload success");
		return "redirect:/test/index";
	}
	
	@PostMapping(value="/files",consumes="multipart/form-data")
	public String uploadFiles(@RequestParam MultipartFile[] files,RedirectAttributes redirectAttributes) {
		boolean isEmpty= true;
		for (MultipartFile file : files) {
			if(file.isEmpty()) {
				continue;
			}
			 try {
				 	String resourcePath = resourceConfigBean.getResourcePath()+file.getOriginalFilename();
					File destFile = new File(ResourceUtils.getURL(resourcePath).getPath());
					file.transferTo(destFile);//将读取的文件放入在目的地址
					
					isEmpty = false;
			 } catch (IllegalStateException | IOException e) {
					e.printStackTrace();
					redirectAttributes.addFlashAttribute("message", "failed success");
					return "redirect:/test/index";
				}
				
		}
		if(isEmpty) {
			redirectAttributes.addFlashAttribute("message", "please select file");
			
		} else {
			redirectAttributes.addFlashAttribute("message", "upload success");
		}
		return "redirect:/test/index";
	}
}
