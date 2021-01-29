package com.example.ReplyKafka.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import com.example.ReplyKafka.ReplyKafkaApplication;

@EnableAutoConfiguration
@EnableKafka
@Configuration
public class MyConfigurationAutoConfig {

	private Map<String, Object> consumerConfig(String bootstrapService, String groupId, String clientId, Integer maxPollRecords, boolean allowAutoCreateTopicFlag, boolean enableAutoCommit) {
		Map<String, Object> config = new HashMap<>();
		config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapService);
		config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
		config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
		config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
		config.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
		config.put(ConsumerConfig.ALLOW_AUTO_CREATE_TOPICS_CONFIG, allowAutoCreateTopicFlag);
		config.put(ConsumerConfig.CLIENT_ID_CONFIG, clientId);
		return config;
	}
	
	@Bean
	public ConsumerFactory<String, String> initConsumerFactory() throws Exception {
		return new DefaultKafkaConsumerFactory<>(consumerConfig(ReplyKafkaApplication.serverPostKafka, ReplyKafkaApplication.groupIdTopicReq, ReplyKafkaApplication.clientId, 5, false, true));
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(ConsumerFactory<String, String> consumerFactory, KafkaTemplate<String, String> replyTemplate) throws Exception {
	    ConcurrentKafkaListenerContainerFactory<String, String> factory =
	        new ConcurrentKafkaListenerContainerFactory<>();
	    factory.setConsumerFactory(consumerFactory);
	    factory.setReplyTemplate(replyTemplate);
	    factory.setAutoStartup(true);
	    return factory;
	}

	
	private Map<String, Object> producerConfig(String bootstrapService, String clientId) {
		Map<String, Object> configProps = new HashMap<>();
		configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapService);
		configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		configProps.put(ProducerConfig.ACKS_CONFIG, "all");
		configProps.put(ProducerConfig.CLIENT_ID_CONFIG, clientId);
		return configProps;
	}
	
	@Bean
	public ProducerFactory<String, String> initProducerFactory() throws Exception {
		return new DefaultKafkaProducerFactory<>(producerConfig(ReplyKafkaApplication.serverPostKafka, ReplyKafkaApplication.clientId));
	}

	@Bean
	public KafkaTemplate<String, String> initReplyingTemplate(ProducerFactory<String, String> producerFactory) throws Exception {
		return new KafkaTemplate<>(producerFactory);
	}
	
}
