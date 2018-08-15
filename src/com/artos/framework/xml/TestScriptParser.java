// Copyright <2018> <Artos>

// Permission is hereby granted, free of charge, to any person obtaining a copy of this software
// and associated documentation files (the "Software"), to deal in the Software without restriction,
// including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
// and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
// subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
// 
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
// INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
// IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
// WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
// OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
package com.artos.framework.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.artos.framework.FWStaticStore;
import com.artos.framework.TestObjectWrapper;
import com.artos.interfaces.PrePostRunnable;
import com.artos.interfaces.TestExecutable;

public class TestScriptParser {

	/**
	 * Reads test script and provides list of test cases name back to user.
	 * 
	 * @param testScriptFile
	 *            testScript formatted with XML
	 * @return list of test cases name
	 */
	public List<TestSuite> readTestScript(File testScriptFile) {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(testScriptFile);

			doc.getDocumentElement().normalize();

			return readTestScript(doc);
		} catch (FileNotFoundException fe) {
			System.out.println(fe.getMessage() + "\n" + "Fall back to Default Organisation values");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Reads logger info from config file
	 * 
	 * @param doc
	 *            Document object of XML file
	 */
	private List<TestSuite> readTestScript(Document doc) {

		List<TestSuite> testSuiteList = new ArrayList<>();

		// System.out.println("Root element :" +
		// doc.getDocumentElement().getNodeName());
		NodeList suiteNodeList = doc.getElementsByTagName("suite");

		for (int temp = 0; temp < suiteNodeList.getLength(); temp++) {
			TestSuite _suite = new TestSuite();

			Node suiteNode = suiteNodeList.item(temp);
			parseSuite(_suite, suiteNode);

			// Only add test suite in the list if atleast one test case is
			// specified
			if (!_suite.getTestFQCNList().isEmpty()) {
				testSuiteList.add(_suite);
			}
		}

		return testSuiteList;
	}

	private void parseSuite(TestSuite _suite, Node suiteNode) {
		if (suiteNode.getNodeType() == Node.ELEMENT_NODE) {
			Element eElement = (Element) suiteNode;
			if ("suite".equals(eElement.getNodeName())) {
				_suite.setSuiteName(eElement.getAttribute("name"));
			}

			NodeList testsNodeList = eElement.getElementsByTagName("tests");
			for (int temp = 0; temp < testsNodeList.getLength(); temp++) {
				Node testsNode = testsNodeList.item(temp);
				parseTests(_suite, testsNode);
			}

			NodeList paramsNodeList = eElement.getElementsByTagName("parameters");
			for (int temp = 0; temp < paramsNodeList.getLength(); temp++) {
				Node parameterNode = paramsNodeList.item(temp);
				parseParameters(_suite, parameterNode);
			}
		}
	}

	private void parseParameters(TestSuite _suite, Node parameterNode) {
		Map<String, String> parametersMap = new HashMap<String, String>();
		NodeList nChildList = parameterNode.getChildNodes();
		for (int i = 0; i < nChildList.getLength(); i++) {
			Node nChildNode = nChildList.item(i);
			if (nChildNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nChildNode;
				if ("parameter".equals(eElement.getNodeName())) {
					parametersMap.put(eElement.getAttribute("name"), eElement.getTextContent());
				}
			}
		}
		_suite.setTestSuiteParameters(parametersMap);
	}

	private void parseTests(TestSuite _suite, Node testsNode) {
		List<String> testFQCNList = new ArrayList<>();
		NodeList nChildList = testsNode.getChildNodes();
		for (int i = 0; i < nChildList.getLength(); i++) {
			Node nChildNode = nChildList.item(i);
			if (nChildNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nChildNode;
				if ("test".equals(eElement.getNodeName())) {
					testFQCNList.add(eElement.getAttribute("name"));
				}
			}
		}
		_suite.setTestFQCNList(testFQCNList);
	}

	public void createTestScriptFromTestExecutable(List<TestExecutable> testList) throws Exception {
		if (null == testList || testList.isEmpty()) {
			return;
		}

		File scriptFile = new File(FWStaticStore.TESTSCRIPT_BASE_DIR + testList.get(0).getClass().getPackage().getName() + ".xml");
		if (!scriptFile.getParentFile().exists()) {
			scriptFile.getParentFile().mkdirs();
		}

		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		// root elements
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("configuration");
		doc.appendChild(rootElement);

		// Organisation Info elements
		Element orgnization_info = doc.createElement("suite");
		rootElement.appendChild(orgnization_info);

		Attr attr = doc.createAttribute("name");
		attr.setValue(testList.get(0).getClass().getPackage().getName());
		orgnization_info.setAttributeNode(attr);

		for (int i = 0; i < testList.size(); i++) {
			// add test cases
			Element property = doc.createElement("test");
			property.appendChild(doc.createTextNode(" "));
			orgnization_info.appendChild(property);

			Attr attr1 = doc.createAttribute("name");
			attr1.setValue(testList.get(i).getClass().getSimpleName());
			property.setAttributeNode(attr1);
		}

		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(scriptFile);

		transformer.transform(source, result);

	}

	public void createExecScriptFromObjWrapper(Class<? extends PrePostRunnable> cls, List<TestObjectWrapper> testList) throws Exception {
		if (null == testList || testList.isEmpty()) {
			return;
		}

		File scriptFile = new File(FWStaticStore.TESTSCRIPT_BASE_DIR + cls.getPackage().getName() + ".xml");
		if (!scriptFile.getParentFile().exists()) {
			scriptFile.getParentFile().mkdirs();
		}

		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		// root elements
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("configuration");
		doc.appendChild(rootElement);

		// Organisation Info elements
		Element orgnization_info = doc.createElement("suite");
		rootElement.appendChild(orgnization_info);

		Attr attr = doc.createAttribute("name");
		attr.setValue(cls.getPackage().getName());
		orgnization_info.setAttributeNode(attr);

		for (int i = 0; i < testList.size(); i++) {
			// add test cases
			Element property = doc.createElement("test");
			property.appendChild(doc.createTextNode(" "));
			orgnization_info.appendChild(property);

			Attr attr1 = doc.createAttribute("name");
			attr1.setValue(testList.get(i).getTestClassObject().getCanonicalName());
			property.setAttributeNode(attr1);
		}

		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(scriptFile);

		transformer.transform(source, result);
	}

	public static void main(String[] args) {
		TestScriptParser xml = new TestScriptParser();
		List<TestSuite> testSuiteList = xml
				.readTestScript(new File("C:\\Arpit\\Arpit_Programming\\arpitos_test_fork\\script\\unit_test.Guardian.xml"));
		for (TestSuite suite : testSuiteList) {
			System.out.println(suite.getSuiteName());
			System.out.println(suite.getThreadName());
			List<String> testlist = suite.getTestFQCNList();
			for (String s : testlist) {
				System.out.println(s);
			}
			Map<String, String> parameterMap = suite.getTestSuiteParameters();
			for (Entry<String, String> entry : parameterMap.entrySet()) {
				System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue());
			}
		}
	}
}
