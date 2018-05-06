package com.arpitos.interfaces;

import java.util.ArrayList;

public interface TestRunnable {

	/**
	 * Execute the tests in testList sequentially
	 * 
	 * @param testList
	 *            List of TestExecutors to run
	 * @param loopCount
	 *            Number of times each TestExecutor will run
	 * @throws Exception
	 *             exceptions that happened within the tests
	 */
	public void executeTest(ArrayList<TestExecutable> testList, Class<?> cls, String serialNumber, int loopCount) throws Exception;
}
