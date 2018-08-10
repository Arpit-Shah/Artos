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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.artos.framework.Enums.TestStatus;
import com.artos.framework.FWStaticStore;
import com.artos.framework.GUITestSelector;
import com.artos.framework.ScanTestSuite;
import com.artos.framework.TestObjectWrapper;
import com.artos.framework.TestScriptParser;
import com.artos.framework.listener.TestExecutionEventListener;
import com.artos.interfaces.PrePostRunnable;
import com.artos.interfaces.TestExecutable;
import com.artos.interfaces.TestExecutionListener;
import com.artos.interfaces.TestRunnable;
import com.artos.utils.Transform;
import com.artos.utils.UtilsFramework;

/**
 * This class is responsible for running test cases. It initialising logger and
 * context with provided information. It is also responsible for running test
 * cases in given sequence (including pre/post methods)
 */
public class Runner {

	Class<? extends PrePostRunnable> cls;
	TestContext context;
	List<TestExecutionListener> listenerList = new ArrayList<TestExecutionListener>();

	// ==================================================================================
	// Constructor (Starting point of framework)
	// ==================================================================================

	/**
	 * This method initialises context with information collected from
	 * {@code FrameworkConfig}
	 * 
	 * @param cls
	 *            Test class with main method
	 */
	public Runner(Class<? extends PrePostRunnable> cls) {
		this.cls = cls;
		this.context = new TestContext();

		// Get Info from XML Configuration file
		String subDirName = context.getFrameworkConfig().getLogSubDir();
		String logDir = context.getFrameworkConfig().getLogRootDir();
		if (!subDirName.trim().equals("")) {
			logDir = logDir + subDirName;
		}
		boolean enableLogDecoration = context.getFrameworkConfig().isEnableLogDecoration();
		boolean enableTextLog = context.getFrameworkConfig().isEnableTextLog();
		boolean enableHTMLLog = context.getFrameworkConfig().isEnableHTMLLog();

		initialise(context, logDir, enableLogDecoration, enableTextLog, enableHTMLLog);
	}

	/**
	 * This method initialise context with user provided information. This will
	 * override information set in {@code FrameworkConfig}
	 * 
	 * @param cls
	 *            Class with main method
	 * @param logDir
	 *            Log base directory
	 * @param enableLogDecoration
	 *            enable|disable log decoration
	 * @param enableTextLog
	 *            enable|disable text log
	 * @param enableHTMLLog
	 *            enable|disable html log
	 */
	public Runner(Class<? extends PrePostRunnable> cls, String logDir, boolean enableLogDecoration, boolean enableTextLog, boolean enableHTMLLog) {
		this.cls = cls;
		this.context = new TestContext();
		initialise(context, logDir, enableLogDecoration, enableTextLog, enableHTMLLog);
	}

	private void initialise(TestContext context, String logDirPath, boolean enableLogDecoration, boolean enableTextLog, boolean enableHTMLLog) {
		// get Test case FQCN
		String strTestFQCN = cls.getPackage().getName();

		// Create Logger
		LogWrapper logWrapper = new LogWrapper(context, logDirPath, strTestFQCN, enableLogDecoration, enableTextLog, enableHTMLLog);

		// Add logger to context
		context.setOrganisedLogger(logWrapper);

		// Register default listener
		TestExecutionEventListener testListener = new TestExecutionEventListener(context);
		registerListener(testListener);
	}

	// ==================================================================================
	// Runner Method
	// ==================================================================================

