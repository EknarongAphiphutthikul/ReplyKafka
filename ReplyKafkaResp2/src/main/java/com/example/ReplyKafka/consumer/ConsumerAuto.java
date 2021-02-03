package com.example.ReplyKafka.consumer;

import javax.annotation.PostConstruct;
import javax.xml.bind.DatatypeConverter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import com.example.ReplyKafka.ReplyKafkaApplication;
import com.example.ReplyKafka.model.Model;
import com.example.ReplyKafka.redis.JedisManager;
import com.google.gson.Gson;

@Component
@Profile("auto")
public class ConsumerAuto {
	
	private Gson gson = new Gson();
	@Autowired
	private JedisManager jedisManager;
	private static Logger logger = LogManager.getLogger(ConsumerAuto.class);

	@KafkaListener(topics = "test-reply-topic-resp")
	public Message<?> listen(String in,
			@Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partitionId,
			@Header(KafkaHeaders.OFFSET) int offset) {
		logger.info("Message=" + in + ", PartitionId=" + partitionId + ", offset=" + offset);
		Model model = gson.fromJson(in, Model.class);
		byte[] correlation = null;
		try {
			String valueStr = jedisManager.getValue(model.getKey());
			logger.info("get value correlationId at redis : key="+model.getKey()+ ", value=" + valueStr);
			correlation = DatatypeConverter.parseHexBinary(valueStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return MessageBuilder.withPayload(in)
				.setHeader(KafkaHeaders.TOPIC, ReplyKafkaApplication.topicResponse)
				.setHeader(KafkaHeaders.CORRELATION_ID, correlation)
				.build();
	}
	
	@PostConstruct
	public void print() {
		logger.info("Consumer By ConsumerAuto");
	}
}
