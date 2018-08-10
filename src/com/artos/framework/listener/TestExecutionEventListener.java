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

import com.artos.framework.TestObjectWrapper;
import com.artos.framework.infra.LogWrapper;
import com.artos.framework.infra.TestContext;
import com.artos.interfaces.TestExecutionListener;

public class TestExecutionEventListener implements TestExecutionListener {

	TestContext context;
	LogWrapper logger;

	public TestExecutionEventListener(TestContext context) {
		this.context = context;
		this.logger = context.getLogger();
	}

	@Override
	public void testSuiteExecutionStarted(String description) {
		logger.debug("\n---------------- Suite Start -------------------");
	}

	@Override
	public void testSuiteExecutionFinished(String description) {
		logger.debug("\n---------------- Suite Finished -------------------");
	}

	@Override
	public void testExecutionStarted(TestObjectWrapper t) {
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
	public void testExecutionFinished(TestObjectWrapper t) {
		// logger.info("\n---------------- Test Finish -------------------");
	}

	@Override
	public void testExecutionSkipped(TestObjectWrapper t) {
		logger.debug("\n---------------- Skipped Test : {} -------------------", t.getTestClassObject().getName());
	}

	public void testExecutionLoopCount(int count) {
		logger.debug("\n---------------- (Test Loop Count : {}) -------------------", (count + 1));
	}
}
