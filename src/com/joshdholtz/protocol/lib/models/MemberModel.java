package com.joshdholtz.protocol.lib.models;

import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import com.joshdholtz.protocol.lib.ProtocolModel;
import com.joshdholtz.protocol.lib.ProtocolModelFormats;
import com.joshdholtz.protocol.lib.ProtocolModelFormats.MapConfig;
import com.joshdholtz.protocol.lib.ProtocolModelFormats.MapModelConfig;

public class MemberModel extends ProtocolModel {

	@MapConfig(key = "first_name", format = ProtocolModelFormats.FORMAT_STRING)
	public String firstName;
	
	@MapConfig(key = "last_name", format = ProtocolModelFormats.FORMAT_STRING)
	public String lastName;
	
	@MapConfig(key = "age", format = ProtocolModelFormats.FORMAT_INT)
	public int age;
	
	@MapConfig(key = "awesome_level", format = ProtocolModelFormats.FORMAT_DOUBLE)
	public double awesomeLevel;
	
	@MapConfig(key = "cool", format = ProtocolModelFormats.FORMAT_BOOLEAN)
	public boolean cool;
	
	@MapConfig(key = "dob", format = "date") 
	public Date birthday;
	
	@MapModelConfig(key = "friend", modelClass = MemberModel.class) 
	public MemberModel friend;
	
	@MapModelConfig(key = "friends", modelClass = MemberModel.class) 
	public List<MemberModel> friends;
	
}
