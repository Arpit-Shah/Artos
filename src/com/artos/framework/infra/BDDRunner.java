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

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import com.artos.framework.Enums.Importance;
import com.artos.framework.Enums.TestStatus;
import com.artos.framework.FWStaticStore;
import com.artos.framework.listener.ExtentReportListener;
import com.artos.framework.listener.TestExecutionEventListener;
import com.artos.framework.listener.UDPReportListener;
import com.artos.framework.parser.BDDFeatureFileParser;
import com.artos.interfaces.TestProgress;
import com.artos.interfaces.TestScenarioRunnable;
import com.artos.utils.Transform;
import com.artos.utils.UtilsFramework;

/**
 * This class is responsible for running test cases in user defined manner
 */
public class BDDRunner {

	TestContext context;
	List<TestProgress> listenerList = new ArrayList<TestProgress>();
	Map<String, TestUnitObjectWrapper> stepDefinitionMap = null;

	// ==================================================================================
	// Constructor (Starting point of framework)
	// ==================================================================================

	/**
	 * <PRE>
	 * Constructor responsible for initialising and registering required listeners. 
	 * TestExecutionEventListener is responsible for printing information during test execution
	 * ExtentReportListener is responsible for Extent report generation
	 * </PRE>
	 * 
	 * @param context                  TestContext object
	 * @param externalListnerClassList external listener class list
	 * @throws Exception exception in case of error
	 * @see TestContext
	 * @see TestExecutionEventListener
	 * @see ExtentReportListener
	 */
	protected BDDRunner(TestContext context, List<Class<?>> externalListnerClassList) throws Exception {
		this.context = context;

		// Register default listener
		TestExecutionEventListener testListener = new TestExecutionEventListener(context);
		registerListener(testListener);
		context.registerListener(testListener);

		// Register extent reporting listener
		if (FWStaticStore.frameworkConfig.isEnableExtentReport()) {
			ExtentReportListener extentListener = new ExtentReportListener(context);
			registerListener(extentListener);
			context.registerListener(extentListener);
		}

		// Register UDPReport listener
		if (FWStaticStore.frameworkConfig.isEnableDashBoard()) {
			UDPReportListener udpListener = new UDPReportListener(context);
			registerListener(udpListener);
			context.registerListener(udpListener);
		}

		// Register external listener
		if (null != externalListnerClassList) {
			for (Class<?> listener : externalListnerClassList) {
				TestProgress externalListener = (TestProgress) listener.getDeclaredConstructor().newInstance();
				registerListener(externalListener);
				context.registerListener(externalListener);
			}
		}

	}

	// ==================================================================================
	// Runner Method
	// ==================================================================================

	/**
	 * Runner for the framework. Responsible for generating test list after scanning
	 * a test suite, generate test script if required, show GUI test selector if
	 * enabled
	 * 
	 * @throws Exception Exception will be thrown if test execution failed
	 */
	protected void run() throws Exception {

		// Create empty scenario list
		List<BDDScenario> scenarioList = new ArrayList<>();
		// Get all featureFileObjectWrapper
		List<BDDFeatureObjectWrapper> featureObjectWrapperList = context.getTestSuite().getFeatureFiles();
		// Find unitGroupList
		List<String> groupList = context.getTestSuite().getTestUnitGroupList();

		// Iterate through each of the provided feature file and find scenarios
		for (BDDFeatureObjectWrapper featureObj : featureObjectWrapperList) {
			// Parse file, If any issue then stop execution
			File featureFile = featureObj.getFeatureFile();
			BDDFeatureFileParser featureFileParser = new BDDFeatureFileParser(featureFile, groupList);
			// print(featureFileParser.getScenarioList());

			// get feature scenarios which contains all methods filtered by the group list
			if (null == scenarioList || scenarioList.isEmpty()) {
				// If it is first feature file then following line will be exercised
				scenarioList = featureFileParser.getFeature().getScenarios();
			} else {
				// If more feature files are processed then add them all to the scenario list
				List<BDDScenario> newScenarioList = featureFileParser.getFeature().getScenarios();
				if (null != newScenarioList && !newScenarioList.isEmpty()) {
					scenarioList.addAll(newScenarioList);
				}
			}
		}

		// Transform TestUnitList into TestUnitObjectWrapper Map
		this.stepDefinitionMap = new BDDTransformToTestObjectWrapper(context).getStepDefinitionMap();

		// find and populate all methods against test steps
		mapTestStepMethods(scenarioList);

		// If GUI test selector is enabled then show it or else execute test cases
		if (FWStaticStore.frameworkConfig.isEnableGUITestSelector()) {
			TestScenarioRunnable runObj = new TestScenarioRunnable() {
				@Override
				public void executeTest(TestContext context, List<BDDScenario> scenarioList) throws Exception {
					runTest(scenarioList);
				}
			};
			new BDDGUITestSelector(context, (List<BDDScenario>) scenarioList, runObj);
		} else {
			runTest(scenarioList);
		}

	}

