package com.joshdholtz.protocol.lib;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.joshdholtz.protocol.lib.R;
import com.joshdholtz.protocol.lib.test.MemberModel;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class ProtocolActivity extends Activity {
	
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
							
							try {
								JSONObject object = new JSONObject(data);
								Toast.makeText(ProtocolActivity.this, "Here", Toast.LENGTH_SHORT).show();
								
								MemberModel member = new MemberModel(object);
								Toast.makeText(ProtocolActivity.this, "Id - " + member.id, Toast.LENGTH_SHORT).show();
								Toast.makeText(ProtocolActivity.this, "First name - " + member.firstName, Toast.LENGTH_SHORT).show();
								Toast.makeText(ProtocolActivity.this, "Last name - " + member.firstName, Toast.LENGTH_SHORT).show();
								
								Log.d("ProtocolTest", object.length() + "");
							} catch (JSONException e) {
								Log.d("ProtocolTest", "here?");
								e.printStackTrace();
							}
							
						}
						
					});
					
				}
				
				
			}
        	
        });
        
    }
    
}