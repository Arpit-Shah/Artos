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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.artos.annotation.AfterTestUnit;
import com.artos.annotation.BeforeTestUnit;
import com.artos.annotation.ExpectedException;
import com.artos.annotation.Group;
import com.artos.annotation.KnownToFail;
import com.artos.annotation.Unit;
import com.artos.framework.FWStaticStore;
import com.artos.framework.TestObjectWrapper;
import com.artos.framework.TestUnitObjectWrapper;

import javassist.Modifier;

public class ScanTestCase {

	TestContext context;
	List<TestUnitObjectWrapper> testUnitWrapperList_All = new ArrayList<>();
	List<TestUnitObjectWrapper> testUnitWrapperList_WithoutSkipped = new ArrayList<>();
	private List<TestUnitObjectWrapper> listOfTransformedTestUnits;

	public ScanTestCase(TestContext context, TestObjectWrapper testObj) {
		this.context = context;
		scanForTestUnits(testObj);
	}

	// **********************************************************************
	//
	// Scanning a test case for unit testing
	//
	// **********************************************************************

	/**
	 * Scans for Test units within provided test class
	 * 
	 * @param testObj current test case object
	 */
	private void scanForTestUnits(TestObjectWrapper testObj) {

		List<Method> methods = new ArrayList<Method>();
		Class<?> klass = testObj.getTestClassObject();
		listOfTransformedTestUnits = new ArrayList<>();

		// Scan method within all classes and super classes
		while (klass != Object.class) {
			/*
			 * need to iterated thought hierarchy in order to retrieve methods from above the current instance iterate though the list of methods
			 * declared in the class represented by klass variable, and add those annotated with the specified annotation
			 */
			final List<Method> allMethods = new ArrayList<Method>(Arrays.asList(klass.getDeclaredMethods()));
			for (final Method method : allMethods) {

				// If method is unit test then can not be pre post
				if (isValidMethod(method, Unit.class)) {
					methods.add(method);
					continue;
				}

				// Do not explore in super classes if BeforeTestUnit method is found in existing class
				if (null == testObj.getMethodBeforeTestUnit() && isValidMethod(method, BeforeTestUnit.class)) {
					testObj.setMethodBeforeTestUnit(method);
					continue;
				}
				// Do not explore in super classes if AfterTestUnit method is found in existing class
				if (null == testObj.getMethodAfterTestUnit() && isValidMethod(method, AfterTestUnit.class)) {
					testObj.setMethodAfterTestUnit(method);
					continue;
				}
			}
			// move to the upper class in the hierarchy in search for more methods
			klass = klass.getSuperclass();
		}

		// Iterate through all valid methods and construct a list of executable methods
		for (Method method : methods) {
			Unit unit = method.getAnnotation(Unit.class);
			KnownToFail ktf = method.getAnnotation(KnownToFail.class);
			ExpectedException expectedException = method.getAnnotation(ExpectedException.class);
			Group group = method.getAnnotation(Group.class);

			TestUnitObjectWrapper testUnitObj = new TestUnitObjectWrapper(method, unit.skip(), unit.sequence(), unit.testtimeout());

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
					testUnitObj.setGroupList(groupList.stream().map(s -> s.toUpperCase().trim().replaceAll("\n", "").replaceAll("\r", "")
							.replaceAll("\t", "").replaceAll("\\\\", "").replaceAll("/", "")).collect(Collectors.toList()));
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
				testUnitObj.setBugTrackingNumber(ktf.bugref());
			}

			// expectedException is optional annotation
			if (null != expectedException) {
				List<Class<? extends Throwable>> expectedExceptionsList = Arrays.asList(expectedException.expectedExceptions());
				testUnitObj.setExpectedExceptionList(expectedExceptionsList);
				testUnitObj.setExceptionContains(expectedException.contains());
				testUnitObj.setEnforce(expectedException.enforce());
			}

			testUnitWrapperList_All.add(testUnitObj);
			if (!unit.skip()) {
				testUnitWrapperList_WithoutSkipped.add(testUnitObj);
			} else {
				FWStaticStore.logDebug(testUnitObj.getTestUnitMethod().getName() + " : Method is marked as skip = true");
			}
		}

		/*
		 * @formatter:off
		 * 1. If XML testScript is provided then use it
		 * 3. If all of the above is not provided then use reflection to find test cases
		 */
		
