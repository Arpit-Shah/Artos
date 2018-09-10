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
package com.artos.framework.listener;

import com.artos.framework.Enums.TestStatus;
import com.artos.framework.TestObjectWrapper;
import com.artos.framework.infra.LogWrapper;
import com.artos.framework.infra.TestContext;
import com.artos.interfaces.TestProgress;

/**
 * Responsible for listening to test execution event and act according to
 * requirement. Can be used for report generation, GUI tool update or plug-in
 * development
 *
 */
public class TestExecutionEventListener implements TestProgress {

	TestContext context;
	LogWrapper logger;

	public TestExecutionEventListener(TestContext context) {
		this.context = context;
		this.logger = context.getLogger();
	}

	@Override
	public void beforeTestSuiteMethodStarted(String description) {
		logger.trace("\n---------------- BeforeTestSuite Method Started -------------------");
	}

	@Override
	public void beforeTestSuiteMethodFinished(String description) {
		logger.trace("\n---------------- BeforeTestSuite Method Finished -------------------");
	}

	@Override
	public void afterTestSuiteMethodStarted(String description) {
		logger.trace("\n---------------- AfterTestSuite Method Started -------------------");
	}

	@Override
	public void afterTestSuiteMethodFinished(String description) {
		logger.trace("\n---------------- AfterTestSuite Method Finished -------------------");
	}

	@Override
	public void testSuiteExecutionStarted(String description) {
		logger.trace("\n---------------- Test Suite Start -------------------");
	}

	@Override
	public void testSuiteExecutionFinished(String description) {
		logger.trace("\n---------------- Test Suite Finished -------------------");
	}

	@Override
	public void beforeTestMethodStarted(TestObjectWrapper t) {
		logger.trace("\n---------------- BeforeTest Method Started -------------------");

		// @formatter:off
				context.getLogger().info("*************************************************************************"
										+ "\nTest Name	: {}" 
										+ "\nWritten BY	: {}"
										+ "\nDate		: {}"
										+ "\nShort Desc	: {}"
										+ "\n*************************************************************************"
										, t.getTestClassObject().getName()
										, t.getTestPlanPreparedBy()
										, t.getTestPlanPreparationDate()
										, t.getTestPlanDescription()
										);
		// @formatter:on
	}

	@Override
	public void beforeTestMethodFinished(TestObjectWrapper t) {
		logger.trace("\n---------------- BeforeTest Method Finished -------------------");
	}

	@Override
	public void testExecutionStarted(TestObjectWrapper t) {
		logger.trace("\n---------------- Test Starts -------------------");
	}

	@Override
	public void testExecutionFinished(TestObjectWrapper t) {
		logger.trace("\n---------------- Test Finish -------------------");
	}

	@Override
	public void afterTestMethodStarted(TestObjectWrapper t) {
		logger.trace("\n---------------- AfterTest Method Execution Started -------------------");
	}

	@Override
	public void afterTestMethodFinished(TestObjectWrapper t) {
		logger.trace("\n---------------- AfterTest Method Execution Finished -------------------");
	}

	@Override
	public void testExecutionSkipped(TestObjectWrapper t) {
		logger.debug("\n---------------- Skipped Test : {} -------------------", t.getTestClassObject().getName());
	}

	@Override
	public void testExecutionLoopCount(int count) {
		logger.info("\n---------------- (Test Loop Count : {}) -------------------", (count + 1));
	}

	@Override
	public void testSuiteException(String description) {
		logger.trace("\n---------------- Test Suite Exception -------------------");
	}

	@Override
	public void testException(String description) {
		logger.trace("\n---------------- Test Case Exception -------------------");
	}

	@Override
	public void testStatusUpdate(TestStatus testStatus, String msg) {
		logger.trace("\n---------------- Test Status Update -------------------");
	}

	@Override
	public void testResult(TestStatus testStatus, String description) {
		logger.trace("\n---------------- Test Result -------------------");
	}
}
