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

import java.io.InvalidObjectException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import com.artos.framework.Enums.TestStatus;
import com.artos.framework.FWStaticStore;
import com.artos.interfaces.TestProgress;
import com.artos.utils.UtilsFramework;

public class RunnerTestUnits {

	TestContext context;
	TestObjectWrapper t;
	List<TestProgress> listenerList;

	protected RunnerTestUnits(TestContext context, List<TestProgress> listenerList) {
		this.context = context;
		this.listenerList = listenerList;
	}

	/**
	 * Run unit tests
	 * 
	 * @param t test case
	 */
	protected void runSingleThreadUnits(TestObjectWrapper t) {
		this.t = t;
		// ********************************************************************************************
		// TestUnits Execution Start
		// ********************************************************************************************
		try {
			// get test unit list
			List<TestUnitObjectWrapper> unitTests = t.getTestUnitList();

			if (null == unitTests || unitTests.isEmpty()) {
				// Setting test status to Pass will ensure that extent report will not set unknown status in absence of unit execution
				context.setTestStatus(TestStatus.PASS, "0 units executed");
				System.err.println("[WARNING] : Test unit execution list is empty");
				System.err.println("[HINT-01] : Test unit(s) may not be present in the test case");
				System.err.println("[HINT-02] : Test unit(s) may not be meeting criteria");
				System.err.println("[HINT-03] : Test unit(s) are skipped due to group filtering");
				// Do not run before and after for the unit if no units to execute
				return;
			}

			try {
				// Run local before Method prior to any test Execution
				if (null != t.getMethodBeforeTestCase()) {
					notifyLocalBeforeTestCaseMethodExecutionStarted(t.getMethodBeforeTestCase().getName(), t);
					t.getMethodBeforeTestCase().invoke(t.getTestClassObject().getDeclaredConstructor().newInstance(), context);
					notifyLocalBeforeTestCaseMethodExecutionFinished(t);
				}
			} catch (Throwable e) {
				printException(e);
			}

			// --------------------------------------------------------------------------------------------
			for (TestUnitObjectWrapper unit : unitTests) {

				long preserveFailCount = context.getCurrentUnitFailCount();

				// Reset test status for next execution
				context.setCurrentUnitTestStatus(TestStatus.PASS);

				// If "stop on fail" is selected then stop test execution
				if (FWStaticStore.frameworkConfig.isStopOnFail()) {
					if (context.getCurrentFailCount() > 0 || context.getCurrentUnitFailCount() > 0) {
						break;
					}
				}

				context.setCurrentTestUnit(unit);
				notifyPrintTestUnitPlan(unit);

				if (null != unit.getDependencyList() && !unit.getDependencyList().isEmpty()) {
					if (!hasDependencyMet(unitTests, unit)) {
						context.getLogger().warn(FWStaticStore.ARTOS_UNIT_DEPENDENCY_REQ_NOT_MET);
						continue;
					}
				}

				// if data provider name is not specified then only execute test once
				if (null == unit.getDataProviderName() || "".equals(unit.getDataProviderName())) {
					runIndividualUnitTest(unit);
				} else {
					runParameterizedUnitTest(unit);
				}

				// If "drop following units upon failure" is enabled then drop rest of the unit execution
				if (unit.isDropRemainingUnitsUponFailure() && context.getCurrentUnitFailCount() > preserveFailCount) {
					context.getLogger().info(FWStaticStore.ARTOS_DROP_EXECUTION_UPON_UNIT_FAIL_STAMP);
					break;
				}
			}
			// --------------------------------------------------------------------------------------------

			try {
				// Run local after Method prior to any test Execution
				if (null != t.getMethodAfterTestCase()) {
					notifyLocalAfterTestCaseMethodExecutionStarted(t.getMethodAfterTestCase().getName(), t);
					t.getMethodAfterTestCase().invoke(t.getTestClassObject().getDeclaredConstructor().newInstance(), context);
					notifyLocalAfterTestCaseMethodExecutionFinished(t);
				}
			} catch (Throwable e) {
				printException(e);
			}

		} catch (Throwable e) {
			// Handle if any exception in pre-post runnable
			UtilsFramework.writePrintStackTrace(context, e);
		}
		// ********************************************************************************************
		// TestUnits Execution Finish
		// ********************************************************************************************
	}

