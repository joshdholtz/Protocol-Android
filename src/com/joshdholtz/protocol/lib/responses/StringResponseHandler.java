package com.joshdholtz.protocol.lib.responses;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import org.apache.http.HttpResponse;

public abstract class StringResponseHandler extends ProtocolResponseHandler {
	
	private String stringResponse;
	
	@Override
	public void handleResponse(HttpResponse response, int status, byte[] data) {
		Charset charsetInput = Charset.forName("UTF-8");
		CharsetDecoder decoder = charsetInput.newDecoder();
		CharBuffer cbuf = null;
		try {
			cbuf = decoder.decode(ByteBuffer.wrap(data));
			stringResponse = cbuf.toString();
		} catch (CharacterCodingException e) {
			e.printStackTrace();
		}
		
		handleResponse(stringResponse);
	}
	
	public abstract void handleResponse(String stringResponse);

	/**
	 * @return the stringResponse
	 */
	public String getStringResponse() {
		return stringResponse;
	}
	
}
