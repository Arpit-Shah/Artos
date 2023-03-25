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
package com.artos.framework.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;

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

import org.apache.logging.log4j.Level;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.artos.framework.FWStaticStore;

/**
 * This class is responsible for storing framework Configuration. During test
 * suit execution XML file will be searched at location ./conf
 */
public class FrameworkConfigParser {

	final File fXmlFile = new File(FWStaticStore.CONFIG_BASE_DIR + "framework_configuration.xml");
	String profileName = "dev";

	// Organisation Info
	private String Organisation_Name = "<Organisation> PTY LTD";
	private String Organisation_Address = "XX, Test Street, Test address";
	private String Organisation_Country = "NewZealand";
	private String Organisation_Contact_Number = "+64 1234567";
	private String Organisation_Email = "artos.framework@gmail.com";
	private String Organisation_Website = "www.theartos.com";

	// Email Settings Info
	private String emailSMTPServer = "smtp.gmail.com";
	private String emailSMTPSSLPort = "587"; // "465";
	private String emailSMTPAuthRequired = "true";
	private String emailSendersName = "John Murray";
	private String emailSendersEmail = "test@gmail.com";
	private String emailAuthSettingsFilePath = FWStaticStore.CONFIG_BASE_DIR + "user_auth_settings.xml";
	private String emailReceiversEmail = "test@gmail.com";
	private String emailReceiversName = "Mac Murray";
	private String emailSubject = FWStaticStore.TOOL_NAME + " Email Client";
	private String emailBody = "This is a test Email from " + FWStaticStore.TOOL_NAME;

	// Logger
	private String logLevel = "debug";
	private String logRootDir = FWStaticStore.LOG_BASE_DIR;
	private boolean enableLogDecoration = true;
	private boolean enableTextLog = true;
	private boolean enableHTMLLog = false;
	private boolean enableRealTimeLog = false;
	private boolean enableExtentReport = true;
	private boolean enableJUnitReport = false;
	private boolean enableLogCleanup = false;

	// Dashboard
	private String dashBoardRemoteIP = "127.0.0.1";
	private String dashBoardRemotePort = "11111";

	// Features
	private boolean enableGUITestSelector = true;
	private boolean enableGUITestSelectorSeqNumber = true;
	private boolean enableOrganisationInfo = true;
	private boolean enableBanner = true;
	private boolean enableEmailClient = false;
	private boolean enableArtosDebug = false;
	private boolean generateEclipseTemplate = true;
	private boolean generateIntelliJTemplate = false;
	private boolean generateTestScript = false;
	private boolean stopOnFail = false;
	private boolean enableDashBoard = false;

	/**
	 * Constructor
	 * 
	 * @param createIfNotPresent enables creation of default configuration file if
	 *                           not present
	 * @param profileName        profile name for choosing correct framework
	 *                           configuration
	 */
	public FrameworkConfigParser(boolean createIfNotPresent, String profileName) {
		this.profileName = profileName;

		// If profile name is not provided then apply default settings
		// if (null == profileName || "".equals(profileName)) {
		// System.err.println("Profile Name is not provided. Default Framework Config is
		// applied");
		// return;
		// }

		readXMLConfig(createIfNotPresent);
	}

	/**
	 * Reads Framework configuration file and set global values so framework
	 * configurations is available to everyone
	 * 
	 * @param createIfNotPresent enables creation of default configuration file if
	 *                           not present
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
			readDashBoardConfig(doc);
			readFeatures(doc);
			if (isEnableEmailClient()) {
				readEmailConfig(doc);
			}
		} catch (FileNotFoundException fe) {
			System.out.println(fe.getMessage() + "\n" + "Fall back to Default Organisation values");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ===============================================================
	// Write
	// ===============================================================

	/**
	 * Writes default framework configuration file
	 * 
	 * @param fXmlFile Destination file object
	 * @throws Exception
	 */
	private void writeDefaultConfig(File fXmlFile) throws Exception {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		docFactory.setNamespaceAware(true);
		docFactory.setValidating(true);
		Schema schema = sf.newSchema(
				new StreamSource(FWStaticStore.CONFIG_BASE_DIR + File.separator + "framework_configuration.xsd"));
		docFactory.setSchema(schema);

		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		// root elements
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("configuration");
		rootElement.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xsi:noNamespaceSchemaLocation",
				"framework_configuration.xsd");
		doc.appendChild(rootElement);

