package com.arpitos.framework;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Logger;

import com.arpitos.infra.OrganisedLog;
import com.arpitos.infra.TestContext;
import com.arpitos.interfaces.PrePostRunnable;
import com.arpitos.interfaces.TestExecutable;

/**
 * This class is responsible in running test cases in provided sequence
 * 
 * @author ArpitS
 *
 */
public class MAIN {

	/**
	 * This method executes test cases
	 * 
	 * @param prePostCycle
	 *            = The object which has implementation of pre or post
	 *            processing for each test suit or test cases
	 * @param tests
	 *            = List of test cases in sequence which user would like to run
	 * @param testPackageName
	 *            = entire package name which user would like to report
	 * @param serialNumber
	 *            = Product serial number or unique reference
	 * @param loopCycle
	 *            = number of time test suit require running
	 * @throws Exception
	 */
	public static void run(PrePostRunnable prePostCycle, List<TestExecutable> tests, Class<?> cls, String serialNumber, int loopCycle)
			throws Exception {

		// -------------------------------------------------------------------//
		// Prepare Context
		String logDir = "./reporting/" + serialNumber;
		OrganisedLog organisedLogger = new OrganisedLog(logDir, cls.getPackage().getName(), true);
		TestContext context = new TestContext(organisedLogger);
		Logger logger = context.getLogger();
		
		long startTime = System.currentTimeMillis();

		// ********************************************************************************************
		// Test Start
		// ********************************************************************************************
		logger.info("\n-------- Start -----------");
		// Run prior to each test suit
		prePostCycle.Init(context);
		for (int index = 0; index < loopCycle; index++) {
			logger.info("\n-------- (" + index + ") -----------");
			// --------------------------------------------------------------------------------------------
			for (TestExecutable t : tests) {
				// Run Pre Method prior to any test Execution
				prePostCycle.preTest(context);

				t.onExecute(context);

				// Run Post Method prior to any test Execution
				prePostCycle.postTest(context);
			}
			// --------------------------------------------------------------------------------------------
		}
		// Run at the end of each test suit
		prePostCycle.Cleanup(context);
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
