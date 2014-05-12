/*
 * $HeadURL: https://svn.apache.org/repos/asf/httpcomponents/httpcore/tags/4.0.1/httpcore/src/examples/org/apache/http/examples/ElementalHttpServer.java $
 * $Revision: 744516 $
 * $Date: 2009-02-14 17:38:14 +0100 (Sat, 14 Feb 2009) $
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package com.adyrhan.networkclipboard;

import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.http.ConnectionClosedException;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpServerConnection;
import org.apache.http.HttpStatus;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;
import org.apache.http.util.EntityUtils;

import com.adyrsoft.framework.android.InputStreamUtils;

import android.util.Log;

/**
 * Basic, yet fully functional and spec compliant, HTTP/1.1 file server.
 * <p>
 * Please note the purpose of this application is demonstrate the usage of HttpCore APIs.
 * It is NOT intended to demonstrate the most efficient way of building an HTTP file server. 
 * 
 *
 * @version $Revision: 744516 $
 */


public class HttpServer {
	private static final String TAG = "HttpServer";
	private AtomicBoolean mIsRunning;
	private RequestListenerThread mThread;
	private ServerSocket mSocket;
	
	public HttpServer() {
		mIsRunning = new AtomicBoolean(false);
	}
	
	public boolean isRunning() {
		return mIsRunning.get();
	}
	
	public void startServer(int port, NewDataListener listener) throws PortIOError {
		try {
			if(!mIsRunning.get()) {
				mThread = new RequestListenerThread(port, listener, mIsRunning);
				mSocket = mThread.serversocket;
				mThread.setDaemon(false);
				mThread.start();
				
				while(!mIsRunning.get()) {
					Thread.sleep(10);
				}
				
			}
		} catch (IOException e) {
			throw new PortIOError(port);
		} catch (InterruptedException e) {
			Log.e(TAG, null, e);
		}
		
	}
	
	public void stopServer() {
		try {
			Log.d(TAG, "HttpServer.stopServer() called! mIsRunning value is "+ Boolean.toString(mIsRunning.get()));
			if(mIsRunning.get() && mThread != null) {
				Log.d(TAG, "Trying to stop Http server...");
				mThread.interrupt();
				mSocket.close();	
				while(mIsRunning.get()) {
					Thread.sleep(10);
				}
			}
			
		} catch (IOException e) {
			Log.e(TAG, "Error while trying to close socket");
		} catch (InterruptedException e) {
			Log.e(TAG, null, e);
		}
	}
    
	public interface NewDataListener {
		public void onNewText(String text);
	}
	
    static class HttpClipboardHandler implements HttpRequestHandler  {
        
        private static final String TAG = "HttpClipboardHandler";
        private NewDataListener listener;
		public HttpClipboardHandler(NewDataListener listener) {
			if(listener == null) {
				throw new NullPointerException("NewDataListener cannot be null");
			}
			this.listener = listener;
		}

		public void handle(
                final HttpRequest request, 
                final HttpResponse response,
                final HttpContext context) throws HttpException, IOException {

            String method = request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);
            if (!method.equals("POST")) {
                throw new MethodNotSupportedException(method + " method not supported"); 
            }
            String target = request.getRequestLine().getUri();
            
            if (request instanceof HttpEntityEnclosingRequest) {
                HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();
                String content = InputStreamUtils.toString(entity.getContent());
                String[] nameValue = content.split("\\s*=\\s*");
                if(nameValue.length == 2){
                	listener.onNewText(nameValue[1]);
                } else {
                	response.setStatusCode(422);
                }
            }
            
        }
        
    }
    
    static class RequestListenerThread extends Thread {

        private static final String TAG = "RequestListenerThread";
		public final ServerSocket serversocket;
        private final HttpParams params; 
        private final HttpService httpService;
        private final AtomicBoolean runningStatus;
        
        
        public RequestListenerThread(int port, NewDataListener listener, AtomicBoolean runningStatus) throws IOException  {
            this.serversocket = new ServerSocket(port);
            this.params = new BasicHttpParams();
            this.params
                .setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 5000)
                .setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, 8 * 1024)
                .setBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, false)
                .setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true)
                .setParameter(CoreProtocolPNames.ORIGIN_SERVER, "HttpComponents/1.1");

            // Set up the HTTP protocol processor
            BasicHttpProcessor httpproc = new BasicHttpProcessor();
            httpproc.addInterceptor(new ResponseDate());
            httpproc.addInterceptor(new ResponseServer());
            httpproc.addInterceptor(new ResponseContent());
            httpproc.addInterceptor(new ResponseConnControl());
            
            // Set up request handlers
            HttpRequestHandlerRegistry registry = new HttpRequestHandlerRegistry();
            registry.register("*", new HttpClipboardHandler(listener));
            
            // Set up the HTTP service
            this.httpService = new HttpService(
                    httpproc, 
                    new DefaultConnectionReuseStrategy(), 
                    new DefaultHttpResponseFactory());
            this.httpService.setParams(this.params);
            this.httpService.setHandlerResolver(registry);
            
            this.runningStatus = runningStatus;
        }
        
        public void run() {
            Log.d(TAG, "Listening on port " + this.serversocket.getLocalPort());
            runningStatus.set(true);
            while (!Thread.interrupted()) {
                try {
                    // Set up HTTP connection
                    Socket socket = this.serversocket.accept();
                    DefaultHttpServerConnection conn = new DefaultHttpServerConnection();
                    Log.d(TAG, "Incoming connection from " + socket.getInetAddress());
                    conn.bind(socket, this.params);

                    // Start worker thread
                    Thread t = new WorkerThread(this.httpService, conn);
                    t.setDaemon(true);
                    t.start();
                }  catch (IOException e) {
                    Log.w(TAG, "I/O error initialising connection thread: " 
                            + e.getMessage());
                    break;
                }
            }
            
            runningStatus.set(false);
            
        }
    }
    
    static class WorkerThread extends Thread {
    	private static final String TAG = "WorkerThread";
        private final HttpService httpservice;
        private final HttpServerConnection conn;
        
        public WorkerThread(
                final HttpService httpservice, 
                final HttpServerConnection conn) {
            super();
            this.httpservice = httpservice;
            this.conn = conn;
        }
        
        public void run() {
            Log.d(TAG, "New connection thread");
            HttpContext context = new BasicHttpContext(null);
            try {
                while (!Thread.interrupted() && this.conn.isOpen()) {
                    this.httpservice.handleRequest(this.conn, context);
                }
            } catch (ConnectionClosedException ex) {
                Log.w(TAG, "Client closed connection");
            } catch (IOException ex) {
                Log.e(TAG, "I/O error: " + ex.getMessage());
            } catch (HttpException ex) {
                Log.e(TAG, "Unrecoverable HTTP protocol violation: " + ex.getMessage());
            } finally {
                try {
                    this.conn.shutdown();
                } catch (IOException ignore) {}
            }
        }

    }
    
}