package com.newlecture.web.dao.mybatis;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.newlecture.web.dao.CategoryDao;
import com.newlecture.web.entity.Category;

@Repository
public class MybatisCategoryDao implements CategoryDao{

	private CategoryDao mapper;
	
	@Autowired
	public MybatisCategoryDao(SqlSession sqlSession) {
		mapper = sqlSession.getMapper(CategoryDao.class);
	}
	
	@Override
	public void createCategory(Category category) {
		mapper.createCategory(category);
	}

	@Override
	public List<Category> retrieveCategoryAll() {
		return mapper.retrieveCategoryAll();
	}

	@Override
	public long retrieveIdByName(String name) {
		return mapper.retrieveIdByName(name);
	}

	@Override
	public Category retrieveCategoryById(String categoryId) {
		return mapper.retrieveCategoryById(categoryId);
	}

	@Override
	public HashMap<Long, String> retrieveCategoryMapAll() {
		List<Category> categoryList = mapper.retrieveCategoryAll();
		HashMap<Long, String> categoryMap = new HashMap<>();
		for(Category category: categoryList) {
			categoryMap.put(category.getId(), category.getName());
		}
		return categoryMap;
	}

}
