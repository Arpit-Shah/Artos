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

import com.artos.framework.Enums.TestStatus;
import com.artos.framework.TestObjectWrapper;
import com.artos.framework.TestUnitObjectWrapper;
import com.artos.framework.Version;
import com.artos.framework.infra.LogWrapper;
import com.artos.framework.infra.TestContext;
import com.artos.interfaces.TestProgress;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

public class ExtentReportListener implements TestProgress {

	TestContext context;
	LogWrapper logger;
	ExtentReports extent = null;
	ExtentTest testParent;
	ExtentTest testChild;

	public ExtentReportListener(TestContext context) {
		this.context = context;
		this.logger = context.getLogger();
	}

	@Override
	public void testSuiteExecutionStarted(String description) {
		extent = logger.getExtent();
		// extent.config().insertJs("$('.test.warning').each(function() { $(this).addClass('pass').removeClass('warning'); });
		// $('.test-status.warning').each(function() { $(this).addClass('pass').removeClass('warning').text('pass'); });$('.tests-quick-view
		// .status.warning').each(function() { $(this).addClass('pass').removeClass('warning').text('PASS'); }); testSetChart(); ");
		extent.addSystemInfo("Artos Version", new Version().getBuildVersion());
	}

	@Override
	public void testSuiteExecutionFinished(String description) {
		extent.flush();
		extent.close();
	}

	@Override
	public void testCaseExecutionStarted(TestObjectWrapper t) {
		testParent = extent.startTest(t.getTestClassObject().getName(), t.getTestPlanDescription());
		testParent.assignAuthor(t.getTestPlanPreparedBy());
	}

	@Override
	public void childTestCaseExecutionStarted(TestObjectWrapper t, String paramInfo) {
		testChild = extent.startTest(paramInfo, t.getTestPlanDescription());
		testChild.assignAuthor(t.getTestPlanPreparedBy());
	}

	@Override
	public void testCaseExecutionFinished(TestObjectWrapper t) {
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
	public void testCaseExecutionSkipped(TestObjectWrapper t) {
		testParent.log(LogStatus.SKIP, "Skipped Test Case: " + t.getTestClassObject().getName());
	}

	@Override
	public void testExecutionLoopCount(int count) {
	}

	public void testCaseStatusUpdate(TestStatus testStatus, String description) {
		if (null != testChild) {
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
	public void testResult(TestStatus testStatus, String description) {
		if (null != testChild) {
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
	public void globalBeforeTestCaseMethodExecutionFinished(TestObjectWrapper t) {
		// TODO Auto-generated method stub

	}

	@Override
	public void globalAfterTestCaseMethodExecutionStarted(String methodName, TestObjectWrapper t) {
		// TODO Auto-generated method stub

	}

	@Override
	public void globalAfterTestCaseMethodExecutionFinished(TestObjectWrapper t) {
		// TODO Auto-generated method stub

	}

	@Override
	public void testSuiteException(String description) {
		// TODO Auto-generated method stub

	}

	@Override
	public void testException(String description) {
		// TODO Auto-generated method stub

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
	public void globalBeforeTestUnitMethodExecutionFinished(TestUnitObjectWrapper unit) {
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
	public void globalAfterTestUnitMethodExecutionFinished(TestUnitObjectWrapper unit) {
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
	public void testUnitExecutionStarted(TestUnitObjectWrapper unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void testUnitExecutionFinished(TestUnitObjectWrapper unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void childTestUnitExecutionStarted(TestObjectWrapper t, TestUnitObjectWrapper unit, String paramInfo) {
		testChild = extent.startTest(paramInfo, t.getTestPlanDescription());
		testChild.assignAuthor(t.getTestPlanPreparedBy());
	}

	@Override
	public void childTestUnitExecutionFinished(TestUnitObjectWrapper unit) {
		// add child to parent
		testParent.appendChild(testChild);
		extent.endTest(testChild);
		testChild = null;
	}

	@Override
	public void printTestUnitPlan(TestUnitObjectWrapper unit) {
		// TODO Auto-generated method stub
		
	}

}
