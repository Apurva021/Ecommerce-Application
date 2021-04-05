package com.apurva.cartservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class CartController {
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private CartRepository cartRepository;
	
	@Autowired
	private JwtUtil jwtUtil;
	
	public boolean canAccess(HttpServletRequest request, Integer userIdInteger) {
		String jwtString = request.getHeader("Authorization").substring(7);
		String emailString = jwtUtil.extractUsername(jwtString);
		Integer idInteger = Integer.parseInt(jwtUtil.getPayload(jwtString));
		
		return idInteger.equals(userIdInteger);
	}
	
	@GetMapping("/{userIdInteger}")
	public List<Product> getProductsByUserId(HttpServletRequest request ,@PathVariable Integer userIdInteger) throws Exception{
		
		if(!canAccess(request, userIdInteger)) {
			throw new Exception("Access denied to sensitive resource");
		}
		
		Cart cart = cartRepository.findByUserIdInteger(userIdInteger);
		List<Product> products = new ArrayList<>();
		if(cart == null) {
			cart = new Cart();
			cart.setUserIdInteger(userIdInteger);
			cart.setProductMap(new HashMap<String, Integer>());
			cartRepository.save(cart);
		}
		else {
			for(String productId: cart.getProductMap().keySet()) {
				products.add(restTemplate.getForObject("http://productcatalog/product" + productId, Product.class));
			}
		}
		return products;
	}
	
	@GetMapping("/{userIdInteger}/{productId}")
	public Product getProductFromCart(HttpServletRequest request ,@PathVariable Integer userIdInteger, @PathVariable String productId) throws Exception {
		
		if(!canAccess(request, userIdInteger)) {
			throw new Exception("Access denied to sensitive resource");
		}
		
		Cart cart = cartRepository.findByUserIdInteger(userIdInteger);
		if(cart == null) {
			cart = new Cart();
			cart.setUserIdInteger(userIdInteger);
			cart.setProductMap(new HashMap<String, Integer>());
			cartRepository.save(cart);
		}
		
		if(cart.getProductMap().containsKey(productId)) {
			Product product = restTemplate.getForObject("http://productcatalog/product" + productId, Product.class);
			return product;
		}
		else {
			throw new Exception("No Such Product Found in cart!");
		}
	}
	
	@PostMapping("/{userIdInteger}/{productId}")
	public String addProductById(HttpServletRequest request ,@PathVariable Integer userIdInteger, @PathVariable String productId) throws Exception {
		
		if(!canAccess(request, userIdInteger)) {
			throw new Exception("Access denied to sensitive resource");
		}
		
		Cart cart = cartRepository.findByUserIdInteger(userIdInteger);
		if(cart == null) {
			cart = new Cart();
			cart.setUserIdInteger(userIdInteger);
			cart.setProductMap(new HashMap<String, Integer>());
			cartRepository.save(cart);
		}
		cart.getProductMap().put(productId, cart.getProductMap().getOrDefault(productId, 0) + 1);
		cartRepository.save(cart);
		
		return "Added product to cart";
	}
	
	@PutMapping("/{userIdInteger}/{productId}")
	public String removeProductById(HttpServletRequest request ,@PathVariable Integer userIdInteger, @PathVariable String productId) throws Exception{
		
		if(!canAccess(request, userIdInteger)) {
			throw new Exception("Access denied to sensitive resource");
		}
		
		Cart cart = cartRepository.findByUserIdInteger(userIdInteger);
		if(cart == null) {
			cart = new Cart();
			cart.setUserIdInteger(userIdInteger);
			cart.setProductMap(new HashMap<String, Integer>());
			cartRepository.save(cart);
		}
		
		if(cart.getProductMap().containsKey(productId)) {
			cart.getProductMap().put(productId, cart.getProductMap().get(productId) - 1);
			if(cart.getProductMap().get(productId) == 0) {
				cart.getProductMap().remove(productId);
			}
			
			cartRepository.save(cart);
		}
		
		return "Product Quantity reduced by 1";
	}
	
	@DeleteMapping("/{userIdInteger}/{productId}")
	public String deleteProductById(HttpServletRequest request ,@PathVariable Integer userIdInteger, @PathVariable String productId) throws Exception {
		
		if(!canAccess(request, userIdInteger)) {
			throw new Exception("Access denied to sensitive resource");
		}
		
		Cart cart = cartRepository.findByUserIdInteger(userIdInteger);
		if(cart == null) {
			cart = new Cart();
			cart.setUserIdInteger(userIdInteger);
			cart.setProductMap(new HashMap<String, Integer>());
			cartRepository.save(cart);
		}
		
		cart.getProductMap().remove(productId);
		cartRepository.save(cart);
		return "Product removed from cart";
	}
	
	
}
