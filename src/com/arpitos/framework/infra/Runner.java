// Copyright <2018> <Arpitos>

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
package com.arpitos.framework.infra;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.arpitos.framework.Enums.TestStatus;
import com.arpitos.framework.FWStatic_Store;
import com.arpitos.framework.GUITestSelector;
import com.arpitos.framework.ScanTestSuite;
import com.arpitos.framework.TestObjectWrapper;
import com.arpitos.interfaces.PrePostRunnable;
import com.arpitos.interfaces.TestExecutable;
import com.arpitos.interfaces.TestRunnable;
import com.arpitos.utils.Convert;
import com.arpitos.utils.UtilsFramework;

/**
 * This class is responsible for running test cases. It initialising logger and
 * context with provided information. It is also responsible for running test
 * cases in given sequence (including pre/post methods)
 * 
 * @author ArpitS
 *
 */
public class Runner {

	Class<?> cls;
	TestContext context = FWStatic_Store.context;

	/**
	 * This method initialises context with information collected from
	 * {@code FrameworkConfig}
	 * 
	 * @param cls
	 *            Test class with main method
	 */
	public Runner(Class<?> cls) {
		this.cls = cls;

		// Get Info from XML Configuration file
		String subDirName = FWStatic_Store.context.getFrameworkConfig().getLogSubDir();
		String logDir = FWStatic_Store.context.getFrameworkConfig().getLogRootDir() + subDirName;
		boolean enableLogDecoration = FWStatic_Store.context.getFrameworkConfig().isEnableLogDecoration();
		boolean enableTextLog = FWStatic_Store.context.getFrameworkConfig().isEnableTextLog();
		boolean enableHTMLLog = FWStatic_Store.context.getFrameworkConfig().isEnableHTMLLog();

		initialise(logDir, subDirName, enableLogDecoration, enableTextLog, enableHTMLLog);
	}

	/**
	 * This method initialise context with user provided information. This will
	 * override information set in {@code FrameworkConfig}
	 * 
	 * @param cls
	 *            Test class with main method
	 */
	public Runner(Class<?> cls, String logDir, String subDirName, boolean enableLogDecoration, boolean enableTextLog, boolean enableHTMLLog) {
		this.cls = cls;
		initialise(logDir, subDirName, enableLogDecoration, enableTextLog, enableHTMLLog);
	}

	private void initialise(String logDir, String subDirName, boolean enableLogDecoration, boolean enableTextLog, boolean enableHTMLLog) {
		String strTestName = cls.getPackage().getName();

		// Create Logger
		LogWrapper logWrapper = new LogWrapper(logDir, strTestName, enableLogDecoration, enableTextLog, enableHTMLLog);

		// Add logger to context
		FWStatic_Store.context.setOrganisedLogger(logWrapper);
	}

	/**
	 * Run Method runs a test case
	 * 
	 * @param args
	 *            command line parameter
	 * @param testList
	 *            test object list which required to be run
	 * @param loopCycle
	 *            Number of time test list required to be executed
	 * @throws Exception
	 */
	public void run(String[] args, List<TestExecutable> testList, int loopCycle) throws Exception {
		// Only process command line argument if provided
		CliProcessor.proessCommandLine(args);

		if (FWStatic_Store.context.getFrameworkConfig().isEnableGUITestSelector()) {
			TestRunnable runObj = new TestRunnable() {
				@Override
				public void executeTest(ArrayList<TestExecutable> testList, Class<?> cls, int loopCount) throws Exception {
					runTest(testList, cls, loopCount);
				}
			};
			new GUITestSelector((ArrayList<TestExecutable>) testList, cls, loopCycle, runObj);
		} else {
			runTest(testList, cls, loopCycle);
		}
	}

	/**
	 * Returns All test case objects wrapped in TestWrapper
	 * 
	 * @param testList
	 *            TestList collected using reflaction
	 * @param cls
	 *            Test Class
	 * @throws Exception
	 */
	public void getTestWrapperList(List<TestExecutable> testList, Class<?> cls) throws Exception {
		if (testList.isEmpty()) {
			testList = (ArrayList<TestExecutable>) new ScanTestSuite(cls.getPackage().getName()).getTestList(true, true);
		}
	}

