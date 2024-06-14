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

import java.io.File;

import com.artos.framework.Enums.TestStatus;
import com.artos.framework.infra.BDDScenario;
import com.artos.framework.infra.BDDStep;
import com.artos.framework.infra.TestObjectWrapper;
import com.artos.framework.infra.TestUnitObjectWrapper;

/**
 * Interface recommended for any listener which requires to be notified test
 * progress events.
 */
public interface TestProgress {

	// ==========================================================
	// Test Loop Count
	// ==========================================================

	/**
	 * Method is called when test loop execution starts, This method is called same
	 * number of time as loop count set by user.
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
	 * @param methodName  method name
	 * @param description description/name of the test suite
	 */
	public void beforeTestSuiteMethodExecutionStarted(String methodName, String description);

	/**
	 * Method is called after {@code BeforeTestSuite} method execution finished
	 * 
	 * @param description description/name of the test suite
	 */
	public void beforeTestSuiteMethodExecutionFinished(String description);

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
	 * @param methodName  method name
	 * @param description description/name of the test suite
	 */
	public void afterTestSuiteMethodExecutionStarted(String methodName, String description);

	/**
	 * Method is called after {@code AfterTestSuite} method execution finishes
	 * 
	 * @param description description/name of the test suite
	 */
	public void afterTestSuiteMethodExecutionFinished(String description);

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

	/**
	 * Method is called before {@code BeforeTest} method execution starts
	 * 
	 * @param sc {@link BDDScenario}
	 */
	public void printTestPlan(BDDScenario sc);

	/**
	 * Method is called before {@code BeforeTestUnit} method execution starts
	 * 
	 * @param unit test unit object wrapper
	 * @see TestUnitObjectWrapper
	 */
	public void printTestUnitPlan(TestUnitObjectWrapper unit);

	/**
	 * Method is called before {@code BeforeTestUnit} method execution starts
	 * 
	 * @param step {@link BDDStep}
	 */
	public void printTestUnitPlan(BDDStep step);

	// ==========================================================
	// Test Unit Before and After
	// ==========================================================
	/**
	 * Method is called before global {@code BeforeTestUnit} method execution starts
	 * 
	 * @param methodName method name annotated with {@code BeforeTestUnit}
	 * @param unit       test unit object wrapper
	 * @see TestUnitObjectWrapper
	 */
	public void globalBeforeTestUnitMethodExecutionStarted(String methodName, TestUnitObjectWrapper unit);

	/**
	 * Method is called before global {@code BeforeTestUnit} method execution starts
	 * 
	 * @param methodName method name
	 * @param step       {@link BDDStep}
	 */
	public void globalBeforeTestUnitMethodExecutionStarted(String methodName, BDDStep step);

	/**
	 * Method is called after global {@code BeforeTestUnit} method execution starts
	 * 
	 * @param unit test unit object wrapper
	 * @see TestUnitObjectWrapper
	 */
	public void globalBeforeTestUnitMethodExecutionFinished(TestUnitObjectWrapper unit);

	/**
	 * Method is called after global {@code BeforeTestUnit} method execution starts
	 * 
	 * @param step {@link BDDStep}
	 */
	public void globalBeforeTestUnitMethodExecutionFinished(BDDStep step);

	/**
	 * Method is called after global {@code AfterTestUnit} method execution starts
	 * 
	 * @param methodName method name annotated with {@code AfterTestUnit}
	 * @param unit       test unit object wrapper
	 * @see TestUnitObjectWrapper
	 */
	public void globalAfterTestUnitMethodExecutionStarted(String methodName, TestUnitObjectWrapper unit);

	/**
	 * Method is called after global {@code AfterTestUnit} method execution starts
	 * 
	 * @param methodName method name
	 * @param step       {@link BDDStep}
	 */
	public void globalAfterTestUnitMethodExecutionStarted(String methodName, BDDStep step);

	/**
	 * Method is called after global {@code AfterFailedUnit} method execution starts
	 * 
	 * @param methodName method name annotated with {@code AfterTestUnit}
	 * @param unit       test unit object wrapper
	 * @see TestUnitObjectWrapper
	 */
	public void globalAfterFailedUnitMethodExecutionStarted(String methodName, TestUnitObjectWrapper unit);

	/**
	 * Method is called after global {@code AfterFailedUnit} method execution starts
	 * 
	 * @param methodName method name
	 * @param step       {@link BDDStep}
	 */
	public void globalAfterFailedUnitMethodExecutionStarted(String methodName, BDDStep step);

