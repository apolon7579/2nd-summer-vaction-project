package com.newlecture.web.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.newlecture.web.dao.ProductDao;
import com.newlecture.web.dto.CartItem;
import com.newlecture.web.dto.ProductItem;
import com.newlecture.web.entity.OrderedProduct;
import com.newlecture.web.entity.Product;

@Service
public class ProductServiceImpl implements ProductService{

	@Autowired
	private ProductDao productDao;
	
	@Override
	public long registerProduct(Product product) {
		productDao.createProduct(product);
		return 0;
	}

	@Override
	public long retrieveIdByName(String name) {
		return productDao.retrieveIdByName(name);
	}

	@Override
	public long retrieveCurId() {
		return productDao.retrieveCurId();
	}

	@Override
	public List<ProductItem> retrieveProductItemListByCategoryId(String categoryId) {
		// TODO Auto-generated method stub
		return productDao.retrieveProductItemListByCategoryId(categoryId);
	}

	@Override
	public List<ProductItem> retrieveProductItemAll() {
		return productDao.retrieveProductItemAll();
	}

	@Override
	public List<ProductItem> retrieveProductItemListRecentlyOneInCategory() {
		return productDao.retrieveProductItemListRecentlyOneInCategory();
	}

	@Override
	public Product retrieveProductById(String productId) {
		return productDao.retrieveProductById(Integer.parseInt(productId));
	}

	@Override
	public ProductItem retrieveProductItemById(String productId) {
		return productDao.retrieveProductItemById(productId);
	}

	@Override
	public List<ProductItem> retrieveRecentlyProductItemAll() {
		// TODO Auto-generated method stub
		return productDao.retrieveRecentlyProductItemAll();
	}

	@Override
	public List<CartItem> retrieveCartItemByUserId(Long userId) {
		// TODO Auto-generated method stub
		return productDao.retrieveCartItemByUserId(userId);
	}

	@Override
	public List<ProductItem> retrieveProductItemBySearch(String search) {
		return productDao.retrieveProductItemBySearch(search);
	}

	@Override
	public int getCartItemSum(Long userId) {
		
		List<CartItem> productItemList = productDao.retrieveCartItemByUserId(userId);
		
		int sum = 0;
		
		for(int i = 0; i < productItemList.size(); i++) {
			int price =  Integer.parseInt(productItemList.get(i).getPrice());
			int count = Integer.parseInt(productItemList.get(i).getCount());
			
			sum += price * count;
		}
		
		return sum;
	}

	@Override
	public long updateProductByStock(OrderedProduct orderProduct) {
		productDao.updateProductByStock(orderProduct);
		return 0;
	}

}
