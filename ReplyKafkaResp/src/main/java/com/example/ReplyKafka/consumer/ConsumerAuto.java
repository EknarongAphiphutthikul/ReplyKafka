package com.example.ReplyKafka.consumer;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@Profile("auto")
public class ConsumerAuto {

	@KafkaListener(topics = "test-reply-topic-req")
	public Message<?> listen(String in, @Header(KafkaHeaders.REPLY_TOPIC) byte[] replyTo,
			@Header(KafkaHeaders.CORRELATION_ID) byte[] correlation,
			@Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partitionId,
			@Header(KafkaHeaders.OFFSET) int offset) {
		System.out.println("Message=" + in + ", REPLY_TOPIC=" + new String(replyTo) + ", CORRELATION_ID=" + new String(correlation) + ", PartitionId=" + partitionId + ", offset=" + offset);
		return MessageBuilder.withPayload(in.toUpperCase())
				.setHeader(KafkaHeaders.TOPIC, replyTo)
				.setHeader(KafkaHeaders.CORRELATION_ID, correlation)
				.build();
	}
	
	@PostConstruct
	public void print() {
		System.out.println("Consumer By ConsumerAuto");
	}
}
