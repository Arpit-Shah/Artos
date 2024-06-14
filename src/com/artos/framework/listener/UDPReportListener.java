package com.artos.framework.listener;

import java.io.File;
import java.io.IOException;

import com.artos.framework.Enums.TestStatus;
import com.artos.framework.FWStaticStore;
import com.artos.framework.infra.BDDScenario;
import com.artos.framework.infra.BDDStep;
import com.artos.framework.infra.LogWrapper;
import com.artos.framework.infra.TestContext;
import com.artos.framework.infra.TestObjectWrapper;
import com.artos.framework.infra.TestUnitObjectWrapper;
import com.artos.interfaces.TestProgress;
import com.artos.utils.UDP;

public class UDPReportListener implements TestProgress {

	TestContext context;
	LogWrapper logger;
	UDP udp;

	public UDPReportListener(TestContext context) {
		this.context = context;
		this.logger = context.getLogger();
		udp = context.getDashBoardConnector();
	}

	@Override
	public void testExecutionLoopCount(int count) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeTestSuiteMethodExecutionStarted(String methodName, String description) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeTestSuiteMethodExecutionFinished(String description) {
		// TODO Auto-generated method stub

	}

	@Override
	public void testSuiteExecutionStarted(String description) {
		// TODO Auto-generated method stub

	}

