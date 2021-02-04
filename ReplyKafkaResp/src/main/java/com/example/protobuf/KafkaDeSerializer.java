package com.example.protobuf;

import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;

import com.google.protobuf.InvalidProtocolBufferException;

public class KafkaDeSerializer implements Deserializer<Model> {

	@Override
	public void close() {
	}

	@Override
	public void configure(Map<String, ?> arg0, boolean arg1) {
	}

	@Override
	public Model deserialize(String topic, byte[] data) {
		try {
			return Model.parseFrom(data);
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
		return null;
	}

}
