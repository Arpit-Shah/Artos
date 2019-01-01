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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.artos.framework.FWStaticStore;
import com.artos.framework.ScanTestSuite;
import com.artos.framework.TestDataProvider;
import com.artos.framework.TestObjectWrapper;
import com.artos.framework.xml.TestSuite;
import com.artos.interfaces.TestExecutable;

public class TransformToTestObjectWrapper {

	private List<TestObjectWrapper> listOfTransformedTestCases;

	/**
	 * Transforms given test list of{@code TestEecutable} type into {@code TestObjectWrapper} type list. This method will only consider test This
	 * method can not transform test cases outside current package, so those test cases will be omitted from the list
	 * 
	 * @param context {@code TestContext}
	 * @param listOfTestCases list of test cases required to be transformed
	 * @param groupList user specified groupList
	 * @throws Exception in case any requirement of test cases are not met
	 */
	public TransformToTestObjectWrapper(TestContext context, List<TestExecutable> listOfTestCases, List<String> groupList) throws Exception {
		listOfTransformedTestCases = new ArrayList<>();

		if (null == groupList || groupList.isEmpty()) {
			new Exception("Group must be specified");
		}

		// If main() method executes from root then package name will be none
		String packageName = "";
		if (null != context.getPrePostRunnableObj().getPackage()) {
			packageName = context.getPrePostRunnableObj().getPackage().getName();
		}
		ScanTestSuite reflection = new ScanTestSuite(packageName);
		guardDataProviderPresence(reflection.getDataProviderMap(), reflection.getTestObjWrapperList_WithoutSkipped());

		// Store test dataProviders in context
		context.setDataProviderMap(reflection.getDataProviderMap());

		/*
		 * @formatter:off
		 * 1. If XML testScript is provided then use it
		 * 2. If user has provided testList using main() method then use it
		 * 3. If all of the above is not provided then use reflection to find test cases
		 * @formatter:on
		 */
		if (null != context.getTestSuite()) {
			testListProvidedViaXMLTestScript(context, context.getTestSuite(), reflection);
		} else if (null != listOfTestCases) {
			testListProvidedByUserUsingMainMethod(context, listOfTestCases, groupList, reflection);
		} else {
			testListIsNotProvided(groupList, reflection);
		}
	}

	/**
	 * Get all test case objects using reflection. Any test cases with \"skip = true\" will be skipped. Test cases will be sorted per package using
	 * sequence number provided in {@code TestCase} annotation by user.
	 */
	private void testListIsNotProvided(List<String> groupList, ScanTestSuite reflection) {
		List<TestObjectWrapper> listOfTestObj = reflection.getTestObjWrapperList(true, true, true);
		for (TestObjectWrapper t : listOfTestObj) {
			if (belongsToApprovedGroup(groupList, t.getGroupList())) {
				listOfTransformedTestCases.add(t);
			}
		}
	}

	/**
	 * Get all test case objects provided via main() method. Any test cases with \"skip = true\" will be skipped. Test cases will be ordered as
	 * provided in the list
	 */
	private void testListProvidedByUserUsingMainMethod(TestContext context, List<TestExecutable> listOfTestCases, List<String> groupList,
			ScanTestSuite reflection) {
		
		// Capitalise all group name to avoid case-sensitivity
		groupList.replaceAll(String::toUpperCase);

		// empty test list = assume all test cases
		if (listOfTestCases.isEmpty()) {
			testListIsNotProvided(groupList, reflection);
		} else {
			Map<String, TestObjectWrapper> testCaseMap = reflection.getTestObjWrapperMap(true);
			context.setGlobalObject(FWStaticStore.GLOBAL_ANNOTATED_TEST_MAP, testCaseMap);

			for (TestExecutable t : listOfTestCases) {
				TestObjectWrapper testObjWrapper = testCaseMap.get(t.getClass().getName());

				if (null == testObjWrapper) {
					// This can happen if test is marked skipped or actually not present
					System.err.println(t.getClass().getName() + " not present in given test suite");
				} else {
					if (belongsToApprovedGroup(groupList, testObjWrapper.getGroupList())) {
						listOfTransformedTestCases.add(testObjWrapper);
					}
				}
			}
		}
	}

	/**
	 * Get all test case objects listed in XML Test Script. Any test cases with \"skip = true\" will be skipped.Test cases will be ordered as provided
	 * in the xml test script.
	 */
	private void testListProvidedViaXMLTestScript(TestContext context, Object testSuiteObject, ScanTestSuite reflection) {
		TestSuite suite = (TestSuite) testSuiteObject;

		// Capitalise all group name to avoid case-sensitivity
		List<String> groupList = suite.getGroupList();
		groupList.replaceAll(String::toUpperCase);

		// populate all global parameters to context
		Map<String, String> parameterMap = suite.getTestSuiteParameters();
		if (null != parameterMap && !parameterMap.isEmpty()) {
			for (Entry<String, String> entry : parameterMap.entrySet()) {
				context.setGlobalObject(entry.getKey(), entry.getValue());
			}
		}

		// empty test list = assume all test cases
		if (suite.getTestFQCNList().isEmpty()) {
			testListIsNotProvided(groupList, reflection);
		} else {
			Map<String, TestObjectWrapper> testCaseMap = reflection.getTestObjWrapperMap(true);
			context.setGlobalObject(FWStaticStore.GLOBAL_ANNOTATED_TEST_MAP, testCaseMap);

			// Create Test object list from test script
			for (String t : suite.getTestFQCNList()) {
				TestObjectWrapper testObjWrapper = testCaseMap.get(t);

				if (null == testObjWrapper) {
					// This can happen if test is marked skipped or actually not present
					System.err.println("WARNING (not found): " + t);
				} else {
					if (belongsToApprovedGroup(groupList, testObjWrapper.getGroupList())) {
						listOfTransformedTestCases.add(testObjWrapper);
					}
				}
			}
		}
	}

	/**
	 * Validates if all required data providers are present. If any data providers are not present then user must be notified and test execution will
	 * not start.
	 * 
	 * @param dataProviderMap hashMap of all data providers
	 * @param testList list of all test cases
	 * @throws Exception if required data provider is not found
	 */
	private void guardDataProviderPresence(Map<String, TestDataProvider> dataProviderMap, List<TestObjectWrapper> testList) throws Exception {
		for (TestObjectWrapper t : testList) {
			if (!"".equals(t.getDataProviderName()) && null == dataProviderMap.get(t.getDataProviderName().toUpperCase())) {
				throw new Exception("DataProvider [" + t.getDataProviderName() + "] required for test case [" + t.getTestClassObject().getSimpleName()
						+ "] is either private or not present");
			}
		}
	}

	/**
	 * Validate if test case belongs to any user defined group(s)
	 * 
	 * @param refGroupList list of user defined group (via test script or via main class)
	 * @param testGroupList list of group test case belong to
	 * @return true if test case belongs to at least one of the user defined groups, false if test case does not belong to any user defined groups
	 */
	private boolean belongsToApprovedGroup(List<String> refGroupList, List<String> testGroupList) {
		
		return refGroupList.stream().anyMatch(num -> testGroupList.contains(num));
//		for (String group : refGroupList) {
//			if (testGroupList.contains(group)) {
//				return true;
//			}
//		}
//		return false;
	}

	public List<TestObjectWrapper> getListOfTransformedTestCases() {
		return listOfTransformedTestCases;
	}
}
