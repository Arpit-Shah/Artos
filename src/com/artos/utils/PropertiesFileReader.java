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
package com.artos.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;

public class PropertiesFileReader {

	Properties prop = new Properties();

	HashMap<String, String> hmap = new HashMap<String, String>();
	File propFile;

	/**
	 * Reads properties file into HashMap, If file is not present function will
	 * attempts to create empty file to provided location
	 * 
	 * @param file
	 *            file to be read as property file
	 * @throws Exception
	 *             if file io fails
	 */
	public PropertiesFileReader(File file) throws Exception {
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			file.createNewFile();
		}
		this.propFile = file;
		ReadPropFile();
	}

	/**
	 * Read properties file into Hash map
	 */
	private void ReadPropFile() {
		InputStream input = null;
		hmap.clear();
		try {
			input = new FileInputStream(propFile);
			prop.load(input);
			Enumeration<?> e = prop.propertyNames();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				String value = prop.getProperty(key);
				hmap.put(key, value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Gets value from properties file with given key
	 * 
	 * @param key
	 *            property file key string
	 * @return String value of queried key
	 */
	public String getValue(String key) {
		return hmap.get(key);
	}

	/**
	 * Gets value from .properties file with given key, If key is not found default
	 * value is returned.
	 * 
	 * @param key
	 *            property file key string
	 * @param defaultValue
	 *            property file default value
	 * @return value for provided key from Hash Map
	 */
	public String getValue(String key, String defaultValue) {
		String value = hmap.get(key);
		if (null == value) {
			return defaultValue;
		} else {
			return value;
		}
	}

	/**
	 * Add Single Key Value pair to .properies file
	 * 
	 * @param key
	 *            property key string
	 * @param value
	 *            property file value string
	 */
	public void setValue(String key, String value) {
		HashMap<String, String> keyValueMap = new HashMap<String, String>();
		keyValueMap.put(key, value);
		setValue(keyValueMap);
	}

	/**
	 * Adds/updates all KeyValues from HashMap to .properties file
	 * 
	 * @param keyValueMap
	 *            Hashmap with KeyValue pair
	 */
	public void setValue(HashMap<String, String> keyValueMap) {
		OutputStream output = null;
		try {
			output = new FileOutputStream(propFile);

			for (Entry<String, String> entry : keyValueMap.entrySet()) {
				prop.setProperty(entry.getKey(), entry.getValue());
			}

			prop.store(output, null);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		// Refill hashmap after writing to property file
		ReadPropFile();
	}
}
