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

import java.util.List;

import com.artos.interfaces.TestExecutable;

public class MainMethodParameterWrapper {

	int loopCount = 1;
	List<TestExecutable> testList = null;
	List<String> testGroupList = null;
	List<String> testUnitGroupList = null;
	String frameworkConfigProfile = null;
	String testSuiteName = "SuiteName";

	public List<TestExecutable> getTestList() {
		return testList;
	}

	public void setTestList(List<TestExecutable> testList) {
		this.testList = testList;
	}

	public List<String> getTestGroupList() {
		return testGroupList;
	}

	public void setTestGroupList(List<String> testGroupList) {
		this.testGroupList = testGroupList;
	}

	public List<String> getTestUnitGroupList() {
		return testUnitGroupList;
	}

	public void setTestUnitGroupList(List<String> testUnitGroupList) {
		this.testUnitGroupList = testUnitGroupList;
	}

	public String getFrameworkConfigProfile() {
		return frameworkConfigProfile;
	}

	public void setFrameworkConfigProfile(String frameworkConfigProfile) {
		this.frameworkConfigProfile = frameworkConfigProfile;
	}

	public String getTestSuiteName() {
		return testSuiteName;
	}

	public void setTestSuiteName(String testSuiteName) {
		this.testSuiteName = testSuiteName;
	}

	public int getLoopCount() {
		return loopCount;
	}

	public void setLoopCount(int loopCount) {
		this.loopCount = loopCount;
	}

}
