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
package com.artos.framework.listener;

import java.io.File;

import com.artos.framework.Enums.TestStatus;
import com.artos.framework.Version;
import com.artos.framework.infra.BDDScenario;
import com.artos.framework.infra.BDDStep;
import com.artos.framework.infra.LogWrapper;
import com.artos.framework.infra.TestContext;
import com.artos.framework.infra.TestObjectWrapper;
import com.artos.framework.infra.TestUnitObjectWrapper;
import com.artos.interfaces.TestProgress;
import com.artos.utils.UtilsFramework;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

public class ExtentReportListener implements TestProgress {

	TestContext context;
	LogWrapper logger;
	ExtentReports extent = null;
	ExtentTest testParent;
	ExtentTest testChild;
	ExtentTest testChildOfChild;

	/**
	 * Constructor
	 * 
	 * @param context {@link TestContext}
	 */
	public ExtentReportListener(TestContext context) {
		this.context = context;
		this.logger = context.getLogger();
	}

	@Override
	public void testSuiteExecutionStarted(String description) {
		extent = logger.getExtent();
		// extent.config().insertJs("$('.test.warning').each(function() {
		// $(this).addClass('pass').removeClass('warning'); });
		// $('.test-status.warning').each(function() {
		// $(this).addClass('pass').removeClass('warning').text('pass');
		// });$('.tests-quick-view
		// .status.warning').each(function() {
		// $(this).addClass('pass').removeClass('warning').text('PASS'); });
		// testSetChart(); ");
		extent.addSystemInfo("Artos Version", new Version().getBuildVersion());
	}

	@Override
	public void testSuiteExecutionFinished(String description) {
		extent.flush();
		extent.close();
	}

	@Override
	public void testCaseExecutionStarted(TestObjectWrapper t) {
		testParent = extent.startTest(t.getTestClassObject().getName(),
				"".equals(t.getTestPlanDescription()) ? t.getTestPlanBDD() : t.getTestPlanDescription());
		testParent.assignAuthor(t.getTestPlanPreparedBy());
		if (t.getGroupList().size() == 1) {
			testParent.assignCategory(t.getGroupList().get(0));
		} else if (t.getGroupList().size() == 2) {
			testParent.assignCategory(t.getGroupList().get(0), t.getGroupList().get(1));
		} else if (t.getGroupList().size() == 3) {
			testParent.assignCategory(t.getGroupList().get(0), t.getGroupList().get(1), t.getGroupList().get(2));
		} else if (t.getGroupList().size() == 4) {
			testParent.assignCategory(t.getGroupList().get(0), t.getGroupList().get(1), t.getGroupList().get(2),
					t.getGroupList().get(3));
		} else if (t.getGroupList().size() == 5) {
			testParent.assignCategory(t.getGroupList().get(0), t.getGroupList().get(1), t.getGroupList().get(2),
					t.getGroupList().get(3), t.getGroupList().get(4));
		}
	}

	@Override
	public void testCaseExecutionStarted(BDDScenario scenario) {
		testParent = extent.startTest("Scenario: " + scenario.getScenarioDescription(),
				scenario.getScenarioDescription());
		// testParent.assignAuthor("");
	}

	@Override
	public void childTestCaseExecutionStarted(TestObjectWrapper t, String paramInfo) {
		testChild = extent.startTest(paramInfo, t.getTestPlanDescription());
		testChild.assignAuthor(t.getTestPlanPreparedBy());
	}

	@Override
	public void childTestCaseExecutionStarted(BDDScenario scenario, String paramInfo) {
		testChild = extent.startTest(paramInfo, scenario.getScenarioDescription());
		testChild.assignAuthor("");
	}

	@Override
	public void testCaseExecutionFinished(TestObjectWrapper t) {
		extent.endTest(testParent);
		testParent = null;
	}

	@Override
	public void testCaseExecutionFinished(BDDScenario scenario) {
		extent.endTest(testParent);
		testParent = null;
	}

	@Override
	public void childTestCaseExecutionFinished(TestObjectWrapper t) {
		// add child to parent
		testParent.appendChild(testChild);
		extent.endTest(testChild);
		testChild = null;
	}

	@Override
	public void childTestCaseExecutionFinished(BDDScenario scenario) {
		// add child to parent
		testParent.appendChild(testChild);
		extent.endTest(testChild);
		testChild = null;
	}

