package com.example.ReplyKafka.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.ReplyKafka.service.KafkaSender;

@RestController				
public class Controller {

	@GetMapping("/")
	public @ResponseBody String testReplyKafka(@RequestParam String msg) throws Exception {
		return KafkaSender.send("test-reply-topic-req", msg, 5000);
	}
}
