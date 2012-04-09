package com.joshdholtz.protocol.lib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

import com.joshdholtz.protocol.lib.helpers.ProtocolConnectTask;
import com.joshdholtz.protocol.lib.helpers.ProtocolConstants;
import com.joshdholtz.protocol.lib.helpers.ProtocolConnectTask.GotResponse;
import com.joshdholtz.protocol.lib.helpers.ProtocolConstants.HttpMethod;

public class Protocol {

	private String baseUrl;
	private Map<String, BasicNameValuePair> headers;
	
	private Protocol() {
		baseUrl = null;
		headers = new HashMap<String, BasicNameValuePair>();
	}
	
	public static Protocol getInstance() {
		return LazyHolder.instance;
	}
	
	private static class LazyHolder {
		private static Protocol instance = new Protocol();
	}
	
	/**
	 * Sets the base url.
	 * @param baseUrl
	 */
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	
	/**
	 * Gets the base url.
	 * @return
	 */
	public String getBaseUrl() {
		return this.baseUrl;
	}
	
	/**
	 * Adds a header to append to every request.
	 * @param key
	 * @param value
	 */
	public void addHeader(String key, String value) {
		headers.put(key, new BasicNameValuePair(key, value));
	}
	
	/**
	 * Removes a header that would be appended to every request.
	 * @param key
	 */
	public void removeHeader(String key) {
		headers.remove(key);
	}
	
	/**
	 * Gets all the headers.
	 * @return List<BasicNameValuePair>
	 */
	public List<BasicNameValuePair> getHeaders() {
		return new ArrayList<BasicNameValuePair>(headers.values());
	}
	
	/**
	 * Performs a GET request with no params.
	 * 
	 * If no base url is set, the route passed in will be the full route used.
	 * 
	 * @param route
	 * @param responseHandler
	 */
	public void doGet(String route, final ProtocolResponse responseHandler) {
		this.doGet(route, new ArrayList<BasicNameValuePair>(), responseHandler);
	}
	
	/**
	 * Performs a GET request with params.
	 * 
	 * If no base url is set, the route passed in will be the full route used.
	 * 
	 * @param route
	 * @param params
	 * @param responseHandler
	 */
	public void doGet(String route, List<BasicNameValuePair> params, final ProtocolResponse responseHandler) {
		if (this.getBaseUrl() != null) {
			route = this.getBaseUrl() + route;
		}
		
		ProtocolConnectTask task = new ProtocolConnectTask(HttpMethod.HTTP_GET, route, params, new GotResponse() {

			@Override
			public void handleResponse(HttpResponse response, int status, String data) {
				responseHandler.handleResponse(response, status, data);
			}
			
		});
		task.execute();
	}
	
	/**
	 * Performs a POST request with no params.
	 * 
	 * If no base url is set, the route passed in will be the full route used.
	 * 
	 * @param route
	 * @param responseHandler
	 */
	public void doPost(String route, final ProtocolResponse responseHandler) {
		this.doPost(route, new ArrayList<BasicNameValuePair>(), responseHandler);
	}
	
	/**
	 * Performs a POST request with params.
	 * 
	 * If no base url is set, the route passed in will be the full route used.
	 * 
	 * @param route
	 * @param params
	 * @param responseHandler
	 */
	public void doPost(String route, List<BasicNameValuePair> params, final ProtocolResponse responseHandler) {
		if (this.getBaseUrl() != null) {
			route = this.getBaseUrl() + route;
		}
		
		ProtocolConnectTask task = new ProtocolConnectTask(HttpMethod.HTTP_POST, route, params, new GotResponse() {

			@Override
			public void handleResponse(HttpResponse response, int status, String data) {
				responseHandler.handleResponse(response, status, data);
			}
			
		});
		task.execute();
	}
	
	/**
	 * Performs a PUT request with no params.
	 * 
	 * If no base url is set, the route passed in will be the full route used.
	 * 
	 * @param route
	 * @param responseHandler
	 */
	public void doPut(String route, final ProtocolResponse responseHandler) {
		this.doPut(route, new ArrayList<BasicNameValuePair>(), responseHandler);
	}
	
	/**
	 * Performs a PUT request with params.
	 * 
	 * If no base url is set, the route passed in will be the full route used.
	 * 
	 * @param route
	 * @param params
	 * @param responseHandler
	 */
	public void doPut(String route, List<BasicNameValuePair> params, final ProtocolResponse responseHandler) {
		if (this.getBaseUrl() != null) {
			route = this.getBaseUrl() + route;
		}
		
		ProtocolConnectTask task = new ProtocolConnectTask(HttpMethod.HTTP_PUT, route, params, new GotResponse() {

			@Override
			public void handleResponse(HttpResponse response, int status, String data) {
				responseHandler.handleResponse(response, status, data);
			}
			
		});
		task.execute();
	}
	
	/**
	 * Performs a DELETE request with no params.
	 * 
	 * If no base url is set, the route passed in will be the full route used.
	 * 
	 * @param route
	 * @param responseHandler
	 */
	public void doDelete(String route, final ProtocolResponse responseHandler) {
		this.doDelete(route, new ArrayList<BasicNameValuePair>(), responseHandler);
	}
	
	/**
	 * Performs a DELETE request with params.
	 * 
	 * If no base url is set, the route passed in will be the full route used.
	 * 
	 * @param route
	 * @param params
	 * @param responseHandler
	 */
	public void doDelete(String route, List<BasicNameValuePair> params, final ProtocolResponse responseHandler) {
		if (this.getBaseUrl() != null) {
			route = this.getBaseUrl() + route;
		}
		
		ProtocolConnectTask task = new ProtocolConnectTask(HttpMethod.HTTP_DELETE, route, params, new GotResponse() {

			@Override
			public void handleResponse(HttpResponse response, int status, String data) {
				responseHandler.handleResponse(response, status, data);
			}
			
		});
		task.execute();
	}
	
}
