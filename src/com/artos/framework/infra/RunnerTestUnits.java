/*******************************************************************************
 * Copyright (C) 2018-2019 Arpit Shah and Artos Contributors
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package com.artos.framework.infra;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.artos.framework.Enums.TestStatus;
import com.artos.framework.FWStaticStore;
import com.artos.framework.TestObjectWrapper;
import com.artos.framework.TestUnitObjectWrapper;
import com.artos.utils.UtilsFramework;

public class RunnerTestUnits {

	TestContext context;
	TestObjectWrapper t;

	protected RunnerTestUnits(TestContext context) {
		this.context = context;
	}

	/**
	 * Run unit tests
	 * 
	 * @param t test case
	 */
	protected void runSingleThreadUnits(TestObjectWrapper t) {
		this.t = t;
		// ********************************************************************************************
		// TestUnits Execution Start
		// ********************************************************************************************
		try {
			// get test unit list
			List<TestUnitObjectWrapper> unitTests = t.getTestUnitList();

			if (null == unitTests || unitTests.isEmpty()) {
				return;
			}

			// --------------------------------------------------------------------------------------------
			for (TestUnitObjectWrapper unit : unitTests) {

				// Reset test status for next execution
				context.setCurrentUnitTestStatus(TestStatus.PASS);

				// If stop on fail is selected then stop test execution
				if (FWStaticStore.frameworkConfig.isStopOnFail()) {
					if (context.getCurrentFailCount() > 0) {
						break;
					}
				}

				// Run global before method prior to each test unit execution
				if (null != context.getBeforeTestUnit()) {
					context.getBeforeTestUnit().invoke(context.getPrePostRunnableObj().newInstance(), context);
				}

				// Run custom before method prior to each test unit execution
				if (null != t.getMethodBeforeTestUnit()) {
					t.getMethodBeforeTestUnit().invoke(t.getTestClassObject().newInstance(), context);
				}

				// if data provider name is not specified then only execute test once
				if (null == unit.getDataProviderName() || "".equals(unit.getDataProviderName())) {
					runIndividualUnitTest(unit);
				} else {
					runParameterizedUnitTest(unit);
				}

				// Run custom after method post each test unit execution
				if (null != t.getMethodAfterTestUnit()) {
					t.getMethodAfterTestUnit().invoke(t.getTestClassObject().newInstance(), context);
				}

				// Run global after method post each test unit execution
				if (null != context.getAfterTestUnit()) {
					context.getAfterTestUnit().invoke(context.getPrePostRunnableObj().newInstance(), context);
				}

				// // long testUnitDuration = unit.getTestUnitFinishTime() - unit.getTestUnitStartTime();
//				// @formatter:off
//				context.getLogger().info(""
//					+ "\n[" 
//					+ context.getCurrentUnitTestStatus() 
//					+ "] : " + unit.getTestUnitMethod().getName() + "()" 
//					+ "\n"
//					/*+ "[Duration : "
//					+ String.format("%d min, %d sec", 
//							TimeUnit.MILLISECONDS.toMinutes(testUnitDuration),
//							TimeUnit.MILLISECONDS.toSeconds(testUnitDuration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(testUnitDuration)))
//					+ "]\n"*/
//				);
//				// @formatter:on
			}
			// --------------------------------------------------------------------------------------------

		} catch (Throwable e) {
			// Handle if any exception in pre-post runnable
			UtilsFramework.writePrintStackTrace(context, e);
		}
		// ********************************************************************************************
		// TestUnits Execution Finish
		// ********************************************************************************************
	}

	/**
	 * Responsible for execution individual test units
	 * 
	 * @param unit TestUnit in format {@code TestUnitObjectWrapper}
	 */
	private void runIndividualUnitTest(TestUnitObjectWrapper unit) {
		// ********************************************************************************************
		// TestUnit Start
		// ********************************************************************************************
		try {
			unit.setTestUnitStartTime(System.currentTimeMillis());

			// Set Default Known to fail information
			// context.setKnownToFail(unit.isKTF(), unit.getBugTrackingNumber());

			// If test timeout is defined then monitor thread for timeout
			if (0 != unit.getTestTimeout()) {
				runUnitTestWithTimeout(unit);
			} else {
				runSimpleUnitTest(unit);
			}

			postTestValidation(unit);
		} catch (Throwable e) {
			processTestUnitException(unit, e);
		} finally {
			unit.setTestUnitFinishTime(System.currentTimeMillis());
			context.generateUnitTestSummary(unit);
		}
		// ********************************************************************************************
		// TestUnit Finish
		// ********************************************************************************************
	}

	/**
	 * Responsible for executing data provider method which upon successful execution returns an array of parameters. TestCase will be re-run using
	 * all parameters available in the parameter array. If data provider method returns empty array or null then test case will be executed only once
	 * with null arguments.
	 * 
	 * @param unit TestCase in format {@code TestUnitObjectWrapper}
	 */
	private void runParameterizedUnitTest(TestUnitObjectWrapper unit) {
		// Implement later if needed
	}

	/**
	 * Responsible for execution of a test unit.
	 * 
	 * @param unit TestCase in format {@code TestUnitObjectWrapper}
	 * @throws Exception Exception during test execution
	 */
	private void runSimpleUnitTest(TestUnitObjectWrapper unit) throws Exception {
		// --------------------------------------------------------------------------------------------
		try {
			// Run single unit
			unit.getTestUnitMethod().invoke(t.getTestClassObject().newInstance(), context);

			// When method fails via reflection, it throws InvocationTargetExcetion
		} catch (InvocationTargetException e) {
			// Catch InvocationTargetException and return cause
			if (null == e.getCause()) {
				throw e;
			} else {
				// Cast cause into Exception because Executor service can not handle throwable
				throw (Exception) e.getCause();
			}
		}

		// --------------------------------------------------------------------------------------------
	}

	/**
	 * Responsible for executing test unit with thread timeout. If test unit execution is not finished within expected time then test will be
	 * considered failed.
	 * 
	 * @param unit Test unit in format {@code TestUnitObjectWrapper} object
	 * @throws Throwable Exception during test execution
	 */
	private void runUnitTestWithTimeout(TestUnitObjectWrapper unit) throws Throwable {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Future<String> future = executor.submit(new Callable<String>() {
			@Override
			public String call() throws Exception {
				runSimpleUnitTest(unit);
				return "TEST UNIT FINISHED WITHIN TIME";
			}
		});

		try {
			// System.out.println(future.get(t.getTestTimeout(), TimeUnit.MILLISECONDS));
			future.get(unit.getTestTimeout(), TimeUnit.MILLISECONDS);
		} catch (TimeoutException e) {
			future.cancel(true);
			context.setTestStatus(TestStatus.FAIL, "TEST UNIT TIMED OUT");
		} catch (ExecutionException e) {
			future.cancel(true);
			if (null == e.getCause()) {
				// If no cause is listed then process parent exception
				throw e;
			} else {
				// If cause is listed then only process cause
				throw e.getCause();
			}
		} catch (InterruptedException e) {
			context.getLogger().warn("InterruptedException was supressed assuming thread was intentinally killed by a runner");
			// This can happen if Unit future thread is cancelled/killed by Test case future while thread is busy/sleeping or occupied.
			// One scenario : if test case time out before unit timeout then test case will force cancel future.
		}

	}

	/**
	 * Responsible for processing throwable/exception thrown by test cases during execution time. If {@code ExpectedException} annotation defines
	 * expected throwable/exception and received throwable/exception does not match any of the defined throwable(s)/Exception(s) then test will be
	 * marked as FAIL.
	 * 
	 * @param unit test case in format {@code TestObjectWrapper}
	 * @param e {@code Throwable} or {@code Exception}
	 */
	private void processTestUnitException(TestUnitObjectWrapper unit, Throwable e) {
		// If user has not specified expected exception then fail the test
		if (null != unit.getExpectedExceptionList() && !unit.getExpectedExceptionList().isEmpty()) {

			boolean exceptionMatchFound = false;
			for (Class<? extends Throwable> exceptionClass : unit.getExpectedExceptionList()) {
				if (e.getClass() == exceptionClass) {
					/* Exception matches as specified by user */
					context.setTestStatus(TestStatus.PASS, "Exception class is as expected : " + e.getClass().getName());

					/* If regular expression then validate exception message with regular expression */
					/* If regular expression does not match then do string compare */
					if (null != unit.getExceptionContains() && !"".equals(unit.getExceptionContains())) {
						if (e.getMessage().equals(unit.getExceptionContains())) {
							context.setTestStatus(TestStatus.PASS, "Exception message matches : " + unit.getExceptionContains());
						} else if (e.getMessage().matches(unit.getExceptionContains())) {
							context.setTestStatus(TestStatus.PASS, "Exception message matches regex : " + unit.getExceptionContains());
						} else {
							context.setTestStatus(TestStatus.FAIL, "Exception message does not match : \nExpected : "
									+ unit.getExceptionContains() + "\nReceived : " + e.getMessage());
						}
					}

					exceptionMatchFound = true;
					break;
				}
			}
			if (!exceptionMatchFound) {
				String expectedExceptions = "";
				for (Class<? extends Throwable> exceptionClass : unit.getExpectedExceptionList()) {
					expectedExceptions += exceptionClass.getName() + " ";
				}
				context.setTestStatus(TestStatus.FAIL,
						"Exception is not as expected : \nExpected : " + expectedExceptions + "\nReturned : " + e.getClass().getName());
				UtilsFramework.writePrintStackTrace(context, e);
			}
		} else {
			context.setTestStatus(TestStatus.FAIL, e.getMessage());
			UtilsFramework.writePrintStackTrace(context, e);
		}
	}

	/**
	 * Responsible for post validation after test case execution is successfully completed. If expected throwable(s)/exception(s) are defined by user
	 * using {@code ExpectedException} and test case status is PASS or FAIL then test unit should be marked failed for not throwing expected
	 * throwable/exception.
	 * 
	 * <PRE>
	 * If test status is marked as SKIP or KTF then do not fail test case based on ExpectedException conditions. 
	 * If test is marked as SKIP then user should have taken decision based on condition checking so test framework does not need to overrule decision.
	 * If test is marked as KTF then user already knows that this test scenario is known to fail, so forcefully failing will dilute the meaning of having known to fail status.
	 * </PRE>
	 * 
	 * @param unit {@code TestUnitObjectWrapper} object
	 */
	private void postTestValidation(TestUnitObjectWrapper unit) {
		if (context.getCurrentTestStatus() == TestStatus.PASS || context.getCurrentTestStatus() == TestStatus.FAIL) {
			if (null != unit.getExpectedExceptionList() && !unit.getExpectedExceptionList().isEmpty() && unit.isEnforceException()) {
				// Exception annotation was specified but did not occur
				context.setTestStatus(TestStatus.FAIL, "Exception was specified but did not occur");
			}
		}
	}
}