	/**
	 * Dependency feature ensures that user specified test cases are executed prior to executing target test case and pre-requisite test cases must be
	 * completed with PASS status, otherwise dependency agreement will not be met.
	 * 
	 * @param unitList = test unit list
	 * @param unit = target test unit
	 * @return true if dependency agreement is met | false if dependency agreement is not met
	 */
	private boolean hasDependencyMet(List<TestUnitObjectWrapper> unitList, TestUnitObjectWrapper unit) {

		// Find execution index of target test unit
		int indexOfUnit = unitList.indexOf(unit);

		// Iterate through provided dependency list and ensure it meets an agreement
		for (String depenencyMethod : unit.getDependencyList()) {

			List<TestUnitObjectWrapper> matchList;

			// Find list dependency method in execution list, If we can not find listed method in execution list then dependency agreement will not be
			// met
			{
				matchList = unitList.stream().filter(obj -> obj.getTestUnitMethod().getName().equals(depenencyMethod)).collect(Collectors.toList());
				if (null == matchList || matchList.isEmpty()) {
					return false;
				}
			}

			TestUnitObjectWrapper dependencyTestUnitObjectWrapper = matchList.get(0);
			// If dependency test case execution index is same or greater than target test-case execution index then requirements will not be met
			{
				int matchIndex = unitList.indexOf(dependencyTestUnitObjectWrapper);
				if (matchIndex >= indexOfUnit) {
					return false;
				}
			}

			// If dependency test cases status is not PASS then dependency agreement will not be met
			{
				if (dependencyTestUnitObjectWrapper.getTestUnitOutcomeList().stream().anyMatch(s -> (s.equals(TestStatus.FAIL)))) {
					return false;
				}
				if (dependencyTestUnitObjectWrapper.getTestUnitOutcomeList().stream().anyMatch(s -> (s.equals(TestStatus.KTF)))) {
					return false;
				}
				if (dependencyTestUnitObjectWrapper.getTestUnitOutcomeList().stream().anyMatch(s -> (s.equals(TestStatus.SKIP)))) {
					return false;
				}
			}
		}
		return true;
	}

	private void printException(Throwable e) {
		if (e.getClass() == InvocationTargetException.class) {
			// Catch InvocationTargetException and return cause
			UtilsFramework.writePrintStackTrace(context, e.getCause());
		} else {
			UtilsFramework.writePrintStackTrace(context, e);
		}
	}

	/**
	 * Responsible for execution individual test units
	 * 
	 * @param unit TestUnit in format {@code TestUnitObjectWrapper}
	 */
	private void runIndividualUnitTest(TestUnitObjectWrapper unit) {
		try {
			// Run global before method prior to each test unit execution
			if (null != context.getBeforeTestUnit()) {
				notifyGlobalBeforeTestUnitMethodExecutionStarted(context.getBeforeTestUnit().getName(), unit);
				context.getBeforeTestUnit().invoke(context.getPrePostRunnableObj().getDeclaredConstructor().newInstance(), context);
				notifyGlobalBeforeTestUnitMethodExecutionFinished(unit);
			}

			// Run custom before method prior to each test unit execution
			if (null != t.getMethodBeforeTestUnit()) {
				notifyLocalBeforeTestUnitMethodExecutionStarted(t, unit);
				t.getMethodBeforeTestUnit().invoke(t.getTestClassObject().getDeclaredConstructor().newInstance(), context);
				notifyLocalBeforeTestUnitMethodExecutionFinished(unit);
			}
		} catch (Throwable e) {
			printException(e);
		}

		// ********************************************************************************************
		// TestUnit Start
		// ********************************************************************************************
		try {
			unit.setTestUnitStartTime(System.currentTimeMillis());

			// If test timeout is defined then monitor thread for timeout
			if (0 != unit.getTestTimeout()) {
				runUnitTestWithTimeout(unit);
			} else {
				runSimpleUnitTest(unit);
			}

			postTestValidation(unit);
		} catch (Throwable e) {
			processTestUnitException(unit, e);
		} finally {
			unit.setTestUnitFinishTime(System.currentTimeMillis());
		}
		// ********************************************************************************************
		// TestUnit Finish
		// ********************************************************************************************

		try {

			// Run custom after method post each test unit execution
			if (null != t.getMethodAfterTestUnit()) {
				notifyLocalAfterTestUnitMethodExecutionStarted(t, unit);
				t.getMethodAfterTestUnit().invoke(t.getTestClassObject().getDeclaredConstructor().newInstance(), context);
				notifyLocalAfterTestUnitMethodExecutionFinished(unit);
			}

			// Run global after method post each test unit execution
			if (null != context.getAfterTestUnit()) {
				notifyGlobalAfterTestUnitMethodExecutionStarted(context.getAfterTestUnit().getName(), unit);
				context.getAfterTestUnit().invoke(context.getPrePostRunnableObj().getDeclaredConstructor().newInstance(), context);
				notifyGlobalAfterTestUnitMethodExecutionFinished(unit);
			}
			
			// Run custom after unit failed method post each failed test unit execution
			// If KTF marked test unit is passing then also execute this method because outcome of this unit will be failed
			if (context.getCurrentUnitTestStatus() == TestStatus.FAIL || (unit.isKTF() && context.getCurrentUnitTestStatus() == TestStatus.PASS)) {
				if (null != t.getMethodAfterFailedUnit()) {
					notifyLocalAfterFailedUnitMethodExecutionStarted(t, unit);
					t.getMethodAfterFailedUnit().invoke(t.getTestClassObject().newInstance(), context);
					notifyLocalAfterFailedUnitMethodExecutionFinished(unit);
				}
			}

			// Run global after unit failed method post each failed test unit execution
			// If KTF marked test unit is passing then also execute this method because outcome of this unit will be failed
			if (context.getCurrentUnitTestStatus() == TestStatus.FAIL || (unit.isKTF() && context.getCurrentUnitTestStatus() == TestStatus.PASS)) {
				if (null != context.getAfterFailedUnit()) {
					notifyGlobalAfterFailedUnitMethodExecutionStarted(context.getAfterFailedUnit().getName(), unit);
					context.getAfterFailedUnit().invoke(context.getPrePostRunnableObj().getDeclaredConstructor().newInstance(), context);
					notifyGlobalAfterFailedUnitMethodExecutionFinished(unit);
				}
			}
		} catch (Throwable e) {
			printException(e);
		}

		// ********************************************************************************************
		// Generate Summary
		// ********************************************************************************************
		context.generateUnitTestSummary(unit);
		// context.getLogger().info(FWStaticStore.ARTOS_LINE_BREAK_2);

	}

