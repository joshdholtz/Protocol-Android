package com.joshdholtz.protocol.lib.responses;

import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;

import com.joshdholtz.protocol.lib.helpers.ProtocolConstants;

public abstract class JSONResponseHandler extends ProtocolResponseHandler {

	JSONObject jsonObject;
	JSONArray jsonArray;
	
	@Override
	public void handleResponse(HttpResponse response, int status, byte[] data) {
		
		Log.d(ProtocolConstants.LOG_TAG, "IN JSON GENERATE RESPONSE");
		try {
			Object json = new JSONTokener(new String(data, "UTF8")).nextValue();
			if (json instanceof JSONObject) {
				jsonObject = (JSONObject) json;
			} else if (json instanceof JSONArray) {
				jsonArray = (JSONArray) json;
			}

		} catch (JSONException e) {
			Log.d(ProtocolConstants.LOG_TAG, "Parse exception - " + e.getMessage());
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		handleResponse(jsonObject, jsonArray);
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
