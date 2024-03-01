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
package com.artos.framework;

import java.util.Properties;

/**
 * This class defines the current version of framework
 *
 */
public class Version {

	static String version = "0.0.0";
	static String buidDate = "0.0.0";
	
	public Version() {
		readPropertiesFile();
	}

	public String getBuildVersion() {
		if(null == version) {
			return "0.0.0";
		}
		return version;
	}

	public String getBuildDate() {
		if(null == buidDate) {
			return "0.0.0";
		}
		return buidDate;
	}

	public void readPropertiesFile() {
		try {
			Properties prop = new Properties();
			prop.load(getClass().getResourceAsStream("/com/artos/version/version.properties"));
			version = prop.getProperty("version");
			buidDate = prop.getProperty("build.date");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
