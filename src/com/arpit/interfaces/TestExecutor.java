package com.arpit.interfaces;

import com.arpit.infra.TestContext;

public interface TestExecutor {
	public void onExecute(TestContext context) throws Exception;
}
