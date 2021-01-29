package com.example.ReplyKafka.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;

public abstract class KafkaConfigUtils {
	
	/*
	 * Kafka Producer
	 */
	// producer config
	protected static Map<String, Object> producerConfig(String bootstrapService, String clientId) {
		Map<String, Object> configProps = new HashMap<>();
		configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapService);
		configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		configProps.put(ProducerConfig.ACKS_CONFIG, "all");
		configProps.put(ProducerConfig.CLIENT_ID_CONFIG, clientId);
		return configProps;
	}
	
	// producer factory
	protected static ProducerFactory<String, String> producerFactory(String bootstrapService, String clientId) throws Exception {
		return new DefaultKafkaProducerFactory<>(producerConfig(bootstrapService, clientId));
	}
	protected static ProducerFactory<String, String> producerFactory(Map<String, Object> producerConfig) throws Exception {
		return new DefaultKafkaProducerFactory<>(producerConfig);
	}
	
	// reply kafka template
	protected static ReplyingKafkaTemplate<String, String, String> replyKafkaTemplateForAutoConfig(ProducerFactory<String, String> producerFactory, ConcurrentMessageListenerContainer<String, String> replyContainer) throws Exception {
		return replyKafkaTemplateForManaulConfig(producerFactory, replyContainer, false);
	}
	protected static ReplyingKafkaTemplate<String, String, String> replyKafkaTemplateForManaulConfig(ProducerFactory<String, String> producerFactory, ConcurrentMessageListenerContainer<String, String> replyContainer, boolean flagStart) throws Exception {
		ReplyingKafkaTemplate<String, String, String> replyKafkaTemplate = new ReplyingKafkaTemplate<>(producerFactory, replyContainer);
		replyKafkaTemplate.setSharedReplyTopic(true);
		if (flagStart) {
			replyKafkaTemplate.start();
		}
		return replyKafkaTemplate;
	}
	
	// kafka template
	protected static KafkaTemplate<String, String> kafkaTemplate(ProducerFactory<String, String> producerFactory) throws Exception {
		return new KafkaTemplate<>(producerFactory);
	}
	
	
	/*
	 * Kafka Consumer
	 */
	// consumer config
	protected static Map<String, Object> consumerConfig(String bootstrapService, String groupId, String clientId, Integer maxPollRecords, boolean allowAutoCreateTopicFlag, boolean enableAutoCommit) {
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
	
	// consumer kafka
	protected static KafkaConsumer<String, String> kafKaConsumer(String bootstrapService, String groupId, String clientId, Integer maxPollRecords, boolean allowAutoCreateTopicFlag, boolean enableAutoCommit) throws Exception {
		return new KafkaConsumer<>(consumerConfig(bootstrapService, groupId, clientId, maxPollRecords, allowAutoCreateTopicFlag, enableAutoCommit));
	}
	protected static KafkaConsumer<String, String> kafKaConsumer(Map<String, Object> config) throws Exception {
		return new KafkaConsumer<>(config);
	}
	
	// consumer factory for consumer reply kafka
	protected static ConsumerFactory<String, String> consumerFactory(String bootstrapService, String groupId, String clientId, Integer maxPollRecords, boolean allowAutoCreateTopicFlag, boolean enableAutoCommit) throws Exception {
		return new DefaultKafkaConsumerFactory<>(consumerConfig(bootstrapService, groupId, clientId, maxPollRecords, allowAutoCreateTopicFlag, enableAutoCommit));
	}
	protected ConsumerFactory<String, String> consumerFactory(Map<String, Object> config) throws Exception {
		return new DefaultKafkaConsumerFactory<>(config);
	}
	
	// Concurrent Kafka Listener Container Factory
	protected static ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(ConsumerFactory<String, String> consumerFactory,  Integer concurrency) throws Exception {
	    ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
	    factory.setConsumerFactory(consumerFactory);
	    factory.setConcurrency(1);
	    if (null != concurrency) {
	    	factory.setConcurrency(concurrency);
	    }
	    return factory;
	}
	protected static ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(ConsumerFactory<String, String> consumerFactory, KafkaTemplate<String, String> kafkaTemplate) throws Exception {
	    ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
	    factory.setConsumerFactory(consumerFactory);
	    factory.setReplyTemplate(kafkaTemplate);
	    factory.setConcurrency(1);
	    factory.setAutoStartup(true);
	    return factory;
	}
	
	// Concurrent Message Listener Container
	protected static ConcurrentMessageListenerContainer<String, String> concurrentMessageListenerContainer(ConcurrentKafkaListenerContainerFactory<String, String> factory, String replyTopic, String groupIdReplyTopic) throws Exception {
		ConcurrentMessageListenerContainer<String, String> replyContainer = factory.createContainer(replyTopic);
		replyContainer.getContainerProperties().setMissingTopicsFatal(true);
		replyContainer.getContainerProperties().setGroupId(groupIdReplyTopic);
		replyContainer.setAutoStartup(true);
		return replyContainer;
	}
}
