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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import com.artos.annotation.DataProvider;
import com.artos.annotation.ExpectedException;
import com.artos.annotation.Group;
import com.artos.annotation.KnownToFail;
import com.artos.annotation.TestCase;
import com.artos.annotation.TestPlan;
import com.artos.framework.infra.TestContext;
import com.artos.interfaces.TestExecutable;

import javassist.Modifier;

/**
 * This class provides all utilities for reflection
 * 
 * 
 *
 */
public class ScanTestSuite {
	Reflections reflection;
	List<TestObjectWrapper> testObjWrapperList_All = new ArrayList<>();
	List<TestObjectWrapper> testObjWrapperList_WithoutSkipped = new ArrayList<>();
	List<String> FQCN = new ArrayList<>();
	Map<String, TestDataProvider> dataProviderMap = new HashMap<>();

	/**
	 * Default constructor. Scans all packages within provided package
	 * 
	 * @param packageName Base package name
	 */
	public ScanTestSuite(String packageName) {
		System.out.println("Scanning for test cases. Please wait...");
		scan(packageName);
	}

	/**
	 * Scans for Test cases within provided packageName
	 * 
	 * @param packageName Base package name
	 */
	private void scan(String packageName) {

		reflection = new Reflections(packageName, new MethodAnnotationsScanner(), new TypeAnnotationsScanner(), new SubTypesScanner(false));

		// for (Class<?> cl : reflection.getTypesAnnotatedWith(TestCase.class)) {
		for (Class<?> cl : reflection.getSubTypesOf(Object.class)) {

			// Find all dataProvider methods
			for (Method method : cl.getMethods()) {
				if (method.isAnnotationPresent(DataProvider.class) && Modifier.isPublic(method.getModifiers())) {
					String dataProviderName = method.getAnnotation(DataProvider.class).name();
					boolean isStaticMethod = false;
					if (Modifier.isStatic(method.getModifiers())) {
						isStaticMethod = true;
					}
					dataProviderMap.put(dataProviderName.toUpperCase(), new TestDataProvider(method, dataProviderName, cl, isStaticMethod));
				}
			}

			if (cl.isAnnotationPresent(TestCase.class)) {
				TestCase testcase = cl.getAnnotation(TestCase.class);
				TestPlan testplan = cl.getAnnotation(TestPlan.class);
				Group group = cl.getAnnotation(Group.class);
				KnownToFail ktf = cl.getAnnotation(KnownToFail.class);
				ExpectedException expectedException = cl.getAnnotation(ExpectedException.class);

				// When test case is in the root directory package will be null
				if (null == cl.getPackage() && !FQCN.contains("")) {
					FQCN.add("");
				} else if (null != cl.getPackage() && !FQCN.contains(cl.getPackage().getName())) {
					FQCN.add(cl.getPackage().getName());
				}

				TestObjectWrapper testobj = new TestObjectWrapper(cl, testcase.skip(), testcase.sequence(), testcase.dataprovider());

				// Test Plan is optional attribute so it can be null
				if (null != testplan) {
					testobj.setTestPlanDescription(testplan.decription());
					testobj.setTestPlanPreparedBy(testplan.preparedBy());
					testobj.setTestPlanPreparationDate(testplan.preparationDate());
					testobj.setTestreviewedBy(testplan.reviewedBy());
					testobj.setTestReviewDate(testplan.reviewDate());
					testobj.setTestPlanBDD(testplan.bdd());
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
						testobj.setGroupList(groupList.stream().map(s -> s.toUpperCase().trim().replaceAll("\n", "").replaceAll("\r", "")
								.replaceAll("\t", "").replaceAll("\\\\", "").replaceAll("/", "")).collect(Collectors.toList()));
					} else {
						testobj.setGroupList(new ArrayList<String>());
					}

					// each group must have * by default (which represents all
					if (!testobj.getGroupList().contains("*")) {
						testobj.getGroupList().add("*");
					}
					// System.out.println(testobj.getGroupList());
				}

				/*
			 * Store label list for each test cases.
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
					List<String> labelList = Arrays.asList(testcase.label());
					testobj.setLabelList(labelList.stream().map(s -> s.toUpperCase().trim().replaceAll("\n", "").replaceAll("\r", "")
							.replaceAll("\t", "").replaceAll("\\\\", "").replaceAll("/", "")).collect(Collectors.toList()));
				}

				// KTF is optional annotation so it can be null
				if (null != ktf) {
					testobj.setKTF(ktf.ktf());
					testobj.setBugTrackingNumber(ktf.bugref());
				}

				// expectedException is optional annotation
				if (null != expectedException) {
					List<Class<? extends Throwable>> expectedExceptionsList = Arrays.asList(expectedException.expectedExceptions());
					testobj.setExpectedExceptionList(expectedExceptionsList);
					testobj.setExceptionContains(expectedException.contains());
					testobj.setEnforceException(expectedException.enforce());
				}

				testObjWrapperList_All.add(testobj);
				if (!testcase.skip()) {
					testObjWrapperList_WithoutSkipped.add(testobj);
				}
			}
		}

		// For debugging purpose
		// @formatter:off
//		{
//			for (Method method : reflection.getMethodsAnnotatedWith(BeforeTest.class)) {
//				System.out.println("@BeforeTest = " + method.getName() + " : " + method.getDeclaringClass().getName());
//			}
//			for (Method method : reflection.getMethodsAnnotatedWith(BeforeTestsuite.class)) {
//				System.out.println("@BeforeTestsuite = " + method.getName() + " : " + method.getDeclaringClass().getName());
//			}
//			for (Method method : reflection.getMethodsAnnotatedWith(AfterTest.class)) {
//				System.out.println("@AfterTest = " + method.getName() + " : " + method.getDeclaringClass().getName());
//			}
//			for (Method method : reflection.getMethodsAnnotatedWith(AfterTestsuite.class)) {
//				System.out.println("@AfterTestsuite = " + method.getName() + " : " + method.getDeclaringClass().getName());
//			}
//			for(String p: FQCN){
//				System.err.println(p);
//			}
//		}
		// @formatter:on
	}

	/**
	 * logic to bubble sort the elements
	 * 
	 * @param testObjWrapperList List of all test objects which requires sorting
	 * @return
	 */
	private List<TestObjectWrapper> bubble_srt(List<TestObjectWrapper> testObjWrapperList) {

		TestObjectWrapper[] array = testObjWrapperList.parallelStream().toArray(TestObjectWrapper[]::new);
		int n = array.length;
		int k;
		for (int m = n; m >= 0; m--) {
			for (int i = 0; i < n - 1; i++) {
				k = i + 1;
				if (array[i].getTestsequence() > array[k].getTestsequence()) {
					swapNumbers(i, k, array);
				}
			}
		}
		return Arrays.asList(array);
	}

	/**
	 * Swap object in the array
	 * 
	 * @param i To
	 * @param j From
	 * @param array Array of all scanned test objects
	 */
	private void swapNumbers(int i, int j, TestObjectWrapper[] array) {
		TestObjectWrapper temp;
		temp = array[i];
		array[i] = array[j];
		array[j] = temp;
	}

	/**
	 * Generates test plan using annotation provided in the test case classes
	 * 
	 * @param context Test Context
	 * @return {@code TestPlanWrapper} list
	 */
	public List<TestPlanWrapper> getTestPlan(TestContext context) {
		List<TestPlanWrapper> testPlanList = new ArrayList<>();
		for (TestObjectWrapper testObject : testObjWrapperList_All) {
			testPlanList.add(new TestPlanWrapper(testObject.getTestClassObject().getName(), testObject.getTestPlanDescription(),
					testObject.getTestPlanPreparedBy(), testObject.getTestPlanPreparationDate(), testObject.getTestreviewedBy(),
					testObject.getTestReviewDate(), testObject.getTestPlanBDD()));
		}
		return testPlanList;
	}

	/**
	 * Returns all scanned test cases wrapped with TestObjWrapper components. if user has chosen to remove "SKIPPED" test cases then any test cases
	 * marked with "skip=true" will be omitted from the list. If user has chosen to sort by sequence number then test cases will be sorted within
	 * their test package by sequence number.
	 * 
	 * @param sortBySeqNum Enables sorting of the test cases
	 * @param removeSkippedTests Enables removal of test cases which are marked 'skip=true'
	 * @param sortWithinPackage Enables sorting test cases within package scope
	 * @return List of {@code TestObjectWrapper}
	 */
	public List<TestObjectWrapper> getTestObjWrapperList(boolean sortBySeqNum, boolean removeSkippedTests, boolean sortWithinPackage) {

		if (sortBySeqNum && sortWithinPackage) {
			return removeSkippedTests ? sortWithinPackage(testObjWrapperList_WithoutSkipped) : sortWithinPackage(testObjWrapperList_All);
		} else if (sortBySeqNum && !sortWithinPackage) {
			return removeSkippedTests ? bubble_srt(testObjWrapperList_WithoutSkipped) : bubble_srt(testObjWrapperList_All);
		}

		// If sorting is not required
		return removeSkippedTests ? testObjWrapperList_WithoutSkipped : testObjWrapperList_All;
	}

	/**
	 * Sort test cases within package scope so any test cases within same package will remain together and sorted by sequence number assigned to them
	 * 
	 * @param listToBeSorted list of {@code TestObjectWrapper} which requires sorting
	 * @return List of sorted test cases (sorted within package scope)
	 */
	private List<TestObjectWrapper> sortWithinPackage(List<TestObjectWrapper> listToBeSorted) {
		List<TestObjectWrapper> sortedList = new ArrayList<>();

		// Create list per package FQCN and add relevant test cases into those list
		List<List<TestObjectWrapper>> listOfTestsList = getListOfTestsListPerFQCN(listToBeSorted, FQCN);

		// Sort list per packages so test cases are sorted within package scope
		for (List<TestObjectWrapper> testObjList : listOfTestsList) {

			// Once sorted, All All of the sorted test cases to master list
			sortedList.addAll(bubble_srt(testObjList));
		}

		// Return master list
		return sortedList;
	}

	/**
	 * This function breaks down one large test list into small test lists per package name (Using FQCN) so it can be used to rearrange test without
	 * loosing scope of the package. If test case(s) are created in root directory then package FQCN will be empty string.
	 * 
	 * @param testObjlist List of {@code TestObjectWrapper} which requires separation
	 * @param FQCN List of Fully Qualified Path Name (Package Name)
	 * @return
	 */
	private List<List<TestObjectWrapper>> getListOfTestsListPerFQCN(List<TestObjectWrapper> testObjlist, List<String> FQCN) {
		List<List<TestObjectWrapper>> listOfTestsList = new ArrayList<>();

		// Iterate through all FQCN. Empty String means no package
		for (String packageFQCN : FQCN) {

			// Create List per FQCN
			List<TestObjectWrapper> list = new ArrayList<>();

			// Iterate through all test cases
			for (TestObjectWrapper testObj : testObjlist) {

				// If test case in root directory (which means no package FQCN)
				if (packageFQCN.equals("") && null == testObj.getTestClassObject().getPackage()) {
					list.add(testObj);
				} else if (!packageFQCN.equals("") && null != testObj.getTestClassObject().getPackage()
						&& testObj.getTestClassObject().getPackage().getName().equals(packageFQCN)) {
					list.add(testObj);
				}
			}

			// Add testList to Master List
			listOfTestsList.add(list);
		}

		// Return master list
		return listOfTestsList;
	}

	/**
	 * Returns all scanned test cases. If sorted option is selected then test cases within same packages will sorted as per sequence number. If remove
	 * skipped test case is selected then any test case marked "skip=true" will be omitted from the list.
	 * 
	 * @param sortBySeqNum Enables sorting of the test cases (Sorting happens within package scope)
	 * @param removeSkippedTests Enables removal of test cases which are marked 'Skip'
	 * @return List of {@code TestExecutable}
	 * @throws IllegalAccessException if the class or its nullary constructor is not accessible.
	 * @throws InstantiationException if this Class represents an abstract class, an interface, an array class, a primitive type, or void; or if the
	 *             class has no nullary constructor; or if the instantiation fails for some other reason.
	 */
	public List<TestExecutable> getTestList(boolean sortBySeqNum, boolean removeSkippedTests) throws InstantiationException, IllegalAccessException {
		List<TestExecutable> testList = new ArrayList<TestExecutable>();

		List<TestObjectWrapper> testObjWrapperList = getTestObjWrapperList(sortBySeqNum, removeSkippedTests, true);
		for (TestObjectWrapper t : testObjWrapperList) {
			// create new Instance of test object so user can execute the test
			testList.add((TestExecutable) t.getTestClassObject().newInstance());
		}
		return testList;
	}

	/**
	 * Returns scanned test cases HashMap so user can search test case by TestCase FQCN.
	 * 
	 * @param removeSkippedTests removes test cases marked with skipped
	 * @return HashMap of all test objects
	 */
	public Map<String, TestObjectWrapper> getTestObjWrapperMap(boolean removeSkippedTests) {

		// This was made LinkedHashMap so it can preserve insertion order
		Map<String, TestObjectWrapper> testObjWrapperMap = new LinkedHashMap<>();

		// get sorted test case list (Sorted within package scope) so calling method can utilise sorting
		List<TestObjectWrapper> testObjWrapperList = getTestObjWrapperList(true, removeSkippedTests, true);

		// populate LinkedHashMap<> with test case FQCN as key so test case object can be queried using FQCN
		for (TestObjectWrapper t : testObjWrapperList) {
			// create new Instance of test object so user can execute the test
			testObjWrapperMap.put(t.getTestClassObject().getName(), t);
		}
		return testObjWrapperMap;
	}

	public Map<String, TestDataProvider> getDataProviderMap() {
		return dataProviderMap;
	}

}
