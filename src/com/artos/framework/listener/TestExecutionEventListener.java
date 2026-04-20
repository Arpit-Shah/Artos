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
package com.artos.framework.listener;

import java.io.File;

import com.artos.framework.Enums.TestStatus;
import com.artos.framework.FWStaticStore;
import com.artos.framework.infra.BDDScenario;
import com.artos.framework.infra.BDDStep;
import com.artos.framework.infra.LogWrapper;
import com.artos.framework.infra.TestContext;
import com.artos.framework.infra.TestObjectWrapper;
import com.artos.framework.infra.TestUnitObjectWrapper;
import com.artos.interfaces.TestProgress;

/**
 * Responsible for listening to test execution event and act according to requirement. Can be used for report generation, GUI tool update or plug-in
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
	public void beforeTestSuiteMethodExecutionStarted(String methodName, String description) {
		logger.info("\n=> " + methodName + "(context)");
	}

	@Override
	public void beforeTestSuiteMethodExecutionFinished(String description) {
		// logger.trace("\n---------------- BeforeTestSuite Method Finished -------------------");
	}

	@Override
	public void afterTestSuiteMethodExecutionStarted(String methodName, String description) {
		logger.info("\n=> " + methodName + "(context)");
	}

	@Override
	public void afterTestSuiteMethodExecutionFinished(String description) {
		// logger.trace("\n---------------- AfterTestSuite Method Finished -------------------");
	}

	@Override
	public void testSuiteExecutionStarted(String description) {
		// logger.trace("\n---------------- Test Suite Start -------------------");
	}

	@Override
	public void testSuiteExecutionFinished(String description) {
		// logger.trace("\n---------------- Test Suite Finished -------------------");
	}

	@Override
	public void printTestPlan(TestObjectWrapper t) {
		StringBuilder sb = new StringBuilder();

		sb.append(FWStaticStore.ARTOS_LINE_BREAK_1);
		sb.append("\n");
		sb.append("Test Name	: " + t.getTestClassObject().getName());
		sb.append("\n");
		if (!"".equals(t.getTestPlanPreparedBy())) {
			sb.append("Written BY	: " + t.getTestPlanPreparedBy());
			sb.append("\n");
		}
		if (!"".equals(t.getTestPlanPreparationDate())) {
			sb.append("Date		: " + t.getTestPlanPreparationDate());
			sb.append("\n");
		}
		if (!"".equals(t.getTestPlanDescription())) {
			sb.append("Short Desc	: " + t.getTestPlanDescription());
			sb.append("\n");
		}
		if (!"".equals(t.getTestPlanBDD())) {
			String BDD = context.processBDD(t.getTestPlanBDD());
			sb.append("BDD Test Plan	: " + BDD);
			sb.append("\n");
		}
//		sb.append(FWStaticStore.ARTOS_LINE_BREAK_1);

		context.getLogger().info(sb.toString());
	}

	@Override
	public void printTestPlan(BDDScenario sc) {
		StringBuilder sb = new StringBuilder();

		sb.append(FWStaticStore.ARTOS_LINE_BREAK_1);
		sb.append("\n");
		sb.append("Scenario: " + sc.getScenarioDescription());
		sb.append("\n");
//		sb.append(FWStaticStore.ARTOS_LINE_BREAK_1);

		context.getLogger().info(sb.toString());
	}

	@Override
	public void printTestUnitPlan(TestUnitObjectWrapper unit) {
		StringBuilder sb = new StringBuilder();

		sb.append(FWStaticStore.ARTOS_LINE_BREAK_2);
		sb.append("\n");
		sb.append("Unit Name	: " + unit.getTestUnitMethod().getName());
		sb.append("\n");
		if (!"".equals(unit.getTestPlanPreparedBy())) {
			sb.append("Written BY	: " + unit.getTestPlanPreparedBy());
			sb.append("\n");
		}
		if (!"".equals(unit.getTestPlanPreparationDate())) {
			sb.append("Date		: " + unit.getTestPlanPreparationDate());
			sb.append("\n");
		}
		if (!"".equals(unit.getTestPlanDescription())) {
			sb.append("Short Desc	: " + unit.getTestPlanDescription());
			sb.append("\n");
		}
		if (!"".equals(unit.getTestPlanBDD())) {
			String BDD = context.processBDD(unit.getTestPlanBDD());
			sb.append("BDD Test Plan	: " + BDD);
			sb.append("\n");
		}
		if (!"".equals(unit.getStepDefinition())) {
			sb.append("Step Definition	: " + unit.getStepDefinition());
			sb.append("\n");
		}

		context.getLogger().info(sb.toString());
	}

	@Override
	public void printTestUnitPlan(BDDStep step) {
		StringBuilder sb = new StringBuilder();

		sb.append(FWStaticStore.ARTOS_LINE_BREAK_2);
		sb.append("\n");
		sb.append("Step: " + step.getStepAction() + " " + step.getStepDescription());
		sb.append("\n");

		context.getLogger().info(sb.toString());
	}

	@Override
	public void localBeforeTestCaseMethodExecutionStarted(String methodName, TestObjectWrapper t) {
		logger.info("\n=> " + methodName + "(context)");
	}

	@Override
	public void localBeforeTestCaseMethodExecutionFinished(TestObjectWrapper t) {
		// TODO Auto-generated method stub

	}

	@Override
	public void localAfterTestCaseMethodExecutionStarted(String methodName, TestObjectWrapper t) {
		logger.info("\n=> " + methodName + "(context)");
	}

	@Override
	public void localAfterTestCaseMethodExecutionFinished(TestObjectWrapper t) {
		// TODO Auto-generated method stub

	}

	@Override
	public void globalBeforeTestCaseMethodExecutionStarted(String methodName, TestObjectWrapper t) {
		logger.info("\n=> " + methodName + "(context)");
	}

	@Override
	public void globalBeforeTestCaseMethodExecutionStarted(String methodName, BDDScenario scenario) {
		logger.info("\n=> " + methodName + "(context)");
	}

	@Override
	public void globalBeforeTestCaseMethodExecutionFinished(TestObjectWrapper t) {
		// logger.trace("\n---------------- BeforeTest Method Finished -------------------");
	}

	@Override
	public void globalBeforeTestCaseMethodExecutionFinished(BDDScenario scenario) {
		// logger.trace("\n---------------- BeforeTest Method Finished -------------------");
	}

	@Override
	public void testCaseExecutionStarted(TestObjectWrapper t) {
		// logger.trace("\n---------------- Test Starts -------------------");
	}

	@Override
	public void testCaseExecutionStarted(BDDScenario scenario) {
		// logger.trace("\n---------------- Test Starts -------------------");
	}

	@Override
	public void testCaseExecutionFinished(TestObjectWrapper t) {
		// logger.trace("\n---------------- Test Finish -------------------");
	}

	@Override
	public void testCaseExecutionFinished(BDDScenario scenario) {
		// logger.trace("\n---------------- Test Finish -------------------");
	}

	@Override
	public void childTestCaseExecutionStarted(TestObjectWrapper t, String paramInfo) {
		// logger.trace("\n---------------- Child Test Starts -------------------");
	}

	@Override
	public void childTestCaseExecutionStarted(BDDScenario scenario, String paramInfo) {
		// logger.trace("\n---------------- Child Test Starts -------------------");
	}

	@Override
	public void childTestCaseExecutionFinished(TestObjectWrapper t) {
		// logger.trace("\n---------------- Child Test Finish -------------------");
	}

	@Override
	public void childTestCaseExecutionFinished(BDDScenario scenario) {
		// logger.trace("\n---------------- Child Test Finish -------------------");
	}

	@Override
	public void globalAfterTestCaseMethodExecutionStarted(String methodName, TestObjectWrapper t) {
		logger.info("\n=> " + methodName + "(context)");
	}

	@Override
	public void globalAfterTestCaseMethodExecutionStarted(String methodName, BDDScenario scenario) {
		logger.info("\n=> " + methodName + "(context)");

	}

	@Override
	public void globalAfterTestCaseMethodExecutionFinished(TestObjectWrapper t) {
		// logger.trace("\n---------------- AfterTest Method Execution Finished -------------------");
	}

	@Override
	public void globalAfterTestCaseMethodExecutionFinished(BDDScenario scenario) {
		// logger.trace("\n---------------- AfterTest Method Execution Finished -------------------");
	}

	@Override
	public void testCaseExecutionSkipped(TestObjectWrapper t) {
		logger.info("\n---------------- Skipped Test : {} -------------------", t.getTestClassObject().getName());
	}

	@Override
	public void testExecutionLoopCount(int count) {
		logger.info("\n---------------- (Test Loop Count : {}) -------------------", (count + 1));
	}

	@Override
	public void testSuiteException(Throwable e) {
		// logger.trace("\n---------------- Test Suite Exception -------------------");
	}

	@Override
	public void testException(Throwable e) {
		// logger.trace("\n---------------- Test Case Exception -------------------");
	}
	
	@Override
	public void unitException(Throwable e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void testCaseStatusUpdate(TestStatus testStatus, File snapshot, String msg) {
		// logger.trace("\n---------------- Test Status Update -------------------");
	}

	@Override
	public void testResult(TestObjectWrapper t, TestStatus testStatus, File snapshot, String description) {
		// logger.trace("\n---------------- Test Result -------------------");
	}

	@Override
	public void testSuiteSummaryPrinting(String description) {
		// logger.trace("\n---------------- Test Suite Summary -------------------");
		// logger.info("*************************************************************************");
	}

	@Override
	public void testSuiteFailureHighlight(String description) {
		// logger.trace("\n---------------- Test Faliure Highlight -------------------");
		// logger.info("*************************************************************************");
	}

	@Override
	public void globalBeforeTestUnitMethodExecutionStarted(String methodName, TestUnitObjectWrapper unit) {
		logger.info("\n=> " + methodName + "(context)");
	}

	@Override
	public void globalBeforeTestUnitMethodExecutionStarted(String methodName, BDDStep step) {
		logger.info("\n=> " + methodName + "(context)");
	}

	@Override
	public void globalBeforeTestUnitMethodExecutionFinished(TestUnitObjectWrapper unit) {
		// logger.trace("\n---------------- Global Before Test Unit Method Finished -------------------");
	}

	@Override
	public void globalBeforeTestUnitMethodExecutionFinished(BDDStep step) {
		// logger.trace("\n---------------- Global Before Test Unit Method Finished -------------------");
	}

	@Override
	public void localBeforeTestUnitMethodExecutionStarted(TestObjectWrapper t, TestUnitObjectWrapper unit) {
		logger.info("\n=> " + t.getMethodBeforeTestUnit().getName() + "(context)");
	}

	@Override
	public void localBeforeTestUnitMethodExecutionFinished(TestUnitObjectWrapper unit) {
		// logger.trace("*************************************************************************");
	}

	@Override
	public void globalAfterTestUnitMethodExecutionStarted(String methodName, TestUnitObjectWrapper unit) {
		logger.info("\n=> " + methodName + "(context)");
	}

	@Override
	public void globalAfterTestUnitMethodExecutionStarted(String methodName, BDDStep step) {
		logger.info("\n=> " + methodName + "(context)");
	}

	@Override
	public void globalAfterFailedUnitMethodExecutionStarted(String methodName, TestUnitObjectWrapper unit) {
		logger.info("\n=> " + methodName + "(context)");
	}

	@Override
	public void globalAfterFailedUnitMethodExecutionStarted(String methodName, BDDStep step) {
		logger.info("\n=> " + methodName + "(context)");
	}

	@Override
	public void globalAfterTestUnitMethodExecutionFinished(TestUnitObjectWrapper unit) {
		// logger.trace("\n---------------- Global After Test Unit Method Finished -------------------");
	}

	@Override
	public void globalAfterTestUnitMethodExecutionFinished(BDDStep step) {
		// logger.trace("\n---------------- Global After Test Unit Method Finished -------------------");
	}

	@Override
	public void globalAfterFailedUnitMethodExecutionFinished(TestUnitObjectWrapper unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void globalAfterFailedUnitMethodExecutionFinished(BDDStep step) {
		// TODO Auto-generated method stub

	}

	@Override
	public void localAfterTestUnitMethodExecutionStarted(TestObjectWrapper t, TestUnitObjectWrapper unit) {
		logger.info("\n=> " + t.getMethodAfterTestUnit().getName() + "(context)");
	}

	@Override
	public void localAfterTestUnitMethodExecutionFinished(TestUnitObjectWrapper unit) {
		// logger.trace("\n---------------- Local After Test Unit Method Finished -------------------");
	}
	
	@Override
	public void localAfterFailedUnitMethodExecutionStarted(TestObjectWrapper t, TestUnitObjectWrapper unit) {
		logger.info("\n=> " + t.getMethodAfterFailedUnit().getName() + "(context)");
	}

	@Override
	public void localAfterFailedUnitMethodExecutionFinished(TestUnitObjectWrapper unit) {
		// logger.trace("\n---------------- Local After Failed Unit Method Finished -------------------");
	}

	@Override
	public void testUnitExecutionStarted(TestUnitObjectWrapper unit) {
		// logger.info("\n=> " + unit.getTestUnitMethod().getName() + "(context)");
	}

	@Override
	public void testUnitExecutionStarted(BDDStep step) {
		// logger.info("\n=> " + step.getUnit().getTestUnitMethod().getName() + "(context)");
	}

	@Override
	public void testUnitExecutionFinished(TestUnitObjectWrapper unit) {
		// logger.trace("\n---------------- Test Unit Execution finished -------------------");
	}

	@Override
	public void testUnitExecutionFinished(BDDStep step) {
		// logger.trace("\n---------------- Test Unit Execution finished -------------------");
	}

	@Override
	public void childTestUnitExecutionStarted(TestObjectWrapper t, TestUnitObjectWrapper unit, String paramInfo) {
		// logger.trace("\n---------------- Child Test Unit Execution started -------------------");
	}

	@Override
	public void childTestUnitExecutionStarted(BDDScenario scenario, BDDStep step, String paramInfo) {
		// logger.trace("\n---------------- Child Test Unit Execution started -------------------");
	}

	@Override
	public void childTestUnitExecutionFinished(TestUnitObjectWrapper unit) {
		// logger.trace("\n---------------- Child Test Unit Execution finished -------------------");
	}

	@Override
	public void childTestUnitExecutionFinished(BDDStep step) {
		// logger.trace("\n---------------- Child Test Unit Execution finished -------------------");
	}

	@Override
	public void testCaseSummaryPrinting(String FQCN, String description) {
		// TODO Auto-generated method stub

	}

	@Override
	public void testUnitSummaryPrinting(String FQCN, String description) {
		// TODO Auto-generated method stub

	}

	@Override
	public void testResult(BDDScenario scenario, TestStatus testStatus, File snapshot, String description) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void testUnitResult(TestUnitObjectWrapper unit, TestStatus testStatus, File snapshot, String description) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void testUnitResult(BDDStep step, TestStatus testStatus, File snapshot, String description) {
		// TODO Auto-generated method stub
		
	}

}
