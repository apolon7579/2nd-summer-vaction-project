package com.newlecture.web.controller.customer;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.newlecture.web.dto.CartItem;
import com.newlecture.web.dto.OrderedProductDto;
import com.newlecture.web.dto.ProductItem;
import com.newlecture.web.entity.Cart;
import com.newlecture.web.entity.Category;
import com.newlecture.web.entity.OrderedProduct;
import com.newlecture.web.entity.Orders;
import com.newlecture.web.entity.Users;
import com.newlecture.web.service.CartService;
import com.newlecture.web.service.CategoryService;
import com.newlecture.web.service.OrderedProductService;
import com.newlecture.web.service.OrdersService;
import com.newlecture.web.service.ProductService;
import com.newlecture.web.service.UsersService;

@Controller
@RequestMapping("/")
public class HomeController {
	
	// '/' -> WEB-INF JSP ViewResolver 2순위
	// '.' -> TilesViewResolver 1순위
	
	@Autowired
	private UsersService usersService;
	@Autowired
	private CategoryService categoryService;
	@Autowired
	private ProductService productService;
	@Autowired
	private CartService cartService;
	@Autowired
	private OrdersService ordersService;
	@Autowired
	private OrderedProductService orderedProductService;
	
	// 메인 페이지 이동
	@RequestMapping("")
	public String index(Model model) {
		
		List<Category> categoryList = categoryService.getCategoryList();
		model.addAttribute("categoryList", categoryList);
		
		List<ProductItem> productItemList = productService.retrieveRecentlyProductItemAll();
		model.addAttribute("productItemList", productItemList);
		
		List<ProductItem> productItemRecentlyList = productService.retrieveProductItemListRecentlyOneInCategory();
		model.addAttribute("productItemRecentlyList", productItemRecentlyList);
		
		HashMap<Long, String> categoryMap = categoryService.retrieveCategoryMapAll();
		model.addAttribute("categoryMap", categoryMap);
		
		return "customer.index";
	}
	
	// 상품 목록 페이지 이동
	@RequestMapping("/list")
	public String list(HttpServletRequest request, Model model) {
		
		String categoryId = request.getParameter("categoryId");
		HashMap<Long, String> categoryMap = categoryService.retrieveCategoryMapAll();
		model.addAttribute("categoryMap", categoryMap);
		List<Category> categoryList = categoryService.getCategoryList();
		model.addAttribute("categoryList", categoryList);
		if(categoryId != null) {
			Category category = categoryService.retrieveCategoryById(categoryId);
			List<ProductItem> productItemList = productService.retrieveProductItemListByCategoryId(categoryId);
			
			model.addAttribute("productItemList", productItemList);
			model.addAttribute("category", category);
			return "customer.list";
		}
		else {
			List<ProductItem> productItemList = productService.retrieveProductItemBySearch(request.getParameter("productName"));
			model.addAttribute("productItemList", productItemList);
			
			return "customer.list";
		}
		
	}
	
	// 상품 조회 페이지 이동
	@RequestMapping("/detail")
	public String detail(HttpServletRequest request, Model model) {
		
		String productId = request.getParameter("productId");
		ProductItem productItem = productService.retrieveProductItemById(productId);
		Category cateogory = categoryService.retrieveCategoryById(Long.toString(productItem.getCategoryId()));
		
		model.addAttribute("productItem", productItem);
		model.addAttribute("category", cateogory);
		
		return "customer.detail";
	}
	
	// 장바구니 조회 페이지 이동
	@RequestMapping("/cart")
	public String cart(HttpServletRequest request, HttpSession session, Model model) {
		
		// 세션이 존재하지 않는다면 index 페이지로 리다이렉트 처리 (피드백 해줘야할 필요성 있음)
		if(session.getAttribute("userRole") == null) {
			return "redirect:/";
		}
		
		Long userId = (Long) session.getAttribute("userId");

		 // CartItem을 따로 처리하는 Service Interface나 Mapper의 필요성이 있으나 따로 만들기에는 규모가 너무 작으므로 일단 productService 및 Mapper등에서 처리하는걸로함
		List<CartItem> cartItemList = productService.retrieveCartItemByUserId(userId);
		int sum = productService.getCartItemSum(userId);
				
		model.addAttribute("cartItemList", cartItemList);
		model.addAttribute("sum", sum);
		
		return "customer.cart";
	}
	
	// 카트에 아이템 추가
	@RequestMapping("/addCart")
	public String addCart(HttpServletRequest request, HttpSession session, Model model) {
		
		// 세션이 존재하지 않는다면 index 페이지로 리다이렉트 처리 (피드백 해줘야할 필요성 있음)
		if(session.getAttribute("userRole") == null) {
			return "redirect:/";
		}
		
		String productId = request.getParameter("productId");
		String count = request.getParameter("count");
		Long userId = (Long) session.getAttribute("userId");
		
		cartService.registerCartItem(new Cart(0, Long.parseLong(productId), userId, Long.parseLong(count)));
						
		return "redirect:/cart";
	}
	
