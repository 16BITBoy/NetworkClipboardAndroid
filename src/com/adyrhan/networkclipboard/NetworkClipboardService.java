package com.adyrhan.networkclipboard;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;

import com.adyrhan.networkclipboard.HttpServer.NewDataListener;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.ClipboardManager;
import android.util.Log;

@SuppressWarnings("deprecation")
public class NetworkClipboardService extends Service implements NewDataListener{

	public static final int LISTENING_PORT = 40400;
	private static final String TAG = "NetworkClipboardService";
	public static boolean isRunning;
	private HttpServer httpServer;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		try {
			super.onCreate();
			httpServer = new HttpServer();
			httpServer.startServer(LISTENING_PORT, this);
			NetworkClipboardService.isRunning = true;
		} catch (PortIOError e) {
			NetworkClipboardService.isRunning = false;
			Log.e(TAG, null, e);
		}
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		return super.onStartCommand(intent, flags, startId);
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "Service is stopping...");
		httpServer.stopServer();
		NetworkClipboardService.isRunning = false;
	}

	@Override
	public void onNewText(String text) {
		try {
			ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
			clipboard.setText(URLDecoder.decode(text, Charset.defaultCharset().name()));
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, null, e);
		}
	}

}
