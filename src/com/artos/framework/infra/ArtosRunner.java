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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.artos.framework.Enums.Importance;
import com.artos.framework.Enums.ScriptFileType;
import com.artos.framework.Enums.TestStatus;
import com.artos.framework.FWStaticStore;
import com.artos.framework.listener.ExtentReportListener;
import com.artos.framework.listener.TestExecutionEventListener;
import com.artos.framework.listener.UDPReportListener;
import com.artos.framework.parser.TestScriptParser;
import com.artos.interfaces.TestExecutable;
import com.artos.interfaces.TestProgress;
import com.artos.interfaces.TestRunnable;
import com.artos.utils.Transform;
import com.artos.utils.UtilsFramework;

/**
 * This class is responsible for running test cases in user defined manner
 */
public class ArtosRunner {

	TestContext context;
	List<TestProgress> listenerList = new ArrayList<TestProgress>();
	List<Class<?>> externalListnerClassList = null;
	// ==================================================================================
	// Constructor (Starting point of framework)
	// ==================================================================================

	/**
	 * <PRE>
	 * Constructor responsible for initialising and registering required listeners. 
	 * TestExecutionEventListener is responsible for printing information during test execution
	 * ExtentReportListener is responsible for Extent report generation
	 * </PRE>
	 * 
	 * @param context TestContext object
	 * @param externalListnerClassList external listener class list
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @see TestContext
	 * @see TestExecutionEventListener
	 * @see ExtentReportListener
	 */
	protected ArtosRunner(TestContext context, List<Class<?>> externalListnerClassList) throws InstantiationException, IllegalAccessException {
		this.context = context;
		this.externalListnerClassList = externalListnerClassList;

		// Register default listener
		TestExecutionEventListener testListener = new TestExecutionEventListener(context);
		registerListener(testListener);
		context.registerListener(testListener);

		// Register extent reporting listener
		if (FWStaticStore.frameworkConfig.isEnableExtentReport()) {
			ExtentReportListener extentListener = new ExtentReportListener(context);
			registerListener(extentListener);
			context.registerListener(extentListener);
		}

		// Register UDPReport listener
		if (FWStaticStore.frameworkConfig.isEnableDashBoard()) {
			UDPReportListener udpListener = new UDPReportListener(context);
			registerListener(udpListener);
			context.registerListener(udpListener);
		}

		// Register external listener
		if (null != externalListnerClassList) {
			for (Class<?> listener : externalListnerClassList) {
				TestProgress externalListener = (TestProgress) listener.newInstance();
				registerListener(externalListener);
				context.registerListener(externalListener);
			}
		}

	}

	// ==================================================================================
	// Runner Method
	// ==================================================================================

	/**
	 * Runner for the framework. Responsible for generating test list after scanning a test suite, generate test script if required, show GUI test
	 * selector if enabled
	 * 
	 * @throws Exception Exception will be thrown if test execution failed
	 */
	protected void run() throws Exception {
		// Transform TestList into TestObjectWrapper Object list
		List<TestObjectWrapper> transformedTestList = new TransformToTestObjectWrapper(context).getListOfTransformedTestCases();
		if (FWStaticStore.frameworkConfig.isGenerateTestScript()) {
			new TestScriptParser().createExecScriptFromObjWrapper(transformedTestList, ScriptFileType.TEST_SCRIPT);
		}

		// If GUI test selector is enabled then show it or else execute test cases
		if (FWStaticStore.frameworkConfig.isEnableGUITestSelector()) {
			TestRunnable runObj = new TestRunnable() {
				@Override
				public void executeTest(TestContext context, List<TestObjectWrapper> transformedTestList) throws Exception {
					runTest(transformedTestList);
				}
			};
			new GUITestSelector(context, (List<TestObjectWrapper>) transformedTestList, runObj);
		} else {
			runTest(transformedTestList);
		}
	}