	/**
	 * Runner for framework
	 * 
	 * @param args
	 *            Command line arguments
	 * @param testList
	 *            List of tests to run. All test must be {@code TestExecutable} type
	 * @param loopCycle
	 *            Test loop execution count
	 * @throws Exception
	 *             Exception will be thrown if test execution failed
	 */
	public void run(String[] args, List<TestExecutable> testList, int loopCycle) throws Exception {
		// Only process command line argument if provided
		CliProcessor.proessCommandLine(context, args);

		// Transform TestList into TestObjectWrapper Object list
		List<TestObjectWrapper> transformedTestList = transformToTestObjWrapper(cls, testList);
		if (context.getFrameworkConfig().isGenerateTestScript()) {
			new TestScriptParser().createExecScriptFromObjWrapper(transformedTestList);
		}

		if (context.getFrameworkConfig().isEnableGUITestSelector()) {
			TestRunnable runObj = new TestRunnable() {
				@Override
				public void executeTest(List<TestObjectWrapper> transformedTestList, Class<?> cls, int loopCount) throws Exception {
					runTest(transformedTestList, cls, loopCount);
				}
			};
			new GUITestSelector((List<TestObjectWrapper>) transformedTestList, cls, loopCycle, runObj);
		} else {
			runTest(transformedTestList, cls, loopCycle);
		}
	}

