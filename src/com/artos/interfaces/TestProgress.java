// Copyright <2018> <Artos>

// Permission is hereby granted, free of charge, to any person obtaining a copy of this software
// and associated documentation files (the "Software"), to deal in the Software without restriction,
// including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
// and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
// subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
// 
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
// INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
// IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
// WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
// OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
package com.artos.interfaces;

import com.artos.framework.Enums.TestStatus;
import com.artos.framework.TestObjectWrapper;

/**
 * Interface recommended for any listener which requires to be notified test
 * progress events.
 */
public interface TestProgress {

	/**
	 * Method is called when test loop execution starts, This method is called
	 * same number of time as loop count set by user.
	 * 
	 * @param count
	 *            number of test suite execution loop count
	 */
	default void testExecutionLoopCount(int count) {
	}

	/**
	 * Method is called when test suite execution starts
	 * 
	 * @param description
	 *            description/name of the test suite
	 */
	default void testSuiteExecutionStarted(String description) {
	}

	/**
	 * Method is called when test suite execution finishes
	 * 
	 * @param description
	 *            description/name of the test suite
	 */
	default void testSuiteExecutionFinished(String description) {
	}

	/**
	 * Method is called when test execution starts
	 * 
	 * @param t
	 *            test object wrapper
	 * @see TestObjectWrapper
	 */
	default void testExecutionStarted(TestObjectWrapper t) {
	}

	/**
	 * Method is called when test execution finishes
	 * 
	 * @param t
	 *            test object wrapper
	 * @see TestObjectWrapper
	 */
	default void testExecutionFinished(TestObjectWrapper t) {
	}

	/**
	 * Method is called when test execution is skipped.
	 * 
	 * @param t
	 *            test object wrapper
	 * @see TestObjectWrapper
	 */
	default void testExecutionSkipped(TestObjectWrapper t) {
	}

	/**
	 * Method is called when user updates test status.
	 * 
	 * @param testStatus
	 *            Test status set by user
	 * @param msg
	 *            description/reason provided by user
	 * @see TestStatus
	 */
	default void testStatusUpdate(TestStatus testStatus, String msg) {
	}

	/**
	 * Method is called when test result is finalised.
	 * 
	 * @param testStatus
	 *            Test status set by user
	 * @param description
	 *            bug tracking info (Ticket/JIRA number)
	 * @see TestStatus
	 */
	default void testResult(TestStatus testStatus, String description) {
	}

}
