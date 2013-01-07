package com.joshdholtz.protocol.lib.requests;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.util.Log;

import com.joshdholtz.protocol.lib.helpers.ProtocolConstants;

public class ParamsRequestData extends ProtocolRequestData {

	private Map<String, Object> params;
	private String contentType;
	
	public ParamsRequestData() {
		this.params = new HashMap<String, Object>();
		contentType = "application/x-www-form-urlencoded";
	}
	
	public ParamsRequestData(Map<String, Object> params) {
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
	
	public void addParam(String key, String value) {
		if (params != null) {
			params.put(key, value);
		}
	}
	
	public void removeParam(String key) {
		if (params != null) {
			params.remove(key);
		}
	}
	
	public Map<String, Object> getParams() {
		return params;
	}

	@Override
	public void log() {
		Log.d(ProtocolConstants.LOG_TAG, "\tPARAMS:");
		if (params != null) {
			for (String param : params.keySet()) {
				Log.d(ProtocolConstants.LOG_TAG, "\t\t" + param + " - " + params.get(param));	
			}
		}
	}

}
