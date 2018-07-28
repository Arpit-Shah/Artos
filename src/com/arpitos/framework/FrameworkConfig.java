// Copyright <2018> <Arpitos>

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
package com.arpitos.framework;

import java.io.File;
import java.io.FileNotFoundException;

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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class is responsible for storing framework Configuration. During test
 * suit execution XML file will be searched at location ./conf
 * 
 * 
 *
 */
public class FrameworkConfig {

	final File fXmlFile = new File("./conf/Framework_Config.xml");

	// Organisation Info
	private String Organisation_Name = "Organisation_Name";
	private String Organisation_Address = "XX, Test Street, Test address";
	private String Organisation_Country = "NewZealand";
	private String Organisation_Contact_Number = "+64 1234567";
	private String Organisation_Email = "test@gmail.com";
	private String Organisation_Website = "www.arpitos.com";

	// Email Settings Info
	private String smtpServer = "smtp.gmail.com";
	private String smtpSSLPort = "465";
	private String smtpAuthRequired = "true";
	private String sendersName = "Test Sender";
	private String sendersEmail = "test@gmail.com";
	private String sendersUserName = "test@gmail.com";
	private String password = "password";
	private String receiversName = "Test Receiver";
	private String receiversEmail = "test@gmail.com";
	private String emailSubject = "ArpitOS Email Client";
	private String messageText = "Test email from Arpitos";

	// Logger
	private String logLevel = "debug";
	private String logRootDir = "./reporting/";
	private String logSubDir = "SN-123";
	private boolean enableLogDecoration = false;
	private boolean enableTextLog = true;
	private boolean enableHTMLLog = false;

	// Features
	private boolean enableGUITestSelector = true;
	private boolean enableOrganisationInfo = true;
	private boolean enableBanner = true;

	/**
	 * Constructor
	 * 
	 * @param createIfNotPresent
	 *            enables creation of default configuration file if not present
	 */
	public FrameworkConfig(boolean createIfNotPresent) {
		readXMLConfig(createIfNotPresent);
	}

