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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;

import com.artos.annotation.ExpectedException;
import com.artos.annotation.Group;
import com.artos.annotation.KnownToFail;
import com.artos.annotation.StepDefinition;
import com.artos.annotation.TestImportance;
import com.artos.annotation.TestPlan;
import com.artos.annotation.Unit;

import javassist.Modifier;

/**
 * This class provides all utilities for reflection
 *
 */
public class BDDScanTestSuite {

	TestContext context;
	Reflections reflection;
	Map<String, TestUnitObjectWrapper> stepDefinitionsMap = new HashMap<>();

	/**
	 * Scans all packages within provided package
	 * 
	 * @param context     Test context
	 * @param packageName Base package name
	 * 
	 */
	public BDDScanTestSuite(TestContext context, String packageName) {
		this.context = context;

		System.out.println("Scanning for test cases. Please wait...");
		scan(packageName);
	}

	/**
	 * Scans for Test methods within provided packageName
	 * 
	 * @param packageName Base package name
	 */
	private void scan(String packageName) {

		// Find all annotation
//		reflection = new Reflections(packageName, new MethodAnnotationsScanner(), new TypeAnnotationsScanner(),	new SubTypesScanner(false));
		reflection = new Reflections(new ConfigurationBuilder().forPackage(packageName).setScanners(Scanners.MethodsAnnotated, Scanners.TypesAnnotated, Scanners.SubTypes.filterResultsBy(s -> true)));

		// GetAllStepDefMethods => Filter Public methods => Get UpperCase StepDef =>
		// Store it
		reflection.getMethodsAnnotatedWith(StepDefinition.class).stream()
				.filter(method -> Modifier.isPublic(method.getModifiers())).forEach(method -> {

					Unit unit = method.getAnnotation(Unit.class);
					TestPlan testplan = method.getAnnotation(TestPlan.class);
					KnownToFail ktf = method.getAnnotation(KnownToFail.class);
					ExpectedException expectedException = method.getAnnotation(ExpectedException.class);
					Group group = method.getAnnotation(Group.class);
					TestImportance testImportance = method.getAnnotation(TestImportance.class);
					StepDefinition stepDef = method.getAnnotation(StepDefinition.class);

					// @Unit annotation is an optional for BDD
					TestUnitObjectWrapper testUnitObj;
					if (null == unit) {
						testUnitObj = new TestUnitObjectWrapper(method, false, 0, null, 0, "", false);
					} else {
						testUnitObj = new TestUnitObjectWrapper(method, unit.skip(), unit.sequence(),
								unit.dataprovider(), unit.testtimeout(), unit.bugref(),
								unit.dropRemainingUnitsUponFailure());
					}

					// Test Plan is an optional attribute so it can be null
					if (null != testplan) {
						testUnitObj.setTestPlanDescription(testplan.description());
						testUnitObj.setTestPlanPreparedBy(testplan.preparedBy());
						testUnitObj.setTestPlanPreparationDate(testplan.preparationDate());
						testUnitObj.setTestreviewedBy(testplan.reviewedBy());
						testUnitObj.setTestReviewDate(testplan.reviewDate());
						testUnitObj.setTestPlanBDD(testplan.bdd());
					}

					/*
						 * Store group list for each test cases.
						 * 
						 * @formatter:off
						 * Label must follow following rules
						 * <PRE>
						 * - Name is case in-sensitive (but it will stored in upper case)
						 * - Name can not have leading or trailing spaces, it will be removed
						 * - Name can not have \r or \n or \t char, it will be removed
						 * - Name can not have \ char, it will be removed
						 * </PRE>
						 * @formatter:on
						 */
					{
						if (null != group) {
							List<String> groupList = Arrays.asList(group.group());
							testUnitObj.setGroupList(groupList.stream()
									.map(s -> s.toUpperCase().trim().replaceAll("\n", "").replaceAll("\r", "")
											.replaceAll("\t", "").replaceAll("\\\\", "").replaceAll("/", ""))
									.collect(Collectors.toList()));
						} else {
							// Create empty arrayList
							testUnitObj.setGroupList(new ArrayList<String>());
						}

						// each group must have * by default (which represents all
						if (!testUnitObj.getGroupList().contains("*")) {
							testUnitObj.getGroupList().add("*");
						}
					}

					// KTF is optional annotation so it can be null
					if (null != ktf) {
						testUnitObj.setKTF(ktf.ktf());
					}

					// expectedException is an optional annotation
					if (null != expectedException) {
						List<Class<? extends Throwable>> expectedExceptionsList = Arrays
								.asList(expectedException.expectedExceptions());
						testUnitObj.setExpectedExceptionList(expectedExceptionsList);
						testUnitObj.setExceptionContains(expectedException.contains());
						testUnitObj.setEnforce(expectedException.enforce());
					}

					// TestImportance is an optional annotation
					if (null != testImportance) {
						testUnitObj.setTestImportance(testImportance.value());
					}

					// Test Def is an optional attribute so it can be null
					if (null != stepDef) {

						String stepDefKey = stepDef.value().trim();

						// If cucumber was used to generate the argument then do the following
						if (stepDefKey.startsWith("^") && stepDefKey.endsWith("$")) {
							// Replace "([^"]*)"
							stepDefKey = stepDefKey.replaceAll("\\\"\\(\\[\\^\\\"\\]\\*\\)\\\"", "\"\"").trim();
							// Replace $
							stepDefKey = stepDefKey.replaceAll("\\$", "").trim();
							// Replace ^
							stepDefKey = stepDefKey.replaceAll("\\^", "").trim();

						} else { // otherwise do this
							// Replace anything between quotes to empty string "xyz" => ""
							stepDefKey = stepDefKey.replaceAll("\\\".*?\\\"", "\"\"").trim();
						}

						testUnitObj.setStepDefinition(stepDefKey);

						if (!stepDefinitionsMap.containsKey(testUnitObj.getStepDefinition())) {
							// Remove everything between "" and store it as a key
							stepDefinitionsMap.put(testUnitObj.getStepDefinition(), testUnitObj);
						} else {
							System.err.println("[Warning] Duplicate step : " + testUnitObj.getStepDefinition());
						}
					}

				});
	}

	/**
	 * Get Step Definitions Map
	 * 
	 * @return Key Value pair of {@link TestUnitObjectWrapper}
	 */
	public Map<String, TestUnitObjectWrapper> getStepDefinitionsMap() {
		return stepDefinitionsMap;
	}

	/**
	 * Set Step Definitions Map
	 * 
	 * @param stepDefinitionsMap Key Value pair of {@link TestUnitObjectWrapper}
	 */
	protected void setStepDefinitionsMap(Map<String, TestUnitObjectWrapper> stepDefinitionsMap) {
		this.stepDefinitionsMap = stepDefinitionsMap;
	}
}
