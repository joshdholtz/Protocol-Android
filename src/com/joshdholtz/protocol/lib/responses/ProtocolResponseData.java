package com.joshdholtz.protocol.lib.responses;

import org.apache.http.HttpResponse;

public abstract class ProtocolResponseData {

	public HttpResponse response;
	public int status;
	public byte[] data;
	
	public ProtocolResponseData() {
		
	}
	
	public abstract void generateResponse();
	
	public void generate(HttpResponse response, int status, byte[] data) {
		this.response = response;
		this.status = status;
		this.data = data;
		
		this.generateResponse();
	}

	/**
	 * @return the response
	 */
	public HttpResponse getResponse() {
		return response;
	}

	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @return the data
	 */
	public byte[] getData() {
		return data;
	}
	
}