		if (null != context.getTestSuite()) {
			List<String> groupList = context.getTestSuite().getTestUnitGroupList();
			groupBasedFiltering(groupList);
		} else {
			groupBasedFiltering(context.getTestUnitGroupListPassedByMainMethod());
		}

		// Clear list otherwise wrong methods will be added against wrong class
		testUnitWrapperList_All.clear();
		testUnitWrapperList_WithoutSkipped.clear();
	}

	/**
	 * Get all test unit objects from test case. Any test units with \"skip = true\" will be skipped. Test units will be ordered as per sequence number
	 */
	private void groupBasedFiltering(List<String> refGroup) {
		List<TestUnitObjectWrapper> listOfTestUnitObj = getTestUnitObjectWrapperList(true, true);
		for (TestUnitObjectWrapper unit : listOfTestUnitObj) {
			if (belongsToApprovedGroup(refGroup, unit.getGroupList())) {
				listOfTransformedTestUnits.add(unit);
			}
		}
	}

	/**
	 * logic to bubble sort the elements
	 * 
	 * @param testUnitObjWrapperList List of all test unit objects which requires sorting
	 * @return
	 */
	private List<TestUnitObjectWrapper> bubble_srt_units(List<TestUnitObjectWrapper> testUnitWrapperList) {
		return testUnitWrapperList.parallelStream().sorted(Comparator.comparing(m -> m.getTestsequence())).collect(Collectors.toList());
	}

	/**
	 * Returns all scanned unit test cases wrapped with TestUnitObjWrapper components. if user has chosen to remove "SKIPPED" test cases then any test
	 * units marked with "skip=true" will be omitted from the list. If user has chosen to sort by sequence number then test units will be sorted by
	 * sequence number.
	 * 
	 * @param sortBySeqNum Enables sorting of the test cases
	 * @param removeSkippedTests Enables removal of test cases which are marked 'skip=true'
	 * @return List of {@code TestUnitObjectWrapper}
	 */
	private List<TestUnitObjectWrapper> getTestUnitObjectWrapperList(boolean sortBySeqNum, boolean removeSkippedTests) {
		if (sortBySeqNum) {
			return removeSkippedTests ? bubble_srt_units(testUnitWrapperList_WithoutSkipped) : bubble_srt_units(testUnitWrapperList_All);
		}

		// If sorting is not required
		return removeSkippedTests ? testUnitWrapperList_WithoutSkipped : testUnitWrapperList_All;
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
	}

	/**
	 * Validates if method follows all rules of being {@link Unit}
	 * 
	 * @param method method object
	 * @param annotation annotation which method must adhere to
	 * @return true | false
	 */
	private boolean isValidMethod(Method method, Class<? extends Annotation> annotationClass) {
		// Only method with @Unit annotation is allowed
		if (!method.isAnnotationPresent(annotationClass)) {
			FWStaticStore.logDebug(method.getName() + " : Method Ignored, Method is missing annotation " + annotationClass.getName());
			return false;
		}
		// Only public methods are allowed
		if (!Modifier.isPublic(method.getModifiers())) {
			FWStaticStore.logDebug(method.getName() + " : Method Ignored, Method is not public");
			return false;
		}
		// Only non static methods are allowed
		if (Modifier.isStatic(method.getModifiers())) {
			FWStaticStore.logDebug(method.getName() + " : Method Ignored, Method is static");
			return false;
		}
		// Only method with return value to be void is allowed
		if (null != method.getReturnType() && void.class != method.getReturnType()) {
			FWStaticStore.logDebug(method.getName() + " : Method Ignored, Method return type is not void");
			return false;
		}
		// Only method with one parameter is allowed
		if (1 != method.getParameterCount()) {
			FWStaticStore.logDebug(method.getName() + " : Method Ignored, Method parameter count is not 1");
			return false;
		}
		// Only method with parameter of type TestContext is allowed
		if (null == method.getParameters() || method.getParameterTypes()[0] != TestContext.class) {
			FWStaticStore.logDebug(method.getName() + " : Method Ignored, Method parameter type is not " + TestContext.class.getName());
			return false;
		}
		return true;
	}

	public List<TestUnitObjectWrapper> getListOfTransformedTestUnits() {
		return listOfTransformedTestUnits;
	}
}
