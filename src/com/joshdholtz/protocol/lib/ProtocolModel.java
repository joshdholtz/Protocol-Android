package com.joshdholtz.protocol.lib;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class ProtocolModel {
	
	public static <T extends ProtocolModel>T createModel(Class<T> clazz, String jsonData) {
		T instance = null;
		try {
			instance = clazz.newInstance();
			instance.initFromJSONString(jsonData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return instance;
	}
	
	public static <T extends ProtocolModel>List<T> createModels(Class<T> clazz, String jsonData) {
		List<T> instances = new ArrayList<T>();
		try {
			JSONArray array = new JSONArray(jsonData);
			for (int i = 0; i < array.length(); ++i) {

				JSONObject object = array.getJSONObject(i);
				
				T instance = clazz.newInstance();
				instance.initFromJSONObject(object);
				
				instances.add(instance);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return instances;
	}
	
	public ProtocolModel() {
		
	}
	
	public ProtocolModel(JSONObject jsonObject) {
		this.initFromJSONObject(jsonObject);
	}
	
	public boolean initFromJSONString(String jsonString) {
		boolean success = true;
		
		try {
			JSONObject object = new JSONObject(jsonString);
			this.initFromJSONObject(object);
		} catch (JSONException e) {
			success = false;
			e.printStackTrace();
		}
		
		return success;
	}
	
	public void initFromJSONObject(JSONObject object) {
		JSONArray names = object.names();
		for (int i = 0; i < names.length(); ++i) {
			try {
				
				String key = names.getString(i);
				if (!object.isNull(key)) {
					this.mapToClass(key, object.get(key));
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public abstract void mapToClass(String key, Object value);
	
}
