package com.arpitos.framework;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Logger;

import com.arpitos.infra.ContextConfiguration;
import com.arpitos.infra.OrganisedLog;
import com.arpitos.infra.TestContext;
import com.arpitos.infra.annotation.ScanTestSuitUsingReflection;
import com.arpitos.infra.annotation.TestObjectWrapper;
import com.arpitos.interfaces.PrePostRunnable;
import com.arpitos.interfaces.TestExecutable;

/**
 * This class is responsible for running test cases. It initialising logger and
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
	public static void run(List<TestExecutable> tests, Class<?> cls, String serialNumber, int loopCycle)
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

		// Using reflection grep all test cases
		{
			String packageName = cls.getName().substring(0, cls.getName().lastIndexOf("."));
			ScanTestSuitUsingReflection reflection = new ScanTestSuitUsingReflection(packageName);
			// Get all test case information and store it for later use
			Map<String, TestObjectWrapper> testCaseMap = reflection.getTestObjWrapperMap(false);
			context.setGlobalObject(ArpitosStatic_Store.GLOBAL_ANNOTATED_TEST_MAP, testCaseMap);
		}

		Logger logger = context.getLogger();
		printUsefulInfo(logger);
		long startTime = System.currentTimeMillis();

		// ********************************************************************************************
		// Test Start
		// ********************************************************************************************
		logger.info("\n-------- Start -----------");

		// Create an instance of Main class
		PrePostRunnable prePostCycleInstance = (PrePostRunnable) cls.newInstance();

		// Run prior to each test suit
		prePostCycleInstance.beforeTestsuit(context);
		for (int index = 0; index < loopCycle; index++) {
			logger.info("\n-------- (" + index + ") -----------");
			// --------------------------------------------------------------------------------------------
			for (TestExecutable t : tests) {
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

	private static void printUsefulInfo(Logger logger) {

		// @formatter:off
		
		logger.info("\nTest FrameWork Info");
		logger.info("* Arpitos version => " + Version.id());
		logger.info("* Java Runtime Environment version => " + System.getProperty("java.version"));
		logger.info("* Java Virtual Machine specification version => " + System.getProperty("java.vm.specification.version"));
		logger.info("* Java Runtime Environment specification version => " + System.getProperty("java.specification.version"));
		logger.info("* Java class path => " + System.getProperty("java.class.path"));
		logger.info("* List of paths to search when loading libraries => " + System.getProperty("java.library.path"));
		logger.info("* Operating system name => " + System.getProperty("os.name"));
		logger.info("* Operating system architecture => " + System.getProperty("os.arch"));
		logger.info("* Operating system version => " + System.getProperty("os.version"));
		logger.info("* File separator (\"/\" on UNIX) => " + System.getProperty("file.separator"));
		logger.info("* Path separator (\":\" on UNIX) => " + System.getProperty("path.separator"));
		logger.info("* User's account name => " + System.getProperty("user.name"));
		logger.info("* User's home directory => " + System.getProperty("user.home"));
		
		// logger.info("Java installation directory => " + System.getProperty("java.home"));
		// logger.info("Java Virtual Machine specification vendor => " + System.getProperty("java.vm.specification.vendor"));
		// logger.info("Java Virtual Machine specification name => " + System.getProperty("java.vm.specification.name"));
		// logger.info("Java Virtual Machine implementation version => " + System.getProperty("java.vm.version"));
		// logger.info("Java Virtual Machine implementation vendor => " + System.getProperty("java.vm.vendor"));
		// logger.info("Java Virtual Machine implementation name => " + System.getProperty("java.vm.name"));
		// logger.info("Java Runtime Environment specification vendor => " + System.getProperty("java.specification.vendor"));
		// logger.info("Java Runtime Environment specification name => " + System.getProperty("java.specification.name"));
		// logger.info("Java class format version number => " + System.getProperty("java.class.version"));
		// logger.info("Default temp file path => " + System.getProperty("java.io.tmpdir"));
		// logger.info("Name of JIT compiler to use => " + System.getProperty("java.compiler"));
		// logger.info("Path of extension directory or directories => " + System.getProperty("java.ext.dirs"));
		// logger.info("Line separator (\"\\n\" on UNIX) => " + System.getProperty("line.separator"));
		
		// logger.info("User's current working directory => " + System.getProperty("user.dir"));
		
		// @formatter:on
	}

}
