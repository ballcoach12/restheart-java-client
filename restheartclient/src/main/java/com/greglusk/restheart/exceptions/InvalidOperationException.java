package com.greglusk.restheart.exceptions;

/**
 * An exception that is thrown when a method is called that is unsupported.
 * @author Greg Lusk
 *
 */
public class InvalidOperationException extends Exception {

	public InvalidOperationException(String msg) {
		super(msg);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
