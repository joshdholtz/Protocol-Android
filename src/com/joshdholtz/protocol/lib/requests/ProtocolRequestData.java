package com.joshdholtz.protocol.lib.requests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.message.BasicNameValuePair;

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
	
	public boolean containsHeader(String key) {
		if (headers == null) {
			return false;
		}
		return headers.containsKey(key);
	}
	
	protected List<BasicNameValuePair> paramsToValuePairs(Map<String, String> params) {
		List<BasicNameValuePair> nameValuePair = new ArrayList<BasicNameValuePair>();

		List<String> keys = new ArrayList<String>(params.keySet());
		for (int i = 0; i < keys.size(); ++i) {
			nameValuePair.add(new BasicNameValuePair(keys.get(i), params.get(keys.get(i)).toString()));
		}
		
		return nameValuePair;
	}
	
}
