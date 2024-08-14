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

/**
 * BDD Step POJO
 * 
 * @author ArpitShah
 *
 */
public class BDDStep {

	private String stepAction;
	private String stepDescription;
	private LinkedHashMap<String, List<String>> localDataTable = new LinkedHashMap<>();
	private TestUnitObjectWrapper unit;
	private List<String> inlineParameterList = new ArrayList<>();
	private boolean hasGlobalReference = false;

	/**
	 * Constructor
	 * 
	 * @param stepAction      Gherikin action keyword (GIVEN, AND, THEN, WHEN, BUT)
	 * @param stepDescription stepDescription description of a test step
	 * @param localDataTable  local datatable object
	 */
	public BDDStep(String stepAction, String stepDescription, LinkedHashMap<String, List<String>> localDataTable) {
		super();
		this.stepAction = stepAction;
		this.stepDescription = stepDescription;
		this.localDataTable = localDataTable;
	}

	/**
	 * Returns step description
	 * 
	 * @return step description step description
	 */
	public String getStepDescription() {
		return stepDescription;
	}

	/**
	 * Set step description
	 * 
	 * @param stepDescription step description
	 */
	public void setStepDescription(String stepDescription) {
		this.stepDescription = stepDescription;
	}

	/**
	 * Return Local Data Table
	 * 
	 * @return Hashmap with data tables stored against their key pair value
	 */
	public LinkedHashMap<String, List<String>> getLocalDataTable() {
		return localDataTable;
	}

	/**
	 * Set LocalData table for the step
	 * 
	 * @param localDataTable Hashmap with local data table and associated key
	 */
	public void setLocalDataTable(LinkedHashMap<String, List<String>> localDataTable) {
		this.localDataTable = localDataTable;
	}

	/**
	 * Return Unit
	 * 
	 * @return {@link TestUnitObjectWrapper}
	 */
	public TestUnitObjectWrapper getUnit() {
		return unit;
	}

	/**
	 * Set {@link TestUnitObjectWrapper}
	 * 
	 * @param unit {@link TestUnitObjectWrapper}
	 */
	public void setUnit(TestUnitObjectWrapper unit) {
		this.unit = unit;
	}

	/**
	 * Return inline parameter list
	 * 
	 * @return inline parameter list
	 */
	public List<String> getInlineParameterList() {
		return inlineParameterList;
	}

	/**
	 * Set inline parameter list
	 * 
	 * @param inlineParameterList List of inline parameter
	 */
	public void setInlineParameterList(List<String> inlineParameterList) {
		this.inlineParameterList = inlineParameterList;
	}

	/**
	 * Return boolean confirming global reference
	 * 
	 * @return boolean true = has global reference, false = does not have global
	 *         reference
	 */
	public boolean hasGlobalReference() {
		return hasGlobalReference;
	}

	/**
	 * Set boolean confirming if global reference is present
	 * 
	 * @param hasGlobalReference boolean parameter, true = has global reference,
	 *                           false = does not have global
	 */
	public void setHasGlobalReference(boolean hasGlobalReference) {
		this.hasGlobalReference = hasGlobalReference;
	}

	/**
	 * Return Step action
	 * 
	 * @return step action
	 */
	public String getStepAction() {
		return stepAction;
	}

	/**
	 * Set Step action
	 * 
	 * @param stepAction step action
	 */
	public void setStepAction(String stepAction) {
		this.stepAction = stepAction;
	}

}
