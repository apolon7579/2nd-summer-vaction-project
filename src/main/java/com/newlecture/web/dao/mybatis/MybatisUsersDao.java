package com.newlecture.web.dao.mybatis;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.newlecture.web.dao.UsersDao;
import com.newlecture.web.entity.Users;

@Repository
public class MybatisUsersDao implements UsersDao {

	private UsersDao mapper;
	
	@Autowired
	public MybatisUsersDao(SqlSession sqlSession) {
		mapper = sqlSession.getMapper(UsersDao.class);
	}
	
	
	@Override
	public void createUsers(Users users) {
		mapper.createUsers(users);
	}

	@Override
	public Users retrieveUsersByUserIdandPassword(String userId, String password) {
		return mapper.retrieveUsersByUserIdandPassword(userId, password);
	}


	@Override
	public Users retrieveUserById(String id) {
		// TODO Auto-generated method stub
		return mapper.retrieveUserById(id);
	}

}
