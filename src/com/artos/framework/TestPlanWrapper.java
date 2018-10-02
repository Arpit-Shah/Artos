package com.artos.framework;

public class TestPlanWrapper {

	String TestCaseName = "";
	String TestDescription = "";
	String TestPreparedBy = "";
	String TestPreparationDate = "";
	String TestReviewedBy = "";
	String TestReviewedDate = "";
	String TestBDD = "";

	public TestPlanWrapper(String testCaseName, String testDescription, String testPreparedBy, String testPreparationDate, String testReviewedBy,
			String testReviewedDate, String testBDD) {
		super();
		TestCaseName = testCaseName;
		TestDescription = testDescription;
		TestPreparedBy = testPreparedBy;
		TestPreparationDate = testPreparationDate;
		TestReviewedBy = testReviewedBy;
		TestReviewedDate = testReviewedDate;
		TestBDD = testBDD;
	}

	public String getTestCaseName() {
		return TestCaseName;
	}

	public String getTestDescription() {
		return TestDescription;
	}

	public String getTestPreparedBy() {
		return TestPreparedBy;
	}

	public String getTestPreparationDate() {
		return TestPreparationDate;
	}

	public String getTestReviewedBy() {
		return TestReviewedBy;
	}

	public String getTestReviewedDate() {
		return TestReviewedDate;
	}

	public String getTestBDD() {
		return TestBDD;
	}
}
