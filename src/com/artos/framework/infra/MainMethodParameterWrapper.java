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
