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

import com.artos.annotation.BeforeTestUnit;
import com.artos.framework.Enums.TestStatus;
import com.artos.framework.TestObjectWrapper;
import com.artos.framework.TestUnitObjectWrapper;

/**
 * Interface recommended for any listener which requires to be notified test progress events.
 */
public interface TestProgress {

	// ==========================================================
	// Test Loop Count
	// ==========================================================

	/**
	 * Method is called when test loop execution starts, This method is called same number of time as loop count set by user.
	 * 
	 * @param count number of test suite execution loop count
	 */
	public void testExecutionLoopCount(int count);

	// ==========================================================
	// Test Suite Before and After
	// ==========================================================

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

	// ==========================================================
	// Test Plan
	// ==========================================================

	/**
	 * Method is called before {@code BeforeTest} method execution starts
	 * 
	 * @param t test object wrapper
	 * @see TestObjectWrapper
	 */
	public void printTestPlan(TestObjectWrapper t);

	// ==========================================================
	// Test Unit Before and After
	// ==========================================================
	/**
	 * Method is called before global {@code BeforeTestUnit} method execution starts
	 * 
	 * @param methodName method name annotated with {@code BeforeTestUnit}
	 * @param unit test unit object wrapper
	 * @see TestUnitObjectWrapper
	 */
	public void beforeGlobalTestUnitMethodStarted(String methodName, TestUnitObjectWrapper unit);

	/**
	 * Method is called after global {@code BeforeTestUnit} method execution starts
	 * 
	 * @param unit test unit object wrapper
	 * @see TestUnitObjectWrapper
	 */
	public void beforeGlobalTestUnitMethodFinished(TestUnitObjectWrapper unit);

	/**
	 * Method is called before local {@code BeforeTestUnit} method execution starts
	 * 
	 * @param t test object wrapper
	 * @param unit test unit object wrapper
	 * @see TestUnitObjectWrapper
	 */
	public void beforeLocalTestUnitMethodStarted(TestObjectWrapper t, TestUnitObjectWrapper unit);

	/**
	 * Method is called after local {@code BeforeTestUnit} method execution starts
	 * 
	 * @param unit test unit object wrapper
	 * @see TestUnitObjectWrapper
	 */
	public void beforeLocalTestUnitMethodFinished(TestUnitObjectWrapper unit);

	/**
	 * Method is called after global {@code AfterTestUnit} method execution starts
	 * 
	 * @param methodName method name annotated with {@code AfterTestUnit}
	 * @param unit test unit object wrapper
	 * @see TestUnitObjectWrapper
	 */
	public void afterGlobalTestUnitMethodStarted(String methodName, TestUnitObjectWrapper unit);

	/**
	 * Method is called after global {@code AfterTestUnit} method execution starts
	 * 
	 * @param unit test unit object wrapper
	 * @see TestUnitObjectWrapper
	 */
	public void afterGlobalTestUnitMethodFinished(TestUnitObjectWrapper unit);

	/**
	 * Method is called after local {@code AfterTestUnit} method execution starts
	 * 
	 * @param t test object wrapper
	 * @param unit test unit object wrapper
	 * @see TestUnitObjectWrapper
	 */
	public void afterLocalTestUnitMethodStarted(TestObjectWrapper t, TestUnitObjectWrapper unit);

	/**
	 * Method is called after local {@code AfterTestUnit} method execution starts
	 * 
	 * @param unit test unit object wrapper
	 * @see TestUnitObjectWrapper
	 */
	public void afterLocalTestUnitMethodFinished(TestUnitObjectWrapper unit);

	// ==========================================================
	// Test Case Before and After
	// ==========================================================
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

	// ==========================================================
	// Test Case Execution
	// ==========================================================
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
	 * Method is called when test unit execution starts
	 * 
	 * @param unit test object wrapper
	 * @see TestUnitObjectWrapper
	 */
	public void testUnitExecutionStarted(TestUnitObjectWrapper unit);

	/**
	 * Method is called when test unit execution finishes
	 * 
	 * @param unit test object wrapper
	 * @see TestUnitObjectWrapper
	 */
	public void testUnitExecutionFinished(TestUnitObjectWrapper unit);

	// ==========================================================
	// Test Skip
	// ==========================================================
	/**
	 * Method is called when test execution is skipped.
	 * 
	 * @param t test object wrapper
	 * @see TestObjectWrapper
	 */
	public void testExecutionSkipped(TestObjectWrapper t);

	// ==========================================================
	// Child test case
	// ==========================================================
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
	 * Method is called when unit child test execution starts
	 * 
	 * @param t test object wrapper
	 * @param unit test unit object wrapper
	 * @param paramInfo parameterInfo
	 * @see TestUnitObjectWrapper
	 */
	public void childTestUnitExecutionStarted(TestObjectWrapper t, TestUnitObjectWrapper unit, String paramInfo);

	/**
	 * Method is called when unit child test execution finishes
	 * 
	 * @param unit test unit object wrapper
	 * @see TestUnitObjectWrapper
	 */
	public void childTestUnitExecutionFinished(TestUnitObjectWrapper unit);

	// ==========================================================
	// Test Status update
	// ==========================================================
	/**
	 * Method is called when user updates test status.
	 * 
	 * @param testStatus Test status set by user
	 * @param msg description/reason provided by user
	 * @see TestStatus
	 */
	public void testStatusUpdate(TestStatus testStatus, String msg);

	// ==========================================================
	// Test Outcome
	// ==========================================================
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

	// ==========================================================
	// Test Suite Failure Highlight
	// ==========================================================
	/**
	 * Method is called when test suite execution is finished and at the end failure highlight is required.
	 * 
	 * @param description description/summary statement
	 */
	public void testSuiteFailureHighlight(String description);

	// ==========================================================
	// Test Exception
	// ==========================================================
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
