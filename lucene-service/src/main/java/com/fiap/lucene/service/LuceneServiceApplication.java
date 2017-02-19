package com.fiap.lucene.service;

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
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.internal.StaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.ConfirmSubscriptionRequest;
import com.amazonaws.services.sns.model.SubscribeRequest;
import com.amazonaws.services.sns.model.SubscribeResult;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
@EnableRedisRepositories
@EnableAsync
public class LuceneServiceApplication {

	 // AWS credentials -- replace with your credentials
    static String ACCESS_KEY = "AKIAIDEYUNIZLYL7FFGQ";
    static String SECRET_KEY = "yGqytXPCsfkThaxTHLFh8MVP0oVBiP3g+6VHbE4e";

    // Shared queue for notifications from HTTP server
    static BlockingQueue<Map<String, String>> messageQueue = new LinkedBlockingQueue<Map<String, String>>();

    
	public static void main(String[] args) throws Exception {
		//SpringApplication.run(LuceneServiceApplication.class, args);
		// Create a client
        AmazonSNS service = AmazonSNSClientBuilder
        		.standard()
        		.withCredentials(new StaticCredentialsProvider(new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY)))
        		.withRegion(Regions.SA_EAST_1)
        		.build();
        
        
        
        // Create a topic
        /*
        CreateTopicRequest createReq = new CreateTopicRequest()
            .withName("MyTopic");
        CreateTopicResult createRes = service.createTopic(createReq);
        */

        // Get an HTTP Port
        int port = args.length == 1 ? Integer.parseInt(args[0]) : 8989;

        // Create and start HTTP server
        Server server = new Server(port);
        server.setHandler(new AmazonSNSHandler());
        server.start();

        // Subscribe to topic
        SubscribeRequest subscribeReq = new SubscribeRequest()
            .withTopicArn("arn:aws:sns:sa-east-1:127262864231:sns-lucene")
            .withProtocol("http")
            .withEndpoint("http://ec2-52-67-132-154.sa-east-1.compute.amazonaws.com:8080");
        SubscribeResult r = service.subscribe(subscribeReq);
                       
        System.out.println("Subscribe Request Delivered: " + r.getSubscriptionArn());

        for (;;) {

            // Wait for a message from HTTP server
            Map<String, String> messageMap = messageQueue.take();

            // Look for a subscription confirmation Token
            String token = messageMap.get("Token");
            System.out.println("GOT TOKEN");
            System.out.println(token);

            if (token != null) {

                // Confirm subscription
                ConfirmSubscriptionRequest confirmReq = new ConfirmSubscriptionRequest()
                    .withTopicArn("arn:aws:sns:sa-east-1:127262864231:sns-lucene")
                    .withToken(token);
                service.confirmSubscription(confirmReq);

                continue;
            }

            // Check for a notification
            String message = messageMap.get("Message");
            if (message != null) {
                System.out.println("Received message: " + message);
            }
        }
	}
	
	// HTTP handler
    static class AmazonSNSHandler extends AbstractHandler {

    	@Override
        public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

            // Scan request into a string
            Scanner scanner = new Scanner(request.getInputStream());
            StringBuilder sb = new StringBuilder();
            while (scanner.hasNextLine()) {
                sb.append(scanner.nextLine());
            }
            
            // Build a message map from the JSON encoded message
            InputStream bytes = new ByteArrayInputStream(sb.toString().getBytes());
            Map<String, String> messageMap = new ObjectMapper().readValue(bytes, Map.class);

            // Enqueue message map for receive loop
            messageQueue.add(messageMap);

            // Set HTTP response
            response.setContentType("text/html");
            response.setStatus(HttpServletResponse.SC_OK);
            ((Request) request).setHandled(true);
        }
        
    }

}