	@Override
	public void testSuiteExecutionFinished(String description) {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterTestSuiteMethodExecutionStarted(String methodName, String description) {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterTestSuiteMethodExecutionFinished(String description) {
		// TODO Auto-generated method stub

	}

	@Override
	public void printTestPlan(TestObjectWrapper t) {
		// TODO Auto-generated method stub

	}

	@Override
	public void printTestPlan(BDDScenario sc) {
		// TODO Auto-generated method stub

	}

	@Override
	public void printTestUnitPlan(TestUnitObjectWrapper unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void printTestUnitPlan(BDDStep step) {
		// TODO Auto-generated method stub

	}

	@Override
	public void globalBeforeTestUnitMethodExecutionStarted(String methodName, TestUnitObjectWrapper unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void globalBeforeTestUnitMethodExecutionStarted(String methodName, BDDStep step) {
		// TODO Auto-generated method stub

	}

	@Override
	public void globalBeforeTestUnitMethodExecutionFinished(TestUnitObjectWrapper unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void globalBeforeTestUnitMethodExecutionFinished(BDDStep step) {
		// TODO Auto-generated method stub

	}

	@Override
	public void globalAfterTestUnitMethodExecutionStarted(String methodName, TestUnitObjectWrapper unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void globalAfterTestUnitMethodExecutionStarted(String methodName, BDDStep step) {
		// TODO Auto-generated method stub

	}

	@Override
	public void globalAfterTestUnitMethodExecutionFinished(TestUnitObjectWrapper unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void globalAfterTestUnitMethodExecutionFinished(BDDStep step) {
		// TODO Auto-generated method stub

	}

	@Override
	public void localBeforeTestUnitMethodExecutionStarted(TestObjectWrapper t, TestUnitObjectWrapper unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void localBeforeTestUnitMethodExecutionFinished(TestUnitObjectWrapper unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void localAfterTestUnitMethodExecutionStarted(TestObjectWrapper t, TestUnitObjectWrapper unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void localAfterTestUnitMethodExecutionFinished(TestUnitObjectWrapper unit) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void localAfterFailedUnitMethodExecutionStarted(TestObjectWrapper t, TestUnitObjectWrapper unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void localAfterFailedUnitMethodExecutionFinished(TestUnitObjectWrapper unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void globalBeforeTestCaseMethodExecutionStarted(String methodName, TestObjectWrapper t) {
		// TODO Auto-generated method stub

	}

	@Override
	public void globalBeforeTestCaseMethodExecutionStarted(String methodName, BDDScenario scenario) {
		// TODO Auto-generated method stub

	}

	@Override
	public void globalBeforeTestCaseMethodExecutionFinished(TestObjectWrapper t) {
		// TODO Auto-generated method stub

	}

	@Override
	public void globalBeforeTestCaseMethodExecutionFinished(BDDScenario scenario) {
		// TODO Auto-generated method stub

	}

	@Override
	public void globalAfterTestCaseMethodExecutionStarted(String methodName, TestObjectWrapper t) {
		// TODO Auto-generated method stub

	}

	@Override
	public void globalAfterTestCaseMethodExecutionStarted(String methodName, BDDScenario scenario) {
		// TODO Auto-generated method stub

	}

	@Override
	public void globalAfterTestCaseMethodExecutionFinished(TestObjectWrapper t) {
		// TODO Auto-generated method stub

	}

	@Override
	public void globalAfterTestCaseMethodExecutionFinished(BDDScenario scenario) {
		// TODO Auto-generated method stub

	}

	@Override
	public void globalAfterFailedUnitMethodExecutionStarted(String methodName, TestUnitObjectWrapper unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void globalAfterFailedUnitMethodExecutionStarted(String methodName, BDDStep step) {
		// TODO Auto-generated method stub

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
	public void localBeforeTestCaseMethodExecutionStarted(String methodName, TestObjectWrapper t) {
		// TODO Auto-generated method stub

	}

	@Override
	public void localBeforeTestCaseMethodExecutionFinished(TestObjectWrapper t) {
		// TODO Auto-generated method stub

	}

	@Override
	public void localAfterTestCaseMethodExecutionStarted(String methodName, TestObjectWrapper t) {
		// TODO Auto-generated method stub

	}

	@Override
	public void localAfterTestCaseMethodExecutionFinished(TestObjectWrapper t) {
		// TODO Auto-generated method stub

	}

	@Override
	public void testCaseExecutionStarted(TestObjectWrapper t) {
		// TODO Auto-generated method stub

	}

	@Override
	public void testCaseExecutionStarted(BDDScenario scenario) {
		// TODO Auto-generated method stub

	}

	@Override
	public void testCaseExecutionFinished(TestObjectWrapper t) {
		// TODO Auto-generated method stub

	}

	@Override
	public void testCaseExecutionFinished(BDDScenario scenario) {
		// TODO Auto-generated method stub

	}

	@Override
	public void testUnitExecutionStarted(TestUnitObjectWrapper unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void testUnitExecutionStarted(BDDStep step) {
		// TODO Auto-generated method stub

	}

	@Override
	public void testUnitExecutionFinished(TestUnitObjectWrapper unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void testUnitExecutionFinished(BDDStep step) {
		// TODO Auto-generated method stub

	}

	@Override
	public void testCaseExecutionSkipped(TestObjectWrapper t) {
		// TODO Auto-generated method stub

	}

	@Override
	public void childTestCaseExecutionStarted(TestObjectWrapper t, String paramInfo) {
		// TODO Auto-generated method stub

	}

	@Override
	public void childTestCaseExecutionStarted(BDDScenario scenario, String paramInfo) {
		// TODO Auto-generated method stub

	}

	@Override
	public void childTestCaseExecutionFinished(TestObjectWrapper t) {
		// TODO Auto-generated method stub

	}

	@Override
	public void childTestCaseExecutionFinished(BDDScenario scenario) {
		// TODO Auto-generated method stub

	}

	@Override
	public void childTestUnitExecutionStarted(TestObjectWrapper t, TestUnitObjectWrapper unit, String paramInfo) {
		// TODO Auto-generated method stub

	}

	@Override
	public void childTestUnitExecutionStarted(BDDScenario scenario, BDDStep step, String paramInfo) {
		// TODO Auto-generated method stub

	}

	@Override
	public void childTestUnitExecutionFinished(TestUnitObjectWrapper unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void childTestUnitExecutionFinished(BDDStep step) {
		// TODO Auto-generated method stub

	}

	@Override
	public void testCaseStatusUpdate(TestStatus testStatus, File snapshot, String msg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void testResult(TestObjectWrapper t, TestStatus testStatus, File snapshot, String description) {
	}

	@Override
	public void testSuiteSummaryPrinting(String description) {
		// TODO Auto-generated method stub

	}

	@Override
	public void testSuiteFailureHighlight(String description) {
		// TODO Auto-generated method stub

	}

	@Override
	public void testSuiteException(Throwable e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void testException(Throwable e) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void unitException(Throwable e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void testCaseSummaryPrinting(String FQCN, String description) {

		if (null == udp) {
			return;
		}

		try {
			String rawUserName = FWStaticStore.systemProperties.getUserAccountName();
			String suiteName = String.format("%-" + 10 + "s", context.getTestSuiteName());
			String userName = String.format("%-" + 10 + "s", rawUserName.length() > 10 ? rawUserName.substring(0, 10) : rawUserName);
			String testCaseName = String.format("%-" + 100 + "s", FQCN.length() > 100 ? FQCN.substring(0, 100) : FQCN);
			String msg = description + ":" + userName + ":" + suiteName + ":" + testCaseName;
			// System.err.println(msg);
			udp.sendMsg(msg.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void testUnitSummaryPrinting(String FQCN, String description) {

		if (null == udp) {
			return;
		}

		try {
			String rawUserName = FWStaticStore.systemProperties.getUserAccountName();
			String suiteName = String.format("%-" + 10 + "s", context.getTestSuiteName());
			String userName = String.format("%-" + 10 + "s", rawUserName.length() > 10 ? rawUserName.substring(0, 10) : rawUserName);
			String testCaseName = String.format("%-" + 100 + "s", FQCN.length() > 100 ? FQCN.substring(0, 100) : FQCN);
			String msg = description + ":" + userName + ":" + suiteName + ":" + testCaseName;
			// System.err.println(msg);
			udp.sendMsg(msg.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void testUnitResult(TestUnitObjectWrapper unit, TestStatus testStatus, File snapshot, String description) {
		// TODO Auto-generated method stub
	}

	@Override
	public void testResult(BDDScenario scenario, TestStatus testStatus, File snapshot, String description) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void testUnitResult(BDDStep step, TestStatus testStatus, File snapshot, String description) {
		// TODO Auto-generated method stub
		
	}

}
