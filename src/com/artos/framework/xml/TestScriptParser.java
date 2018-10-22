/*******************************************************************************
 * Copyright (C) 2018 Arpit Shah
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
package com.artos.framework.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.artos.exception.InvalidDataException;
import com.artos.framework.FWStaticStore;
import com.artos.framework.TestObjectWrapper;

public class TestScriptParser {

	/**
	 * Reads test script and provides list of test cases name back to user.
	 * 
	 * @param testScriptFile testScript formatted with XML
	 * @return list of test cases name
	 * @throws ParserConfigurationException if a DocumentBuildercannot be created which satisfies the configuration requested.
	 * @throws IOException If any IO errors occur.
	 * @throws SAXException If any parse errors occur.
	 * @throws InvalidDataException If user provides invalid data
	 */
	public List<TestSuite> readTestScript(File testScriptFile) throws ParserConfigurationException, SAXException, IOException, InvalidDataException {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(testScriptFile);

			doc.getDocumentElement().normalize();

			return readTestScript(doc);
		} catch (FileNotFoundException fe) {
			System.out.println(fe.getMessage() + "\n" + "Fall back to Default Organisation values");
		}
		return null;
	}

	/**
	 * Reads logger info from config file
	 * 
	 * @param doc Document object of XML file
	 * @throws InvalidDataException thrown when invalid data is provided by user
	 */
	private List<TestSuite> readTestScript(Document doc) throws InvalidDataException {

		List<TestSuite> testSuiteList = new ArrayList<>();

		// System.out.println("Root element :" +
		// doc.getDocumentElement().getNodeName());
		NodeList suiteNodeList = doc.getElementsByTagName("suite");

		for (int temp = 0; temp < suiteNodeList.getLength(); temp++) {
			TestSuite _suite = new TestSuite();

			Node suiteNode = suiteNodeList.item(temp);
			parseSuite(_suite, suiteNode);

			// add test suite in the list even no test list is provided
			if (null != _suite.getTestFQCNList()) {
				testSuiteList.add(_suite);
			}
		}

		return testSuiteList;
	}

	private void parseSuite(TestSuite _suite, Node suiteNode) throws InvalidDataException {
		if (suiteNode.getNodeType() == Node.ELEMENT_NODE) {
			Element eElement = (Element) suiteNode;
			if ("suite".equals(eElement.getNodeName())) {
				_suite.setSuiteName(eElement.getAttribute("name").trim());

				// If loopcount attribute is not provided then assume 1, if
				// provided then check if it is valid
				String loopCount = eElement.getAttribute("loopcount").trim();

				if ("".equals(loopCount)) {
					_suite.setLoopCount(1);
				} else {
					try {
						int nLoopCount = Integer.parseInt(loopCount);
						if (nLoopCount <= 0) {
							throw new InvalidDataException("Invalid Loop Count : " + loopCount);
						}
						_suite.setLoopCount(nLoopCount);
					} catch (NumberFormatException e) {
						throw new InvalidDataException("Invalid Loop Count : " + loopCount);
					}
				}
			}

			NodeList testsNodeList = eElement.getElementsByTagName("tests");
			if (testsNodeList.getLength() > 0) {
				for (int temp = 0; temp < testsNodeList.getLength(); temp++) {
					Node testsNode = testsNodeList.item(temp);
					parseTests(_suite, testsNode);
				}
			} else {
				// create empty list so null pointer exception can be avoided
				_suite.setTestFQCNList(new ArrayList<>());
			}

			NodeList paramsNodeList = eElement.getElementsByTagName("parameters");
			if (paramsNodeList.getLength() > 0) {
				for (int temp = 0; temp < paramsNodeList.getLength(); temp++) {
					Node parameterNode = paramsNodeList.item(temp);
					parseParameters(_suite, parameterNode);
				}
			} else {
				// create empty map so null pointer exception can be avoided
				_suite.setTestSuiteParameters(new HashMap<>());
			}

			NodeList groupsNodeList = eElement.getElementsByTagName("groups");
			if (groupsNodeList.getLength() > 0) {
				for (int temp = 0; temp < groupsNodeList.getLength(); temp++) {
					Node groupsNode = groupsNodeList.item(temp);
					parseGroups(_suite, groupsNode);
				}
			} else {
				// create list with "*" so all test can be run
				_suite.setGroupList(new ArrayList<>());
				_suite.getGroupList().add("*");
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
					testFQCNList.add(eElement.getAttribute("name").trim());
				}
			}
		}
		_suite.setTestFQCNList(testFQCNList);
	}

	private void parseGroups(TestSuite _suite, Node groupsNode) {
		List<String> groupList = new ArrayList<>();
		NodeList nChildList = groupsNode.getChildNodes();
		for (int i = 0; i < nChildList.getLength(); i++) {
			Node nChildNode = nChildList.item(i);
			if (nChildNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nChildNode;
				if ("group".equals(eElement.getNodeName())) {
					groupList.add(eElement.getAttribute("name").trim().toUpperCase());
				}
			}
		}

		// If group list is empty then atleast add * which will run all test
		// cases
		if (groupList.isEmpty()) {
			groupList.add("*");
		}
		_suite.setGroupList(groupList);
	}

	public void createExecScriptFromObjWrapper(List<TestObjectWrapper> testList) throws Exception {
		if (null == testList || testList.isEmpty()) {
			return;
		}

		String packageName;
		// Package will be none if test is in root package
		if (null == testList.get(0).getTestClassObject().getPackage()) {
			packageName = "ProjectRoot";
		} else {
			packageName = testList.get(0).getTestClassObject().getPackage().getName();
		}
		File scriptFile = new File(FWStaticStore.TESTSCRIPT_BASE_DIR + packageName + ".xml");

		if (scriptFile.exists() && scriptFile.isFile()) {
			return;
		}

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
		Element suite = doc.createElement("suite");
		rootElement.appendChild(suite);

		Attr attr = doc.createAttribute("name");
		attr.setValue(packageName);
		suite.setAttributeNode(attr);

		Attr attr2 = doc.createAttribute("loopcount");
		attr2.setValue("1");
		suite.setAttributeNode(attr2);

		Comment comment = doc
				.createComment("java -cp \"artos-0.0.1.jar;test.jar\" unit_test.Main --testscript=\".\\script\\" + packageName + ".xml\"");
		suite.getParentNode().insertBefore(comment, suite);

		createTestList(testList, doc, suite);
		createSuiteParameters(doc, suite);
		createSuiteGroups(doc, suite);

		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(scriptFile);

		transformer.transform(source, result);

	}

	private void createTestList(List<TestObjectWrapper> testList, Document doc, Element suite) {
		// Organisation Info elements
		Element tests = doc.createElement("tests");
		suite.appendChild(tests);

		for (int i = 0; i < testList.size(); i++) {
			// add test cases
			Element property = doc.createElement("test");
			property.appendChild(doc.createTextNode(""));
			tests.appendChild(property);

			Attr attr1 = doc.createAttribute("name");
			attr1.setValue(testList.get(i).getTestClassObject().getCanonicalName());
			property.setAttributeNode(attr1);
		}
	}

	private void createSuiteParameters(Document doc, Element suite) {
		// Organisation Info elements
		Element parameters = doc.createElement("parameters");
		suite.appendChild(parameters);

		for (int i = 0; i < 3; i++) {
			// add test cases
			Element property = doc.createElement("parameter");
			property.appendChild(doc.createTextNode("parameterValue_" + i));
			parameters.appendChild(property);

			Attr attr1 = doc.createAttribute("name");
			attr1.setValue("PARAMETER_" + i);
			property.setAttributeNode(attr1);
		}
	}

	private void createSuiteGroups(Document doc, Element suite) {
		// Organisation Info elements
		Element groups = doc.createElement("groups");
		suite.appendChild(groups);

		// add test cases
		Element property = doc.createElement("group");
		property.appendChild(doc.createTextNode(""));
		groups.appendChild(property);

		Attr attr1 = doc.createAttribute("name");
		attr1.setValue("*");
		property.setAttributeNode(attr1);
	}

}
