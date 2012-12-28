package com.joshdholtz.protocol.lib;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.joshdholtz.protocol.lib.ProtocolClient.ProtocolStatusListener;
import com.joshdholtz.protocol.lib.ProtocolClient.ProtocolTask;
import com.joshdholtz.protocol.lib.R;
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
import android.widget.ImageView;
import android.widget.Toast;

public class ProtocolActivity extends Activity {
	
	ImageView img;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		img = (ImageView) this.findViewById(R.id.img);
		
		ProtocolTask task = new ProtocolTask(HttpMethod.HTTP_GET, "http://www.statuscodewhat.com/200?body=HelloWorldddd", null, new ProtocolResponseHandler() {

			@Override
			public void handleResponse(HttpResponse response, int status, byte[] data) {
				String responseData = new String(data);
				Toast.makeText(getApplication(), "ProtocolTaskResponse - " + responseData, Toast.LENGTH_SHORT).show();
			}
			
		});
		task.execute();
		
		ProtocolClient client = new ProtocolClient("http://www.statuscodewhat.com");
		
		client.addHeader("client_test_request_header", "client_test_request_header_value");
		client.addHeader("client_test_request_header_override", "THIS SHOULDN'T SHOW");
		
		client.doGet("/200?body=HelloWorld", null, new StringResponseHandler() {

			@Override
			public void handleResponse(String stringResponse) {
				Toast.makeText(getApplication(), stringResponse, Toast.LENGTH_SHORT).show();
			}

			
		});
		
		ParamsRequestData requestData1 = new ParamsRequestData();
		requestData1.addParam("body", "{\"name1\":\"value1\",\"name2\":\"value2\",\"name3\":\"value3\"}");
		client.doGet("/200", requestData1, new JSONResponseHandler() {

			@Override
			public void handleResponse(JSONObject jsonObject, JSONArray jsonArray) {
				if (jsonArray != null) {
					Toast.makeText(getApplication(), "JSON Array Size - " + jsonArray.length(), Toast.LENGTH_SHORT).show();
				} else if (jsonObject != null) {
					Toast.makeText(getApplication(), "JSON Object Size - " + jsonObject.length(), Toast.LENGTH_SHORT).show();
				}
			}
			
		});
		
		ParamsRequestData requestData2 = new ParamsRequestData();
		requestData2.addParam("body", "[{\"name1\":\"value1\",\"name2\":\"value2\"},{\"name1\":\"value1\",\"name2\":\"value2\"}]");
		client.doGet("/200", requestData2, new JSONResponseHandler() {

			@Override
			public void handleResponse(JSONObject jsonObject, JSONArray jsonArray) {
				if (jsonArray != null) {
					Toast.makeText(getApplication(), "JSON Array Size - " + jsonArray.length(), Toast.LENGTH_SHORT).show();
				} else if (jsonObject != null) {
					Toast.makeText(getApplication(), "JSON Object Size - " + jsonObject.length(), Toast.LENGTH_SHORT).show();
				}
			}
			
		});
		