	// 카트에 담겨있는 아이템 삭제
	@RequestMapping("/deleteCart")
	public String deleteCart(HttpServletRequest request, Model model) {
		
		String cartId = request.getParameter("cartId");
		
		cartService.deleteCartByCartId(cartId);
		
		return "redirect:/cart";
	}
	
	
	// 결제 페이지 이동
	@RequestMapping("/checkout")
	public String checkOut(HttpServletRequest request, Model model, HttpSession session) {
		List<CartItem> cartItem = productService.retrieveCartItemByUserId((Long)session.getAttribute("userId"));
		long priceSum = cartItem.stream().mapToLong(p->Long.parseLong(p.getPrice())*Long.parseLong(p.getCount())).sum();
		model.addAttribute("cartItem", cartItem);
		model.addAttribute("priceSum", priceSum);
		
		return "customer.checkout";
	}
	
	// 결제
	@RequestMapping("/customer/checkout")
	public String orderCheckOut(HttpServletRequest request, Model model, HttpSession session) {
		
		List<Cart> cartList = cartService.retrieveCartListByUserId((Long)session.getAttribute("userId"));
		ordersService.createOrders(new Orders(0, Date.from(Instant.now())
				, request.getParameter("firstName")+request.getParameter("lastName")
				, request.getParameter("phone"), request.getParameter("streetAddress")+request.getParameter("etcAddress")
				, (Long)session.getAttribute("userId"), (Long)session.getAttribute("userRole")));
		long orderId = ordersService.retrieveLastIdByUserId((Long)session.getAttribute("userId"));
		
		for(Cart item : cartList) {
			
			orderedProductService.createOrderedProduct(new OrderedProduct(0
					, orderId
					, item.getProductId(), item.getCount()));
			
			productService.updateProductByStock(new OrderedProduct(0, orderId, item.getProductId(), item.getCount()));
		}
		
		cartService.deleteCartAllByUserId((Long)session.getAttribute("userId"));
		
		return "redirect:/";
	}
	
	// contact 페이지 이동
	@RequestMapping("/contact")
	public String contact() {
		
		return "customer.contact";
	}
	
	// 주문목록 페이지 이동
	@RequestMapping("/orderlist")
	public String orderList(HttpServletRequest request, HttpSession session, Model model) {
		
		// 세션이 존재하지 않는다면 index 페이지로 리다이렉트 처리 (피드백 해줘야할 필요성 있음)
		if(session.getAttribute("userRole") == null) {
			return "redirect:/";
		}
		
		Long userId = (Long) session.getAttribute("userId");
		List<Orders> orderList = ordersService.getOrderListByUserId(userId);
		List<OrderedProductDto> orderedProductList = orderedProductService.getOrderedProductDtoListByOrderList(orderList);
		
		model.addAttribute("orderList", orderedProductList);
		
		return "customer.orderlist";
	}
	
	// 로그인 페이지 이동
	@RequestMapping("/login")
	public String login() {
		
		return "customer/login";
	}
	
	// 로그인 처리
	@RequestMapping("/customer/loginCheck")
	public String loginCheck(HttpServletRequest request, HttpSession httpSession, Model model, RedirectAttributes redirectAttributes) {
		
		String id = request.getParameter("id");
		String password = request.getParameter("password");
		boolean check = usersService.loginCheck(id, password, httpSession);
		
		if(check) {
			// 로그인 성공시 index 페이지로 이동
			return "redirect:/";
		}else {
			// 로그인 실패시 login 페이지로 이동
			redirectAttributes.addAttribute("loginCheck", false);
			return "redirect:/login";
		}
	}
	
	// 로그아웃 처리
	@RequestMapping("/customer/logout")
	public String logout(HttpSession session) {
		
		usersService.logout(session);
		
		return "redirect:/";
	}
	
	@ResponseBody
	@RequestMapping("/duplicateCheck")
	public String ajaxIdCheck(String id) {
		
		boolean check = usersService.idCheck(id);
		
		if(check) {
			return "true";
		}else {
			return "false";
		}
	}
	
	// 회원가입 페이지 이동
	@RequestMapping("/signup")
	public String signUp() {
		
		return "customer/signup";
	}
	
	// 회원가입 처리후 index 페이지 이동
	@RequestMapping("/customer/signup")
	public String signUpDo(HttpServletRequest request) throws ServletException, IOException{
		
		String name = request.getParameter("name");
		String addr = request.getParameter("addr");
		String phone = request.getParameter("phone");
		String prevNumber = request.getParameter("prevNumber");
		String email = request.getParameter("email");
		String rePassword = request.getParameter("rePassword");
		
		prevNumber += phone;
		phone = prevNumber;
		
		usersService.signUp(new Users(0, email, rePassword, name, phone, 1, Date.from(Instant.now()), addr));
		
		
		return "redirect:/"; // customer/signup.do url이 유지되면 안되므로 redirect 처리함
	}
}
