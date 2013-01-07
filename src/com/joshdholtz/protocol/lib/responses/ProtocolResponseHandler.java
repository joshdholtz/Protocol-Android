package com.joshdholtz.protocol.lib.responses;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import org.apache.http.HttpResponse;

public abstract class ProtocolResponseHandler {
	
	private HttpResponse response;
	private int status;
	private byte[] data;
	
	public void init(HttpResponse response, int status, byte[] data) {
		this.response = response;
		this.status = status;
		this.data = data;
	}
	
	/**
	 * @return the response
	 */
	public HttpResponse getResponse() {
		return response;
	}

	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @return the data
	 */
	public byte[] getData() {
		return data;
	}
	
	@Override
	public String toString() {
		String stringResponse = "";
		
		Charset charsetInput = Charset.forName("UTF-8");
		CharsetDecoder decoder = charsetInput.newDecoder();
		CharBuffer cbuf = null;
		try {
			cbuf = decoder.decode(ByteBuffer.wrap(data));
			stringResponse = cbuf.toString();
		} catch (CharacterCodingException e) {
			e.printStackTrace();
		}
		
		return stringResponse;
	}

	public abstract void handleResponse(HttpResponse response, int status, byte[] data);
	
}
