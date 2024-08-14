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
package com.artos.framework.infra;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.artos.framework.Enums.Importance;
import com.artos.framework.Enums.TestStatus;
import com.artos.framework.FWStaticStore;
import com.artos.framework.SystemProperties;
import com.artos.framework.parser.TestSuite;
import com.artos.interfaces.TestProgress;
import com.artos.utils.UDP;

/**
 * This is TestContext which is wrapper around all objects/tools/loggers user
 * may need during test case execution. This class is also responsible for
 * Summarising test results.
 */
public class TestContext {

	private LogWrapper logWrapper;
	private TestStatus currentTestStatus = TestStatus.PASS;
	private TestStatus currentUnitTestStatus = TestStatus.PASS;
	private boolean KnownToFail = false;

	private String strBugTrackingReference = "";

	private long totalTestCount = 0;
	private long currentPassCount = 0;
	private long currentFailCount = 0;
	private long currentSkipCount = 0;
	private long currentKTFCount = 0;

	// Test Importance for failed test cases
	private int totalFatalCount = 0;
	private int totalCriticalCount = 0;
	private int totalHighCount = 0;
	private int totalMediumCount = 0;
	private int totalLowCount = 0;
	private int totalUndefinedCount = 0;

	private long totalUnitTestCount = 0;
	private long currentUnitPassCount = 0;
	private long currentUnitFailCount = 0;
	private long currentUnitSkipCount = 0;
	private long currentUnitKTFCount = 0;

	// Test Importance for failed test units
	private int totalUnitFatalCount = 0;
	private int totalUnitCriticalCount = 0;
	private int totalUnitHighCount = 0;
	private int totalUnitMediumCount = 0;
	private int totalUnitLowCount = 0;
	private int totalUnitUndefinedCount = 0;

	private TestSuite testSuite = null;
	private CountDownLatch threadLatch;
	List<TestProgress> listenerList = new ArrayList<TestProgress>();

	// Objects required for running test cases
	private Map<String, TestDataProvider> dataProviderMap;
	private Class<?> prePostRunnableObj = null;
	private Method beforeTestSuite = null;
	private Method afterTestSuite = null;
	private Method beforeTest = null;
	private Method afterTest = null;
	private Method beforeTestUnit = null;
	private Method afterTestUnit = null;
	private Method afterFailedUnit = null;

	// Current TestCase and Unit
	private TestObjectWrapper currentTestCase = null;
	private TestUnitObjectWrapper currentTestUnit = null;
	private BDDScenario currentScenario = null;
	private BDDStep currentTestStep = null;

	// Test suite start time
	private long testSuiteStartTime = 0;
	// Test suite finish time
	private long testSuiteFinishTime = 0;

	// Index of parameterised Array
	private int testParameterIndex = 0;
	private int testUnitParameterIndex = 0;

	// Global parameters
	private Map<String, Object> globalObject = new HashMap<String, Object>();
	private Map<String, String> globalString = new HashMap<String, String>();
	private Object parameterisedObject1 = null;
	private Object parameterisedObject2 = null;

	UDP dashBoardConnector = null;

	// FeatureFile Related Parameter
	private Map<String, String> stepParameter = new HashMap<String, String>();

	/**
	 * Sets Test status in memory. Status is not finalised until
	 * generateTestSummary() function is called. This function stamps "FAIL HERE"
	 * warning as soon as status is set to FAIL so user can pin point location of
	 * the failure
	 * 
	 * @param testStatus Test Status
	 */
	@Deprecated
	public void setTestStatus(TestStatus testStatus) {
		setTestStatus(testStatus, "");
	}

	/**
	 * Sets Test status in memory. Status is not finalised until
	 * generateTestSummary() function is called. This function stamps "FAIL HERE"
	 * warning as soon as status is set to FAIL so user can pin point location of
	 * the failure
	 * 
	 * @param testStatus  Test Status
	 * @param description status description or reason
	 */
	public void setTestStatus(TestStatus testStatus, String description) {
		setTestStatus(testStatus, null, description);
	}

	/**
	 * Sets Test status in memory. Status is not finalised until
	 * generateTestSummary() function is called. This function stamps "FAIL HERE"
	 * warning as soon as status is set to FAIL so user can pin point location of
	 * the failure
	 * 
	 * @param testStatus  Test Status
	 * @param snapshot    Image file required to upload to report or null
	 * @param description status description or reason
	 */
	public void setTestStatus(TestStatus testStatus, File snapshot, String description) {

		/*
		 * Print status set by user and description/reason. User can not down grade
		 * (FAIL=>KTF=>SKIP=>PASS) finalTest status but down graded status is allowed to
		 * be printed.
		 */
		if (null == description) {
			getLogger().info("[" + TestStatus.getEnumName(testStatus.getValue()) + "]: ");
			notifyTestStatusUpdate(testStatus, snapshot, "");
		} else {
			getLogger().info("[" + TestStatus.getEnumName(testStatus.getValue()) + "]: " + description);
			notifyTestStatusUpdate(testStatus, snapshot, description);
		}

		/*
		 * This Method maintains Unit Test Status. User is not allows to down grade test
		 * status (FAIL=>KTF=>SKIP=>PASS). Which means once failed test case status can
		 * not become KTF, SKIP or PASS.
		 */
		if (testStatus.getValue() >= currentUnitTestStatus.getValue()) {
			currentUnitTestStatus = testStatus;
		}

		/*
		 * This Method maintains Test Case Test Status. User is not allows to down grade
		 * test status (FAIL=>KTF=>SKIP=>PASS). Which means once failed test case status
		 * can not become KTF, SKIP or PASS.
		 */
		if (testStatus.getValue() >= currentTestStatus.getValue()) {
			currentTestStatus = testStatus;

			// Append Warning in the log so user can pin point where test failed
			if (testStatus == TestStatus.FAIL) {
				//@formatter:off
				getLogger().info(FWStaticStore.ARTOS_TEST_FAIL_STAMP);
				//@formatter:on
			}
		}
	}