	/**
	 * Method is called after global {@code AfterTestUnit} method execution starts
	 * 
	 * @param unit test unit object wrapper
	 * @see TestUnitObjectWrapper
	 */
	public void globalAfterTestUnitMethodExecutionFinished(TestUnitObjectWrapper unit);

	public void globalAfterTestUnitMethodExecutionFinished(BDDStep step);

	/**
	 * Method is called after global {@code AfterFailedUnit} method execution starts
	 * 
	 * @param unit test unit object wrapper
	 * @see TestUnitObjectWrapper
	 */
	public void globalAfterFailedUnitMethodExecutionFinished(TestUnitObjectWrapper unit);

	/**
	 * Method is called after global {@code AfterFailedUnit} method execution starts
	 * 
	 * @param step {@link BDDStep}
	 */
	public void globalAfterFailedUnitMethodExecutionFinished(BDDStep step);

	/**
	 * Method is called before local {@code BeforeTestUnit} method execution starts
	 * 
	 * @param t    test object wrapper
	 * @param unit test unit object wrapper
	 * @see TestUnitObjectWrapper
	 */
	public void localBeforeTestUnitMethodExecutionStarted(TestObjectWrapper t, TestUnitObjectWrapper unit);

	/**
	 * Method is called after local {@code BeforeTestUnit} method execution starts
	 * 
	 * @param unit test unit object wrapper
	 * @see TestUnitObjectWrapper
	 */
	public void localBeforeTestUnitMethodExecutionFinished(TestUnitObjectWrapper unit);

	/**
	 * Method is called after local {@code AfterTestUnit} method execution starts
	 * 
	 * @param t    test object wrapper
	 * @param unit test unit object wrapper
	 * @see TestUnitObjectWrapper
	 */
	public void localAfterTestUnitMethodExecutionStarted(TestObjectWrapper t, TestUnitObjectWrapper unit);

	/**
	 * Method is called after local {@code AfterTestUnit} method execution starts
	 * 
	 * @param unit test unit object wrapper
	 * @see TestUnitObjectWrapper
	 */
	public void localAfterTestUnitMethodExecutionFinished(TestUnitObjectWrapper unit);
	
	/**
	 * Method is called after local {@code AfterFailedUnit} method execution starts
	 * 
	 * @param t test object wrapper
	 * @param unit test unit object wrapper
	 * @see TestUnitObjectWrapper
	 */
	public void localAfterFailedUnitMethodExecutionStarted(TestObjectWrapper t, TestUnitObjectWrapper unit);
	
	/**
	 * Method is called after local {@code AfterTestUnit} method execution starts
	 * 
	 * @param unit test unit object wrapper
	 * @see TestUnitObjectWrapper
	 */
	public void localAfterFailedUnitMethodExecutionFinished(TestUnitObjectWrapper unit);

	// ==========================================================
	// Test Case Before and After
	// ==========================================================
	/**
	 * Method is called before {@code BeforeTest} method execution starts
	 * 
	 * @param methodName method name
	 * @param t          test object wrapper
	 * @see TestObjectWrapper
	 */
	public void globalBeforeTestCaseMethodExecutionStarted(String methodName, TestObjectWrapper t);

	/**
	 * Method is called before {@code BeforeTest} method execution starts
	 * 
	 * @param methodName method name
	 * @param scenario   {@link BDDScenario}
	 */
	public void globalBeforeTestCaseMethodExecutionStarted(String methodName, BDDScenario scenario);

	/**
	 * Method is called after {@code BeforeTest} method execution finishes
	 * 
	 * @param t test object wrapper
	 * @see TestObjectWrapper
	 */
	public void globalBeforeTestCaseMethodExecutionFinished(TestObjectWrapper t);

	/**
	 * Method is called after {@code BeforeTest} method execution finishes
	 * 
	 * @param scenario {@link BDDScenario}
	 */
	public void globalBeforeTestCaseMethodExecutionFinished(BDDScenario scenario);

	/**
	 * Method is called before {@code AfterTest} method execution started
	 * 
	 * @param methodName method name
	 * @param t          test object wrapper
	 * @see TestObjectWrapper
	 */
	public void globalAfterTestCaseMethodExecutionStarted(String methodName, TestObjectWrapper t);

	/**
	 * Method is called before {@code AfterTest} method execution started
	 * 
	 * @param methodName method name
	 * @param scenario   {@link BDDScenario}
	 */
	public void globalAfterTestCaseMethodExecutionStarted(String methodName, BDDScenario scenario);

