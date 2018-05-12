package com.arpitos.framework.infra;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Logger;

import com.arpitos.framework.GUITestSelector;
import com.arpitos.framework.ScanTestSuit;
import com.arpitos.framework.Static_Store;
import com.arpitos.framework.TestObjectWrapper;
import com.arpitos.interfaces.PrePostRunnable;
import com.arpitos.interfaces.TestExecutable;
import com.arpitos.interfaces.TestRunnable;

/**
 * This class is responsible for running test cases. It initialising logger and
 * context with provided information. It is also responsible for running test
 * cases in given sequence (including pre/post methods)
 * 
 * @author ArpitS
 *
 */
public class Runner {

	public static void getTestWrapperList(List<TestExecutable> testList, Class<?> cls) throws Exception {
		if (testList.isEmpty()) {
			testList = (ArrayList<TestExecutable>) new ScanTestSuit(cls.getPackage().getName()).getTestList(true, true);
		}
	}

	public static void run(List<TestExecutable> testList, Class<?> cls, String serialNumber, int loopCycle) throws Exception {
		if (Static_Store.FWConfig.isEnableGUITestSelector()) {
			TestRunnable runObj = new TestRunnable() {
				@Override
				public void executeTest(ArrayList<TestExecutable> testList, Class<?> cls, String serialNumber, int loopCount) throws Exception {
					Runner.runTest(testList, cls, serialNumber, loopCount);
				}
			};
			new GUITestSelector((ArrayList<TestExecutable>) testList, cls, serialNumber, loopCycle, runObj);
		} else {
			runTest(testList, cls, serialNumber, loopCycle);
		}
	}

	/**
	 * This method executes test cases
	 * 
	 * @param testList
	 *            test object list
	 * @param cls
	 *            class object which is executing test
	 * @param serialNumber
	 *            product serial number
	 * @param loopCycle
	 *            test loop cycle
	 * @throws Exception
	 */
	public static void runTest(List<TestExecutable> testList, Class<?> cls, String serialNumber, int loopCycle) throws Exception {

		// -------------------------------------------------------------------//
		// Prepare Context
		// read configuration at start
		String logDir = Static_Store.FWConfig.getLogRootDir() + serialNumber;
		String strTestName = cls.getPackage().getName();
		boolean enableLogDecoration = Static_Store.FWConfig.isEnableLogDecoration();
		boolean enableTextLog = Static_Store.FWConfig.isEnableTextLog();
		boolean enableHTMLLog = Static_Store.FWConfig.isEnableHTMLLog();
		OrganisedLog organisedLogger = new OrganisedLog(logDir, strTestName, enableLogDecoration, enableTextLog, enableHTMLLog);
		TestContext context = new TestContext(organisedLogger);
		Static_Store.context = context;

		// Using reflection grep all test cases
		{
			String packageName = cls.getName().substring(0, cls.getName().lastIndexOf("."));
			ScanTestSuit reflection = new ScanTestSuit(packageName);
			// Get all test case information and store it for later use
			Map<String, TestObjectWrapper> testCaseMap = reflection.getTestObjWrapperMap(false);
			context.setGlobalObject(Static_Store.GLOBAL_ANNOTATED_TEST_MAP, testCaseMap);
		}

		Logger logger = context.getLogger();

		// TODO : Parallel running test case can not work with current
		// architecture so should not enable this feature until solution is
		// found
		boolean enableParallelTestRunning = false;
		if (enableParallelTestRunning) {
			runParallelThread(testList, cls, loopCycle, context);
		} else {
			runSingleThread(testList, cls, loopCycle, context);
		}

		logger.info("PASS:" + context.getCurrentPassCount() + " FAIL:" + context.getCurrentFailCount() + " SKIP:" + context.getCurrentSkipCount()
				+ " KTF:" + context.getCurrentKTFCount() + " TOTAL:" + context.getTotalTestCount());
		context.setTestSuitFinishTime(System.currentTimeMillis());
		logger.info("Test duration : " + String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes(context.getTestSuitTimeDuration()),
				TimeUnit.MILLISECONDS.toSeconds(context.getTestSuitTimeDuration())
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(context.getTestSuitTimeDuration()))));
		System.exit((int) context.getCurrentFailCount());
	}

	private static void runSingleThread(List<TestExecutable> testList, Class<?> cls, int loopCycle, TestContext context)
			throws InstantiationException, IllegalAccessException, Exception {
		Logger logger = context.getLogger();

		// ********************************************************************************************
		// Test Start
		// ********************************************************************************************
		logger.info("\n---------------- Start -------------------");
		context.setTestSuitStartTime(System.currentTimeMillis());

		// Create an instance of Main class
		PrePostRunnable prePostCycleInstance = (PrePostRunnable) cls.newInstance();

		// Run prior to each test suit
		prePostCycleInstance.beforeTestsuit(context);
		for (int index = 0; index < loopCycle; index++) {
			logger.info("\n---------------- (Test Loop Count : " + (index + 1) + ") -------------------");
			// --------------------------------------------------------------------------------------------
			for (TestExecutable t : testList) {
				// Run Pre Method prior to any test Execution
				prePostCycleInstance.beforeTest(context);

				t.onExecute(context);

				// Run Post Method prior to any test Execution
				prePostCycleInstance.afterTest(context);
			}
			// --------------------------------------------------------------------------------------------
		}
		// Run at the end of each test suit
		prePostCycleInstance.afterTestsuit(context);
		logger.info("\n---------------- Finished -------------------");
		// ********************************************************************************************
		// Test Finish
		// ********************************************************************************************
	}

	@SuppressWarnings("unchecked")
	private static void runParallelThread(List<TestExecutable> testList, Class<?> cls, int loopCycle, TestContext context)
			throws InstantiationException, IllegalAccessException, Exception {
		Logger logger = context.getLogger();

		// ********************************************************************************************
		// Test Start
		// ********************************************************************************************
		logger.info("\n---------------- Start -------------------");
		context.setTestSuitStartTime(System.currentTimeMillis());

		// Create an instance of Main class
		PrePostRunnable prePostCycleInstance = (PrePostRunnable) cls.newInstance();

		// Run prior to each test suit
		prePostCycleInstance.beforeTestsuit(context);
		for (int index = 0; index < loopCycle; index++) {
			logger.info("\n---------------- (Test Loop Count : " + index + 1 + ") -------------------");
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
		prePostCycleInstance.afterTestsuit(context);
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

			test.onExecute(context);

			// Run Post Method prior to any test Execution
			prePostCycleInstance.afterTest(context);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}