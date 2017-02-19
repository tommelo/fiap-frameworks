package com.fiap.lucene.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableRedisRepositories
@EnableAsync
public class LuceneServiceApplication {
 
	public static void main(String[] args) throws Exception {
		SpringApplication.run(LuceneServiceApplication.class, args);
	}
	
}
