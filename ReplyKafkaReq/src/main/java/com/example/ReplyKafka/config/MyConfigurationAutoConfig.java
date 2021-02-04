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
import com.example.protobuf.Model;

@Configuration
@Profile("auto")
public class MyConfigurationAutoConfig extends KafkaConfigUtils {
	
	@Bean
	public ConcurrentMessageListenerContainer<String, Model> initReplyContainer(ConcurrentKafkaListenerContainerFactory<String, Model> factory) throws Exception {
		return concurrentMessageListenerContainer(factory, ReplyKafkaApplication.topicResponse, ReplyKafkaApplication.groupIdTopicResp);
	}

	@Bean
	public ReplyingKafkaTemplate<String, Model, Model> initReplyKafkaTemplate(ProducerFactory<String, Model> producerFactory, ConcurrentMessageListenerContainer<String, Model> replyContainer) throws Exception {
		return replyKafkaTemplateForAutoConfig(producerFactory, replyContainer);
	}

	@PostConstruct
	public void print() {
		System.out.println("Config By MyConfigurationAutoConfig.class");
	}
}
