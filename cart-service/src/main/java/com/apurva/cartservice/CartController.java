package com.apurva.cartservice;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonObjectDeserializer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
				Product[] tempProducts =restTemplate.getForObject("http://productcatalog/product?id=" + productId, Product[].class);
				tempProducts[0].setQuantityBought(cart.getProductMap().get(productId));
				products.add(tempProducts[0]);
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
			Product[] products = restTemplate.getForObject("http://productcatalog/product?id=" + productId, Product[].class);
			products[0].setQuantityBought(cart.getProductMap().get(productId));
			return products[0];
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
	
	/**
	 * This method requires a Request parameter ?addressId=<integer value>
	 * this address id has to be available in the address table
	 * and that address has to belong to the user
	 * we can make him pick the address using radio button by showing his available addresses
	 */
	
	
	@GetMapping("/checkout/{userIdInteger}")
	public String checkooutCart(@RequestParam Integer addressId ,HttpServletRequest request, @PathVariable Integer userIdInteger) throws Exception {
		if(!canAccess(request, userIdInteger)) {
			throw new Exception("Acess Denied to sensitive resource!");
		}
		
		// declaring data for the request body
		String receiptIdString = RandomStringUtils.random(10, true, true);
		Double totalBillDouble = 0D;
		Double billAmountDouble;
		Integer addressIdInteger = addressId;
		Integer quantityBoughtInteger;
		Integer sellerIdInteger;
		Integer productIdInteger;
		String orderStatus;
		
		Date dateOfPurchaseDate;
		Date dateOfDeliveryDate;
		
		String sellerId;
		
		List<Product> productsPurchased = getProductsByUserId(request, userIdInteger);
		
		for(Product product : productsPurchased) {
			billAmountDouble = product.getPrice();
			quantityBoughtInteger = product.getQuantityBought();
			sellerId = product.getSellerId();
			sellerIdInteger = null;
			productIdInteger = Integer.parseInt(product.getProductId());
			dateOfPurchaseDate = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
			dateOfDeliveryDate = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
			
			totalBillDouble += (billAmountDouble * quantityBoughtInteger);
			orderStatus = "PENDING";
			Map<String, Object> requestBodyMap = new HashMap<>();
			
			requestBodyMap.put("userIdInteger", userIdInteger);
			requestBodyMap.put("sellerIdInteger", sellerIdInteger);
			requestBodyMap.put("productIdInteger", productIdInteger);
			requestBodyMap.put("addressIdInteger", addressIdInteger);
			requestBodyMap.put("quantityBoughtInteger", quantityBoughtInteger);
			requestBodyMap.put("dateOfPurchaseDate", dateOfPurchaseDate);
			requestBodyMap.put("dateOfDeliverDate", dateOfDeliveryDate);
			requestBodyMap.put("billAmountDouble", billAmountDouble);
			requestBodyMap.put("orderStatusString", orderStatus);
			requestBodyMap.put("receiptIdString", receiptIdString);
			requestBodyMap.put("sellerId", sellerId);
			
			HttpHeaders headers = new HttpHeaders();
			headers.set("Authorization", request.getHeader("Authorization"));
			
			HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBodyMap, headers);
			
			ResponseEntity<String> responseEntity = restTemplate.postForEntity("http://order-service/create-order", entity, String.class);
			
			if(responseEntity == null) {
				throw new Exception("Failed order creation");
			}
			
			
			deleteProductById(request, userIdInteger, product.getProductId());
			
		}
		
		return "receiptId: " + receiptIdString + " totalBill:" + totalBillDouble;
		
	} 
	
	
	
}
