package com.joshdholtz.protocol.lib;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicNameValuePair;

import com.joshdholtz.protocol.lib.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class RestCatActivity extends Activity {
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Protocol.getInstance().setBaseUrl("http://kingofti.me");
        
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair("email", "josh@rokkincat.com"));
        params.add(new BasicNameValuePair("password", "test01"));
        Protocol.getInstance().doPost("/session", params, new ProtocolResponse() {

			@Override
			public void handleResponse(HttpResponse response, int status, String data) {
				Log.d("ProtocolTest", "POST session - " + status + " - " + data);
				
				if (status == 200) {
					Header cookie = response.getFirstHeader("Set-Cookie");
					Protocol.getInstance().addHeader("Cookie", cookie.getValue());
					
					Protocol.getInstance().doGet("/session", new ProtocolResponse() {

						@Override
						public void handleResponse(HttpResponse response,int status, String data) {
							Log.d("ProtocolTest", "GET session - " + status + " - " + data);
						}
						
					});
					
				}
				
				
			}
        	
        });
        
    }
    
}