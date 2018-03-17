package com.arpitos.interfaces;

import org.apache.logging.log4j.Logger;

import com.arpitos.infra.TestContext;

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
	default public void preTest(TestContext context) throws Exception {
		Logger logger = context.getLogger();
		logger.info(".........................................................................");

		// --------------------------------------------------------------------------------------------
		// TODO Write Code Here
		logger.info("This runs pre each test execution");
		// --------------------------------------------------------------------------------------------

		logger.info(".........................................................................");
	}

	/**
	 * Runs post each test case execution
	 * 
	 * @param context
	 *            Test context
	 * @throws Exception
	 *             In case of test of post execution failed
	 */
	default public void postTest(TestContext context) throws Exception {
		Logger logger = context.getLogger();
		logger.info(".........................................................................");

		// --------------------------------------------------------------------------------------------
		// TODO Write Code Here
		logger.info("This runs post each test execution");
		// --------------------------------------------------------------------------------------------

		logger.info(".........................................................................");
	}

	/**
	 * Runs prior to test suit execution. Only run once
	 * 
	 * @param context
	 *            Test context
	 * @throws Exception
	 *             In case of initialization failed
	 */
	default public void Init(TestContext context) throws Exception {
		Logger logger = context.getLogger();
		logger.info(".........................................................................");

		// --------------------------------------------------------------------------------------------
		// TODO Write Code Here
		logger.info("This runs at the start of testsuit execution");
		// --------------------------------------------------------------------------------------------

		logger.info(".........................................................................");
	}

	/**
	 * Runs at the end of test suit execution. Only run once
	 * 
	 * @param context
	 *            Test context
	 * @throws Exception
	 *             In case of cleanup failed
	 */
	default public void Cleanup(TestContext context) throws Exception {
		Logger logger = context.getLogger();
		logger.info(".........................................................................");

		// --------------------------------------------------------------------------------------------
		// TODO Write Code Here
		logger.info("This runs at the end of testsuit execution");
		// --------------------------------------------------------------------------------------------

		logger.info(".........................................................................");
	}
}
