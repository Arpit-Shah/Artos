package com.arpitos.framework;

import com.arpitos.framework.infra.TestContext;

public class Static_Store {
	
	public static final String toolName = "Arpitos";
	
	// Global object storage Variables
	public static final String GLOBAL_ANNOTATED_TEST_MAP = "ANNOTATED_TEST_MAP";
	public static final FrameworkConfig FWConfig = new FrameworkConfig(true);
	public static final SystemProperties SysProperties = new SystemProperties();
	public static TestContext context;
}
