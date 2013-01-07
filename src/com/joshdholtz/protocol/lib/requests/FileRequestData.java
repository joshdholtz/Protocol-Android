package com.joshdholtz.protocol.lib.requests;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

import com.joshdholtz.protocol.lib.ProtocolMultipartEntity;
import com.joshdholtz.protocol.lib.helpers.ProtocolConstants;

public class FileRequestData extends ProtocolRequestData {

	private String boundary;
	private Map<String, File> files;
	private Map<String, Object> params;
	private String contentType;
	
	public FileRequestData(Map<String, Object> params, Map<String, File> files) {
		this.params = params;
		this.files = files;
		boundary = "---------------------------14737809831466499882746641449";
		contentType = "multipart/form-data; boundary=" + boundary;
	}
	
	@Override
	public HttpEntity getEntity() {
		ProtocolMultipartEntity entity = new ProtocolMultipartEntity(boundary, paramsToValuePairs(params), files);
		return entity;
	}
	
	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public void log() {
		Log.d(ProtocolConstants.LOG_TAG, "\tPARAMS:");
		if (params != null) {
			for (String param : params.keySet()) {
				Log.d(ProtocolConstants.LOG_TAG, "\t\t" + param + " - " + params.get(param));	
			}
		}
		Log.d(ProtocolConstants.LOG_TAG, "\tFILES:");
		if (files != null) {
			for (String file : files.keySet()) {
				Log.d(ProtocolConstants.LOG_TAG, "\t\t" + file + " - " + files.get(file).getAbsolutePath());	
			}
		}
	}

}
