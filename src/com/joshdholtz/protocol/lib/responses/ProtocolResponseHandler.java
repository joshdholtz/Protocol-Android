package com.joshdholtz.protocol.lib.responses;

import org.apache.http.HttpResponse;

public abstract class ProtocolResponseHandler {
	
	private HttpResponse response;
	private int status;
	private byte[] data;
	
	/**
	 * Sets the response, status, and data and executes the handleResponse method for
	 * this class and any child classes. 
	 * 
	 * @param response
	 * @param status
	 * @param data
	 */
	public void generateResponse(HttpResponse response, int status, byte[] data) {
		this.response = response;
		this.status = status;
		this.data = data;
		
		handleResponse(response, status, data);
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

	public abstract void handleResponse(HttpResponse response, int status, byte[] data);
	
}
