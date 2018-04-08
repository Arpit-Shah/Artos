package com.arpitos.infra.annotation;

public class TestObjectWrapper {

	Class<?> cls = null;
	boolean skipTest = false;
	String testPlanScenario = "";
	String testPlanDescription = "";
	String testPlanPreparedBy = "";
	String testPlanPreparationDate = "";
	String testreviewedBy = "";
	String testReviewData = "";

	public TestObjectWrapper(Class<?> cls, boolean skipTest, String testPlanScenario, String testPlanDescription, String testPlanPreparedBy,
			String testPlanPreparationDate, String testreviewedBy, String testReviewData) {
		super();
		this.skipTest = skipTest;
		this.cls = cls;
		this.testPlanScenario = testPlanScenario;
		this.testPlanDescription = testPlanDescription;
		this.testPlanPreparedBy = testPlanPreparedBy;
		this.testPlanPreparationDate = testPlanPreparationDate;
		this.testreviewedBy = testreviewedBy;
		this.testReviewData = testReviewData;
	}

	public String getTestPlanScenario() {
		return testPlanScenario;
	}

	public void setTestPlanScenario(String testPlanScenario) {
		this.testPlanScenario = testPlanScenario;
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

	public String getTestReviewData() {
		return testReviewData;
	}

	public void setTestReviewData(String testReviewData) {
		this.testReviewData = testReviewData;
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

}
