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

public class BDDStep {

	private String stepAction;
	private String stepDescription;
	private LinkedHashMap<String, List<String>> localDataTable = new LinkedHashMap<>();
	private TestUnitObjectWrapper unit;
	private List<String> inlineParameterList = new ArrayList<>();
	private boolean hasGlobalReference = false;

	/**
	 * 
	 * @param stepAction Gherikin action keyword (GIVEN, AND, THEN, WHEN, BUT)
	 * @param stepDescription stepDescription description of a test step
	 * @param localDataTable local datatable object
	 */
	public BDDStep(String stepAction, String stepDescription, LinkedHashMap<String, List<String>> localDataTable) {
		super();
		this.stepAction = stepAction;
		this.stepDescription = stepDescription;
		this.localDataTable = localDataTable;
	}

	public String getStepDescription() {
		return stepDescription;
	}

	public void setStepDescription(String stepDescription) {
		this.stepDescription = stepDescription;
	}

	public LinkedHashMap<String, List<String>> getLocalDataTable() {
		return localDataTable;
	}

	public void setLocalDataTable(LinkedHashMap<String, List<String>> localDataTable) {
		this.localDataTable = localDataTable;
	}

	public TestUnitObjectWrapper getUnit() {
		return unit;
	}

	public void setUnit(TestUnitObjectWrapper unit) {
		this.unit = unit;
	}

	public List<String> getInlineParameterList() {
		return inlineParameterList;
	}

	public void setInlineParameterList(List<String> inlineParameterList) {
		this.inlineParameterList = inlineParameterList;
	}

	public boolean hasGlobalReference() {
		return hasGlobalReference;
	}

	public void setHasGlobalReference(boolean hasGlobalReference) {
		this.hasGlobalReference = hasGlobalReference;
	}

	public String getStepAction() {
		return stepAction;
	}

	public void setStepAction(String stepAction) {
		this.stepAction = stepAction;
	}

}
