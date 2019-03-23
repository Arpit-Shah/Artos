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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.artos.framework.Enums.Importance;
import com.artos.framework.Enums.TestStatus;

public class BDDScenario {

	// TestTracking variables
	long testStartTime;
	long testFinishTime;
	/*
	 * This will be used to store all parameterised tests status. If test case is not parameterised test case then only one status will be stored
	 */
	List<TestStatus> testOutcomeList = new ArrayList<>();

	// testImportance
	Importance testImportance = Importance.UNDEFINED;

	String scenarioDescription;
	List<String> groupList = new ArrayList<>();
	List<BDDStep> steplist = new ArrayList<>();
	LinkedHashMap<String, List<String>> globalDataTable = new LinkedHashMap<>();

	// Background
	boolean background = false;

	public String getScenarioDescription() {
		return scenarioDescription;
	}

	public void setScenarioDescription(String scenarioDescription) {
		this.scenarioDescription = scenarioDescription;
	}

	public List<String> getGroupList() {
		return groupList;
	}

	public void setGroupList(List<String> groupList) {
		this.groupList = groupList;
	}

	public LinkedHashMap<String, List<String>> getGlobalDataTable() {
		return globalDataTable;
	}

	public void setGlobalDataTable(LinkedHashMap<String, List<String>> globalDataTable) {
		this.globalDataTable = globalDataTable;
	}

	public List<BDDStep> getSteplist() {
		return steplist;
	}

	public void setSteplist(List<BDDStep> steplist) {
		this.steplist = steplist;
	}

	public long getTestStartTime() {
		return testStartTime;
	}

	public void setTestStartTime(long testStartTime) {
		this.testStartTime = testStartTime;
	}

	public long getTestFinishTime() {
		return testFinishTime;
	}

	public void setTestFinishTime(long testFinishTime) {
		this.testFinishTime = testFinishTime;
	}

	public List<TestStatus> getTestOutcomeList() {
		return testOutcomeList;
	}

	public void setTestOutcomeList(List<TestStatus> testOutcomeList) {
		this.testOutcomeList = testOutcomeList;
	}

	public boolean isBackground() {
		return background;
	}

	public void setBackground(boolean background) {
		this.background = background;
	}

	public Importance getTestImportance() {
		return testImportance;
	}

	public void setTestImportance(Importance testImportance) {
		this.testImportance = testImportance;
	}

}
