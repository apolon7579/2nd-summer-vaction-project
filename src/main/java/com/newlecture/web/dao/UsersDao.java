package com.newlecture.web.dao;

import org.apache.ibatis.annotations.Param;

import com.newlecture.web.entity.Users;

public interface UsersDao {

	public void createUsers(Users users);
	// interface ������ �������� ���� annotaion�̳� mybatis���� ��ü�� �ƴ� ������ �ٷ� �������� ���ϹǷ� ���
	public Users retrieveUsersByUserIdandPassword(@Param("userId") String userId, @Param("password") String password);
	public Users retrieveUserById(String id);
}
