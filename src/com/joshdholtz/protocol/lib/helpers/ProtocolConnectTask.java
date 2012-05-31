package com.joshdholtz.protocol.lib.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
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
import android.util.Log;

public class ProtocolConnectTask extends AsyncTask<Void, Void, HttpResponse> {

	private HttpMethod method;
	private String route;
	private String contentType;
	private HttpEntity entity;
	private Timer timer;
	private GotResponse handler;
	
	private HttpUriRequest httpUriRequest;
	
	public ProtocolConnectTask(HttpMethod method, String route, HttpEntity entity, GotResponse handler) {
		this(method, route, null, entity, handler);
	}
	
	public ProtocolConnectTask(HttpMethod method, String route, String contentType, HttpEntity entity, GotResponse handler) {
		this.method = method;
		this.route = route;
		this.contentType = contentType;
		this.entity = entity;
		this.handler = handler;
	}
	
	@Override
	protected void onPreExecute() {
		timer = new Timer();
		timer.schedule(new ConnectTimerTask(), 100000);
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
			
			Log.d(ProtocolConstants.LOG_TAG, "URL - " + route);
			HttpResponse httpResponse = httpClient.execute(httpUriRequest);
			
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
			Log.d("", "ServerConnect - aborting request from cancel");
		}
	}
	
	@Override
	protected void onPostExecute(HttpResponse httpResponse) {
		timer.cancel();
		
		if (this.isCancelled() || httpResponse == null) {
			handler.handleResponse(null, -1, null);
		} else {
			int status = httpResponse.getStatusLine().getStatusCode();
			StringBuffer out = new StringBuffer();
			
			// Gets the input stream and unpackages the response into a command
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
				String line = null;
				while((line = reader.readLine()) != null){
					out.append(line + "\n");
				}
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			handler.handleResponse(httpResponse, status, out.toString());
		}
	}
	
	class ConnectTimerTask extends TimerTask {

		@Override
		public void run() {
			ProtocolConnectTask.this.cancel(true);
			handler.handleResponse(null, -1, null);
		}
		
	}
	
	public abstract static class GotResponse {
		public abstract void handleResponse(HttpResponse response, int status, String data);
	}

}
