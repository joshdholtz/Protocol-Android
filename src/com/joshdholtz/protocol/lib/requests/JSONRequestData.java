package com.joshdholtz.protocol.lib.requests;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import android.util.Log;

import com.joshdholtz.protocol.lib.helpers.ProtocolConstants;

public class JSONRequestData extends ProtocolRequestData {

	private String contentType;
	private JSONObject json;
	
	public JSONRequestData(Map<String, Object> map) {
		this.json = new JSONObject(map);
		contentType = "application/json";
	}
	
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

	@Override
	public void log() {
		Log.d(ProtocolConstants.LOG_TAG, "\tJSON:");
		if (json != null) {
			Log.d(ProtocolConstants.LOG_TAG, "\t\t" + json.toString());	
		}
	}

}
