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
package com.artos.framework;

import com.artos.annotation.Group;
import com.artos.annotation.KnownToFail;
import com.artos.annotation.TestCase;
import com.artos.annotation.TestPlan;

public class TestPlanWrapper {

	// test case
	private boolean skip;
	private int sequenece;
	private String dataProviderName;

	// test plan
	private String TestCaseName = "";
	private String TestDescription = "";
	private String TestPreparedBy = "";
	private String TestPreparationDate = "";
	private String TestReviewedBy = "";
	private String TestReviewedDate = "";
	private String TestBDD = "";

	// test group
	private String groupList = "";

	// ktf
	private boolean ktf;
	private String bugTrackingNumber;

	/**
	 * This method allows setting of parameters set using {@link TestPlan} annotation.
	 * 
	 * @param testCaseName test case name
	 * @param testDescription test short description
	 * @param testPreparedBy name of the person prepared the test case
	 * @param testPreparationDate test case preparation date. recommended in YYYY/MM/DD format
	 * @param testReviewedBy test code reviewer name
	 * @param testReviewedDate test case review date. recommended in YYYY/MM/DD format
	 * @param testBDD test plan BDD. recommended using keyword like (Given, AND, WHEN, THEN)
	 */
	public void setTestPlan(String testCaseName, String testDescription, String testPreparedBy, String testPreparationDate, String testReviewedBy,
			String testReviewedDate, String testBDD) {
		TestCaseName = testCaseName;
		TestDescription = testDescription;
		TestPreparedBy = testPreparedBy;
		TestPreparationDate = testPreparationDate;
		TestReviewedBy = testReviewedBy;
		TestReviewedDate = testReviewedDate;
		TestBDD = testBDD;
	}

	/**
	 * This method allows setting of parameters set using {@link TestCase} annotation.
	 * 
	 * @param sequence test sequence number
	 * @param skip true if test case is to be skipped otherwise false
	 * @param dataProviderName name of the data provider method
	 */
	public void setTestPlanSkip(int sequence, boolean skip, String dataProviderName) {
		this.sequenece = sequence;
		this.skip = skip;
		this.dataProviderName = dataProviderName;
	}

	/**
	 * This method allows setting of parameters set using {@link Group} annotation.
	 * 
	 * @param groupList test case group list
	 */
	public void setTestPlanGroup(String groupList) {
		this.groupList = groupList;
	}

	/**
	 * This method allows setting of parameters set using {@link KnownToFail} annotation.
	 * 
	 * @param ktf true if test case is not to fail otherwise false
	 * @param bugTrackingNumber bug tracking reference for record keeping
	 */
	public void setTestPlanKTF(boolean ktf, String bugTrackingNumber) {
		this.ktf = ktf;
		this.bugTrackingNumber = bugTrackingNumber;
	}

	/**
	 * Gets test case name
	 * 
	 * @return test case name
	 */
	public String getTestCaseName() {
		return TestCaseName;
	}

	/**
	 * Gets test description
	 * 
	 * @return test case description
	 */
	public String getTestDescription() {
		return TestDescription;
	}

	/**
	 * Gets test case creators name
	 * 
	 * @return name of the person preparing the test case
	 */
	public String getTestPreparedBy() {
		return TestPreparedBy;
	}

	/**
	 * Gets test case preparation date
	 * 
	 * @return test case preparation date
	 */
	public String getTestPreparationDate() {
		return TestPreparationDate;
	}

	/**
	 * Gets test case code reviewers name
	 * 
	 * @return test case reviewer name
	 */
	public String getTestReviewedBy() {
		return TestReviewedBy;
	}

	/**
	 * Gets test case review date
	 * 
	 * @return test case review date
	 */
	public String getTestReviewedDate() {
		return TestReviewedDate;
	}

	/**
	 * Gets test plan written in BDD format
	 * 
	 * @return test plan
	 */
	public String getTestBDD() {
		return TestBDD;
	}

	/**
	 * Gets test case skip preference
	 * 
	 * @return true if test case is to be skipped otherwise false
	 */
	public boolean isSkip() {
		return skip;
	}

	/**
	 * Gets test case sequence number
	 * 
	 * @return test sequence number
	 */
	public int getSequenece() {
		return sequenece;
	}

	/**
	 * Gets data provider method name
	 * 
	 * @return data provider method name
	 */
	public String getDataProviderName() {
		return dataProviderName;
	}

	/**
	 * Gets String formated group list
	 * 
	 * @return test case group list in string format
	 */
	public String getGroupList() {
		return groupList;
	}

	/**
	 * Gets test case preference for known to fail
	 * 
	 * @return true if test is known to fail otherwise false
	 */
	public boolean isKtf() {
		return ktf;
	}

	/**
	 * Gets test case bug tracking number(s)
	 * 
	 * @return bug tracking references
	 */
	public String getBugTrackingNumber() {
		return bugTrackingNumber;
	}

}
