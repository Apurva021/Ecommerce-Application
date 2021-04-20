package com.apurva.orderservice.kafka;



import java.util.LinkedHashMap;
import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.apurva.orderservice.Order;
import com.apurva.orderservice.OrderRepository;





@Service 
public class kafkaService {

	@Autowired
	private OrderRepository repo;

	
	@KafkaListener( topics="OrderStream", containerFactory="kafkaListnerContainerFactory")
	public void kafkaListner(StatusUpdate msg) {
		
		try {
		if (msg.getEventType().equalsIgnoreCase("confirmed")) {
			if(msg.getReceiptId()==null) {
				throw new Exception("receiptId not found");
			}
			List<Order> orders = repo.findByReceiptIdString(msg.getReceiptId());
			for(Order order : orders) {
				order.setOrderStatus(msg.getStatus());
				repo.save(order);
			}
			System.out.println("Message Consumed.....");
			
		}else if( msg.getEventType().equalsIgnoreCase("canceled")){
			if(msg.getReceiptId()==null) {
				if(msg.getOrderId()==null) {
					throw new Exception("no id found");
				}else {
					Order order = repo.findByOrderIdInteger(Integer.parseInt(msg.getOrderId()));
					order.setOrderStatus(msg.getStatus());
					repo.save(order);
				}
			}else {
				List<Order> orders = repo.findByReceiptIdString(msg.getReceiptId());
				for(Order order : orders) {
					order.setOrderStatus(msg.getStatus());
					repo.save(order);
				}
			}
			System.out.println("Message Consumed.....");
		}else if(msg.getEventType().equalsIgnoreCase("shipped")){ 
			if(msg.getOrderId()==null) {
				throw new Exception("No orderId found");
			}else {
				Order order = repo.findByOrderIdInteger(Integer.parseInt(msg.getOrderId()));
				order.setOrderStatus(msg.getStatus());
				repo.save(order);
			}
			System.out.println("Message Consumed.....");
		}else if(msg.getEventType().equalsIgnoreCase("delivered")) {
			if(msg.getOrderId()==null) {
				throw new Exception("No orderId found");
			}else {
				Order order = repo.findByOrderIdInteger(Integer.parseInt(msg.getOrderId()));
				order.setOrderStatus(msg.getStatus());
				repo.save(order);
			}
			System.out.println("Message Consumed.....");
		}
		else {
			System.out.println("type mismatch");
		}
		}catch(Exception e) {
			System.out.println("Exception : " + e.getMessage());
			e.printStackTrace();
		}
	}
	
//	@KafkaListener( topics="TestStream", containerFactory="objectKafkaListner")
//	public void paymentListner(ConsumerRecord e) {
//		LinkedHashMap<String,Object> map =(LinkedHashMap<String, Object>) e.value();
//		
//		System.out.println(map.get("title"));
//	}
}
