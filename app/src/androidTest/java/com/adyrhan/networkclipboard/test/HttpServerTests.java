package com.adyrhan.networkclipboard.test;

import com.adyrhan.networkclipboard.HttpServer;
import com.adyrhan.networkclipboard.PortIOError;

import android.test.AndroidTestCase;

public class HttpServerTests extends AndroidTestCase {
	
	
	public void testStartAndShutdown() throws PortIOError, InterruptedException{
		HttpServer server = new HttpServer();
		server.startServer(35035, null);
		assertEquals(true, server.isRunning());
		server.stopServer();
		assertEquals(false, server.isRunning());
		server.startServer(35035, null);
		assertEquals(true, server.isRunning());
		server.stopServer();
		assertEquals(false, server.isRunning());
		server.startServer(35035, null);
		assertEquals(true, server.isRunning());
		server.stopServer();
		assertEquals(false, server.isRunning());
		server.startServer(35035, null);
		assertEquals(true, server.isRunning());
		server.stopServer();
		assertEquals(false, server.isRunning());
	}
}
