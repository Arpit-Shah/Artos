package com.arpitos.framework;

import com.arpitos.framework.infra.TestContext;
import com.arpitos.utils.Convert;

/**
 * This class provides container which holds all static element of test
 * framework
 * 
 * @author arpit
 *
 */
public class Static_Store {

	public static final String toolName = "Arpitos";

	// Global object storage Variables
	public static final String GLOBAL_ANNOTATED_TEST_MAP = "ANNOTATED_TEST_MAP";
	public static final FrameworkConfig FWConfig = new FrameworkConfig(true);
	public static final SystemProperties SysProperties = new SystemProperties();
	public static final Convert convert = new Convert();
	public static TestContext context;
}