	/**
	 * Concludes final test result and generates summary report. This also includes
	 * bugTicketNumber if provided
	 * 
	 * @param t {@link TestObjectWrapper}
	 */
	protected void generateTestSummary(TestObjectWrapper t) {

		String strTestFQCN = t.getTestClassObject().getName();

		// Test is marked as known to fail and for some reason it pass then
		// consider that test Fail so user can look in to it
		if (isKnownToFail()) {
			if (getCurrentTestStatus() == TestStatus.PASS) {
				//@formatter:off
				getLogger().info(FWStaticStore.ARTOS_KTF_TEST_PASSED_STAMP);
				//@formatter:on
				setTestStatus(TestStatus.FAIL, "KTF Test passed, which is not as expected");
			}
		}

		// Add to total test count
		setTotalTestCount(getTotalTestCount() + 1);

		// Store count details per status
		if (getCurrentTestStatus() == TestStatus.PASS) {
			setCurrentPassCount(getCurrentPassCount() + 1);
		} else if (getCurrentTestStatus() == TestStatus.FAIL) {
			setCurrentFailCount(getCurrentFailCount() + 1);

			// Record test importance count in case of failure
			if (t.getTestImportance() == Importance.FATAL) {
				setTotalFatalCount(getTotalFatalCount() + 1);
			} else if (t.getTestImportance() == Importance.CRITICAL) {
				setTotalCriticalCount(getTotalCriticalCount() + 1);
			} else if (t.getTestImportance() == Importance.HIGH) {
				setTotalHighCount(getTotalHighCount() + 1);
			} else if (t.getTestImportance() == Importance.MEDIUM) {
				setTotalMediumCount(getTotalMediumCount() + 1);
			} else if (t.getTestImportance() == Importance.LOW) {
				setTotalLowCount(getTotalLowCount() + 1);
			} else if (t.getTestImportance() == Importance.UNDEFINED) {
				setTotalUndefinedCount(getTotalUndefinedCount() + 1);
			}
		} else if (getCurrentTestStatus() == TestStatus.SKIP) {
			setCurrentSkipCount(getCurrentSkipCount() + 1);
		} else if (getCurrentTestStatus() == TestStatus.KTF) {
			setCurrentKTFCount(getCurrentKTFCount() + 1);
		}

		long totalTestTime = t.getTestFinishTime() - t.getTestStartTime();
		// Finalise and add test result in log file
		getLogger().info("\nTest Result : {}", getCurrentTestStatus().name() + "\n");

		// Finalise and add test summary to Summary report
		appendSummaryReport(t, getCurrentTestStatus(), strTestFQCN, getStrBugTrackingReference(), getCurrentPassCount(),
				getCurrentFailCount(), getCurrentSkipCount(), getCurrentKTFCount(), totalTestTime);
		notifyTestResult(t, getCurrentTestStatus(), null, getStrBugTrackingReference());
		// Update test object with final outcome, if parameterised test cases then
		// status will be tracked in list
		t.getTestOutcomeList().add(getCurrentTestStatus());

		// Go through test unit of each log and print status of each test units into
		// report
		for (int i = 0; i < t.getTestUnitList().size(); i++) {
			TestUnitObjectWrapper unit = t.getTestUnitList().get(i);
			long totalTestUnitTime = unit.getTestUnitFinishTime() - unit.getTestUnitStartTime();

			// go through outcome list of test unit and print them all
			for (int j = 0; j < unit.getTestUnitOutcomeList().size(); j++) {
				if (unit.getDataProviderName().equals("")) {
					appendUnitSummaryReport(t, unit, unit.getTestUnitOutcomeList().get(j),
							unit.getTestUnitMethod().getName() + "(context)", unit.getBugTrackingNumber(),
							totalTestUnitTime);
				} else { // if data provider then append data provider number
					appendUnitSummaryReport(t, unit, unit.getTestUnitOutcomeList().get(j),
							unit.getTestUnitMethod().getName() + "(context)" + " : data[" + j + "]",
							unit.getBugTrackingNumber(), totalTestUnitTime);
				}
			}
		}

		// reset status for next test
		resetUnitTestStatus();
		resetTestStatus();
		// Reset Known to fail status
		setKnownToFail(false, "");
	}

	/**
	 * Concludes test unit result.
	 * 
	 * @param unit {@link TestUnitObjectWrapper}
	 */
	protected void generateUnitTestSummary(TestUnitObjectWrapper unit) {
		// Test is marked as known to fail and for some reason it pass then
		// consider that test Fail so user can look in to it
		if (unit.isKTF()) {
			if (getCurrentUnitTestStatus() == TestStatus.PASS) {
				//@formatter:off
				getLogger().info(FWStaticStore.ARTOS_KTF_TESTUNIT_PASSED_STAMP);
				//@formatter:on
				setTestStatus(TestStatus.FAIL, "KTF Test unit passed, which is not as expected");
			}
		}

		// Add to total test count
		setTotalUnitTestCount(getTotalUnitTestCount() + 1);

		// Store count details per status
		if (getCurrentUnitTestStatus() == TestStatus.PASS) {
			setCurrentUnitPassCount(getCurrentUnitPassCount() + 1);
		} else if (getCurrentUnitTestStatus() == TestStatus.FAIL) {
			setCurrentUnitFailCount(getCurrentUnitFailCount() + 1);

			// Record test importance count in case of failure
			if (unit.getTestImportance() == Importance.FATAL) {
				setTotalUnitFatalCount(getTotalUnitFatalCount() + 1);
			} else if (unit.getTestImportance() == Importance.CRITICAL) {
				setTotalUnitCriticalCount(getTotalUnitCriticalCount() + 1);
			} else if (unit.getTestImportance() == Importance.HIGH) {
				setTotalUnitHighCount(getTotalUnitHighCount() + 1);
			} else if (unit.getTestImportance() == Importance.MEDIUM) {
				setTotalUnitMediumCount(getTotalUnitMediumCount() + 1);
			} else if (unit.getTestImportance() == Importance.LOW) {
				setTotalUnitLowCount(getTotalUnitLowCount() + 1);
			} else if (unit.getTestImportance() == Importance.UNDEFINED) {
				setTotalUnitUndefinedCount(getTotalUnitUndefinedCount() + 1);
			} else {
				System.err.println(unit.getTestImportance().name());
			}
		} else if (getCurrentTestStatus() == TestStatus.SKIP) {
			setCurrentUnitSkipCount(getCurrentUnitSkipCount() + 1);
		} else if (getCurrentTestStatus() == TestStatus.KTF) {
			setCurrentUnitKTFCount(getCurrentUnitKTFCount() + 1);
		}

		// Update test object with final outcome, if parameterised test cases then
		// status will be tracked in list
		unit.getTestUnitOutcomeList().add(getCurrentUnitTestStatus());

		// print test unit outcome on the console and log file
//		getLogger().info("\n[" + TestStatus.getEnumName(getCurrentUnitTestStatus().getValue()) + "]: "
//				+ unit.getTestUnitMethod().getName() + "(context)");
		getLogger().info("\nUnit Result : " + TestStatus.getEnumName(getCurrentUnitTestStatus().getValue()));
		getLogger().info(FWStaticStore.ARTOS_LINE_BREAK_2);

		// Log test unit summary into extent report
		String bugTrackingNum = "".equals(unit.getBugTrackingNumber()) ? ""
				: " [Bug_Reference: " + unit.getBugTrackingNumber() + "]";
		notifyTestStatusUpdate(getCurrentUnitTestStatus(), null,
				"\n[" + TestStatus.getEnumName(getCurrentUnitTestStatus().getValue()) + "]: "
						+ unit.getTestUnitMethod().getName() + "(context) " + bugTrackingNum);
		notifyTestUnitResult(unit, getCurrentUnitTestStatus(), null, unit.getBugTrackingNumber());
		// reset status for next test
		resetUnitTestStatus();
	}

