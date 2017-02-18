package com.fiap.ticket.service.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.fiap.ticket.service.domain.TicketResult;

public interface TicketRepository extends PagingAndSortingRepository<TicketResult, String>{
	
}
