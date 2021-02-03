package com.example.ReplyKafka.kafka.interceptor;

import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.Header;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.support.KafkaHeaders;

import com.example.ReplyKafka.config.RedisConfig;
import com.example.ReplyKafka.model.Model;
import com.google.gson.Gson;

public class CorrelatingProducerInterceptor implements ProducerInterceptor<String, String> {
	
	private static Logger logger = LogManager.getLogger(CorrelatingProducerInterceptor.class);
	private Gson gson = new Gson();

	@Override
	public void configure(Map<String, ?> configs) {
	}

	@Override
	public ProducerRecord<String, String> onSend(ProducerRecord<String, String> record) {
		Header correlation = record.headers().lastHeader(KafkaHeaders.CORRELATION_ID);
		Model model = gson.fromJson(record.value(), Model.class);
		try {
			setToRedis(model.getKey(), correlation.value());
		} catch (Exception e) {
			logger.error("SaveRedis Exception :", e);
		}
		return record;
	}

	@Override
	public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
	}

	@Override
	public void close() {
	}
	
	private void setToRedis(String key, byte[] correlationId) throws Exception {
		if (null != key && null != correlationId && correlationId.length > 0) {
			RedisConfig.jedisManager.setKeyValue(key, DatatypeConverter.printHexBinary(correlationId));
			logger.info("Set value In Redis Success : key="+key);
		}
	}

}
