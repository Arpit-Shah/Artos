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
package com.artos.framework;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import com.artos.annotation.AfterTest;
import com.artos.annotation.AfterTestSuite;
import com.artos.annotation.AfterTestUnit;
import com.artos.annotation.BeforeTest;
import com.artos.annotation.BeforeTestSuite;
import com.artos.annotation.BeforeTestUnit;
import com.artos.annotation.DataProvider;
import com.artos.annotation.ExpectedException;
import com.artos.annotation.Group;
import com.artos.annotation.KnownToFail;
import com.artos.annotation.TestCase;
import com.artos.annotation.TestPlan;
import com.artos.annotation.Unit;
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
	List<TestUnitObjectWrapper> testUnitWrapperList_All = new ArrayList<>();
	List<TestUnitObjectWrapper> testUnitWrapperList_WithoutSkipped = new ArrayList<>();
	List<String> FQCNList = new ArrayList<>();
	Map<String, TestDataProvider> dataProviderMap = new HashMap<>();
	Method beforeTestSuite = null;
	Method afterTestSuite = null;
	Method beforeTest = null;
	Method afterTest = null;

	/**
	 * Default constructor
	 */
	public ScanTestSuite() {

	}

	/**
	 * Scans all packages within provided package
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

		// Find all annotation
		reflection = new Reflections(packageName, new MethodAnnotationsScanner(), new TypeAnnotationsScanner(), new SubTypesScanner(false));

		// GetAllDataProviderMethods => Filter Public methods => Get UpperCase
		// DataProviderName => Store it
		reflection.getMethodsAnnotatedWith(DataProvider.class).stream().filter(m -> Modifier.isPublic(m.getModifiers())).forEach(m -> {
			String dataProviderName = m.getAnnotation(DataProvider.class).name().toUpperCase();
			TestDataProvider testDataProvider = new TestDataProvider(m, dataProviderName, m.getDeclaringClass(), Modifier.isStatic(m.getModifiers()));
			if (dataProviderMap.containsKey(dataProviderName)) {
				System.err.println("Duplicate Dataprovider (case in-sensitive): " + m.getAnnotation(DataProvider.class).name());
			}
			dataProviderMap.put(dataProviderName, testDataProvider);
		});

		// getAnnotatedMethod(BeforeTestSuite.class);
		// getAnnotatedMethod(AfterTestSuite.class);
		// getAnnotatedMethod(BeforeTest.class);
		// getAnnotatedMethod(AfterTest.class);

		for (Class<?> cl : reflection.getTypesAnnotatedWith(TestCase.class)) {

			TestCase testcase = cl.getAnnotation(TestCase.class);
			TestPlan testplan = cl.getAnnotation(TestPlan.class);
			Group group = cl.getAnnotation(Group.class);
			KnownToFail ktf = cl.getAnnotation(KnownToFail.class);
			ExpectedException expectedException = cl.getAnnotation(ExpectedException.class);

			// When test case is in the root directory package will be null
			if (null == cl.getPackage() && !FQCNList.contains("")) {
				FQCNList.add("");
			} else if (null != cl.getPackage() && !FQCNList.contains(cl.getPackage().getName())) {
				FQCNList.add(cl.getPackage().getName());
			}

			TestObjectWrapper testobj = new TestObjectWrapper(cl, testcase.skip(), testcase.sequence(), testcase.dataprovider(),
					testcase.testtimeout());

			// Test Plan is optional attribute so it can be null
			if (null != testplan) {
				testobj.setTestPlanDescription(testplan.description());
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
					// Create empty arrayList
					testobj.setGroupList(new ArrayList<String>());
				}

				// each group must have * by default (which represents all
				if (!testobj.getGroupList().contains("*")) {
					testobj.getGroupList().add("*");
				}
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

			scanForTestUnits(testobj);

			testObjWrapperList_All.add(testobj);
			if (!testcase.skip()) {
				testObjWrapperList_WithoutSkipped.add(testobj);
			} else {
				FWStaticStore.logDebug(testobj.getTestClassObject().getName() + " : TestCase is marked as skip = true");
			}
		}
	}

	// private void getAnnotatedMethod(Class<? extends Annotation> annotation) {
	// reflection.getMethodsAnnotatedWith(annotation).stream().filter(m -> Modifier.isPublic(m.getModifiers())).forEach(m -> {
	// System.out.println(m.getDeclaringClass().getName());
	// });
	// }

	/**
	 * logic to bubble sort the elements
	 * 
	 * @param testObjWrapperList List of all test objects which requires sorting
	 * @return
	 */
	private List<TestObjectWrapper> bubble_srt(List<TestObjectWrapper> testObjWrapperList) {
		return testObjWrapperList.parallelStream().sorted(Comparator.comparing(t -> t.getTestsequence())).collect(Collectors.toList());
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
			TestPlanWrapper testPlan = new TestPlanWrapper();
			testPlan.setTestPlan(testObject.getTestClassObject().getName(), testObject.getTestPlanDescription(), testObject.getTestPlanPreparedBy(),
					testObject.getTestPlanPreparationDate(), testObject.getTestreviewedBy(), testObject.getTestReviewDate(),
					testObject.getTestPlanBDD());
			testPlan.setTestPlanGroup(testObject.getGroupList().toString());
			testPlan.setTestPlanKTF(testObject.isKTF(), testObject.getBugTrackingNumber());
			testPlan.setTestPlanSkip(testObject.getTestsequence(), testObject.isSkipTest(), testObject.getDataProviderName());

			testPlanList.add(testPlan);
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

		// sort FQCNList by Name
		FQCNList = FQCNList.parallelStream().sorted(Comparator.comparing(s -> s.toString())).collect(Collectors.toList());
		// Generate seperateList per package using FQCN Info and add those lists to
		// listOfTestsList
		List<List<TestObjectWrapper>> listOfTestsList = getListOfTestsListPerFQCN(listToBeSorted, FQCNList);

		// sort each list individually so test cases from same package remains together
		for (List<TestObjectWrapper> testObjList : listOfTestsList) {
			// Once sorted, Add All of the sorted test cases to master list
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

		// get sorted test case list (Sorted within package scope) so calling method can
		// utilise sorting
		List<TestObjectWrapper> testObjWrapperList = getTestObjWrapperList(true, removeSkippedTests, true);

		// populate LinkedHashMap<> with test case FQCN as key so test case object can
		// be queried using FQCN
		for (TestObjectWrapper t : testObjWrapperList) {
			// create new Instance of test object so user can execute the test
			testObjWrapperMap.put(t.getTestClassObject().getName(), t);
		}

		return testObjWrapperMap;
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
	public void scanForTestUnits(TestObjectWrapper testObj) {

		List<Method> methods = new ArrayList<Method>();
		Class<?> klass = testObj.getTestClassObject();

		// Scan method within all classes and super classes
		while (klass != Object.class) {
			/*
			 * need to iterated thought hierarchy in order to retrieve methods from above the current instance iterate though the list of methods
			 * declared in the class represented by klass variable, and add those annotated with the specified annotation
			 */
			final List<Method> allMethods = new ArrayList<Method>(Arrays.asList(klass.getDeclaredMethods()));
			for (final Method method : allMethods) {
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

			TestUnitObjectWrapper testUnitObj = new TestUnitObjectWrapper(method, unit.skip(), unit.sequence(), unit.testtimeout());
			// expectedException is optional annotation
			if (null != expectedException) {
				List<Class<? extends Throwable>> expectedExceptionsList = Arrays.asList(expectedException.expectedExceptions());
				testUnitObj.setExpectedExceptionList(expectedExceptionsList);
				testUnitObj.setExceptionContains(expectedException.contains());
				testUnitObj.setEnforce(expectedException.enforce());
			}

			// KTF is optional annotation so it can be null
			if (null != ktf) {
				testUnitObj.setKTF(ktf.ktf());
				testUnitObj.setBugTrackingNumber(ktf.bugref());
			}

			testUnitWrapperList_All.add(testUnitObj);
			if (!unit.skip()) {
				testUnitWrapperList_WithoutSkipped.add(testUnitObj);
			} else {
				FWStaticStore.logDebug(testUnitObj.getTestUnitMethod().getName() + " : Method is marked as skip = true");
			}
		}

		testObj.setTestUnitList(getTestUnitObjectWrapperList(true, true));

		// Clear list otherwise wrong methods will be added against wrong class
		testUnitWrapperList_All.clear();
		testUnitWrapperList_WithoutSkipped.clear();
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

	// **********************************************************************
	//
	// Scanning a class for common pre and post methods (Non Unit)
	//
	// **********************************************************************

	/**
	 * Scans for Test units within provided test class
	 * 
	 * @param testObj current test case object
	 */
	public void scanForPrePostMethods(Class<?> cl) {

		Class<?> klass = cl;

		// Scan method within all classes and super classes
		while (klass != Object.class) {
			/*
			 * need to iterated thought hierarchy in order to retrieve methods from above the current instance iterate though the list of methods
			 * declared in the class represented by klass variable, and add those annotated with the specified annotation
			 */
			final List<Method> allMethods = new ArrayList<Method>(Arrays.asList(klass.getDeclaredMethods()));
			for (final Method method : allMethods) {
				if (isValidMethod(method, BeforeTestSuite.class)) {
					beforeTestSuite = method;
				}

				if (isValidMethod(method, AfterTestSuite.class)) {
					afterTestSuite = method;
				}

				if (isValidMethod(method, BeforeTest.class)) {
					beforeTest = method;
				}

				if (isValidMethod(method, AfterTest.class)) {
					afterTest = method;
				}
			}
			// move to the upper class in the hierarchy in search for more methods
			klass = klass.getSuperclass();
		}
	}

	// **********************************************************************
	//
	// Generic
	//
	// **********************************************************************

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

	// **********************************************************************
	//
	// Getters and Setters
	//
	// **********************************************************************

	public Map<String, TestDataProvider> getDataProviderMap() {
		return dataProviderMap;
	}

	public List<TestObjectWrapper> getTestObjWrapperList_All() {
		return testObjWrapperList_All;
	}

	public List<TestObjectWrapper> getTestObjWrapperList_WithoutSkipped() {
		return testObjWrapperList_WithoutSkipped;
	}

	public List<String> getFQCNList() {
		return FQCNList;
	}

	public List<TestUnitObjectWrapper> getTestUnitWrapperList_All() {
		return testUnitWrapperList_All;
	}

	public void setTestUnitWrapperList_All(List<TestUnitObjectWrapper> testUnitWrapperList_All) {
		this.testUnitWrapperList_All = testUnitWrapperList_All;
	}

	public List<TestUnitObjectWrapper> getTestUnitWrapperList_WithoutSkipped() {
		return testUnitWrapperList_WithoutSkipped;
	}

	public void setTestUnitWrapperList_WithoutSkipped(List<TestUnitObjectWrapper> testUnitWrapperList_WithoutSkipped) {
		this.testUnitWrapperList_WithoutSkipped = testUnitWrapperList_WithoutSkipped;
	}

	public Method getBeforeTestSuite() {
		return beforeTestSuite;
	}

	public Method getAfterTestSuite() {
		return afterTestSuite;
	}

	public Method getBeforeTest() {
		return beforeTest;
	}

	public Method getAfterTest() {
		return afterTest;
	}

}
