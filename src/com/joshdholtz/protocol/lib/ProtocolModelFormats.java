package com.joshdholtz.protocol.lib;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;
import android.widget.Toast;

import com.joshdholtz.protocol.lib.ProtocolClient.ProtocolStatusListener;
import com.joshdholtz.protocol.lib.helpers.ProtocolConstants;
import com.joshdholtz.protocol.lib.requests.JSONRequestData;
import com.joshdholtz.protocol.lib.requests.ParamsRequestData;
import com.joshdholtz.protocol.lib.responses.JSONResponseHandler;
import com.joshdholtz.protocol.lib.responses.ProtocolResponseHandler;


public class ProtocolModelFormats extends ProtocolClient {

	public final static String FORMAT_STRING = "string";
	public final static String FORMAT_DOUBLE = "double";
	public final static String FORMAT_INT = "int";
	public final static String FORMAT_BOOLEAN = "boolean";
	
	private Map<String, MapFormat> formats;
	
	@Retention(RetentionPolicy.RUNTIME)
	public @interface ModelMap {
	    String key() default "";
	    String format() default "default";
	}
	
	public static abstract class MapFormat {
		public abstract Object format(Object value);
	}
	
	private ProtocolModelFormats() {
		super();
		formats = new HashMap<String, MapFormat>();
		
		formats.put(FORMAT_STRING, new MapFormat() {

			@Override
			public Object format(Object value) {
				return value.toString();
			}
			
		});
		
		formats.put(FORMAT_DOUBLE, new MapFormat() {

			@Override
			public Object format(Object value) {
				return Double.parseDouble(value.toString());
			}
			
		});
		
		formats.put(FORMAT_INT, new MapFormat() {

			@Override
			public Object format(Object value) {
				return Integer.parseInt(value.toString());
			}
			
		});
		
		formats.put(FORMAT_BOOLEAN, new MapFormat() {

			@Override
			public Object format(Object value) {
				return Boolean.parseBoolean(value.toString());
			}
			
		});
		
	}
	
	private static ProtocolModelFormats getInstance() {
		return LazyHolder.instance;
	}

	private static class LazyHolder {
		private static ProtocolModelFormats instance = new ProtocolModelFormats();
	}
	
	public static void set(String format, MapFormat mapFormat) {
		ProtocolModelFormats.getInstance().formats.put(format, mapFormat);
	}
	
	public static Object get(String format, Object value) {
		Log.d(ProtocolConstants.LOG_TAG, "Trying to get - " + format);
		return ProtocolModelFormats.getInstance().formats.get(format).format(value);
	}
	
}
