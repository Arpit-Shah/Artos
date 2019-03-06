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
import com.artos.framework.TestDataProvider;
import com.artos.framework.TestObjectWrapper;
import com.artos.framework.TestUnitObjectWrapper;
import com.artos.framework.xml.TestSuite;
import com.artos.interfaces.TestProgress;

/**
 * This is TestContext which is wrapper around all objects/tools/loggers user may need during test case execution. This class is also responsible for
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

	// Test suite start time
	private long testSuiteStartTime = 0;
	// Test suite finish time
	private long testSuiteFinishTime = 0;

	private Map<String, Object> globalObject = new HashMap<String, Object>();
	private Map<String, String> globalString = new HashMap<String, String>();
	private Object parameterisedObject1 = null;
	private Object parameterisedObject2 = null;

	/**
	 * Sets Test status in memory. Status is not finalised until generateTestSummary() function is called. This function stamps "FAIL HERE" warning as
	 * soon as status is set to FAIL so user can pin point location of the failure
	 * 
	 * @param testStatus Test Status
	 */
	@Deprecated
	public void setTestStatus(TestStatus testStatus) {
		setTestStatus(testStatus, "");
	}

	/**
	 * Sets Test status in memory. Status is not finalised until generateTestSummary() function is called. This function stamps "FAIL HERE" warning as
	 * soon as status is set to FAIL so user can pin point location of the failure
	 * 
	 * @param testStatus Test Status
	 * @param description status description or reason
	 */
	public void setTestStatus(TestStatus testStatus, String description) {

		/*
		 * Print status set by user and description/reason. User can not down grade (FAIL=>KTF=>SKIP=>PASS) finalTest status but down graded status is
		 * allowed to be printed.
		 */
		if (null == description) {
			getLogger().info("[" + testStatus.getEnumName(testStatus.getValue()) + "] : ");
			notifyTestStatusUpdate(testStatus, "");
		} else {
			getLogger().info("[" + testStatus.getEnumName(testStatus.getValue()) + "] : " + description);
			notifyTestStatusUpdate(testStatus, description);
		}

		/*
		 * This Method maintains Unit Test Status. User is not allows to down grade test status (FAIL=>KTF=>SKIP=>PASS). Which means once failed test
		 * case status can not become KTF, SKIP or PASS.
		 */
		if (testStatus.getValue() >= currentUnitTestStatus.getValue()) {
			currentUnitTestStatus = testStatus;
		}

		/*
		 * This Method maintains Test Case Test Status. User is not allows to down grade test status (FAIL=>KTF=>SKIP=>PASS). Which means once failed
		 * test case status can not become KTF, SKIP or PASS.
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
	 * Concludes final test result and generates summary report. This also includes bugTicketNumber if provided
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
		getLogger().info("\nTest Result : {}", getCurrentTestStatus().name() + "\n" + FWStaticStore.ARTOS_LINE_BREAK_1);

		// Finalise and add test summary to Summary report
		appendSummaryReport(t, getCurrentTestStatus(), strTestFQCN, getStrBugTrackingReference(), getCurrentPassCount(), getCurrentFailCount(),
				getCurrentSkipCount(), getCurrentKTFCount(), totalTestTime);
		notifyTestResult(getCurrentTestStatus(), getStrBugTrackingReference());
		// Update test object with final outcome, if parameterised test cases then status will be tracked in list
		t.getTestOutcomeList().add(getCurrentTestStatus());

		// Go through test unit of each log and print status of each test units into report
		for (int i = 0; i < t.getTestUnitList().size(); i++) {
			TestUnitObjectWrapper unit = t.getTestUnitList().get(i);
			long totalTestUnitTime = unit.getTestUnitFinishTime() - unit.getTestUnitStartTime();

			// go through outcome list of test unit and print them all
			for (int j = 0; j < unit.getTestUnitOutcomeList().size(); j++) {
				if (unit.getDataProviderName().equals("")) {
					appendUnitSummaryReport(unit, unit.getTestUnitOutcomeList().get(j), unit.getTestUnitMethod().getName() + "(context)",
							unit.getBugTrackingNumber(), totalTestUnitTime);
				} else { // if data provider then append data provider number
					appendUnitSummaryReport(unit, unit.getTestUnitOutcomeList().get(j),
							unit.getTestUnitMethod().getName() + "(context)" + " : data[" + j + "]", unit.getBugTrackingNumber(), totalTestUnitTime);
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

		// Update test object with final outcome, if parameterised test cases then status will be tracked in list
		unit.getTestUnitOutcomeList().add(getCurrentUnitTestStatus());

		// print test unit outcome on the console and log file
		getLogger().info("[" + getCurrentUnitTestStatus().getEnumName(getCurrentUnitTestStatus().getValue()) + "] : "
				+ unit.getTestUnitMethod().getName() + "(context)");

		// Log test unit summary into extent report
		String bugTrackingNum = "".equals(unit.getBugTrackingNumber()) ? unit.getBugTrackingNumber() : "=>" + unit.getBugTrackingNumber();
		notifyTestStatusUpdate(getCurrentUnitTestStatus(), "\n[" + getCurrentUnitTestStatus().getEnumName(getCurrentUnitTestStatus().getValue())
				+ "] : " + unit.getTestUnitMethod().getName() + "(context) " + bugTrackingNum);

		// reset status for next test
		resetUnitTestStatus();
	}

	/**
	 * Append test summary to summary report
	 * 
	 * @param t {@link TestObjectWrapper} object
	 * @param status Test status
	 * @param strTestFQCN Test fully qualified class name (Example : com.test.unit.Abc)
	 * @param bugTrackingNumber BugTracking Number
	 * @param passCount Current passed test count
	 * @param failCount Current failed test count
	 * @param skipCount Current skipped test count
	 * @param ktfCount Current known to fail test count
	 * @param testDuration Test duration
	 */
	private void appendSummaryReport(TestObjectWrapper t, TestStatus status, String strTestFQCN, String bugTrackingNumber, long passCount,
			long failCount, long skipCount, long ktfCount, long testDuration) {

		long hours = TimeUnit.MILLISECONDS.toHours(testDuration);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(testDuration) - TimeUnit.HOURS.toMinutes(hours);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(testDuration) - TimeUnit.HOURS.toSeconds(hours) - TimeUnit.MINUTES.toSeconds(minutes);
		long millis = testDuration - TimeUnit.HOURS.toMillis(hours) - TimeUnit.MINUTES.toMillis(minutes) - TimeUnit.SECONDS.toMillis(seconds);
		String testTime = String.format("duration:%3d:%2d:%2d.%2d", hours, minutes, seconds, millis).replace(" ", "0");

		String testStatus = String.format("%-" + 4 + "s", status.getEnumName(status.getValue()));
		String testName = String.format("%-" + 100 + "s", strTestFQCN).replace(" ", ".");
		String JiraRef = String.format("%-" + 15 + "s", bugTrackingNumber);
		String PassCount = String.format("%-" + 4 + "s", passCount);
		String SkipCount = String.format("%-" + 4 + "s", skipCount);
		String KTFCount = String.format("%-" + 4 + "s", ktfCount);
		String FailCount = String.format("%-" + 4 + "s", failCount);
		String TestImportance = String.format("%-" + 10 + "s", (t.getTestImportance() == Importance.UNDEFINED ? "" : t.getTestImportance().name()));

		getLogger().getSummaryLogger().info(testStatus + " = " + testName + " P:" + PassCount + " S:" + SkipCount + " K:"
				+ KTFCount + " F:" + FailCount + " [" + TestImportance + "] " + testTime + " " + JiraRef);
	}

	/**
	 * Append test unit summary to summary report
	 * 
	 * @param unit {@link TestUnitObjectWrapper} object
	 * @param status Test status
	 * @param testUnitName Test unit name
	 * @param bugTrackingNumber BugTracking Number
	 * @param testDuration Test duration
	 */
	private void appendUnitSummaryReport(TestUnitObjectWrapper unit, TestStatus status, String testUnitName, String bugTrackingNumber,
			long testDuration) {

		long hours = TimeUnit.MILLISECONDS.toHours(testDuration);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(testDuration) - TimeUnit.HOURS.toMinutes(hours);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(testDuration) - TimeUnit.HOURS.toSeconds(hours) - TimeUnit.MINUTES.toSeconds(minutes);
		long millis = testDuration - TimeUnit.HOURS.toMillis(hours) - TimeUnit.MINUTES.toMillis(minutes) - TimeUnit.SECONDS.toMillis(seconds);
		String testTime = String.format("duration:%3d:%2d:%2d.%2d", hours, minutes, seconds, millis).replace(" ", "0");

		String testStatus = String.format("%-" + 4 + "s", status.getEnumName(status.getValue()));
		String testName = String.format("%-" + 95 + "s", testUnitName);
		String JiraRef = String.format("%-" + 15 + "s", bugTrackingNumber);
		String PassCount = String.format("%-" + 4 + "s", "");
		String FailCount = String.format("%-" + 4 + "s", "");
		String SkipCount = String.format("%-" + 4 + "s", "");
		String KTFCount = String.format("%-" + 4 + "s", "");
		String TestImportance = String.format("%-" + 10 + "s",
				(unit.getTestImportance() == Importance.UNDEFINED ? "" : unit.getTestImportance().name()));

		getLogger().getSummaryLogger().info("  |--" + testStatus + " = " + testName + "  :" + PassCount + "  :" + FailCount + "  :" + SkipCount
				+ "  :" + KTFCount + " [" + TestImportance + "] " + testTime + " " + JiraRef);
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
		sb.append("* Java Virtual Machine specification version => " + sysProp.getJavaVirtualMachineSpecificationVersion());
		sb.append("\n");
		sb.append("* Java Runtime Environment specification version => " + sysProp.getJavaRuntimeEnvironmentSpecificationVersion());
		sb.append("\n");
		sb.append("* Java class path => " + sysProp.getJavaClassPath());
		sb.append("\n");
		sb.append("* List of paths to search when loading libraries => " + sysProp.getListofPathstoSearchWhenLoadingLibraries());
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

	// *******************************************************************
	// Listener
	// *******************************************************************
	public void registerListener(TestProgress listener) {
		listenerList.add(listener);
	}

	public void deRegisterListener(TestProgress listener) {
		listenerList.remove(listener);
	}

	public void deRegisterAllListener() {
		listenerList.clear();
	}

	private void notifyTestStatusUpdate(TestStatus testStatus, String Msg) {
		for (TestProgress listener : listenerList) {
			listener.testCaseStatusUpdate(testStatus, Msg);
		}
	}

	private void notifyTestResult(TestStatus testStatus, String Msg) {
		for (TestProgress listener : listenerList) {
			listener.testResult(testStatus, Msg);
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
	 * This method should be exercised as initial line for every test case, this allows user to set properties of test case as known to fail. If known
	 * to fail test case passes then it will be marked as a fail. Which will help user figure out if developer has fixed some feature without letting
	 * them know and gives test engineer an opportunity to re-look at the test case behaviour.
	 * 
	 * @param knownToFail true|false
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
	protected long getTestSuiteTimeDuration() {
		return getTestSuiteFinishTime() - getTestSuiteStartTime();
	}

	/**
	 * Sets Object which is available globally to all test cases. User must maintain Key for the HashTable
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
	 * Sets String which is available globally to all test cases. User must maintain Key for the HashTable
	 * 
	 * @param key = Key to recognise an Object
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

}
