package com.joshdholtz.protocol.lib.helpers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpResponse;

import com.joshdholtz.protocol.lib.ProtocolBitmapResponse;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

public class ProtocolConnectBitmapTask extends AsyncTask<Void, Void, Bitmap> {

	String url;
	
	ProtocolBitmapResponse responseHandler;
	
	public ProtocolConnectBitmapTask(String url, ProtocolBitmapResponse responseHandler) {
		this.url = url;
		this.responseHandler = responseHandler;
	}
	
	@Override
	protected Bitmap doInBackground(Void... arg0) {
		URL newurl = null;
		Bitmap bitmap = null;
		try {
			newurl = new URL(this.url);
			bitmap = BitmapFactory.decodeStream(newurl.openConnection() .getInputStream());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}
	
	@Override
	protected void onPostExecute(Bitmap bitmap) {
		
		this.responseHandler.handleResponse(bitmap);
		
	}

}
