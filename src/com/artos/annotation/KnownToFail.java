/*******************************************************************************
 * Copyright (C) 2018-2019 Arpit Shah and Artos Contributors
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
 * Annotation {@code KnownToFail} can be used to enable KTF check, if enabled then test case must fail with {@code TestStatus} = KTF status, otherwise
 * test case will be marked as failed. User may provide optional parameter using bugRef, which allows user to specify any bugRef/Ticket which can be
 * printed in the report.
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
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface KnownToFail {

	/**
	 * Check which can be enabled to ensure that test case annotated with {@code KnownToFail} fails with {@code TestStatus} = KTF status.
	 * 
	 * @return true = test case outcome must be {@code TestStatus} = KTF otherwise test case will be marked as fail|false = test case outcome can be
	 *         anything.
	 */
	boolean ktf() default false;

	/**
	 * bug reference number
	 * 
	 * @return bugRefNumber
	 */
	String bugref() default "";
}
