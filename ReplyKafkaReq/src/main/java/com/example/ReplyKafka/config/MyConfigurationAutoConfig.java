package com.example.ReplyKafka.config;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;

import com.example.ReplyKafka.ReplyKafkaApplication;

@Configuration
@Profile("auto")
public class MyConfigurationAutoConfig extends KafkaConfigUtils {
	
	@Bean
	public ConcurrentMessageListenerContainer<String, String> replyContainer(ConcurrentKafkaListenerContainerFactory<String, String> factory) throws Exception {
		return concurrentMessageListenerContainer(factory, ReplyKafkaApplication.topicResponse, ReplyKafkaApplication.groupIdTopicResp);
	}

	@Bean
	public ReplyingKafkaTemplate<String, String, String> createReplyKafkaTemplate(ProducerFactory<String, String> producerFactory, ConcurrentMessageListenerContainer<String, String> replyContainer) throws Exception {
		return cretaeReplyKafkaTemplate(producerFactory, replyContainer);
	}

	@PostConstruct
	public void print() {
		System.out.println("Config By MyConfigurationAutoConfig.class");
	}
}
