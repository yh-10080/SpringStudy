package com.hqyj.StudySB.config;

import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.hqyj.StudySB.filter.ParameterFilter;
import com.hqyj.StudySB.interceptor.UrlInterceptor;

@Configuration
@AutoConfigureAfter({WebMvcAutoConfiguration.class})
public class WebMvcConfig implements WebMvcConfigurer{
	
	@Value("${server.http.port}")
	private int httpPort;
	
	@Autowired//按照type注入对象
	private UrlInterceptor urlIntercepetor;
	
	@Autowired
	private ResourceConfigBean resourceConfigBean;
	
	@Bean
	public Connector connector() {
		Connector connector = new Connector();
		connector.setScheme("http");
		connector.setPort(httpPort);
		return connector;
	} 
	
	@Bean
	public ServletWebServerFactory servletWebServerFactory() {
		TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
		
		factory.addAdditionalTomcatConnectors(connector());
		return factory;
	}
	
	/**
	 *将我们自定义的过滤器注册到spring中来，交付给spring管理(spring内嵌服务器)
	 */
	@Bean//生成bean放入到spring容器
	public FilterRegistrationBean<ParameterFilter> filter() {
		FilterRegistrationBean<ParameterFilter> register = new FilterRegistrationBean<ParameterFilter>();
		register.setFilter(new ParameterFilter());
		return register;
	}
	
	//注册拦截器
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(urlIntercepetor).addPathPatterns("/**");
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		String systemName = System.getProperty("os.name");
		if (systemName.toLowerCase().startsWith("win")) {
			registry.addResourceHandler(resourceConfigBean.getResourcePathPattern())
				.addResourceLocations(ResourceUtils.FILE_URL_PREFIX + resourceConfigBean.getLocalPathForWindow());
		} else  {
			registry.addResourceHandler(resourceConfigBean.getResourcePathPattern())
				.addResourceLocations(ResourceUtils.FILE_URL_PREFIX + resourceConfigBean.getLocalPathForLinux());
		}
	}

	
	
}
