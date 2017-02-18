package com.fiap.lucene.service.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fiap.lucene.service.domain.ServiceStatus;
import com.fiap.lucene.service.domain.Ticket;
import com.fiap.lucene.service.processor.LuceneProcessor;

@RestController("/lucene")
public class LuceneController {

	@Autowired
	private LuceneProcessor processor;
	
	@PostMapping
	public ServiceStatus process(@RequestBody Ticket ticket) throws IOException {
		processor.process(ticket);
		return new ServiceStatus(ServiceStatus.PROCESSING);
	}
	
}
