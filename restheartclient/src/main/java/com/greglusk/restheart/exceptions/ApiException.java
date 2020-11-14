package com.greglusk.restheart.exceptions;

/**
 * A generic exception that is thrown as a last resort.
 * 
 * An HTTP response code 500 usually generates this exception.
 * @author Greg Lusk
 *
 */
public class ApiException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ApiException(String msg) {
		super(msg);
	}

}
