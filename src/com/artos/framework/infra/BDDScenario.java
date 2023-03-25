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

/**
 * BDD Scenario POJO
 * 
 * @author ArpitShah
 *
 */
public class BDDScenario {
	
	/**
	 * Default constructor
	 */
	public BDDScenario() {
		// TODO Auto-generated constructor stub
	}

	// TestTracking variables
	long testStartTime;
	long testFinishTime;
	/*
	 * This will be used to store all parameterised tests status. If test case is
	 * not parameterised test case then only one status will be stored
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

	/**
	 * Get Scenario description
	 * 
	 * @return scenario description
	 */
	public String getScenarioDescription() {
		return scenarioDescription;
	}

	/**
	 * Set Scenario description
	 * 
	 * @param scenarioDescription scenario description
	 */
	public void setScenarioDescription(String scenarioDescription) {
		this.scenarioDescription = scenarioDescription;
	}

	/**
	 * Get list of Group
	 * 
	 * @return List of group
	 */
	public List<String> getGroupList() {
		return groupList;
	}

	/**
	 * Set List of Group
	 * 
	 * @param groupList List of group
	 */
	public void setGroupList(List<String> groupList) {
		this.groupList = groupList;
	}

	/**
	 * get global data table
	 * 
	 * @return {@link LinkedHashMap} of GlobalDataTable
	 */
	public LinkedHashMap<String, List<String>> getGlobalDataTable() {
		return globalDataTable;
	}

	/**
	 * Set global data table
	 * 
	 * @param globalDataTable {@link LinkedHashMap} of GlobalDataTable
	 */
	public void setGlobalDataTable(LinkedHashMap<String, List<String>> globalDataTable) {
		this.globalDataTable = globalDataTable;
	}

	/**
	 * get Step list
	 * 
	 * @return List of {@link BDDStep}
	 */
	public List<BDDStep> getSteplist() {
		return steplist;
	}

	/**
	 * set step list
	 * 
	 * @param steplist List of {@link BDDStep}
	 */
	public void setSteplist(List<BDDStep> steplist) {
		this.steplist = steplist;
	}

	/**
	 * get test start time
	 * 
	 * @return test start time
	 */
	public long getTestStartTime() {
		return testStartTime;
	}

	/**
	 * set test start time
	 * 
	 * @param testStartTime test start time
	 */
	public void setTestStartTime(long testStartTime) {
		this.testStartTime = testStartTime;
	}

	/**
	 * get test finish time
	 * 
	 * @return test finish time
	 */
	public long getTestFinishTime() {
		return testFinishTime;
	}

	/**
	 * set test finish time
	 * 
	 * @param testFinishTime test finish time
	 */
	public void setTestFinishTime(long testFinishTime) {
		this.testFinishTime = testFinishTime;
	}

	/**
	 * get list of {@link TestStatus}
	 * 
	 * @return List of {@link TestStatus}
	 */
	public List<TestStatus> getTestOutcomeList() {
		return testOutcomeList;
	}

	/**
	 * set list of {@link TestStatus}
	 * 
	 * @param testOutcomeList List of {@link TestStatus}
	 */
	public void setTestOutcomeList(List<TestStatus> testOutcomeList) {
		this.testOutcomeList = testOutcomeList;
	}

	/**
	 * check if background is enabled
	 * 
	 * @return boolean
	 */
	public boolean isBackground() {
		return background;
	}

	/**
	 * set if background is enabled
	 * 
	 * @param background true = set background, false = do not set background
	 */
	public void setBackground(boolean background) {
		this.background = background;
	}

	/**
	 * get {@link Importance}
	 * 
	 * @return {@link Importance}
	 */
	public Importance getTestImportance() {
		return testImportance;
	}

	/**
	 * Set {@link Importance}
	 * 
	 * @param testImportance {@link Importance}
	 */
	public void setTestImportance(Importance testImportance) {
		this.testImportance = testImportance;
	}

}
