package com.example.ReplyKafka.consumer;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaProducerException;
import org.springframework.kafka.core.KafkaSendCallback;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import com.example.ReplyKafka.ReplyKafkaApplication;
import com.example.protobuf.Model;

@Component
@Profile("thread")
public class KafkaSenderAsync {
	
	@Autowired
	private KafkaTemplate<String, Model> kafkaTemplate;
	private static Logger logger = LogManager.getLogger(KafkaSenderAsync.class);

	@Async("threadPoolTaskExecutor")
	public void sendToTopicResp(byte[] topicResp, byte[] correlationId, Model msg) {
		try {	
			Message<Model> message = MessageBuilder
	                .withPayload(Model.newBuilder().setKey(msg.getKey()).setMsg(msg.getMsg().toUpperCase()).build())
	                .setHeader(KafkaHeaders.TOPIC, ReplyKafkaApplication.topicResponse)
	                .build();

			ListenableFuture<SendResult<String, Model>> future = kafkaTemplate.send(message);
			future.addCallback(new KafkaSendCallback<String, Model>() {

			    @Override
			    public void onSuccess(SendResult<String, Model> result) {
			    	ProducerRecord<String, Model> success = result.getProducerRecord();
			    	logger.info("Success : " + success.value());
			    }

			    @Override
			    public void onFailure(KafkaProducerException ex) {
			        ProducerRecord<String, Model> failed = ex.getFailedProducerRecord();
			        logger.info("Fail : " + failed.value());
			       logger.error("KafkaProducerException Exception : ", ex);
			    }

			});
		} catch (Exception e) {
			logger.info("******************************************************* Send Topic Resp Fail ************************************************************");
			logger.error("sendToTopicResp Exception : ", e);
		}
	}
	
	@Async("threadPoolTaskExecutor")
	public void sendToTopicReq(byte[] topicResp, byte[] correlationId, Model msg) {
		try {	
		    Message<Model> message = MessageBuilder
	                .withPayload(msg)
	                .setHeader(KafkaHeaders.TOPIC, ReplyKafkaApplication.topicRequest)
	                .setHeader(KafkaHeaders.REPLY_TOPIC, topicResp)
	                .setHeader(KafkaHeaders.CORRELATION_ID, correlationId)
	                .build();

			ListenableFuture<SendResult<String, Model>> future = kafkaTemplate.send(message);
			future.addCallback(new KafkaSendCallback<String, Model>() {

			    @Override
			    public void onSuccess(SendResult<String, Model> result) {
			    	ProducerRecord<String, Model> success = result.getProducerRecord();
			    	logger.info("Success : " + success.value());
			    }

			    @Override
			    public void onFailure(KafkaProducerException ex) {
			        ProducerRecord<String, Model> failed = ex.getFailedProducerRecord();
			        logger.info("Fail : " + failed.value());
			       logger.error("KafkaProducerException Exception : ", ex);
			    }

			});
		} catch (Exception e) {
			logger.info("******************************************************* Send Topic Resp Fail ************************************************************");
			logger.error("sendToTopicResp Exception : ", e);
		}
	}
}
