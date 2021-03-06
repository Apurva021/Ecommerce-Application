package com.apurva.cartservice;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface CartRepository extends MongoRepository<Cart, String>{
	public Cart findByUserIdInteger(Integer userIdInteger);
}
