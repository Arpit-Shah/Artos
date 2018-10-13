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
package com.artos.framework.infra;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.artos.framework.Enums.TestStatus;
import com.artos.framework.FWStaticStore;
import com.artos.framework.GUITestSelector;
import com.artos.framework.ScanTestSuite;
import com.artos.framework.TestObjectWrapper;
import com.artos.framework.listener.ExtentReportListener;
import com.artos.framework.listener.TestExecutionEventListener;
import com.artos.framework.xml.TestScriptParser;
import com.artos.framework.xml.TestSuite;
import com.artos.interfaces.PrePostRunnable;
import com.artos.interfaces.TestExecutable;
import com.artos.interfaces.TestProgress;
import com.artos.interfaces.TestRunnable;
import com.artos.utils.Transform;
import com.artos.utils.UtilsFramework;

/**
 * This class is responsible for running test cases. It initialising logger and context with provided information. It is also responsible for running
 * test cases in given sequence (including pre/post methods)
 */
public class ArtosRunner {

	TestContext context;
	List<TestProgress> listenerList = new ArrayList<TestProgress>();

	// ==================================================================================
	// Constructor (Starting point of framework)
	// ==================================================================================

	/**
	 * Constructor responsible for storing TestContext and class which contains main() method. Upon initialisation TestExecutionEventListener is
	 * registered so test decoration can be printed.
	 * 
	 * @param context TestContext object
	 * @see TestContext
	 * @see TestExecutionEventListener
	 */
	public ArtosRunner(TestContext context) {
		this.context = context;

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

	}

	// ==================================================================================
	// Runner Method
	// ==================================================================================