	/**
	 * Responsible for executing data provider method which upon successful execution returns an array of parameters. TestCase will be re-run using
	 * all parameters available in the parameter array. If data provider method returns empty array or null then test case will be executed only once
	 * with null arguments.
	 * 
	 * @param unit TestCase in format {@code TestUnitObjectWrapper}
	 */
	private void runParameterizedUnitTest(TestUnitObjectWrapper unit) {
		Object[][] data = null;
		TestDataProvider dataProviderObj;

		try {
			// get dataProvider specified for this test case (data provider name is always
			// stored in upper case)
			dataProviderObj = context.getDataProviderMap().get(unit.getDataProviderName().toUpperCase());

			// If specified data provider is not found in the list then throw exception
			// (Remember : Data provider name is case in-sensitive)
			if (null == dataProviderObj) {
				throw new InvalidObjectException("DataProvider not found (or private) : " + (unit.getDataProviderName()));
			}

			// Handle it because this executes method
			try {
				if (dataProviderObj.isStaticMethod()) {
					data = (Object[][]) dataProviderObj.getMethod().invoke(null, context);
				} else {
					/* NonStatic data provider method needs an instance */
					data = (Object[][]) dataProviderObj.getMethod().invoke(dataProviderObj.getClassOfTheMethod().getDeclaredConstructor().newInstance(), context);
				}
			} catch (InvocationTargetException e) {
				context.getLogger().debug("=================================================");
				context.getLogger().debug("=== DataProvider Method failed to return data ===");
				context.getLogger().debug("=================================================");
				processInvocationTargetException(e);
			}

			// If data provider method returns null or empty object then execute test with
			// null parameter
			if (null == data || data.length == 0) {
				executeChildTest(unit, new String[][] { {} }, 0);
			} else {
				for (int i = 0; i < data.length; i++) {
					executeChildTest(unit, data, i);
				}
			}
		} catch (Exception e) {
			// Print Exception
			UtilsFramework.writePrintStackTrace(context, e);
			// notifyTestSuiteException(e.getMessage());

			// Mark current test as fail due to exception during data provider processing
			context.setTestStatus(TestStatus.FAIL, e.getMessage());
			context.generateUnitTestSummary(unit);
		}
	}

