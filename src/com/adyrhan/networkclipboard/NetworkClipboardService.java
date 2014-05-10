package com.adyrhan.networkclipboard;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpService;

import com.adyrhan.networkclipboard.HttpServer.NewDataListener;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.ClipboardManager;
import android.util.Log;

public class NetworkClipboardService extends Service implements NewDataListener{

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
		super.onCreate();
		httpServer = new HttpServer();
		try {
			httpServer.startServer(40400, this);
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
