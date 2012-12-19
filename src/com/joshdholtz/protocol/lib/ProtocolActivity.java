package com.joshdholtz.protocol.lib;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.joshdholtz.protocol.lib.R;
import com.joshdholtz.protocol.lib.helpers.ProtocolConstants;
import com.joshdholtz.protocol.lib.test.MemberModel;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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
		
		Protocol.getInstance().setBaseUrl("http://joshdholtz.com/protocol.php");
		Protocol.getInstance().setDebug(true);

		/*
		 * Shows how to do a simple GET request and map to a model
		 */
		this.getMember();
		
		/*
		 * Shows how to do a simple GET request and map to a list of models
		 */
//		this.getMembers();

		/*
		 * Shows how to do a simple GET request to get a bitmap
		 */
//		Protocol.getInstance().setMaxAsyncCount(1);
//		this.getBitmap();
//		this.getBitmap();
//		this.getBitmap();
//		this.getBitmap();
//		this.getBitmap();
		
//		JSONObject body = new JSONObject();
//		try {
//			body.put("email", "nick.gartmann@gmail.com");
//			body.put("password", "Mclblko3");
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		Protocol.getInstance().doPost("/session", body, Protocol.CONTENT_TYPE_JSON, new ProtocolResponse() {
//
//			@Override
//			public void handleResponse(HttpResponse response, int status, String data) {
//				
//			}
//			
//		});
		
//		Protocol.getInstance().doGet("/304", new ProtocolResponse() {
//
//			@Override
//			public void handleResponse(HttpResponse response, int status, String data) {
//				Log.d(ProtocolConstants.LOG_TAG, "Status - " + status);
//			}
//			
//		});
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		this.registerReceiver(receiver, new IntentFilter("com.joshdholtz.protocol.TEST"));
	}
	
	@Override
	public void onPause() {
		super.onPause();
		this.unregisterReceiver(receiver);
	}
	
	/**
	 * Gets a member model.
	 */
	private void getMember() {
		
		// Creates the parameters for the request		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("example","member_1");
		
		Protocol.getInstance().doGet("/protocol.php", null, params, Protocol.CONTENT_TYPE_JSON, this, new Intent("com.joshdholtz.protocol.TEST"));
		
		// Sends the GET request
//		Protocol.getInstance().doGet("/protocol.php", params, new ProtocolResponse() {
//
//			@Override
//			public void handleResponse(HttpResponse response, int status, String data) {
//				
//				// Checks for an okay status of 200
//				if (status == 200) {
//
//					/*
//					 * Method 1 - Create empty model and calls "initFromJSONString"
//					 * initFromJSONString returns a boolean (return false if error with JSON parsing)
//					 */
//					MemberModel member = new MemberModel();
//					boolean success = member.initFromJSONString(data);
//					if (success) { 
//						Log.d(ProtocolConstants.LOG_TAG, "Id - " + member.id);
//						Log.d(ProtocolConstants.LOG_TAG, "First name - " + member.firstName);
//						Log.d(ProtocolConstants.LOG_TAG, "Last name - " + member.lastName);
//					}
//						
//					/*
//					 * Method 2 - Creates a model with ProtocolModel
//					 * Returns the model (return null if error with JSON parsing)
//					 * 
//					 * Why would we do this? There is a call similar to this that returns an array of model.
//					 * Some developers like consistency :)
//					 * 
//					 */
//					member = ProtocolModel.createModel(MemberModel.class, data);
//					if (member != null) {
//						Log.d(ProtocolConstants.LOG_TAG, "Id - " + member.id);
//						Log.d(ProtocolConstants.LOG_TAG, "First name - " + member.firstName);
//						Log.d(ProtocolConstants.LOG_TAG, "Last name - " + member.lastName);
//					}
//					
//				}
//			}
//
//		});
		
	}
	
	private void getMembers() {
		
		// Creates the parameters for the request
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("example","members");
		
		// Sends the GET request
		Protocol.getInstance().doGet("/protocol.php", params, new ProtocolResponse() {

			@Override
			public void handleResponse(HttpResponse response, int status, String data) {
				
				// Checks for an okay status of 200
				if (status == 200) {

					/*
					 * Method 1 - Loops through a JSONArray and creates empty model and calls "initFromJSONString"
					 * for each object
					 */
					List<MemberModel> membersMethod1 = ProtocolModel.createModels(MemberModel.class, data);
					try {
						
						// Individually maps JSON objects in JSON array to a list of models
						JSONArray array = new JSONArray(data);
						for (int i = 0; i < array.length(); ++i) {
							MemberModel member = new MemberModel();
							boolean success = member.initFromJSONString(data);
							if (success) {
								membersMethod1.add(member);
							}
						}
						
						// Prints out the data that we got
						Log.d(ProtocolConstants.LOG_TAG, "Number of members - " + membersMethod1.size());
						for (int i = 0; i < membersMethod1.size(); ++i) {
							MemberModel member = membersMethod1.get(i);
							Log.d(ProtocolConstants.LOG_TAG, "Id - " + member.id);
							Log.d(ProtocolConstants.LOG_TAG, "First name - " + member.firstName);
							Log.d(ProtocolConstants.LOG_TAG, "Last name - " + member.lastName);
						}
						
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
						
					/*
					 * Method 2 - Creates a list of models with ProtocolModel
					 * Returns the list model (returns an empty array - no returning of null)
					 * 
					 * Why would we do this? There is a call similar to this that returns a single model
					 * Some developers like consistency :)
					 * 
					 */
					List<MemberModel> membersMethod2 = ProtocolModel.createModels(MemberModel.class, data);
					if (membersMethod2 != null) {
						
						// Prints out the data that we got
						Log.d(ProtocolConstants.LOG_TAG, "Number of members - " + membersMethod2.size());
						for (int i = 0; i < membersMethod2.size(); ++i) {
							MemberModel member = membersMethod2.get(i);
							Log.d(ProtocolConstants.LOG_TAG, "Id - " + member.id);
							Log.d(ProtocolConstants.LOG_TAG, "First name - " + member.firstName);
							Log.d(ProtocolConstants.LOG_TAG, "Last name - " + member.lastName);
						}
						
					}
					
				}
			}

		});
		
	}
	
	private void getBitmap() {
		
//		Protocol.getInstance().doGetBitmap("http://images2.fanpop.com/images/photos/6900000/cute-kitten-cats-6987468-670-578.jpg", new ProtocolBitmapResponse() {
//
//			@Override
//			public void handleResponse(Bitmap bitmap) {
//				
//				if (bitmap != null) {
//					img.setImageBitmap(bitmap);
//					Toast.makeText(ProtocolActivity.this, "Meow - " + bitmap.getWidth() + " x " + bitmap.getHeight(), Toast.LENGTH_LONG).show();
//				}
//				
//			}
//			
//		});
		
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
				Protocol.getInstance().doPostWithFile("/fileupload.php", new ArrayList<BasicNameValuePair>(), files, new ProtocolResponse() {

					@Override
					public void handleResponse(HttpResponse response, int status, String data) {

					}

				});

			}
		}
	}
	
	BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Map<String, String> headers = (Map<String, String>) intent.getSerializableExtra(Protocol.BROADCAST_DATA_HEADERS);
			int status = intent.getIntExtra(Protocol.BROADCAST_DATA_STATUS, -1);
			String response = intent.getStringExtra(Protocol.BROADCAST_DATA_RESPONSE);
		}
		
	};

}