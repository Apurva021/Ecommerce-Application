package com.apurva.recommenderservice;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


@RestController
@RequestMapping
public class RecommenderController {
	@Autowired
	private RestTemplate restTemplate;
	
	@GetMapping("recommend/{userIdInteger}")
	public List<Product> recommendById(HttpServletRequest request, @PathVariable Integer userIdInteger) {
		List<Product> products = new ArrayList<>();
		
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", request.getHeader("Authorization"));
		
		HttpEntity entity = new HttpEntity(headers);
		ResponseEntity<Order[]> responseEntity = restTemplate.exchange("http://order-service/user/" + userIdInteger, HttpMethod.GET, entity ,Order[].class);
		Order[] orders = responseEntity.getBody();
		
		
		/*Product[] tempProducts = restTemplate.getForObject("http://similarproducts/similar?id=" + userIdInteger, Product[].class);
		return tempProducts;*/
		
		
		for(int i=orders.length-1;i>=orders.length-3 && i>=0;--i) {
			String productId = orders[i].getProductIdInteger().toString();
			System.out.println("productId:" + productId);
			/*
			Product[] products2 = restTemplate.getForObject("http://similarproducts/similar?id=" + productId, Product[].class);
			for(Product product: products2) {
				products.add(product);
			}
			*/
			
			List<Product> products2 = getProductsById(request, productId);
			products.addAll(products2);
		}
		
		return products;
	}
	
	@GetMapping("/orders/{userIdInteger}")
	public List<Order> getOrdersById(HttpServletRequest request, @PathVariable Integer userIdInteger) {
		List<Order> orders = new ArrayList<>();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", request.getHeader("Authorization"));
		
		HttpEntity entity = new HttpEntity(headers);
		ResponseEntity<Order[]> responseEntity = restTemplate.exchange("http://order-service/user/" + userIdInteger, HttpMethod.GET, entity ,Order[].class);
		Order[] ordersArray = responseEntity.getBody();
		
		for(Order order: ordersArray) {
			orders.add(order);
		}
		
		return orders;
	}
	
	@GetMapping("/simproducts/{productId}")
	public List<Product> getProductsById(HttpServletRequest request, @PathVariable String productId) {
		List<Product> products = new ArrayList<>();
		Product[] products2 = restTemplate.getForObject("http://similarproducts/similar?id=" + productId, Product[].class);
		for(Product product:products2) {
			products.add(product);
		}
		return products;
	}
	
}
