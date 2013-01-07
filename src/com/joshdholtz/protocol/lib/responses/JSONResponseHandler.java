package com.joshdholtz.protocol.lib.responses;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public abstract class JSONResponseHandler extends StringResponseHandler {

	JSONObject jsonObject;
	JSONArray jsonArray;
	
	@Override
	public void handleResponse(String stringResponse) {
		
		try {
			Object json = new JSONTokener(stringResponse).nextValue();
			if (json instanceof JSONObject) {
				jsonObject = (JSONObject) json;
			} else if (json instanceof JSONArray) {
				jsonArray = (JSONArray) json;
			}

		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
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
