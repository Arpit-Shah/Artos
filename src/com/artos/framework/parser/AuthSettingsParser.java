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
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Parses Auth Settings
 *
 */
public class AuthSettingsParser {

	File fXmlFile;
	Map<String, UserAuthParametersParser> authMap;

	/**
	 * Parse file for email authentication settings
	 * 
	 * @param emailAuthSettingFilePath File path for email auth settings
	 */
	public AuthSettingsParser(File emailAuthSettingFilePath) {
		this.fXmlFile = emailAuthSettingFilePath;
		this.authMap = new HashMap<>();

		readXMLConfig();
	}

	private void readXMLConfig() {
		try {
			if (!fXmlFile.exists() || !fXmlFile.isFile()) {
				throw new Exception("File does not exist : " + fXmlFile.getAbsolutePath());
			}

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			// optional, but recommended
			// read this -
			// http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();

			readUsersAuth(doc);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void readUsersAuth(Document doc) {
		// System.out.println("Root element :" +
		// doc.getDocumentElement().getNodeName());
		NodeList nList = doc.getElementsByTagName("user");

		for (int temp = 0; temp < nList.getLength(); temp++) {

			UserAuthParametersParser userAuth = new UserAuthParametersParser();
			String id = null;
			Node nNode = nList.item(temp);

			NodeList nChildList = nNode.getChildNodes();
			for (int i = 0; i < nChildList.getLength(); i++) {
				Node nChildNode = nChildList.item(i);

				if (nChildNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nChildNode;
					if ("id".equals(eElement.getTagName())) {
						id = eElement.getTextContent();
					}
					if ("username".equals(eElement.getTagName())) {
						userAuth.setUserName(eElement.getTextContent());
					}
					if ("password".equals(eElement.getTagName())) {
						userAuth.setPassword(eElement.getTextContent());
					}
				}
			}
			authMap.put(id, userAuth);
		}
	}

	/**
	 * Gets auth parameter by its ID
	 * 
	 * @param id pramater ID
	 * @return {@link UserAuthParametersParser}
	 */
	public UserAuthParametersParser getAuthParametersByID(String id) {
		return getAuthMap().get(id);
	}

	/**
	 * Get auth Map
	 * 
	 * @return {@link Map} Key value pair
	 */
	public Map<String, UserAuthParametersParser> getAuthMap() {
		return authMap;
	}

	/**
	 * Set auth Map
	 * 
	 * @param authMap with key value pair
	 */
	public void setAuthMap(Map<String, UserAuthParametersParser> authMap) {
		this.authMap = authMap;
	}
}