	public void generateTestScenarioSummary(BDDScenario scenario) {
		String strTestFQCN = scenario.getScenarioDescription();

		// Test is marked as known to fail and for some reason it pass then
		// consider that test Fail so user can look in to it
		if (isKnownToFail()) {
			if (getCurrentTestStatus() == TestStatus.PASS) {
				//@formatter:off
				getLogger().info(FWStaticStore.ARTOS_KTF_TEST_PASSED_STAMP);
				//@formatter:on
				setTestStatus(TestStatus.FAIL, "KTF Test passed, which is not as expected");
			}
		}

		// Add to total test count
		setTotalTestCount(getTotalTestCount() + 1);

		// Store count details per status
		if (getCurrentTestStatus() == TestStatus.PASS) {
			setCurrentPassCount(getCurrentPassCount() + 1);
		} else if (getCurrentTestStatus() == TestStatus.FAIL) {
			setCurrentFailCount(getCurrentFailCount() + 1);

			// Record test importance count in case of failure
			if (scenario.getTestImportance() == Importance.FATAL) {
				setTotalFatalCount(getTotalFatalCount() + 1);
			} else if (scenario.getTestImportance() == Importance.CRITICAL) {
				setTotalCriticalCount(getTotalCriticalCount() + 1);
			} else if (scenario.getTestImportance() == Importance.HIGH) {
				setTotalHighCount(getTotalHighCount() + 1);
			} else if (scenario.getTestImportance() == Importance.MEDIUM) {
				setTotalMediumCount(getTotalMediumCount() + 1);
			} else if (scenario.getTestImportance() == Importance.LOW) {
				setTotalLowCount(getTotalLowCount() + 1);
			} else if (scenario.getTestImportance() == Importance.UNDEFINED) {
				setTotalUndefinedCount(getTotalUndefinedCount() + 1);
			}
		} else if (getCurrentTestStatus() == TestStatus.SKIP) {
			setCurrentSkipCount(getCurrentSkipCount() + 1);
		} else if (getCurrentTestStatus() == TestStatus.KTF) {
			setCurrentKTFCount(getCurrentKTFCount() + 1);
		}

		long totalTestTime = scenario.getTestFinishTime() - scenario.getTestStartTime();
		// Finalise and add test result in log file
		getLogger().info("\nScenario Result : {}", getCurrentTestStatus().name() + "\n");

		// Finalise and add test summary to Summary report
		appendSummaryReport(scenario, getCurrentTestStatus(), strTestFQCN, getStrBugTrackingReference(),
				getCurrentPassCount(), getCurrentFailCount(), getCurrentSkipCount(), getCurrentKTFCount(),
				totalTestTime);
		notifyTestResult(scenario, getCurrentTestStatus(), null, getStrBugTrackingReference());
		// Update test object with final outcome, if parameterised test cases then
		// status will be tracked in list
		scenario.getTestOutcomeList().add(getCurrentTestStatus());

		// Go through test unit of each log and print status of each test units into
		// report
		for (int i = 0; i < scenario.getSteplist().size(); i++) {
			BDDStep step = scenario.getSteplist().get(i);
			TestUnitObjectWrapper unit = step.getUnit();
			long totalTestUnitTime = unit.getTestUnitFinishTime() - unit.getTestUnitStartTime();

			// go through outcome list of test unit and print them all
			for (int j = 0; j < unit.getTestUnitOutcomeList().size(); j++) {
				appendUnitSummaryReport(scenario, unit, unit.getTestUnitOutcomeList().get(j),
						step.getStepAction() + " " + step.getStepDescription(), unit.getBugTrackingNumber(),
						totalTestUnitTime);
			}

			// This has to be done in BDD because same test can be called again in next
			// scenario
			// This is to avoid mix up
			unit.getTestUnitOutcomeList().clear();
		}

		// reset status for next test
		resetUnitTestStatus();
		resetTestStatus();
		// Reset Known to fail status
		setKnownToFail(false, "");
	}

	public void generateStepTestSummary(BDDStep step) {
		TestUnitObjectWrapper unit = step.getUnit();
		// Test is marked as known to fail and for some reason it pass then
		// consider that test Fail so user can look in to it
		if (unit.isKTF()) {
			if (getCurrentUnitTestStatus() == TestStatus.PASS) {
				//@formatter:off
						getLogger().info(FWStaticStore.ARTOS_KTF_TESTUNIT_PASSED_STAMP);
						//@formatter:on
				setTestStatus(TestStatus.FAIL, "KTF Test unit passed, which is not as expected");
			}
		}

		// Add to total test count
		setTotalUnitTestCount(getTotalUnitTestCount() + 1);

		// Store count details per status
		if (getCurrentUnitTestStatus() == TestStatus.PASS) {
			setCurrentUnitPassCount(getCurrentUnitPassCount() + 1);
		} else if (getCurrentUnitTestStatus() == TestStatus.FAIL) {
			setCurrentUnitFailCount(getCurrentUnitFailCount() + 1);

			// Record test importance count in case of failure
			if (step.getUnit().getTestImportance() == Importance.FATAL) {
				setTotalUnitFatalCount(getTotalUnitFatalCount() + 1);
			} else if (step.getUnit().getTestImportance() == Importance.CRITICAL) {
				setTotalUnitCriticalCount(getTotalUnitCriticalCount() + 1);
			} else if (step.getUnit().getTestImportance() == Importance.HIGH) {
				setTotalUnitHighCount(getTotalUnitHighCount() + 1);
			} else if (step.getUnit().getTestImportance() == Importance.MEDIUM) {
				setTotalUnitMediumCount(getTotalUnitMediumCount() + 1);
			} else if (step.getUnit().getTestImportance() == Importance.LOW) {
				setTotalUnitLowCount(getTotalUnitLowCount() + 1);
			} else if (step.getUnit().getTestImportance() == Importance.UNDEFINED) {
				setTotalUnitUndefinedCount(getTotalUnitUndefinedCount() + 1);
			} else {
				System.err.println(step.getUnit().getTestImportance().name());
			}
		} else if (getCurrentTestStatus() == TestStatus.SKIP) {
			setCurrentUnitSkipCount(getCurrentUnitSkipCount() + 1);
		} else if (getCurrentTestStatus() == TestStatus.KTF) {
			setCurrentUnitKTFCount(getCurrentUnitKTFCount() + 1);
		}

		// Update test object with final outcome, if parameterised test cases then
		// status will be tracked in list
		unit.getTestUnitOutcomeList().add(getCurrentUnitTestStatus());

		// print test unit outcome on the console and log file
		getLogger().info("[" + TestStatus.getEnumName(getCurrentUnitTestStatus().getValue()) + "]: "
				+ step.getStepAction() + " " + step.getStepDescription());

		// Log test unit summary into extent report
		String bugTrackingNum = "".equals(unit.getBugTrackingNumber()) ? ""
				: " [Bug_Reference: " + unit.getBugTrackingNumber() + "]";
		notifyTestStatusUpdate(getCurrentUnitTestStatus(), null,
				"\n[" + TestStatus.getEnumName(getCurrentUnitTestStatus().getValue()) + "]: "
						+ step.getStepDescription() + " " + bugTrackingNum);
		// notify step final outcome
		notifyTestUnitResult(step, getCurrentUnitTestStatus(), null, unit.getBugTrackingNumber());
		// reset status for next test
		resetUnitTestStatus();
	}

