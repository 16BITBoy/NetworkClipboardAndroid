package com.adyrhan.networkclipboard;

public class PortIOError extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9181694902635992805L;

	public PortIOError(int port) {
		super("Cannot listen on port "+Integer.toString(port));
	}
}
