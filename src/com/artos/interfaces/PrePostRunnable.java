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
package com.artos.interfaces;

import com.artos.framework.FWStaticStore;
import com.artos.framework.infra.TestContext;

/**
 * Used for classes which implements Pre Post methods for test cases
 */
public interface PrePostRunnable {

	/**
	 * Runs prior to each test case execution
	 * 
	 * @param context Test Context
	 * @throws Exception In case of pre execution failed
	 */
	default public void beforeTest(TestContext context) throws Exception {
		FWStaticStore.logDebug("beforeTest() default method executed");
	}

	/**
	 * Runs post each test case execution
	 * 
	 * @param context Test context
	 * @throws Exception In case of test of post execution failed
	 */
	default public void afterTest(TestContext context) throws Exception {
		FWStaticStore.logDebug("afterTest() default method executed");
	}

	/**
	 * Runs prior to test suite execution. Only run once
	 * 
	 * @param context Test context
	 * @throws Exception In case of initialisation failed
	 */
	default public void beforeTestSuite(TestContext context) throws Exception {
		FWStaticStore.logDebug("beforeTestSuite() default method executed");
	}

	/**
	 * Runs at the end of test suite execution. Only run once
	 * 
	 * @param context Test context
	 * @throws Exception In case of cleanup failed
	 */
	default public void afterTestSuite(TestContext context) throws Exception {
		FWStaticStore.logDebug("afterTestSuite() default method executed");
	}
}
