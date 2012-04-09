package com.joshdholtz.protocol.lib.test;

import org.json.JSONObject;

import android.util.Log;

import com.joshdholtz.protocol.lib.ProtocolModel;

public class MemberModel extends ProtocolModel {
	
	public int id;
	public String firstName, lastName;

	public MemberModel(JSONObject jsonObject) {
		super(jsonObject);
	}

	@Override
	public void mapToClass(String key, Object value) {
		Log.d("Protocol", "Trying to map - " + key + " for " + value.toString());
		if (key.equals("id")) {
			this.id = (Integer) value;
		} else if (key.equals("first_name")) {
			this.firstName = (String) value;
		} else if (key.equals("last_name")) {
			this.lastName = (String) value;
		}
	}

}
