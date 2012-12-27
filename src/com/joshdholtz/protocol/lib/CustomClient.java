package com.joshdholtz.protocol.lib;

import com.joshdholtz.protocol.lib.requests.JSONRequestData;
import com.joshdholtz.protocol.lib.requests.ParamsRequestData;
import com.joshdholtz.protocol.lib.responses.JSONResponse;


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
	
	public static ProtocolTask get(String route, ParamsRequestData requestData, final ProtocolResponseHandler<JSONResponse> responseHandler) {
		return CustomClient.getInstance().doGet(route, requestData, JSONResponse.class, responseHandler);
	}
	
	public static ProtocolTask post(String route, JSONRequestData requestData, final ProtocolResponseHandler<JSONResponse> responseHandler) {
		return CustomClient.getInstance().doPost(route, requestData, JSONResponse.class, responseHandler);
	}
	
	public static ProtocolTask put(String route, JSONRequestData requestData, final ProtocolResponseHandler<JSONResponse> responseHandler) {
		return CustomClient.getInstance().doPut(route, requestData, JSONResponse.class, responseHandler);
	}
	
	public static ProtocolTask delete(String route, ParamsRequestData requestData, final ProtocolResponseHandler<JSONResponse> responseHandler) {
		return CustomClient.getInstance().doDelete(route, requestData, JSONResponse.class, responseHandler);
	}
	
}
