package com.apurva.orderservice;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {
	@Autowired
	private OrderRepository orderRepository;
	
	@GetMapping("/order/{orderIdInteger}")
	public Optional<Order> getOrderById(@PathVariable Integer orderIdInteger) {
		return orderRepository.findById(orderIdInteger);
	}
	
	@PutMapping("/update/{orderIdInteger}")
	public String updateOrderById(@RequestBody Order order) {
		orderRepository.save(order);
		return "Order updated successfully!";
	}
	
	@GetMapping("/user/{userIdInteger}")
	public List<Order> getOrdersByUserId(@PathVariable Integer userIdInteger) {
		return orderRepository.findByUserIdInteger(userIdInteger);
	}
	
	@GetMapping("/seller/{sellerIdInteger}")
	public List<Order> getOrdersBySellerId(@PathVariable Integer sellerIdInteger) {
		return orderRepository.findBySellerIdInteger(sellerIdInteger);
	}
	
	@PostMapping("/")
	public String createOrder() {
		return "Order Created!";
	}
}