	/**
	 * Finds all units for each of the test steps and store them inside TestStep
	 * object If any units are missing then generates skeleton method for helping
	 * user. This will stop execution straight away.
	 * 
	 * @param feature BDD feature object
	 */
	private void mapTestStepMethods(List<BDDScenario> scenarioList) {
		boolean missingStepMethods = false;
		List<String> mockMethodNames = new ArrayList<>();

		StringBuilder sb = new StringBuilder();
		sb.append(
				"\nSEEMS THAT YOU ARE MISSING SOME METHODS\nYou can implement missing steps with the snippets below:");

		for (BDDScenario sc : scenarioList) {
			for (BDDStep st : sc.getSteplist()) {
				String description = st.getStepDescription().trim().replaceAll("\\\".*?\\\"", "\"\"");

				TestUnitObjectWrapper unit = stepDefinitionMap.get(description);
				if (null == unit) {
					missingStepMethods = true;
					// System.err.println("[Warning] Step \"" + st.getStepDescription() + "\" can
					// not be found in a step file");
					sb.append(buildMockFunction(mockMethodNames, st.getStepDescription().trim()));
				}
				st.setUnit(unit);
			}
		}

		if (missingStepMethods) {
			// Print Scenario so user can see what we read from the file
			printScenario(scenarioList);
			// Print mock methods, which may help user construct step class
			System.err.println(sb.toString());
			// Stop execution
			System.exit(-1);
		}
	}

	private void printScenario(List<BDDScenario> scenarios) {
		for (BDDScenario sc : scenarios) {
			if (null != sc.getGroupList() && !sc.getGroupList().isEmpty()) {
				System.out.println("\n");
				for (String group : sc.getGroupList()) {
					if (group.equals("*")) {
						continue;
					}
					System.out.print("@" + group + " ");
				}
				System.out.println("");
			}
			if (sc.isBackground()) {
				System.out.println("BackGround: " + sc.scenarioDescription);
			} else {
				System.out.println("Scenario: " + sc.scenarioDescription);
			}
			for (BDDStep step : sc.steplist) {
				System.out.println("\t" + step.getStepAction() + " " + step.getStepDescription());
				if (null != step.getLocalDataTable() && !step.getLocalDataTable().isEmpty()) {
					System.out.print("\t  ");
					for (Entry<String, List<String>> entry : step.getLocalDataTable().entrySet()) {
						System.out.print("|" + entry.getKey());
					}
					System.out.println("|");
				}
			}
			if (null != sc.getGlobalDataTable() && !sc.getGlobalDataTable().isEmpty()) {
				System.out.print("\nExample:");
				for (Entry<String, List<String>> entry : sc.getGlobalDataTable().entrySet()) {
					System.out.print("|" + entry.getKey());
				}
				System.out.println("|");
			}
		}
	}

