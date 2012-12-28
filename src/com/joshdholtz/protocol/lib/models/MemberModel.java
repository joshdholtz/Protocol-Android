package com.joshdholtz.protocol.lib.models;

import org.json.JSONObject;

import com.joshdholtz.protocol.lib.ProtocolModel;

public class MemberModel extends ProtocolModel {

	private String firstName;
	private String lastName;
	
	public MemberModel() {
		
	}
	
	public MemberModel(JSONObject jsonObject) {
		
	}
	
	@Override
	public void mapToClass(String key, Object value) {
		if (key.equals("first_name")) {
			firstName = (String)value;
		} else if (key.equals("last_name")) {
			lastName = (String)value;
		}
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

}