		ParamsRequestData requestData3 = new ParamsRequestData();
		requestData3.addHeader("test_request_header", "test_request_header_value");
		requestData3.addHeader("client_test_request_header_override", "client_test_request_header_override_value");
		requestData3.addParam("body", "");
		requestData3.addParam("show_headers", "true");
		client.doGet("/200", requestData3, new JSONResponseHandler() {

			@Override
			public void handleResponse(JSONObject jsonObject, JSONArray jsonArray) {
				if (jsonObject != null) {
					try {
						JSONObject headers = jsonObject.getJSONObject("headers");
						if (headers.has("test_request_header")) {
							Toast.makeText(getApplication(), "Found test_request_header - " + headers.getString("test_request_header"), Toast.LENGTH_SHORT).show();
						}
						if (headers.has("client_test_request_header")) {
							Toast.makeText(getApplication(), "Found client_test_request_header - " + headers.getString("client_test_request_header"), Toast.LENGTH_SHORT).show();
						}
						if (headers.has("client_test_request_header_override")) {
							Toast.makeText(getApplication(), "Found client_test_request_header_override - " + headers.getString("client_test_request_header_override"), Toast.LENGTH_SHORT).show();
						}
					
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
			
		});
		
		
		ParamsRequestData requestData4 = new ParamsRequestData();
		requestData4.addParam("body", "{\"name1\":\"value1\",\"name2\":\"value2\"}}");
		CustomClient.get("/200", requestData4, new JSONResponseHandler() {

			@Override
			public void handleResponse(JSONObject jsonObject, JSONArray jsonArray) {
				if (jsonObject != null) {
					try {
						String name1 = jsonObject.getString("name1");
						Toast.makeText(getApplication(), "CustomClient name1 - " + name1, Toast.LENGTH_SHORT).show();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
			
		});
		
		client.observeStatus(401, new ProtocolStatusListener() {

			@Override
			public boolean observedStatus(int status, ProtocolResponseHandler handler) {
				Toast.makeText(getApplication(), "You are not logged in; We observed a status - " + status, Toast.LENGTH_SHORT).show();
				return false;
			}
			
		});
		
		client.observeStatus(500, new ProtocolStatusListener() {

			@Override
			public boolean observedStatus(int status, ProtocolResponseHandler handler) {
				Toast.makeText(getApplication(), "We got a server error; We observed a status - " + status, Toast.LENGTH_SHORT).show();
				return true;
			}
			
		});
		
		client.doGet("/401", null, new JSONResponseHandler() {

			@Override
			public void handleResponse(JSONObject jsonObject, JSONArray jsonArray) {
				Toast.makeText(getApplication(), "The ProtocolStatusListener should catch this 401 and not show this toast", Toast.LENGTH_SHORT).show();
			}
			
		});
		
		client.doGet("/500", null, new JSONResponseHandler() {

			@Override
			public void handleResponse(JSONObject jsonObject, JSONArray jsonArray) {
				Toast.makeText(getApplication(), "The ProtocolStatusListener should catch this 500 and show this toast", Toast.LENGTH_SHORT).show();
			}
			
		});
		
		ParamsRequestData requestData5 = new ParamsRequestData();
		requestData5.addParam("first_name", "Josh");
		requestData5.addParam("last_name", "Holtz");
		client.doPost("/200", requestData5, new StringResponseHandler() {

			@Override
			public void handleResponse(String stringResponse) {
				if (this.getStatus() == 200) {
					Toast.makeText(getApplication(), "POST param success", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getApplication(), "POST params failure", Toast.LENGTH_SHORT).show();
				}
			}
			
		});
		
		Map<String, String> jsonObjectData = new HashMap<String, String>();
		jsonObjectData.put("first_name", "Josh");
		jsonObjectData.put("last_name", "Holtz");
		JSONObject jsonObject = new JSONObject(jsonObjectData);
		
		JSONRequestData requestData6 = new JSONRequestData(jsonObject);
		client.doPut("/200", requestData6, new StringResponseHandler() {

			@Override
			public void handleResponse(String stringResponse) {
				if (this.getStatus() == 200) {
					Toast.makeText(getApplication(), "POST json success", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getApplication(), "POST json failure", Toast.LENGTH_SHORT).show();
				}
			}
			
		});
		
		Map<String, Object> fileObjectData = new HashMap<String, Object>();
		fileObjectData.put("first_name", "Josh");
		fileObjectData.put("last_name", "Holtz");
		
		Map<String, File> filesData = new HashMap<String, File>();
		filesData.put("file1", new File("../somepath.."));
		
		FileRequestData requestData7 = new FileRequestData(fileObjectData, filesData);
		client.doPut("/200", requestData7, new StringResponseHandler() {

			@Override
			public void handleResponse(String stringResponse) {
				if (this.getStatus() == 200) {
					Toast.makeText(getApplication(), "POST file success", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getApplication(), "POST file failure", Toast.LENGTH_SHORT).show();
				}
			}
			
		});
		
		ParamsRequestData requestData8 = new ParamsRequestData();
		requestData8.addParam("body", "{\"first_name\":\"Josh\",\"last_name\":\"Holtz\"}");
		client.doGet("/200", requestData8, new ModelResponseHandler<MemberModel>() {

			@Override
			public void handleResponse(MemberModel model) {
				Toast.makeText(getApplication(), "Member name - " + model.getFirstName() + " " + model.getLastName(), Toast.LENGTH_SHORT).show();
			}

			@Override
			public void handleResponse(List<MemberModel> model) {}

			@Override
			public void handleError() {}
			
		});
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

				Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);

				File file = new File(filePath);

				List<File> files = new ArrayList<File>();
				files.add(file);
				

			}
		}
	}

}