	/**
	 * This method executes test cases
	 * 
	 * @param transformedTestList
	 *            test object list
	 * @param cls
	 *            class object which is executing test
	 * @param loopCycle
	 *            test loop cycle
	 * @throws Exception
	 */
	private void runTest(List<TestObjectWrapper> transformedTestList, Class<?> cls, int loopCycle) throws Exception {

		LogWrapper logger = context.getLogger();

		// TODO : Parallel running test case can not work with current
		// architecture so should not enable this feature until solution is
		// found
		boolean enableParallelTestRunning = false;
		if (enableParallelTestRunning) {
			runParallelThread(transformedTestList, cls, loopCycle, context);
		} else {
			runSingleThread(transformedTestList, cls, loopCycle, context);
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

		System.exit((int) context.getCurrentFailCount());
	}

	private void runSingleThread(List<TestObjectWrapper> testList, Class<?> cls, int loopCycle, TestContext context)
			throws InstantiationException, IllegalAccessException, Exception {
		// ********************************************************************************************
		// TestSuite Start
		// ********************************************************************************************
		notifyTestSuiteExecutionStarted(cls.getName());
		context.setTestSuiteStartTime(System.currentTimeMillis());

		// Create an instance of Main class
		PrePostRunnable prePostCycleInstance = (PrePostRunnable) cls.newInstance();

		// Run prior to each test suit
		prePostCycleInstance.beforeTestsuite(context);
		for (int index = 0; index < loopCycle; index++) {
			notifyTestExecutionLoopCount(index);
			// --------------------------------------------------------------------------------------------
			for (TestObjectWrapper t : testList) {
				// Skip execution of the test if marked skip
				if (t.isSkipTest()) {
					notifyTestExecutionSkipped(t);
					continue;
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

		// Set Test Finish Time
		context.setTestSuiteFinishTime(System.currentTimeMillis());
		notifyTestSuiteExecutionFinished(cls.getName());
		// ********************************************************************************************
		// TestSuite Finish
		// ********************************************************************************************
	}

	/**
	 * This method executes the test case and handles all the exception and set the
	 * test status correctly
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
	private void runParallelThread(List<TestObjectWrapper> testList, Class<?> cls, int loopCycle, TestContext context)
			throws InstantiationException, IllegalAccessException, Exception {
		// ********************************************************************************************
		// Test Start
		// ********************************************************************************************
		notifyTestSuiteExecutionStarted(cls.getName());
		context.setTestSuiteStartTime(System.currentTimeMillis());

		// Create an instance of Main class
		PrePostRunnable prePostCycleInstance = (PrePostRunnable) cls.newInstance();

		// Run prior to each test suit
		prePostCycleInstance.beforeTestsuite(context);
		for (int index = 0; index < loopCycle; index++) {
			notifyTestExecutionLoopCount(index);
			// --------------------------------------------------------------------------------------------
			ExecutorService service = Executors.newFixedThreadPool(1000);
			List<Future<Runnable>> futures = new ArrayList<>();

			for (TestObjectWrapper t : testList) {
				// If user dictate test list then do not skip even if marked skip
				// if (t.isSkipTest()) {
				// notifyTestExecutionSkipped(t);
				// continue;
				// }

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

	public void registerListener(TestExecutionListener listener) {
		getListenerList().add(listener);
	}

	public void deRegisterListener(TestExecutionListener listener) {
		getListenerList().remove(listener);
	}

	public void deRegisterAllListener() {
		getListenerList().clear();
	}

	void notifyTestSuiteExecutionStarted(String testSuiteName) {
		for (TestExecutionListener listener : getListenerList()) {
			listener.testSuiteExecutionStarted(testSuiteName);
		}
	}

	void notifyTestSuiteExecutionFinished(String testSuiteName) {
		for (TestExecutionListener listener : getListenerList()) {
			listener.testSuiteExecutionFinished(testSuiteName);
		}
	}

	void notifyTestExecutionStarted(TestObjectWrapper t) {
		for (TestExecutionListener listener : getListenerList()) {
			listener.testExecutionStarted(t);
		}
	}

	void notifyTestExecutionFinished(TestObjectWrapper t) {
		for (TestExecutionListener listener : getListenerList()) {
			listener.testExecutionFinished(t);
		}
	}

	void notifyTestExecutionSkipped(TestObjectWrapper t) {
		for (TestExecutionListener listener : getListenerList()) {
			listener.testExecutionSkipped(t);
		}
	}

	void notifyTestExecutionLoopCount(int count) {
		for (TestExecutionListener listener : getListenerList()) {
			listener.testExecutionLoopCount(count);
		}
	}

	// ==================================================================================
	// Facade for arranging test cases
	// ==================================================================================
	/**
	 * This method transforms given test list of{@code TestEecutable} type into
	 * {@code TestObjectWrapper} type list. This method will only consider test This
	 * method can not transform test cases outside current package, so those test
	 * cases will be omitted from the list
	 * 
	 * @param cls
	 *            class with main method
	 * @param listOfTestCases
	 *            list of test cases required to be transformed
	 * @return Test list formatted into {@code TestObjectWrapper} type
	 */
	public List<TestObjectWrapper> transformToTestObjWrapper(Class<?> cls, List<TestExecutable> listOfTestCases) {

		Object testScriptObject;
		List<TestObjectWrapper> listOfTransformedTestCases = new ArrayList<>();
		String packageName = cls.getName().substring(0, cls.getName().lastIndexOf("."));
		ScanTestSuite reflection = new ScanTestSuite(packageName);

		/*
		 * @formatter:off
		 * 1. If XML testScript is provided then use it
		 * 2. If user has provided testList using main() method then use it
		 * 3. If all of the above is not provided then use reflection to find test cases
		 * @formatter:on
		 */

		if (null != (testScriptObject = context.getGlobalObject(FWStaticStore.GLOBAL_TEST_SCRIPT_PATH))) {

			File testScriptFile = (File) testScriptObject;
			List<String> testNameList = new TestScriptParser().readTestScript(testScriptFile);

			// If user provides test list then find list object from Hashmap
			// Get all test case information and store it for later use
			Map<String, TestObjectWrapper> testCaseMap = reflection.getTestObjWrapperMap(false);
			context.setGlobalObject(FWStaticStore.GLOBAL_ANNOTATED_TEST_MAP, testCaseMap);

			for (String t : testNameList) {
				TestObjectWrapper testObjWrapper = testCaseMap.get(t);

				if (null == testObjWrapper) {
					System.err.println("WARNING (not found): " + t);
				} else {
					listOfTransformedTestCases.add(testObjWrapper);
				}
			}

		} else if (null != listOfTestCases && !listOfTestCases.isEmpty()) {

			// If user provides test list then find list object from Hashmap
			// Get all test case information and store it for later use
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

	public List<TestExecutionListener> getListenerList() {
		return listenerList;
	}

	public void setListenerList(List<TestExecutionListener> listenerList) {
		this.listenerList = listenerList;
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