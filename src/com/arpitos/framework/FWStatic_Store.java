package com.arpitos.framework;

import com.arpitos.framework.infra.TestContext;

/**
 * This class provides container which holds all static element of test
 * framework
 * 
 * @author arpit
 *
 */
public class FWStatic_Store {

	public static final String TOOL_NAME = "Arpitos";
	public static final String GLOBAL_ANNOTATED_TEST_MAP = "ANNOTATED_TEST_MAP";
	
	// Global object storage Variables
	public static TestContext context = new TestContext();
}
