package com.joshdholtz.protocol.lib;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.joshdholtz.protocol.lib.ProtocolModelFormats.MapConfig;
import com.joshdholtz.protocol.lib.ProtocolModelFormats.MapModelConfig;
import com.joshdholtz.protocol.lib.helpers.ProtocolConstants;

import android.util.Log;

public abstract class ProtocolModel {
	
	private static boolean debug = false;
	
	public static void setDebug(boolean debug) {
		ProtocolModel.debug = debug;
	}
	
	public static <T extends ProtocolModel>T createModel(Class<T> clazz, String jsonData) {
		T instance = null;
		
		try {
			instance = clazz.newInstance();
			instance.initFromJSONString(jsonData);
		} catch (Exception e) {
			if (debug) {
				Log.e(ProtocolConstants.LOG_TAG,  "Could not create " + clazz.getCanonicalName() + " from " + jsonData, e);
			}
		}
		
		return instance;
	}
	
	public static <T extends ProtocolModel>T createModel(Class<T> clazz, JSONObject object) {
		T instance = null;
		try {
			instance = clazz.newInstance();
			instance.initFromJSONObject(object);
		} catch (Exception e) {
			if (debug) {
				Log.e(ProtocolConstants.LOG_TAG,  "Could not create " + clazz.getCanonicalName() + " from " + object, e);
			}
		}
		
		return instance;
	}
	
	public static <T extends ProtocolModel>List<T> createModels(Class<T> clazz, String jsonData) {
		List<T> instances = new ArrayList<T>();
		try {
			JSONArray array = new JSONArray(jsonData);
			return ProtocolModel.createModels(clazz, array);
		} catch (Exception e) {
			if (debug) {
				Log.e(ProtocolConstants.LOG_TAG,  "Could not create " + clazz.getCanonicalName() + "s - " + e.getClass() + " " + e.getMessage(), e);
				Log.e(ProtocolConstants.LOG_TAG,  "Could not create " + clazz.getCanonicalName() + "s from " + jsonData, e);
			}
		}
		return instances;
	}
	
	public static <T extends ProtocolModel>List<T> createModels(Class<T> clazz, JSONArray array) {
		List<T> instances = new ArrayList<T>();
		try {
			for (int i = 0; i < array.length(); ++i) {

				JSONObject object = array.getJSONObject(i);
				
				T instance = clazz.newInstance();
				boolean success = instance.initFromJSONObject(object);
				
				if (success) {
					instances.add(instance);
				} else {
					Log.w(ProtocolConstants.LOG_TAG, "Could not create " + clazz.getCanonicalName() + " from " + object.toString());
				}
			}
		} catch (Exception e) {
			if (debug) {
				Log.e(ProtocolConstants.LOG_TAG,  "Could not create " + clazz.getCanonicalName() + "s - " + e.getClass() + " " + e.getMessage(), e);
			}
		}
		
		return instances;
	}
	
	public ProtocolModel() {
		
	}
	
	public ProtocolModel(String jsonString) {
		this.initFromJSONString(jsonString);
	}
	
	public ProtocolModel(JSONObject jsonObject) {
		this.initFromJSONObject(jsonObject);
	}
	
	public boolean initFromJSONString(String jsonString) {
		boolean success = true;
		
		try {
			JSONObject object = new JSONObject(jsonString);
			success = this.initFromJSONObject(object);
		} catch (JSONException e) {
			success = false;
			if (debug) {
				Log.e(ProtocolConstants.LOG_TAG, "Error parsing JSON string", e);
			}
		}
		
		return success;
	}
	
	public boolean initFromJSONObject(JSONObject object) {
		boolean success = true;
		
		for(Field field : this.getClass().getDeclaredFields()){
			 String name = field.getName();
			 
			 Annotation[] annotations = field.getAnnotations();
			 
			 if (field.isAnnotationPresent(MapModelConfig.class)) {
				 MapModelConfig map = field.getAnnotation(MapModelConfig.class);
				 try {
					 if (object.get(map.key()) != null) {
						field.setAccessible(true);
						if (map.modelClass() != null && ProtocolModel.class.isAssignableFrom(map.modelClass())) {
							Object json = new JSONTokener(object.get(map.key()).toString()).nextValue();
							
							if (json instanceof JSONObject) {
								Object val = ProtocolModel.createModel(map.modelClass(), (JSONObject) json);
								if (val != null) {
									field.set(this, val);
								}
							} else if (json instanceof JSONArray) {
								Object val = ProtocolModel.createModels(map.modelClass(), (JSONArray) json);
								if (val != null) {
										field.set(this, val);
								}
							}
						}
					 }
				} catch (IllegalArgumentException e) {
					if (debug) {
						Log.e(ProtocolConstants.LOG_TAG, "Error setting field " + name, e);
					}
				} catch (IllegalAccessException e) {
					if (debug) {
						Log.e(ProtocolConstants.LOG_TAG, "Error setting field " + name, e);
					}
				} catch (JSONException e) {
					if (debug) {
						Log.e(ProtocolConstants.LOG_TAG, "Error setting field " + name, e);
					}
				} catch (Exception e) {
					if (debug) {
						Log.e(ProtocolConstants.LOG_TAG, "Error setting field " + name, e);
					}
				}
			 } else if (field.isAnnotationPresent(MapConfig.class)) {
				MapConfig map = field.getAnnotation(MapConfig.class);
				try {
					if (object.get(map.key()) != null) {
						field.setAccessible(true);
						if (map.format().equals("default")) {
							field.set(this, object.get(map.key()));
						} else {
							Object val = ProtocolModelFormats.get(map.format(), object.get(map.key()));
							if (val != null) {
								field.set(this, val);
							}
						}
					}
				} catch (IllegalArgumentException e) {
					if (debug) {
						Log.e(ProtocolConstants.LOG_TAG, "Error setting field " + name, e);
					}
				} catch (IllegalAccessException e) {
					if (debug) {
						Log.e(ProtocolConstants.LOG_TAG, "Error setting field " + name, e);
					}
				} catch (JSONException e) {
					if (debug) {
						Log.e(ProtocolConstants.LOG_TAG, "Error setting field " + name, e);
					}
				} catch (Exception e) {
					if (debug) {
						Log.e(ProtocolConstants.LOG_TAG, "Error setting field " + name, e);
					}
				}
			 }
		}
		
		JSONArray names = object.names();
		for (int i = 0; i < names.length(); ++i) {
			try {

				String key = names.getString(i);
				if (!object.isNull(key)) {
					this.mapToClass(key, object.get(key));
				}
				
			} catch (JSONException e) {
				success = false;
				if (debug) {
					Log.e(ProtocolConstants.LOG_TAG, "Error with legacy map to class");
				}
			}
		}
		
		return success;
	}
	
	public void mapToClass(String key, Object value) {
	}
	
}
