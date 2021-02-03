package com.example.ReplyKafka.service;

import java.time.Duration;

import javax.xml.bind.DatatypeConverter;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.Header;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import com.example.ReplyKafka.model.Model;
import com.example.ReplyKafka.redis.JedisManager;
import com.google.gson.Gson;

@Service
public class KafkaSenderService {
	
	private static Logger logger = LogManager.getLogger(KafkaSenderService.class);
	
	private Gson gson = new Gson();
	@Autowired
	private ReplyingKafkaTemplate<String, String, String> replyKafkaTemplate;
	@Autowired
	private JedisManager jedisManager;

	public String send(String requestTopic, Model model, long timeoutMillis) throws Exception {
		String result = null;
		try {
			String message = gson.toJson(model);
			Duration timeout = Duration.ofMillis(timeoutMillis);
			ProducerRecord<String, String> record = new ProducerRecord<>(requestTopic, message);
			RequestReplyFuture<String, String, String> future = replyKafkaTemplate.sendAndReceive(record, timeout);
			SendResult<String, String> sendResult = future.getSendFuture().get();
			logger.info("Sent ok value: " + model.getMsg());
//			printMetaData(sendResult.getRecordMetadata(), sendResult.getProducerRecord());
			setToRedis(sendResult.getProducerRecord(), model.getKey());
			ConsumerRecord<String, String> response = future.get();
			result = gson.fromJson(response.value(), Model.class).getMsg();
			logger.info("Return value: " + result);
		} catch (Throwable e) {
			logger.info("******************************************************************* FAIL TO SEND *************************************************************");
			logger.error(e);
		}
		return result;
	}
	
	private void printMetaData(RecordMetadata metadata, ProducerRecord<String, String> record) {
		logger.info("MetaData : Offset=" + metadata.offset());
		logger.info("MetaData : Partition=" + metadata.partition());
		logger.info("MetaData : Topic=" + metadata.topic());
		
		logger.info("ProducerRecord Header : =" + record.headers());
	}
	
	private void setToRedis(ProducerRecord<String, String> record, String key) throws Exception {
		byte[] correlationId = null;
		for (Header header : record.headers()) {
			if (header.key().equals(KafkaHeaders.CORRELATION_ID)) {
				correlationId = header.value();
			}
		}
		jedisManager.setKeyValue(key, encodeUsingDataTypeConverter(correlationId));
		logger.info("Set value In Redis Success : key="+key);
	}
	
	public String encodeUsingDataTypeConverter(byte[] bytes) {
	    return DatatypeConverter.printHexBinary(bytes);
	}

	public byte[] decodeUsingDataTypeConverter(String hexString) {
	    return DatatypeConverter.parseHexBinary(hexString);
	}
	
}
