package com.arpit.utils;

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
	 * Reads property file into HashMap, If file is not present function will
	 * attempts to create empty file to provided location
	 * 
	 * @param file
	 * @throws Exception
	 */
	public PropertiesFileReader(File file) throws Exception {
		if (!file.exists()) {
			file.createNewFile();
		}
		this.propFile = file;
		ReadPropFile();
	}

	/**
	 * Read propety file into Hash map
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
	 * @return
	 */
	public String getValue(String key) {
		return hmap.get(key);
	}

	/**
	 * Gets value from .properties file with given key, If key is not found
	 * default value is returned.
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
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
	 * @param value
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