	/**
	 * Responsible for execution of a test unit.
	 * 
	 * @param unit TestCase in format {@code TestUnitObjectWrapper}
	 * @throws Exception Exception during test execution
	 */
	private void runSimpleUnitTest(TestUnitObjectWrapper unit) throws Exception {
		// --------------------------------------------------------------------------------------------
		try {
			notifyTestUnitExecutionStarted(unit);

			// Run single unit
			unit.getTestUnitMethod().invoke(t.getTestClassObject().getDeclaredConstructor().newInstance(), context);

			notifyTestUnitExecutionFinished(unit);

			// When method fails via reflection, it throws InvocationTargetExcetion
		} catch (InvocationTargetException e) {
			processInvocationTargetException(e);
		}

		// --------------------------------------------------------------------------------------------
	}

	private void processInvocationTargetException(InvocationTargetException e) throws InvocationTargetException, Exception {
		// Catch InvocationTargetException and return cause
		if (null == e.getCause()) {
			throw e;
		} else {
			// Cast cause into Exception because Executor service can not handle throwable
			throw (Exception) e.getCause();
		}
	}

	/**
	 * Responsible for executing test unit with thread timeout. If test unit execution is not finished within expected time then test will be
	 * considered failed.
	 * 
	 * @param unit Test unit in format {@code TestUnitObjectWrapper} object
	 * @throws Throwable Exception during test execution
	 */
	private void runUnitTestWithTimeout(TestUnitObjectWrapper unit) throws Throwable {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Future<String> future = executor.submit(new Callable<String>() {
			@Override
			public String call() throws Exception {
				runSimpleUnitTest(unit);
				return "TEST UNIT FINISHED WITHIN TIME";
			}
		});

		try {
			// System.out.println(future.get(t.getTestTimeout(), TimeUnit.MILLISECONDS));
			future.get(unit.getTestTimeout(), TimeUnit.MILLISECONDS);
		} catch (TimeoutException e) {
			future.cancel(true);
			context.setTestStatus(TestStatus.FAIL, "TEST UNIT TIMED OUT");
		} catch (ExecutionException e) {
			future.cancel(true);
			if (null == e.getCause()) {
				// If no cause is listed then process parent exception
				throw e;
			} else {
				// If cause is listed then only process cause
				throw e.getCause();
			}
		} catch (InterruptedException e) {
			context.getLogger().warn("InterruptedException was supressed assuming thread was intentinally killed by a runner");
			// This can happen if Unit future thread is cancelled/killed by Test case future while thread is busy/sleeping or occupied.
			// One scenario : if test case time out before unit timeout then test case will force cancel future.
		}

	}

	/**
	 * Responsible for execution of test units (Considered as child test units) with given parameter. Parameterised object array index and value(s)
	 * class type(s) will be printed prior to test execution for user's benefit.
	 * 
	 * @param unit TestCase in format {@code TestUnitObjectWrapper}
	 * @param data Array of parameters
	 * @param arrayIndex Parameter array index
	 */
	private void executeChildTest(TestUnitObjectWrapper unit, Object[][] data, int arrayIndex) {
		String userInfo = "DataProvider(" + arrayIndex + ")  : ";
		if (data[arrayIndex].length == 2) {
			context.setParameterisedObject1(null == data[arrayIndex][0] ? null : data[arrayIndex][0]);
			context.setParameterisedObject2(null == data[arrayIndex][1] ? null : data[arrayIndex][1]);
			String firstType = (context.getParameterisedObject1().getClass().getSimpleName());
			String secondType = (context.getParameterisedObject2().getClass().getSimpleName());
			userInfo += "[" + firstType + "][" + secondType + "]";

		} else if (data[arrayIndex].length == 1) {
			context.setParameterisedObject1(null == data[arrayIndex][0] ? null : data[arrayIndex][0]);
			context.setParameterisedObject2(null);
			String firstType = (context.getParameterisedObject1().getClass().getSimpleName());
			userInfo += "[" + firstType + "][]";
		} else {
			context.setParameterisedObject1(null);
			context.setParameterisedObject2(null);
			userInfo += "[][]";
		}
		context.getLogger().info(userInfo);

		// ********************************************************************************************
		// Parameterised Child TestCase Start
		// ********************************************************************************************

		notifyChildTestUnitExecutionStarted(t, unit, userInfo);

		runIndividualUnitTest(unit);

		notifyChildTestUnitExecutionFinished(unit);

		// ********************************************************************************************
		// Parameterised Child TestCase Finish
		// ********************************************************************************************
	}

