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
package com.artos.framework.infra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Level;

import com.artos.framework.Enums.TestStatus;
import com.artos.framework.FrameworkConfig;
import com.artos.framework.SystemProperties;

/**
 * This is TestContext which is wrapper around all objects/tools/loggers user
 * may need during test case execution. This class is also responsible for
 * Summarising test results.
 */
public class TestContext {

	private LogWrapper logWrapper;
	private FrameworkConfig frameworkConfig;
	private SystemProperties systemProperties;
	private TestStatus currentTestStatus = TestStatus.PASS;
	private boolean KnownToFail = false;

	private String strBugTrackingReference = "";
	private long totalTestCount = 0;
	private long currentPassCount = 0;
	private long currentFailCount = 0;
	private long currentSkipCount = 0;
	private long currentKTFCount = 0;
	private List<String> passTestList = new ArrayList<>();
	private List<String> failedTestList = new ArrayList<>();
	private List<String> skippedTestList = new ArrayList<>();
	private List<String> ktfTestList = new ArrayList<>();

	// Test suite start time
	private long testSuiteStartTime;
	// Test suite finish time
	private long testSuiteFinishTime;

	Map<String, Object> globalObjectsHashMap = new HashMap<String, Object>();

	/**
	 * Constructor
	 */
	public TestContext() {
		this.frameworkConfig = new FrameworkConfig(true);
		this.systemProperties = new SystemProperties();
	}

	/**
	 * Sets Test status in memory. Status is not finalised until
	 * generateTestSummary() function is called. This function stamps "FAIL HERE"
	 * warning as soon as status is set to FAIL so user can pin point location of
	 * the failure
	 * 
	 * @param testStatus
	 *            Test Status
	 */
	public void setTestStatus(TestStatus testStatus) {
		if (testStatus.getValue() >= currentTestStatus.getValue()) {
			currentTestStatus = testStatus;

			// Append Warning in the log so user can pin point where test failed
			if (testStatus == TestStatus.FAIL) {
				//@formatter:off
				getLogger().warn("**********************************"
								+"\n*********** FAIL HERE ************"
								+"\n**********************************");
				//@formatter:on
			}
		}
	}

	/**
	 * Concludes final test result and generates summary report. This also includes
	 * bugTicketNumber if provided
	 * 
	 * @param strTestFQCN
	 *            Test fully qualified class name (Example : com.test.unit.Abc)
	 * @param testStartTime
	 *            Test start Time in milliseconds
	 * @param testFinishTime
	 *            Test finish time in milliseconds
	 */
	public void generateTestSummary(String strTestFQCN, long testStartTime, long testFinishTime) {
		// Test is marked as known to fail and for some reason it pass then
		// consider that test Fail so user can look in to it
		if (isKnownToFail()) {
			if (getCurrentTestStatus() == TestStatus.PASS) {
				//@formatter:off
				getLogger().warn("\n**********************************"
								+"\n******** KTF TEST PASSED *********"
								+"\n**********************************");
				//@formatter:on
				setTestStatus(TestStatus.FAIL);
			}
		}

		// Add to total test count
		setTotalTestCount(getTotalTestCount() + 1);

		// Store count details per status
		if (getCurrentTestStatus() == TestStatus.PASS) {
			setCurrentPassCount(getCurrentPassCount() + 1);
			passTestList.add(strTestFQCN);
		} else if (getCurrentTestStatus() == TestStatus.FAIL) {
			setCurrentFailCount(getCurrentFailCount() + 1);
			failedTestList.add(strTestFQCN);
		} else if (getCurrentTestStatus() == TestStatus.SKIP) {
			setCurrentSkipCount(getCurrentSkipCount() + 1);
			skippedTestList.add(strTestFQCN);
		} else if (getCurrentTestStatus() == TestStatus.KTF) {
			setCurrentKTFCount(getCurrentKTFCount() + 1);
			ktfTestList.add(strTestFQCN);
		}

		long totalTestTime = testFinishTime - testStartTime;
		// Finalise and add test result in log file
		getLogger().info("\nTest Result : {}", getCurrentTestStatus().name());
		// Finalise and add test summary to Summary report
		getLogWrapper().appendSummaryReport(getCurrentTestStatus(), strTestFQCN, getStrBugTrackingReference(), getCurrentPassCount(),
				getCurrentFailCount(), getCurrentSkipCount(), getCurrentKTFCount(), totalTestTime);
		// reset status for next test
		resetTestStatus();
		setKnownToFail(false, "");
	}

	private void resetTestStatus() {
		// Reset for next test
		currentTestStatus = TestStatus.PASS;
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
	 * Returns Log Level Enum value based on Framework configuration set in XML file
	 * 
	 * @see Level
	 * 
	 * @return LogLevel
	 * @see Level
	 */
	public Level getLoglevelFromXML() {
		String logLevel = getFrameworkConfig().getLogLevel();
		if (logLevel.equals("info")) {
			return Level.INFO;
		}
		if (logLevel.equals("all")) {
			return Level.ALL;
		}
		if (logLevel.equals("fatal")) {
			return Level.FATAL;
		}
		if (logLevel.equals("trace")) {
			return Level.TRACE;
		}
		if (logLevel.equals("warn")) {
			return Level.WARN;
		}
		if (logLevel.equals("debug")) {
			return Level.DEBUG;
		}
		return Level.DEBUG;
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
	 * @param knownToFail
	 *            true|false
	 * @param strBugTrackingReference
	 *            Bug Tracking reference (Example : JIRA number)
	 */
	public void setKnownToFail(boolean knownToFail, String strBugTrackingReference) {
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
	 * @param key
	 *            = Key to recognize an Object
	 * @param obj
	 *            = Object to be stored
	 */
	public void setGlobalObject(String key, Object obj) {
		globalObjectsHashMap.put(key, obj);
	}

	/**
	 * Gets Globally set Object from the Map using provided Key.
	 * 
	 * @param key
	 *            = String key to retrieve an object
	 * @return Object associated with given key
	 */
	public Object getGlobalObject(String key) {
		return globalObjectsHashMap.get(key);
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
	 * @param logWrapper
	 *            logWrapper Object
	 */
	public void setOrganisedLogger(LogWrapper logWrapper) {
		this.logWrapper = logWrapper;
	}

	/**
	 * Returns Test suite start time
	 * 
	 * @return test suite start time in milliseconds
	 */
	public long getTestSuiteStartTime() {
		return testSuiteStartTime;
	}

	public void setTestSuiteStartTime(long testSuiteStartTime) {
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

	public void setTestSuiteFinishTime(long testSuiteFinishTime) {
		this.testSuiteFinishTime = testSuiteFinishTime;
	}

	/**
	 * Returns FrameworkConfig object
	 * 
	 * @return {@code FrameworkConfig} object
	 * 
	 * @see FrameworkConfig
	 */
	public FrameworkConfig getFrameworkConfig() {
		return frameworkConfig;
	}

	public void setFrameworkConfig(FrameworkConfig frameworkConfig) {
		this.frameworkConfig = frameworkConfig;
	}

	/**
	 * 
	 * @return {@code SystemProperties} object
	 * 
	 * @see SystemProperties
	 */
	public SystemProperties getSystemProperties() {
		return systemProperties;
	}

	public void setSystemProperties(SystemProperties systemProperties) {
		this.systemProperties = systemProperties;
	}

	public List<String> getPassTestList() {
		return passTestList;
	}

	public List<String> getFailedTestList() {
		return failedTestList;
	}

	public List<String> getSkippedTestList() {
		return skippedTestList;
	}

	public List<String> getKtfTestList() {
		return ktfTestList;
	}

}
