package com.joshdholtz.protocol.lib;

import android.graphics.Bitmap;

public abstract class ProtocolBitmapCache {
	
	public abstract Bitmap getCachedBitmap(String key);
	public abstract void addBitmapToCache(String key, Bitmap bitmap);
	public abstract boolean containsKey(String key);
	
}
