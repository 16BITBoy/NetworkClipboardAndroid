package com.adyrsoft.framework.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.util.ByteArrayBuffer;

public final class InputStreamUtils {
	private InputStreamUtils() {};
	
	public static String toString(InputStream is) throws IOException {
	    int char_ = 0;
	    ByteArrayBuffer total = new ByteArrayBuffer(0);
	    
	    // Wrap a BufferedReader around the InputStream
	    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
	    

	    // Read response until the end
	    while ((char_ = rd.read()) != -1) { 
	        total.append(char_); 
	    }
	    
	    
	    // Return full string
	    String output = new String(total.toByteArray());
	    return output;
	}
}
