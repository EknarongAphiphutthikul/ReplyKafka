package com.example.ReplyKafka.service;

import java.time.Duration;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;

import com.example.ReplyKafka.config.KafkaProducerConfig;

public class KafkaSender extends KafkaProducerConfig {
	
	private KafkaSender() {
	    throw new IllegalStateException("Utility class");
	}
	
	private static String appName = "ReplyKafkaProject";
	private static ReplyingKafkaTemplate<String, String, String> replyKafkaTemplate = null;

	public static String send(String requestTopic, String message, long timeoutMillis) throws Exception {
		String result = null;
		try {
			checkReplyKafkaTemplateConnection();
			Duration timeout = Duration.ofMillis(timeoutMillis);
			ProducerRecord<String, String> record = new ProducerRecord<>(requestTopic, message);
			RequestReplyFuture<String, String, String> future = KafkaSender.replyKafkaTemplate.sendAndReceive(record, timeout);
			ConsumerRecord<String, String> response = future.get();
			result = response.value();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private static void checkReplyKafkaTemplateConnection() throws Exception {
		if (null == KafkaSender.replyKafkaTemplate) {
			KafkaSender.replyKafkaTemplate = cretaeReplyKafkaTemplate("localhost:9093", appName, "test-reply-topic-resp");
		}
	}
}
