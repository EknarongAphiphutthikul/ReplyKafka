package com.example.ReplyKafka.consumer;

import org.apache.kafka.clients.producer.ProducerRecord;
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

@Component
@Profile("thread")
public class KafkaSenderAsync {
	
	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	@Async("threadPoolTaskExecutor")
	public void sendToTopicResp(byte[] topicResp, byte[] correlationId, String msg) {
		try {		    
		    Message<String> message = MessageBuilder
	                .withPayload(msg.toUpperCase())
	                .setHeader(KafkaHeaders.TOPIC, topicResp)
	                .setHeader(KafkaHeaders.CORRELATION_ID, correlationId)
	                .build();

			ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(message);
			future.addCallback(new KafkaSendCallback<String, String>() {

			    @Override
			    public void onSuccess(SendResult<String, String> result) {
			    	ProducerRecord<String, String> success = result.getProducerRecord();
			    	System.out.println("Success : " + success.value());
			    }

			    @Override
			    public void onFailure(KafkaProducerException ex) {
			        ProducerRecord<String, String> failed = ex.getFailedProducerRecord();
			        System.out.println("Fail : " + failed.value());
			        ex.printStackTrace();
			    }

			});
		} catch (Exception e) {
			System.out.println("******************************************************* Send Topic Resp Fail ************************************************************");
			e.printStackTrace();
		}
	}
}
