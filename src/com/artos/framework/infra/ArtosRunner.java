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
package com.artos.framework.infra;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
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
 * This class is responsible for running test cases. It initialising logger and
 * context with provided information. It is also responsible for running test
 * cases in given sequence (including pre/post methods)
 */
public class ArtosRunner {

	TestContext context;
	List<TestProgress> listenerList = new ArrayList<TestProgress>();

	// ==================================================================================
	// Constructor (Starting point of framework)
	// ==================================================================================

	/**
	 * Constructor responsible for storing TestContext and class which contains
	 * main() method. Upon initialisation TestExecutionEventListener is
	 * registered so test decoration can be printed.
	 * 
	 * @param context
	 *            TestContext object
	 * @see TestContext
	 * @see TestExecutionEventListener
	 */
	public ArtosRunner(TestContext context) {
		this.context = context;

		// Register default listener
		TestExecutionEventListener testListener = new TestExecutionEventListener(context);
		registerListener(testListener);
		context.registerListener(testListener);

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
	 * @param testList
	 *            List of tests to run. All test must be {@code TestExecutable}
	 *            type
	 * @throws Exception
	 *             Exception will be thrown if test execution failed
	 */
	public void run(List<TestExecutable> testList) throws Exception {
		if (FWStaticStore.frameworkConfig.isGenerateEclipseTemplate()) {
			// only create template file if not present already
			File targetFile = new File(FWStaticStore.TEMPLATE_BASE_DIR + File.separator + "template.xml");
			if (!targetFile.exists() || !targetFile.isFile()) {

				// create dir if not present
				File file = new File(FWStaticStore.TEMPLATE_BASE_DIR);
				if (!file.exists() || !file.isDirectory()) {
					file.mkdirs();
				}

				InputStream ins = getClass().getResourceAsStream("/com/artos/template/template.xml");
				byte[] buffer = new byte[ins.available()];
				ins.read(buffer);

				OutputStream outStream = new FileOutputStream(targetFile);
				outStream.write(buffer);
				outStream.flush();
				outStream.close();
				ins.close();
			}
		}
		if (FWStaticStore.frameworkConfig.isEnableExtentReport()) {
			// only create Extent config file if not present already
			File targetFile = new File(FWStaticStore.CONFIG_BASE_DIR + File.separator + "Extent_Config.xml");
			if (!targetFile.exists() || !targetFile.isFile()) {

				// create dir if not present
				File file = new File(FWStaticStore.CONFIG_BASE_DIR);
				if (!file.exists() || !file.isDirectory()) {
					file.mkdirs();
				}

				InputStream ins = getClass().getResourceAsStream("/com/artos/template/Extent_Config.xml");
				byte[] buffer = new byte[ins.available()];
				ins.read(buffer);

				OutputStream outStream = new FileOutputStream(targetFile);
				outStream.write(buffer);
				outStream.flush();
				outStream.close();
				ins.close();
			}
		}

		// Transform TestList into TestObjectWrapper Object list
		List<TestObjectWrapper> transformedTestList = transformToTestObjWrapper(testList);
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
	 * @param transformedTestList
	 *            test object list
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
		logger.info("PASS:" + context.getCurrentPassCount() + " FAIL:" + context.getCurrentFailCount() + " SKIP:" + context.getCurrentSkipCount()
				+ " KTF:" + context.getCurrentKTFCount() + " TOTAL:" + context.getTotalTestCount());

		// Print Test suite Start and Finish time
		String timeStamp = new Transform().MilliSecondsToFormattedDate("dd-MM-yyyy hh:mm:ss", context.getTestSuiteStartTime());
		context.getLogger().getGeneralLogger().info("\nTest start time : {}", timeStamp);
		context.getLogger().getSummaryLogger().info("\nTest start time : {}", timeStamp);
		timeStamp = new Transform().MilliSecondsToFormattedDate("dd-MM-yyyy hh:mm:ss", context.getTestSuiteFinishTime());
		context.getLogger().getGeneralLogger().info("Test finish time : {}", timeStamp);
		context.getLogger().getSummaryLogger().info("Test finish time : {}", timeStamp);

		// Print Test suite total duration
		logger.info("Test duration : " + String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes(context.getTestSuiteTimeDuration()),
				TimeUnit.MILLISECONDS.toSeconds(context.getTestSuiteTimeDuration())
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(context.getTestSuiteTimeDuration()))));
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
			prePostCycleInstance.beforeTestsuite(context);
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

