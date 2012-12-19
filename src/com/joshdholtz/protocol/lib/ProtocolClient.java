package com.joshdholtz.protocol.lib;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;

import com.joshdholtz.protocol.lib.helpers.ProtocolConnectBitmapTask;
import com.joshdholtz.protocol.lib.helpers.ProtocolConnectTask;
import com.joshdholtz.protocol.lib.helpers.ProtocolConstants;
import com.joshdholtz.protocol.lib.helpers.ProtocolConnectTask.GotResponse;
import com.joshdholtz.protocol.lib.helpers.ProtocolConstants.HttpMethod;
import com.joshdholtz.protocol.lib.requests.ParamsRequestData;
import com.joshdholtz.protocol.lib.requests.ProtocolRequestData;

public class ProtocolClient {
	
	public final static String CONTENT_TYPE_FORM_DATA = "application/x-www-form-urlencoded";
	public final static String CONTENT_TYPE_JSON = "application/json";
	
	public final static String BROADCAST_DATA_STATUS = "status";
	public final static String BROADCAST_DATA_RESPONSE = "response";
	public final static String BROADCAST_DATA_HEADERS = "headers";

	private String baseUrl;
	private Map<String, BasicNameValuePair> headers;
	
	private int timeout;
	
	private int maxAsyncCount;
	
	private int runningCount;
	private LinkedList queue;
	
	private boolean debug;
	
	private SparseArray<ProtocolStatusListener> observedStatuses;
	
	public ProtocolClient() {
		baseUrl = null;
		headers = new HashMap<String, BasicNameValuePair>();
		
		timeout = 30000;
		
		maxAsyncCount = 15;
		
		runningCount = 0;
		queue = new LinkedList();
		
		debug = false;
		
		observedStatuses = new SparseArray<ProtocolStatusListener>();
	}
	
//	public static Protocol getInstance() {
//		return LazyHolder.instance;
//	}
//	
//	private static class LazyHolder {
//		private static Protocol instance = new Protocol();
//	}
	
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
	 * @return the timeout
	 */
	public int getTimeout() {
		return timeout;
	}

	/**
	 * @param timeout the timeout to set
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	/**
	 * @return the maxAsyncCount
	 */
	public int getMaxAsyncCount() {
		return maxAsyncCount;
	}

	/**
	 * @param maxAsyncCount the maxAsyncCount to set
	 */
	public void setMaxAsyncCount(int maxAsyncCount) {
		this.maxAsyncCount = maxAsyncCount;
	}
	
	public void clearQueue() {
		this.queue.clear();
	}

//	/**
//	 * @return the bitmapCache
//	 */
//	public ProtocolBitmapCache getBitmapCache() {
//		return bitmapCache;
//	}
//
//	/**
//	 * @param bitmapCache the bitmapCache to set
//	 */
//	public void setBitmapCache(ProtocolBitmapCache bitmapCache) {
//		this.bitmapCache = bitmapCache;
//	}

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
	
	public void observeStatus(int status, ProtocolStatusListener listener) {
		this.observedStatuses.put(status, listener);
	}
	
	public void removeObserveStatus(int status) {
		this.observedStatuses.remove(status);
	}
	
	/**
	 * Performs a GET request with params.
	 * 
	 * If no base url is set, the route passed in will be the full route used.
	 * 
	 * @param route
	 * @param params
	 * @param contentType
	 * @param responseHandler
	 */
	public void doGet(String route, ParamsRequestData requestData, final ProtocolResponse responseHandler) {
		route = this.formatRoute(route);
		if (requestData != null) {
			route = route + this.paramsToString(requestData.getParams());
		}
		
		ProtocolConnectTask task = new ProtocolConnectTask(HttpMethod.HTTP_GET, route, requestData, timeout, new ProtocolGotResponse(responseHandler), null, null);
		this.executeProtocolConnectTask(task);
	}
	
	/**
	 * Performs a GET request with no params.
	 * 
	 * If no base url is set, the route passed in will be the full route used.
	 * 
	 * @param route
	 * @param responseHandler
	 */
	public void doGetBitmap(String route, String imageViewTag, final ProtocolBitmapResponse responseHandler) {
		route = this.formatRoute(route);
		
		ProtocolConnectBitmapTask task = new ProtocolConnectBitmapTask(route, imageViewTag, timeout, responseHandler);
		this.executeProtocolConnectTask(task);
	}
		
	/**
	 * Performs a POST request with params.
	 * 
	 * If no base url is set, the route passed in will be the full route used.
	 * 
	 * @param route
	 * @param params
	 * @param contentType
	 * @param responseHandler
	 */
	public void doPost(String route, ProtocolRequestData requestData, final ProtocolResponse responseHandler) {
		route = this.formatRoute(route);
		
		ProtocolConnectTask task = new ProtocolConnectTask(HttpMethod.HTTP_POST, route, requestData, timeout, new ProtocolGotResponse(responseHandler), null, null);
		this.executeProtocolConnectTask(task);
	}
		
