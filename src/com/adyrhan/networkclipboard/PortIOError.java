package com.adyrhan.networkclipboard;

public class PortIOError extends Exception {
	public PortIOError(int port) {
		super("Cannot listen on port "+Integer.toString(port));
	}
}
