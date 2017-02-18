package com.fiap.search.service.domain;

public class Ticket {

	private String ticket;
	private String query;
	
	public Ticket(String id, String query) {
		this.ticket = id;	
		this.query = query;
	}
	
	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}
		
}
