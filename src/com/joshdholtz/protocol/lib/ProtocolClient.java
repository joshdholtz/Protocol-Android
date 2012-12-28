package com.joshdholtz.protocol.lib;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;

import com.joshdholtz.protocol.lib.helpers.ProtocolConstants;
import com.joshdholtz.protocol.lib.helpers.ProtocolConstants.HttpMethod;
import com.joshdholtz.protocol.lib.requests.ParamsRequestData;
import com.joshdholtz.protocol.lib.requests.ProtocolRequestData;
import com.joshdholtz.protocol.lib.responses.ProtocolResponseData;

public class ProtocolClient {
	
	public final static String BROADCAST_DATA_STATUS = "status";
	public final static String BROADCAST_DATA_RESPONSE = "response";
	public final static String BROADCAST_DATA_HEADERS = "headers";

	private String baseUrl;
	private Map<String, String> headers;
	
	private int timeout;
	
	private int maxAsyncCount;
	
	private int runningCount;
	private LinkedList queue;
	
	private boolean debug;
	
	private SparseArray<ProtocolStatusListener> observedStatuses;
	
	public ProtocolClient() {
		baseUrl = null;
		headers = new HashMap<String, String>();
		
		timeout = 30000;
		
		maxAsyncCount = 15;
		
		runningCount = 0;
		queue = new LinkedList();
		
		debug = false;
		
		observedStatuses = new SparseArray<ProtocolStatusListener>();
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
		headers.put(key, value);
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
	 * @return Map<String, String>
	 */
	public Map<String, String> getHeaders() {
		return headers;
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
	
	/**
	 * Clears request queue
	 */
	public void clearQueue() {
		this.queue.clear();
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
	 * Add a status listener
	 * @param status
	 * @param listener
	 */
	public void observeStatus(int status, ProtocolStatusListener listener) {
		this.observedStatuses.put(status, listener);
	}
	
	/**
	 * Remove a status listener
	 * @param status
	 */
	public void removeObserveStatus(int status) {
		this.observedStatuses.remove(status);
	}
	
	/**
	 * Performs a GET request.
	 * 
	 * If no base url is set, the route passed in will be the full route used.
	 * 
	 * 
	 * @param route
	 * @param requestData
	 * @param clazz
	 * @param responseHandler
	 */
	public ProtocolTask doGet(String route, ParamsRequestData requestData, Class<? extends ProtocolResponseData> clazz, final ProtocolResponseHandler<? extends ProtocolResponseData> responseHandler) {
		route = this.formatRoute(route);
		if (requestData != null) {
			route = route + this.paramsToString(requestData.getParams());
		}
		
		if (requestData == null) {
			requestData = new ParamsRequestData();
		}
		
		// Adds global headers
		addHeadersToRequest(requestData);
		
		ProtocolTask task = new ProtocolTask(HttpMethod.HTTP_GET, route, requestData, timeout, new ProtocolClientGotResponse(clazz, responseHandler));
		this.executeProtocolConnectTask(task);
		
		return task;
	}
	
	/**
	 * Performs a GET request with no params.
	 * 
	 * If no base url is set, the route passed in will be the full route used.
	 * 
	 * @param route
	 * @param responseHandler
	 */
//	public void doGetBitmap(String route, String imageViewTag, final ProtocolBitmapResponse responseHandler) {
//		route = this.formatRoute(route);
//		
//		ProtocolConnectBitmapTask task = new ProtocolConnectBitmapTask(route, imageViewTag, timeout, responseHandler);
//		this.executeProtocolConnectTask(task);
//	}
	
	/**
	 * Performs a POST request.
	 * 
	 * If no base url is set, the route passed in will be the full route used.
	 * 
	 * @param route
	 * @param requestData
	 * @param clazz
	 * @param responseHandler
	 */
	public ProtocolTask doPost(String route, ProtocolRequestData requestData, Class<? extends ProtocolResponseData> clazz, final ProtocolResponseHandler<? extends ProtocolResponseData> responseHandler) {
		route = this.formatRoute(route);
		
		// Adds global headers
		addHeadersToRequest(requestData);
		
		ProtocolTask task = new ProtocolTask(HttpMethod.HTTP_POST, route, requestData, timeout, new ProtocolClientGotResponse(clazz, responseHandler));
		this.executeProtocolConnectTask(task);
		
		return task;
	}
		
	/**
	 * Performs a PUT request.
	 * 
	 * If no base url is set, the route passed in will be the full route used.
	 * 
	 * 
	 * @param route
	 * @param requestData
	 * @param clazz
	 * @param responseHandler
	 */
	public ProtocolTask doPut(String route, ProtocolRequestData requestData, Class<? extends ProtocolResponseData> clazz, final ProtocolResponseHandler<? extends ProtocolResponseData> responseHandler) {
		route = this.formatRoute(route);
		
		// Adds global headers
		addHeadersToRequest(requestData);
		
		ProtocolTask task = new ProtocolTask(HttpMethod.HTTP_PUT, route, requestData, timeout, new ProtocolClientGotResponse(clazz, responseHandler));
		this.executeProtocolConnectTask(task);
		
		return task;
	}

	/**
	 * Performs a PUT request.
	 * 
	 * If no base url is set, the route passed in will be the full route used.
	 * 
	 * @param route
	 * @param requestData
	 * @param clazz
	 * @param responseHandler
	 */
	public ProtocolTask doDelete(String route, ParamsRequestData requestData, Class<? extends ProtocolResponseData> clazz, final ProtocolResponseHandler<? extends ProtocolResponseData> responseHandler) {
		route = this.formatRoute(route);
		route = route + this.paramsToString(requestData.getParams());
		
		if (requestData == null) {
			requestData = new ParamsRequestData();
		}
		
		// Adds global headers
		addHeadersToRequest(requestData);
		
		ProtocolTask task = new ProtocolTask(HttpMethod.HTTP_DELETE, route, requestData, timeout, new ProtocolClientGotResponse(clazz, responseHandler));
		this.executeProtocolConnectTask(task);
		
		return task;
	}
	
	private class ProtocolClientGotResponse extends ProtocolGotResponse {
		
		private Class<? extends ProtocolResponseData> clazz;
		private ProtocolResponseHandler responseHandler;
		
		public ProtocolClientGotResponse() {
			
		}
		
		public ProtocolClientGotResponse(Class<? extends ProtocolResponseData> clazz, ProtocolResponseHandler responseHandler) {
			this.responseHandler = responseHandler;
			this.clazz = clazz;
		}
		
		@Override
		public void handleResponse(HttpResponse response, int status, byte[] data) {
			finishedProtocolConnectTask();
			
			if (debug) {
				Log.d(ProtocolConstants.LOG_TAG, "POST - " + status + ", " + data);
			}
			
			boolean executeHandler = true;
			ProtocolStatusListener statusListener = observedStatuses.get(status);
			if (statusListener != null) {
				executeHandler = statusListener.observedStatus(status);
			}
			
			if (executeHandler) {
				ProtocolResponseData obj;
				try {
					obj = clazz.newInstance();
					obj.generate(response, status, data);
					responseHandler.handleResponse(obj);
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
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
	
	private void finishedProtocolConnectTask() {
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
	
	private void addHeadersToRequest(ProtocolRequestData requestData) {
		List<String> keys = new ArrayList<String>(headers.keySet());
		for (String key : keys) {
			if (!requestData.containsHeader(key)) {
				requestData.addHeader(key, headers.get(key));
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
	
	public static class ProtocolTask extends AsyncTask<Void, Void, HttpResponse> {

		private HttpMethod method;
		private String route;
		private Map<String, String> headers;
		private String contentType;
		private HttpEntity entity;
		private Timer timer;
		private int timeout;
		private ProtocolGotResponse handler;
		
		private HttpUriRequest httpUriRequest;
		
		private int status;
		private byte[] byteResp;
		
		private Handler threadHandler = new Handler();
		
		public ProtocolTask(HttpMethod method, String route, ProtocolRequestData requestData, ProtocolGotResponse handler) {
			this(method, route, requestData, 60000, handler);
		}
		
		public ProtocolTask(HttpMethod method, String route, ProtocolRequestData requestData, int timeout, ProtocolGotResponse handler) {
			if (requestData == null) {
				Log.d(ProtocolConstants.LOG_TAG, "REQUEST DATA IS NULL");
			}
			
			if (requestData == null) {
				requestData = new ParamsRequestData();
			}
			
			this.method = method;
			this.route = route;
			this.headers = requestData.getHeaders();
			this.contentType = requestData.getContentType();
			this.entity = requestData.getEntity();
			this.timeout = timeout;
			this.handler = handler;
			
			if (headers == null) {
				headers = new HashMap<String, String>();
			}
		}
		
		@Override
		protected void onPreExecute() {
			timer = new Timer();
			timer.schedule(new ConnectTimerTask(), timeout);
		}
		
		@Override
		protected HttpResponse doInBackground(Void... arg0) {
			try {
				
				// Creates the HTTP client
				AbstractHttpClient httpClient = new DefaultHttpClient();

				// Creates the request
				switch(method) {
					case HTTP_GET:
						httpUriRequest = new HttpGet(route);
						break;
					case HTTP_POST:
						HttpPost httpPostRequest = new HttpPost(route);
						httpPostRequest.setEntity(entity);
						httpUriRequest = httpPostRequest;
						break;
					case HTTP_PUT:
						HttpPut httpPutRequest = new HttpPut(route);
						httpPutRequest.setEntity(entity);
						httpUriRequest = httpPutRequest;
						break;
					case HTTP_DELETE:
						httpUriRequest = new HttpDelete(route);
						break;
				}
			
				Iterator<Entry<String,String>> it = headers.entrySet().iterator();
			    while (it.hasNext()) {
			        Entry<String,String> pairs = it.next();
			        httpUriRequest.setHeader(pairs.getKey(), pairs.getValue());
			        it.remove(); // avoids a ConcurrentModificationException
			    }
			    
			    if (contentType != null) {
					httpUriRequest.setHeader("Content-Type", contentType);
				}
				
				Log.d(ProtocolConstants.LOG_TAG, "Content-Type: " + contentType);
				
				Log.d(ProtocolConstants.LOG_TAG, method.toString() + " - " + route);
				HttpResponse httpResponse = httpClient.execute(httpUriRequest);
				
				status = httpResponse.getStatusLine().getStatusCode();
				StringBuffer out = new StringBuffer();
				
				// Gets the input stream and unpackages the response into a command
				if (httpResponse.getEntity() != null) {
					try {
						InputStream in = httpResponse.getEntity().getContent();
						byteResp = this.readBytes(in);
						
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				return httpResponse;
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		
			return null;
		}
		
		@Override
		protected void onCancelled() {
			if (httpUriRequest != null) {
				httpUriRequest.abort();
				
				String nullStr = null;
				if (handler != null) {
					handler.handleResponse(null, -1, new byte[0]);
					Log.d("", "ServerConnect - aborting request from cancel");
				}
				
			}
		}
		
		@Override
		protected void onPostExecute(HttpResponse httpResponse) {
			timer.cancel();
			
			if (this.isCancelled() || httpResponse == null) {
				if (handler != null) {
					handler.handleResponse(null, status, new byte[0]);
				}
			} else {
				if (handler != null) {
					handler.handleResponse(httpResponse, status, byteResp);
				}
			}
		}
		
		private byte[] readBytes(InputStream inputStream) throws IOException {
			  // this dynamically extends to take the bytes you read
			  ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

			  // this is storage overwritten on each iteration with bytes
			  int bufferSize = 1024;
			  byte[] buffer = new byte[bufferSize];

			  // we need to know how may bytes were read to write them to the byteBuffer
			  int len = 0;
			  while ((len = inputStream.read(buffer)) != -1) {
			    byteBuffer.write(buffer, 0, len);
			  }

			  // and then we can return your byte array.
			  return byteBuffer.toByteArray();
			}
		
		class ConnectTimerTask extends TimerTask {

			@Override
			public void run() {
				ProtocolTask.this.cancel(true);
			}
			
		}

	}
	
	public static abstract class ProtocolGotResponse {
		public abstract void handleResponse(HttpResponse response, int status, byte[] data);
	}
	
	private class ProtocolConnectBitmapTask extends AsyncTask<Void, Void, Bitmap> {

		private String url;
		private String imageViewTag;
		private int timeout;
		private Timer timer;
		
		private ProtocolBitmapResponse responseHandler;
		
		public ProtocolConnectBitmapTask(String url, String imageViewTag, int timeout, ProtocolBitmapResponse responseHandler) {
			this.url = url;
			this.imageViewTag = imageViewTag;
			this.timeout = timeout;
			this.responseHandler = responseHandler;
		}
		
		@Override
		protected void onPreExecute() {
			timer = new Timer();
			timer.schedule(new ConnectTimerTask(), timeout);
		}
		
		@Override
		protected Bitmap doInBackground(Void... arg0) {
			
//			if (Protocol.getInstance().getBitmapCache() != null) {
//				if (Protocol.getInstance().getBitmapCache().containsKey(url)) {
//					Log.d(ProtocolConstants.LOG_TAG, "Loading image from cache - " + url);
//					return Protocol.getInstance().getBitmapCache().getCachedBitmap(url);
//				} else {
//					Log.d(ProtocolConstants.LOG_TAG, "Image from cache - " + url);
//				}
//			}
			
			URL newurl = null;
			Bitmap bitmap = null;
			try {
				newurl = new URL(this.url);
				bitmap = BitmapFactory.decodeStream(newurl.openConnection() .getInputStream());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
//			if (Protocol.getInstance().getBitmapCache() != null) {
//				Protocol.getInstance().getBitmapCache().addBitmapToCache(url, bitmap);
//			}
			
			return bitmap;
		}
		
		@Override
		protected void onPostExecute(Bitmap bitmap) {
			timer.cancel();
			
			finishedProtocolConnectTask();
			
			if (this.isCancelled() || bitmap == null) {
				if (isDebug()) {
					Log.d(ProtocolConstants.LOG_TAG, "Bitmap - not retrieved");
				}
				responseHandler.handleResponse(imageViewTag, null);
			} else {
				if (isDebug()) {
					Log.d(ProtocolConstants.LOG_TAG, "Bitmap - retrieved");
				}
				this.responseHandler.handleResponse(imageViewTag, bitmap);
			}
				
		}
		
		class ConnectTimerTask extends TimerTask {

			@Override
			public void run() {
				ProtocolConnectBitmapTask.this.cancel(true);
				responseHandler.handleResponse(imageViewTag, null);
			}
			
		}

	}
	
}
