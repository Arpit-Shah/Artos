/*******************************************************************************
 * Copyright (C) 2018 Arpit Shah
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