	/**
	 * Append test summary to summary report
	 * 
	 * @param t                 {@link TestObjectWrapper} object
	 * @param status            Test status
	 * @param strTestFQCN       Test fully qualified class name (Example :
	 *                          com.test.unit.Abc)
	 * @param bugTrackingNumber BugTracking Number
	 * @param passCount         Current passed test count
	 * @param failCount         Current failed test count
	 * @param skipCount         Current skipped test count
	 * @param ktfCount          Current known to fail test count
	 * @param testDuration      Test duration
	 */
	private void appendSummaryReport(TestObjectWrapper t, TestStatus status, String strTestFQCN,
			String bugTrackingNumber, long passCount, long failCount, long skipCount, long ktfCount,
			long testDuration) {

		long hours = TimeUnit.MILLISECONDS.toHours(testDuration);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(testDuration) - TimeUnit.HOURS.toMinutes(hours);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(testDuration) - TimeUnit.HOURS.toSeconds(hours)
				- TimeUnit.MINUTES.toSeconds(minutes);
		long millis = testDuration - TimeUnit.HOURS.toMillis(hours) - TimeUnit.MINUTES.toMillis(minutes)
				- TimeUnit.SECONDS.toMillis(seconds);
		String testTime = String.format("duration:%3d:%2d:%2d.%3d", hours, minutes, seconds, millis).replace(" ", "0")
				.substring(0, 22);

		String testStatus = String.format("%-" + 4 + "s", TestStatus.getEnumName(status.getValue()));
		String testName = String.format("%-" + 100 + "s", strTestFQCN).replace(" ", ".").substring(0, 100);
		String JiraRef = String.format("%-" + 20 + "s", bugTrackingNumber).replace(" ", ".").substring(0, 20);
		String PassCount = String.format("%-" + 4 + "s", passCount);
		String SkipCount = String.format("%-" + 4 + "s", skipCount);
		String KTFCount = String.format("%-" + 4 + "s", ktfCount);
		String FailCount = String.format("%-" + 4 + "s", failCount);
		String TestImportance = String.format("%-" + 10 + "s",
				(t.getTestImportance() == Importance.UNDEFINED ? "" : t.getTestImportance().name()));

		String summaryString = testStatus + " = " + testName + " P:" + PassCount + " S:" + SkipCount + " K:" + KTFCount
				+ " F:" + FailCount + " [" + TestImportance + "] " + testTime + " " + JiraRef;
		getLogger().getSummaryLogger().info(summaryString);
		notifyTestCaseSummary(t.getTestClassObject().getName(), summaryString);
	}

	/**
	 * Append test summary to summary report
	 * 
	 * @param scenario          {@link BDDScenario} object
	 * @param status            Test status
	 * @param strTestFQCN       Test fully qualified class name (Example :
	 *                          com.test.unit.Abc)
	 * @param bugTrackingNumber BugTracking Number
	 * @param passCount         Current passed test count
	 * @param failCount         Current failed test count
	 * @param skipCount         Current skipped test count
	 * @param ktfCount          Current known to fail test count
	 * @param testDuration      Test duration
	 */
	private void appendSummaryReport(BDDScenario scenario, TestStatus status, String strTestFQCN,
			String bugTrackingNumber, long passCount, long failCount, long skipCount, long ktfCount,
			long testDuration) {

		long hours = TimeUnit.MILLISECONDS.toHours(testDuration);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(testDuration) - TimeUnit.HOURS.toMinutes(hours);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(testDuration) - TimeUnit.HOURS.toSeconds(hours)
				- TimeUnit.MINUTES.toSeconds(minutes);
		long millis = testDuration - TimeUnit.HOURS.toMillis(hours) - TimeUnit.MINUTES.toMillis(minutes)
				- TimeUnit.SECONDS.toMillis(seconds);
		String testTime = String.format("duration:%3d:%2d:%2d.%3d", hours, minutes, seconds, millis).replace(" ", "0")
				.substring(0, 22);

		String testStatus = String.format("%-" + 4 + "s", TestStatus.getEnumName(status.getValue()));
		String testName = String.format("%-" + 100 + "s", strTestFQCN).replace(" ", ".").substring(0, 100);
		String JiraRef = String.format("%-" + 20 + "s", bugTrackingNumber).replace(" ", ".").substring(0, 20);
		String PassCount = String.format("%-" + 4 + "s", passCount);
		String SkipCount = String.format("%-" + 4 + "s", skipCount);
		String KTFCount = String.format("%-" + 4 + "s", ktfCount);
		String FailCount = String.format("%-" + 4 + "s", failCount);
		String TestImportance = String.format("%-" + 10 + "s",
				(scenario.getTestImportance() == Importance.UNDEFINED ? "" : scenario.getTestImportance().name()));

		String summaryString = testStatus + " = " + testName + " P:" + PassCount + " S:" + SkipCount + " K:" + KTFCount
				+ " F:" + FailCount + " [" + TestImportance + "] " + testTime + " " + JiraRef;
		getLogger().getSummaryLogger().info(summaryString);
		notifyTestCaseSummary(scenario.scenarioDescription, summaryString);

	}

