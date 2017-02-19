package com.fiap.lucene.service.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.SubscribeRequest;
import com.amazonaws.services.sns.model.SubscribeResult;
import com.fiap.lucene.service.domain.ServiceStatus;
import com.fiap.lucene.service.processor.LuceneProcessor;

@RestController("/lucene")
public class LuceneController {

	@Autowired
	private LuceneProcessor processor;
	
	@Autowired
	@Value("${aws.sns.arn}")
	private String arn;
	
	private AmazonSNS sns;
	
	@Autowired
	public LuceneController(
			@Value("${aws.credentials.acccess.key}") 
			String accessKey,
			@Value("${aws.credentials.secret.key}")
			String secretKey) {

		BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
		sns = AmazonSNSClientBuilder
				.standard()
				.withCredentials(new AWSStaticCredentialsProvider(credentials))
				.withRegion(Regions.SA_EAST_1)
				.build();	
		
		setup();
	}
	
	@PostMapping
	public ServiceStatus process(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Map<String, String[]> map = request.getParameterMap();
		for (Map.Entry<String, String[]> entry : map.entrySet()) {
			System.out.println(entry.getKey());
			System.out.println(entry.getValue());
			System.out.println();
		}
		//processor.process(ticket);
		return new ServiceStatus(ServiceStatus.PROCESSING);
	}
	
	private void setup() {
		SubscribeRequest subscribeReq = new SubscribeRequest()
				.withTopicArn(arn)				
				.withProtocol("http")
				.withEndpoint("http://ec2-52-67-132-154.sa-east-1.compute.amazonaws.com:8080");
		 
		SubscribeResult r = sns.subscribe(subscribeReq);
		System.out.println(r.getSubscriptionArn());
	}
	
}