	private String buildMockFunction(List<String> mockMethodNames, String stepDefinition) {

		String constructMethodName = stepDefinition.replaceAll("\\\".*?\\\"", "").trim().replaceAll("\\s", "_");

		// Protect against repeat
		if (mockMethodNames.contains(constructMethodName)) {
			return "";
		} else {
			mockMethodNames.add(constructMethodName);
		}

		StringBuilder sb = new StringBuilder();
		sb.append("\n\n");
		sb.append("@StepDefinition(\"" + stepDefinition.replaceAll("\\\".*?\\\"", "\\\\\"\\\\\"") + "\")");
		sb.append("\n");
		// add _ to avoid error in-case description has digit as first character
		// replace all spaces with _
		// replace all values between "" to empty space
		sb.append("public void _"
				+ stepDefinition.replaceAll("\\\".*?\\\"", "").trim().replaceAll("\\s", "_").toLowerCase()
				+ "(TestContext context) throws Exception {");
		sb.append("\n");
		sb.append("\t");
		sb.append("throw new Exception(\"Pending to implement\");");
		sb.append("\n");
		sb.append("}");
		return sb.toString();
	}

	/**
	 * This method executes test cases
	 * 
	 * @param feature test feature
	 * @throws Exception
	 */
	private void runTest(List<BDDScenario> scenarioList) throws Exception {

		LogWrapper logger = context.getLogger();

		// TODO : Parallel running test case can not work with current
		// architecture so should not enable this feature until solution is
		// found
		boolean enableParallelTestRunning = false;
		if (enableParallelTestRunning) {
			runParallelThread(scenarioList, context);
		} else {
			runSingleThread(scenarioList, context);
		}

		// Print Test results
		StringBuilder sb = new StringBuilder();
		// sb.append("\n");
		sb.append(FWStaticStore.ARTOS_LINE_BREAK_1);
		sb.append("\n");
		sb.append("[Scenarios] ");
		sb.append("EXECUTED:" + String.format("%-" + 4 + "s", context.getTotalTestCount()));
		sb.append(" PASS:" + String.format("%-" + 4 + "s", context.getCurrentPassCount()));
		sb.append(" SKIP:" + String.format("%-" + 4 + "s", context.getCurrentSkipCount()));
		sb.append(" KTF:" + String.format("%-" + 4 + "s", context.getCurrentKTFCount()));
		sb.append(" FAIL:" + String.format("%-" + 4 + "s", context.getCurrentFailCount()));
		// Total does not make sense because parameterised test cases are considered as
		// a test case
		// sb.append(" TOTAL:" + transformedTestList.size());
		sb.append(" [");
		sb.append("FATAL:" + String.format("%-" + 4 + "s", context.getTotalFatalCount()));
		sb.append(" CRITICAL:" + String.format("%-" + 4 + "s", context.getTotalCriticalCount()));
		sb.append(" HIGH:" + String.format("%-" + 4 + "s", context.getTotalHighCount()));
		sb.append(" MEDIUM:" + String.format("%-" + 4 + "s", context.getTotalMediumCount()));
		sb.append(" LOW:" + String.format("%-" + 4 + "s", context.getTotalLowCount()));
		sb.append(" UNDEFINED:" + String.format("%-" + 4 + "s", context.getTotalUndefinedCount()));
		sb.append("]");

		PrintTotalUnitResult(scenarioList, sb);

		// Print Test suite Start and Finish time
		String startTimeStamp = new Transform().MilliSecondsToFormattedDate("dd-MM-yyyy hh:mm:ss",
				context.getTestSuiteStartTime());
		String finishTimeStamp = new Transform().MilliSecondsToFormattedDate("dd-MM-yyyy hh:mm:ss",
				context.getTestSuiteFinishTime());
		sb.append("\n\n");
		sb.append("Test start time : " + startTimeStamp);
		sb.append("\n");
		sb.append("Test finish time : " + finishTimeStamp);
		sb.append("\n");
		sb.append("Test duration : "
				+ String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes(context.getTestSuiteTimeDuration()),
						TimeUnit.MILLISECONDS.toSeconds(context.getTestSuiteTimeDuration()) - TimeUnit.MINUTES
								.toSeconds(TimeUnit.MILLISECONDS.toMinutes(context.getTestSuiteTimeDuration()))));