	/**
	 * Method is called after {@code AfterTest} method execution finishes
	 * 
	 * @param t test object wrapper
	 * @see TestObjectWrapper
	 */
	public void globalAfterTestCaseMethodExecutionFinished(TestObjectWrapper t);

	/**
	 * Method is called after {@code AfterTest} method execution finishes
	 * 
	 * @param scenario {@link BDDScenario}
	 */
	public void globalAfterTestCaseMethodExecutionFinished(BDDScenario scenario);

	/**
	 * Method is called before {@code BeforeTest} method execution starts
	 * 
	 * @param methodName method name
	 * @param t          test object wrapper
	 * @see TestObjectWrapper
	 */
	public void localBeforeTestCaseMethodExecutionStarted(String methodName, TestObjectWrapper t);

	/**
	 * Method is called after {@code BeforeTest} method execution finishes
	 * 
	 * @param t test object wrapper
	 * @see TestObjectWrapper
	 */
	public void localBeforeTestCaseMethodExecutionFinished(TestObjectWrapper t);

	/**
	 * Method is called before {@code AfterTest} method execution started
	 * 
	 * @param methodName method name
	 * @param t          test object wrapper
	 * @see TestObjectWrapper
	 */
	public void localAfterTestCaseMethodExecutionStarted(String methodName, TestObjectWrapper t);

	/**
	 * Method is called after {@code AfterTest} method execution finishes
	 * 
	 * @param t test object wrapper
	 * @see TestObjectWrapper
	 */
	public void localAfterTestCaseMethodExecutionFinished(TestObjectWrapper t);

	// ==========================================================
	// Test Case Execution
	// ==========================================================
	/**
	 * Method is called when test execution starts
	 * 
	 * @param t test object wrapper
	 * @see TestObjectWrapper
	 */
	public void testCaseExecutionStarted(TestObjectWrapper t);

	/**
	 * Method is called when Scenario execution starts
	 * 
	 * @param scenario {@link BDDScenario}
	 */
	public void testCaseExecutionStarted(BDDScenario scenario);

	/**
	 * Method is called when test execution finishes
	 * 
	 * @param t test object wrapper
	 * @see TestObjectWrapper
	 */
	public void testCaseExecutionFinished(TestObjectWrapper t);

	/**
	 * Method is called when Scenario execution finishes
	 * 
	 * @param scenario {@link BDDScenario}
	 */
	public void testCaseExecutionFinished(BDDScenario scenario);

	/**
	 * Method is called when test unit execution starts
	 * 
	 * @param unit test object wrapper
	 * @see TestUnitObjectWrapper
	 */
	public void testUnitExecutionStarted(TestUnitObjectWrapper unit);

	/**
	 * Method is called when Scenario step execution starts
	 * 
	 * @param step {@link BDDStep}
	 */
	public void testUnitExecutionStarted(BDDStep step);

	/**
	 * Method is called when test unit execution finishes
	 * 
	 * @param unit test object wrapper
	 * @see TestUnitObjectWrapper
	 */
	public void testUnitExecutionFinished(TestUnitObjectWrapper unit);

	/**
	 * Method is called when Scenario step execution finishes
	 * 
	 * @param step {@link BDDStep}
	 */
	public void testUnitExecutionFinished(BDDStep step);

	// ==========================================================
	// Test Skip
	// ==========================================================
	/**
	 * Method is called when test execution is skipped.
	 * 
	 * @param t test object wrapper
	 * @see TestObjectWrapper
	 */
	public void testCaseExecutionSkipped(TestObjectWrapper t);

	// ==========================================================
	// Child test case
	// ==========================================================
	/**
	 * Method is called when child test execution starts
	 * 
	 * @param t         test object wrapper
	 * @param paramInfo parameterInfo
	 * @see TestObjectWrapper
	 */
	public void childTestCaseExecutionStarted(TestObjectWrapper t, String paramInfo);

	/**
	 * Method is called when child Scenario execution starts
	 * 
	 * @param scenario  {@link BDDScenario}
	 * @param paramInfo parameter info
	 */
	public void childTestCaseExecutionStarted(BDDScenario scenario, String paramInfo);

	/**
	 * Method is called when child test execution finishes
	 * 
	 * @param t test object wrapper
	 * @see TestObjectWrapper
	 */
	public void childTestCaseExecutionFinished(TestObjectWrapper t);

	/**
	 * Method is called when child Scenario execution finishes
	 * 
	 * @param scenario {@link BDDScenario}
	 */
	public void childTestCaseExecutionFinished(BDDScenario scenario);

