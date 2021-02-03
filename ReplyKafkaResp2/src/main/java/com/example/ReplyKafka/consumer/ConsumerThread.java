package com.example.ReplyKafka.consumer;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
	private static Logger logger = LogManager.getLogger(ConsumerThread.class);

	@KafkaListener(topics = "test-reply-topic-resp")
	public void listen(String in, 
			@Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partitionId,
			@Header(KafkaHeaders.OFFSET) int offset) {
		logger.info("Message=" + in + ", PartitionId=" + partitionId + ", offset=" + offset);
		kafkaSenderAsync.sendToTopicResp(in);
	}
	
	@PostConstruct
	public void print() {
		logger.info("Consumer By ConsumerThread");
	}
}