	/**
	 * Append test unit summary to summary report
	 * 
	 * @param t                 {@link TestObjectWrapper} object
	 * @param unit              {@link TestUnitObjectWrapper} object
	 * @param status            Test status
	 * @param testUnitName      Test unit name
	 * @param bugTrackingNumber BugTracking Number
	 * @param testDuration      Test duration
	 */
	private void appendUnitSummaryReport(TestObjectWrapper t, TestUnitObjectWrapper unit, TestStatus status,
			String testUnitName, String bugTrackingNumber, long testDuration) {

		long hours = TimeUnit.MILLISECONDS.toHours(testDuration);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(testDuration) - TimeUnit.HOURS.toMinutes(hours);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(testDuration) - TimeUnit.HOURS.toSeconds(hours)
				- TimeUnit.MINUTES.toSeconds(minutes);
		long millis = testDuration - TimeUnit.HOURS.toMillis(hours) - TimeUnit.MINUTES.toMillis(minutes)
				- TimeUnit.SECONDS.toMillis(seconds);
		String testTime = String.format("duration:%3d:%2d:%2d.%3d", hours, minutes, seconds, millis).replace(" ", "0")
				.substring(0, 22);

		String testStatus = String.format("%-" + 4 + "s", TestStatus.getEnumName(status.getValue()));
		String testName = String.format("%-" + 95 + "s", testUnitName).replace(" ", ".").substring(0, 95);
		String JiraRef = String.format("%-" + 20 + "s", bugTrackingNumber).replace(" ", ".").substring(0, 20);
		String PassCount = String.format("%-" + 4 + "s", "");
		String FailCount = String.format("%-" + 4 + "s", "");
		String SkipCount = String.format("%-" + 4 + "s", "");
		String KTFCount = String.format("%-" + 4 + "s", "");
		String TestImportance = String.format("%-" + 10 + "s",
				(unit.getTestImportance() == Importance.UNDEFINED ? "" : unit.getTestImportance().name()));

		String summaryString = "  |--" + testStatus + " = " + testName + "  :" + PassCount + "  :" + FailCount + "  :"
				+ SkipCount + "  :" + KTFCount + " [" + TestImportance + "] " + testTime + " " + JiraRef;
		getLogger().getSummaryLogger().info(summaryString);
		notifyTestUnitSummary(t.getTestClassObject().getName(), summaryString);
	}

	/**
	 * Append test unit summary to summary report
	 * 
	 * @param scenario          {@link BDDScenario} object
	 * @param unit              {@link TestUnitObjectWrapper} object
	 * @param status            Test status
	 * @param testUnitName      Test unit name
	 * @param bugTrackingNumber BugTracking Number
	 * @param testDuration      Test duration
	 */
	private void appendUnitSummaryReport(BDDScenario scenario, TestUnitObjectWrapper unit, TestStatus status,
			String testUnitName, String bugTrackingNumber, long testDuration) {

		long hours = TimeUnit.MILLISECONDS.toHours(testDuration);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(testDuration) - TimeUnit.HOURS.toMinutes(hours);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(testDuration) - TimeUnit.HOURS.toSeconds(hours)
				- TimeUnit.MINUTES.toSeconds(minutes);
		long millis = testDuration - TimeUnit.HOURS.toMillis(hours) - TimeUnit.MINUTES.toMillis(minutes)
				- TimeUnit.SECONDS.toMillis(seconds);
		String testTime = String.format("duration:%3d:%2d:%2d.%3d", hours, minutes, seconds, millis).replace(" ", "0")
				.substring(0, 22);

		String testStatus = String.format("%-" + 4 + "s", TestStatus.getEnumName(status.getValue()));
		String testName = String.format("%-" + 95 + "s", testUnitName).substring(0, 95);
		String JiraRef = String.format("%-" + 20 + "s", bugTrackingNumber).replace(" ", ".").substring(0, 20);
		String PassCount = String.format("%-" + 4 + "s", "");
		String FailCount = String.format("%-" + 4 + "s", "");
		String SkipCount = String.format("%-" + 4 + "s", "");
		String KTFCount = String.format("%-" + 4 + "s", "");
		String TestImportance = String.format("%-" + 10 + "s",
				(unit.getTestImportance() == Importance.UNDEFINED ? "" : unit.getTestImportance().name()));

		String summaryString = "  |--" + testStatus + " = " + testName + "  :" + PassCount + "  :" + FailCount + "  :"
				+ SkipCount + "  :" + KTFCount + " [" + TestImportance + "] " + testTime + " " + JiraRef;
		getLogger().getSummaryLogger().info(summaryString);
		notifyTestUnitSummary(scenario.scenarioDescription, summaryString);
	}

	/**
	 * Prints Organisation details to each log files
	 */
	private void printMendatoryInfo() {
		//@formatter:off
		
		String organisationInfo = "\n************************************ Header Start ******************************************"
								 +"\nOrganisation_Name : " + FWStaticStore.frameworkConfig.getOrganisation_Name()
								 +"\nOrganisation_Country : " + FWStaticStore.frameworkConfig.getOrganisation_Country()
								 +"\nOrganisation_Address : " + FWStaticStore.frameworkConfig.getOrganisation_Address()
								 +"\nOrganisation_Phone : " + FWStaticStore.frameworkConfig.getOrganisation_Contact_Number()
								 +"\nOrganisation_Email : " + FWStaticStore.frameworkConfig.getOrganisation_Email()
								 +"\nOrganisation_Website : " + FWStaticStore.frameworkConfig.getOrganisation_Website()
								 +"\n************************************ Header End ********************************************";
		//@formatter:on

		if (FWStaticStore.frameworkConfig.isEnableBanner()) {
			getLogger().getGeneralLogger().info(Banner.getBanner());
			getLogger().getSummaryLogger().info(Banner.getBanner());
			getLogger().getRealTimeLogger().info(Banner.getBanner());
		}

		if (FWStaticStore.frameworkConfig.isEnableOrganisationInfo()) {
			getLogger().getGeneralLogger().info(organisationInfo);
			getLogger().getSummaryLogger().info(organisationInfo);
			getLogger().getRealTimeLogger().info(organisationInfo);
		}
	}

	/**
	 * Print selected System Info to log file
	 */
	private void printUsefulInfo() {

		SystemProperties sysProp = FWStaticStore.systemProperties;

		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		sb.append("Test FrameWork Info");
		sb.append("\n");
		sb.append("* Artos version => " + FWStaticStore.ARTOS_BUILD_VERSION);
		sb.append("\n");
		sb.append("* Artos build date => " + FWStaticStore.ARTOS_BUILD_DATE);
		sb.append("\n");
		sb.append("* Java Runtime Environment version => " + sysProp.getJavaRuntimeEnvironmentVersion());
		sb.append("\n");
		sb.append("* Java Virtual Machine specification version => "
				+ sysProp.getJavaVirtualMachineSpecificationVersion());
		sb.append("\n");
		sb.append("* Java Runtime Environment specification version => "
				+ sysProp.getJavaRuntimeEnvironmentSpecificationVersion());
		sb.append("\n");
		sb.append("* Java class path => " + sysProp.getJavaClassPath());
		sb.append("\n");
		sb.append("* List of paths to search when loading libraries => "
				+ sysProp.getListofPathstoSearchWhenLoadingLibraries());
		sb.append("\n");
		sb.append("* Operating system name => " + sysProp.getOperatingSystemName());
		sb.append("\n");
		sb.append("* Operating system architecture => " + sysProp.getOperatingSystemArchitecture());
		sb.append("\n");
		sb.append("* Operating system version => " + sysProp.getOperatingSystemVersion());
		sb.append("\n");
		sb.append("* File separator (\"/\" on UNIX) => " + sysProp.getFileSeparator());
		sb.append("\n");
		sb.append("* Path separator (\":\" on UNIX) => " + sysProp.getPathSeparator());
		sb.append("\n");
		sb.append("* User's account name => " + sysProp.getUserAccountName());
		sb.append("\n");
		sb.append("* User's home directory => " + sysProp.getUserHomeDir());
		sb.append("\n");

		getLogger().debug(sb.toString());
	}