	/**
	 * Responsible for processing throwable/exception thrown by test cases during execution time. If {@code ExpectedException} annotation defines
	 * expected throwable/exception and received throwable/exception does not match any of the defined throwable(s)/Exception(s) then test will be
	 * marked as FAIL.
	 * 
	 * @param unit test case in format {@code TestObjectWrapper}
	 * @param e {@code Throwable} or {@code Exception}
	 */
	private void processTestUnitException(TestUnitObjectWrapper unit, Throwable e) {
		// If user has not specified expected exception then fail the test
		if (null != unit.getExpectedExceptionList() && !unit.getExpectedExceptionList().isEmpty()) {

			boolean exceptionMatchFound = false;
			for (Class<? extends Throwable> exceptionClass : unit.getExpectedExceptionList()) {
				if (e.getClass() == exceptionClass) {
					/* Exception matches as specified by user */
					context.setTestStatus(TestStatus.PASS, "Exception class is as expected : " + e.getClass().getName());

					/* If regular expression then validate exception message with regular expression */
					/* If regular expression does not match then do string compare */
					if (null != unit.getExceptionContains() && !"".equals(unit.getExceptionContains())) {
						if (e.getMessage().contains(unit.getExceptionContains())) {
							context.setTestStatus(TestStatus.PASS, "Exception message contains : " + unit.getExceptionContains());
						} else if (e.getMessage().matches(unit.getExceptionContains())) {
							context.setTestStatus(TestStatus.PASS, "Exception message matches regex : " + unit.getExceptionContains());
						} else {
							context.setTestStatus(TestStatus.FAIL, "Exception message does not match : \nExpected : " + unit.getExceptionContains()
									+ "\nReceived : " + e.getMessage());
							notifyPrintTestUnitException(e);
						}
					}

					exceptionMatchFound = true;
					break;
				}
			}
			if (!exceptionMatchFound) {
				String expectedExceptions = "";
				for (Class<? extends Throwable> exceptionClass : unit.getExpectedExceptionList()) {
					expectedExceptions += exceptionClass.getName() + " ";
				}
				context.setTestStatus(TestStatus.FAIL,
						"Exception is not as expected : \nExpected : " + expectedExceptions + "\nReturned : " + e.getClass().getName());
				UtilsFramework.writePrintStackTrace(context, e);
				notifyPrintTestUnitException(e);
			}
		} else {
			context.setTestStatus(TestStatus.FAIL, e.getMessage());
			UtilsFramework.writePrintStackTrace(context, e);
			notifyPrintTestUnitException(e);
		}
	}

	/**
	 * Responsible for post validation after test case execution is successfully completed. If expected throwable(s)/exception(s) are defined by user
	 * using {@code ExpectedException} and test case status is PASS or FAIL then test unit should be marked failed for not throwing expected
	 * throwable/exception.
	 * 
	 * <PRE>
	 * If test status is marked as SKIP or KTF then do not fail test case based on ExpectedException conditions. 
	 * If test is marked as SKIP then user should have taken decision based on condition checking so test framework does not need to overrule decision.
	 * If test is marked as KTF then user already knows that this test scenario is known to fail, so forcefully failing will dilute the meaning of having known to fail status.
	 * </PRE>
	 * 
	 * @param unit {@code TestUnitObjectWrapper} object
	 */
	private void postTestValidation(TestUnitObjectWrapper unit) {
		if (context.getCurrentTestStatus() == TestStatus.PASS || context.getCurrentTestStatus() == TestStatus.FAIL) {
			if (null != unit.getExpectedExceptionList() && !unit.getExpectedExceptionList().isEmpty() && unit.isEnforceException()) {
				// Exception annotation was specified but did not occur
				context.setTestStatus(TestStatus.FAIL, "Exception was specified but did not occur");
			}
		}
	}

	// ==================================================================================
	// Register, deRegister and Notify Event Listeners
	// ==================================================================================

	void notifyGlobalBeforeTestUnitMethodExecutionStarted(String methodName, TestUnitObjectWrapper unit) {
		for (TestProgress listener : listenerList) {
			listener.globalBeforeTestUnitMethodExecutionStarted(methodName, unit);
		}
	}

	void notifyGlobalBeforeTestUnitMethodExecutionFinished(TestUnitObjectWrapper unit) {
		for (TestProgress listener : listenerList) {
			listener.globalBeforeTestUnitMethodExecutionFinished(unit);
		}
	}

	void notifyGlobalAfterTestUnitMethodExecutionStarted(String methodName, TestUnitObjectWrapper unit) {
		for (TestProgress listener : listenerList) {
			listener.globalAfterTestUnitMethodExecutionStarted(methodName, unit);
		}
	}

