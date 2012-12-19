package com.joshdholtz.protocol.lib.requests;

import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

public class JSONRequestData extends ProtocolRequestData {

	private String contentType;
	private JSONObject json;
	
	public JSONRequestData(JSONObject json) {
		this.json = json;
		contentType = "application/json";
	}
	
	@Override
	public HttpEntity getEntity() {
		HttpEntity entity = null;
		try {
			entity = new StringEntity(json.toString());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return entity;
	}

	@Override
	public String getContentType() {
		return contentType;
	}

}
