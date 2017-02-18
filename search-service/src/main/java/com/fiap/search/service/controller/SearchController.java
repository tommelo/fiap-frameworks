package com.fiap.search.service.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.fiap.search.service.domain.Ticket;
import com.fiap.search.service.utils.JacksonUtils;

@RestController("/search")
public class SearchController {
	
	@Value("${aws.sns.arn}")
	private String arn;
	
	@Autowired
	private AmazonSNS sns;
	
	@GetMapping
	@CrossOrigin("*")
	public Ticket get(String query) {
		String id = UUID.randomUUID().toString();		
		Ticket ticket = new Ticket(id, query);
		
		PublishRequest request = 
				new PublishRequest(
						arn, 
						JacksonUtils.toJson(ticket));
		
		PublishResult result = sns.publish(request);
		System.out.println("Message delivered | ID: " + result.getMessageId());
		 
		return ticket;
	}
	
	
}
