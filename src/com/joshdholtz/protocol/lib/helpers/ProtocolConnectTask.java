package com.joshdholtz.protocol.lib.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import com.joshdholtz.protocol.lib.Protocol;
import com.joshdholtz.protocol.lib.ProtocolMultipartEntity;
import com.joshdholtz.protocol.lib.helpers.ProtocolConstants.HttpMethod;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

public class ProtocolConnectTask extends AsyncTask<Void, Void, HttpResponse> {

	private HttpMethod method;
	private String route;
	private Map<String, String> headers;
	private String contentType;
	private HttpEntity entity;
	private Timer timer;
	private int timeout;
	private GotResponse handler;
	
	private HttpUriRequest httpUriRequest;
	
	private int status;
	private String stringResp;
	
	private Handler threadHandler = new Handler();
	
	public ProtocolConnectTask(HttpMethod method, String route, HttpEntity entity, int timeout, GotResponse handler) {
		this(method, route, null, entity, timeout, handler);
	}
	
	public ProtocolConnectTask(HttpMethod method, String route, String contentType, HttpEntity entity, int timeout, GotResponse handler) {
		this(method, route, new HashMap<String, String>(), contentType, entity, timeout, handler);
	}
	
	public ProtocolConnectTask(HttpMethod method, String route, Map<String, String> headers, String contentType, HttpEntity entity, int timeout, GotResponse handler) {
		this.method = method;
		this.route = route;
		this.headers = headers;
		this.contentType = contentType;
		this.entity = entity;
		this.timeout = timeout;
		this.handler = handler;
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
				case HTTP_POST_FILE:
					HttpPost httpPostFileRequest = new HttpPost(route);
					httpPostFileRequest.setEntity(entity);
					httpUriRequest = httpPostFileRequest;
					
					httpUriRequest.addHeader("Content-Type", contentType);
					
					
				
//					httpUriRequest.addHeader("Content-Length", String.valueOf(multi.forRealSize()));
//					httpUriRequest.addHeader("Accept", "*/*");
//					httpUriRequest.addHeader("Accept-Language", "en-us");
//					httpUriRequest.addHeader("Accept-Encoding", "gzip, deflate");
					
					break;
			}
			
			if (contentType != null) {
				httpUriRequest.setHeader("Content-Type", contentType);
			}
			
			// Adds the headers
			for (int i = 0; i < Protocol.getInstance().getHeaders().size(); ++i) {
				BasicNameValuePair header = Protocol.getInstance().getHeaders().get(i);
				httpUriRequest.setHeader(header.getName(), header.getValue());
			}
		
			Iterator<Entry<String,String>> it = headers.entrySet().iterator();
		    while (it.hasNext()) {
		        Entry<String,String> pairs = it.next();
		        httpUriRequest.setHeader(pairs.getKey(), pairs.getValue());
		        it.remove(); // avoids a ConcurrentModificationException
		    }
			
			Log.d(ProtocolConstants.LOG_TAG, method.toString() + " - " + route);
			HttpResponse httpResponse = httpClient.execute(httpUriRequest);
			
			status = httpResponse.getStatusLine().getStatusCode();
			StringBuffer out = new StringBuffer();
			
			// Gets the input stream and unpackages the response into a command
			if (httpResponse.getEntity() != null) {
				try {
					InputStream in = httpResponse.getEntity().getContent();
					
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					String line = null;
					while((line = reader.readLine()) != null){
						out.append(line + "\n");
					}
					reader.close();
					
					stringResp = out.toString();
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			return httpResponse;

//			return command.unpackageJSON(out.toString());
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
			handler.handleResponse(null, -1, nullStr);
			Log.d("", "ServerConnect - aborting request from cancel");
		}
	}
	
	@Override
	protected void onPostExecute(HttpResponse httpResponse) {
		timer.cancel();
		
		Protocol.getInstance().finishedProtocolConnectTask();
		
		if (this.isCancelled() || httpResponse == null) {
			String nullStr = null;
			handler.handleResponse(null, status, nullStr);
		} else {
			handler.handleResponse(httpResponse, status, stringResp);
		}
	}
	
	class ConnectTimerTask extends TimerTask {

		@Override
		public void run() {
			ProtocolConnectTask.this.cancel(true);
		}
		
	}
	
	public abstract static class GotResponse {
		public abstract void handleResponse(HttpResponse response, int status, String data);
		public abstract void handleResponse(HttpResponse response, int status, InputStream in);
	}

}
