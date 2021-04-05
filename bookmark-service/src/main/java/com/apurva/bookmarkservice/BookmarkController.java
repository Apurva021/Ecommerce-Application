package com.apurva.bookmarkservice;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class BookmarkController {
	
	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private BookmarkRepository bookmarkRepository;
	
	public boolean canAccess(HttpServletRequest request, Integer userIdInteger) {
		String jwtString = request.getHeader("Authorization").substring(7);
		String emailString = jwtUtil.extractUsername(jwtString);
		Integer idInteger = Integer.parseInt(jwtUtil.getPayload(jwtString));
		
		return idInteger.equals(userIdInteger);
	}
	
	@GetMapping("/{userIdInteger}")
	public List<Product> getProductsByUserId(HttpServletRequest request, @PathVariable Integer userIdInteger) throws Exception{
		if(!canAccess(request, userIdInteger)) {
			throw new Exception("Access Denied to sensitive resource");
		}
		List<Product> products = new ArrayList<>();
		Bookmark bookmark = bookmarkRepository.findByUserIdInteger(userIdInteger);
		if(bookmark == null) {
			bookmark = new Bookmark();
			bookmark.setUserIdInteger(userIdInteger);
			bookmark.setProductIdSet(new HashSet<String>());
			bookmarkRepository.save(bookmark);
		}
		else {
			for(String productId: bookmark.getProductIdSet()) {
				products.add(restTemplate.getForObject("http://productcatalog/product" + productId, Product.class));
			}
		}
		return products;
	}
	
	/**
	 * this method can be used to check if a particular product is bookmarked by the user or not
	 * @param request
	 * @param userIdInteger
	 * @param productId
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/{userIdInteger}/{productId}")
	public boolean getProductById(HttpServletRequest request, @PathVariable Integer userIdInteger, @PathVariable String productId) throws Exception {
		if(!canAccess(request, userIdInteger)) {
			throw new Exception("Access Denied to sensitive resource");
		}
		Bookmark bookmark = bookmarkRepository.findByUserIdInteger(userIdInteger);
		if(bookmark == null) {
			bookmark = new Bookmark();
			bookmark.setUserIdInteger(userIdInteger);
			bookmark.setProductIdSet(new HashSet<String>());
			bookmarkRepository.save(bookmark);
			return false;
		}
		else {
			return bookmark.getProductIdSet().contains(productId);
		}
		
	}
	
	@PostMapping("/{userIdInteger}/{productId}")
	public String addProductById(HttpServletRequest request, @PathVariable Integer userIdInteger, @PathVariable String productId) throws Exception{
		if(!canAccess(request, userIdInteger)) {
			throw new Exception("Access Denied to sensitive resource");
		}
		Bookmark bookmark = bookmarkRepository.findByUserIdInteger(userIdInteger);
		if(bookmark == null) {
			bookmark = new Bookmark();
			bookmark.setUserIdInteger(userIdInteger);
			bookmark.setProductIdSet(new HashSet<String>());
		}
		
		bookmark.getProductIdSet().add(productId);
		bookmarkRepository.save(bookmark);
		
		return "Product added to bookmarks!";
	}
	
	@DeleteMapping("/{userIdInteger}/{productId}")
	public String deleteProductById(HttpServletRequest request, @PathVariable Integer userIdInteger, @PathVariable String productId) throws Exception{
		if(!canAccess(request, userIdInteger)) {
			throw new Exception("Access Denied to sensitive resource");
		}
		Bookmark bookmark = bookmarkRepository.findByUserIdInteger(userIdInteger);
		if(bookmark == null) {
			bookmark = new Bookmark();
			bookmark.setUserIdInteger(userIdInteger);
			bookmark.setProductIdSet(new HashSet<String>());
			bookmarkRepository.save(bookmark);
			return "Product not found bookmarks!";
		}
		else {
			bookmark.getProductIdSet().remove(productId);
		}
		
		return "product removed from bookmarks!";
	}
}
