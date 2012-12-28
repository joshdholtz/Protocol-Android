package com.joshdholtz.protocol.lib.responses;

import org.apache.http.HttpResponse;

public abstract class StringResponseHandler extends ProtocolResponseHandler {
	
	@Override
	public void handleResponse(HttpResponse response, int status, byte[] data) {
		handleResponse(new String(data));
	}
	
	public abstract void handleResponse(String stringResponse);

}
