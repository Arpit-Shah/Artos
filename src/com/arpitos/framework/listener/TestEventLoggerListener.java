package com.arpitos.framework.listener;

import com.arpitos.framework.TestObjectWrapper;
import com.arpitos.framework.infra.LogWrapper;
import com.arpitos.framework.infra.TestContext;
import com.arpitos.interfaces.TestExecutionListner;

public class TestEventLoggerListener implements TestExecutionListner {

	TestContext context;
	LogWrapper logger;

	public TestEventLoggerListener(TestContext context) {
		this.context = context;
		this.logger = context.getLogger();
	}

	@Override
	public void testSuiteExecutionStarted(String description) {
		logger.info("\n---------------- Suite Start -------------------");
	}

	@Override
	public void testSuiteExecutionFinished(String description) {
		logger.info("\n---------------- Suite Finished -------------------");
	}

	@Override
	public void testExecutionStarted(TestObjectWrapper t) {
		// logger.info("\n---------------- Test Start -------------------");
		// @formatter:off
		context.getLogger().info("*************************************************************************"
								+ "\nTest Name	: " + t.getTestClassObject().getName()
								+ "\nWritten BY	: " + t.getTestPlanPreparedBy()
								+ "\nDate		: " + t.getTestPlanPreparationDate()
								+ "\nShort Desc	: " + t.getTestPlanDescription()
								+ "\n*************************************************************************");
		// @formatter:on
	}

	@Override
	public void testExecutionFinished(TestObjectWrapper t) {
		// logger.info("\n---------------- Test Finish -------------------");
	}

	@Override
	public void testExecutionSkipped(TestObjectWrapper t) {
		logger.info("\n---------------- Skipped Test : " + t.getTestClassObject().getName() + " -------------------");
	}

	public void testExecutionLoopCount(int count) {
		logger.info("\n---------------- (Test Loop Count : " + (count + 1) + ") -------------------");
	}
}
