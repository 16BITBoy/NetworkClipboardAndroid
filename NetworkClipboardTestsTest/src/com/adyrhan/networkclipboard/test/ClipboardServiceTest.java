package com.adyrhan.networkclipboard.test;


import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import com.adyrhan.networkclipboard.NetworkClipboardService;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.http.AndroidHttpClient;
import android.text.ClipboardManager;
import android.test.ServiceTestCase;

@SuppressWarnings("deprecation")
public class ClipboardServiceTest extends ServiceTestCase<NetworkClipboardService> {
	private static final String SERVER_ADDRESS = "http://127.0.0.1:40400";
	public ClipboardServiceTest() {
		super(NetworkClipboardService.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}
	
	public void testClipboardServer() throws IOException {
		ClipboardManager clipboard = (ClipboardManager) getSystemContext().getSystemService(Context.CLIPBOARD_SERVICE);
		
		startService(new Intent(getSystemContext(), com.adyrhan.networkclipboard.NetworkClipboardService.class));
		assertTrue(NetworkClipboardService.isRunning);
		
		AndroidHttpClient httpClient = AndroidHttpClient.newInstance("tester");
		
		String text = "Some text";
		HttpPost request = new HttpPost(SERVER_ADDRESS);
		ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("content", text));
		request.setEntity(new UrlEncodedFormEntity(pairs));
		HttpResponse response = httpClient.execute(request);
		assertEquals(200, response.getStatusLine().getStatusCode());
		assertEquals(text, clipboard.getText());
		
		// Server doesn't accept empty text requests
		text = "";
		request = new HttpPost(SERVER_ADDRESS);
		pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("content", text));
		request.setEntity(new UrlEncodedFormEntity(pairs));
		response = httpClient.execute(request);
		assertEquals(422, response.getStatusLine().getStatusCode());

		
	}

	
	
}