		// Print Test suite summary
		logger.info(sb.toString());
		context.getLogger().getSummaryLogger().info(sb.toString());
		notifyTestSuiteSummaryPrinting(sb.toString());

		// HighLight Failed Test Cases
		highlightFailure(scenarioList);

		// to release a thread lock
		context.getThreadLatch().countDown();
	}

	private void PrintTotalUnitResult(List<BDDScenario> scenarioList, StringBuilder sb) {
		// Print Test results
		sb.append("\n");
		sb.append("[TestSteps] ");
		sb.append("EXECUTED:" + String.format("%-" + 4 + "s", context.getTotalUnitTestCount()));
		sb.append(" PASS:" + String.format("%-" + 4 + "s", context.getCurrentUnitPassCount()));
		sb.append(" SKIP:" + String.format("%-" + 4 + "s", context.getCurrentUnitSkipCount()));
		sb.append(" KTF:" + String.format("%-" + 4 + "s", context.getCurrentUnitKTFCount()));
		sb.append(" FAIL:" + String.format("%-" + 4 + "s", context.getCurrentUnitFailCount()));
		sb.append(" [");
		sb.append("FATAL:" + String.format("%-" + 4 + "s", context.getTotalUnitFatalCount()));
		sb.append(" CRITICAL:" + String.format("%-" + 4 + "s", context.getTotalUnitCriticalCount()));
		sb.append(" HIGH:" + String.format("%-" + 4 + "s", context.getTotalUnitHighCount()));
		sb.append(" MEDIUM:" + String.format("%-" + 4 + "s", context.getTotalUnitMediumCount()));
		sb.append(" LOW:" + String.format("%-" + 4 + "s", context.getTotalUnitLowCount()));
		sb.append(" UNDEFINED:" + String.format("%-" + 4 + "s", context.getTotalUnitUndefinedCount()));
		sb.append("]");
	}

	/**
	 * Highlight failed test cases at the end of test execution
	 * 
	 * @param scenarioList list of test cases
	 */
	private void highlightFailure(List<BDDScenario> scenarioList) {

		if (context.getCurrentFailCount() > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append(FWStaticStore.ARTOS_LINE_BREAK_1);
			sb.append("\n");
			sb.append(" FAILED TEST SCENARIOS (" + context.getCurrentFailCount() + ")");
			sb.append("\n\n");
			sb.append(FWStaticStore.ARTOS_LINE_BREAK_1);

			int testErrorcount = 0;
			for (BDDScenario scenario : scenarioList) {

				/*
				 * If stopOnFail=true then test cases after first failure will not be executed
				 * which means TestOutcomeList will be empty
				 */
				if (scenario.getTestOutcomeList().isEmpty()) {
					continue;
				}

				if (scenario.getTestOutcomeList().get(0) == TestStatus.FAIL) {
					testErrorcount++;
					sb.append("\n");
					sb.append(String.format("%-4s%s", testErrorcount, scenario.getScenarioDescription()));
					sb.append(scenario.getTestImportance() == Importance.UNDEFINED ? ""
							: " [" + scenario.getTestImportance().name() + "]");

					for (BDDStep step : scenario.getSteplist()) {

						TestUnitObjectWrapper unit = step.getUnit();
						/*
						 * If stopOnFail=true then test unit after first failure will not be executed
						 * which means TestUnitOutcomeList will be empty
						 */
						if (unit.getTestUnitOutcomeList().isEmpty()) {
							continue;
						}

						// If test case is without date provider
						if ("".equals(unit.getDataProviderName())
								&& unit.getTestUnitOutcomeList().get(0) == TestStatus.FAIL) {
							sb.append(String.format("\n"));
							sb.append(String.format("\t|-- %s", unit.getTestUnitMethod().getName() + "(context)"));
							sb.append(unit.getTestImportance() == Importance.UNDEFINED ? ""
									: " [" + unit.getTestImportance().name() + "]");

							// If test case with data provider then go through each status of the list
						} else if (!"".equals(unit.getDataProviderName())) {
							for (int j = 0; j < unit.getTestUnitOutcomeList().size(); j++) {
								if (unit.getTestUnitOutcomeList().get(j) == TestStatus.FAIL) {
									sb.append(String.format("\n"));
									sb.append(String.format("\t|-- %s", unit.getTestUnitMethod().getName() + "(context)"
											+ " : DataProvider[" + j + "]"));
									sb.append(unit.getTestImportance() == Importance.UNDEFINED ? ""
											: " [" + unit.getTestImportance().name() + "]");
								}
							}
						}

					}
				}
			}

			sb.append(String.format("\n"));
			sb.append(String.format(FWStaticStore.ARTOS_LINE_BREAK_1));
			System.err.println(sb.toString());
			notifyTestSuiteFailureHighlight(sb.toString());
		}
	}

	private void runSingleThread(List<BDDScenario> scenarioList, TestContext context)
			throws InstantiationException, IllegalAccessException, Exception {
		// ********************************************************************************************
		// TestSuite Start
		// ********************************************************************************************
		notifyTestSuiteExecutionStarted(context.getPrePostRunnableObj().getName());
		context.setTestSuiteStartTime(System.currentTimeMillis());
		ScanTestSuite scan = new ScanTestSuite();
		scan.scanForBeforeAfterMethods(context);

		try {

			// Run prior to each test suite
			if (null != context.getBeforeTestSuite()) {
				notifyBeforeTestSuiteMethodExecutionStarted(context.getBeforeTestSuite().getName(),
						context.getPrePostRunnableObj().getName());
				context.getBeforeTestSuite()
						.invoke(context.getPrePostRunnableObj().getDeclaredConstructor().newInstance(), context);
				notifyBeforeTestSuiteMethodExecutionFinished(context.getPrePostRunnableObj().getName());
			}

			int loopCount = context.getTestSuite().getLoopCount();

			// Run as many loop set via test script or main method
			for (int index = 0; index < loopCount; index++) {
				notifyTestExecutionLoopCount(index);
				// --------------------------------------------------------------------------------------------
				// Go through each test case and execute it
				for (BDDScenario scenario : scenarioList) {

					// reset parameterised index
					context.setTestParameterIndex(0);

					// If "stop on fail" is enabled then stop test execution
					if (FWStaticStore.frameworkConfig.isStopOnFail()) {
						if (context.getCurrentFailCount() > 0) {
							break;
						}
					}

					// Print test case header and test plan in the log file
					context.setCurrentTestScenario(scenario);
					notifyPrintTestPlan(scenario);

					notifyTestCaseExecutionStarted(scenario);
					// if global data table is not specified
					if (null == scenario.getGlobalDataTable() || scenario.getGlobalDataTable().isEmpty()) {
						runIndividualTest(scenario);
					} else { // if data provider is specified
						runParameterizedTest(scenario);
					}
					notifyTestCaseExecutionFinished(scenario);

				}
				// --------------------------------------------------------------------------------------------
			}

			// Run at the end of each test suit
			if (null != context.getAfterTestSuite()) {
				notifyAfterTestSuiteMethodExecutionStarted(context.getAfterTestSuite().getName(),
						context.getPrePostRunnableObj().getName());
				context.getAfterTestSuite()
						.invoke(context.getPrePostRunnableObj().getDeclaredConstructor().newInstance(), context);
				notifyAfterTestSuiteMethodExecutionFinished(context.getPrePostRunnableObj().getName());
			}

		} catch (Throwable e) {
			// Catch InvocationTargetException and return cause
			if (null == e.getCause()) {
				// Handle if any exception in pre-post runnable
				UtilsFramework.writePrintStackTrace(context, e);
				notifyTestSuiteException(e);
			} else {
				// Handle if any exception in pre-post runnable
				UtilsFramework.writePrintStackTrace(context, e.getCause());
				notifyTestSuiteException(e.getCause());
			}
		}

		// Set Test Finish Time
		context.setTestSuiteFinishTime(System.currentTimeMillis());
		notifyTestSuiteExecutionFinished(context.getPrePostRunnableObj().getName());
		// ********************************************************************************************
		// TestSuite Finish
		// ********************************************************************************************
	}

	/**
	 * Responsible for execution individual test scenario
	 * 
	 * @param scenario TestCase in format {@code TestObjectWrapper}
	 */
	private void runIndividualTest(BDDScenario scenario) {

		try {
			// Run Pre Method prior to any scenario Execution
			if (null != context.getBeforeTest()) {
				notifyGlobalBeforeTestCaseMethodExecutionStarted(context.getBeforeTest().getName(), scenario);
				context.getBeforeTest().invoke(context.getPrePostRunnableObj().getDeclaredConstructor().newInstance(),
						context);
				notifyGlobalBeforeTestCaseMethodExecutionFinished(scenario);
			}
		} catch (Throwable e) {
			if (e.getClass() == InvocationTargetException.class) {
				// Catch InvocationTargetException and return cause
				UtilsFramework.writePrintStackTrace(context, e.getCause());
			} else {
				UtilsFramework.writePrintStackTrace(context, e);
			}
		}

		// ********************************************************************************************
		// TestScenario Start
		// ********************************************************************************************

		try {

			scenario.setTestStartTime(System.currentTimeMillis());

			runSimpleTest(scenario);

		} catch (Throwable e) {
			context.setTestStatus(TestStatus.FAIL, e.getMessage());
			UtilsFramework.writePrintStackTrace(context, e);
			notifyTestException(e);
		} finally {
			scenario.setTestFinishTime(System.currentTimeMillis());
		}

		// ********************************************************************************************
		// TestScenario Finish
		// ********************************************************************************************

		try {
			// Run Post Method prior to any test Execution
			if (null != context.getAfterTest()) {
				notifyGlobalAfterTestCaseMethodExecutionStarted(context.getAfterTest().getName(), scenario);
				context.getAfterTest().invoke(context.getPrePostRunnableObj().getDeclaredConstructor().newInstance(),
						context);
				notifyGlobalAfterTestCaseMethodExecutionFinished(scenario);
			}
		} catch (Throwable e) {
			if (e.getClass() == InvocationTargetException.class) {
				// Catch InvocationTargetException and return cause
				UtilsFramework.writePrintStackTrace(context, e.getCause());
			} else {
				UtilsFramework.writePrintStackTrace(context, e);
			}
		}

		// ********************************************************************************************
		// Generate Summary
		// ********************************************************************************************
		context.generateTestScenarioSummary(scenario);
	}

	/**
	 * Responsible for executing data provider method which upon successful
	 * execution returns an array of parameters. TestCase will be re-run using all
	 * parameters available in the parameter array. If data provider method returns
	 * empty array or null then test case will be executed only once with null
	 * arguments.
	 * 
	 * @param scenario TestCase in format {@code TestObjectWrapper}
	 */
	private void runParameterizedTest(BDDScenario scenario) {

		try {

			// Find the length of dataList, which will tell us how many time to iterate
			// child test cases
			List<String> firstSet = scenario.getGlobalDataTable().values().iterator().next();
			for (int i = 0; i < firstSet.size(); i++) {

				// Create Empty Map every iteration
				context.setStepParameter(new HashMap<>());
				// set current iteration index
				context.setTestParameterIndex(i);
				// execute child scenario
				executeChildTest(scenario);
			}

		} catch (Exception e) {
			// Print Exception
			UtilsFramework.writePrintStackTrace(context, e);
			notifyTestSuiteException(e);

			// Mark current test as fail due to exception during data provider processing
			context.setTestStatus(TestStatus.FAIL, e.getMessage());
			context.generateTestScenarioSummary(scenario);
		}
	}

	/**
	 * Responsible for execution of a test case.
	 * 
	 * @param scenario TestCase in format {@code TestObjectWrapper}
	 * @throws Exception Exception during test execution
	 */
	private void runSimpleTest(BDDScenario scenario) throws Exception {
		// --------------------------------------------------------------------------------------------

		// Run Unit tests (This is if test suite have unit tests)
		new BDDRunnerTestSteps(context, listenerList).runSingleThreadSteps(scenario);

		// --------------------------------------------------------------------------------------------
	}

	/**
	 * Responsible for execution of test cases (Considered as child test case) with
	 * given parameter. Parameterised object array index and value(s) class type(s)
	 * will be printed prior to test execution for user's benefit.
	 * 
	 * @param scenario TestCase in format {@code TestObjectWrapper}
	 * @param data     Array of parameters
	 */
	private void executeChildTest(BDDScenario scenario) {

		String userInfo = "DataTable(" + context.getTestParameterIndex() + ")";
		context.getLogger().info(userInfo);
		//
		// // Get StepParameterMap, at this stage it
		// Map<String, String> stepParameter = context.getStepParameterMap();
		//
		// // Add global table parameters to Map
		// for (Entry<String, List<String>> entry :
		// scenario.getGlobalDataTable().entrySet()) {
		// String key = entry.getKey();
		// List<String> value = entry.getValue();
		// stepParameter.put(key, value.get(context.getTestParameterIndex()));
		// }

		// ********************************************************************************************
		// Parameterised Child TestCase Start
		// ********************************************************************************************

		notifyChildTestCaseExecutionStarted(scenario, userInfo);

		runIndividualTest(scenario);

		notifyChildTestCaseExecutionFinished(scenario);

		// ********************************************************************************************
		// Parameterised Child TestCase Finish
		// ********************************************************************************************
	}

	private void runParallelThread(List<BDDScenario> testScenarioList, TestContext context)
			throws InstantiationException, IllegalAccessException, Exception {
		// ********************************************************************************************
		// Test Start
		// ********************************************************************************************

		// ********************************************************************************************
		// Test Finish
		// ********************************************************************************************
	}

	// ==================================================================================
	// Register, deRegister and Notify Event Listeners
	// ==================================================================================

	/**
	 * register new {@link TestProgress} listener
	 * 
	 * @param listener {@link TestProgress} listener
	 */
	protected void registerListener(TestProgress listener) {
		listenerList.add(listener);
	}

	/**
	 * De-register {@link TestProgress} listener
	 * 
	 * @param listener {@link TestProgress} listener
	 */
	protected void deRegisterListener(TestProgress listener) {
		listenerList.remove(listener);
	}

	/**
	 * De-register all listeners
	 */
	protected void deRegisterAllListener() {
		listenerList.clear();
	}

	void notifyBeforeTestSuiteMethodExecutionStarted(String methodName, String testSuiteName) {
		for (TestProgress listener : listenerList) {
			listener.beforeTestSuiteMethodExecutionStarted(methodName, testSuiteName);
		}
	}

	void notifyBeforeTestSuiteMethodExecutionFinished(String testSuiteName) {
		for (TestProgress listener : listenerList) {
			listener.beforeTestSuiteMethodExecutionFinished(testSuiteName);
		}
	}

	void notifyAfterTestSuiteMethodExecutionStarted(String methodName, String testSuiteName) {
		for (TestProgress listener : listenerList) {
			listener.afterTestSuiteMethodExecutionStarted(methodName, testSuiteName);
		}
	}

	void notifyAfterTestSuiteMethodExecutionFinished(String testSuiteName) {
		for (TestProgress listener : listenerList) {
			listener.afterTestSuiteMethodExecutionFinished(testSuiteName);
		}
	}

	void notifyTestSuiteExecutionStarted(String testSuiteName) {
		for (TestProgress listener : listenerList) {
			listener.testSuiteExecutionStarted(testSuiteName);
		}
	}

	void notifyTestSuiteExecutionFinished(String testSuiteName) {
		for (TestProgress listener : listenerList) {
			listener.testSuiteExecutionFinished(testSuiteName);
		}
	}

	void notifyPrintTestPlan(BDDScenario scenario) {
		for (TestProgress listener : listenerList) {
			listener.printTestPlan(scenario);
		}
	}

	void notifyGlobalBeforeTestCaseMethodExecutionStarted(String methodName, BDDScenario scenario) {
		for (TestProgress listener : listenerList) {
			listener.globalBeforeTestCaseMethodExecutionStarted(methodName, scenario);
		}
	}

	void notifyGlobalBeforeTestCaseMethodExecutionFinished(BDDScenario scenario) {
		for (TestProgress listener : listenerList) {
			listener.globalBeforeTestCaseMethodExecutionFinished(scenario);
		}
	}

	void notifyGlobalAfterTestCaseMethodExecutionStarted(String methodName, BDDScenario scenario) {
		for (TestProgress listener : listenerList) {
			listener.globalAfterTestCaseMethodExecutionStarted(methodName, scenario);
		}
	}

	void notifyGlobalAfterTestCaseMethodExecutionFinished(BDDScenario scenario) {
		for (TestProgress listener : listenerList) {
			listener.globalAfterTestCaseMethodExecutionFinished(scenario);
		}
	}

	void notifyTestCaseExecutionStarted(BDDScenario scenario) {
		for (TestProgress listener : listenerList) {
			listener.testCaseExecutionStarted(scenario);
		}
	}

	void notifyTestCaseExecutionFinished(BDDScenario scenario) {
		for (TestProgress listener : listenerList) {
			listener.testCaseExecutionFinished(scenario);
		}
	}

	void notifyChildTestCaseExecutionStarted(BDDScenario scenario, String userInfo) {
		for (TestProgress listener : listenerList) {
			listener.childTestCaseExecutionStarted(scenario, userInfo);
		}
	}

	void notifyChildTestCaseExecutionFinished(BDDScenario scenario) {
		for (TestProgress listener : listenerList) {
			listener.childTestCaseExecutionFinished(scenario);
		}
	}

	// void notifyTestCaseExecutionSkipped(TestScenario scenario) {
	// for (TestProgress listener : listenerList) {
	// listener.testCaseExecutionSkipped(scenario);
	// }
	// }

	void notifyTestExecutionLoopCount(int count) {
		for (TestProgress listener : listenerList) {
			listener.testExecutionLoopCount(count);
		}
	}

	void notifyTestSuiteException(Throwable e) {
		for (TestProgress listener : listenerList) {
			listener.testSuiteException(e);
		}
	}

	void notifyTestException(Throwable e) {
		for (TestProgress listener : listenerList) {
			listener.testException(e);
		}
	}

	void notifyTestSuiteSummaryPrinting(String description) {
		for (TestProgress listener : listenerList) {
			listener.testSuiteSummaryPrinting(description);
		}
	}

	void notifyTestSuiteFailureHighlight(String description) {
		for (TestProgress listener : listenerList) {
			listener.testSuiteFailureHighlight(description);
		}
	}
}