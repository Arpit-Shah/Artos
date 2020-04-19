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
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.artos.framework.Enums.TestStatus;
import com.artos.framework.FWStaticStore;
import com.artos.framework.infra.BDDScenario;
import com.artos.framework.infra.BDDStep;
import com.artos.framework.infra.TestContext;
import com.artos.framework.infra.TestObjectWrapper;
import com.artos.framework.infra.TestUnitObjectWrapper;
import com.artos.interfaces.TestProgress;

public class JUnitReportListener implements TestProgress {

	TestContext context;
	File fXmlFile;
	Document doc;
	Element rootElement;
	Element test_suite;
	long testcasePassCount = 0;
	long testcaseFailCount = 0;
	long testcaseSkipCount = 0;
	long testcaseKTFCount = 0;
	long totalUnitPassCount = 0;
	long totalUnitFailCount = 0;
	long totalUnitSkipCount = 0;
	long totalUnitKTFCount = 0;
	long singleTestCasePassUnitCount = 0;
	long singleTestCaseFailUnitCount = 0;
	long singleTestCaseSkipUnitCount = 0;
	long singleTestCaseKTFUnitCount = 0;

	public JUnitReportListener(TestContext context) {
		this.context = context;
		fXmlFile = new File(FWStaticStore.JUNIT_REPORT_BASE_DIR + "JUnit_Report_" + context.getTestSuiteName() + ".xml");

		// Delete this file if already exist
		try {
			if (fXmlFile.exists() && fXmlFile.isFile()) {
				fXmlFile.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			if (!fXmlFile.exists() || !fXmlFile.isFile()) {
				fXmlFile.getParentFile().mkdirs();
			}

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			docFactory.setNamespaceAware(true);
			docFactory.setValidating(true);
			Schema schema = sf.newSchema(new StreamSource(FWStaticStore.CONFIG_BASE_DIR + File.separator + "JUnit.xsd"));
			docFactory.setSchema(schema);

			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			doc = docBuilder.newDocument();
			rootElement = doc.createElement("testsuites");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void testExecutionLoopCount(int count) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeTestSuiteMethodExecutionStarted(String methodName, String description) {

	}

	@Override
	public void beforeTestSuiteMethodExecutionFinished(String description) {
		// TODO Auto-generated method stub

	}

	@Override
	public void testSuiteExecutionStarted(String description) {

	}

	@Override
	public void testSuiteExecutionFinished(String description) {
		try {
			rootElement.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xsi:noNamespaceSchemaLocation", "../../conf/JUnit.xsd");
			rootElement.setAttribute("disabled", "false");
			rootElement.setAttribute("errors", "0");
			rootElement.setAttribute("failures", Long.toString(context.getCurrentFailCount()));
			rootElement.setAttribute("name", context.getTestSuiteName());
			rootElement.setAttribute("tests", Long.toString(context.getTotalTestCount()));
			rootElement.setAttribute("time", String.format("%2d.%3d", TimeUnit.MILLISECONDS.toSeconds(context.getTestSuiteTimeDuration()),
					-TimeUnit.MINUTES.toMillis(TimeUnit.MILLISECONDS.toSeconds(context.getTestSuiteTimeDuration()))));
			doc.appendChild(rootElement);

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new PrintWriter(new FileOutputStream(fXmlFile, false)));

			transformer.transform(source, result);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	public void printTestUnitPlan(TestUnitObjectWrapper unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void printTestUnitPlan(BDDStep step) {
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
	public void globalAfterTestUnitMethodExecutionStarted(String methodName, TestUnitObjectWrapper unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void globalAfterTestUnitMethodExecutionStarted(String methodName, BDDStep step) {
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
	public void globalAfterTestUnitMethodExecutionFinished(TestUnitObjectWrapper unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void globalAfterTestUnitMethodExecutionFinished(BDDStep step) {
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
	public void localBeforeTestUnitMethodExecutionStarted(TestObjectWrapper t, TestUnitObjectWrapper unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void localBeforeTestUnitMethodExecutionFinished(TestUnitObjectWrapper unit) {
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
	public void testCaseExecutionStarted(TestObjectWrapper t) {
		// create testsuite element
		test_suite = doc.createElement("testsuite");
	}

	@Override
	public void testCaseExecutionStarted(BDDScenario scenario) {
		// TODO Auto-generated method stub

	}

	@Override
	public void testCaseExecutionFinished(TestObjectWrapper t) {

	}

	@Override
	public void testResult(TestObjectWrapper t, TestStatus testStatus, File snapshot, String description) {

		if (testStatus == TestStatus.PASS) {
			testcasePassCount++;
		} else if (testStatus == TestStatus.FAIL) {
			testcaseFailCount++;
		} else if (testStatus == TestStatus.SKIP) {
			testcaseSkipCount++;
		} else if (testStatus == TestStatus.KTF) {
			testcaseKTFCount++;
		}

		// populate test suite element
		test_suite.setAttribute("disabled", "false");
		test_suite.setAttribute("errors", "0");
		test_suite.setAttribute("skipped", Long.toString(singleTestCaseSkipUnitCount));
		test_suite.setAttribute("failures", Long.toString(singleTestCaseFailUnitCount));
		test_suite.setAttribute("hostname", "");
		test_suite.setAttribute("id", "");
		test_suite.setAttribute("name", t.getTestClassObject().getName());
		// test_suite.setAttribute("package", t.getTestClassObject().getPackage().getName());
		test_suite.setAttribute("tests", Long.toString(t.getTestUnitList().size()));
		test_suite.setAttribute("time", convertMillisecondsToSecondMills(t.getTestFinishTime() - t.getTestStartTime()));
		test_suite.setAttribute("timestamp", "");

		// properties element
		Element properties = doc.createElement("properties");

		// add property to properties element
		Element property = doc.createElement("property");
		property.setAttribute("name", "");
		property.setAttribute("value", "");
		properties.appendChild(property);

//		test_suite.appendChild(doc.createElement("system-out"));
//		test_suite.appendChild(doc.createElement("system-err"));

		// add properties element to test suite
		test_suite.appendChild(properties);

		rootElement.appendChild(test_suite);

		// Reset count after each test execution
		{
			singleTestCasePassUnitCount = 0;
			singleTestCaseFailUnitCount = 0;
			singleTestCaseSkipUnitCount = 0;
			singleTestCaseKTFUnitCount = 0;
		}
	}

	@Override
	public void testUnitResult(TestUnitObjectWrapper unit, TestStatus testStatus, File snapshot, String description) {

		// add testcase to properties element
		Element testcase = doc.createElement("testcase");

		if (testStatus == TestStatus.PASS) {
			totalUnitPassCount++;
			singleTestCasePassUnitCount++;
		} else if (testStatus == TestStatus.FAIL) {
			totalUnitFailCount++;
			singleTestCaseFailUnitCount++;
			Element failure = doc.createElement("failure");
			failure.setAttribute("message", description);
			testcase.appendChild(failure);
		} else if (testStatus == TestStatus.SKIP) {
			totalUnitSkipCount++;
			singleTestCaseSkipUnitCount++;
			Element skipped = doc.createElement("skipped");
			testcase.appendChild(skipped);
		} else if (testStatus == TestStatus.KTF) {
			totalUnitKTFCount++;
			singleTestCaseKTFUnitCount++;
			Element errors = doc.createElement("error");
			errors.setAttribute("message", description);
			testcase.appendChild(errors);
		}

		testcase.setAttribute("assertions", "");
		testcase.setAttribute("classname", unit.getTestUnitMethod().getDeclaringClass().getName());
		testcase.setAttribute("name", unit.getTestUnitMethod().getName());
		testcase.setAttribute("status", TestStatus.getEnumName(context.getCurrentUnitTestStatus().getValue()));
		testcase.setAttribute("time", convertMillisecondsToSecondMills(unit.getTestUnitFinishTime() - unit.getTestUnitStartTime()));

		// testcase.appendChild(doc.createElement("system-out"));
		// testcase.appendChild(doc.createElement("system-err"));
		test_suite.appendChild(testcase);
	}

	@Override
	public void testCaseExecutionFinished(BDDScenario scenario) {
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

	}

	@Override
	public void testCaseExecutionSkipped(TestObjectWrapper t) {
		// TODO Auto-generated method stub

	}

	@Override
	public void childTestCaseExecutionStarted(TestObjectWrapper t, String paramInfo) {
		// TODO Auto-generated method stub

	}

	@Override
	public void childTestCaseExecutionStarted(BDDScenario scenario, String paramInfo) {
		// TODO Auto-generated method stub

	}

	@Override
	public void childTestCaseExecutionFinished(TestObjectWrapper t) {
		// TODO Auto-generated method stub

	}

	@Override
	public void childTestCaseExecutionFinished(BDDScenario scenario) {
		// TODO Auto-generated method stub

	}

	@Override
	public void childTestUnitExecutionStarted(TestObjectWrapper t, TestUnitObjectWrapper unit, String paramInfo) {
		// TODO Auto-generated method stub

	}

	@Override
	public void childTestUnitExecutionStarted(BDDScenario scenario, BDDStep step, String paramInfo) {
		// TODO Auto-generated method stub

	}

	@Override
	public void childTestUnitExecutionFinished(TestUnitObjectWrapper unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void childTestUnitExecutionFinished(BDDStep step) {
		// TODO Auto-generated method stub

	}

	@Override
	public void testCaseStatusUpdate(TestStatus testStatus, File snapshot, String msg) {
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
	public void testSuiteSummaryPrinting(String description) {
		// TODO Auto-generated method stub

	}

	@Override
	public void testSuiteFailureHighlight(String description) {
		// TODO Auto-generated method stub

	}

	@Override
	public void testSuiteException(Throwable e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void testException(Throwable e) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void unitException(Throwable e) {
		// TODO Auto-generated method stub

	}

	private String convertMillisecondsToSecondMills(long totalTimeinMilliseconds) {
		long seconds = 0l;
		long milliseconds = 0l;
		if (totalTimeinMilliseconds > 1000) {
			seconds = TimeUnit.MILLISECONDS.toSeconds(totalTimeinMilliseconds);
			milliseconds = totalTimeinMilliseconds - TimeUnit.SECONDS.toMillis(seconds);
		} else {
			milliseconds = totalTimeinMilliseconds;
		}
		return String.format("%02d.%03d", seconds, milliseconds);
	}

	@Override
	public void testResult(BDDScenario scenario, TestStatus testStatus, File snapshot, String description) {
		// TODO Auto-generated method stub

	}

	@Override
	public void testUnitResult(BDDStep step, TestStatus testStatus, File snapshot, String description) {
		// TODO Auto-generated method stub
		
	}

}