	/**
	 * Reads Framework configuration file and set global values so framework
	 * configurations is available to everyone
	 * 
	 * @param createIfNotPresent
	 *            enables creation of default configuration file if not present
	 */
	public void readXMLConfig(boolean createIfNotPresent) {

		try {
			if (!fXmlFile.exists() || !fXmlFile.isFile()) {
				if (createIfNotPresent) {
					fXmlFile.getParentFile().mkdirs();
					writeDefaultConfig(fXmlFile);
				}
			}

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			// optional, but recommended
			// read this -
			// http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();

			readOrganisationInfo(doc);
			readLoggerConfig(doc);
			readFeatures(doc);
			readEmailSettings(doc);
		} catch (FileNotFoundException fe) {
			System.out.println(fe.getMessage() + "\n" + "Fall back to Default Organisation values");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Writes default framework configuration file
	 * 
	 * @param fXmlFile
	 *            Destination file object
	 * @throws Exception
	 */
	private void writeDefaultConfig(File fXmlFile) throws Exception {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		// root elements
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("configuration");
		doc.appendChild(rootElement);

		// Organisation Info elements
		Element orgnization_info = doc.createElement("organization_info");
		rootElement.appendChild(orgnization_info);

		// Properties of Organisation Info
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(getOrganisation_Name()));
			orgnization_info.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("Name");
			property.setAttributeNode(attr);
		}
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(getOrganisation_Address()));
			orgnization_info.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("Address");
			property.setAttributeNode(attr);
		}
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(getOrganisation_Country()));
			orgnization_info.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("Country");
			property.setAttributeNode(attr);
		}
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(getOrganisation_Contact_Number()));
			orgnization_info.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("Contact_Number");
			property.setAttributeNode(attr);
		}
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(getOrganisation_Email()));
			orgnization_info.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("Email");
			property.setAttributeNode(attr);
		}
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(getOrganisation_Website()));
			orgnization_info.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("Website");
			property.setAttributeNode(attr);
		}

		// Logger config elements
		Element logger = doc.createElement("logger");
		rootElement.appendChild(logger);
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(getLogLevel()));
			logger.appendChild(property);

			Comment comment = doc.createComment("LogLevel Options : info:debug:trace:fatal:warn:all");
			property.getParentNode().insertBefore(comment, property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("logLevel");
			property.setAttributeNode(attr);
		}
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(getLogRootDir()));
			logger.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("logRootDir");
			property.setAttributeNode(attr);
		}
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(getLogSubDir()));
			logger.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("logSubDir");
			property.setAttributeNode(attr);
		}
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(Boolean.toString(isEnableLogDecoration())));
			logger.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("enableLogDecoration");
			property.setAttributeNode(attr);
		}
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(Boolean.toString(isEnableTextLog())));
			logger.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("enableTextLog");
			property.setAttributeNode(attr);
		}
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(Boolean.toString(isEnableHTMLLog())));
			logger.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("enableHTMLLog");
			property.setAttributeNode(attr);
		}

		// SMTP Settings
		Element smtp_settings = doc.createElement("smtp_settings");
		rootElement.appendChild(smtp_settings);

		// Properties of Organisation Info
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(getSmtpServer()));
			smtp_settings.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("ServerAddress");
			property.setAttributeNode(attr);
		}
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(getSmtpSSLPort()));
			smtp_settings.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("SSLPort");
			property.setAttributeNode(attr);
		}
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(getSmtpAuthRequired()));
			smtp_settings.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("SMTPAuth");
			property.setAttributeNode(attr);
		}
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(getSendersName()));
			smtp_settings.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("SendersName");
			property.setAttributeNode(attr);
		}
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(getSendersEmail()));
			smtp_settings.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("SendersEmail");
			property.setAttributeNode(attr);
		}
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(getSendersUserName()));
			smtp_settings.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("SendersUserName");
			property.setAttributeNode(attr);
		}
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(getPassword()));
			smtp_settings.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("Password");
			property.setAttributeNode(attr);
		}
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(getReceiversName()));
			smtp_settings.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("ReceiversName");
			property.setAttributeNode(attr);
		}
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(getReceiversEmail()));
			smtp_settings.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("ReceiversEmail");
			property.setAttributeNode(attr);
		}
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(getEmailSubject()));
			smtp_settings.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("EmailSubject");
			property.setAttributeNode(attr);
		}
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(getMessageText()));
			smtp_settings.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("EmailMessage");
			property.setAttributeNode(attr);
		}

		// Features
		Element features = doc.createElement("features");
		rootElement.appendChild(features);
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(Boolean.toString(isEnableGUITestSelector())));
			features.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("enableGUITestSelector");
			property.setAttributeNode(attr);
		}
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(Boolean.toString(isEnableBanner())));
			features.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("enableBanner");
			property.setAttributeNode(attr);
		}
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(Boolean.toString(isEnableOrganisationInfo())));
			features.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("enableOrganisationInfo");
			property.setAttributeNode(attr);
		}

		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(fXmlFile);

		transformer.transform(source, result);

	}

	/**
	 * Reads logger info from config file
	 * 
	 * @param doc
	 *            Document object of XML file
	 */
	private void readLoggerConfig(Document doc) {
		// System.out.println("Root element :" +
		// doc.getDocumentElement().getNodeName());
		NodeList nList = doc.getElementsByTagName("logger");

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
					if ("logLevel".equals(eElement.getAttribute("name"))) {
						setLogLevel(eElement.getTextContent());
					}
					if ("logRootDir".equals(eElement.getAttribute("name"))) {
						if (eElement.getTextContent().endsWith("/") || eElement.getTextContent().endsWith("\\")) {
							setLogRootDir(eElement.getTextContent());
						} else {
							setLogRootDir(eElement.getTextContent() + File.separator);
						}
					}
					if ("logSubDir".equals(eElement.getAttribute("name"))) {
						if (eElement.getTextContent().endsWith("/") || eElement.getTextContent().endsWith("\\")) {
							setLogSubDir(eElement.getTextContent());
						} else {
							setLogSubDir(eElement.getTextContent() + File.separator);
						}
					}
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
	}

	/**
	 * Reads features info from config file
	 * 
	 * @param doc
	 *            Document object of an XML file
	 */
	private void readFeatures(Document doc) {
		// System.out.println("Root element :" +
		// doc.getDocumentElement().getNodeName());
		NodeList nList = doc.getElementsByTagName("features");

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
					if ("enableGUITestSelector".equals(eElement.getAttribute("name"))) {
						setEnableGUITestSelector(Boolean.parseBoolean(eElement.getTextContent()));
					}
					if ("enableBanner".equals(eElement.getAttribute("name"))) {
						setEnableBanner(Boolean.parseBoolean(eElement.getTextContent()));
					}
					if ("enableOrganisationInfo".equals(eElement.getAttribute("name"))) {
						setEnableOrganisationInfo(Boolean.parseBoolean(eElement.getTextContent()));
					}
				}
			}
		}
	}

	/**
	 * Reads organisationInfo from config file
	 * 
	 * @param doc
	 *            Document object of an XML file
	 */
	private void readOrganisationInfo(Document doc) {
		// System.out.println("Root element :" +
		// doc.getDocumentElement().getNodeName());
		NodeList nList = doc.getElementsByTagName("organization_info");

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
					if ("Email".equals(eElement.getAttribute("name"))) {
						setOrganisation_Email(eElement.getTextContent());
					}
					if ("Website".equals(eElement.getAttribute("name"))) {
						setOrganisation_Website(eElement.getTextContent());
					}
				}
			}
		}
	}

	private void readEmailSettings(Document doc) {
		NodeList nList = doc.getElementsByTagName("smtp_settings");
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);

			NodeList nChildList = nNode.getChildNodes();
			for (int i = 0; i < nChildList.getLength(); i++) {
				Node nChildNode = nChildList.item(i);
				if (nChildNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nChildNode;
					if ("ServerAddress".equals(eElement.getAttribute("name"))) {
						setSmtpServer(eElement.getTextContent());
					}
					if ("SSLPort".equals(eElement.getAttribute("name"))) {
						setSmtpSSLPort(eElement.getTextContent());
					}
					if ("SMTPAuth".equals(eElement.getAttribute("name"))) {
						setSmtpAuthRequired(eElement.getTextContent());
					}
					if ("SendersName".equals(eElement.getAttribute("name"))) {
						setSendersName(eElement.getTextContent());
					}
					if ("SendersEmail".equals(eElement.getAttribute("name"))) {
						setSendersEmail(eElement.getTextContent());
					}
					if ("SendersUserName".equals(eElement.getAttribute("name"))) {
						setSendersUserName(eElement.getTextContent());
					}
					if ("Password".equals(eElement.getAttribute("name"))) {
						setPassword(eElement.getTextContent());
					}
					if ("ReceiversName".equals(eElement.getAttribute("name"))) {
						setReceiversName(eElement.getTextContent());
					}
					if ("ReceiversEmail".equals(eElement.getAttribute("name"))) {
						setReceiversEmail(eElement.getTextContent());
					}
					if ("EmailSubject".equals(eElement.getAttribute("name"))) {
						setEmailSubject(eElement.getTextContent());
					}
					if ("EmailMessage".equals(eElement.getAttribute("name"))) {
						setMessageText(eElement.getTextContent());
					}
				}
			}
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

	public String getOrganisation_Country() {
		return Organisation_Country;
	}

	public void setOrganisation_Country(String organisation_Country) {
		Organisation_Country = organisation_Country;
	}

	public String getOrganisation_Contact_Number() {
		return Organisation_Contact_Number;
	}

	public void setOrganisation_Contact_Number(String organisation_Contact_Number) {
		Organisation_Contact_Number = organisation_Contact_Number;
	}

	public String getOrganisation_Email() {
		return Organisation_Email;
	}

	public void setOrganisation_Email(String organisation_Email) {
		Organisation_Email = organisation_Email;
	}

	public String getOrganisation_Website() {
		return Organisation_Website;
	}

	public void setOrganisation_Website(String organisation_Website) {
		Organisation_Website = organisation_Website;
	}

	public String getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(String logLevel) {
		this.logLevel = logLevel;
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

	public String getLogRootDir() {
		return logRootDir;
	}

	public void setLogRootDir(String logRootDir) {
		this.logRootDir = logRootDir;
	}

	public boolean isEnableGUITestSelector() {
		return enableGUITestSelector;
	}

	public void setEnableGUITestSelector(boolean enableGUITestSelector) {
		this.enableGUITestSelector = enableGUITestSelector;
	}

	public String getLogSubDir() {
		return logSubDir;
	}

	public void setLogSubDir(String logSubDir) {
		this.logSubDir = logSubDir;
	}

	public String getSmtpServer() {
		return smtpServer;
	}

	public void setSmtpServer(String smtpServer) {
		this.smtpServer = smtpServer;
	}

	public String getSmtpSSLPort() {
		return smtpSSLPort;
	}

	public void setSmtpSSLPort(String smtpSSLPort) {
		this.smtpSSLPort = smtpSSLPort;
	}

	public String getSmtpAuthRequired() {
		return smtpAuthRequired;
	}

	public void setSmtpAuthRequired(String smtpAuthRequired) {
		this.smtpAuthRequired = smtpAuthRequired;
	}

	public String getSendersName() {
		return sendersName;
	}

	public void setSendersName(String sendersName) {
		this.sendersName = sendersName;
	}

	public String getSendersEmail() {
		return sendersEmail;
	}

	public void setSendersEmail(String sendersEmail) {
		this.sendersEmail = sendersEmail;
	}

	public String getSendersUserName() {
		return sendersUserName;
	}

	public void setSendersUserName(String sendersUserName) {
		this.sendersUserName = sendersUserName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getReceiversName() {
		return receiversName;
	}

	public void setReceiversName(String receiversName) {
		this.receiversName = receiversName;
	}

	public String getReceiversEmail() {
		return receiversEmail;
	}

	public void setReceiversEmail(String receiversEmail) {
		this.receiversEmail = receiversEmail;
	}

	public String getEmailSubject() {
		return emailSubject;
	}

	public void setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;
	}

	public String getMessageText() {
		return messageText;
	}

	public void setMessageText(String messageText) {
		this.messageText = messageText;
	}

	public boolean isEnableBanner() {
		return enableBanner;
	}

	public void setEnableBanner(boolean enableBanner) {
		this.enableBanner = enableBanner;
	}

	public boolean isEnableOrganisationInfo() {
		return enableOrganisationInfo;
	}

	public void setEnableOrganisationInfo(boolean enableOrganisationInfo) {
		this.enableOrganisationInfo = enableOrganisationInfo;
	}
}