package com.hqyj.StudySB.modules.account.dao;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserRoleDao {

	@Delete("delete from user_role where user_id = #{userId}")
	void deletUserRoleByUserId(int userId);
	
	@Insert("insert user_role(role_id, user_id) value(#{roleId}, #{userId})")
	void addUserRole(@Param("roleId") int roleId, @Param("userId") int userId);
}
