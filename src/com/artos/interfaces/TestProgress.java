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

import com.artos.framework.Enums.TestStatus;
import com.artos.framework.TestObjectWrapper;

/**
 * Interface recommended for any listener which requires to be notified test progress events.
 */
public interface TestProgress {

	/**
	 * Method is called when test loop execution starts, This method is called same number of time as loop count set by user.
	 * 
	 * @param count number of test suite execution loop count
	 */
	public void testExecutionLoopCount(int count);

	/**
	 * Method is called before {@code BeforeTestSuite} method execution starts
	 * 
	 * @param description description/name of the test suite
	 */
	public void beforeTestSuiteMethodStarted(String description);

	/**
	 * Method is called after {@code BeforeTestSuite} method execution finished
	 * 
	 * @param description description/name of the test suite
	 */
	public void beforeTestSuiteMethodFinished(String description);

	/**
	 * Method is called when test suite execution starts
	 * 
	 * @param description description/name of the test suite
	 */
	public void testSuiteExecutionStarted(String description);

	/**
	 * Method is called when test suite execution finishes
	 * 
	 * @param description description/name of the test suite
	 */
	public void testSuiteExecutionFinished(String description);

	/**
	 * Method is called before {@code AfterTestSuite} method execution starts
	 * 
	 * @param description description/name of the test suite
	 */
	public void afterTestSuiteMethodStarted(String description);

	/**
	 * Method is called after {@code AfterTestSuite} method execution finishes
	 * 
	 * @param description description/name of the test suite
	 */
	public void afterTestSuiteMethodFinished(String description);

	/**
	 * Method is called before {@code BeforeTest} method execution starts
	 * 
	 * @param t test object wrapper
	 * @see TestObjectWrapper
	 */
	public void beforeTestMethodStarted(TestObjectWrapper t);

	/**
	 * Method is called after {@code BeforeTest} method execution finishes
	 * 
	 * @param t test object wrapper
	 * @see TestObjectWrapper
	 */
	public void beforeTestMethodFinished(TestObjectWrapper t);

	/**
	 * Method is called before {@code AfterTest} method execution started
	 * 
	 * @param t test object wrapper
	 * @see TestObjectWrapper
	 */
	public void afterTestMethodStarted(TestObjectWrapper t);

	/**
	 * Method is called after {@code AfterTest} method execution finishes
	 * 
	 * @param t test object wrapper
	 * @see TestObjectWrapper
	 */
	public void afterTestMethodFinished(TestObjectWrapper t);

	/**
	 * Method is called when test execution starts
	 * 
	 * @param t test object wrapper
	 * @see TestObjectWrapper
	 */
	public void testExecutionStarted(TestObjectWrapper t);

	/**
	 * Method is called when test execution finishes
	 * 
	 * @param t test object wrapper
	 * @see TestObjectWrapper
	 */
	public void testExecutionFinished(TestObjectWrapper t);

	/**
	 * Method is called when test execution is skipped.
	 * 
	 * @param t test object wrapper
	 * @see TestObjectWrapper
	 */
	public void testExecutionSkipped(TestObjectWrapper t);

	/**
	 * Method is called when child test execution starts
	 * 
	 * @param t test object wrapper
	 * @param paramInfo parameterInfo
	 * @see TestObjectWrapper
	 */
	public void childTestExecutionStarted(TestObjectWrapper t, String paramInfo);

	/**
	 * Method is called when child test execution finishes
	 * 
	 * @param t test object wrapper
	 * @see TestObjectWrapper
	 */
	public void childTestExecutionFinished(TestObjectWrapper t);

	/**
	 * Method is called when user updates test status.
	 * 
	 * @param testStatus Test status set by user
	 * @param msg description/reason provided by user
	 * @see TestStatus
	 */
	public void testStatusUpdate(TestStatus testStatus, String msg);

	/**
	 * Method is called when test result is finalised.
	 * 
	 * @param testStatus Test status set by user
	 * @param description bug tracking info (Ticket/JIRA number)
	 * @see TestStatus
	 */
	public void testResult(TestStatus testStatus, String description);

	/**
	 * Method is called when test suite execution is finished and summary requires to be printed
	 * 
	 * @param description description/summary statement
	 */
	public void testSuiteSummaryPrinting(String description);

	/**
	 * Method is called when test suite execution is finished and at the end failure highlight is required.
	 * 
	 * @param description description/summary statement
	 */
	public void testSuiteFailureHighlight(String description);

	/**
	 * Method is called when exception is thrown during test suite execution which can not be handled on test level
	 * 
	 * @param description Description of an error/exception
	 */
	public void testSuiteException(String description);

	/**
	 * Method is called when exception is thrown during test case execution
	 * 
	 * @param description Description of an error/exception
	 */
	public void testException(String description);

}
