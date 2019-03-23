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
import java.util.List;

import com.artos.framework.Enums.Importance;
import com.artos.framework.Enums.TestStatus;

public class TestUnitObjectWrapper {

	// TestTracking variables
	long testUnitStartTime;
	long testUnitFinishTime;

	/*
	 * This will be used to store all parameterised tests status. If test case is not parameterised test case then only one status will be stored
	 */
	List<TestStatus> testUnitOutcomeList = new ArrayList<>();

	// TestUnit
	Method testUnitMethod = null;
	boolean skipTest = false;
	int testsequence = 0;
	String dataProviderName = "";
	long testTimeout = 0;

	// TestPlan
	String testPlanDescription = "";
	String testPlanPreparedBy = "";
	String testPlanPreparationDate = "";
	String testreviewedBy = "";
	String testReviewDate = "";
	String testPlanBDD = "";
	
	// StepDefinition
	String stepDefinition = "";

	// testImportance
	Importance testImportance = Importance.UNDEFINED;

	// Group
	List<String> groupList = new ArrayList<>();

	// KnowToFail
	boolean KTF = false;
	String bugTrackingNumber = "";

	// ExpectedException
	List<Class<? extends Throwable>> expectedExceptionList = null;
	String exceptionContains = "";
	Boolean enforce = true;

	/**
	 * Default constructor
	 * 
	 * @param method test method object
	 * @param skipTest skip property as specified in annotation
	 * @param testsequence test sequence as specified in annotation
	 * @param dataProviderName dataProvider name provided in {@code DataProvider} annotation
	 * @param testTimeout test execution timeout, 0=no timeout
	 */
	public TestUnitObjectWrapper(Method method, boolean skipTest, int testsequence, String dataProviderName, long testTimeout) {
		super();

		this.testUnitMethod = method;
		this.skipTest = skipTest;
		this.testsequence = testsequence;
		this.dataProviderName = dataProviderName;
		this.testTimeout = testTimeout;
	}

	public long getTestUnitStartTime() {
		return testUnitStartTime;
	}

	public void setTestUnitStartTime(long testUnitStartTime) {
		this.testUnitStartTime = testUnitStartTime;
	}

	public long getTestUnitFinishTime() {
		return testUnitFinishTime;
	}

	public void setTestUnitFinishTime(long testUnitFinishTime) {
		this.testUnitFinishTime = testUnitFinishTime;
	}

	public Method getTestUnitMethod() {
		return testUnitMethod;
	}

	public void setTestUnitMethod(Method testUnitMethod) {
		this.testUnitMethod = testUnitMethod;
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

	public long getTestTimeout() {
		return testTimeout;
	}

	public void setTestTimeout(long testTimeout) {
		this.testTimeout = testTimeout;
	}

	public List<Class<? extends Throwable>> getExpectedExceptionList() {
		return expectedExceptionList;
	}

	public void setExpectedExceptionList(List<Class<? extends Throwable>> expectedExceptionList) {
		this.expectedExceptionList = expectedExceptionList;
	}

	public String getExceptionContains() {
		return exceptionContains;
	}

	public void setExceptionContains(String exceptionContains) {
		this.exceptionContains = exceptionContains;
	}

	public Boolean isEnforceException() {
		return enforce;
	}

	public void setEnforce(Boolean enforce) {
		this.enforce = enforce;
	}

	public String getDataProviderName() {
		return dataProviderName;
	}

	public void setDataProviderName(String dataProviderName) {
		this.dataProviderName = dataProviderName;
	}

	public boolean isKTF() {
		return KTF;
	}

	public void setKTF(boolean kTF) {
		KTF = kTF;
	}

	public String getBugTrackingNumber() {
		return bugTrackingNumber;
	}

	public void setBugTrackingNumber(String bugTrackingNumber) {
		this.bugTrackingNumber = bugTrackingNumber;
	}

	public List<TestStatus> getTestUnitOutcomeList() {
		return testUnitOutcomeList;
	}

	public void setTestUnitOutcomeList(List<TestStatus> testUnitOutcomeList) {
		this.testUnitOutcomeList = testUnitOutcomeList;
	}

	public List<String> getGroupList() {
		return groupList;
	}

	public void setGroupList(List<String> groupList) {
		this.groupList = groupList;
	}

	public Importance getTestImportance() {
		return testImportance;
	}

	public void setTestImportance(Importance testImportance) {
		this.testImportance = testImportance;
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

	public String getTestPlanBDD() {
		return testPlanBDD;
	}

	public void setTestPlanBDD(String testPlanBDD) {
		this.testPlanBDD = testPlanBDD;
	}

	public String getStepDefinition() {
		return stepDefinition;
	}

	public void setStepDefinition(String stepDefinition) {
		this.stepDefinition = stepDefinition;
	}

}
