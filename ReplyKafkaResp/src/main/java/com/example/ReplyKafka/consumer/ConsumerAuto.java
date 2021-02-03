package com.example.ReplyKafka.consumer;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import com.example.ReplyKafka.ReplyKafkaApplication;
import com.example.ReplyKafka.model.Model;
import com.google.gson.Gson;

@Component
@Profile("auto")
public class ConsumerAuto {
	
	private static Logger logger = LogManager.getLogger(ConsumerAuto.class);
	private Gson gson = new Gson();

	@KafkaListener(topics = "test-reply-topic-req")
	public Message<?> listen(String in, @Header(KafkaHeaders.REPLY_TOPIC) byte[] replyTo,
			@Header(KafkaHeaders.CORRELATION_ID) byte[] correlation,
			@Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partitionId,
			@Header(KafkaHeaders.OFFSET) int offset) {
		logger.info("Message=" + in + ", REPLY_TOPIC=" + new String(replyTo) + ", CORRELATION_ID=" + new String(correlation) + ", PartitionId=" + partitionId + ", offset=" + offset);
		Model model = gson.fromJson(in, Model.class);
		model.setMsg(model.getMsg().toUpperCase());
		return MessageBuilder.withPayload(gson.toJson(model))
				.setHeader(KafkaHeaders.TOPIC, ReplyKafkaApplication.topicResponse)
//				.setHeader(KafkaHeaders.CORRELATION_ID, correlation)
				.build();
	}
	
	@PostConstruct
	public void print() {
		logger.info("Consumer By ConsumerAuto");
	}
}
