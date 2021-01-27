package com.example.ReplyKafka.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;

public abstract class KafkaProducerConfig {

	protected static ProducerFactory<String, String> producerFactory(String bootstrapService, String appName) throws Exception {
		Map<String, Object> configProps = new HashMap<>();
		configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapService);
		configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		configProps.put(ProducerConfig.ACKS_CONFIG, "all");
		configProps.put(ProducerConfig.CLIENT_ID_CONFIG, appName);
		return new DefaultKafkaProducerFactory<>(configProps);
	}

	protected static ReplyingKafkaTemplate<String, String, String> cretaeReplyKafkaTemplate(String bootstrapService, String appName, String replyTopic) throws Exception {
		ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
		ConcurrentMessageListenerContainer<String, String> replyContainer = factory.createContainer(replyTopic);
        replyContainer.getContainerProperties().setMissingTopicsFatal(false);
        replyContainer.getContainerProperties().setGroupId(appName);
		return new ReplyingKafkaTemplate<>(producerFactory(bootstrapService, appName), replyContainer);
	}
}
