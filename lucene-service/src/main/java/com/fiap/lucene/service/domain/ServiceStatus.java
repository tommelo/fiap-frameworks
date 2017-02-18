package com.fiap.lucene.service.domain;

public class ServiceStatus {
	
	public static final String PROCESSING = "PROCESSING";
	
	private String status;
	
	public ServiceStatus(String status) {
		this.status = status;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}
