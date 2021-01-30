package com.example.ReplyKafka.controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.ReplyKafka.ReplyKafkaApplication;
import com.example.ReplyKafka.service.KafkaSenderService;

@RestController
public class Controller {

	@Autowired
	private KafkaSenderService kafkaSenderService;

	@GetMapping("/senddata")
	public @ResponseBody String sendData(@RequestParam String msg) throws Exception {
		return kafkaSenderService.send(ReplyKafkaApplication.topicRequest, msg, 15000);
	}

	@GetMapping("/loadtest")
	public @ResponseBody boolean testReplyKafka() throws Exception {
		String req = generatingRandomStringBounded();
		String response = kafkaSenderService.send(ReplyKafkaApplication.topicRequest, req, 15000);
		boolean value = req.toUpperCase().equals(response);
		if (!value) {
			System.out.println("**********************FAIL*******************************");
		}
		return value;
	}

	private String generatingRandomStringBounded() {

		int leftLimit = 33;
		int rightLimit = 126;
		int targetStringLength = 20;
		Random random = new Random();
		StringBuilder buffer = new StringBuilder(targetStringLength);
		for (int i = 0; i < targetStringLength; i++) {
			int randomLimitedInt = leftLimit + (int) (random.nextFloat() * (rightLimit - leftLimit + 1));
			buffer.append((char) randomLimitedInt);
		}
		return buffer.toString();
	}
}