	private void resetTestStatus() {
		// Reset for next test
		currentTestStatus = TestStatus.PASS;

	}

	private void resetUnitTestStatus() {
		// Reset for next test
		currentUnitTestStatus = TestStatus.PASS;
	}

	/**
	 * Get the calling function/method name
	 * 
	 * <PRE>
	 * depth in the call stack 
	 * 0 = current method
	 * 1 = call method
	 * etc..
	 * </PRE>
	 * 
	 * @return method name
	 */
	public String printMethodName() {
		String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
		getLogger().debug("Method : " + methodName + "()");
		return methodName;
	}

	/**
	 * Generates test plan using annotation provided in the test case classes
	 * 
	 * @param removeSkippedTests true = remove skipped test from test plan, false =
	 *                           print all test plan
	 * @return {@code TestPlanWrapper} list
	 */
	public List<TestPlanWrapper> getTestPlan(boolean removeSkippedTests) {

		String packageName = "";
		if (null != getPrePostRunnableObj().getPackage()) {
			packageName = getPrePostRunnableObj().getPackage().getName();
		}

		ScanTestSuite reflection = new ScanTestSuite(this, packageName);
		List<TestPlanWrapper> testPlanList = new ArrayList<>();
		for (TestObjectWrapper testObject : reflection.getTestObjWrapperList(true, removeSkippedTests, true)) {
			TestPlanWrapper testPlan = new TestPlanWrapper();

			testPlan.setTestPlan(testObject.getTestClassObject().getName(), testObject.getTestPlanDescription(),
					testObject.getTestPlanPreparedBy(), testObject.getTestPlanPreparationDate(),
					testObject.getTestreviewedBy(), testObject.getTestReviewDate(), testObject.getTestPlanBDD());
			testPlan.setTestPlanGroup(testObject.getGroupList().toString());
			testPlan.setTestPlanKTF(testObject.isKTF(), testObject.getBugTrackingNumber());
			testPlan.setTestPlanSkip(testObject.getTestsequence(), testObject.isSkipTest(),
					testObject.getDataProviderName());
			testPlan.setTestImportance(Importance.getEnumName(testObject.getTestImportance().getValue()));

			testPlanList.add(testPlan);
		}
		return testPlanList;
	}

	/**
	 * @param formatBDD true = BDD string will be formatted Prints test plan for
	 *                  current context
	 */
	public void printTestPlan(boolean formatBDD) {
		List<TestPlanWrapper> testPlanList = getTestPlan(true);

		getLogger().debug("\nTest Plan" + "\n=========");

		for (TestPlanWrapper t : testPlanList) {

			StringBuilder sb = new StringBuilder();

			sb.append("\n");
			sb.append("Test Name	: " + t.getTestCaseName());
			sb.append("\n");
			if (!"".equals(t.getTestPreparedBy())) {
				sb.append("Written BY	: " + t.getTestPreparedBy());
				sb.append("\n");
			}
			if (0 != t.getSequenece()) {
				sb.append("Sequence	: " + t.getSequenece());
				sb.append("\n");
			}
			if (!"".equals(t.getTestPreparationDate())) {
				sb.append("Date		: " + t.getTestPreparationDate());
				sb.append("\n");
			}
			if (!"".equals(t.getTestDescription())) {
				sb.append("Short Desc	: " + t.getTestDescription());
				sb.append("\n");
			}
			if (!"".equals(t.getTestBDD())) {
				String BDD;
				if (formatBDD) {
					BDD = processBDD(t.getTestBDD());
				} else {
					BDD = t.getTestBDD();
				}
				sb.append("BDD Test Plan	: " + BDD);
				sb.append("\n");
			}

			getLogger().info(sb.toString());
		}
	}

	/**
	 * 
	 * @param testPlanBDD BDD test string
	 * @return BDD formated string
	 */
	public String processBDD(String testPlanBDD) {
		String strBDD = testPlanBDD.replaceAll("\\b([Gg][Ii][Vv][Ee][Nn])\\b", "\nGIVEN");
		strBDD = strBDD.replaceAll("\\b([Aa][Nn][Dd])\\b", "\nAND");
		strBDD = strBDD.replaceAll("\\b([Ww][Hh][Ee][Nn])\\b", "\nWHEN");
		strBDD = strBDD.replaceAll("\\b([Tt][Hh][Ee][Nn])\\b", "\nTHEN");
		strBDD = strBDD.replaceAll("\\b([Bb][Uu][Tt])\\b", "\nBUT");
		return strBDD;
	}

	// *******************************************************************
	// Listener
	// *******************************************************************
	protected void registerListener(TestProgress listener) {
		listenerList.add(listener);
	}

	protected void deRegisterListener(TestProgress listener) {
		listenerList.remove(listener);
	}

	protected void deRegisterAllListener() {
		listenerList.clear();
	}

	private void notifyTestStatusUpdate(TestStatus testStatus, File snapshot, String Msg) {
		for (TestProgress listener : listenerList) {
			listener.testCaseStatusUpdate(testStatus, snapshot, Msg);
		}
	}

	private void notifyTestResult(TestObjectWrapper t, TestStatus testStatus, File snapshot, String Msg) {
		for (TestProgress listener : listenerList) {
			listener.testResult(t, testStatus, snapshot, Msg);
		}
	}

	private void notifyTestResult(BDDScenario scenario, TestStatus testStatus, File snapshot, String Msg) {
		for (TestProgress listener : listenerList) {
			listener.testResult(scenario, testStatus, snapshot, Msg);
		}
	}

	private void notifyTestUnitResult(TestUnitObjectWrapper unit, TestStatus testStatus, File snapshot, String Msg) {
		for (TestProgress listener : listenerList) {
			listener.testUnitResult(unit, testStatus, snapshot, Msg);
		}
	}

	private void notifyTestUnitResult(BDDStep step, TestStatus testStatus, File snapshot, String Msg) {
		for (TestProgress listener : listenerList) {
			listener.testUnitResult(step, testStatus, snapshot, Msg);
		}
	}

	private void notifyTestCaseSummary(String FQCN, String description) {
		for (TestProgress listener : listenerList) {
			listener.testCaseSummaryPrinting(FQCN, description);
		}
	}

	private void notifyTestUnitSummary(String FQCN, String description) {
		for (TestProgress listener : listenerList) {
			listener.testUnitSummaryPrinting(FQCN, description);
		}
	}

	// *******************************************************************
	// Getter and Setters
	// *******************************************************************

	protected TestSuite getTestSuite() {
		return testSuite;
	}

	protected void setTestSuite(TestSuite testSuite) {
		this.testSuite = testSuite;
	}

	protected Class<?> getPrePostRunnableObj() {
		return prePostRunnableObj;
	}

