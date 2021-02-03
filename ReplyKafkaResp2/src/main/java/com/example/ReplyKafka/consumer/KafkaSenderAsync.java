package com.example.ReplyKafka.consumer;

import javax.xml.bind.DatatypeConverter;

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
import com.example.ReplyKafka.model.Model;
import com.example.ReplyKafka.redis.JedisManager;
import com.google.gson.Gson;

@Component
@Profile("thread")
public class KafkaSenderAsync {
	
	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;
	
	private Gson gson = new Gson();
	@Autowired
	private JedisManager jedisManager;
	private static Logger logger = LogManager.getLogger(KafkaSenderAsync.class);

	@Async("threadPoolTaskExecutor")
	public void sendToTopicResp(String msg) {
		try {	
			Model model = gson.fromJson(msg, Model.class);
			byte[] correlationId = null;
			try {
				String valueStr = jedisManager.getValue(model.getKey());
				logger.info("get value correlationId at redis : key="+model.getKey()+ ", value=" + valueStr);
				correlationId = DatatypeConverter.parseHexBinary(valueStr);
			} catch (Exception e) {
				e.printStackTrace();
			}
		    Message<String> message = MessageBuilder
	                .withPayload(gson.toJson(model))
	                .setHeader(KafkaHeaders.TOPIC, ReplyKafkaApplication.topicResponse)
	                .setHeader(KafkaHeaders.CORRELATION_ID, correlationId)
	                .build();

			ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(message);
			future.addCallback(new KafkaSendCallback<String, String>() {

			    @Override
			    public void onSuccess(SendResult<String, String> result) {
			    	ProducerRecord<String, String> success = result.getProducerRecord();
			    	logger.info("Success : " + success.value());
			    }

			    @Override
			    public void onFailure(KafkaProducerException ex) {
			        ProducerRecord<String, String> failed = ex.getFailedProducerRecord();
			        logger.info("Fail : " + failed.value());
			        logger.error(ex);
			    }

			});
		} catch (Exception e) {
			logger.info("******************************************************* Send Topic Resp Fail ************************************************************");
			 logger.error(e);
		}
	}
}
