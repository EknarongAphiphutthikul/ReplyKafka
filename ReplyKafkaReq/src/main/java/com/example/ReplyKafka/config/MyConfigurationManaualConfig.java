package com.example.ReplyKafka.config;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;

import com.example.ReplyKafka.ReplyKafkaApplication;

@Configuration
@Profile("manaual")
public class MyConfigurationManaualConfig extends KafkaConfigUtils {

	@Bean
	public ReplyingKafkaTemplate<String, String, String> initReplyKafkaTemplate() throws Exception {
		ProducerFactory<String, String> producerFactory = producerFactory(ReplyKafkaApplication.serverPostKafka, ReplyKafkaApplication.clientId);
		ConsumerFactory<String, String> consumerFactory = consumerFactory(ReplyKafkaApplication.serverPostKafka, ReplyKafkaApplication.groupIdTopicResp, ReplyKafkaApplication.clientId, 5, false, true);
		ConcurrentKafkaListenerContainerFactory<String, String> factory = kafkaListenerContainerFactory(consumerFactory, 1);
		ConcurrentMessageListenerContainer<String, String> replyContainer = concurrentMessageListenerContainer(factory, ReplyKafkaApplication.topicResponse, ReplyKafkaApplication.groupIdTopicResp);
		return replyKafkaTemplateForManaulConfig(producerFactory, replyContainer, true);
	}
	
	@PostConstruct
	public void print() {
		System.out.println("Config By MyConfigurationManaualConfig.class");
	}
}
