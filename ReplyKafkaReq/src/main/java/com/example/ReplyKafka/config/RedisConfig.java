package com.example.ReplyKafka.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.ReplyKafka.redis.JedisManager;

@Configuration
public class RedisConfig {

	
	@Bean
	public JedisManager createJedisManager() throws Exception {
		return new JedisManager(1);
	}
}
