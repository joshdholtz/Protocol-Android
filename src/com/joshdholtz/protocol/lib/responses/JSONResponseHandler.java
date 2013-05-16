package com.joshdholtz.protocol.lib.responses;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.joshdholtz.protocol.lib.helpers.ProtocolConstants;

import android.os.AsyncTask;
import android.util.Log;

public abstract class JSONResponseHandler extends StringResponseHandler {

	JSONObject jsonObject;
	JSONArray jsonArray;
	
	@Override
	public void handleResponse(String stringResponse) {
		
		AsyncTask<String, Void, Object> task = new AsyncTask<String, Void, Object>() {
			@Override
			protected Object doInBackground(String... params) {
				Object json = null;
				try {
					json = new JSONTokener(params[0]).nextValue();
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				return json;
			}
			
			@Override
			protected void onPostExecute(Object json) {
				if (json != null && json instanceof JSONObject) {
					jsonObject = (JSONObject) json;
				} else if (json != null && json instanceof JSONArray) {
					jsonArray = (JSONArray) json;
				}
				
				handleResponse(jsonObject, jsonArray);
			}
		};
		task.execute(stringResponse);
		
	}
	
	public abstract void handleResponse(JSONObject jsonObject, JSONArray jsonArray);

	/**
	 * @return the jsonObject
	 */
	public JSONObject getJsonObject() {
		return jsonObject;
	}

	/**
	 * @return the jsonArray
	 */
	public JSONArray getJsonArray() {
		return jsonArray;
	}
	
}