	protected void setPrePostRunnableObj(Class<?> prePostRunnableObj) {
		this.prePostRunnableObj = prePostRunnableObj;
	}

	protected CountDownLatch getThreadLatch() {
		return threadLatch;
	}

	protected void setThreadLatch(CountDownLatch threadLatch) {
		this.threadLatch = threadLatch;
	}

	/**
	 * Returns general logger object
	 * 
	 * @return {@code LogWrapper} object
	 * 
	 * @see LogWrapper
	 */
	public LogWrapper getLogger() {
		return getLogWrapper();
	}

	/**
	 * Returns current test status
	 * 
	 * @return Current test status
	 */
	public TestStatus getCurrentTestStatus() {
		return currentTestStatus;
	}

	/**
	 * 
	 * @return true if test is marked known to fail
	 */
	public boolean isKnownToFail() {
		return KnownToFail;
	}

	/**
	 * This method should be exercised as initial line for every test case, this
	 * allows user to set properties of test case as known to fail. If known to fail
	 * test case passes then it will be marked as a fail. Which will help user
	 * figure out if developer has fixed some feature without letting them know and
	 * gives test engineer an opportunity to re-look at the test case behaviour.
	 * 
	 * @param knownToFail             true|false
	 * @param strBugTrackingReference Bug Tracking reference (Example : JIRA number)
	 */
	protected void setKnownToFail(boolean knownToFail, String strBugTrackingReference) {
		KnownToFail = knownToFail;
		setStrBugTrackingReference(strBugTrackingReference);
	}

	private String getStrBugTrackingReference() {
		return strBugTrackingReference;
	}

	private void setStrBugTrackingReference(String strBugTrackingRef) {
		this.strBugTrackingReference = strBugTrackingRef;
	}

	/**
	 * Returns Test suite run duration time
	 * 
	 * @return Test duration in milliseconds
	 */
	public long getTestSuiteTimeDuration() {
		return getTestSuiteFinishTime() - getTestSuiteStartTime();
	}

	/**
	 * Sets Object which is available globally to all test cases. User must maintain
	 * Key for the HashTable
	 * 
	 * @param key = Key to recognise an Object
	 * @param obj = Object to be stored
	 */
	public void setGlobalObject(String key, Object obj) {
		globalObject.put(key, obj);
	}

	/**
	 * Gets Globally set Object from the Map using provided Key.
	 * 
	 * @param key String key to retrieve an object
	 * @return Object associated with given key
	 */
	public Object getGlobalObject(String key) {
		return globalObject.get(key);
	}

	/**
	 * Gets Globally set String from the Map using provided Key.
	 * 
	 * @param key String key to retrieve an object
	 * @return String associated with given key
	 */
	public String getGlobalString(String key) {
		return globalString.get(key);
	}

	/**
	 * Sets String which is available globally to all test cases. User must maintain
	 * Key for the HashTable
	 * 
	 * @param key   = Key to recognise an Object
	 * @param value = Value to be stored
	 */
	public void setGlobalString(String key, String value) {
		globalString.put(key, value);
	}

	/**
	 * Returns total pass test count at the time of request
	 * 
	 * @return Test pass count
	 */
	public long getCurrentPassCount() {
		return currentPassCount;
	}

	private void setCurrentPassCount(long currentPassCount) {
		this.currentPassCount = currentPassCount;
	}

	/**
	 * Returns total fail test count at the time of request
	 * 
	 * @return Failed test count
	 */
	public long getCurrentFailCount() {
		return currentFailCount;
	}

	private void setCurrentFailCount(long currentFailCount) {
		this.currentFailCount = currentFailCount;
	}

	/**
	 * Returns total skip test count at the time of request
	 * 
	 * @return Skipped test count
	 */
	public long getCurrentSkipCount() {
		return currentSkipCount;
	}

	private void setCurrentSkipCount(long currentSkipCount) {
		this.currentSkipCount = currentSkipCount;
	}

	/**
	 * Returns total known To fail test count at the time of request
	 * 
	 * @return Known to fail test count
	 */
	public long getCurrentKTFCount() {
		return currentKTFCount;
	}

	private void setCurrentKTFCount(long currentKTFCount) {
		this.currentKTFCount = currentKTFCount;
	}

	/**
	 * Returns total number of test count
	 * 
	 * @return total test count
	 */
	public long getTotalTestCount() {
		return totalTestCount;
	}

	private void setTotalTestCount(long totalTestCount) {
		this.totalTestCount = totalTestCount;
	}

	/**
	 * Get OrganisedLogger Object
	 * 
	 * @return {@link LogWrapper}
	 */
	public LogWrapper getLogWrapper() {
		return logWrapper;
	}

	/**
	 * Set LogWrapper object
	 * 
	 * @param logWrapper logWrapper Object
	 */
	protected void setOrganisedLogger(LogWrapper logWrapper) {
		this.logWrapper = logWrapper;
		printMendatoryInfo();
		printUsefulInfo();
	}

	/**
	 * Returns Test suite start time
	 * 
	 * @return test suite start time in milliseconds
	 */
	public long getTestSuiteStartTime() {
		return testSuiteStartTime;
	}

	protected void setTestSuiteStartTime(long testSuiteStartTime) {
		this.testSuiteStartTime = testSuiteStartTime;
	}

	/**
	 * Returns Test suite finish time
	 * 
	 * @return test suite finish time in milliseconds
	 */
	public long getTestSuiteFinishTime() {
		return testSuiteFinishTime;
	}

	protected void setTestSuiteFinishTime(long testSuiteFinishTime) {
		this.testSuiteFinishTime = testSuiteFinishTime;
	}

	public Map<String, TestDataProvider> getDataProviderMap() {
		return dataProviderMap;
	}

	protected void setDataProviderMap(Map<String, TestDataProvider> dataProviderMap) {
		this.dataProviderMap = dataProviderMap;
	}

	/**
	 * Returns parameterised first Object if present otherwise Null
	 * 
	 * @return parameterised object or Null
	 */
	public Object getParameterisedObject1() {
		return parameterisedObject1;
	}

	protected void setParameterisedObject1(Object parameterisedObject1) {
		this.parameterisedObject1 = parameterisedObject1;
	}

	/**
	 * Returns parameterised second Object if present otherwise Null
	 * 
	 * @return parameterised object or Null
	 */
	public Object getParameterisedObject2() {
		return parameterisedObject2;
	}

	protected void setParameterisedObject2(Object parameterisedObject2) {
		this.parameterisedObject2 = parameterisedObject2;
	}

	public TestStatus getCurrentUnitTestStatus() {
		return currentUnitTestStatus;
	}

	protected void setCurrentUnitTestStatus(TestStatus currentUnitTestStatus) {
		this.currentUnitTestStatus = currentUnitTestStatus;
	}

	protected Method getBeforeTestSuite() {
		return beforeTestSuite;
	}

