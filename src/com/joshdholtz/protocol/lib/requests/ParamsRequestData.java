package com.joshdholtz.protocol.lib.requests;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

public class ParamsRequestData extends ProtocolRequestData {

	private Map<String, String> params;
	private String contentType;
	
	public ParamsRequestData(Map<String, String> params) {
		this.params = params;
		contentType = "application/x-www-form-urlencoded";
	}
	
	@Override
	public HttpEntity getEntity() {
		HttpEntity entity = null;
		try {
			entity = new UrlEncodedFormEntity(this.paramsToValuePairs(params), HTTP.UTF_8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return entity;
	}
	
	@Override
	public String getContentType() {
		return contentType;
	}
	
	public Map<String, String> getParams() {
		return params;
	}
	
	private List<BasicNameValuePair> paramsToValuePairs(Map<String, String> params) {
		List<BasicNameValuePair> nameValuePair = new ArrayList<BasicNameValuePair>();

		List<String> keys = new ArrayList<String>(params.keySet());
		for (int i = 0; i < keys.size(); ++i) {
			nameValuePair.add(new BasicNameValuePair(keys.get(i), params.get(keys.get(i)).toString()));
		}
		
		return nameValuePair;
	}

}
