package com.joshdholtz.protocol.lib.models;

import java.util.Date;

import org.json.JSONObject;

import com.joshdholtz.protocol.lib.ProtocolModel;
import com.joshdholtz.protocol.lib.ProtocolModelFormats;
import com.joshdholtz.protocol.lib.ProtocolModelFormats.ModelMap;

public class MemberModel extends ProtocolModel {

	@ModelMap(key = "first_name", format = ProtocolModelFormats.FORMAT_STRING)
	private String firstName;
	
	@ModelMap(key = "last_name", format = ProtocolModelFormats.FORMAT_STRING)
	private String lastName;
	
	@ModelMap(key = "age", format = ProtocolModelFormats.FORMAT_INT)
	private int age;
	
	@ModelMap(key = "awesome_level", format = ProtocolModelFormats.FORMAT_DOUBLE)
	private double awesomeLevel;
	
	@ModelMap(key = "cool", format = ProtocolModelFormats.FORMAT_BOOLEAN)
	private boolean cool;
	
	@ModelMap(key = "dob", format = "date") 
	private Date birthday;
	
	public MemberModel() {
		
	}
	
	public MemberModel(JSONObject jsonObject) {
		
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

	/**
	 * @return the age
	 */
	public int getAge() {
		return age;
	}

	/**
	 * @return the awesomeLevel
	 */
	public double getAwesomeLevel() {
		return awesomeLevel;
	}

	/**
	 * @return the cool
	 */
	public boolean isCool() {
		return cool;
	}

	/**
	 * @return the birthday
	 */
	public Date getBirthday() {
		return birthday;
	}
	
}
