package com.fiap.ticket.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fiap.ticket.service.domain.TicketResult;
import com.fiap.ticket.service.repository.TicketRepository;

@RestController("/tickets")
public class TicketController {

	@Autowired
	private TicketRepository repository;
	
	@ResponseBody
	@CrossOrigin("*")
	@GetMapping("/{id}")
	public ResponseEntity<TicketResult> get(@PathVariable String id) {
		TicketResult result = repository.findOne(id);
		HttpStatus status = result == null ? 
				HttpStatus.NOT_FOUND : HttpStatus.OK;
		
		return new ResponseEntity<TicketResult>(result, status);
	}
	
}
