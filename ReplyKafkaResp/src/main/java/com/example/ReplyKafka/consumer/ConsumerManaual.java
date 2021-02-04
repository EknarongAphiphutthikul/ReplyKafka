package com.example.ReplyKafka.consumer;

import javax.annotation.PostConstruct;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaProducerException;
import org.springframework.kafka.core.KafkaSendCallback;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import com.example.ReplyKafka.ReplyKafkaApplication;
import com.example.protobuf.Model;

@Component
@Profile("manaual")
public class ConsumerManaual {
	
	@Autowired
	private KafkaTemplate<String, Model> kafkaTemplate;
	
	private static Logger logger = LogManager.getLogger(ConsumerManaual.class);

	@KafkaListener(topics = "test-reply-topic-req")
	public void listen(Model model, @Header(KafkaHeaders.REPLY_TOPIC) byte[] replyTo,
			@Header(KafkaHeaders.CORRELATION_ID) byte[] correlation,
			@Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partitionId,
			@Header(KafkaHeaders.OFFSET) int offset) {
		logger.info("Message=" + model + ", REPLY_TOPIC=" + new String(replyTo) + ", CORRELATION_ID=" + new String(correlation) + ", PartitionId=" + partitionId + ", offset=" + offset);
		sendToTopicResp(replyTo, correlation, model);
	}
	
	private void sendToTopicResp(byte[] topicResp, byte[] correlationId, Model msg) {
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
			        logger.error("KafkaProducerException : ", ex);
			    }

			});
		} catch (Exception e) {
			logger.info("******************************************************* Send Topic Resp Fail ************************************************************");
			 logger.error("sendToTopicResp Exception : ", e);
		}
	}
	
	
	@PostConstruct
	public void print() {
		logger.info("Consumer By ConsumerManaual");
	}
}
