package com.fiap.lucene.service.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fiap.lucene.service.domain.ServiceStatus;
import com.fiap.lucene.service.processor.LuceneProcessor;

@RestController("/lucene")
public class LuceneController {

	@Autowired
	private LuceneProcessor processor;
	
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
	
}
