package com.joshdholtz.protocol.lib.responses;

import org.apache.http.HttpResponse;

public abstract class ProtocolResponseHandler {
	
	public void generateResponse(HttpResponse response, int status, byte[] data) {
		
		handleResponse(response, status, data);
	}
	
	public abstract void handleResponse(HttpResponse response, int status, byte[] data);
	
}
