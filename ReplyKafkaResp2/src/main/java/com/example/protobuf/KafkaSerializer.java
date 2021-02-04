package com.example.protobuf;

import java.util.Map;

import org.apache.kafka.common.serialization.Serializer;

public class KafkaSerializer implements Serializer<Model> {

	@Override
	public void configure(Map<String, ?> map, boolean b) {
	}

	@Override
	public void close() {
	}

	@Override
	public byte[] serialize(String topic, Model data) {
		return data.toByteArray();
	}

}