	/**
	 * Method is called when unit child test execution starts
	 * 
	 * @param t         test object wrapper
	 * @param unit      test unit object wrapper
	 * @param paramInfo parameterInfo
	 * @see TestUnitObjectWrapper
	 */
	public void childTestUnitExecutionStarted(TestObjectWrapper t, TestUnitObjectWrapper unit, String paramInfo);

	/**
	 * Method is called when child Scenario steps execution starts
	 * 
	 * @param scenario  {@link BDDScenario}
	 * @param step      {@link BDDStep}
	 * @param paramInfo parameter info
	 */
	public void childTestUnitExecutionStarted(BDDScenario scenario, BDDStep step, String paramInfo);

	/**
	 * Method is called when unit child test execution finishes
	 * 
	 * @param unit test unit object wrapper
	 * @see TestUnitObjectWrapper
	 */
	public void childTestUnitExecutionFinished(TestUnitObjectWrapper unit);

	/**
	 * Method is called when child Scenario step execution finishes
	 * 
	 * @param step {@link BDDStep}
	 */
	public void childTestUnitExecutionFinished(BDDStep step);

	// ==========================================================
	// Test Status update
	// ==========================================================
	/**
	 * Method is called when user updates test status.
	 * 
	 * @param testStatus Test status set by user
	 * @param snapshot   picture file or null
	 * @param msg        description/reason provided by user
	 * @see TestStatus
	 */
	public void testCaseStatusUpdate(TestStatus testStatus, File snapshot, String msg);

	// ==========================================================
	// Test Outcome
	// ==========================================================
	/**
	 * Method is called when test result is finalised.
	 * 
	 * @param t           test object wrapper
	 * @param testStatus  Test status set by user
	 * @param snapshot    picture file or null
	 * @param description bug tracking info (Ticket/JIRA number)
	 * @see TestStatus
	 */
	public void testResult(TestObjectWrapper t, TestStatus testStatus, File snapshot, String description);

	/**
	 * Method is called when test result is finalised.
	 * 
	 * @param scenario    test scenario object
	 * @param testStatus  Test status set by user
	 * @param snapshot    picture file or null
	 * @param description bug tracking info (Ticket/JIRA number)
	 * @see TestStatus
	 */
	public void testResult(BDDScenario scenario, TestStatus testStatus, File snapshot, String description);

	/**
	 * Method is called when test case execution is finished and summary requires to
	 * be printed
	 * 
	 * @param FQCN        testcase name0
	 * @param description description/summary statement
	 */
	public void testCaseSummaryPrinting(String FQCN, String description);

	/**
	 * Method is called when test unit result is finalised.
	 * 
	 * @param unit        unit object wrapper
	 * @param testStatus  Test status set by user
	 * @param snapshot    picture file or null
	 * @param description bug tracking info (Ticket/JIRA number)
	 * @see TestStatus
	 */
	public void testUnitResult(TestUnitObjectWrapper unit, TestStatus testStatus, File snapshot, String description);

	/**
	 * Method is called when step result is finalised.
	 * 
	 * @param step        {@link BDDStep}
	 * @param testStatus  {@link TestStatus}
	 * @param snapshot    picture file or null
	 * @param description bug tracking info (Ticket/JIRA number)
	 */
	public void testUnitResult(BDDStep step, TestStatus testStatus, File snapshot, String description);

	/**
	 * Method is called when test unit execution is finished and summary requires to
	 * be printed
	 * 
	 * @param FQCN        testcase name0
	 * @param description description/summary statement
	 */
	public void testUnitSummaryPrinting(String FQCN, String description);

	/**
	 * Method is called when test suite execution is finished and summary requires
	 * to be printed
	 * 
	 * @param description description/summary statement
	 */
	public void testSuiteSummaryPrinting(String description);

	// ==========================================================
	// Test Suite Failure Highlight
	// ==========================================================
	/**
	 * Method is called when test suite execution is finished and at the end failure
	 * highlight is required.
	 * 
	 * @param description description/summary statement
	 */
	public void testSuiteFailureHighlight(String description);

	// ==========================================================
	// Test Exception
	// ==========================================================
	/**
	 * Method is called when exception is thrown during test suite execution which
	 * can not be handled on test level
	 * 
	 * @param e an error/exception/throwable
	 */
	public void testSuiteException(Throwable e);

	/**
	 * Method is called when exception is thrown during test case execution
	 * 
	 * @param e an error/exception/throwable
	 */
	public void testException(Throwable e);

	/**
	 * Method is called when exception is thrown during unit execution
	 * 
	 * @param e an error/exception/throwable
	 */
	public void unitException(Throwable e);

}
