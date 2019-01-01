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

	public RunnerTestUnits(TestContext context) {
		this.context = context;
	}

	/**
	 * Run unit tests
	 * 
	 * @param t test case
	 * @throws Exception
	 */
	public void runSingleThreadUnits(TestObjectWrapper t) throws Exception {
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

				// Print Method name
				// context.getLogger().info("\n<" + unit.getTestUnitMethod().getName() + "()>\n");

				// Run Pre Method prior to any test unit Execution
				if (null != t.getMethodBeforeTestUnit()) {
					t.getMethodBeforeTestUnit().invoke(t.getTestClassObject().newInstance(), context);
				}

				// if data provider name is not specified then only execute test once
				if (null == unit.getDataProviderName() || "".equals(unit.getDataProviderName())) {
					runIndividualUnitTest(unit);
				} else {
					runParameterizedUnitTest(unit);
				}

				// Run Post Method prior to any test unit Execution
				if (null != t.getMethodAfterTestUnit()) {
					t.getMethodAfterTestUnit().invoke(t.getTestClassObject().newInstance(), context);
				}

				long testUnitDuration = unit.getTestUnitFinishTime() - unit.getTestUnitStartTime();
				// @formatter:off
				context.getLogger().info(""
					+ "\n[" 
					+ context.getCurrentUnitTestStatus() 
					+ "] : " + unit.getTestUnitMethod().getName() + "()" 
					+ "\n"
					/*+ "[Duration : "
					+ String.format("%d min, %d sec", 
							TimeUnit.MILLISECONDS.toMinutes(testUnitDuration),
							TimeUnit.MILLISECONDS.toSeconds(testUnitDuration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(testUnitDuration)))
					+ "]\n"*/
				);
				// @formatter:on
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
			// context.generateTestSummary(unit);
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
	 * @param t test case in format {@code TestObjectWrapper}
	 * @param e {@code Throwable} or {@code Exception}
	 */
	private void processTestUnitException(TestUnitObjectWrapper t, Throwable e) {
		// If user has not specified expected exception then fail the test
		if (null != t.getExpectedExceptionList() && !t.getExpectedExceptionList().isEmpty()) {

			boolean exceptionMatchFound = false;
			for (Class<? extends Throwable> exceptionClass : t.getExpectedExceptionList()) {
				if (e.getClass() == exceptionClass) {
					/* Exception matches as specified by user */
					context.setTestStatus(TestStatus.PASS, "Exception is as expected : " + e.getClass().getName() + " : " + e.getMessage());

					/* If regular expression then validate exception message with regular expression */
					/* If regular expression does not match then do string compare */
					if (null != t.getExceptionContains() && !"".equals(t.getExceptionContains())) {
						if (e.getMessage().matches(t.getExceptionContains())) {
							context.setTestStatus(TestStatus.PASS, "Exception message matches regex : " + t.getExceptionContains());
						} else if (e.getMessage().equals(t.getExceptionContains())) {
							context.setTestStatus(TestStatus.PASS, "Exception message matches string : " + t.getExceptionContains());
						} else {
							context.setTestStatus(TestStatus.FAIL, "Exception message does not match regex : \nRegularExpression : "
									+ t.getExceptionContains() + "\nException Message : " + e.getMessage());
						}
					}

					exceptionMatchFound = true;
					break;
				}
			}
			if (!exceptionMatchFound) {
				String expectedExceptions = "";
				for (Class<? extends Throwable> exceptionClass : t.getExpectedExceptionList()) {
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
	 * @param t {@code TestObjectWrapper} object
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