					notifyTestExecutionStarted(t);
					// Run Pre Method prior to any test Execution
					prePostCycleInstance.beforeTest(context);

					runIndividualTest(t);

					// Run Post Method prior to any test Execution
					prePostCycleInstance.afterTest(context);
					notifyTestExecutionFinished(t);
				}
				// --------------------------------------------------------------------------------------------
			}
			// Run at the end of each test suit
			prePostCycleInstance.afterTestsuite(context);

		} catch (Throwable e) {
			// Handle if any exception in pre-post runnable
			e.printStackTrace();
			context.getLogger().error(e);
		}

		// Set Test Finish Time
		context.setTestSuiteFinishTime(System.currentTimeMillis());
		notifyTestSuiteExecutionFinished(context.getPrePostRunnableObj().getName());
		// ********************************************************************************************
		// TestSuite Finish
		// ********************************************************************************************
	}

	/**
	 * This method executes the test case and handles all the exception and set
	 * the test status correctly
	 * 
	 * @param t
	 *            TestCase {@code TestObjectWrapper}
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

		} catch (Exception e) {
			context.setTestStatus(TestStatus.FAIL);
			UtilsFramework.writePrintStackTrace(context, e);
		} catch (Throwable ex) {
			context.setTestStatus(TestStatus.FAIL);
			UtilsFramework.writePrintStackTrace(context, ex);
		} finally {
			long testFinishTime = System.currentTimeMillis();
			context.generateTestSummary(t.getTestClassObject().getName(), testStartTime, testFinishTime);
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
		prePostCycleInstance.beforeTestsuite(context);
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
		prePostCycleInstance.afterTestsuite(context);
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

	// ==================================================================================
	// Facade for arranging test cases
	// ==================================================================================
	/**
	 * This method transforms given test list of{@code TestEecutable} type into
	 * {@code TestObjectWrapper} type list. This method will only consider test
	 * This method can not transform test cases outside current package, so
	 * those test cases will be omitted from the list
	 * 
	 * @param listOfTestCases
	 *            list of test cases required to be transformed
	 * @return Test list formatted into {@code TestObjectWrapper} type
	 */
	public List<TestObjectWrapper> transformToTestObjWrapper(List<TestExecutable> listOfTestCases) {

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
			 * Get all test case object using reflection. If user provides XML
			 * based test script then skip attribute set in annotation should be
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

		} else if (null != listOfTestCases && !listOfTestCases.isEmpty()) {

			/**
			 * Get all test case object using reflection. If user provides test
			 * list then skip attribute set in annotation should be ignored
			 * because test list must dictates the behaviour.
			 */
			Map<String, TestObjectWrapper> testCaseMap = reflection.getTestObjWrapperMap(false);
			context.setGlobalObject(FWStaticStore.GLOBAL_ANNOTATED_TEST_MAP, testCaseMap);

			for (TestExecutable t : listOfTestCases) {
				TestObjectWrapper testObjWrapper = testCaseMap.get(t.getClass().getName());

				if (null == testObjWrapper) {
					System.err.println(t.getClass().getName() + " not present in given test suite");
				} else {
					listOfTransformedTestCases.add(testObjWrapper);
				}
			}

		} else {
			listOfTransformedTestCases = reflection.getTestObjWrapperList(true, true);
		}

		return listOfTransformedTestCases;
	}

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
			context.setTestStatus(TestStatus.FAIL);
			UtilsFramework.writePrintStackTrace(context, e);
		} catch (Throwable ex) {
			context.setTestStatus(TestStatus.FAIL);
			UtilsFramework.writePrintStackTrace(context, ex);
		} finally {
			long testFinishTime = System.currentTimeMillis();
			context.generateTestSummary(t.getTestClassObject().getName(), testStartTime, testFinishTime);
		}
	}
}