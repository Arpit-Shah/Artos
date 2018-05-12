package com.arpitos.framework;

/**
 * This class wraps test object with other necessary information which is
 * helpful during test execution
 * 
 * @author arpit
 *
 */
public class TestObjectWrapper {

	Class<?> cls = null;
	boolean skipTest = false;
	int testsequence = 0;
	String testCaseLabel = "";

	String testPlanDescription = "";
	String testPlanPreparedBy = "";
	String testPlanPreparationDate = "";
	String testreviewedBy = "";
	String testReviewDate = "";

	/**
	 * Default constructor
	 * 
	 * @param cls
	 *            test class object
	 * @param skipTest
	 *            skip property as specified in annotation
	 * @param testsequence
	 *            test sequence as specified in annotation
	 * @param testCaseLabel
	 *            test label as specified in annotation
	 * @param testPlanDescription
	 *            test plan as specified in annotation
	 * @param testPlanPreparedBy
	 *            info about test creator as specified in annotation
	 * @param testPlanPreparationDate
	 *            test preparation date as specified in annotation
	 * @param testreviewedBy
	 *            test reviewer name as specified in annotation
	 * @param testReviewDate
	 *            test review date as specified in annotation
	 */
	public TestObjectWrapper(Class<?> cls, boolean skipTest, int testsequence, String testCaseLabel, String testPlanDescription,
			String testPlanPreparedBy, String testPlanPreparationDate, String testreviewedBy, String testReviewDate) {
		super();

		this.cls = cls;

		this.skipTest = skipTest;
		this.testsequence = testsequence;
		this.testCaseLabel = testCaseLabel;

		this.testPlanDescription = testPlanDescription;
		this.testPlanPreparedBy = testPlanPreparedBy;
		this.testPlanPreparationDate = testPlanPreparationDate;
		this.testreviewedBy = testreviewedBy;
		this.testReviewDate = testReviewDate;
	}

	public String getTestPlanDescription() {
		return testPlanDescription;
	}

	public void setTestPlanDescription(String testPlanDescription) {
		this.testPlanDescription = testPlanDescription;
	}

	public String getTestPlanPreparedBy() {
		return testPlanPreparedBy;
	}

	public void setTestPlanPreparedBy(String testPlanPreparedBy) {
		this.testPlanPreparedBy = testPlanPreparedBy;
	}

	public String getTestPlanPreparationDate() {
		return testPlanPreparationDate;
	}

	public void setTestPlanPreparationDate(String testPlanPreparationDate) {
		this.testPlanPreparationDate = testPlanPreparationDate;
	}

	public String getTestreviewedBy() {
		return testreviewedBy;
	}

	public void setTestreviewedBy(String testreviewedBy) {
		this.testreviewedBy = testreviewedBy;
	}

	public String getTestReviewDate() {
		return testReviewDate;
	}

	public void setTestReviewDate(String testReviewDate) {
		this.testReviewDate = testReviewDate;
	}

	public Class<?> getCls() {
		return cls;
	}

	public void setCls(Class<?> cls) {
		this.cls = cls;
	}

	public boolean isSkipTest() {
		return skipTest;
	}

	public void setSkipTest(boolean skipTest) {
		this.skipTest = skipTest;
	}

	public int getTestsequence() {
		return testsequence;
	}

	public void setTestsequence(int testsequence) {
		this.testsequence = testsequence;
	}

	public String getTestCaseLabel() {
		return testCaseLabel;
	}

	public void setTestCaseLabel(String testCaseLabel) {
		this.testCaseLabel = testCaseLabel;
	}

}
