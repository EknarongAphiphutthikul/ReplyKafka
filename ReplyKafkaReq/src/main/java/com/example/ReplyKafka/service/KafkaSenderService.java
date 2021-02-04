package com.example.ReplyKafka.service;

import java.time.Duration;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import com.example.protobuf.Model;

@Service
public class KafkaSenderService {
	
	private static Logger logger = LogManager.getLogger(KafkaSenderService.class);
	
	@Autowired
	private ReplyingKafkaTemplate<String, Model, Model> replyKafkaTemplate;

	public String send(String requestTopic, Model.Builder model, long timeoutMillis) throws Exception {
		Model result = null;
		try {
			Duration timeout = Duration.ofMillis(timeoutMillis);
			ProducerRecord<String, Model> record = new ProducerRecord<>(requestTopic, model.build());
			RequestReplyFuture<String, Model, Model> future = replyKafkaTemplate.sendAndReceive(record, timeout);
			SendResult<String, Model> sendResult = future.getSendFuture().get();
			logger.info("Sent ok value: " + model.getMsg());
			printMetaData(sendResult.getRecordMetadata(), sendResult.getProducerRecord());
			ConsumerRecord<String, Model> response = future.get();
			result = response.value();
			logger.info("Return value: " + result);
		} catch (Throwable e) {
			logger.info("******************************************************************* FAIL TO SEND *************************************************************");
			logger.error("Send Exception : ", e);
		}
		return result.getMsg();
	}
	
	private void printMetaData(RecordMetadata metadata, ProducerRecord<String, Model> record) {
		logger.info("MetaData : Offset=" + metadata.offset());
		logger.info("MetaData : Partition=" + metadata.partition());
		logger.info("MetaData : Topic=" + metadata.topic());
		
		logger.info("ProducerRecord Header : =" + record.headers());
	}
	
}
