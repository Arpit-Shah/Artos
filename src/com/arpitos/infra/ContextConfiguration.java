package com.arpitos.infra;

import java.io.File;
import java.io.FileNotFoundException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class is responsible for storing Organization Information. During test
 * suit execution XML file will be searched at location
 * ./conf/organisation_info.xml
 * 
 * <PRE>
 * 
 * Sample file format is given below
 * {@code
 	<?xml version="1.0" encoding="UTF-8"?>
	<Configuration>
		<Organization>
			<Property name="enableLogDecoration">true</Property>
			<Property name="enableTextLog">true</Property>
			<Property name="enableHTMLLog">true</Property>
		</Organization>
	</Configuration>
	}
 * </PRE>
 * 
 * @author ArpitS
 *
 */
public class ContextConfiguration {

	private boolean enableLogDecoration = false;
	private boolean enableTextLog = true;
	private boolean enableHTMLLog = false;

	public ContextConfiguration() {
		readConfig();
	}

	/**
	 * Reads XML file from project root location, If not found then default info
	 * is applied
	 */
	private void readConfig() {
		try {
			File fXmlFile = new File("./conf/context_config.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			// optional, but recommended
			// read this -
			// http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();

			// System.out.println("Root element :" +
			// doc.getDocumentElement().getNodeName());
			NodeList nList = doc.getElementsByTagName("Organization");

			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);

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
						if ("enableLogDecoration".equals(eElement.getAttribute("name"))) {
							setEnableLogDecoration(Boolean.parseBoolean(eElement.getTextContent()));
						}
						if ("enableTextLog".equals(eElement.getAttribute("name"))) {
							setEnableTextLog(Boolean.parseBoolean(eElement.getTextContent()));
						}
						if ("enableHTMLLog".equals(eElement.getAttribute("name"))) {
							setEnableHTMLLog(Boolean.parseBoolean(eElement.getTextContent()));
						}
					}
				}
			}
		} catch (FileNotFoundException fe) {
			System.out.println(fe.getMessage() + "\n" + "Fall back to Context configuration Default values");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isEnableLogDecoration() {
		return enableLogDecoration;
	}

	public void setEnableLogDecoration(boolean enableLogDecoration) {
		this.enableLogDecoration = enableLogDecoration;
	}

	public boolean isEnableTextLog() {
		return enableTextLog;
	}

	public void setEnableTextLog(boolean enableTextLog) {
		this.enableTextLog = enableTextLog;
	}

	public boolean isEnableHTMLLog() {
		return enableHTMLLog;
	}

	public void setEnableHTMLLog(boolean enableHTMLLog) {
		this.enableHTMLLog = enableHTMLLog;
	}

}
