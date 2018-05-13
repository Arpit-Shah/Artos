package com.arpitos.exception;

/**
 * Checked Exception used mainly for Guardian class. This Exception can be used
 * if Values are not as expected
 * 
 * @author arpit
 *
 */
public class ValueNotAsExpected extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3143300873685133606L;

	public ValueNotAsExpected(String message) {
		super(message);
	}

}
