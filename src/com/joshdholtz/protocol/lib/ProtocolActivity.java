package com.joshdholtz.protocol.lib;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.joshdholtz.protocol.lib.R;
import com.joshdholtz.protocol.lib.helpers.ProtocolConstants;
import com.joshdholtz.protocol.lib.test.MemberModel;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class ProtocolActivity extends Activity {
	
	TextView txt;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Protocol.getInstance().setBaseUrl("http://192.168.1.7");
        Protocol.getInstance().setDebug(true);
        
        txt = (TextView) this.findViewById(R.id.txt);
        
		Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, 0); 
        
//        Protocol.getInstance().setBaseUrl("http://kingofti.me");
//        
//        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
//        params.add(new BasicNameValuePair("email", "josh@rokkincat.com"));
//        params.add(new BasicNameValuePair("password", "test01"));
//        Protocol.getInstance().doPost("/session", params, new ProtocolResponse() {
//
//			@Override
//			public void handleResponse(HttpResponse response, int status, String data) {
//				Log.d("ProtocolTest", "POST session - " + status + " - " + data);
//				
//				if (status == 200) {
//					Header cookie = response.getFirstHeader("Set-Cookie");
//					Protocol.getInstance().addHeader("Cookie", cookie.getValue());
//					
//					Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//			        startActivityForResult(i, 0); 
//					
//					Protocol.getInstance().doGet("/session", new ProtocolResponse() {
//
//						@Override
//						public void handleResponse(HttpResponse response,int status, String data) {
//							Log.d("ProtocolTest", "GET session - " + status + " - " + data);
//							
//							try {
//								JSONObject object = new JSONObject(data);
//								Toast.makeText(ProtocolActivity.this, "Here", Toast.LENGTH_SHORT).show();
//								
//								MemberModel member = new MemberModel(object);
//								Toast.makeText(ProtocolActivity.this, "Id - " + member.id, Toast.LENGTH_SHORT).show();
//								Toast.makeText(ProtocolActivity.this, "First name - " + member.firstName, Toast.LENGTH_SHORT).show();
//								Toast.makeText(ProtocolActivity.this, "Last name - " + member.firstName, Toast.LENGTH_SHORT).show();
//								
//								Log.d("ProtocolTest", object.length() + "");
//							} catch (JSONException e) {
//								Log.d("ProtocolTest", "here?");
//								e.printStackTrace();
//							}
//							
//						}
//						
//					});
//					
//				}
//				
//				
//			}
//        	
//        });
        
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) { 
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent); 

        switch(requestCode) { 
        case 0:
            if(resultCode == RESULT_OK){  
                Uri selectedImage = imageReturnedIntent.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String filePath = cursor.getString(columnIndex);
                cursor.close();

                Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);
                
                File file = new File(filePath);
                
                List<File> files = new ArrayList<File>();
                files.add(file);
                Protocol.getInstance().doPostWithFile("/fileupload.php", new ArrayList<BasicNameValuePair>(), files, new ProtocolResponse() {

					@Override
					public void handleResponse(HttpResponse response, int status, String data) {
						
					}
                	
                });
                
            }
        }
    }
    
}