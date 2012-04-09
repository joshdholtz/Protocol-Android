package com.joshdholtz.protocol.lib;

import org.apache.http.HttpResponse;

public abstract class ProtocolResponse {
	public abstract void handleResponse(HttpResponse response, int status, String data);
}
