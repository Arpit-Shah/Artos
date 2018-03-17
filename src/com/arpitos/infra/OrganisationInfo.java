package com.arpitos.infra;

import java.io.File;
import java.io.FileNotFoundException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class OrganisationInfo {

	private String Organisation_Name = "Organisation_Name";
	private String Organisation_Address = "XX, Test Street, Test address";
	private String Organisation_Country = "NewZealand";
	private String Organisation_Contact_Number = "+64 1234567";
	private String Organisation_Website = "www.arpitos.com";

	public OrganisationInfo() {
		readConfig();
	}

	private void readConfig() {
		try {
			File fXmlFile = new File("./conf/organisation_info.xml");
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
						// System.out.println(eElement.getAttribute("name") +
						// ":" +
						// eElement.getTextContent());
						if ("Name".equals(eElement.getAttribute("name"))) {
							setOrganisation_Name(eElement.getTextContent());
						}
						if ("Address".equals(eElement.getAttribute("name"))) {
							setOrganisation_Address(eElement.getTextContent());
						}
						if ("Country".equals(eElement.getAttribute("name"))) {
							setOrganisation_Country(eElement.getTextContent());
						}
						if ("Contact_Number".equals(eElement.getAttribute("name"))) {
							setOrganisation_Contact_Number(eElement.getTextContent());
						}
						if ("Website".equals(eElement.getAttribute("name"))) {
							setOrganisation_Website(eElement.getTextContent());
						}
					}
				}
			}
		} catch (FileNotFoundException fe) {
			System.out.println(fe.getMessage() + "\n" + "Fall back to Default Organisation values will be used");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getOrganisation_Name() {
		return Organisation_Name;
	}

	public void setOrganisation_Name(String organisation_Name) {
		Organisation_Name = organisation_Name;
	}

	public String getOrganisation_Address() {
		return Organisation_Address;
	}

	public void setOrganisation_Address(String organisation_Address) {
		Organisation_Address = organisation_Address;
	}

	public String getOrganisation_Contact_Number() {
		return Organisation_Contact_Number;
	}

	public void setOrganisation_Contact_Number(String organisation_Contact_Number) {
		Organisation_Contact_Number = organisation_Contact_Number;
	}

	public String getOrganisation_Website() {
		return Organisation_Website;
	}

	public void setOrganisation_Website(String organisation_Website) {
		Organisation_Website = organisation_Website;
	}

	public String getOrganisation_Country() {
		return Organisation_Country;
	}

	public void setOrganisation_Country(String organisation_Country) {
		Organisation_Country = organisation_Country;
	}

}
