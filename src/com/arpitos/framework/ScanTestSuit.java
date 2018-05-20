package com.arpitos.framework;

import java.lang.reflect.Method;
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

import com.arpitos.annotation.AfterTest;
import com.arpitos.annotation.AfterTestsuit;
import com.arpitos.annotation.BeforeTest;
import com.arpitos.annotation.BeforeTestsuit;
import com.arpitos.annotation.TestCase;
import com.arpitos.annotation.TestPlan;
import com.arpitos.framework.infra.TestContext;
import com.arpitos.interfaces.TestExecutable;

/**
 * This class provides all utilities for reflection
 * 
 * @author arpit
 *
 */
public class ScanTestSuit {
	Reflections reflection;
	List<String> testLabels = new ArrayList<>();
	List<TestObjectWrapper> testObjWrapperList_All = new ArrayList<>();
	List<TestObjectWrapper> testObjWrapperList_WithoutSkipped = new ArrayList<>();

	/**
	 * Default constructor. Scans all packages within provided package
	 * 
	 * @param packageName
	 *            Base package name
	 * @throws Exception
	 */
	public ScanTestSuit(String packageName) throws Exception {
		scan(packageName);
	}

	/**
	 * Scans for Test cases within provided packageName
	 * 
	 * @param packageName
	 *            Base package name
	 * @throws Exception
	 */
	private void scan(String packageName) throws Exception {

		List<String> testLabels_withDuplicates = new ArrayList<>();
		reflection = new Reflections(packageName, new MethodAnnotationsScanner(), new TypeAnnotationsScanner(), new SubTypesScanner(false));

		for (Class<?> cl : reflection.getTypesAnnotatedWith(TestCase.class)) {
			TestCase testcase = cl.getAnnotation(TestCase.class);
			TestPlan testplan = cl.getAnnotation(TestPlan.class);

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

			// Test Plan Attribute is optional, If user does not set it then
			// test should continue
			TestObjectWrapper testobj = null;
			if (null == testplan) {
				testobj = new TestObjectWrapper(cl, testcase.skip(), testcase.sequence(), testcase.label(), "Warning : TestPlan Attribute is not set",
						"???", "???", "???", "???");
			} else {
				testobj = new TestObjectWrapper(cl, testcase.skip(), testcase.sequence(), testcase.label(), testplan.decription(),
						testplan.preparedBy(), testplan.preparationDate(), testplan.reviewedBy(), testplan.reviewDate());
			}

			// collect all labels
			String[] labelArray = testcase.label().toLowerCase().trim().split(":");
			for (String s : labelArray) {
				if (null != s && !"".equals(s)) {
					testLabels_withDuplicates.add(s.trim());
				}
			}

			testObjWrapperList_All.add(testobj);
			if (!testcase.skip()) {
				testObjWrapperList_WithoutSkipped.add(testobj);
			}
		}

		// Remove duplicates from the list
		testLabels = testLabels_withDuplicates.stream().distinct().collect(Collectors.toList());

		for (Method method : reflection.getMethodsAnnotatedWith(BeforeTest.class)) {
			// System.out.println("@BeforeTest = " + method.getName() + " : " +
			// method.getDeclaringClass().getName());
		}
		for (Method method : reflection.getMethodsAnnotatedWith(BeforeTestsuit.class)) {
			// System.out.println("@BeforeTestsuit = " + method.getName() + " :
			// " + method.getDeclaringClass().getName());
		}
		for (Method method : reflection.getMethodsAnnotatedWith(AfterTest.class)) {
			// System.out.println("@AfterTest = " + method.getName() + " : " +
			// method.getDeclaringClass().getName());
		}
		for (Method method : reflection.getMethodsAnnotatedWith(AfterTestsuit.class)) {
			// System.out.println("@AfterTestsuit = " + method.getName() + " : "
			// + method.getDeclaringClass().getName());
		}
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
	 * @return
	 */
	public String getTestPlan(TestContext context) {
		StringBuilder sb = new StringBuilder();
		
		for (TestObjectWrapper testObject : testObjWrapperList_All) {
			sb.append("\nTestCaseName : " + testObject.getCls().getName());
			sb.append("\nSkipTest : " + Boolean.toString(testObject.isSkipTest()));
			sb.append("\nTestSequence : " + testObject.getTestsequence());
			sb.append("\nTestLabel : " + testObject.getTestCaseLabel());
			sb.append("\nDescription : " + testObject.getTestPlanDescription());
			sb.append("\nPreparedBy : " + testObject.getTestPlanPreparedBy());
			sb.append("\nPreparationDate : " + testObject.getTestPlanPreparationDate());
			sb.append("\nReviewedBy : " + testObject.getTestreviewedBy());
			sb.append("\nReviewedDate : " + testObject.getTestReviewDate());
		}

		return sb.toString();
	}

	public String getTestLabelsToPrint(TestContext context) {
		StringBuilder sb = new StringBuilder();
		
		for (String label : testLabels) {
			sb.append("\n" + label);
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
	 * @return
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
	 * @return
	 */
	public List<TestExecutable> getTestList(boolean sortBySeqNum, boolean removeSkippedTests) throws Exception {
		List<TestExecutable> testList = new ArrayList<TestExecutable>();

		List<TestObjectWrapper> testObjWrapperList = getTestObjWrapperList(sortBySeqNum, removeSkippedTests);
		for (TestObjectWrapper t : testObjWrapperList) {
			// create new Instance of test object so user can execute the test
			testList.add((TestExecutable) t.getCls().newInstance());
		}
		return testList;
	}

	/**
	 * Returns scanned test cases HashMap so user can search test case by
	 * TestCase Name
	 * 
	 * @param removeSkippedTests
	 * @return
	 */
	public Map<String, TestObjectWrapper> getTestObjWrapperMap(boolean removeSkippedTests) {
		Map<String, TestObjectWrapper> testObjWrapperMap = new HashMap<>();

		List<TestObjectWrapper> testObjWrapperList = getTestObjWrapperList(false, removeSkippedTests);
		for (TestObjectWrapper t : testObjWrapperList) {
			// create new Instance of test object so user can execute the test
			testObjWrapperMap.put(t.getCls().getName(), t);
		}
		return testObjWrapperMap;
	}

	public List<String> getTestLabels() {
		return testLabels;
	}

	public void setTestLabels(List<String> testLabels) {
		this.testLabels = testLabels;
	}
}
