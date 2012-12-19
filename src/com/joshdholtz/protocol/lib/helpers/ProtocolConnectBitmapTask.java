package com.joshdholtz.protocol.lib.helpers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;

import com.joshdholtz.protocol.lib.ProtocolClient;
import com.joshdholtz.protocol.lib.ProtocolBitmapResponse;
import com.joshdholtz.protocol.lib.helpers.ProtocolConnectTask.ConnectTimerTask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

public class ProtocolConnectBitmapTask extends AsyncTask<Void, Void, Bitmap> {

	private String url;
	private String imageViewTag;
	private int timeout;
	private Timer timer;
	
	private ProtocolBitmapResponse responseHandler;
	
	public ProtocolConnectBitmapTask(String url, String imageViewTag, int timeout, ProtocolBitmapResponse responseHandler) {
		this.url = url;
		this.imageViewTag = imageViewTag;
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
		
//		if (Protocol.getInstance().getBitmapCache() != null) {
//			if (Protocol.getInstance().getBitmapCache().containsKey(url)) {
//				Log.d(ProtocolConstants.LOG_TAG, "Loading image from cache - " + url);
//				return Protocol.getInstance().getBitmapCache().getCachedBitmap(url);
//			} else {
//				Log.d(ProtocolConstants.LOG_TAG, "Image from cache - " + url);
//			}
//		}
		
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
		
//		if (Protocol.getInstance().getBitmapCache() != null) {
//			Protocol.getInstance().getBitmapCache().addBitmapToCache(url, bitmap);
//		}
		
		return bitmap;
	}
	
	@Override
	protected void onPostExecute(Bitmap bitmap) {
		timer.cancel();
		
		ProtocolClient.getInstance().finishedProtocolConnectTask();
		
		if (this.isCancelled() || bitmap == null) {
			if (ProtocolClient.getInstance().isDebug()) {
				Log.d(ProtocolConstants.LOG_TAG, "Bitmap - not retrieved");
			}
			responseHandler.handleResponse(imageViewTag, null);
		} else {
			if (ProtocolClient.getInstance().isDebug()) {
				Log.d(ProtocolConstants.LOG_TAG, "Bitmap - retrieved");
			}
			this.responseHandler.handleResponse(imageViewTag, bitmap);
		}
			
	}
	
	class ConnectTimerTask extends TimerTask {

		@Override
		public void run() {
			ProtocolConnectBitmapTask.this.cancel(true);
			responseHandler.handleResponse(imageViewTag, null);
		}
		
	}

}