	void notifyGlobalAfterTestUnitMethodExecutionFinished(TestUnitObjectWrapper unit) {
		for (TestProgress listener : listenerList) {
			listener.globalAfterTestUnitMethodExecutionFinished(unit);
		}
	}

	void notifyGlobalAfterFailedUnitMethodExecutionStarted(String methodName, TestUnitObjectWrapper unit) {
		for (TestProgress listener : listenerList) {
			listener.globalAfterFailedUnitMethodExecutionStarted(methodName, unit);
		}
	}

	void notifyGlobalAfterFailedUnitMethodExecutionFinished(TestUnitObjectWrapper unit) {
		for (TestProgress listener : listenerList) {
			listener.globalAfterFailedUnitMethodExecutionFinished(unit);
		}
	}

	void notifyLocalBeforeTestUnitMethodExecutionStarted(TestObjectWrapper t, TestUnitObjectWrapper unit) {
		for (TestProgress listener : listenerList) {
			listener.localBeforeTestUnitMethodExecutionStarted(t, unit);
		}
	}

	void notifyLocalBeforeTestUnitMethodExecutionFinished(TestUnitObjectWrapper unit) {
		for (TestProgress listener : listenerList) {
			listener.localBeforeTestUnitMethodExecutionFinished(unit);
		}
	}

	void notifyLocalAfterTestUnitMethodExecutionStarted(TestObjectWrapper t, TestUnitObjectWrapper unit) {
		for (TestProgress listener : listenerList) {
			listener.localAfterTestUnitMethodExecutionStarted(t, unit);
		}
	}

	void notifyLocalAfterTestUnitMethodExecutionFinished(TestUnitObjectWrapper unit) {
		for (TestProgress listener : listenerList) {
			listener.localAfterTestUnitMethodExecutionFinished(unit);
		}
	}
	
	void notifyLocalAfterFailedUnitMethodExecutionStarted(TestObjectWrapper t, TestUnitObjectWrapper unit) {
		for (TestProgress listener : listenerList) {
			listener.localAfterFailedUnitMethodExecutionStarted(t, unit);
		}
	}

	void notifyLocalAfterFailedUnitMethodExecutionFinished(TestUnitObjectWrapper unit) {
		for (TestProgress listener : listenerList) {
			listener.localAfterFailedUnitMethodExecutionFinished(unit);
		}
	}

	void notifyTestUnitExecutionStarted(TestUnitObjectWrapper unit) {
		for (TestProgress listener : listenerList) {
			listener.testUnitExecutionStarted(unit);
		}
	}

	void notifyTestUnitExecutionFinished(TestUnitObjectWrapper unit) {
		for (TestProgress listener : listenerList) {
			listener.testUnitExecutionFinished(unit);
		}
	}

	private void notifyChildTestUnitExecutionStarted(TestObjectWrapper t, TestUnitObjectWrapper unit, String userInfo) {
		for (TestProgress listener : listenerList) {
			listener.childTestUnitExecutionStarted(t, unit, userInfo);
		}
	}

	private void notifyChildTestUnitExecutionFinished(TestUnitObjectWrapper unit) {
		for (TestProgress listener : listenerList) {
			listener.childTestUnitExecutionFinished(unit);
		}
	}

	void notifyLocalBeforeTestCaseMethodExecutionStarted(String methodName, TestObjectWrapper t) {
		for (TestProgress listener : listenerList) {
			listener.localBeforeTestCaseMethodExecutionStarted(methodName, t);
		}
	}

	void notifyLocalBeforeTestCaseMethodExecutionFinished(TestObjectWrapper t) {
		for (TestProgress listener : listenerList) {
			listener.localBeforeTestCaseMethodExecutionFinished(t);
		}
	}

	void notifyLocalAfterTestCaseMethodExecutionStarted(String methodName, TestObjectWrapper t) {
		for (TestProgress listener : listenerList) {
			listener.localAfterTestCaseMethodExecutionStarted(methodName, t);
		}
	}

	void notifyLocalAfterTestCaseMethodExecutionFinished(TestObjectWrapper t) {
		for (TestProgress listener : listenerList) {
			listener.localAfterTestCaseMethodExecutionFinished(t);
		}
	}

	void notifyPrintTestUnitPlan(TestUnitObjectWrapper unit) {
		for (TestProgress listener : listenerList) {
			listener.printTestUnitPlan(unit);
		}
	}
	
	void notifyPrintTestUnitException(Throwable e) {
		for (TestProgress listener : listenerList) {
			listener.unitException(e);
		}
	}

}
