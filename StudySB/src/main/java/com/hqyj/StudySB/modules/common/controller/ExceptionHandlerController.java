package com.hqyj.StudySB.modules.common.controller;

import org.apache.shiro.authz.AuthorizationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hqyj.StudySB.modules.common.vo.Result;
import com.hqyj.StudySB.modules.common.vo.Result.ResultStatus;

@ControllerAdvice
public class ExceptionHandlerController {
	//ExceptionHandlerController 局部的异常控制器
	//ExceptionHandler
	@ExceptionHandler(value=AuthorizationException.class)
	@ResponseBody
	public Result<Object> exceptionHandlerFor403() {
		return new Result<Object>(ResultStatus.FAILD.status,"You have no permission","/common/403");
	}
}