	@Override
	public void testCaseExecutionSkipped(TestObjectWrapper t) {
		testParent.log(LogStatus.SKIP, "Skipped Test Case: " + t.getTestClassObject().getName());
	}

	@Override
	public void testExecutionLoopCount(int count) {
	}

	public void testCaseStatusUpdate(TestStatus testStatus, File snapshot, String description) {
		if (null != testChildOfChild) {
			description = (null == snapshot ? description
					: description + testChildOfChild.addScreenCapture(snapshot.getAbsolutePath()));
			if (TestStatus.FAIL == testStatus) {
				testChildOfChild.log(LogStatus.FAIL, description);
			} else if (TestStatus.KTF == testStatus) {
				// Extent do not have KTF status so add it to warning
				testChildOfChild.log(LogStatus.WARNING, description);
			} else if (TestStatus.SKIP == testStatus) {
				testChildOfChild.log(LogStatus.SKIP, description);
			} else if (TestStatus.PASS == testStatus) {
				testChildOfChild.log(LogStatus.PASS, description);
			}
		} else if (null != testChild) {
			description = (null == snapshot ? description
					: description + testChild.addScreenCapture(snapshot.getAbsolutePath()));
			if (TestStatus.FAIL == testStatus) {
				testChild.log(LogStatus.FAIL, description);
			} else if (TestStatus.KTF == testStatus) {
				// Extent do not have KTF status so add it to warning
				testChild.log(LogStatus.WARNING, description);
			} else if (TestStatus.SKIP == testStatus) {
				testChild.log(LogStatus.SKIP, description);
			} else if (TestStatus.PASS == testStatus) {
				testChild.log(LogStatus.PASS, description);
			}
		} else if (null != testParent) {
			description = (null == snapshot ? description
					: description + testParent.addScreenCapture(snapshot.getAbsolutePath()));
			if (TestStatus.FAIL == testStatus) {
				testParent.log(LogStatus.FAIL, description);
			} else if (TestStatus.KTF == testStatus) {
				// Extent do not have KTF status so add it to warning
				testParent.log(LogStatus.WARNING, description);
			} else if (TestStatus.SKIP == testStatus) {
				testParent.log(LogStatus.SKIP, description);
			} else if (TestStatus.PASS == testStatus) {
				testParent.log(LogStatus.PASS, description);
			}
		}
	}

	@Override
	public void testResult(TestObjectWrapper t, TestStatus testStatus, File snapshot, String description) {
		if (null != testChildOfChild) {
			description = (null == snapshot ? description
					: description + testChildOfChild.addScreenCapture(snapshot.getAbsolutePath()));
			if (TestStatus.FAIL == testStatus) {
				testChildOfChild.log(LogStatus.FAIL, description);
			} else if (TestStatus.KTF == testStatus) {
				// Extent do not have KTF status so add it to warning
				testChildOfChild.log(LogStatus.WARNING, description);
			} else if (TestStatus.SKIP == testStatus) {
				testChildOfChild.log(LogStatus.SKIP, description);
			} else if (TestStatus.PASS == testStatus) {
				testChildOfChild.log(LogStatus.PASS, description);
			}
		} else if (null != testChild) {
			description = (null == snapshot ? description
					: description + testChild.addScreenCapture(snapshot.getAbsolutePath()));
			if (TestStatus.FAIL == testStatus) {
				testChild.log(LogStatus.FAIL, description);
			} else if (TestStatus.KTF == testStatus) {
				// Extent do not have KTF status so add it to warning
				testChild.log(LogStatus.WARNING, description);
			} else if (TestStatus.SKIP == testStatus) {
				testChild.log(LogStatus.SKIP, description);
			} else if (TestStatus.PASS == testStatus) {
				testChild.log(LogStatus.PASS, description);
			}
		} else if (null != testParent) {
			description = (null == snapshot ? description
					: description + testParent.addScreenCapture(snapshot.getAbsolutePath()));
			if (TestStatus.FAIL == testStatus) {
				testParent.log(LogStatus.FAIL, description);
			} else if (TestStatus.KTF == testStatus) {
				// Extent do not have KTF status so add it to warning
				testParent.log(LogStatus.WARNING, description);
			} else if (TestStatus.SKIP == testStatus) {
				testParent.log(LogStatus.SKIP, description);
			} else if (TestStatus.PASS == testStatus) {
				testParent.log(LogStatus.PASS, description);
			}
		}
	}

