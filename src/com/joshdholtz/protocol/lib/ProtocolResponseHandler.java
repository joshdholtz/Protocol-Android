package com.joshdholtz.protocol.lib;

import com.joshdholtz.protocol.lib.responses.ProtocolResponseData;

public abstract class ProtocolResponseHandler<T extends ProtocolResponseData> {
	
	public abstract void handleResponse(T responseData);
	
}
