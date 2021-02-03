package com.example.ReplyKafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ReplyKafkaApplication {
	
	public static final String serverPostKafka = System.getProperty("SERVER_PORT");
	public static final String clientId = System.getProperty("CLIENT_ID");
	public static final String groupIdTopicReq = System.getProperty("GROUPID_TOPIC_REQ");
	public static final String topicRequest = System.getProperty("TOPIC_REQ");
	public static final String topicResponse = System.getProperty("TOPIC_RESP");

	public static void main(String[] args) {
		SpringApplication.run(ReplyKafkaApplication.class, args);
	}

}
