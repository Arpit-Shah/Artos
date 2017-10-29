package com.arpit.infra;

import java.util.HashMap;
import java.util.Map;

public class TestContext {

	private OrganisedLog logger;
	private Status currentTestStatus = Status.PASS;
	private boolean KnownToFail = false;
	private String strJIRARef = "";
	private long totalTestCount = 0;
	private long currentPassCount = 0;
	private long currentFailCount = 0;
	private long currentSkipCount = 0;
	private long currentKTFCount = 0;
	Map<String, Object> globalObjectsHashMap = new HashMap<String, Object>();

	public TestContext(OrganisedLog logger) {
		this.logger = logger;
	}

	/**
	 * Enums for setting test status
	 * 
	 * @author arpit_000
	 *
	 */
	public enum Status {
		PASS(0), SKIP(1), KTF(2), FAIL(3);

		private final int status;

		Status(int status) {
			this.status = status;
		}

		public int getValue() {
			return status;
		}

		public String getEnumName(int status) {
			for (Status e : Status.values()) {
				if (status == e.getValue()) {
					return e.name();
				}
			}
			return null;
		}
	}

	/**
	 * Sets Test status in memory. Status is not finalized until
	 * generateTestSummary() function is called
	 * 
	 * @param testStatus
	 */
	public void setCurrentTestStatus(Status testStatus) {
		if (testStatus.getValue() >= currentTestStatus.getValue()) {
			currentTestStatus = testStatus;
			if (testStatus == Status.FAIL) {
				logger.println_err("**********************************");
				logger.println_err("*********** FAIL HERE ************");
				logger.println_err("**********************************");
			}
		}
	}

	/**
	 * Concludes final test result and generates summary report. All Status and
	 * flags are reset at the same time to avoid polluting next test result with
	 * previous test status.
	 * 
	 * @param strTestName
	 */
	public void generateTestSummary(String strTestName) {
		// Test is marked as known to fail and for some reason it pass then
		// consider that test Fail so user can look in to it
		if (isKnownToFail()) {
			if (getCurrentTestStatus() == Status.PASS) {
				logger.println_err("**********************************");
				logger.println_err("******** KTF TEST PASSED *********");
				logger.println_err("**********************************");
				setCurrentTestStatus(Status.FAIL);
			}
		}

		// Add to total test count
		setTotalTestCount(getTotalTestCount() + 1);

		// Store count details per status
		if (getCurrentTestStatus() == Status.PASS) {
			setCurrentPassCount(getCurrentPassCount() + 1);
		} else if (getCurrentTestStatus() == Status.FAIL) {
			setCurrentFailCount(getCurrentFailCount() + 1);
		} else if (getCurrentTestStatus() == Status.SKIP) {
			setCurrentSkipCount(getCurrentSkipCount() + 1);
		} else if (getCurrentTestStatus() == Status.KTF) {
			setCurrentKTFCount(getCurrentKTFCount() + 1);
		}

		// Finalize and add test result in log file
		getLogger().println("\nTest Result : " + getCurrentTestStatus().name());
		// Finalize and add test summary to Summary report
		getLogger().appendSummaryReport(getCurrentTestStatus(), strTestName, getStrJIRARef(), getCurrentPassCount(), getCurrentFailCount(),
				getCurrentSkipCount(), getCurrentKTFCount());

		// reset statuses for next test
		resetTestStatus();
		setKnownToFail(false, "");
	}

	private void resetTestStatus() {
		// Reset for next test
		currentTestStatus = Status.PASS;
	}

	/**
	 * Get the method name for a depth in call stack.
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
		getLogger().println("\nMethod : " + methodName + "()");
		return methodName;
	}

	public OrganisedLog getLogger() {
		return logger;
	}

	public void setLogger(OrganisedLog logger) {
		this.logger = logger;
	}

	public Status getCurrentTestStatus() {
		return currentTestStatus;
	}

	public boolean isKnownToFail() {
		return KnownToFail;
	}

	public void setKnownToFail(boolean knownToFail, String strJIRARef) {
		KnownToFail = knownToFail;
		setStrJIRARef(strJIRARef);
	}

	private String getStrJIRARef() {
		return strJIRARef;
	}

	private void setStrJIRARef(String strJIRARef) {
		this.strJIRARef = strJIRARef;
	}

	/**
	 * Sets Object which is available globally to all test cases. User must
	 * maintain Key for the HashTable
	 * 
	 * @param Key
	 * @param obj
	 */
	public void setGlobalObject(String key, Object obj) {
		globalObjectsHashMap.put(key, obj);
	}

	/**
	 * Gets Globally set Object provided valid key is supplied.
	 * 
	 * @param Key
	 */
	public void getGlobalObject(String key) {
		globalObjectsHashMap.get(key);
	}

	public long getCurrentPassCount() {
		return currentPassCount;
	}

	private void setCurrentPassCount(long currentPassCount) {
		this.currentPassCount = currentPassCount;
	}

	public long getCurrentFailCount() {
		return currentFailCount;
	}

	private void setCurrentFailCount(long currentFailCount) {
		this.currentFailCount = currentFailCount;
	}

	public long getCurrentSkipCount() {
		return currentSkipCount;
	}

	private void setCurrentSkipCount(long currentSkipCount) {
		this.currentSkipCount = currentSkipCount;
	}

	public long getCurrentKTFCount() {
		return currentKTFCount;
	}

	private void setCurrentKTFCount(long currentKTFCount) {
		this.currentKTFCount = currentKTFCount;
	}

	public long getTotalTestCount() {
		return totalTestCount;
	}

	private void setTotalTestCount(long totalTestCount) {
		this.totalTestCount = totalTestCount;
	}

}
