package com.joshdholtz.protocol.lib;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.joshdholtz.protocol.lib.ProtocolClient.ProtocolStatusListener;
import com.joshdholtz.protocol.lib.ProtocolClient.ProtocolTask;
import com.joshdholtz.protocol.lib.ProtocolModelFormats.MapFormat;
import com.joshdholtz.protocol.lib.R;
import com.joshdholtz.protocol.lib.helpers.ProtocolConstants;
import com.joshdholtz.protocol.lib.helpers.ProtocolConstants.HttpMethod;
import com.joshdholtz.protocol.lib.models.MemberModel;
import com.joshdholtz.protocol.lib.requests.FileRequestData;
import com.joshdholtz.protocol.lib.requests.JSONRequestData;
import com.joshdholtz.protocol.lib.requests.ParamsRequestData;
import com.joshdholtz.protocol.lib.responses.JSONResponseHandler;
import com.joshdholtz.protocol.lib.responses.ModelResponseHandler;
import com.joshdholtz.protocol.lib.responses.ProtocolResponseHandler;
import com.joshdholtz.protocol.lib.responses.StringResponseHandler;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class ProtocolActivity extends Activity {
	
	ImageView img;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

//		img = (ImageView) this.findViewById(R.id.img);
		
		// Adds custom model mapping
//		ProtocolModelFormats.set("date", new MapFormat() {
//
//			@Override
//			public Object format(Object value) {
//				Log.d(ProtocolConstants.LOG_TAG, "Am I here?");
//				SimpleDateFormat sdf = new SimpleDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ssZ");
//				try {
//					return sdf.parse(value.toString());
//				} catch (ParseException e) {e.printStackTrace();}
//				return null;
//			}
//			
//		});
//		
//		// Test model mapping
//		MemberModel member = ProtocolModel.createModel(MemberModel.class, 
//				"{\"first_name\":\"Josh\"," +
//				"\"last_name\":\"Holtz\"," +
//				"\"age\":4," +
//				"\"awesome_level\":\"4.6\"," +
//				"\"cool\":true," +
//				"\"dob\":\"2012-10-12T22:55:20+00:00\"," +
//				"\"friend\":{\"first_name\":\"Bandit\"}," +
//				"\"friends\":[{\"first_name\":\"Bandit\"},{\"first_name\":\"Bandit\"}]" +
//				"}");
//		Toast.makeText(getApplication(), "First Name - " + member.firstName + 
//				"\nLast Name " + member.lastName + 
//				"\nAge " + member.age + 
//				"\nAwesome level " + member.awesomeLevel + 
//				"\nCool " + member.cool +
//				"\nBirthday " + member.birthday +
//				"\nFriend " + member.friend.firstName + 
//				"\nFriends count: " + member.friends.size(),
//				Toast.LENGTH_LONG).show();
		
//		ProtocolClient shareClient = new ProtocolClient("http://sharemd-api-dev.herokuapp.com");
//		
//		Map<String, Object> jsonData = new HashMap<String, Object>();
//		jsonData.put("email", "josh@share.md");
//		jsonData.put("password", "test123");
//		JSONRequestData data = new JSONRequestData(new JSONObject(jsonData));
//		shareClient.doPost("/session", data, new ProtocolResponseHandler() {
//
//			@Override
//			public void handleResponse(HttpResponse response, int status, byte[] data) {
//				Toast.makeText(getApplication(), "ProtocolTaskResponse - " + status, Toast.LENGTH_SHORT).show();
//				Header[] headers = response.getAllHeaders();
//				for (Header header : headers) {
//					Toast.makeText(getApplication(), header.getName() + " - " + header.getValue(), Toast.LENGTH_SHORT).show();
//				}
//			}
//			
//		});
		
//		ProtocolTask task = new ProtocolTask(HttpMethod.HTTP_GET, "http://www.statuscodewhat.com/200?body=HelloWorldddd", null, new ProtocolResponseHandler() {
//
//			@Override
//			public void handleResponse(HttpResponse response, int status, byte[] data) {
//				String responseData = new String(data);
//				Toast.makeText(getApplication(), "ProtocolTaskResponse - " + responseData, Toast.LENGTH_SHORT).show();
//			}
//			
//		});
//		task.execute();
//		
//		
//		client.addHeader("client_test_request_header", "client_test_request_header_value");
//		client.addHeader("client_test_request_header_override", "THIS SHOULDN'T SHOW");
//		
//		client.doGet("/200?body=", null, new StringResponseHandler() {
//
//			@Override
//			public void handleResponse(String stringResponse) {
//				Toast.makeText(getApplication(), "Empty body - " + stringResponse, Toast.LENGTH_SHORT).show();
//			}
//
//			
//		});
		
//		ParamsRequestData requestData1 = new ParamsRequestData();
//		requestData1.addParam("body", "{\"name1\":\"value1\",\"name2\":\"value2\",\"name3\":\"value3\"}");
//		client.doGet("/200", requestData1, new JSONResponseHandler() {
//
//			@Override
//			public void handleResponse(JSONObject jsonObject, JSONArray jsonArray) {
//				if (jsonArray != null) {
//					Toast.makeText(getApplication(), "JSON Array Size - " + jsonArray.length(), Toast.LENGTH_SHORT).show();
//				} else if (jsonObject != null) {
//					Toast.makeText(getApplication(), "JSON Object Size - " + jsonObject.length(), Toast.LENGTH_SHORT).show();
//				}
//			}
//			
//		});
//		
//		ParamsRequestData requestData2 = new ParamsRequestData();
//		requestData2.addParam("body", "{\"first_name\":\"Josh\"," +
//				"\"last_name\":\"Holtz\"," +
//				"\"age\":4," +
//				"\"awesome_level\":\"4.6\"," +
//				"\"cool\":null," +
//				"\"dob\":\"2012-10-12T22:55:20+00:00\"," +
//				"\"friend\":{\"first_name\":\"Bandit\"}," +
//				"\"friends\":[{\"first_name\":\"Bandit\"},{\"first_name\":\"Bandit\"}]" +
//				"}");
//		client.doGet("/200", requestData2, new JSONResponseHandler() {
//
//			@Override
//			public void handleResponse(JSONObject jsonObject, JSONArray jsonArray) {
//				if (jsonArray != null) {
////					Toast.makeText(getApplication(), "JSON Array Size - " + jsonArray.length(), Toast.LENGTH_SHORT).show();
//				} else if (jsonObject != null) {
//					MemberModel member = ProtocolModel.createModel(MemberModel.class, jsonObject);
//					Toast.makeText(getApplication(), "Cool - " + member.cool, Toast.LENGTH_LONG).show();
////					Toast.makeText(getApplication(), "JSON Object Size - " + jsonObject.length(), Toast.LENGTH_SHORT).show();
//				}
//			}
//			
//		});
//		
//		ParamsRequestData requestData3 = new ParamsRequestData();
//		requestData3.addHeader("test_request_header", "test_request_header_value");
//		requestData3.addHeader("client_test_request_header_override", "client_test_request_header_override_value");
//		requestData3.addParam("body", "");
//		requestData3.addParam("show_headers", "true");
//		client.doGet("/200", requestData3, new JSONResponseHandler() {
//
//			@Override
//			public void handleResponse(JSONObject jsonObject, JSONArray jsonArray) {
//				if (jsonObject != null) {
//					try {
//						JSONObject headers = jsonObject.getJSONObject("headers");
//						if (headers.has("test_request_header")) {
//							Toast.makeText(getApplication(), "Found test_request_header - " + headers.getString("test_request_header"), Toast.LENGTH_SHORT).show();
//						}
//						if (headers.has("client_test_request_header")) {
//							Toast.makeText(getApplication(), "Found client_test_request_header - " + headers.getString("client_test_request_header"), Toast.LENGTH_SHORT).show();
//						}
//						if (headers.has("client_test_request_header_override")) {
//							Toast.makeText(getApplication(), "Found client_test_request_header_override - " + headers.getString("client_test_request_header_override"), Toast.LENGTH_SHORT).show();
//						}
//					
//					} catch (JSONException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//			
//		});
//		
//		
//		ParamsRequestData requestData4 = new ParamsRequestData();
//		requestData4.addParam("body", "{\"name1\":\"value1\",\"name2\":\"value2\"}}");
//		CustomClient.get("/200", requestData4, new JSONResponseHandler() {
//
//			@Override
//			public void handleResponse(JSONObject jsonObject, JSONArray jsonArray) {
//				if (jsonObject != null) {
//					try {
//						String name1 = jsonObject.getString("name1");
//						Toast.makeText(getApplication(), "CustomClient name1 - " + name1, Toast.LENGTH_SHORT).show();
//					} catch (JSONException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//			
//		});
//		
//		client.observeStatus(401, new ProtocolStatusListener() {
//
//			@Override
//			public boolean observedStatus(int status, ProtocolResponseHandler handler) {
//				Toast.makeText(getApplication(), "You are not logged in; We observed a status - " + status, Toast.LENGTH_SHORT).show();
//				return false;
//			}
//			
//		});
//		
//		client.observeStatus(500, new ProtocolStatusListener() {
//
//			@Override
//			public boolean observedStatus(int status, ProtocolResponseHandler handler) {
//				Toast.makeText(getApplication(), "We got a server error; We observed a status - " + status, Toast.LENGTH_SHORT).show();
//				return true;
//			}
//			
//		});
//		
//		client.doGet("/401", null, new JSONResponseHandler() {
//
//			@Override
//			public void handleResponse(JSONObject jsonObject, JSONArray jsonArray) {
//				Toast.makeText(getApplication(), "The ProtocolStatusListener should catch this 401 and not show this toast", Toast.LENGTH_SHORT).show();
//			}
//			
//		});
//		
//		client.doGet("/500", null, new JSONResponseHandler() {
//
//			@Override
//			public void handleResponse(JSONObject jsonObject, JSONArray jsonArray) {
//				Toast.makeText(getApplication(), "The ProtocolStatusListener should catch this 500 and show this toast", Toast.LENGTH_SHORT).show();
//			}
//			
//		});
//		
//		ParamsRequestData requestData5 = new ParamsRequestData();
//		requestData5.addParam("first_name", "Josh");
//		requestData5.addParam("last_name", "Holtz");
//		client.doPost("/200", requestData5, new StringResponseHandler() {
//
//			@Override
//			public void handleResponse(String stringResponse) {
//				if (this.getStatus() == 200) {
//					Toast.makeText(getApplication(), "POST param success", Toast.LENGTH_SHORT).show();
//				} else {
//					Toast.makeText(getApplication(), "POST params failure", Toast.LENGTH_SHORT).show();
//				}
//			}
//			
//		});
//		
//		Map<String, String> jsonObjectData = new HashMap<String, String>();
//		jsonObjectData.put("first_name", "Josh");
//		jsonObjectData.put("last_name", "Holtz");
//		JSONObject jsonObject = new JSONObject(jsonObjectData);
//		
//		JSONRequestData requestData6 = new JSONRequestData(jsonObject);
//		client.doPut("/200", requestData6, new StringResponseHandler() {
//
//			@Override
//			public void handleResponse(String stringResponse) {
//				if (this.getStatus() == 200) {
//					Toast.makeText(getApplication(), "POST json success", Toast.LENGTH_SHORT).show();
//				} else {
//					Toast.makeText(getApplication(), "POST json failure", Toast.LENGTH_SHORT).show();
//				}
//			}
//			
//		});
//		
//		Map<String, Object> fileObjectData = new HashMap<String, Object>();
//		fileObjectData.put("first_name", "Josh");
//		fileObjectData.put("last_name", "Holtz");
//		
//		Map<String, File> filesData = new HashMap<String, File>();
//		filesData.put("file1", new File("../somepath.."));
//		
//		FileRequestData requestData7 = new FileRequestData(fileObjectData, filesData);
//		client.doPut("/200", requestData7, new StringResponseHandler() {
//
//			@Override
//			public void handleResponse(String stringResponse) {
//				if (this.getStatus() == 200) {
//					Toast.makeText(getApplication(), "POST file success", Toast.LENGTH_SHORT).show();
//				} else {
//					Toast.makeText(getApplication(), "POST file failure", Toast.LENGTH_SHORT).show();
//				}
//			}
//			
//		});
//		
//		ParamsRequestData requestData8 = new ParamsRequestData();
//		requestData8.addParam("body", "{\"first_name\":\"Josh\",\"last_name\":\"Holtz\"}");
//		client.doGet("/200", requestData8, new ModelResponseHandler<MemberModel>() {
//
//			@Override
//			public void handleResponse(MemberModel model) {
//				Toast.makeText(getApplication(), "Member name - " + model.firstName + " " + model.lastName, Toast.LENGTH_SHORT).show();
//			}
//
//			@Override
//			public void handleResponse(List<MemberModel> model) {}
//
//			@Override
//			public void handleError() {}
//			
//		});

	}
	
	public void onClickUploadImage(View view) {
		uploadFile();
	}

	private void uploadFile() {
		Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(i, 0); 
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
				Log.d(ProtocolConstants.LOG_TAG, "FILE PATH - " + filePath);

				Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);
				img.setImageBitmap(yourSelectedImage);
				
				File file = new File(filePath);

				Map<String, File> files = new HashMap<String, File>();
				files.put("file", file);
				
				FileRequestData requestData = new FileRequestData(new HashMap<String, Object>(), files);
				ProtocolClient client = new ProtocolClient("http://www.statuscodewhat.com");
				client.setDebug(true);
				client.doPost("/200?show_headers=true", requestData, new StringResponseHandler() {

					@Override
					public void handleResponse(String stringResponse) {
						Toast.makeText(ProtocolActivity.this, "Status = " + this.getStatus(), Toast.LENGTH_SHORT).show();
					}
					
				});
			}
		}
	}

}