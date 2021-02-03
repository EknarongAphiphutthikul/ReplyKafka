package com.example.ReplyKafka.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Model implements Serializable {
	
	private static final long serialVersionUID = 7476167124713404556L;
	private String msg;
	private String key;

}