	/**
	 * Performs a PUT request with params.
	 * 
	 * If no base url is set, the route passed in will be the full route used.
	 * 
	 * @param route
	 * @param params
	 * @param contentType
	 * @param responseHandler
	 */
	public void doPut(String route, ProtocolRequestData requestData, final ProtocolResponse responseHandler) {
		route = this.formatRoute(route);
		
		ProtocolConnectTask task = new ProtocolConnectTask(HttpMethod.HTTP_PUT, route, requestData, timeout, new ProtocolGotResponse(responseHandler), null, null);
		this.executeProtocolConnectTask(task);
	}
	
	/**
	 * Performs a DELETE request with params.
	 * 
	 * If no base url is set, the route passed in will be the full route used.
	 * 
	 * @param route
	 * @param params
	 * @param contentType
	 * @param responseHandler
	 */
	public void doDelete(String route, ParamsRequestData requestData, final ProtocolResponse responseHandler) {
		route = this.formatRoute(route);
		route = route + this.paramsToString(requestData.getParams());
		
		ProtocolConnectTask task = new ProtocolConnectTask(HttpMethod.HTTP_DELETE, route, requestData, timeout, new ProtocolGotResponse(responseHandler), null, null);
		this.executeProtocolConnectTask(task);
	}
	
	private class ProtocolGotResponse extends GotResponse {
		
		private ProtocolResponse responseHandler;
		
		public ProtocolGotResponse(ProtocolResponse responseHandler) {
			this.responseHandler = responseHandler;
		}
		
		@Override
		public void handleResponse(HttpResponse response, int status, String data) {
			if (debug) {
				Log.d(ProtocolConstants.LOG_TAG, "POST - " + status + ", " + data);
			}
			
			boolean executeHandler = true;
			ProtocolStatusListener statusListener = observedStatuses.get(status);
			if (statusListener != null) {
				executeHandler = statusListener.observedStatus(status);
			}
			
			if (executeHandler) {
				responseHandler.handleResponse(response, status, data);
			}
		}
		
		@Override
		public void handleResponse(HttpResponse response, int status, InputStream in) {
			
		}
		
	}
	
	private void executeProtocolConnectTask(AsyncTask task) {
		synchronized (this) {
			
			if (runningCount >= maxAsyncCount ) {
				queue.add(task);
				
				if (debug) {
					Log.d(ProtocolConstants.LOG_TAG, "Queueing task");
					Log.d(ProtocolConstants.LOG_TAG, "Running count - " + runningCount + ", Queue count - " + queue.size());
				}
			} else {
				runningCount++;
				task.execute(null);
				
				if (debug) {
					Log.d(ProtocolConstants.LOG_TAG, "Running count - " + runningCount + ", Queue count - " + queue.size());
				}
			}
		}
	}
	
	public void finishedProtocolConnectTask() {
		synchronized (this) {
			runningCount--;
			if (debug) {
				Log.d(ProtocolConstants.LOG_TAG, "Running count - " + runningCount + ", Queue count - " + queue.size());
			}
			
			if (!queue.isEmpty()) {
				if (debug) {
					Log.d(ProtocolConstants.LOG_TAG, "Popping task");
				}
				AsyncTask task = (AsyncTask) queue.removeFirst();
				
				runningCount++;
				task.execute(null);
				
				if (debug) {
					Log.d(ProtocolConstants.LOG_TAG, "Running count - " + runningCount + ", Queue count - " + queue.size());
				}
			}
		}
	}
	
	private String formatRoute(String route) {
		if (!route.startsWith("http://") && !route.startsWith("https://" ) ) {
			if (this.getBaseUrl() != null) {
				route = this.getBaseUrl() + route;
			}
		}
		
		return route;
	}
	
	private String paramsToString(Map<String, String> params) {
		String paramsStr = "";
		if (params != null && params.size() > 0) {
			paramsStr += "?";
			try {
				List<String> keys = new ArrayList<String>(params.keySet());
				for (int i = 0; i < keys.size(); ++i) {
					if (i != 0) {
						paramsStr += "&";
					}
					paramsStr += URLEncoder.encode(keys.get(i), "UTF-8") + "=" + URLEncoder.encode(params.get(keys.get(i)).toString(), "UTF-8");
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		
		return paramsStr;
	}
	
	private List<BasicNameValuePair> paramsToValuePairs(Map<String, Object> params) {
		List<BasicNameValuePair> nameValuePair = new ArrayList<BasicNameValuePair>();

		List<String> keys = new ArrayList<String>(params.keySet());
		for (int i = 0; i < keys.size(); ++i) {
			nameValuePair.add(new BasicNameValuePair(keys.get(i), params.get(keys.get(i)).toString()));
		}
		
		return nameValuePair;
	}
	
	public interface ProtocolStatusListener {
		public boolean observedStatus(int status);
	}
	
}
