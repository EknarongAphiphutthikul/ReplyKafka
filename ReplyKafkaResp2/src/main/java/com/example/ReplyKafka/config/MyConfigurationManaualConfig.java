package com.example.ReplyKafka.config;

import javax.annotation.PostConstruct;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import com.example.ReplyKafka.ReplyKafkaApplication;

@EnableAutoConfiguration
@EnableKafka
@Configuration
@Profile("manaual")
public class MyConfigurationManaualConfig extends KafkaConfigUtils {

	@Bean
	public ConsumerFactory<String, String> initConsumerFactory() throws Exception {
		return consumerFactory(ReplyKafkaApplication.serverPostKafka, ReplyKafkaApplication.groupIdTopicReq, ReplyKafkaApplication.clientId, 5, false, true);
	}

	@Bean("kafkaListenerContainerFactory")
	public ConcurrentKafkaListenerContainerFactory<String, String> initkafkaListenerContainerFactory(ConsumerFactory<String, String> consumerFactory) throws Exception {
		return kafkaListenerContainerFactory(consumerFactory, 1);
	}

	@Bean
	public ProducerFactory<String, String> initProducerFactory() throws Exception {
		return producerFactory(ReplyKafkaApplication.serverPostKafka, ReplyKafkaApplication.clientId);
	}

	@Bean
	public KafkaTemplate<String, String> initReplyingTemplate(ProducerFactory<String, String> producerFactory) throws Exception {
		return kafkaTemplate(producerFactory);
	}

	@PostConstruct
	public void print() {
		System.out.println("Config By MyConfigurationManaualConfig");
	}

}
