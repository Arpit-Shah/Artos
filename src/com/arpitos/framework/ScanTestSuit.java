package com.arpitos.framework;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.core.Logger;
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

public class ScanTestSuit {
	Reflections reflaction;
	List<TestObjectWrapper> testObjWrapperList_All = new ArrayList<>();
	List<TestObjectWrapper> testObjWrapperList_WithoutSkipped = new ArrayList<>();

	public ScanTestSuit(String packageName) throws Exception {
		scan(packageName);
	}

	private void scan(String packageName) throws Exception {

		reflaction = new Reflections(packageName, new MethodAnnotationsScanner(), new TypeAnnotationsScanner(), new SubTypesScanner(false));

		for (Class<?> cl : reflaction.getTypesAnnotatedWith(TestCase.class)) {
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

			testObjWrapperList_All.add(testobj);
			if (!testcase.skip()) {
				testObjWrapperList_WithoutSkipped.add(testobj);
			}
		}

		for (Method method : reflaction.getMethodsAnnotatedWith(BeforeTest.class)) {
			// System.out.println("@BeforeTest = " + method.getName() + " : " +
			// method.getDeclaringClass().getName());
		}
		for (Method method : reflaction.getMethodsAnnotatedWith(BeforeTestsuit.class)) {
			// System.out.println("@BeforeTestsuit = " + method.getName() + " :
			// " + method.getDeclaringClass().getName());
		}
		for (Method method : reflaction.getMethodsAnnotatedWith(AfterTest.class)) {
			// System.out.println("@AfterTest = " + method.getName() + " : " +
			// method.getDeclaringClass().getName());
		}
		for (Method method : reflaction.getMethodsAnnotatedWith(AfterTestsuit.class)) {
			// System.out.println("@AfterTestsuit = " + method.getName() + " : "
			// + method.getDeclaringClass().getName());
		}
	}

	// logic to bubble sort the elements
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

	private void swapNumbers(int i, int j, TestObjectWrapper[] array) {
		TestObjectWrapper temp;
		temp = array[i];
		array[i] = array[j];
		array[j] = temp;
	}

	public void generateTestPlan(TestContext context) {
		Logger logger = context.getLogger();

		for (TestObjectWrapper testObject : testObjWrapperList_All) {
			logger.info("\nTestCaseName : " + testObject.getCls().getName());
			logger.info("SkipTest : " + Boolean.toString(testObject.isSkipTest()));
			logger.info("TestSequence : " + testObject.getTestsequence());
			logger.info("TestLabel : " + testObject.getTestCaseLabel());
			logger.info("Description : " + testObject.getTestPlanDescription());
			logger.info("PreparedBy : " + testObject.getTestPlanPreparedBy());
			logger.info("PreparationDate : " + testObject.getTestPlanPreparationDate());
			logger.info("ReviewedBy : " + testObject.getTestreviewedBy());
			logger.info("ReviewedDate : " + testObject.getTestReviewDate());
		}
	}

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

	public List<TestExecutable> getTestList(boolean sortBySeqNum, boolean removeSkippedTests) throws Exception {
		List<TestExecutable> testList = new ArrayList<TestExecutable>();

		List<TestObjectWrapper> testObjWrapperList = getTestObjWrapperList(sortBySeqNum, removeSkippedTests);
		for (TestObjectWrapper t : testObjWrapperList) {
			// create new Instance of test object so user can execute the test
			testList.add((TestExecutable) t.getCls().newInstance());
		}
		return testList;
	}

	public Map<String, TestObjectWrapper> getTestObjWrapperMap(boolean removeSkippedTests) {
		Map<String, TestObjectWrapper> testObjWrapperMap = new HashMap<>();

		List<TestObjectWrapper> testObjWrapperList = getTestObjWrapperList(false, removeSkippedTests);
		for (TestObjectWrapper t : testObjWrapperList) {
			// create new Instance of test object so user can execute the test
			testObjWrapperMap.put(t.getCls().getName(), t);
		}
		return testObjWrapperMap;
	}
}
