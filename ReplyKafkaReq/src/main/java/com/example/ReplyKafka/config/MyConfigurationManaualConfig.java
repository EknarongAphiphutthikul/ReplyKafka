package com.example.ReplyKafka.config;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;

import com.example.ReplyKafka.ReplyKafkaApplication;
import com.example.ReplyKafka.kafka.interceptor.CorrelatingProducerInterceptor;
import com.example.protobuf.Model;

@Configuration
@Profile("manaual")
public class MyConfigurationManaualConfig extends KafkaConfigUtils {

	@Bean
	public ReplyingKafkaTemplate<String, Model, Model> initReplyKafkaTemplate() throws Exception {
		Map<String, Object> producerConfig =  producerConfig(ReplyKafkaApplication.serverPostKafka, ReplyKafkaApplication.clientId);
		producerConfig.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, CorrelatingProducerInterceptor.class.getName());
		ProducerFactory<String, Model> producerFactory = producerFactory(producerConfig);
		
		
		Map<String, Object> consumerConfig = consumerConfig(ReplyKafkaApplication.serverPostKafka, ReplyKafkaApplication.groupIdTopicResp, ReplyKafkaApplication.clientId, 5, false, true);
		ConsumerFactory<String, Model> consumerFactory = consumerFactory(consumerConfig);
		
		
		ConcurrentKafkaListenerContainerFactory<String, Model> factory = kafkaListenerContainerFactory(consumerFactory, 1);
		ConcurrentMessageListenerContainer<String, Model> replyContainer = concurrentMessageListenerContainer(factory, ReplyKafkaApplication.topicResponse, ReplyKafkaApplication.groupIdTopicResp);
		return replyKafkaTemplateForManaulConfig(producerFactory, replyContainer, true);
	}
	
	@PostConstruct
	public void print() {
		System.out.println("Config By MyConfigurationManaualConfig.class");
	}
}
