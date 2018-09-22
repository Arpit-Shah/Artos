/*******************************************************************************
 * Copyright (C) 2018 Arpit Shah
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

import java.io.File;

/**
 * This class provides container which holds all static element of test
 * framework
 */
public class FWStaticStore {

	public static final String TOOL_NAME = "Artos";

	// default paths
	public static final String TESTSCRIPT_BASE_DIR = "." + File.separator + "script" + File.separator;
	public static final String CONFIG_BASE_DIR = "." + File.separator + "conf" + File.separator;
	public static final String TEMPLATE_BASE_DIR = "." + File.separator + "template" + File.separator;
	public static final String LOG_BASE_DIR = "." + File.separator + "reporting" + File.separator;

	// Must be kept after default paths initialised
	public static FrameworkConfig frameworkConfig = new FrameworkConfig(true);
	public static SystemProperties systemProperties = new SystemProperties();
	public static final String ARTOS_BUILD_VERSION = new Version().getBuildVersion();
	public static final String ARTOS_BUILD_DATE = new Version().getBuildDate();

	// Global Hashmap key name
	public static final String GLOBAL_ANNOTATED_TEST_MAP = "ANNOTATED_TEST_MAP";

}
