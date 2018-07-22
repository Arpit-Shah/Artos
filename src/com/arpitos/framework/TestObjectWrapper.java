// Copyright <2018> <Arpitos>

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
package com.arpitos.framework;

/**
 * This class wraps test object with other necessary information which is
 * helpful during test execution
 * 
 * @author ArpitS
 *
 */
public class TestObjectWrapper {

	Class<?> cls = null;
	boolean skipTest = false;
	int testsequence = 0;
	String testCaseLabel = "";

	String testPlanDescription = "Warning : TestPlan Attribute is not set";
	String testPlanPreparedBy = "???";
	String testPlanPreparationDate = "???";
	String testreviewedBy = "???";
	String testReviewDate = "???";

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
	 */
	public TestObjectWrapper(Class<?> cls, boolean skipTest, int testsequence, String testCaseLabel) {
		super();

		this.cls = cls;

		this.skipTest = skipTest;
		this.testsequence = testsequence;
		this.testCaseLabel = testCaseLabel;
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
