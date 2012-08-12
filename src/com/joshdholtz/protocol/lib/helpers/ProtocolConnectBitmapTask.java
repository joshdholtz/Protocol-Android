package com.joshdholtz.protocol.lib.helpers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;

import com.joshdholtz.protocol.lib.Protocol;
import com.joshdholtz.protocol.lib.ProtocolBitmapResponse;
import com.joshdholtz.protocol.lib.helpers.ProtocolConnectTask.ConnectTimerTask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

public class ProtocolConnectBitmapTask extends AsyncTask<Void, Void, Bitmap> {

	private String url;
	private int timeout;
	private Timer timer;
	
	private ProtocolBitmapResponse responseHandler;
	
	public ProtocolConnectBitmapTask(String url, int timeout, ProtocolBitmapResponse responseHandler) {
		this.url = url;
		this.timeout = timeout;
		this.responseHandler = responseHandler;
	}
	
	@Override
	protected void onPreExecute() {
		timer = new Timer();
		timer.schedule(new ConnectTimerTask(), timeout);
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
		timer.cancel();
		
		Protocol.getInstance().finishedProtocolConnectTask();
		
		if (this.isCancelled() || bitmap == null) {
			if (Protocol.getInstance().isDebug()) {
				Log.d(ProtocolConstants.LOG_TAG, "Bitmap - not retrieved");
			}
			responseHandler.handleResponse(null);
		} else {
			if (Protocol.getInstance().isDebug()) {
				Log.d(ProtocolConstants.LOG_TAG, "Bitmap - retrieved");
			}
			this.responseHandler.handleResponse(bitmap);
		}
			
	}
	
	class ConnectTimerTask extends TimerTask {

		@Override
		public void run() {
			ProtocolConnectBitmapTask.this.cancel(true);
			responseHandler.handleResponse(null);
		}
		
	}

}