	/**
	 * This method executes test cases
	 * 
	 * @param testList
	 *            test object list
	 * @param cls
	 *            class object which is executing test
	 * @param loopCycle
	 *            test loop cycle
	 * @throws Exception
	 */
	private void runTest(List<TestExecutable> testList, Class<?> cls, int loopCycle) throws Exception {

		// -------------------------------------------------------------------//
		// Using reflection grep all test cases
		{
			String packageName = cls.getName().substring(0, cls.getName().lastIndexOf("."));
			ScanTestSuite reflection = new ScanTestSuite(packageName);
			// Get all test case information and store it for later use
			Map<String, TestObjectWrapper> testCaseMap = reflection.getTestObjWrapperMap(false);
			context.setGlobalObject(FWStatic_Store.GLOBAL_ANNOTATED_TEST_MAP, testCaseMap);
		}

		LogWrapper logger = context.getLogger();

		// TODO : Parallel running test case can not work with current
		// architecture so should not enable this feature until solution is
		// found
		boolean enableParallelTestRunning = false;
		if (enableParallelTestRunning) {
			runParallelThread(testList, cls, loopCycle, context);
		} else {
			runSingleThread(testList, cls, loopCycle, context);
		}

		// Print Test results
		logger.info("PASS:" + context.getCurrentPassCount() + " FAIL:" + context.getCurrentFailCount() + " SKIP:" + context.getCurrentSkipCount()
				+ " KTF:" + context.getCurrentKTFCount() + " TOTAL:" + context.getTotalTestCount());

		// Set Test Finish Time
		context.setTestSuiteFinishTime(System.currentTimeMillis());

		// Print Test suite Start and Finish time
		String timeStamp = new Convert().MilliSecondsToFormattedDate("dd-MM-yyyy hh:mm:ss", context.getTestSuiteStartTime());
		context.getLogger().getGeneralLogger().info("\nTest start time : " + timeStamp);
		context.getLogger().getSummaryLogger().info("\nTest start time : " + timeStamp);
		timeStamp = new Convert().MilliSecondsToFormattedDate("dd-MM-yyyy hh:mm:ss", context.getTestSuiteFinishTime());
		context.getLogger().getGeneralLogger().info("Test finish time : " + timeStamp);
		context.getLogger().getSummaryLogger().info("Test finish time : " + timeStamp);

		// Print Test suite total duration
		logger.info("Test duration : " + String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes(context.getTestSuiteTimeDuration()),
				TimeUnit.MILLISECONDS.toSeconds(context.getTestSuiteTimeDuration())
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(context.getTestSuiteTimeDuration()))));
		System.exit((int) context.getCurrentFailCount());
	}

	private void runSingleThread(List<TestExecutable> testList, Class<?> cls, int loopCycle, TestContext context)
			throws InstantiationException, IllegalAccessException, Exception {
		LogWrapper logger = context.getLogger();

		// ********************************************************************************************
		// Test Start
		// ********************************************************************************************
		logger.info("\n---------------- Start -------------------");
		context.setTestSuiteStartTime(System.currentTimeMillis());

		// Create an instance of Main class
		PrePostRunnable prePostCycleInstance = (PrePostRunnable) cls.newInstance();

		// Run prior to each test suit
		prePostCycleInstance.beforeTestsuite(context);
		for (int index = 0; index < loopCycle; index++) {
			logger.info("\n---------------- (Test Loop Count : " + (index + 1) + ") -------------------");
			// --------------------------------------------------------------------------------------------
			for (TestExecutable t : testList) {
				// Run Pre Method prior to any test Execution
				prePostCycleInstance.beforeTest(context);

				runIndividualTest(t);

				// Run Post Method prior to any test Execution
				prePostCycleInstance.afterTest(context);
			}
			// --------------------------------------------------------------------------------------------
		}
		// Run at the end of each test suit
		prePostCycleInstance.afterTestsuite(context);
		logger.info("\n---------------- Finished -------------------");
		// ********************************************************************************************
		// Test Finish
		// ********************************************************************************************
	}

	private void runIndividualTest(TestExecutable t) {
		long testStartTime = System.currentTimeMillis();
		try {
			@SuppressWarnings("unchecked")
			Map<String, TestObjectWrapper> testMap = (Map<String, TestObjectWrapper>) context
					.getGlobalObject(FWStatic_Store.GLOBAL_ANNOTATED_TEST_MAP);
			TestObjectWrapper testObject = testMap.get(t.getClass().getName());

			// @formatter:off
			context.getLogger().info("\n*************************************************************************"
									+ "\nTest Name	: " + t.getClass().getName()
									+ "\nWritten BY	: " + testObject.getTestPlanPreparedBy()
									+ "\nDate		: " + testObject.getTestPlanPreparationDate()
									+ "\nShort Desc	: " + testObject.getTestPlanDescription()
									+ "\n-------------------------------------------------------------------------");
			// @formatter:on

			// --------------------------------------------------------------------------------------------
			t.execute(context);
			// --------------------------------------------------------------------------------------------

		} catch (Exception e) {
			context.setTestStatus(TestStatus.FAIL);
			UtilsFramework.writePrintStackTrace(context, e);
		} catch (Throwable ex) {
			context.setTestStatus(TestStatus.FAIL);
			UtilsFramework.writePrintStackTrace(context, ex);
		} finally {
			context.generateTestSummary(t.getClass().getName(), testStartTime, System.currentTimeMillis());
		}
	}

	@SuppressWarnings("unchecked")
	private void runParallelThread(List<TestExecutable> testList, Class<?> cls, int loopCycle, TestContext context)
			throws InstantiationException, IllegalAccessException, Exception {
		LogWrapper logger = context.getLogger();

		// ********************************************************************************************
		// Test Start
		// ********************************************************************************************
		logger.info("\n---------------- Start -------------------");
		context.setTestSuiteStartTime(System.currentTimeMillis());

		// Create an instance of Main class
		PrePostRunnable prePostCycleInstance = (PrePostRunnable) cls.newInstance();

		// Run prior to each test suit
		prePostCycleInstance.beforeTestsuite(context);
		for (int index = 0; index < loopCycle; index++) {
			logger.info("\n---------------- (Test Loop Count : " + (index + 1) + ") -------------------");
			// --------------------------------------------------------------------------------------------
			ExecutorService service = Executors.newFixedThreadPool(1000);
			List<Future<Runnable>> futures = new ArrayList<>();

			for (TestExecutable t : testList) {

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
		logger.info("\n---------------- Finished -------------------");
		// ********************************************************************************************
		// Test Finish
		// ********************************************************************************************
	}
}

class runTestInParallel implements Runnable {

	PrePostRunnable prePostCycleInstance;
	TestContext context;
	TestExecutable test;

	public runTestInParallel(TestContext context, TestExecutable test, PrePostRunnable prePostCycleInstance) {
		this.context = context;
		this.test = test;
		this.prePostCycleInstance = prePostCycleInstance;
	}

	@Override
	public void run() {
		// Run Pre Method prior to any test Execution
		try {
			prePostCycleInstance.beforeTest(context);

			runIndividualTest(test);

			// Run Post Method prior to any test Execution
			prePostCycleInstance.afterTest(context);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void runIndividualTest(TestExecutable t) {
		long testStartTime = System.currentTimeMillis();
		try {
			@SuppressWarnings("unchecked")
			Map<String, TestObjectWrapper> testMap = (Map<String, TestObjectWrapper>) context
					.getGlobalObject(FWStatic_Store.GLOBAL_ANNOTATED_TEST_MAP);
			TestObjectWrapper testObject = testMap.get(t.getClass().getName());

			// @formatter:off
			context.getLogger().info("\n*************************************************************************"
									+ "\nTest Name	: " + t.getClass().getName()
									+ "\nWritten BY	: " + testObject.getTestPlanPreparedBy()
									+ "\nDate		: " + testObject.getTestPlanPreparationDate()
									+ "\nShort Desc	: " + testObject.getTestPlanDescription()
									+ "\n-------------------------------------------------------------------------");
			// @formatter:on

			// --------------------------------------------------------------------------------------------
			t.execute(context);
			// --------------------------------------------------------------------------------------------

		} catch (Exception e) {
			context.setTestStatus(TestStatus.FAIL);
			UtilsFramework.writePrintStackTrace(context, e);
		} catch (Throwable ex) {
			context.setTestStatus(TestStatus.FAIL);
			UtilsFramework.writePrintStackTrace(context, ex);
		} finally {
			context.generateTestSummary(t.getClass().getName(), testStartTime, System.currentTimeMillis());
		}
	}
}