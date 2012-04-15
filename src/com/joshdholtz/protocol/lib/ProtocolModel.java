package com.joshdholtz.protocol.lib;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class ProtocolModel {
	
	public ProtocolModel() {
		
	}
	
	public ProtocolModel(JSONObject jsonObject) {
		JSONArray names = jsonObject.names();
		for (int i = 0; i < names.length(); ++i) {
			try {
				
				String key = names.getString(i);
				if (!jsonObject.isNull(key)) {
					this.mapToClass(key, jsonObject.get(key));
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public abstract void mapToClass(String key, Object value);
	
}
