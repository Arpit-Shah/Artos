package com.arpitos.interfaces;

import com.arpitos.infra.TestContext;

public interface TestExecutor {
	public void onExecute(TestContext context) throws Exception;
}
