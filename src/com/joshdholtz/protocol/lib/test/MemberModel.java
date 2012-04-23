package com.joshdholtz.protocol.lib.test;

import org.json.JSONObject;

import com.joshdholtz.protocol.lib.ProtocolModel;

public class MemberModel extends ProtocolModel {

	public int id;
	public String firstName, lastName;

	public MemberModel() {
		super();
	}
	
	public MemberModel(JSONObject jsonObject) {
		super(jsonObject);
	}

	@Override
	public void mapToClass(String key, Object value) {
		if (key.equals("id")) {
			this.id = (Integer) value;
		} else if (key.equals("first_name")) {
			this.firstName = (String) value;
		} else if (key.equals("last_name")) {
			this.lastName = (String) value;
		}
	}

}
