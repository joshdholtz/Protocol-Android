package com.joshdholtz.protocol.lib;

import android.widget.Toast;

import com.joshdholtz.protocol.lib.ProtocolClient.ProtocolStatusListener;
import com.joshdholtz.protocol.lib.requests.JSONRequestData;
import com.joshdholtz.protocol.lib.requests.ParamsRequestData;
import com.joshdholtz.protocol.lib.responses.JSONResponseHandler;
import com.joshdholtz.protocol.lib.responses.ProtocolResponseHandler;


public class CustomClient extends ProtocolClient {

	private CustomClient() {
		super();
		this.setBaseUrl("http://www.statuscodewhat.com");
		
	}
	
	public static CustomClient getInstance() {
		return LazyHolder.instance;
	}

	private static class LazyHolder {
		private static CustomClient instance = new CustomClient();
	}
	
	public static ProtocolTask get(String route, ParamsRequestData requestData, JSONResponseHandler responseHandler) {
		return CustomClient.getInstance().doGet(route, requestData, responseHandler);
	}
	
	public static ProtocolTask post(String route, JSONRequestData requestData, JSONResponseHandler responseHandler) {
		return CustomClient.getInstance().doPost(route, requestData, responseHandler);
	}
	
	public static ProtocolTask put(String route, JSONRequestData requestData, JSONResponseHandler responseHandler) {
		return CustomClient.getInstance().doPut(route, requestData, responseHandler);
	}
	
	public static ProtocolTask delete(String route, ParamsRequestData requestData, JSONResponseHandler responseHandler) {
		return CustomClient.getInstance().doDelete(route, requestData, responseHandler);
	}
	
}
