/*******************************************************************************
 * Copyright (C) 2018 Arpit Shah
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package com.artos.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * Annotation {@code ExpectedException} can be used to define expected exception during test case execution. Specified exception will remain in scope
 * of test case where annotation {@code ExpectedException} is defined. User can optionally provide regular expression which can be used to match
 * exception message.
 * 
 * <p>
 * Annotation {@code RetentionPolicy.RUNTIME} is recorded in the class file by the compiler and retained by the VM at run time, so it may be read
 * reflectively.
 * </p>
 *
 */
// Make the annotation available at runtime:
@Retention(RetentionPolicy.RUNTIME)
// Allow to use only on types:
@Target(ElementType.TYPE)
public @interface ExpectedException {

	/**
	 * Mandatory argument which defines expected {@code Throwable}(s) or {@code Exception}(s)
	 * 
	 * @return array of {@code Throwable} or {@code Exception} class
	 */
	Class<? extends Throwable>[] expectedExceptions();

	/**
	 * Optional Exception Description, Accepts Regular expression
	 * 
	 * @return regular expression designed by user to match exception message
	 */
	String contains() default "";

	/**
	 * if enforce = true exception did not occur then test case will be marked failed.
	 * 
	 * @return true if enforced, false if not enforced
	 */
	boolean enforce() default true;
}