	/**
	 * This method executes test cases
	 * 
	 * @param transformedTestList test object list
	 * @throws Exception
	 */
	private void runTest(List<TestObjectWrapper> transformedTestList) throws Exception {

		LogWrapper logger = context.getLogger();

		// TODO : Parallel running test case can not work with current
		// architecture so should not enable this feature until solution is
		// found
		boolean enableParallelTestRunning = false;
		if (enableParallelTestRunning) {
			runParallelThread(transformedTestList, context);
		} else {
			runSingleThread(transformedTestList, context);
		}

		// Print Test results
		StringBuilder sb = new StringBuilder();
		// sb.append("\n");
		sb.append(FWStaticStore.ARTOS_LINE_BREAK_1);
		sb.append("\n");
		sb.append("[TestCases] ");
		sb.append("EXECUTED:" + String.format("%-" + 4 + "s", context.getTotalTestCount()));
		sb.append(" PASS:" + String.format("%-" + 4 + "s", context.getCurrentPassCount()));
		sb.append(" SKIP:" + String.format("%-" + 4 + "s", context.getCurrentSkipCount()));
		sb.append(" KTF:" + String.format("%-" + 4 + "s", context.getCurrentKTFCount()));
		sb.append(" FAIL:" + String.format("%-" + 4 + "s", context.getCurrentFailCount()));
		// Total does not make sense because parameterised test cases are considered as a test case
		// sb.append(" TOTAL:" + transformedTestList.size());
		sb.append(" [");
		sb.append("FATAL:" + String.format("%-" + 4 + "s", context.getTotalFatalCount()));
		sb.append(" CRITICAL:" + String.format("%-" + 4 + "s", context.getTotalCriticalCount()));
		sb.append(" HIGH:" + String.format("%-" + 4 + "s", context.getTotalHighCount()));
		sb.append(" MEDIUM:" + String.format("%-" + 4 + "s", context.getTotalMediumCount()));
		sb.append(" LOW:" + String.format("%-" + 4 + "s", context.getTotalLowCount()));
		sb.append(" UNDEFINED:" + String.format("%-" + 4 + "s", context.getTotalUndefinedCount()));
		sb.append("]");

		PrintTotalUnitResult(transformedTestList, sb);

		// Print Test suite Start and Finish time
		String startTimeStamp = new Transform().MilliSecondsToFormattedDate("dd-MM-yyyy hh:mm:ss", context.getTestSuiteStartTime());
		String finishTimeStamp = new Transform().MilliSecondsToFormattedDate("dd-MM-yyyy hh:mm:ss", context.getTestSuiteFinishTime());
		sb.append("\n\n");
		sb.append("Test start time : " + startTimeStamp);
		sb.append("\n");
		sb.append("Test finish time : " + finishTimeStamp);
		sb.append("\n");
		sb.append("Test duration : " + String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes(context.getTestSuiteTimeDuration()),
				TimeUnit.MILLISECONDS.toSeconds(context.getTestSuiteTimeDuration())
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(context.getTestSuiteTimeDuration()))));

		// Print Test suite summary
		logger.info(sb.toString());
		context.getLogger().getSummaryLogger().info(sb.toString());
		notifyTestSuiteSummaryPrinting(sb.toString());

		// HighLight Failed Test Cases
		List<TestObjectWrapper> failedTestList = highlightFailure(transformedTestList);

		if (FWStaticStore.frameworkConfig.isGenerateTestScript()) {
			// Create Script file for failed test cases
			new TestScriptParser().createExecScriptFromObjWrapper(failedTestList, ScriptFileType.ERROR_SCRIPT);
		}

		// to release a thread lock
		context.getThreadLatch().countDown();
	}

	private void PrintTotalUnitResult(List<TestObjectWrapper> transformedTestList, StringBuilder sb) {
		int totalUnitPassCount = 0;
		int totalUnitFailCount = 0;
		int totalSkipCount = 0;
		int totalKTFCount = 0;
		int totalExecuted = 0;

		int totalFatal = 0;
		int totalCritical = 0;
		int totalHigh = 0;
		int totalMedium = 0;
		int totalLow = 0;
		int totalUndefined = 0;
		for (TestObjectWrapper t : transformedTestList) {
			for (TestUnitObjectWrapper unit : t.getTestUnitList()) {
				if (unit.getTestUnitOutcomeList().isEmpty()) {
					continue;
				}
				for (int j = 0; j < unit.getTestUnitOutcomeList().size(); j++) {
					totalExecuted++;
					if (unit.getTestUnitOutcomeList().get(j) == TestStatus.PASS) {
						totalUnitPassCount++;
					} else if (unit.getTestUnitOutcomeList().get(j) == TestStatus.FAIL) {
						totalUnitFailCount++;

						if (unit.getTestImportance() == Importance.FATAL) {
							totalFatal++;
						} else if (unit.getTestImportance() == Importance.CRITICAL) {
							totalCritical++;
						} else if (unit.getTestImportance() == Importance.HIGH) {
							totalHigh++;
						} else if (unit.getTestImportance() == Importance.MEDIUM) {
							totalMedium++;
						} else if (unit.getTestImportance() == Importance.LOW) {
							totalLow++;
						} else if (unit.getTestImportance() == Importance.UNDEFINED) {
							totalUndefined++;
						}
					} else if (unit.getTestUnitOutcomeList().get(j) == TestStatus.SKIP) {
						totalSkipCount++;
					} else if (unit.getTestUnitOutcomeList().get(j) == TestStatus.KTF) {
						totalKTFCount++;
					}

				}
			}
		}

		// Print Test results
		sb.append("\n");
		sb.append("[TestUnits] ");
		sb.append("EXECUTED:" + String.format("%-" + 4 + "s", totalExecuted));
		sb.append(" PASS:" + String.format("%-" + 4 + "s", totalUnitPassCount));
		sb.append(" SKIP:" + String.format("%-" + 4 + "s", totalSkipCount));
		sb.append(" KTF:" + String.format("%-" + 4 + "s", totalKTFCount));
		sb.append(" FAIL:" + String.format("%-" + 4 + "s", totalUnitFailCount));
		sb.append(" [");
		sb.append("FATAL:" + String.format("%-" + 4 + "s", totalFatal));
		sb.append(" CRITICAL:" + String.format("%-" + 4 + "s", totalCritical));
		sb.append(" HIGH:" + String.format("%-" + 4 + "s", totalHigh));
		sb.append(" MEDIUM:" + String.format("%-" + 4 + "s", totalMedium));
		sb.append(" LOW:" + String.format("%-" + 4 + "s", totalLow));
		sb.append(" UNDEFINED:" + String.format("%-" + 4 + "s", totalUndefined));
		sb.append("]");
	}

	/**
	 * Highlight failed test cases at the end of test execution
	 * 
	 * @param transformedTestList list of test cases
	 * @return list of failed test cases
	 */
	private List<TestObjectWrapper> highlightFailure(List<TestObjectWrapper> transformedTestList) {

		List<TestObjectWrapper> failedTestList = new ArrayList<>();
		if (context.getCurrentFailCount() > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append(FWStaticStore.ARTOS_LINE_BREAK_1);
			sb.append("\n");
			sb.append("                 FAILED TEST CASES (" + context.getCurrentFailCount() + ")");
			sb.append("\n\n");
			sb.append(FWStaticStore.ARTOS_LINE_BREAK_1);

			int testErrorcount = 0;
			for (TestObjectWrapper t : transformedTestList) {

				/*
				 * If stopOnFail=true then test cases after first failure will not be executed which means TestOutcomeList will be empty
				 */
				if (t.getTestOutcomeList().isEmpty()) {
					continue;
				}

				if (t.getTestOutcomeList().get(0) == TestStatus.FAIL) {
					failedTestList.add(t);

					testErrorcount++;
					sb.append("\n");
					sb.append(String.format("%-4s%s", testErrorcount, t.getTestClassObject().getName()));
					sb.append(t.getTestImportance() == Importance.UNDEFINED ? "" : " [" + t.getTestImportance().name() + "]");

					for (TestUnitObjectWrapper unit : t.getTestUnitList()) {
						/*
						 * If stopOnFail=true then test unit after first failure will not be executed which means TestUnitOutcomeList will be empty
						 */
						if (unit.getTestUnitOutcomeList().isEmpty()) {
							continue;
						}

						// If test case is without date provider
						if ("".equals(unit.getDataProviderName()) && unit.getTestUnitOutcomeList().get(0) == TestStatus.FAIL) {
							sb.append(String.format("\n"));
							sb.append(String.format("\t|-- %s", unit.getTestUnitMethod().getName() + "(context)"));
							sb.append(unit.getTestImportance() == Importance.UNDEFINED ? "" : " [" + unit.getTestImportance().name() + "]");

							// If test case with data provider then go through each status of the list
						} else if (!"".equals(unit.getDataProviderName())) {
							for (int j = 0; j < unit.getTestUnitOutcomeList().size(); j++) {
								if (unit.getTestUnitOutcomeList().get(j) == TestStatus.FAIL) {
									sb.append(String.format("\n"));
									sb.append(String.format("\t|-- %s",
											unit.getTestUnitMethod().getName() + "(context)" + " : DataProvider[" + j + "]"));
									sb.append(unit.getTestImportance() == Importance.UNDEFINED ? "" : " [" + unit.getTestImportance().name() + "]");
								}
							}
						}

					}
				}
			}

			sb.append(String.format("\n"));
			sb.append(String.format(FWStaticStore.ARTOS_LINE_BREAK_1));
			System.err.println(sb.toString());
			notifyTestSuiteFailureHighlight(sb.toString());
		}

		return failedTestList;
	}

	private void runSingleThread(List<TestObjectWrapper> testList, TestContext context)
			throws InstantiationException, IllegalAccessException, Exception {
		// ********************************************************************************************
		// TestSuite Start
		// ********************************************************************************************
		notifyTestSuiteExecutionStarted(context.getPrePostRunnableObj().getName());
		context.setTestSuiteStartTime(System.currentTimeMillis());
		ScanTestSuite scan = new ScanTestSuite();
		scan.scanForBeforeAfterMethods(context);

		try {

			// Run prior to each test suite
			if (null != context.getBeforeTestSuite()) {
				notifyBeforeTestSuiteMethodExecutionStarted(context.getBeforeTestSuite().getName(), context.getPrePostRunnableObj().getName());
				context.getBeforeTestSuite().invoke(context.getPrePostRunnableObj().newInstance(), context);
				notifyBeforeTestSuiteMethodExecutionFinished(context.getPrePostRunnableObj().getName());
			}

			int loopCount = context.getTestSuite().getLoopCount();

			// Run as many loop set via test script or main method
			for (int index = 0; index < loopCount; index++) {
				notifyTestExecutionLoopCount(index);
				// --------------------------------------------------------------------------------------------

				if (testList.isEmpty()) {
					System.err.println("[WARNING] : Test case execution list is empty");
					System.err.println("[HINT-01] : Test case class may not be meeting criteria");
					System.err.println("[HINT-02] : Test cases may not be within the same package as TestRunner");
					System.err.println("[HINT-03] : Test case(s) are filtered out due to group filtering");
				}

				// Go through each test case and execute it
				for (TestObjectWrapper t : testList) {

					// If "stop on fail" is enabled then stop test execution
					if (FWStaticStore.frameworkConfig.isStopOnFail()) {
						if (context.getCurrentFailCount() > 0) {
							context.getLogger().info(FWStaticStore.ARTOS_STOP_ON_FAIL_STAMP);
							break;
						}
					}

					// Print test case header and test plan in the log file
					notifyPrintTestPlan(t);

					// if data provider is not specified
					if (null == t.getDataProviderName() || "".equals(t.getDataProviderName())) {
						runIndividualTest(t);
					} else { // if data provider is specified
						runParameterizedTest(t);
					}

				}
				// --------------------------------------------------------------------------------------------
			}

			// Run at the end of each test suit
			if (null != context.getAfterTestSuite()) {
				notifyAfterTestSuiteMethodExecutionStarted(context.getAfterTestSuite().getName(), context.getPrePostRunnableObj().getName());
				context.getAfterTestSuite().invoke(context.getPrePostRunnableObj().newInstance(), context);
				notifyAfterTestSuiteMethodExecutionFinished(context.getPrePostRunnableObj().getName());
			}

		} catch (Throwable e) {
			// Catch InvocationTargetException and return cause
			if (null == e.getCause()) {
				// Handle if any exception in pre-post runnable
				UtilsFramework.writePrintStackTrace(context, e);
				notifyTestSuiteException(e.getMessage());
			} else {
				// Handle if any exception in pre-post runnable
				UtilsFramework.writePrintStackTrace(context, e.getCause());
				notifyTestSuiteException(e.getCause().getMessage());
			}
		}

		// Set Test Finish Time
		context.setTestSuiteFinishTime(System.currentTimeMillis());
		notifyTestSuiteExecutionFinished(context.getPrePostRunnableObj().getName());
		// ********************************************************************************************
		// TestSuite Finish
		// ********************************************************************************************
	}

	/**
	 * Responsible for execution individual test cases
	 * 
	 * @param t TestCase in format {@code TestObjectWrapper}
	 */
	private void runIndividualTest(TestObjectWrapper t) {

		try {
			// Run Pre Method prior to any test Execution
			if (null != context.getBeforeTest()) {
				notifyGlobalBeforeTestCaseMethodExecutionStarted(context.getBeforeTest().getName(), t);
				context.getBeforeTest().invoke(context.getPrePostRunnableObj().newInstance(), context);
				notifyGlobalBeforeTestCaseMethodExecutionFinished(t);
			}
		} catch (Throwable e) {
			if (e.getClass() == InvocationTargetException.class) {
				// Catch InvocationTargetException and return cause
				UtilsFramework.writePrintStackTrace(context, e.getCause());
			} else {
				UtilsFramework.writePrintStackTrace(context, e);
			}
		}

		// ********************************************************************************************
		// TestCase Start
		// ********************************************************************************************
		try {
			t.setTestStartTime(System.currentTimeMillis());

			// Set Default Known to fail information
			context.setKnownToFail(t.isKTF(), t.getBugTrackingNumber());

			// If test timeout is defined then monitor thread for timeout
			if (0 != t.getTestTimeout()) {
				runTestWithTimeout(t);
			} else {
				runSimpleTest(t);
			}

			postTestValidation(t);
		} catch (Throwable e) {
			processTestException(t, e);
			notifyTestException(e.getMessage());
		} finally {
			t.setTestFinishTime(System.currentTimeMillis());
		}
		// ********************************************************************************************
		// TestCase Finish
		// ********************************************************************************************

		try {
			// Run Post Method prior to any test Execution
			if (null != context.getAfterTest()) {
				notifyGlobalAfterTestCaseMethodExecutionStarted(context.getAfterTest().getName(), t);
				context.getAfterTest().invoke(context.getPrePostRunnableObj().newInstance(), context);
				notifyGlobalAfterTestCaseMethodExecutionFinished(t);
			}
		} catch (Throwable e) {
			if (e.getClass() == InvocationTargetException.class) {
				// Catch InvocationTargetException and return cause
				UtilsFramework.writePrintStackTrace(context, e.getCause());
			} else {
				UtilsFramework.writePrintStackTrace(context, e);
			}
		}

		// ********************************************************************************************
		// Generate Summary
		// ********************************************************************************************
		context.generateTestSummary(t);
	}

	/**
	 * Responsible for executing data provider method which upon successful execution returns an array of parameters. TestCase will be re-run using
	 * all parameters available in the parameter array. If data provider method returns empty array or null then test case will be executed only once
	 * with null arguments.
	 * 
	 * @param t TestCase in format {@code TestObjectWrapper}
	 */
	private void runParameterizedTest(TestObjectWrapper t) {
		Object[][] data;
		TestDataProvider dataProviderObj;

		try {
			// get dataProvider specified for this test case (data provider name is always
			// stored in upper case)
			dataProviderObj = context.getDataProviderMap().get(t.getDataProviderName().toUpperCase());

			// If specified data provider is not found in the list then throw exception
			// (Remember : Data provider name is case in-sensitive)
			if (null == dataProviderObj) {
				throw new InvalidObjectException("DataProvider not found (or private) : " + (t.getDataProviderName()));
			}

			// Handle it because this executes method
			try {
				if (dataProviderObj.isStaticMethod()) {
					data = (Object[][]) dataProviderObj.getMethod().invoke(null, context);
				} else {
					/* NonStatic data provider method needs an instance */
					data = (Object[][]) dataProviderObj.getMethod().invoke(dataProviderObj.getClassOfTheMethod().newInstance(), context);
				}
			} catch (InvocationTargetException e) {
				context.getLogger().info(FWStaticStore.ARTOS_DATAPROVIDER_FAIL_STAMP);
				// Catch InvocationTargetException and return cause
				if (null == e.getCause()) {
					throw e;
				} else {
					// Cast cause into Exception because Executor service can not handle throwable
					throw (Exception) e.getCause();
				}
			}

			// If data provider method returns null or empty object then execute test with
			// null parameter
			if (null == data || data.length == 0) {
				executeChildTest(t, new String[][] { {} }, 0);
			} else {
				for (int i = 0; i < data.length; i++) {
					executeChildTest(t, data, i);
				}
			}
		} catch (Exception e) {
			// Print Exception
			UtilsFramework.writePrintStackTrace(context, e);
			notifyTestSuiteException(e.getMessage());

			// Mark current test as fail due to exception during data provider processing
			context.setTestStatus(TestStatus.FAIL, e.getMessage());
			context.generateTestSummary(t);
		}
	}

	/**
	 * Responsible for execution of a test case.
	 * 
	 * @param t TestCase in format {@code TestObjectWrapper}
	 * @throws Exception Exception during test execution
	 */
	private void runSimpleTest(TestObjectWrapper t) throws Exception {
		// --------------------------------------------------------------------------------------------
		notifyTestCaseExecutionStarted(t);

		// Run Unit tests (This is if test suite have unit tests)
		new RunnerTestUnits(context, listenerList).runSingleThreadUnits(t);

		notifyTestCaseExecutionFinished(t);
		// --------------------------------------------------------------------------------------------
	}

	/**
	 * Responsible for executing test case with thread timeout. If test case execution is not finished within expected time then test will be
	 * considered failed.
	 * 
	 * @param t TestCase in format {@code TestObjectWrapper} object
	 * @throws Throwable Exception during test execution
	 */
	private void runTestWithTimeout(TestObjectWrapper t) throws Throwable {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Future<String> future = executor.submit(new Callable<String>() {
			@Override
			public String call() throws Exception {
				runSimpleTest(t);
				return "TEST CASE FINISHED WITHIN TIME";
			}
		});

		try {
			// System.out.println(future.get(t.getTestTimeout(), TimeUnit.MILLISECONDS));
			future.get(t.getTestTimeout(), TimeUnit.MILLISECONDS);
		} catch (TimeoutException e) {
			future.cancel(true);
			context.setTestStatus(TestStatus.FAIL, "TEST CASE TIMED OUT");
		} catch (ExecutionException e) {
			future.cancel(true);
			if (null == e.getCause()) {
				// If no cause is listed then throw parent exception
				throw e;
			} else {
				// If cause is listed then only throw cause
				throw e.getCause();
			}
		}
	}

	/**
	 * Responsible for execution of test cases (Considered as child test case) with given parameter. Parameterised object array index and value(s)
	 * class type(s) will be printed prior to test execution for user's benefit.
	 * 
	 * @param t TestCase in format {@code TestObjectWrapper}
	 * @param data Array of parameters
	 * @param arrayIndex Parameter array index
	 */
	private void executeChildTest(TestObjectWrapper t, Object[][] data, int arrayIndex) {
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

		notifyChildTestCaseExecutionStarted(t, userInfo);

		runIndividualTest(t);

		notifyChildTestCaseExecutionFinished(t);

		// ********************************************************************************************
		// Parameterised Child TestCase Finish
		// ********************************************************************************************
	}

	/**
	 * Responsible for post validation after test case execution is successfully completed. If expected throwable(s)/exception(s) are defined by user
	 * using {@code ExpectedException} and test case status is PASS or FAIL then test case should be marked failed for not throwing expected
	 * throwable/exception.
	 * 
	 * <PRE>
	 * If test status is marked as SKIP or KTF then do not fail test case based on ExpectedException conditions. 
	 * If test is marked as SKIP then user should have taken decision based on condition checking so test framework does not need to overrule decision.
	 * If test is marked as KTF then user already knows that this test scenario is known to fail, so forcefully failing will dilute the meaning of having known to fail status.
	 * </PRE>
	 * 
	 * @param t {@code TestObjectWrapper} object
	 */
	private void postTestValidation(TestObjectWrapper t) {
		if (context.getCurrentTestStatus() == TestStatus.PASS || context.getCurrentTestStatus() == TestStatus.FAIL) {
			if (null != t.getExpectedExceptionList() && !t.getExpectedExceptionList().isEmpty() && t.isEnforceException()) {
				// Exception annotation was specified but did not occur
				context.setTestStatus(TestStatus.FAIL, "Exception was specified but did not occur");
			}
		}
	}

	/**
	 * Responsible for processing throwable/exception thrown by test cases during execution time. If {@code ExpectedException} annotation defines
	 * expected throwable/exception and received throwable/exception does not match any of the defined throwable(s)/Exception(s) then test will be
	 * marked as FAIL.
	 * 
	 * @param t test case in format {@code TestObjectWrapper}
	 * @param e {@code Throwable} or {@code Exception}
	 */
	private void processTestException(TestObjectWrapper t, Throwable e) {
		// If user has not specified expected exception then fail the test
		if (null != t.getExpectedExceptionList() && !t.getExpectedExceptionList().isEmpty()) {

			boolean exceptionMatchFound = false;
			for (Class<? extends Throwable> exceptionClass : t.getExpectedExceptionList()) {
				if (e.getClass() == exceptionClass) {
					/* Exception matches as specified by user */
					context.setTestStatus(TestStatus.PASS, "Exception is as expected : " + e.getClass().getName() + " : " + e.getMessage());

					/* If regular expression then validate exception message with regular expression */
					/* If regular expression does not match then do string compare */
					if (null != t.getExceptionContains() && !"".equals(t.getExceptionContains())) {
						if (e.getMessage().contains(t.getExceptionContains())) {
							context.setTestStatus(TestStatus.PASS, "Exception message contains : " + t.getExceptionContains());
						} else if (e.getMessage().matches(t.getExceptionContains())) {
							context.setTestStatus(TestStatus.PASS, "Exception message matches regex : " + t.getExceptionContains());
						} else {
							context.setTestStatus(TestStatus.FAIL,
									"Exception message does not match : \nExpected : " + t.getExceptionContains() + "\nReceived : " + e.getMessage());
						}
					}

					exceptionMatchFound = true;
					break;
				}
			}
			if (!exceptionMatchFound) {
				String expectedExceptions = "";
				for (Class<? extends Throwable> exceptionClass : t.getExpectedExceptionList()) {
					expectedExceptions += exceptionClass.getName() + " ";
				}
				context.setTestStatus(TestStatus.FAIL,
						"Exception is not as expected : \nExpected : " + expectedExceptions + "\nReturned : " + e.getClass().getName());
				UtilsFramework.writePrintStackTrace(context, e);
			}
		} else {
			context.setTestStatus(TestStatus.FAIL, e.getMessage());
			UtilsFramework.writePrintStackTrace(context, e);
		}
	}

	@SuppressWarnings("unchecked")
	private void runParallelThread(List<TestObjectWrapper> testList, TestContext context)
			throws InstantiationException, IllegalAccessException, Exception {
		// ********************************************************************************************
		// Test Start
		// ********************************************************************************************
		notifyTestSuiteExecutionStarted(context.getPrePostRunnableObj().getName());
		context.setTestSuiteStartTime(System.currentTimeMillis());
		ScanTestSuite scan = new ScanTestSuite();
		scan.scanForBeforeAfterMethods(context);

		// Run prior to each test suite
		if (null != context.getBeforeTestSuite()) {
			notifyBeforeTestSuiteMethodExecutionStarted(context.getBeforeTestSuite().getName(), context.getPrePostRunnableObj().getName());
			context.getBeforeTestSuite().invoke(context.getPrePostRunnableObj().newInstance(), context);
			notifyBeforeTestSuiteMethodExecutionFinished(context.getPrePostRunnableObj().getName());
		}

		// get loop count from testSuite
		int loopCount = context.getTestSuite().getLoopCount();

		for (int index = 0; index < loopCount; index++) {
			notifyTestExecutionLoopCount(index);
			// --------------------------------------------------------------------------------------------
			ExecutorService service = Executors.newFixedThreadPool(1000);
			List<Future<Runnable>> futures = new ArrayList<>();

			for (TestObjectWrapper t : testList) {

				Future<?> f = service.submit(new runTestInParallel(context, t, scan));
				futures.add((Future<Runnable>) f);

			}

			// wait for all tasks to complete before continuing
			for (Future<Runnable> f : futures) {
				f.get();
			}

			// shut down the executor service so that this thread can exit
			service.shutdownNow();
			// --------------------------------------------------------------------------------------------
		}

		// Run at the end of each test suit
		if (null != context.getAfterTestSuite()) {
			notifyAfterTestSuiteMethodExecutionStarted(context.getAfterTestSuite().getName(), context.getPrePostRunnableObj().getName());
			context.getAfterTestSuite().invoke(context.getPrePostRunnableObj().newInstance(), context);
			notifyAfterTestSuiteMethodExecutionFinished(context.getPrePostRunnableObj().getName());
		}
		// ********************************************************************************************
		// Test Finish
		// ********************************************************************************************
	}

	// ==================================================================================
	// Register, deRegister and Notify Event Listeners
	// ==================================================================================

	protected void registerListener(TestProgress listener) {
		listenerList.add(listener);
	}

	protected void deRegisterListener(TestProgress listener) {
		listenerList.remove(listener);
	}

	protected void deRegisterAllListener() {
		listenerList.clear();
	}

	void notifyBeforeTestSuiteMethodExecutionStarted(String methodName, String testSuiteName) {
		for (TestProgress listener : listenerList) {
			listener.beforeTestSuiteMethodExecutionStarted(methodName, testSuiteName);
		}
	}

	void notifyBeforeTestSuiteMethodExecutionFinished(String testSuiteName) {
		for (TestProgress listener : listenerList) {
			listener.beforeTestSuiteMethodExecutionFinished(testSuiteName);
		}
	}

	void notifyAfterTestSuiteMethodExecutionStarted(String methodName, String testSuiteName) {
		for (TestProgress listener : listenerList) {
			listener.afterTestSuiteMethodExecutionStarted(methodName, testSuiteName);
		}
	}

	void notifyAfterTestSuiteMethodExecutionFinished(String testSuiteName) {
		for (TestProgress listener : listenerList) {
			listener.afterTestSuiteMethodExecutionFinished(testSuiteName);
		}
	}

	void notifyTestSuiteExecutionStarted(String testSuiteName) {
		for (TestProgress listener : listenerList) {
			listener.testSuiteExecutionStarted(testSuiteName);
		}
	}

	void notifyTestSuiteExecutionFinished(String testSuiteName) {
		for (TestProgress listener : listenerList) {
			listener.testSuiteExecutionFinished(testSuiteName);
		}
	}

	void notifyPrintTestPlan(TestObjectWrapper t) {
		for (TestProgress listener : listenerList) {
			listener.printTestPlan(t);
		}
	}

	void notifyGlobalBeforeTestCaseMethodExecutionStarted(String methodName, TestObjectWrapper t) {
		for (TestProgress listener : listenerList) {
			listener.globalBeforeTestCaseMethodExecutionStarted(methodName, t);
		}
	}

	void notifyGlobalBeforeTestCaseMethodExecutionFinished(TestObjectWrapper t) {
		for (TestProgress listener : listenerList) {
			listener.globalBeforeTestCaseMethodExecutionFinished(t);
		}
	}

	void notifyGlobalAfterTestCaseMethodExecutionStarted(String methodName, TestObjectWrapper t) {
		for (TestProgress listener : listenerList) {
			listener.globalAfterTestCaseMethodExecutionStarted(methodName, t);
		}
	}

	void notifyGlobalAfterTestCaseMethodExecutionFinished(TestObjectWrapper t) {
		for (TestProgress listener : listenerList) {
			listener.globalAfterTestCaseMethodExecutionFinished(t);
		}
	}

	void notifyTestCaseExecutionStarted(TestObjectWrapper t) {
		for (TestProgress listener : listenerList) {
			listener.testCaseExecutionStarted(t);
		}
	}

	void notifyTestCaseExecutionFinished(TestObjectWrapper t) {
		for (TestProgress listener : listenerList) {
			listener.testCaseExecutionFinished(t);
		}
	}

	void notifyChildTestCaseExecutionStarted(TestObjectWrapper t, String userInfo) {
		for (TestProgress listener : listenerList) {
			listener.childTestCaseExecutionStarted(t, userInfo);
		}
	}

	void notifyChildTestCaseExecutionFinished(TestObjectWrapper t) {
		for (TestProgress listener : listenerList) {
			listener.childTestCaseExecutionFinished(t);
		}
	}

	void notifyTestCaseExecutionSkipped(TestObjectWrapper t) {
		for (TestProgress listener : listenerList) {
			listener.testCaseExecutionSkipped(t);
		}
	}

	void notifyTestExecutionLoopCount(int count) {
		for (TestProgress listener : listenerList) {
			listener.testExecutionLoopCount(count);
		}
	}

	void notifyTestSuiteException(String description) {
		for (TestProgress listener : listenerList) {
			listener.testSuiteException(description);
		}
	}

	void notifyTestException(String description) {
		for (TestProgress listener : listenerList) {
			listener.testException(description);
		}
	}

	void notifyTestSuiteSummaryPrinting(String description) {
		for (TestProgress listener : listenerList) {
			listener.testSuiteSummaryPrinting(description);
		}
	}

	void notifyTestSuiteFailureHighlight(String description) {
		for (TestProgress listener : listenerList) {
			listener.testSuiteFailureHighlight(description);
		}
	}
}

