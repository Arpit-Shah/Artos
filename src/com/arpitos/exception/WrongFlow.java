package com.arpitos.exception;

/**
 * Checked Exception mainly used to highlight code is travelling through
 * wrong/unexpected flow
 */
public class WrongFlow extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7654109285223161620L;

	public WrongFlow(String message) {
		super(message);
	}
}
