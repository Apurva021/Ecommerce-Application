package com.apurva.orderservice.kafka;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;




@Configuration
public class KafkaConfiguration {

	
	@Bean
	public ConsumerFactory<String,StatusUpdate> consumerFactory(){
		
		
		Map<String,Object> config = new HashMap<>();
		config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
		config.put(ConsumerConfig.GROUP_ID_CONFIG,"Group1");
//		config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//		config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
		JsonDeserializer<StatusUpdate> jd = new JsonDeserializer<>(StatusUpdate.class,false);
		jd.addTrustedPackages("*");
	
				 
		//return new DefaultKafkaConsumerFactory<>(config);
		return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), jd);
		
	}
	
	@Bean
	public ConcurrentKafkaListenerContainerFactory<String,StatusUpdate> kafkaListnerContainerFactory(){
		ConcurrentKafkaListenerContainerFactory<String,StatusUpdate> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory());
		return factory;
	}
	
	
}