	@Override
	public void beforeTestSuiteMethodExecutionStarted(String methodName, String description) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeTestSuiteMethodExecutionFinished(String description) {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterTestSuiteMethodExecutionStarted(String methodName, String description) {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterTestSuiteMethodExecutionFinished(String description) {
		// TODO Auto-generated method stub

	}

	@Override
	public void printTestPlan(TestObjectWrapper t) {
		// TODO Auto-generated method stub

	}

	@Override
	public void printTestPlan(BDDScenario sc) {
		// TODO Auto-generated method stub

	}

	@Override
	public void localBeforeTestCaseMethodExecutionStarted(String methodName, TestObjectWrapper t) {
		// TODO Auto-generated method stub

	}

	@Override
	public void localBeforeTestCaseMethodExecutionFinished(TestObjectWrapper t) {
		// TODO Auto-generated method stub

	}

	@Override
	public void localAfterTestCaseMethodExecutionStarted(String methodName, TestObjectWrapper t) {
		// TODO Auto-generated method stub

	}

	@Override
	public void localAfterTestCaseMethodExecutionFinished(TestObjectWrapper t) {
		// TODO Auto-generated method stub

	}

	@Override
	public void globalBeforeTestCaseMethodExecutionStarted(String methodName, TestObjectWrapper t) {
		// TODO Auto-generated method stub

	}

	@Override
	public void globalBeforeTestCaseMethodExecutionStarted(String methodName, BDDScenario scenario) {
		// TODO Auto-generated method stub

	}

	@Override
	public void globalBeforeTestCaseMethodExecutionFinished(TestObjectWrapper t) {
		// TODO Auto-generated method stub

	}

	@Override
	public void globalBeforeTestCaseMethodExecutionFinished(BDDScenario scenario) {
		// TODO Auto-generated method stub

	}

	@Override
	public void globalAfterTestCaseMethodExecutionStarted(String methodName, TestObjectWrapper t) {
		// TODO Auto-generated method stub

	}

	@Override
	public void globalAfterTestCaseMethodExecutionStarted(String methodName, BDDScenario scenario) {
		// TODO Auto-generated method stub

	}

	@Override
	public void globalAfterTestCaseMethodExecutionFinished(TestObjectWrapper t) {
		// TODO Auto-generated method stub

	}

	@Override
	public void globalAfterTestCaseMethodExecutionFinished(BDDScenario scenario) {
		// TODO Auto-generated method stub

	}

	@Override
	public void globalAfterFailedUnitMethodExecutionStarted(String methodName, TestUnitObjectWrapper unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void globalAfterFailedUnitMethodExecutionStarted(String methodName, BDDStep step) {
		// TODO Auto-generated method stub

	}

	@Override
	public void globalAfterFailedUnitMethodExecutionFinished(TestUnitObjectWrapper unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void globalAfterFailedUnitMethodExecutionFinished(BDDStep step) {
		// TODO Auto-generated method stub

	}

	@Override
	public void testSuiteException(Throwable e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void testException(Throwable e) {
		String exceptionString = UtilsFramework.getPrintStackTraceAsString(e).replace("\n", "<p/>");
		if (null != testChildOfChild) {
			testChildOfChild.log(LogStatus.FAIL, exceptionString);
		} else if (null != testChild) {
			testChild.log(LogStatus.FAIL, exceptionString);
		} else if (null != testParent) {
			testParent.log(LogStatus.FAIL, exceptionString);
		}
	}

	@Override
	public void unitException(Throwable e) {
		String exceptionString = UtilsFramework.getPrintStackTraceAsString(e).replace("\n", "<p/>");
		if (null != testChildOfChild) {
			testChildOfChild.log(LogStatus.FAIL, exceptionString);
		} else if (null != testChild) {
			testChild.log(LogStatus.FAIL, exceptionString);
		} else if (null != testParent) {
			testParent.log(LogStatus.FAIL, exceptionString);
		}
	}

	@Override
	public void testSuiteSummaryPrinting(String description) {
		// TODO Auto-generated method stub

	}

	@Override
	public void testSuiteFailureHighlight(String description) {
		// TODO Auto-generated method stub

	}

	@Override
	public void globalBeforeTestUnitMethodExecutionStarted(String methodName, TestUnitObjectWrapper unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void globalBeforeTestUnitMethodExecutionStarted(String methodName, BDDStep step) {
		// TODO Auto-generated method stub

	}

	@Override
	public void globalBeforeTestUnitMethodExecutionFinished(TestUnitObjectWrapper unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void globalBeforeTestUnitMethodExecutionFinished(BDDStep step) {
		// TODO Auto-generated method stub

	}

	@Override
	public void localBeforeTestUnitMethodExecutionStarted(TestObjectWrapper t, TestUnitObjectWrapper unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void localBeforeTestUnitMethodExecutionFinished(TestUnitObjectWrapper unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void globalAfterTestUnitMethodExecutionStarted(String methodName, TestUnitObjectWrapper unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void globalAfterTestUnitMethodExecutionStarted(String methodName, BDDStep step) {
		// TODO Auto-generated method stub

	}

	@Override
	public void globalAfterTestUnitMethodExecutionFinished(TestUnitObjectWrapper unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void globalAfterTestUnitMethodExecutionFinished(BDDStep step) {
		// TODO Auto-generated method stub

	}

	@Override
	public void localAfterTestUnitMethodExecutionStarted(TestObjectWrapper t, TestUnitObjectWrapper unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void localAfterTestUnitMethodExecutionFinished(TestUnitObjectWrapper unit) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void localAfterFailedUnitMethodExecutionStarted(TestObjectWrapper t, TestUnitObjectWrapper unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void localAfterFailedUnitMethodExecutionFinished(TestUnitObjectWrapper unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void testUnitExecutionStarted(TestUnitObjectWrapper unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void testUnitExecutionStarted(BDDStep step) {
		// TODO Auto-generated method stub

	}

	@Override
	public void testUnitExecutionFinished(TestUnitObjectWrapper unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void testUnitExecutionFinished(BDDStep step) {
		// TODO Auto-generated method stub

	}

	@Override
	public void childTestUnitExecutionStarted(TestObjectWrapper t, TestUnitObjectWrapper unit, String paramInfo) {
		/*
		 * Parameterised test case at test case level and test unit level call this
		 * function
		 */
		if (null == testChild) {
			testChild = extent.startTest(paramInfo,
					"".equals(unit.getTestPlanDescription()) ? unit.getTestPlanBDD() : unit.getTestPlanDescription());
			// testChild.assignAuthor("");
		}
	}

	@Override
	public void childTestUnitExecutionStarted(BDDScenario scenario, BDDStep step, String paramInfo) {
		// If Child test case is not present (Because test case is running as
		// non-parameterised test cases) then create new child test case in extent
		if (null == testChild) {
			testChild = extent.startTest(paramInfo, step.getStepAction() + " " + step.getStepDescription());
			// testChild.assignAuthor("");

			// If Child test case is present (Because test case is running as Parameterised
			// test cases) then create new childOfchild test case in
			// extent
		} else {
			testChildOfChild = extent.startTest(paramInfo, step.getStepAction() + " " + step.getStepDescription());
			// testChildOfChild.assignAuthor("");
		}
	}

	@Override
	public void childTestUnitExecutionFinished(TestUnitObjectWrapper unit) {
		if (null == testChildOfChild) {
			// add child to parent
			testParent.appendChild(testChild);
			extent.endTest(testChild);
			testChild = null;
		} else {
			// add child to parent
			testChild.appendChild(testChildOfChild);
			extent.endTest(testChildOfChild);
			testChildOfChild = null;
		}
	}

	@Override
	public void childTestUnitExecutionFinished(BDDStep step) {
		if (null == testChildOfChild) {
			// add child to parent
			testParent.appendChild(testChild);
			extent.endTest(testChild);
			testChild = null;
		} else {
			// add child to parent
			testChild.appendChild(testChildOfChild);
			extent.endTest(testChildOfChild);
			testChildOfChild = null;
		}
	}

	@Override
	public void printTestUnitPlan(TestUnitObjectWrapper unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void printTestUnitPlan(BDDStep step) {
		// TODO Auto-generated method stub

	}

	@Override
	public void testCaseSummaryPrinting(String FQCN, String description) {
		// TODO Auto-generated method stub

	}

	@Override
	public void testUnitSummaryPrinting(String FQCN, String description) {
		// TODO Auto-generated method stub

	}

	@Override
	public void testResult(BDDScenario scenario, TestStatus testStatus, File snapshot, String description) {
		// TODO Auto-generated method stub

	}

	@Override
	public void testUnitResult(TestUnitObjectWrapper unit, TestStatus testStatus, File snapshot, String description) {
		// TODO Auto-generated method stub

	}

	@Override
	public void testUnitResult(BDDStep step, TestStatus testStatus, File snapshot, String description) {
		// TODO Auto-generated method stub

	}

}
