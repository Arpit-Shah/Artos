/*******************************************************************************
 * Copyright (C) 2018-2019 Arpit Shah
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
 * Mandatory Annotation {@code TestCase} defines class to be a test case. User can provide optional parameters test sequence number
 * {@code sequence()}, skip attribute and label. All of the optional parameters will be ignored when user runs test case via test script or via
 * provided test list.
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
public @interface TestCase {

	/**
	 * Responsible for marking test to be skipped if set true. Ignored if test list is provided via test script or using main() class.
	 * 
	 * @return true = skip test|false = execute test
	 */
	boolean skip() default false;

	/**
	 * Defines sequence in which test case should execute. Ignored if test list is provided via test script or using main() class.
	 * 
	 * @return test sequence number
	 */
	int sequence() default 1;

	/**
	 * Labels for each test cases
	 * 
	 * @return test case label
	 */
	String[] label() default { "all" };

	/**
	 * Data provider name set insider {@code DataProvider} annotation
	 * 
	 * @return data provider method name
	 */
	String dataprovider() default "";

	/**
	 * Test Timeout, If test execution did not finish within this time then test will be marked as fail
	 * 
	 * @return test timeout in milliseconds
	 */
	long testtimeout() default 0;
}
