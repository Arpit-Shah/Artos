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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.artos.framework.Enums.TestStatus;
import com.artos.framework.FWStaticStore;
import com.artos.interfaces.TestProgress;
import com.artos.utils.UtilsFramework;

/**
 * BDD Runner Test Steps processor
 * 
 * @author ArpitShah
 *
 */
public class BDDRunnerTestSteps {

	TestContext context;
	BDDScenario scenario;
	List<TestProgress> listenerList;

	/**
	 * Constructor
	 * 
	 * @param context      {@link TestContext}
	 * @param listenerList List of {@link TestProgress} listener
	 */
	protected BDDRunnerTestSteps(TestContext context, List<TestProgress> listenerList) {
		this.context = context;
		this.listenerList = listenerList;
	}

	/**
	 * Run unit tests
	 * 
	 * @param scenario test scenario
	 */
	protected void runSingleThreadSteps(BDDScenario scenario) {
		this.scenario = scenario;
		// ********************************************************************************************
		// TestUnits Execution Start
		// ********************************************************************************************
		try {
			// get test unit list
			List<BDDStep> testSteps = scenario.getSteplist();

			// If there are no steps to execute then move on
			if (null == testSteps || testSteps.isEmpty()) {
				return;
			}

			// --------------------------------------------------------------------------------------------
			for (BDDStep step : testSteps) {

				long preserveFailCount = context.getCurrentFailCount();

				// Reset test status for next execution
				context.setCurrentUnitTestStatus(TestStatus.PASS);

				// reset parameterised test index count
				context.setTestUnitParameterIndex(0);

				// Clean parameter HashMap
				context.setStepParameter(new HashMap<>());
				// }

				// If stop on fail is selected then stop test execution
				if (FWStaticStore.frameworkConfig.isStopOnFail()) {
					if (context.getCurrentFailCount() > 0) {
						break;
					}
				}

				context.setCurrentTestStep(step);
				notifyPrintTestUnitPlan(step);

				// if data provider name is not specified then only execute test once
				if ((null == step.getLocalDataTable() || step.getLocalDataTable().isEmpty())) {
					runIndividualUnitTest(step);
				} else {
					runParameterizedUnitTest(step);
				}

				// If "drop following tests execution upon failure" is enabled then drop rest of
				// test cases
				if (step.getUnit().isDropRemainingUnitsUponFailure()
						&& context.getCurrentUnitFailCount() > preserveFailCount) {
					context.getLogger().info(FWStaticStore.ARTOS_DROP_EXECUTION_UPON_UNIT_FAIL_STAMP);
					break;
				}
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

	private void printException(Throwable e) {
		if (e.getClass() == InvocationTargetException.class) {
			// Catch InvocationTargetException and return cause
			UtilsFramework.writePrintStackTrace(context, e.getCause());
		} else {
			UtilsFramework.writePrintStackTrace(context, e);
		}
	}

	/**
	 * Responsible for execution individual test units
	 * 
	 * @param step TestUnit in format {@code TestStep}
	 */
	private void runIndividualUnitTest(BDDStep step) {
		try {
			// Run global before method prior to each test unit execution
			if (null != context.getBeforeTestUnit()) {
				notifyGlobalBeforeTestUnitMethodExecutionStarted(context.getBeforeTestUnit().getName(), step);
				// notifyGlobalBeforeTestUnitMethodExecutionStarted(step.getStepAction() + " " +
				// step.getStepDescription(), step);
				context.getBeforeTestUnit()
						.invoke(context.getPrePostRunnableObj().getDeclaredConstructor().newInstance(), context);
				notifyGlobalBeforeTestUnitMethodExecutionFinished(step);
			}
		} catch (Throwable e) {
			printException(e);
		}

		// ********************************************************************************************
		// TestStep Start
		// ********************************************************************************************
		try {

			injectGlobalTableValues();
			injectInlineTagValues(step);

			step.getUnit().setTestUnitStartTime(System.currentTimeMillis());

			runSimpleStep(step);

			postTestValidation(step.getUnit());
		} catch (Throwable e) {
			processTestUnitException(step.getUnit(), e);
		} finally {
			step.getUnit().setTestUnitFinishTime(System.currentTimeMillis());
		}
		// ********************************************************************************************
		// TestStep Finish
		// ********************************************************************************************

		try {
			// Run global after method post each test unit execution
			if (null != context.getAfterTestUnit()) {
				notifyGlobalAfterTestUnitMethodExecutionStarted(context.getAfterTestUnit().getName(), step);
				// notifyGlobalAfterTestUnitMethodExecutionStarted(step.getStepAction() + " " +
				// step.getStepDescription(), step);
				context.getAfterTestUnit()
						.invoke(context.getPrePostRunnableObj().getDeclaredConstructor().newInstance(), context);
				notifyGlobalAfterTestUnitMethodExecutionFinished(step);
			}

			// Run global after unit failed method post each failed test unit execution
			// If KTF marked test unit is passing then also execute this method because
			// outcome of this unit will be failed
			if (context.getCurrentUnitTestStatus() == TestStatus.FAIL
					|| (step.getUnit().isKTF() && context.getCurrentUnitTestStatus() == TestStatus.PASS)) {
				if (null != context.getAfterFailedUnit()) {
					notifyGlobalAfterFailedUnitMethodExecutionStarted(context.getAfterFailedUnit().getName(), step);
					// notifyGlobalAfterFailedUnitMethodExecutionStarted(step.getStepAction() + " "
					// + step.getStepDescription(), step);
					context.getAfterFailedUnit()
							.invoke(context.getPrePostRunnableObj().getDeclaredConstructor().newInstance(), context);
					notifyGlobalAfterFailedUnitMethodExecutionFinished(step);
				}
			}
		} catch (Throwable e) {
			printException(e);
		}

		// ********************************************************************************************
		// Generate Summary
		// ********************************************************************************************
		context.generateStepTestSummary(step);
		// context.getLogger().info(FWStaticStore.ARTOS_LINE_BREAK_2);

	}

	/**
	 * Responsible for executing data provider method which upon successful
	 * execution returns an array of parameters. TestCase will be re-run using all
	 * parameters available in the parameter array. If data provider method returns
	 * empty array or null then test case will be executed only once with null
	 * arguments.
	 * 
	 * @param step TestCase in format {@code TestUnitObjectWrapper}
	 */
	private void runParameterizedUnitTest(BDDStep step) {
		try {

			// Decide how many time step iteration is required
			int childIterationSize = 1;
			if (null == step.getLocalDataTable() || step.getLocalDataTable().isEmpty()) {
				childIterationSize = 1;
			} else {
				// Find the length of dataList, which will tell us how many time to iterate
				// child test cases
				List<String> firstSet = step.getLocalDataTable().values().iterator().next();
				childIterationSize = firstSet.size();
			}

			for (int i = 0; i < childIterationSize; i++) {
				// Reset HashMap
				context.setStepParameter(new HashMap<>());
				// Reset Index count
				context.setTestUnitParameterIndex(i);
				executeChildTest(step);
			}
		} catch (Exception e) {
			// Print Exception
			UtilsFramework.writePrintStackTrace(context, e);
			// notifyTestSuiteException(e.getMessage());

			// Mark current test as fail due to exception during data provider processing
			context.setTestStatus(TestStatus.FAIL, e.getMessage());
			context.generateStepTestSummary(step);
		}
	}

	/**
	 * Responsible for execution of a test unit.
	 * 
	 * @param step TestCase in format {@code TestUnitObjectWrapper}
	 * @throws Exception Exception during test execution
	 */
	private void runSimpleStep(BDDStep step) throws Exception {
		// --------------------------------------------------------------------------------------------
		try {
			notifyTestUnitExecutionStarted(step);

			// Run single unit
			TestUnitObjectWrapper unit = step.getUnit();
			unit.getTestUnitMethod().invoke(
					unit.getTestUnitMethod().getDeclaringClass().getDeclaredConstructor().newInstance(), context);

			notifyTestUnitExecutionFinished(step);

			// When method fails via reflection, it throws InvocationTargetExcetion
		} catch (InvocationTargetException e) {
			processInvocationTargetException(e);
		}

		// --------------------------------------------------------------------------------------------
	}

	private void processInvocationTargetException(InvocationTargetException e)
			throws InvocationTargetException, Exception {
		// Catch InvocationTargetException and return cause
		if (null == e.getCause()) {
			throw e;
		} else {
			// Cast cause into Exception because Executor service can not handle throwable
			throw (Exception) e.getCause();
		}
	}

	/**
	 * Responsible for execution of test units (Considered as child test units) with
	 * given parameter. Parameterised object array index and value(s) class type(s)
	 * will be printed prior to test execution for user's benefit.
	 * 
	 * @param step       TestCase in format {@code TestUnitObjectWrapper}
	 * @param arrayIndex Parameter array index
	 */
	private void executeChildTest(BDDStep step) {
		String userInfo = "StepDataTable(" + context.getTestUnitParameterIndex() + ")";
		context.getLogger().info(userInfo);

		injectGlobalTableValues();
		injectLocalTableValues(step);
		injectInlineTagValues(step);

		// ********************************************************************************************
		// Parameterised Child TestCase Start
		// ********************************************************************************************
		// Disabled because it generates wrong extent flow when it runs as parameterised
		// steps
		// notifyChildTestUnitExecutionStarted(scenario, step, userInfo);

		runIndividualUnitTest(step);

		// Disabled because it generates wrong extent flow when it runs as parameterised
		// steps
		// notifyChildTestUnitExecutionFinished(step);

		// ********************************************************************************************
		// Parameterised Child TestCase Finish
		// ********************************************************************************************
	}

	private void injectInlineTagValues(BDDStep step) {
		// Get stepParameter Map, at this stage it should be empty
		Map<String, String> stepParameter = context.getStepParameterMap();

		// This is to look after in-line tag and in-line global tag
		for (int i = 0; i < step.getInlineParameterList().size(); i++) {

			// Get In-line tag
			String value = step.getInlineParameterList().get(i);

			if (value.startsWith("<") && value.endsWith(">")) {
				String globalDataTableKey = value.replaceFirst("<", "").replaceAll(">", "").trim();
				// get global data table value
				List<String> globalValueList = scenario.getGlobalDataTable().get(globalDataTableKey);

				// Ensure this is TestCase index and not the Test Unit
				// System.out.println(context.getTestParameterIndex());
				stepParameter.put("Param" + i, globalValueList.get(context.getTestParameterIndex()));
			} else {
				stepParameter.put("Param" + i, value);
			}
		}
	}

	private void injectLocalTableValues(BDDStep step) {

		// Get stepParameter Map, at this stage it should be empty
		Map<String, String> stepParameter = context.getStepParameterMap();

		// This is to look after local table and local table with global tag
		// [WARNING]: Any key name overlapping with global key will be overwritten
		for (Entry<String, List<String>> entry : step.getLocalDataTable().entrySet()) {
			String key = entry.getKey();
			List<String> valueList = entry.getValue();

			// if value refers to global table tag then populate value from global table
			// column
			String value = valueList.get(context.getTestUnitParameterIndex());
			if (value.startsWith("<") && value.endsWith(">")) {
				String globalDataTableKey = value.replaceFirst("<", "").replaceAll(">", "").trim();

				// get global data table value
				List<String> globalValueList = scenario.getGlobalDataTable().get(globalDataTableKey);

				// Ensure this is TestCase index and not the Test Unit
				stepParameter.put(key, globalValueList.get(context.getTestParameterIndex()));
			} else {
				stepParameter.put(key, value);
			}
		}
	}

	private void injectGlobalTableValues() {
		// Get stepParameter Map, at this stage it should be empty
		Map<String, String> stepParameter = context.getStepParameterMap();

		// This is to look after global table
		if (null != scenario.getGlobalDataTable() && !scenario.getGlobalDataTable().isEmpty()) {
			// Add global table parameters to Map
			for (Entry<String, List<String>> entry : scenario.getGlobalDataTable().entrySet()) {
				String key = entry.getKey();
				List<String> value = entry.getValue();
				stepParameter.put(key, value.get(context.getTestParameterIndex()));
			}
		}
	}

	/**
	 * Responsible for processing throwable/exception thrown by test cases during
	 * execution time. If {@code ExpectedException} annotation defines expected
	 * throwable/exception and received throwable/exception does not match any of
	 * the defined throwable(s)/Exception(s) then test will be marked as FAIL.
	 * 
	 * @param unit test case in format {@code TestObjectWrapper}
	 * @param e    {@code Throwable} or {@code Exception}
	 */
	private void processTestUnitException(TestUnitObjectWrapper unit, Throwable e) {
		// If user has not specified expected exception then fail the test
		if (null != unit.getExpectedExceptionList() && !unit.getExpectedExceptionList().isEmpty()) {

			boolean exceptionMatchFound = false;
			for (Class<? extends Throwable> exceptionClass : unit.getExpectedExceptionList()) {
				if (e.getClass() == exceptionClass) {
					/* Exception matches as specified by user */
					context.setTestStatus(TestStatus.PASS,
							"Exception class is as expected : " + e.getClass().getName());

					/*
					 * If regular expression then validate exception message with regular expression
					 */
					/* If regular expression does not match then do string compare */
					if (null != unit.getExceptionContains() && !"".equals(unit.getExceptionContains())) {
						if (e.getMessage().contains(unit.getExceptionContains())) {
							context.setTestStatus(TestStatus.PASS,
									"Exception message contains : " + unit.getExceptionContains());
						} else if (e.getMessage().matches(unit.getExceptionContains())) {
							context.setTestStatus(TestStatus.PASS,
									"Exception message matches regex : " + unit.getExceptionContains());
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
				context.setTestStatus(TestStatus.FAIL, "Exception is not as expected : \nExpected : "
						+ expectedExceptions + "\nReturned : " + e.getClass().getName());
				UtilsFramework.writePrintStackTrace(context, e);
			}
		} else {
			context.setTestStatus(TestStatus.FAIL, e.getMessage());
			UtilsFramework.writePrintStackTrace(context, e);
		}
	}

	/**
	 * Responsible for post validation after test case execution is successfully
	 * completed. If expected throwable(s)/exception(s) are defined by user using
	 * {@code ExpectedException} and test case status is PASS or FAIL then test unit
	 * should be marked failed for not throwing expected throwable/exception.
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
			if (null != unit.getExpectedExceptionList() && !unit.getExpectedExceptionList().isEmpty()
					&& unit.isEnforceException()) {
				// Exception annotation was specified but did not occur
				context.setTestStatus(TestStatus.FAIL, "Exception was specified but did not occur");
			}
		}
	}

	// ==================================================================================
	// Register, deRegister and Notify Event Listeners
	// ==================================================================================

	void notifyGlobalBeforeTestUnitMethodExecutionStarted(String methodName, BDDStep step) {
		for (TestProgress listener : listenerList) {
			listener.globalBeforeTestUnitMethodExecutionStarted(methodName, step);
		}
	}

	void notifyGlobalBeforeTestUnitMethodExecutionFinished(BDDStep step) {
		for (TestProgress listener : listenerList) {
			listener.globalBeforeTestUnitMethodExecutionFinished(step);
		}
	}

	void notifyGlobalAfterTestUnitMethodExecutionStarted(String methodName, BDDStep step) {
		for (TestProgress listener : listenerList) {
			listener.globalAfterTestUnitMethodExecutionStarted(methodName, step);
		}
	}

	void notifyGlobalAfterTestUnitMethodExecutionFinished(BDDStep step) {
		for (TestProgress listener : listenerList) {
			listener.globalAfterTestUnitMethodExecutionFinished(step);
		}
	}

	void notifyGlobalAfterFailedUnitMethodExecutionStarted(String methodName, BDDStep step) {
		for (TestProgress listener : listenerList) {
			listener.globalAfterFailedUnitMethodExecutionStarted(methodName, step);
		}
	}

	void notifyGlobalAfterFailedUnitMethodExecutionFinished(BDDStep step) {
		for (TestProgress listener : listenerList) {
			listener.globalAfterFailedUnitMethodExecutionFinished(step);
		}
	}

	// void notifyLocalBeforeTestUnitMethodExecutionStarted(TestScenario scenario,
	// TestStep step) {
	// for (TestProgress listener : listenerList) {
	// listener.localBeforeTestUnitMethodExecutionStarted(scenario, step);
	// }
	// }

	// void notifyLocalBeforeTestUnitMethodExecutionFinished(TestStep step) {
	// for (TestProgress listener : listenerList) {
	// listener.localBeforeTestUnitMethodExecutionFinished(step);
	// }
	// }

	// void notifyLocalAfterTestUnitMethodExecutionStarted(TestScenario scenario,
	// TestStep step) {
	// for (TestProgress listener : listenerList) {
	// listener.localAfterTestUnitMethodExecutionStarted(scenario, step);
	// }
	// }

	// void notifyLocalAfterTestUnitMethodExecutionFinished(TestStep step) {
	// for (TestProgress listener : listenerList) {
	// listener.localAfterTestUnitMethodExecutionFinished(step);
	// }
	// }

	void notifyTestUnitExecutionStarted(BDDStep step) {
		for (TestProgress listener : listenerList) {
			listener.testUnitExecutionStarted(step);
		}
	}

	void notifyTestUnitExecutionFinished(BDDStep step) {
		for (TestProgress listener : listenerList) {
			listener.testUnitExecutionFinished(step);
		}
	}

	// private void notifyChildTestUnitExecutionStarted(BDDScenario scenario,
	// BDDStep step, String userInfo) {
	// for (TestProgress listener : listenerList) {
	// listener.childTestUnitExecutionStarted(scenario, step, userInfo);
	// }
	// }

	// private void notifyChildTestUnitExecutionFinished(BDDStep step) {
	// for (TestProgress listener : listenerList) {
	// listener.childTestUnitExecutionFinished(step);
	// }
	// }

	// void notifyLocalBeforeTestCaseMethodExecutionStarted(String methodName,
	// TestScenario scenario) {
	// for (TestProgress listener : listenerList) {
	// listener.localBeforeTestCaseMethodExecutionStarted(methodName, scenario);
	// }
	// }

	// void notifyLocalBeforeTestCaseMethodExecutionFinished(TestScenario scenario)
	// {
	// for (TestProgress listener : listenerList) {
	// listener.localBeforeTestCaseMethodExecutionFinished(scenario);
	// }
	// }

	// void notifyLocalAfterTestCaseMethodExecutionStarted(String methodName,
	// TestScenario scenario) {
	// for (TestProgress listener : listenerList) {
	// listener.localAfterTestCaseMethodExecutionStarted(methodName, scenario);
	// }
	// }

	// void notifyLocalAfterTestCaseMethodExecutionFinished(TestScenario scenario) {
	// for (TestProgress listener : listenerList) {
	// listener.localAfterTestCaseMethodExecutionFinished(scenario);
	// }
	// }

	void notifyPrintTestUnitPlan(BDDStep step) {
		for (TestProgress listener : listenerList) {
			listener.printTestUnitPlan(step);
		}
	}

}
