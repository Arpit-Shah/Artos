package com.arpitos.framework;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import com.arpitos.infra.ContextConfiguration;
import com.arpitos.infra.OrganisedLog;
import com.arpitos.infra.TestContext;
import com.arpitos.infra.annotation.AfterTest;
import com.arpitos.infra.annotation.AfterTestsuit;
import com.arpitos.infra.annotation.ScanTestSuitUsingReflection;
import com.arpitos.infra.annotation.BeforeTest;
import com.arpitos.infra.annotation.BeforeTestsuit;
import com.arpitos.infra.annotation.TestObjectWrapper;
import com.arpitos.infra.annotation.Testcase;
import com.arpitos.interfaces.PrePostRunnable;
import com.arpitos.interfaces.TestExecutable;

/**
 * This class is responsible for running test cases. It initializing logger and
 * context with provided information. It is also responsible for running test
 * cases in given sequence (including pre/post methods)
 * 
 * @author ArpitS
 *
 */
public class Runner {

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
	public static void run(PrePostRunnable prePostCycle, List<TestExecutable> tests, Class<?> cls, String serialNumber, int loopCycle)
			throws Exception {

		// -------------------------------------------------------------------//
		// Prepare Context
		ContextConfiguration contextconf = new ContextConfiguration();
		String logDir = "./reporting/" + serialNumber;
		String strTestName = cls.getPackage().getName();
		boolean enableLogDecoration = contextconf.isEnableLogDecoration();
		boolean enableTextLog = contextconf.isEnableTextLog();
		boolean enableHTMLLog = contextconf.isEnableHTMLLog();
		OrganisedLog organisedLogger = new OrganisedLog(logDir, strTestName, enableLogDecoration, enableTextLog, enableHTMLLog);
		TestContext context = new TestContext(organisedLogger);

		// Scan all test cases with correct annotation and store it in global
		// variable
		String packageName = cls.getName().substring(0, cls.getName().lastIndexOf("."));
		Map<String, TestObjectWrapper> testMap = new ScanTestSuitUsingReflection(context, packageName).invoke();
		context.setGlobalObject(ArpitosStatic_Store.GLOBAL_ANNOTATED_TEST_MAP, testMap);

		Logger logger = context.getLogger();

		long startTime = System.currentTimeMillis();

		// ********************************************************************************************
		// Test Start
		// ********************************************************************************************
		logger.info("\n-------- Start -----------");
		// Run prior to each test suit
		prePostCycle.beforeTestsuit(context);
		for (int index = 0; index < loopCycle; index++) {
			logger.info("\n-------- (" + index + ") -----------");
			// --------------------------------------------------------------------------------------------
			for (TestExecutable t : tests) {
				// Run Pre Method prior to any test Execution
				prePostCycle.beforeTest(context);

				t.onExecute(context);

				// Run Post Method prior to any test Execution
				prePostCycle.afterTest(context);
			}
			// --------------------------------------------------------------------------------------------
		}
		// Run at the end of each test suit
		prePostCycle.afterTestsuit(context);
		logger.info("\n-------- Finished -----------");
		// ********************************************************************************************
		// Test Finish
		// ********************************************************************************************

		long endTime = System.currentTimeMillis();
		logger.info("PASS:" + context.getCurrentPassCount() + " FAIL:" + context.getCurrentFailCount() + " SKIP:" + context.getCurrentSkipCount()
				+ " KTF:" + context.getCurrentKTFCount() + " TOTAL:" + context.getTotalTestCount());
		logger.info("Total Test Time : " + String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes((endTime - startTime)),
				TimeUnit.MILLISECONDS.toSeconds((endTime - startTime))
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((endTime - startTime)))));
		// System.exit(0);
	}

}
