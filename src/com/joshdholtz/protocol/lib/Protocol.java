package com.joshdholtz.protocol.lib;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.joshdholtz.protocol.lib.helpers.ProtocolConnectTask;
import com.joshdholtz.protocol.lib.helpers.ProtocolConstants;
import com.joshdholtz.protocol.lib.helpers.ProtocolConnectTask.GotResponse;
import com.joshdholtz.protocol.lib.helpers.ProtocolConstants.HttpMethod;

public class Protocol {

	private String baseUrl;
	private Map<String, BasicNameValuePair> headers;
	
	private boolean debug;
	
	private Protocol() {
		baseUrl = null;
		headers = new HashMap<String, BasicNameValuePair>();
		
		debug = false;
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
	 * @return the debug
	 */
	public boolean isDebug() {
		return debug;
	}

	/**
	 * @param debug the debug to set
	 */
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	/**
	 * Checks if network is available.
	 * @return boolean
	 */
	public boolean isNetworkAvailable(Context context) {
	    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null;
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
		route = route + this.paramsToString(params);
		
		ProtocolConnectTask task = new ProtocolConnectTask(HttpMethod.HTTP_GET, route, null, new GotResponse() {

			@Override
			public void handleResponse(HttpResponse response, int status, String data) {
				if (debug) {
					Log.d(ProtocolConstants.LOG_TAG, "GET - " + status + ", " + data);
				}
				
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
		
		HttpEntity entity = null;
		try {
			entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		ProtocolConnectTask task = new ProtocolConnectTask(HttpMethod.HTTP_POST, route, entity, new GotResponse() {

			@Override
			public void handleResponse(HttpResponse response, int status, String data) {
				if (debug) {
					Log.d(ProtocolConstants.LOG_TAG, "POST - " + status + ", " + data);
				}
				
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
		
		HttpEntity entity = null;
		try {
			entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		ProtocolConnectTask task = new ProtocolConnectTask(HttpMethod.HTTP_PUT, route, entity, new GotResponse() {

			@Override
			public void handleResponse(HttpResponse response, int status, String data) {
				if (debug) {
					Log.d(ProtocolConstants.LOG_TAG, "PUT - " + status + ", " + data);
				}
				
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
		
		route = route + this.paramsToString(params);
		
		ProtocolConnectTask task = new ProtocolConnectTask(HttpMethod.HTTP_DELETE, route, null, new GotResponse() {

			@Override
			public void handleResponse(HttpResponse response, int status, String data) {
				if (debug) {
					Log.d(ProtocolConstants.LOG_TAG, "DELETE - " + status + ", " + data);
				}
				
				responseHandler.handleResponse(response, status, data);
			}
			
		});
		task.execute();
	}
	
	public void doPostWithFile(String route, final File file, final ProtocolResponse responseHandler) {
		if (this.getBaseUrl() != null) {
			route = this.getBaseUrl() + route;
		}
		
		final String boundary = "---------------------------14737809831466499882746641449";
		String contentType = "multipart/form-data; boundary=" + boundary;
		
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("param1", "value1"));
		params.add(new BasicNameValuePair("param2", "value2"));
		
		List<File> files = new ArrayList<File>();
		files.add(file);
		files.add(file);
		
		HttpEntity entity = new ProtocolMultipartEntity(boundary, params, files);
		ProtocolConnectTask task = new ProtocolConnectTask(HttpMethod.HTTP_POST_FILE, route, contentType, entity, new GotResponse() {

			@Override
			public void handleResponse(HttpResponse response, int status, String data) {
				if (debug) {
					Log.d(ProtocolConstants.LOG_TAG, "POST FILE - " + status + ", " + data);
				}
				
				responseHandler.handleResponse(response, status, data);
			}
			
		});
		task.execute();
	}
	
	private String paramsToString(List<BasicNameValuePair> params) {
		String paramsStr = "?";
		try {
			for (int i = 0; i < params.size(); ++i) {
				if (i != 0) {
					paramsStr += "&";
				}
				paramsStr += URLEncoder.encode(params.get(i).getName(), "UTF-8") + "=" + URLEncoder.encode(params.get(i).getValue(), "UTF-8");
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return paramsStr;
	}
	
}