		addOrganisatioInfo(doc, rootElement);
		addLoggerConfig(doc, rootElement);
		addEmailConfig(doc, rootElement);
		addDashBoardConfig(doc, rootElement);
		addFeatureList(doc, rootElement);

		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(fXmlFile);

		transformer.transform(source, result);

	}

	private void addFeatureList(Document doc, Element rootElement) {
		// Features
		Element features = doc.createElement("features");
		features.setAttribute("profile", "dev");
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
			property.appendChild(doc.createTextNode(Boolean.toString(isEnableGUITestSelectorSeqNumber())));
			features.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("enableGUITestSelectorSeqNumber");
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
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(Boolean.toString(isEnableEmailClient())));
			features.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("enableEmailClient");
			property.setAttributeNode(attr);
		}
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(Boolean.toString(isEnableArtosDebug())));
			features.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("enableArtosDebug");
			property.setAttributeNode(attr);
		}

		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(Boolean.toString(isGenerateEclipseTemplate())));
			features.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("generateEclipseTemplate");
			property.setAttributeNode(attr);
		}
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(Boolean.toString(isGenerateIntelliJTemplate())));
			features.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("generateIntelliJTemplate");
			property.setAttributeNode(attr);
		}
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(Boolean.toString(isGenerateTestScript())));
			features.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("generateTestScript");
			property.setAttributeNode(attr);
		}
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(Boolean.toString(isStopOnFail())));
			features.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("stopOnFail");
			property.setAttributeNode(attr);
		}
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(Boolean.toString(isEnableDashBoard())));
			features.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("enableDashBoard");
			property.setAttributeNode(attr);
		}
	}

	private void addEmailConfig(Document doc, Element rootElement) {
		// SMTP Settings
		Element smtp_settings = doc.createElement("smtp_settings");
		smtp_settings.setAttribute("profile", "dev");
		rootElement.appendChild(smtp_settings);

		// Properties of Organisation Info
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(getEmailSMTPServer()));
			smtp_settings.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("ServerAddress");
			property.setAttributeNode(attr);
		}
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(getEmailSMTPSSLPort()));
			smtp_settings.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("SSLPort");
			property.setAttributeNode(attr);
		}
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(getEmailSMTPAuthRequired()));
			smtp_settings.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("SMTPAuth");
			property.setAttributeNode(attr);
		}
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(getEmailSendersName()));
			smtp_settings.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("SendersName");
			property.setAttributeNode(attr);
		}
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(getEmailSendersEmail()));
			smtp_settings.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("SendersEmail");
			property.setAttributeNode(attr);
		}
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(getEmailAuthSettingsFilePath()));
			smtp_settings.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("emailAuthSettingsFilePath");
			property.setAttributeNode(attr);
		}
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(getEmailReceiversEmail()));
			smtp_settings.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("ReceiversEmail");
			property.setAttributeNode(attr);
		}
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(getEmailReceiversName()));
			smtp_settings.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("ReceiversName");
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
			property.appendChild(doc.createTextNode(getEmailBody()));
			smtp_settings.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("EmailMessage");
			property.setAttributeNode(attr);
		}
	}

	private void addLoggerConfig(Document doc, Element rootElement) {
		// Logger config elements
		Element logger = doc.createElement("logger");
		logger.setAttribute("profile", "dev");
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
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(Boolean.toString(isEnableRealTimeLog())));
			logger.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("enableRealTimeLog");
			property.setAttributeNode(attr);
		}
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(Boolean.toString(isEnableExtentReport())));
			logger.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("enableExtentReport");
			property.setAttributeNode(attr);
		}
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(Boolean.toString(isEnableJUnitReport())));
			logger.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("enableJUnitReport");
			property.setAttributeNode(attr);
		}
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(Boolean.toString(isEnableLogCleanup())));
			logger.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("enableLogCleanup");
			property.setAttributeNode(attr);
		}
	}

	private void addDashBoardConfig(Document doc, Element rootElement) {
		// Logger config elements
		Element logger = doc.createElement("dashboard");
		logger.setAttribute("profile", "dev");
		rootElement.appendChild(logger);
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(getDashBoardRemoteIP()));
			logger.appendChild(property);

			Comment comment = doc.createComment("Default : 127.0.0.1");
			property.getParentNode().insertBefore(comment, property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("dashBoardRemoteIP");
			property.setAttributeNode(attr);
		}
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(getDashBoardRemotePort()));
			logger.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("dashBoardRemotePort");
			property.setAttributeNode(attr);
		}
	}

	private void addOrganisatioInfo(Document doc, Element rootElement) {
		// Organisation Info elements
		Element orgnization_info = doc.createElement("organization_info");
		orgnization_info.setAttribute("profile", "dev");
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
	}

	// ===============================================================
	// Read
	// ===============================================================

	/**
	 * Reads logger info from config file
	 * 
	 * @param doc Document object of XML file
	 */
	private void readLoggerConfig(Document doc) {
		// System.out.println("Root element :" +
		// doc.getDocumentElement().getNodeName());
		NodeList nList = doc.getElementsByTagName("logger");

		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);

			Element element = (Element) nNode;

			// If profile is provided then look for configuration with given profile or else
			// take default
			if (element.hasAttributes()) {
				if (profileName == null || !profileName.equals(element.getAttribute("profile").toString().trim())) {
					if (profileName == null || temp == nList.getLength() - 1) {
						System.err.println("[WARNING]: logger profile with name " + profileName
								+ " does not exist. Applying default");
					}
					continue;
				}
			}

			NodeList nChildList = nNode.getChildNodes();
			for (int i = 0; i < nChildList.getLength(); i++) {
				Node nChildNode = nChildList.item(i);
				if (nChildNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nChildNode;

					if ("logLevel".equals(eElement.getAttribute("name"))) {
						setLogLevel(eElement.getTextContent());
					} else if ("logRootDir".equals(eElement.getAttribute("name"))) {
						String rootDir = Paths.get(eElement.getTextContent()).toString();
						if (rootDir.endsWith("/") || rootDir.endsWith("\\")) {
							setLogRootDir(rootDir);
						} else {
							setLogRootDir(rootDir + File.separator);
						}
					} else if ("enableLogDecoration".equals(eElement.getAttribute("name"))) {
						setEnableLogDecoration(Boolean.parseBoolean(eElement.getTextContent()));
					} else if ("enableTextLog".equals(eElement.getAttribute("name"))) {
						setEnableTextLog(Boolean.parseBoolean(eElement.getTextContent()));
					} else if ("enableHTMLLog".equals(eElement.getAttribute("name"))) {
						setEnableHTMLLog(Boolean.parseBoolean(eElement.getTextContent()));
					} else if ("enableRealTimeLog".equals(eElement.getAttribute("name"))) {
						setEnableRealTimeLog(Boolean.parseBoolean(eElement.getTextContent()));
					} else if ("enableExtentReport".equals(eElement.getAttribute("name"))) {
						setEnableExtentReport(Boolean.parseBoolean(eElement.getTextContent()));
					} else if ("enableJUnitReport".equals(eElement.getAttribute("name"))) {
						setEnableJUnitReport(Boolean.parseBoolean(eElement.getTextContent()));
					} else if ("enableLogCleanup".equals(eElement.getAttribute("name"))) {
						setEnableLogCleanup(Boolean.parseBoolean(eElement.getTextContent()));
					}
				}
			}

			break;
		}
	}

	/**
	 * Reads features info from config file
	 * 
	 * @param doc Document object of an XML file
	 */
	private void readFeatures(Document doc) {
		// System.out.println("Root element :" +
		// doc.getDocumentElement().getNodeName());
		NodeList nList = doc.getElementsByTagName("features");

		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);

			Element element = (Element) nNode;

			// If profile is provided then look for configuration with given profile or else
			// take default
			if (element.hasAttributes()) {
				if (profileName == null || !profileName.equals(element.getAttribute("profile").toString().trim())) {
					if (profileName == null || temp == nList.getLength() - 1) {
						System.err.println("[WARNING]: features profile with name " + profileName
								+ " does not exist. Applying default");
					}
					continue;
				}
			}

			NodeList nChildList = nNode.getChildNodes();
			for (int i = 0; i < nChildList.getLength(); i++) {
				Node nChildNode = nChildList.item(i);
				if (nChildNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nChildNode;

					if ("enableGUITestSelector".equals(eElement.getAttribute("name"))) {
						setEnableGUITestSelector(Boolean.parseBoolean(eElement.getTextContent()));
					} else if ("enableGUITestSelectorSeqNumber".equals(eElement.getAttribute("name"))) {
						setEnableGUITestSelectorSeqNumber(Boolean.parseBoolean(eElement.getTextContent()));
					} else if ("enableBanner".equals(eElement.getAttribute("name"))) {
						setEnableBanner(Boolean.parseBoolean(eElement.getTextContent()));
					} else if ("enableOrganisationInfo".equals(eElement.getAttribute("name"))) {
						setEnableOrganisationInfo(Boolean.parseBoolean(eElement.getTextContent()));
					} else if ("enableEmailClient".equals(eElement.getAttribute("name"))) {
						setEnableEmailClient(Boolean.parseBoolean(eElement.getTextContent()));
					} else if ("enableArtosDebug".equals(eElement.getAttribute("name"))) {
						setEnableArtosDebug(Boolean.parseBoolean(eElement.getTextContent()));
					} else if ("generateEclipseTemplate".equals(eElement.getAttribute("name"))) {
						setGenerateEclipseTemplate(Boolean.parseBoolean(eElement.getTextContent()));
					} else if ("generateIntelliJTemplate".equals(eElement.getAttribute("name"))) {
						setGenerateIntelliJTemplate(Boolean.parseBoolean(eElement.getTextContent()));
					} else if ("generateTestScript".equals(eElement.getAttribute("name"))) {
						setGenerateTestScript(Boolean.parseBoolean(eElement.getTextContent()));
					} else if ("stopOnFail".equals(eElement.getAttribute("name"))) {
						setStopOnFail(Boolean.parseBoolean(eElement.getTextContent()));
					} else if ("enableDashBoard".equals(eElement.getAttribute("name"))) {
						setEnableDashBoard(Boolean.parseBoolean(eElement.getTextContent()));
					}
				}
			}

			break;
		}
	}

	/**
	 * Reads organisationInfo from config file
	 * 
	 * @param doc Document object of an XML file
	 */
	private void readOrganisationInfo(Document doc) {
		// System.out.println("Root element :" +
		// doc.getDocumentElement().getNodeName());
		NodeList nList = doc.getElementsByTagName("organization_info");

		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);

			Element element = (Element) nNode;

			// If profile is provided then look for configuration with given profile or else
			// take default
			if (element.hasAttributes()) {
				if (profileName == null || !profileName.equals(element.getAttribute("profile").toString().trim())) {
					if (profileName == null || temp == nList.getLength() - 1) {
						System.err.println("[WARNING]: organization_info profile with name " + profileName
								+ " does not exist. Applying default");
					}
					continue;
				}
			}

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
					} else if ("Address".equals(eElement.getAttribute("name"))) {
						setOrganisation_Address(eElement.getTextContent());
					} else if ("Country".equals(eElement.getAttribute("name"))) {
						setOrganisation_Country(eElement.getTextContent());
					} else if ("Contact_Number".equals(eElement.getAttribute("name"))) {
						setOrganisation_Contact_Number(eElement.getTextContent());
					} else if ("Email".equals(eElement.getAttribute("name"))) {
						setOrganisation_Email(eElement.getTextContent());
					} else if ("Website".equals(eElement.getAttribute("name"))) {
						setOrganisation_Website(eElement.getTextContent());
					}
				}
			}

			break;
		}
	}

	/**
	 * Reads dash board configuration from config file
	 * 
	 * @param doc Document object of an XML file
	 */
	private void readDashBoardConfig(Document doc) {
		// System.out.println("Root element :" +
		// doc.getDocumentElement().getNodeName());
		NodeList nList = doc.getElementsByTagName("dashboard");

		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);

			Element element = (Element) nNode;

			// If profile is provided then look for configuration with given profile or else
			// take default
			if (element.hasAttributes()) {
				if (profileName == null || !profileName.equals(element.getAttribute("profile").toString().trim())) {
					if (profileName == null || temp == nList.getLength() - 1) {
						System.err.println("[WARNING]: dashboard profile with name " + profileName
								+ " does not exist. Applying default");
					}
					continue;
				}
			}

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
					if ("dashBoardRemoteIP".equals(eElement.getAttribute("name"))) {
						setDashBoardRemoteIP(eElement.getTextContent());
					} else if ("dashBoardRemotePort".equals(eElement.getAttribute("name"))) {
						setDashBoardRemotePort(eElement.getTextContent());
					}
				}
			}

			break;
		}
	}

	private void readEmailConfig(Document doc) {
		NodeList nList = doc.getElementsByTagName("smtp_settings");

		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);

			Element element = (Element) nNode;

			// If profile is provided then look for configuration with given profile or else
			// take default
			if (element.hasAttributes()) {
				if (profileName == null || !profileName.equals(element.getAttribute("profile").toString().trim())) {
					if (profileName == null || temp == nList.getLength() - 1) {
						System.err.println("[WARNING]: smtp_settings profile with name " + profileName
								+ " does not exist. Applying default");
					}
					continue;
				}
			}

			NodeList nChildList = nNode.getChildNodes();
			for (int i = 0; i < nChildList.getLength(); i++) {
				Node nChildNode = nChildList.item(i);
				if (nChildNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nChildNode;
					if ("ServerAddress".equals(eElement.getAttribute("name"))) {
						setEmailSMTPServer(eElement.getTextContent());
					} else if ("SSLPort".equals(eElement.getAttribute("name"))) {
						setEmailSMTPSSLPort(eElement.getTextContent());
					} else if ("SMTPAuth".equals(eElement.getAttribute("name"))) {
						setEmailSMTPAuthRequired(eElement.getTextContent());
					} else if ("SendersEmail".equals(eElement.getAttribute("name"))) {
						setEmailSendersEmail(eElement.getTextContent());
					} else if ("SendersName".equals(eElement.getAttribute("name"))) {
						setEmailSendersName(eElement.getTextContent());
					} else if ("emailAuthSettingsFilePath".equals(eElement.getAttribute("name"))) {
						setEmailAuthSettingsFilePath(Paths.get(eElement.getTextContent()).toString());
					} else if ("ReceiversEmail".equals(eElement.getAttribute("name"))) {
						setEmailReceiversEmail(eElement.getTextContent());
					} else if ("ReceiversName".equals(eElement.getAttribute("name"))) {
						setEmailReceiversName(eElement.getTextContent());
					} else if ("EmailSubject".equals(eElement.getAttribute("name"))) {
						setEmailSubject(eElement.getTextContent());
					} else if ("EmailMessage".equals(eElement.getAttribute("name"))) {
						setEmailBody(eElement.getTextContent());
					}
				}
			}

			break;
		}
	}

	/**
	 * Returns Log Level Enum value based on Framework configuration set in XML file
	 * 
	 * @see Level
	 * 
	 * @return LogLevel
	 * @see Level
	 */
	public Level getLoglevelFromXML() {
		String logLevel = getLogLevel();
		if (logLevel.equals("info")) {
			return Level.INFO;
		}
		if (logLevel.equals("all")) {
			return Level.ALL;
		}
		if (logLevel.equals("fatal")) {
			return Level.FATAL;
		}
		if (logLevel.equals("trace")) {
			return Level.TRACE;
		}
		if (logLevel.equals("warn")) {
			return Level.WARN;
		}
		if (logLevel.equals("debug")) {
			return Level.DEBUG;
		}
		return Level.DEBUG;
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

	public boolean isGenerateTestScript() {
		return generateTestScript;
	}

	public void setGenerateTestScript(boolean generateTestScript) {
		this.generateTestScript = generateTestScript;
	}

	public boolean isStopOnFail() {
		return stopOnFail;
	}

	public void setStopOnFail(boolean stopOnFail) {
		this.stopOnFail = stopOnFail;
	}

	public boolean isGenerateEclipseTemplate() {
		return generateEclipseTemplate;
	}

	public void setGenerateEclipseTemplate(boolean generateEclipseTemplate) {
		this.generateEclipseTemplate = generateEclipseTemplate;
	}

	public boolean isEnableExtentReport() {
		return enableExtentReport;
	}

	public void setEnableExtentReport(boolean enableExtentReport) {
		this.enableExtentReport = enableExtentReport;
	}

	public String getEmailSMTPServer() {
		return emailSMTPServer;
	}

	public void setEmailSMTPServer(String emailSMTPServer) {
		this.emailSMTPServer = emailSMTPServer;
	}

	public String getEmailSMTPSSLPort() {
		return emailSMTPSSLPort;
	}

	public void setEmailSMTPSSLPort(String emailSMTPSSLPort) {
		this.emailSMTPSSLPort = emailSMTPSSLPort;
	}

	public String getEmailSMTPAuthRequired() {
		return emailSMTPAuthRequired;
	}

	public void setEmailSMTPAuthRequired(String emailSMTPAuthRequired) {
		this.emailSMTPAuthRequired = emailSMTPAuthRequired;
	}

	public String getEmailSendersName() {
		return emailSendersName;
	}

	public void setEmailSendersName(String emailSendersName) {
		this.emailSendersName = emailSendersName;
	}

	public String getEmailSendersEmail() {
		return emailSendersEmail;
	}

	public void setEmailSendersEmail(String emailSendersEmail) {
		this.emailSendersEmail = emailSendersEmail;
	}

	public String getEmailReceiversEmail() {
		return emailReceiversEmail;
	}

	public void setEmailReceiversEmail(String emailReceiversEmail) {
		this.emailReceiversEmail = emailReceiversEmail;
	}

	public String getEmailReceiversName() {
		return emailReceiversName;
	}

	public void setEmailReceiversName(String emailReceiversName) {
		this.emailReceiversName = emailReceiversName;
	}

	public String getEmailSubject() {
		return emailSubject;
	}

	public void setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;
	}

	public String getEmailBody() {
		return emailBody;
	}

	public void setEmailBody(String emailBody) {
		this.emailBody = emailBody;
	}

	public boolean isEnableEmailClient() {
		return enableEmailClient;
	}

	public void setEnableEmailClient(boolean enableEmailClient) {
		this.enableEmailClient = enableEmailClient;
	}

	public String getEmailAuthSettingsFilePath() {
		return emailAuthSettingsFilePath;
	}

	public void setEmailAuthSettingsFilePath(String emailAuthSettingsFilePath) {
		this.emailAuthSettingsFilePath = emailAuthSettingsFilePath;
	}

	public boolean isEnableGUITestSelectorSeqNumber() {
		return enableGUITestSelectorSeqNumber;
	}

	public void setEnableGUITestSelectorSeqNumber(boolean enableGUITestSelectorSeqNumber) {
		this.enableGUITestSelectorSeqNumber = enableGUITestSelectorSeqNumber;
	}

	public boolean isEnableArtosDebug() {
		return enableArtosDebug;
	}

	public void setEnableArtosDebug(boolean enableArtosDebug) {
		this.enableArtosDebug = enableArtosDebug;
	}

	public boolean isGenerateIntelliJTemplate() {
		return generateIntelliJTemplate;
	}

	public void setGenerateIntelliJTemplate(boolean generateIntelliJTemplate) {
		this.generateIntelliJTemplate = generateIntelliJTemplate;
	}

	public boolean isEnableDashBoard() {
		return enableDashBoard;
	}

	public void setEnableDashBoard(boolean enableDashBoard) {
		this.enableDashBoard = enableDashBoard;
	}

	public String getDashBoardRemoteIP() {
		return dashBoardRemoteIP;
	}

	public void setDashBoardRemoteIP(String dashBoardRemoteIP) {
		this.dashBoardRemoteIP = dashBoardRemoteIP;
	}

	public String getDashBoardRemotePort() {
		return dashBoardRemotePort;
	}

	public void setDashBoardRemotePort(String dashBoardRemotePort) {
		this.dashBoardRemotePort = dashBoardRemotePort;
	}

	public boolean isEnableJUnitReport() {
		return enableJUnitReport;
	}

	public void setEnableJUnitReport(boolean enableJUnitReport) {
		this.enableJUnitReport = enableJUnitReport;
	}

	public boolean isEnableRealTimeLog() {
		return enableRealTimeLog;
	}

	/**
	 * Enables real time log
	 * 
	 * @param enableRealTimeLog true|false
	 */
	public void setEnableRealTimeLog(boolean enableRealTimeLog) {
		this.enableRealTimeLog = enableRealTimeLog;
	}

	/**
	 * Return true or false based on configuration
	 * 
	 * @return true|false based on user setting
	 */
	public boolean isEnableLogCleanup() {
		return enableLogCleanup;
	}

	/**
	 * Enables log clean up
	 * 
	 * @param enableLogCleanup true|false
	 */
	public void setEnableLogCleanup(boolean enableLogCleanup) {
		this.enableLogCleanup = enableLogCleanup;
	}
}
