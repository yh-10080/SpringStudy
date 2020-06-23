package com.hqyj.StudySB.modules.account.service;

import org.springframework.web.multipart.MultipartFile;

import com.github.pagehelper.PageInfo;
import com.hqyj.StudySB.modules.account.entity.User;
import com.hqyj.StudySB.modules.common.vo.Result;
import com.hqyj.StudySB.modules.common.vo.SearchVo;

public interface UserService {
	User getUserByUserName(String userName);

	Result<User> login(User user);
	
	void logout();

	PageInfo<User> getUsersBySearchVo(SearchVo searchVo);

	User getUserByUserId(int userId);

	Result<Object> deleteUser(int userId);

	Result<User> editUser(User user);

	Result<String> uploadUserImage(MultipartFile userImage);

	Result<User> updateUserProfile(User user);
}
