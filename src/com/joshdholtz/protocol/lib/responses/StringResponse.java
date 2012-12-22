package com.joshdholtz.protocol.lib.responses;

import java.io.UnsupportedEncodingException;

public class StringResponse extends ProtocolResponseData {

	private String string;
	
	/**
	 * @return the string
	 */
	public String getString() {
		return string;
	}

	@Override
	public void generateResponse() {
		try {
			string = new String(this.getData(), "UTF8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

}