class runTestInParallel implements Runnable {

	ScanTestSuite scan;
	TestContext context;
	TestObjectWrapper t;

	public runTestInParallel(TestContext context, TestObjectWrapper test, ScanTestSuite scan) {
		this.context = context;
		this.t = test;
		this.scan = scan;
	}

	@Override
	public void run() {
		try {
			// notifyTestExecutionStarted(t);
			// Run Pre Method prior to any test Execution
			context.getBeforeTest().invoke(context.getPrePostRunnableObj().newInstance(), context);

			runIndividualTest(t);

			// Run Post Method prior to any test Execution
			context.getAfterTestSuite().invoke(context.getPrePostRunnableObj().newInstance(), context);
			// notifyTestExecutionFinished(t);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void runIndividualTest(TestObjectWrapper t) {
		t.setTestStartTime(System.currentTimeMillis());
		try {
			// Set Default Known to fail information
			context.setKnownToFail(t.isKTF(), t.getBugTrackingNumber());

			// --------------------------------------------------------------------------------------------
			// get test objects new instance and cast it to TestExecutable type
			((TestExecutable) t.getTestClassObject().newInstance()).execute(context);
			// --------------------------------------------------------------------------------------------

		} catch (Exception e) {
			context.setTestStatus(TestStatus.FAIL, e.getMessage());
			UtilsFramework.writePrintStackTrace(context, e);
		} catch (Throwable ex) {
			context.setTestStatus(TestStatus.FAIL, ex.getMessage());
			UtilsFramework.writePrintStackTrace(context, ex);
		} finally {
			t.setTestFinishTime(System.currentTimeMillis());
			context.generateTestSummary(t);
		}
	}
}