	protected void setBeforeTestSuite(Method beforeTestSuite) {
		this.beforeTestSuite = beforeTestSuite;
	}

	protected Method getAfterTestSuite() {
		return afterTestSuite;
	}

	protected void setAfterTestSuite(Method afterTestSuite) {
		this.afterTestSuite = afterTestSuite;
	}

	protected Method getBeforeTest() {
		return beforeTest;
	}

	protected void setBeforeTest(Method beforeTest) {
		this.beforeTest = beforeTest;
	}

	protected Method getAfterTest() {
		return afterTest;
	}

	protected void setAfterTest(Method afterTest) {
		this.afterTest = afterTest;
	}

	protected Method getBeforeTestUnit() {
		return beforeTestUnit;
	}

	protected void setBeforeTestUnit(Method beforeTestUnit) {
		this.beforeTestUnit = beforeTestUnit;
	}

	protected Method getAfterTestUnit() {
		return afterTestUnit;
	}

	protected void setAfterTestUnit(Method afterTestUnit) {
		this.afterTestUnit = afterTestUnit;
	}

	public int getTotalFatalCount() {
		return totalFatalCount;
	}

	protected void setTotalFatalCount(int totalFatalCount) {
		this.totalFatalCount = totalFatalCount;
	}

	public int getTotalCriticalCount() {
		return totalCriticalCount;
	}

	protected void setTotalCriticalCount(int totalCriticalCount) {
		this.totalCriticalCount = totalCriticalCount;
	}

	public int getTotalHighCount() {
		return totalHighCount;
	}

	protected void setTotalHighCount(int totalHighCount) {
		this.totalHighCount = totalHighCount;
	}

	public int getTotalMediumCount() {
		return totalMediumCount;
	}

	protected void setTotalMediumCount(int totalMediumCount) {
		this.totalMediumCount = totalMediumCount;
	}

	public int getTotalLowCount() {
		return totalLowCount;
	}

	protected void setTotalLowCount(int totalLowCount) {
		this.totalLowCount = totalLowCount;
	}

	public int getTotalUndefinedCount() {
		return totalUndefinedCount;
	}

	protected void setTotalUndefinedCount(int totalUndefinedCount) {
		this.totalUndefinedCount = totalUndefinedCount;
	}

	public boolean isRunningFromScript() {
		return CliProcessor.getTestScriptFile() == null ? false : true;
	}

	public String getStepParameter(String key) {
		return stepParameter.get(key);
	}

	protected Map<String, String> getStepParameterMap() {
		return stepParameter;
	}

	protected void setStepParameter(Map<String, String> stepParameter) {
		this.stepParameter = stepParameter;
	}

	public int getTestParameterIndex() {
		return testParameterIndex;
	}

	public void setTestParameterIndex(int testParameterIndex) {
		this.testParameterIndex = testParameterIndex;
	}

	public int getTestUnitParameterIndex() {
		return testUnitParameterIndex;
	}

	public void setTestUnitParameterIndex(int testUnitParameterIndex) {
		this.testUnitParameterIndex = testUnitParameterIndex;
	}

	public long getTotalUnitTestCount() {
		return totalUnitTestCount;
	}

	protected void setTotalUnitTestCount(long totalUnitTestCount) {
		this.totalUnitTestCount = totalUnitTestCount;
	}

	public long getCurrentUnitPassCount() {
		return currentUnitPassCount;
	}

	protected void setCurrentUnitPassCount(long currentUnitPassCount) {
		this.currentUnitPassCount = currentUnitPassCount;
	}

	public long getCurrentUnitFailCount() {
		return currentUnitFailCount;
	}

	protected void setCurrentUnitFailCount(long currentUnitFailCount) {
		this.currentUnitFailCount = currentUnitFailCount;
	}

	public long getCurrentUnitSkipCount() {
		return currentUnitSkipCount;
	}

	protected void setCurrentUnitSkipCount(long currentUnitSkipCount) {
		this.currentUnitSkipCount = currentUnitSkipCount;
	}

	public long getCurrentUnitKTFCount() {
		return currentUnitKTFCount;
	}

	protected void setCurrentUnitKTFCount(long currentUnitKTFCount) {
		this.currentUnitKTFCount = currentUnitKTFCount;
	}

	public int getTotalUnitFatalCount() {
		return totalUnitFatalCount;
	}

	protected void setTotalUnitFatalCount(int totalUnitFatalCount) {
		this.totalUnitFatalCount = totalUnitFatalCount;
	}

	public int getTotalUnitCriticalCount() {
		return totalUnitCriticalCount;
	}

	protected void setTotalUnitCriticalCount(int totalUnitCriticalCount) {
		this.totalUnitCriticalCount = totalUnitCriticalCount;
	}

	public int getTotalUnitHighCount() {
		return totalUnitHighCount;
	}

	protected void setTotalUnitHighCount(int totalUnitHighCount) {
		this.totalUnitHighCount = totalUnitHighCount;
	}

	public int getTotalUnitMediumCount() {
		return totalUnitMediumCount;
	}

	protected void setTotalUnitMediumCount(int totalUnitMediumCount) {
		this.totalUnitMediumCount = totalUnitMediumCount;
	}

	public int getTotalUnitLowCount() {
		return totalUnitLowCount;
	}

	protected void setTotalUnitLowCount(int totalUnitLowCount) {
		this.totalUnitLowCount = totalUnitLowCount;
	}

	public int getTotalUnitUndefinedCount() {
		return totalUnitUndefinedCount;
	}

	protected void setTotalUnitUndefinedCount(int totalUnitUndefinedCount) {
		this.totalUnitUndefinedCount = totalUnitUndefinedCount;
	}

	public String getTestSuiteName() {
		return this.testSuite.getSuiteName();
	}

	public UDP getDashBoardConnector() {
		return dashBoardConnector;
	}

	protected void setDashBoardConnector(UDP dashBoardConnector) {
		this.dashBoardConnector = dashBoardConnector;
	}

	public Method getAfterFailedUnit() {
		return afterFailedUnit;
	}

	protected void setAfterFailedUnit(Method afterFailedUnit) {
		this.afterFailedUnit = afterFailedUnit;
	}

	protected void setCurrentTestCase(TestObjectWrapper t) {
		currentTestCase = t;
	}

	public TestObjectWrapper getCurrentTestCase() {
		return currentTestCase;
	}

	protected void setCurrentTestUnit(TestUnitObjectWrapper unit) {
		currentTestUnit = unit;
	}

	public TestUnitObjectWrapper getCurrentTestUnit() {
		return currentTestUnit;
	}

	protected void setCurrentTestScenario(BDDScenario scenario) {
		currentScenario = scenario;
	}

	public BDDScenario getCurrentTestScenario() {
		return currentScenario;
	}

	protected void setCurrentTestStep(BDDStep step) {
		currentTestStep = step;
	}

	public BDDStep getCurrentTestStep() {
		return currentTestStep;
	}

}
