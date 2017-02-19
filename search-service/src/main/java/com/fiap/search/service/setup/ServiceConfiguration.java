package com.fiap.search.service.setup;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;

@Configuration
public class ServiceConfiguration {

//	@Bean
//	public AmazonSNS sns() {
//		return AmazonSNSClientBuilder
//				.standard()
//				.withCredentials(new InstanceProfileCredentialsProvider(true))
//				.build();
//	}
	
}
