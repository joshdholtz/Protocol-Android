package com.joshdholtz.protocol.lib;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class ProtocolModel {
	
	public ProtocolModel(JSONObject jsonObject) {
		JSONArray names = jsonObject.names();
		for (int i = 0; i < names.length(); ++i) {
			try {
				this.mapToClass(names.getString(i), jsonObject.get(names.getString(i)));
				names.get(i);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public abstract void mapToClass(String key, Object value);
	
}
