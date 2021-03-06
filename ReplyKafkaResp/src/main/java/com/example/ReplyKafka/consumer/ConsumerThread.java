package com.example.ReplyKafka.consumer;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
@Profile("thread")
public class ConsumerThread {
	

	@Autowired
	private KafkaSenderAsync kafkaSenderAsync;

	@KafkaListener(topics = "test-reply-topic-req")
	public void listen(String in, @Header(KafkaHeaders.REPLY_TOPIC) byte[] replyTo,
			@Header(KafkaHeaders.CORRELATION_ID) byte[] correlation,
			@Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partitionId,
			@Header(KafkaHeaders.OFFSET) int offset) {
		System.out.println("Message=" + in + ", REPLY_TOPIC=" + new String(replyTo) + ", CORRELATION_ID=" + new String(correlation) + ", PartitionId=" + partitionId + ", offset=" + offset);
		kafkaSenderAsync.sendToTopicResp(replyTo, correlation, in);
	}
	
	@PostConstruct
	public void print() {
		System.out.println("Consumer By ConsumerThread");
	}
}
