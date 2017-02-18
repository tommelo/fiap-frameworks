package com.fiap.search.service.setup;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;

@Configuration
public class ServiceConfiguration {

	@Bean
	public AmazonSNS sns() {
		return AmazonSNSClientBuilder
				.defaultClient();
	}
	
}