	/**
	 * Runner for the framework
	 * 
	 * @param testList List of tests to run. All test must be {@code TestExecutable} type
	 * @param groupList Group list which is required for test case filtering
	 * @throws Exception Exception will be thrown if test execution failed
	 */
	public void run(List<TestExecutable> testList, List<String> groupList) throws Exception {
		// Transform TestList into TestObjectWrapper Object list
		List<TestObjectWrapper> transformedTestList = transformToTestObjWrapper(testList, groupList);
		if (FWStaticStore.frameworkConfig.isGenerateTestScript()) {
			new TestScriptParser().createExecScriptFromObjWrapper(transformedTestList);
		}

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
		notifyTestSuiteSummaryPrinting("");
		logger.info("PASS:" + context.getCurrentPassCount() + " FAIL:" + context.getCurrentFailCount() + " SKIP:" + context.getCurrentSkipCount()
				+ " KTF:" + context.getCurrentKTFCount() + " EXECUTED:" + context.getTotalTestCount() + " TOTAL:" + transformedTestList.size());

		// Print Test suite Start and Finish time
		String timeStamp = new Transform().MilliSecondsToFormattedDate("dd-MM-yyyy hh:mm:ss", context.getTestSuiteStartTime());
		context.getLogger().getGeneralLogger().info("\nTest start time : {}", timeStamp);
		context.getLogger().getSummaryLogger().info("\nTest start time : {}", timeStamp);
		timeStamp = new Transform().MilliSecondsToFormattedDate("dd-MM-yyyy hh:mm:ss", context.getTestSuiteFinishTime());
		context.getLogger().getGeneralLogger().info("Test finish time : {}", timeStamp);
		context.getLogger().getSummaryLogger().info("Test finish time : {}", timeStamp);

		// Print Test suite summary
		logger.info("Test duration : " + String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes(context.getTestSuiteTimeDuration()),
				TimeUnit.MILLISECONDS.toSeconds(context.getTestSuiteTimeDuration())
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(context.getTestSuiteTimeDuration()))));

		notifyTestSuiteFailureHighlight("");
		if (context.getCurrentFailCount() > 0) {
			System.err.println("********************************************************");
			System.err.println("                 FAILED TEST CASES (" + context.getCurrentFailCount() + ")");
			System.err.println("\n********************************************************");
			for (int i = 0; i < context.getFailedTestList().size(); i++) {
				System.err.println(String.format("%-4s%s", (i + 1), context.getFailedTestList().get(i)).replace(" ", "."));
			}
			System.err.println("********************************************************\n********************************************************");
		}

		// to release a thread lock
		context.getThreadLatch().countDown();
	}

	private void runSingleThread(List<TestObjectWrapper> testList, TestContext context)
			throws InstantiationException, IllegalAccessException, Exception {
		// ********************************************************************************************
		// TestSuite Start
		// ********************************************************************************************
		notifyTestSuiteExecutionStarted(context.getPrePostRunnableObj().getName());
		context.setTestSuiteStartTime(System.currentTimeMillis());

		try {

			// Create an instance of Main class
			PrePostRunnable prePostCycleInstance = (PrePostRunnable) context.getPrePostRunnableObj().newInstance();

			// Run prior to each test suit
			notifyBeforeTestSuiteMethodStarted(context.getPrePostRunnableObj().getName());
			prePostCycleInstance.beforeTestSuite(context);
			notifyBeforeTestSuiteMethodFinished(context.getPrePostRunnableObj().getName());

			for (int index = 0; index < context.getTotalLoopCount(); index++) {
				notifyTestExecutionLoopCount(index);
				// --------------------------------------------------------------------------------------------
				for (TestObjectWrapper t : testList) {

					// If stop on fail is selected then stop test execution
					if (FWStaticStore.frameworkConfig.isStopOnFail()) {
						if (context.getCurrentFailCount() > 0) {
							break;
						}
					}

					// Run Pre Method prior to any test Execution
					notifyBeforeTestMethodStarted(t);
					prePostCycleInstance.beforeTest(context);
					notifyBeforeTestMethodFinished(t);

					notifyTestExecutionStarted(t);
					runIndividualTest(t);
					notifyTestExecutionFinished(t);

					// Run Post Method prior to any test Execution
					notifyAfterTestMethodStarted(t);
					prePostCycleInstance.afterTest(context);
					notifyAfterTestMethodFinished(t);

				}
				// --------------------------------------------------------------------------------------------
			}

			// Run at the end of each test suit
			notifyAfterTestSuiteMethodStarted(context.getPrePostRunnableObj().getName());
			prePostCycleInstance.afterTestSuite(context);
			notifyAfterTestSuiteMethodFinished(context.getPrePostRunnableObj().getName());

		} catch (Throwable e) {
			// Handle if any exception in pre-post runnable
			e.printStackTrace();
			UtilsFramework.writePrintStackTrace(context, e);
			notifyTestSuiteException(e.getMessage());
		}

		// Set Test Finish Time
		context.setTestSuiteFinishTime(System.currentTimeMillis());
		notifyTestSuiteExecutionFinished(context.getPrePostRunnableObj().getName());
		// ********************************************************************************************
		// TestSuite Finish
		// ********************************************************************************************
	}

	/**
	 * This method executes the test case and handles all the exception and set the test status correctly
	 * 
	 * @param t TestCase {@code TestObjectWrapper}
	 */
	private void runIndividualTest(TestObjectWrapper t) {
		long testStartTime = System.currentTimeMillis();
		try {
			// Set Default Known to fail information
			context.setKnownToFail(t.isKTF(), t.getBugTrackingNumber());

			// --------------------------------------------------------------------------------------------
			// get test objects new instance and cast it to TestExecutable type
			((TestExecutable) t.getTestClassObject().newInstance()).execute(context);
			// --------------------------------------------------------------------------------------------

			postTestValidation(t);
		} catch (Throwable e) {
			processTestException(t, e);
			notifyTestException(e.getMessage());
		} finally {
			long testFinishTime = System.currentTimeMillis();
			context.generateTestSummary(t.getTestClassObject().getName(), testStartTime, testFinishTime);
		}
	}

	/**
	 * After test case execution, perform post validation and update test status accordingly.
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
	 * This function processes exception if test during run time throws {@code Throwable} or {@code Exception}. If {@code ExpectedException}
	 * annotation if specified then Exception will be validated using provided parameters. Otherwise test will be considered failed.
	 * 
	 * @param t {@code TestObjectWrapper} object
	 * @param e {@code Throwable} or {@code Exception}
	 */
	private void processTestException(TestObjectWrapper t, Throwable e) {
		// If user has not specified expected exception then fail the test
		if (t.getExpectedExceptionList() != null && !t.getExpectedExceptionList().isEmpty()) {

			boolean exceptionMatchFound = false;
			for (Class<? extends Throwable> exceptionClass : t.getExpectedExceptionList()) {
				if (e.getClass() == exceptionClass) {
					// Exception matches as specified by user
					context.setTestStatus(TestStatus.PASS, "Exception is as expected : " + e.getClass().getName() + " : " + e.getMessage());

					/*
					 * If User has provided regular expression then validate exception message with regular expression
					 */
					if (null != t.getExceptionContains() && !"".equals(t.getExceptionContains())) {
						if (e.getMessage().matches(t.getExceptionContains())) {
							context.setTestStatus(TestStatus.PASS, "Exception message matches regex : " + t.getExceptionContains());
						} else {
							context.setTestStatus(TestStatus.FAIL, "Exception message does not match regex : \nRegularExpression : "
									+ t.getExceptionContains() + "\nException Message : " + e.getMessage());
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

		// Create an instance of Main class
		PrePostRunnable prePostCycleInstance = (PrePostRunnable) context.getPrePostRunnableObj().newInstance();

		// Run prior to each test suit
		prePostCycleInstance.beforeTestSuite(context);
		for (int index = 0; index < context.getTotalLoopCount(); index++) {
			notifyTestExecutionLoopCount(index);
			// --------------------------------------------------------------------------------------------
			ExecutorService service = Executors.newFixedThreadPool(1000);
			List<Future<Runnable>> futures = new ArrayList<>();

			for (TestObjectWrapper t : testList) {

				Future<?> f = service.submit(new runTestInParallel(context, t, prePostCycleInstance));
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
		prePostCycleInstance.afterTestSuite(context);
		// ********************************************************************************************
		// Test Finish
		// ********************************************************************************************
	}

	// ==================================================================================
	// Register, deRegister and Notify Event Listeners
	// ==================================================================================

	public void registerListener(TestProgress listener) {
		listenerList.add(listener);
	}

	public void deRegisterListener(TestProgress listener) {
		listenerList.remove(listener);
	}

	public void deRegisterAllListener() {
		listenerList.clear();
	}

	void notifyBeforeTestSuiteMethodStarted(String testSuiteName) {
		for (TestProgress listener : listenerList) {
			listener.beforeTestSuiteMethodStarted(testSuiteName);
		}
	}

	void notifyBeforeTestSuiteMethodFinished(String testSuiteName) {
		for (TestProgress listener : listenerList) {
			listener.beforeTestSuiteMethodFinished(testSuiteName);
		}
	}

	void notifyAfterTestSuiteMethodStarted(String testSuiteName) {
		for (TestProgress listener : listenerList) {
			listener.afterTestSuiteMethodStarted(testSuiteName);
		}
	}

	void notifyAfterTestSuiteMethodFinished(String testSuiteName) {
		for (TestProgress listener : listenerList) {
			listener.afterTestSuiteMethodFinished(testSuiteName);
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

	void notifyBeforeTestMethodStarted(TestObjectWrapper t) {
		for (TestProgress listener : listenerList) {
			listener.beforeTestMethodStarted(t);
		}
	}

	void notifyBeforeTestMethodFinished(TestObjectWrapper t) {
		for (TestProgress listener : listenerList) {
			listener.beforeTestMethodFinished(t);
		}
	}

	void notifyAfterTestMethodStarted(TestObjectWrapper t) {
		for (TestProgress listener : listenerList) {
			listener.afterTestMethodStarted(t);
		}
	}

	void notifyAfterTestMethodFinished(TestObjectWrapper t) {
		for (TestProgress listener : listenerList) {
			listener.afterTestMethodFinished(t);
		}
	}

	void notifyTestExecutionStarted(TestObjectWrapper t) {
		for (TestProgress listener : listenerList) {
			listener.testExecutionStarted(t);
		}
	}

	void notifyTestExecutionFinished(TestObjectWrapper t) {
		for (TestProgress listener : listenerList) {
			listener.testExecutionFinished(t);
		}
	}

	void notifyTestExecutionSkipped(TestObjectWrapper t) {
		for (TestProgress listener : listenerList) {
			listener.testExecutionSkipped(t);
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

	// ==================================================================================
	// Facade for arranging test cases
	// ==================================================================================
	/**
	 * This method transforms given test list of{@code TestEecutable} type into {@code TestObjectWrapper} type list. This method will only consider
	 * test This method can not transform test cases outside current package, so those test cases will be omitted from the list
	 * 
	 * @param listOfTestCases list of test cases required to be transformed
	 * @param groupList user specified groupList
	 * @return Test list formatted into {@code TestObjectWrapper} type
	 */
	public List<TestObjectWrapper> transformToTestObjWrapper(List<TestExecutable> listOfTestCases, List<String> groupList) {

		if (null == groupList || groupList.isEmpty()) {
			new Exception("Group must be specified");
		}

		Object testSuiteObject;
		List<TestObjectWrapper> listOfTransformedTestCases = new ArrayList<>();

		// If main() method executes from root then package name will be none
		String packageName = "";
		if (null != context.getPrePostRunnableObj().getPackage()) {
			packageName = context.getPrePostRunnableObj().getPackage().getName();
		}
		ScanTestSuite reflection = new ScanTestSuite(packageName);

		/*
		 * @formatter:off
		 * 1. If XML testScript is provided then use it
		 * 2. If user has provided testList using main() method then use it
		 * 3. If all of the above is not provided then use reflection to find test cases
		 * @formatter:on
		 */
		if (null != (testSuiteObject = context.getTestSuite())) {

			/**
			 * Get all test case object using reflection. If user provides XML based test script then skip attribute set in annotation should be
			 * ignored because XML test script must dictates the behaviour.
			 */
			Map<String, TestObjectWrapper> testCaseMap = reflection.getTestObjWrapperMap(false);
			context.setGlobalObject(FWStaticStore.GLOBAL_ANNOTATED_TEST_MAP, testCaseMap);

			TestSuite suite = (TestSuite) testSuiteObject;

			// populate all global parameters to context
			Map<String, String> parameterMap = suite.getTestSuiteParameters();
			if (null != parameterMap && !parameterMap.isEmpty()) {
				for (Entry<String, String> entry : parameterMap.entrySet()) {
					context.setGlobalObject(entry.getKey(), entry.getValue());
				}
			}

			// Get list of all test name
			List<String> testNameList = suite.getTestFQCNList();

			// If test list is empty then assume user wants to run all test
			// cases
			if (testNameList.isEmpty()) {
				for (Map.Entry<String, TestObjectWrapper> entry : testCaseMap.entrySet()) {
					if (belongsToApprovedGroup(suite.getGroupList(), entry.getValue().getGroupList())) {
						listOfTransformedTestCases.add(entry.getValue());
					}
				}
			} else {
				// Create Test object list from test script
				for (String t : testNameList) {
					TestObjectWrapper testObjWrapper = testCaseMap.get(t);

					if (null == testObjWrapper) {
						System.err.println("WARNING (not found): " + t);
					} else {
						if (belongsToApprovedGroup(suite.getGroupList(), testObjWrapper.getGroupList())) {
							listOfTransformedTestCases.add(testObjWrapper);
						}
					}
				}
			}

		} else if (null != listOfTestCases && !listOfTestCases.isEmpty()) {

			/**
			 * Get all test case object using reflection. If user provides test list then skip attribute set in annotation should be ignored because
			 * test list must dictates the behaviour.
			 */
			Map<String, TestObjectWrapper> testCaseMap = reflection.getTestObjWrapperMap(false);
			context.setGlobalObject(FWStaticStore.GLOBAL_ANNOTATED_TEST_MAP, testCaseMap);

			for (TestExecutable t : listOfTestCases) {
				TestObjectWrapper testObjWrapper = testCaseMap.get(t.getClass().getName());

				if (null == testObjWrapper) {
					System.err.println(t.getClass().getName() + " not present in given test suite");
				} else {
					if (belongsToApprovedGroup(groupList, testObjWrapper.getGroupList())) {
						listOfTransformedTestCases.add(testObjWrapper);
					}
				}
			}

		} else {
			List<TestObjectWrapper> listOfTestObj = reflection.getTestObjWrapperList(true, true, true);
			for (TestObjectWrapper t : listOfTestObj) {
				if (belongsToApprovedGroup(groupList, t.getGroupList())) {
					listOfTransformedTestCases.add(t);
				}
			}

		}

		return listOfTransformedTestCases;
	}

	/**
	 * 
	 * @param refGroupList list of user defined group via test script or via main class
	 * @param testGroupList list of group test case belong to
	 * @return true if test case belongs to at least one of the user defined groups, false if test case does not belong to any user defined groups
	 */
	private boolean belongsToApprovedGroup(List<String> refGroupList, List<String> testGroupList) {
		for (String group : refGroupList) {
			if (testGroupList.contains(group)) {
				return true;
			}
		}
		return false;
	}
}

class runTestInParallel implements Runnable {

	PrePostRunnable prePostCycleInstance;
	TestContext context;
	TestObjectWrapper t;

	public runTestInParallel(TestContext context, TestObjectWrapper test, PrePostRunnable prePostCycleInstance) {
		this.context = context;
		this.t = test;
		this.prePostCycleInstance = prePostCycleInstance;
	}

	@Override
	public void run() {
		try {
			// notifyTestExecutionStarted(t);
			// Run Pre Method prior to any test Execution
			prePostCycleInstance.beforeTest(context);

			runIndividualTest(t);

			// Run Post Method prior to any test Execution
			prePostCycleInstance.afterTest(context);
			// notifyTestExecutionFinished(t);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void runIndividualTest(TestObjectWrapper t) {
		long testStartTime = System.currentTimeMillis();
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
			long testFinishTime = System.currentTimeMillis();
			context.generateTestSummary(t.getTestClassObject().getName(), testStartTime, testFinishTime);
		}
	}
}
