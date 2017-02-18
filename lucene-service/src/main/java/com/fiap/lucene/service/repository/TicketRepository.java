package com.fiap.lucene.service.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.fiap.lucene.service.domain.TicketResult;

public interface TicketRepository extends PagingAndSortingRepository<TicketResult, String>{
	
}
