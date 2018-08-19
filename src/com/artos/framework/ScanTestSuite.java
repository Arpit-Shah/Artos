// Copyright <2018> <Artos>

// Permission is hereby granted, free of charge, to any person obtaining a copy of this software
// and associated documentation files (the "Software"), to deal in the Software without restriction,
// including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
// and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
// subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
// 
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
// INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
// IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
// WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
// OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
package com.artos.framework;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import com.artos.annotation.Group;
import com.artos.annotation.KnownToFail;
import com.artos.annotation.TestCase;
import com.artos.annotation.TestPlan;
import com.artos.framework.infra.TestContext;
import com.artos.interfaces.TestExecutable;

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

	/**
	 * Default constructor. Scans all packages within provided package
	 * 
	 * @param packageName
	 *            Base package name
	 */
	public ScanTestSuite(String packageName) {
		scan(packageName);
	}

	/**
	 * Scans for Test cases within provided packageName
	 * 
	 * @param packageName
	 *            Base package name
	 */
	private void scan(String packageName) {

		reflection = new Reflections(packageName, new MethodAnnotationsScanner(), new TypeAnnotationsScanner(), new SubTypesScanner(false));

		for (Class<?> cl : reflection.getTypesAnnotatedWith(TestCase.class)) {
			TestCase testcase = cl.getAnnotation(TestCase.class);
			TestPlan testplan = cl.getAnnotation(TestPlan.class);
			Group group = cl.getAnnotation(Group.class);
			KnownToFail ktf = cl.getAnnotation(KnownToFail.class);

			// @formatter:off
			//			System.out.println("@Testcase = " + cl.getName()
			//			+ "\nskip : " + testcase.skip()
			//			+ "\nscenario : " + testcase.sequence()
			//			+ "\ndecription : " + testcase.label()
			//			);
			//			System.out.println("@Testcase = " + cl.getName()
			//			+ "\ndecription : " + testplan.decription()
			//			+ "\npreparedBy : " + testplan.preparedBy()
			//			+ "\npreparationDate : " + testplan.preparationDate()
			//			+ "\nreviewedBy : " + testplan.reviewedBy()
			//			+ "\nreviewDate : " + testplan.reviewDate()
			//			);
			// @formatter:on

			TestObjectWrapper testobj = new TestObjectWrapper(cl, testcase.skip(), testcase.sequence());

			// Test Plan is optional attribute so it can be null
			if (null != testplan) {
				testobj.setTestPlanDescription(testplan.decription());
				testobj.setTestPlanPreparedBy(testplan.preparedBy());
				testobj.setTestPlanPreparationDate(testplan.preparationDate());
				testobj.setTestreviewedBy(testplan.reviewedBy());
				testobj.setTestReviewDate(testplan.reviewDate());
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
				testobj.setLabelList(labelList.stream().map(s -> s.toUpperCase().trim().replaceAll("\n", "").replaceAll("\r", "").replaceAll("\t", "")
						.replaceAll("\\\\", "").replaceAll("/", "")).collect(Collectors.toList()));
			}

			// KTF is optional attribute so it can be null
			if (null != ktf) {
				testobj.setKTF(ktf.ktf());
				testobj.setBugTrackingNumber(ktf.bugref());
			}

			testObjWrapperList_All.add(testobj);
			if (!testcase.skip()) {
				testObjWrapperList_WithoutSkipped.add(testobj);
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
//		}
		// @formatter:on
	}

	/**
	 * logic to bubble sort the elements
	 * 
	 * @param array
	 *            Array of all scanned test objects
	 * @return
	 */
	private TestObjectWrapper[] bubble_srt(TestObjectWrapper[] array) {
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
		return array;
	}

	/**
	 * Swap object in the array
	 * 
	 * @param i
	 *            To
	 * @param j
	 *            From
	 * @param array
	 *            Array of all scanned test objects
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
	 * @param context
	 *            Test Context
	 * @return String Test Plan
	 */
	public String getTestPlan(TestContext context) {
		StringBuilder sb = new StringBuilder();

		for (TestObjectWrapper testObject : testObjWrapperList_All) {
			sb.append("\nTestCaseName : " + testObject.getTestClassObject().getName());
			sb.append("\nSkipTest : " + Boolean.toString(testObject.isSkipTest()));
			sb.append("\nTestSequence : " + testObject.getTestsequence());
			sb.append("\nTestLabel : " + testObject.getLabelList());
			sb.append("\nDescription : " + testObject.getTestPlanDescription());
			sb.append("\nPreparedBy : " + testObject.getTestPlanPreparedBy());
			sb.append("\nPreparationDate : " + testObject.getTestPlanPreparationDate());
			sb.append("\nReviewedBy : " + testObject.getTestreviewedBy());
			sb.append("\nReviewedDate : " + testObject.getTestReviewDate());
		}

		return sb.toString();
	}

	/**
	 * Returns all scanned test cases wrapped with TestObjWrapper components
	 * 
	 * @param sortBySeqNum
	 *            Enables sorting of the test cases
	 * @param removeSkippedTests
	 *            Enables removal of test cases which are marked 'Skip'
	 * @return List of {@code TestObjectWrapper}
	 */
	public List<TestObjectWrapper> getTestObjWrapperList(boolean sortBySeqNum, boolean removeSkippedTests) {
		// Convert list to array and then do bubble sort based on sequence
		// number
		TestObjectWrapper[] sortedArray = null;

		if (!sortBySeqNum) {
			if (removeSkippedTests) {
				return testObjWrapperList_WithoutSkipped;
			} else {
				return testObjWrapperList_All;
			}
		}

		if (removeSkippedTests) {
			sortedArray = bubble_srt(testObjWrapperList_WithoutSkipped.parallelStream().toArray(TestObjectWrapper[]::new));
		} else {
			sortedArray = bubble_srt(testObjWrapperList_All.parallelStream().toArray(TestObjectWrapper[]::new));
		}
		return Arrays.asList(sortedArray);
	}

	/**
	 * Returns all scanned test cases
	 * 
	 * @param sortBySeqNum
	 *            Enables sorting of the test cases
	 * @param removeSkippedTests
	 *            Enables removal of test cases which are marked 'Skip'
	 * @return List of {@code TestExecutable}
	 * @throws IllegalAccessException
	 *             if the class or its nullary constructor is not accessible.
	 * @throws InstantiationException
	 *             if this Class represents an abstract class, an interface, an
	 *             array class, a primitive type, or void; or if the class has
	 *             no nullary constructor; or if the instantiation fails for
	 *             some other reason.
	 */
	public List<TestExecutable> getTestList(boolean sortBySeqNum, boolean removeSkippedTests) throws InstantiationException, IllegalAccessException {
		List<TestExecutable> testList = new ArrayList<TestExecutable>();

		List<TestObjectWrapper> testObjWrapperList = getTestObjWrapperList(sortBySeqNum, removeSkippedTests);
		for (TestObjectWrapper t : testObjWrapperList) {
			// create new Instance of test object so user can execute the test
			testList.add((TestExecutable) t.getTestClassObject().newInstance());
		}
		return testList;
	}

	/**
	 * Returns scanned test cases HashMap so user can search test case by
	 * TestCase FQCN
	 * 
	 * @param removeSkippedTests
	 *            removes test cases marked with skipped
	 * @return HashMap of all test objects
	 */
	public Map<String, TestObjectWrapper> getTestObjWrapperMap(boolean removeSkippedTests) {
		Map<String, TestObjectWrapper> testObjWrapperMap = new HashMap<>();

		List<TestObjectWrapper> testObjWrapperList = getTestObjWrapperList(false, removeSkippedTests);
		for (TestObjectWrapper t : testObjWrapperList) {
			// create new Instance of test object so user can execute the test
			testObjWrapperMap.put(t.getTestClassObject().getName(), t);
		}
		return testObjWrapperMap;
	}

}
