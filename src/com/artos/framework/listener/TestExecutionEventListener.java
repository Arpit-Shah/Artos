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
