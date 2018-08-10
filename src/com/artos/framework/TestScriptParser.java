package com.artos.framework;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

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

import com.artos.framework.TestObjectWrapper;
import com.artos.interfaces.TestExecutable;

public class TestScriptParser {

	public List<String> readTestScript(File testScriptFile) {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(testScriptFile);

			// optional, but recommended
			// read this -
			// http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();

			return readTestCases(doc);
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
	private List<String> readTestCases(Document doc) {

		String suite = "";
		List<String> testList = new ArrayList<>();
		// System.out.println("Root element :" +
		// doc.getDocumentElement().getNodeName());
		NodeList nList = doc.getElementsByTagName("suite");

		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				if ("suite".equals(eElement.getNodeName())) {
					suite = eElement.getAttribute("name");
				}
			}

			NodeList nChildList = nNode.getChildNodes();
			for (int i = 0; i < nChildList.getLength(); i++) {
				Node nChildNode = nChildList.item(i);
				if (nChildNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nChildNode;
					// System.out.println(eElement.getNodeName());
					// System.out.println(eElement.getAttribute("name"));
					// System.out.println(eElement.getAttribute("name")
					// +
					// ":" +
					// eElement.getTextContent());
					if ("test".equals(eElement.getNodeName())) {
						testList.add(suite + "." + eElement.getAttribute("name"));
					}
				}
			}
		}

		return testList;
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

	public void createExecScriptFromObjWrapper(List<TestObjectWrapper> testList) throws Exception {
		if (null == testList || testList.isEmpty()) {
			return;
		}

		File scriptFile = new File(FWStaticStore.TESTSCRIPT_BASE_DIR + testList.get(0).getTestClassObject().getPackage().getName() + ".xml");
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
		attr.setValue(testList.get(0).getTestClassObject().getPackage().getName());
		orgnization_info.setAttributeNode(attr);

		for (int i = 0; i < testList.size(); i++) {
			// add test cases
			Element property = doc.createElement("test");
			property.appendChild(doc.createTextNode(" "));
			orgnization_info.appendChild(property);

			Attr attr1 = doc.createAttribute("name");
			attr1.setValue(testList.get(i).getTestClassObject().getSimpleName());
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
		// RunXMLConfig xml = new
		// RunXMLConfig("C:\\Arpit\\Java_Workspace\\BladeRunner_Platform_QA_A\\conf\\Artos_Run.xml");
		// List<String> testList = xml.readTestScript();
		// for (String t : testList) {
		// System.out.println(t);
		// }

		// RunXMLConfig xml = new
		// RunXMLConfig("C:\\Arpit\\Java_Workspace\\BladeRunner_Platform_QA_A\\conf\\Artos_Run_1.xml");
		// List<String> testList = new ArrayList<>();
		// xml.writeDefaultConfig(testList);
		// for (String t : testList) {
		// System.out.println(t);
		// }
	}
}
