package com.arpitos.framework.infra;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
	 * @param prePostCycle
	 *            The class object which implements {@link PrePostRunnable}
	 * @param tests
	 *            List of test cases which implements {@link TestExecutable}
	 * @param cls
	 *            TestSuit Main class (Used for getting package name to
	 *            construct log directory structure)
	 * @param serialNumber
	 *            Product serialNumber or unique identifier
	 * @param loopCycle
	 *            Test Suit look time (Repeat count)
	 * @throws Exception
	 *             If test case exception is not handled then test execution
	 *             stops at this point
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
		long startTime = System.currentTimeMillis();

		// ********************************************************************************************
		// Test Start
		// ********************************************************************************************
		logger.info("\n---------------- Start -------------------");

		// Create an instance of Main class
		PrePostRunnable prePostCycleInstance = (PrePostRunnable) cls.newInstance();

		// Run prior to each test suit
		prePostCycleInstance.beforeTestsuit(context);
		for (int index = 0; index < loopCycle; index++) {
			logger.info("\n---------------- (Test Loop Count : " + index + 1 + ") -------------------");
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

		long endTime = System.currentTimeMillis();
		logger.info("PASS:" + context.getCurrentPassCount() + " FAIL:" + context.getCurrentFailCount() + " SKIP:" + context.getCurrentSkipCount()
				+ " KTF:" + context.getCurrentKTFCount() + " TOTAL:" + context.getTotalTestCount());
		logger.info("Total Test Time : " + String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes((endTime - startTime)),
				TimeUnit.MILLISECONDS.toSeconds((endTime - startTime))
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((endTime - startTime)))));
		System.exit((int)context.getCurrentFailCount());
	}
}
