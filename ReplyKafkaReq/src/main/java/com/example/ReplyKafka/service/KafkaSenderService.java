package com.example.ReplyKafka.service;

import java.time.Duration;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

@Service
public class KafkaSenderService {
	
	@Autowired
	private ReplyingKafkaTemplate<String, String, String> replyKafkaTemplate;

	public String send(String requestTopic, String message, long timeoutMillis) throws Exception {
		String result = null;
		try {
			Duration timeout = Duration.ofMillis(timeoutMillis);
			ProducerRecord<String, String> record = new ProducerRecord<>(requestTopic, message);
			RequestReplyFuture<String, String, String> future = replyKafkaTemplate.sendAndReceive(record, timeout);
			SendResult<String, String> sendResult = future.getSendFuture().get();
			System.out.println("Sent ok: " + sendResult.getRecordMetadata());
			ConsumerRecord<String, String> response = future.get();
			result = response.value();
			System.out.println("Return value: " + result);
		} catch (Throwable e) {
			System.out.println("******************************************************************* FAIL TO SEND *************************************************************");
			e.printStackTrace();
		}
		return result;
	}
	
}
