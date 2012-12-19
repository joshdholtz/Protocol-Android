package com.joshdholtz.protocol.lib.requests;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;

public abstract class ProtocolRequestData {
	
	private Map<String, String> headers;
	
	public abstract String getContentType();
	public abstract HttpEntity getEntity();
	
	public Map<String, String> getHeaders() {
		return headers;
	}
	
	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}
	
	public void addHeader(String key, String value) {
		if (headers == null) {
			headers = new HashMap<String, String>();
		}
		headers.put(key, value);
	}
	
}
