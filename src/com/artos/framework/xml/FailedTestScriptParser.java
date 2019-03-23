/*******************************************************************************
 * Copyright (C) 2018-2019 Pramod K and Artos Contributors
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
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.artos.framework.Enums.TestStatus;
import com.artos.framework.FWStaticStore;
import com.artos.framework.TestObjectWrapper;

public class FailedTestScriptParser {



	public void createExecScriptFromObjWrapper(List<TestObjectWrapper> testList) throws Exception {
		if (null == testList || testList.isEmpty()) {
			return;
		}
		//Create new script file
		File scriptFile = new File(FWStaticStore.TESTSCRIPT_BASE_DIR +"Failed"+ "TestCases" + ".xml");

		if (scriptFile.exists() && scriptFile.isFile()) {
			//Delete old file if present
			scriptFile.delete();			
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

		Attr verAttr = doc.createAttribute("version");
		verAttr.setValue("1");
		rootElement.setAttributeNode(verAttr);

		// Organisation Info elements
		Element suite = doc.createElement("suite");
		rootElement.appendChild(suite);

		Attr attr1 = doc.createAttribute("enable");
		attr1.setValue("true");
		suite.setAttributeNode(attr1);

		Attr attr2 = doc.createAttribute("name");
		attr2.setValue("UniqueName");
		suite.setAttributeNode(attr2);

		Attr attr3 = doc.createAttribute("loopcount");
		attr3.setValue("1");
		suite.setAttributeNode(attr3);

		Comment comment = doc
				.createComment("java -cp \"artos-0.0.1.jar;test.jar\" unit_test.Main --testscript=\".\\script\\" + "TestCases" + ".xml\"");
		suite.getParentNode().insertBefore(comment, suite);

		createTestList(testList, doc, suite);
		createSuiteParameters(doc, suite);
		createTestCaseGroups(doc, suite);
		createTestUnitGroups(doc, suite);

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
		int i =0;
		for (TestObjectWrapper t : testList) {
			// add test cases
			Element property = doc.createElement("test");
			property.appendChild(doc.createTextNode(""));
			tests.appendChild(property);

			if(t.getTestOutcomeList().get(0) == TestStatus.FAIL) {
				Attr attr1 = doc.createAttribute("name");
				attr1.setValue(testList.get(i).getTestClassObject().getCanonicalName());
				property.setAttributeNode(attr1);	
			}
			i++;
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

	private void createTestCaseGroups(Document doc, Element suite) {
		// Organisation Info elements
		Element groups = doc.createElement("testcasegroups");
		suite.appendChild(groups);

		// add test cases
		Element property = doc.createElement("group");
		property.appendChild(doc.createTextNode(""));
		groups.appendChild(property);

		Attr attr1 = doc.createAttribute("name");
		attr1.setValue("*");
		property.setAttributeNode(attr1);
	}

	private void createTestUnitGroups(Document doc, Element suite) {
		// Organisation Info elements
		Element groups = doc.createElement("testunitgroups");
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
