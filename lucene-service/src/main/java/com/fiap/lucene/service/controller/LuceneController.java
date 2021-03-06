package com.fiap.lucene.service.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.ConfirmSubscriptionRequest;
import com.amazonaws.services.sns.model.SubscribeRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.lucene.service.domain.Ticket;
import com.fiap.lucene.service.processor.LuceneProcessor;
import com.fiap.lucene.service.utils.JacksonUtils;

@Component
public class LuceneController implements CommandLineRunner {

	@Value("${aws.sns.endpoint}")
	private String endpoint;
	
	@Value("${aws.sns.endpoint.protocol}")
	private String protocol;
	
	@Value("${jetty.server.port}")
	private int port;	
		
	@Value("${aws.sns.arn}")
	private String arn;
		
	@Value("${aws.credentials.acccess.key}") 
	private String accessKey;
	
	@Value("${aws.credentials.secret.key}")
	private String secretKey;
	
	@Autowired
	private LuceneProcessor processor;
	
	private AmazonSNS sns;
	
	private BlockingQueue<Map<String, String>> messageQueue = 
			new LinkedBlockingQueue<Map<String, String>>();
	
	@Override
	public void run(String... arg0) throws Exception {
		BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
		sns = AmazonSNSClientBuilder
				.standard()
				.withCredentials(new AWSStaticCredentialsProvider(credentials))
				.withRegion(Regions.SA_EAST_1)
				.build();	
		
		Server server = new Server(port);
		server.setHandler(new AmazonSNSHandler());
		server.start();
		
		SubscribeRequest subscribeReq = new SubscribeRequest()
				.withTopicArn(arn)				
				.withProtocol(protocol)
				.withEndpoint(endpoint);
		 
		sns.subscribe(subscribeReq);
				
		for (;;) {

            Map<String, String> messageMap = messageQueue.take();

            String token = messageMap.get("Token");            
            if (token != null) {
                ConfirmSubscriptionRequest confirmReq = new ConfirmSubscriptionRequest()
                    .withTopicArn(arn)
                    .withToken(token);
                
                sns.confirmSubscription(confirmReq);
                continue;
            }

            String message = messageMap.get("Message");
            if (message != null) {
                System.out.println("Received message: " + message);
                Ticket ticket = JacksonUtils.fromJson(message, Ticket.class);
                processor.process(ticket);
            }
        }
	}

	/**
	 * 
	 * @author 
	 *
	 */
	class AmazonSNSHandler extends AbstractHandler {
    	
		@Override
		@SuppressWarnings("unchecked")
        public void handle(String arg0, Request req, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

            Scanner scanner = new Scanner(request.getInputStream());
            StringBuilder sb = new StringBuilder();
            while (scanner.hasNextLine()) {
                sb.append(scanner.nextLine());
            }
            
            InputStream bytes = new ByteArrayInputStream(sb.toString().getBytes());
            Map<String, String> messageMap = new ObjectMapper().readValue(bytes, Map.class);
            
            messageQueue.add(messageMap);
            scanner.close();
            
            response.setContentType("text/html");
            response.setStatus(HttpServletResponse.SC_OK);
            ((Request) request).setHandled(true);
        }
        
    }

	
}
