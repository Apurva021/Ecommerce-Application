package com.apurva.orderservice;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

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
	
	@Autowired 
	private JwtUtil jwtUtil;
	
	public boolean canAccess(HttpServletRequest request, Integer userIdInteger) {
		String jwtString = request.getHeader("Authorization").substring(7);
		String emailString = jwtUtil.extractUsername(jwtString);
		Integer idInteger = Integer.parseInt(jwtUtil.getPayload(jwtString));
		
		return idInteger.equals(userIdInteger);
	}
	
	@GetMapping("/order/{orderIdInteger}")
	public Order getOrderById(HttpServletRequest request, @PathVariable Integer orderIdInteger) throws Exception{
		Order order = orderRepository.findByOrderIdInteger(orderIdInteger);
		if(order == null) {
			throw new Exception("No such order id!");
		}
		else {
			String jwtString = request.getHeader("Authorization").substring(7);
			Integer userIdInteger = Integer.parseInt(jwtUtil.getPayload(jwtString));
			if(order.getUserIdInteger() != userIdInteger) {
				throw new Exception("Access denied to sensitive data!");
			}
			else {
				return order;
			}
		}
	}
	
	@PutMapping("/update/{orderIdInteger}")
	public String updateOrderById(HttpServletRequest request,@RequestBody Order order, @PathVariable Integer userIdInteger) throws Exception{
		if(!canAccess(request, userIdInteger)) {
			throw new Exception("Access denied to sensitive resource!");
		}
		orderRepository.save(order);
		return "Order updated successfully!";
	}
	
	@GetMapping("/user/{userIdInteger}")
	public List<Order> getOrdersByUserId(HttpServletRequest request,@PathVariable Integer userIdInteger) throws Exception{
		if(!canAccess(request, userIdInteger)) {
			throw new Exception("Access denied to sensitive resource!");
		}
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
	
	@PostMapping("/create-order")
	public String createOrder(HttpServletRequest request, @RequestBody Order order) {
		order.setOrderStatus("PENDING");
		orderRepository.save(order);
		return "Order Confirmed!";
	}
}
