package com.arpitos.infra;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.core.Logger;

import com.arpitos.infra.Enums.TestStatus;

/**
 * This is TestContext which is wrapper around all objects/tools/loggers user
 * may need during test case execution. This class is also responsible for
 * summarizing test results.
 * 
 * @author ArpitS
 *
 */
public class TestContext {

	private OrganisedLog organiseLogger;
	private OrganisationInfo organisationInfo;
	private TestStatus currentTestStatus = TestStatus.PASS;
	private boolean KnownToFail = false;
	private String strJIRARef = "";
	private long totalTestCount = 0;
	private long currentPassCount = 0;
	private long currentFailCount = 0;
	private long currentSkipCount = 0;
	private long currentKTFCount = 0;
	Map<String, Object> globalObjectsHashMap = new HashMap<String, Object>();
	public static TestContext context;

	/**
	 * Constructor
	 * 
	 * @param organisedLog
	 *            = Logger object
	 */
	public TestContext(OrganisedLog organisedLog) {
		this.organisationInfo = new OrganisationInfo();
		this.organiseLogger = organisedLog;
		printOrganisationInfo();
		setTestContext(this);
	}

	/**
	 * Prints Organization details to each log files
	 */
	private void printOrganisationInfo() {
		//@formatter:off
		getOrganiseLogger().getGeneralLogger().info(Banner.getBanner());
		getOrganiseLogger().getGeneralLogger().info("************************************ Header Start ******************************************"
													+"\nOrganisation_Name : " + getOrganisationInfo().getOrganisation_Name()
													+"\nOrganisation_Country : " + getOrganisationInfo().getOrganisation_Country()
													+"\nOrganisation_Address : " + getOrganisationInfo().getOrganisation_Address()
													+"\nOrganisation_Phone : " + getOrganisationInfo().getOrganisation_Contact_Number()
													+"\nOrganisation_Website : " + getOrganisationInfo().getOrganisation_Website()
													+"\n************************************ Header End ********************************************");

		getOrganiseLogger().getSummaryLogger().info(Banner.getBanner());
		getOrganiseLogger().getSummaryLogger().info("************************************ Header Start ******************************************"
													+"\nOrganisation_Name : " + getOrganisationInfo().getOrganisation_Name()
													+"\nOrganisation_Country : " + getOrganisationInfo().getOrganisation_Country()
													+"\nOrganisation_Address : " + getOrganisationInfo().getOrganisation_Address()
													+"\nOrganisation_Phone : " + getOrganisationInfo().getOrganisation_Contact_Number()
													+"\nOrganisation_Website : " + getOrganisationInfo().getOrganisation_Website()
													+"\n************************************ Header End ********************************************");
		//@formatter:on
	}

	/**
	 * Sets Test status in memory. Status is not finalized until
	 * generateTestSummary() function is called. This function stamps "FAIL
	 * HERE" warning as soon as status is set to FAIL so user can pin point
	 * location of the failure
	 * 
	 * @param testStatus
	 *            = Test Status
	 */
	public void setCurrentTestStatus(TestStatus testStatus) {
		if (testStatus.getValue() >= currentTestStatus.getValue()) {
			currentTestStatus = testStatus;

			// Append Warning in the log so user can pin point where test failed
			if (testStatus == TestStatus.FAIL) {
				//@formatter:off
				getLogger().warn("**********************************"
								+ "\n*********** FAIL HERE ************"
								+ "\n**********************************");
				//@formatter:on
			}
		}
	}

	/**
	 * Concludes final test result and generates summary report. This also
	 * includes bugTicketNumber if provided
	 * 
	 * @param strTestName
	 *            = Test Name
	 */
	public void generateTestSummary(String strTestName) {
		// Test is marked as known to fail and for some reason it pass then
		// consider that test Fail so user can look in to it
		if (isKnownToFail()) {
			if (getCurrentTestStatus() == TestStatus.PASS) {
				//@formatter:off
				getLogger().warn("**********************************"
								+ "\n******** KTF TEST PASSED *********"
								+ "\n**********************************");
				//@formatter:on
				setCurrentTestStatus(TestStatus.FAIL);
			}
		}

		// Add to total test count
		setTotalTestCount(getTotalTestCount() + 1);

		// Store count details per status
		if (getCurrentTestStatus() == TestStatus.PASS) {
			setCurrentPassCount(getCurrentPassCount() + 1);
		} else if (getCurrentTestStatus() == TestStatus.FAIL) {
			setCurrentFailCount(getCurrentFailCount() + 1);
		} else if (getCurrentTestStatus() == TestStatus.SKIP) {
			setCurrentSkipCount(getCurrentSkipCount() + 1);
		} else if (getCurrentTestStatus() == TestStatus.KTF) {
			setCurrentKTFCount(getCurrentKTFCount() + 1);
		}

		// Finalize and add test result in log file
		getLogger().info("Test Result : " + getCurrentTestStatus().name());
		// Finalize and add test summary to Summary report
		getOrganiseLogger().appendSummaryReport(getCurrentTestStatus(), strTestName, getStrJIRARef(), getCurrentPassCount(), getCurrentFailCount(),
				getCurrentSkipCount(), getCurrentKTFCount());

		// reset statuses for next test
		resetTestStatus();
		setKnownToFail(false, "");
	}

	private void resetTestStatus() {
		// Reset for next test
		currentTestStatus = TestStatus.PASS;
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
		getLogger().debug("\nMethod : " + methodName + "()");
		return methodName;
	}

	public Logger getLogger() {
		return (Logger) getOrganiseLogger().getGeneralLogger();
	}

	public TestStatus getCurrentTestStatus() {
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
	 * @param Key
	 *            = String key to retrive an object
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

	public OrganisedLog getOrganiseLogger() {
		return organiseLogger;
	}

	public void setOrganiseLogger(OrganisedLog organiseLogger) {
		this.organiseLogger = organiseLogger;
	}

	public static TestContext getTestContext() {
		return context;
	}

	public static void setTestContext(TestContext currentTestContext) {
		TestContext.context = currentTestContext;
	}

	public OrganisationInfo getOrganisationInfo() {
		return organisationInfo;
	}

	public void setOrganisationInfo(OrganisationInfo organisationInfo) {
		this.organisationInfo = organisationInfo;
	}

}
