package com.arpitos.interfaces;

import org.apache.logging.log4j.Logger;

import com.arpitos.infra.TestContext;
import com.arpitos.infra.annotation.AfterTest;
import com.arpitos.infra.annotation.AfterTestsuit;
import com.arpitos.infra.annotation.BeforeTest;
import com.arpitos.infra.annotation.BeforeTestsuit;

/**
 * Used for classes which implements Pre Post methods for test cases
 * 
 * @author ArpitS
 *
 */
public interface PrePostRunnable {

	/**
	 * Runs prior to each test case execution
	 * 
	 * @param context
	 *            Test Context
	 * @throws Exception
	 *             In case of pre execution failed
	 */
	@BeforeTest
	default public void beforeTest(TestContext context) throws Exception {
		Logger logger = context.getLogger();

		// --------------------------------------------------------------------------------------------
		// TODO Write Code Here
		logger.info("--------This runs pre each test execution--------");
		// --------------------------------------------------------------------------------------------

	}

	/**
	 * Runs post each test case execution
	 * 
	 * @param context
	 *            Test context
	 * @throws Exception
	 *             In case of test of post execution failed
	 */
	@AfterTest
	default public void afterTest(TestContext context) throws Exception {
		Logger logger = context.getLogger();

		// --------------------------------------------------------------------------------------------
		// TODO Write Code Here
		logger.info("--------This runs post each test execution--------");
		// --------------------------------------------------------------------------------------------

	}

	/**
	 * Runs prior to test suit execution. Only run once
	 * 
	 * @param context
	 *            Test context
	 * @throws Exception
	 *             In case of initialization failed
	 */
	@BeforeTestsuit
	default public void beforeTestsuit(TestContext context) throws Exception {
		Logger logger = context.getLogger();

		// --------------------------------------------------------------------------------------------
		// TODO Write Code Here
		logger.info("--------This runs at the start of testsuit execution--------");
		// --------------------------------------------------------------------------------------------

	}

	/**
	 * Runs at the end of test suit execution. Only run once
	 * 
	 * @param context
	 *            Test context
	 * @throws Exception
	 *             In case of cleanup failed
	 */
	@AfterTestsuit
	default public void afterTestsuit(TestContext context) throws Exception {
		Logger logger = context.getLogger();

		// --------------------------------------------------------------------------------------------
		// TODO Write Code Here
		logger.info("--------This runs at the end of testsuit execution--------");
		// --------------------------------------------------------------------------------------------

	}
	
}
