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

import java.io.File;

import com.artos.framework.xml.FrameworkConfig;

/**
 * This class provides container which holds all static element of test framework
 */
public class FWStaticStore {

	public static final String TOOL_NAME = "Artos";

	// default decoration
	// used for encapsulating headers or separate sections
	public static final String ARTOS_LINE_BREAK_1 = "*************************************************************************";
	// used to lightly separate sections within same test cases (Example: test units)
	public static final String ARTOS_LINE_BREAK_2 = ".........................................................................";
	// Warning/Highlights
	public static final String ARTOS_LINE_BREAK_3 = "=========================================================================";
	// Fail Stamp
	public static final String ARTOS_TEST_FAIL_STAMP = ARTOS_LINE_BREAK_3 + "\n=============================== FAIL HERE ===============================\n" + ARTOS_LINE_BREAK_3;
	// KTF TEST PASSED Stamp
	public static final String ARTOS_KTF_TEST_PASSED_STAMP = ARTOS_LINE_BREAK_3 + "\n============================ KTF TEST PASSED ============================\n" + ARTOS_LINE_BREAK_3;
	// KTF TEST UNIT PASSED Stamp
	public static final String ARTOS_KTF_TESTUNIT_PASSED_STAMP = ARTOS_LINE_BREAK_3 + "\n========================== KTF TEST UNIT PASSED =========================\n" + ARTOS_LINE_BREAK_3;
	// DataProvider Fail Stamp
	public static final String ARTOS_DATAPROVIDER_FAIL_STAMP = ARTOS_LINE_BREAK_3 + "\n=== DataProvider Method failed to return data ===\n"
			+ ARTOS_LINE_BREAK_3;

	// default paths
	public static final String TESTSCRIPT_BASE_DIR = "." + File.separator + "script" + File.separator;
	public static final String CONFIG_BASE_DIR = "." + File.separator + "conf" + File.separator;
	public static final String TEMPLATE_BASE_DIR = "." + File.separator + "template" + File.separator;
	public static final String LOG_BASE_DIR = "." + File.separator + "reporting" + File.separator;

	// Must be kept after default paths initialised
	public static FrameworkConfig frameworkConfig = null;
	public static SystemProperties systemProperties = new SystemProperties();
	public static final String ARTOS_BUILD_VERSION = new Version().getBuildVersion();
	public static final String ARTOS_BUILD_DATE = new Version().getBuildDate();

	// Global Hashmap key name
	public static final String GLOBAL_ANNOTATED_TEST_MAP = "ANNOTATED_TEST_MAP";

	public static void logDebug(String log) {
		if (frameworkConfig.isEnableArtosDebug()) {
			System.err.println("[DEBUG] " + log);
		}
	}

}
