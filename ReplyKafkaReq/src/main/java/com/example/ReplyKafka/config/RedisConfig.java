package com.example.ReplyKafka.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.ReplyKafka.redis.JedisManager;

@Configuration
public class RedisConfig {

	public static JedisManager jedisManager = null;
	
	@Bean
	public JedisManager createJedisManager() throws Exception {
		RedisConfig.jedisManager = new JedisManager(1);
		return RedisConfig.jedisManager;
	}
}
