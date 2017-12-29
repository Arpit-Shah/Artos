package com.arpit.framework;

import com.arpit.infra.TestContext;
import com.arpit.infra.TestContext.Status;
import com.arpit.utils.Utils;

public abstract class Test {

	public void onExecute(TestContext context, Class<?> cls, String author, String date, String description) throws Exception {
		try {

			context.getLogger().info("\n*************************************************************************");
			context.getLogger().info("Test Name	: " + cls.getName());
			context.getLogger().info("Written BY	: " + author);
			context.getLogger().info("Date		: " + date);
			context.getLogger().info("Short Desc	: " + description);
			context.getLogger().info("-------------------------------------------------------------------------");

			// --------------------------------------------------------------------------------------------
			execute(context);
			// --------------------------------------------------------------------------------------------

			context.generateTestSummary(cls.getName());
		} catch (Exception e) {
			context.setCurrentTestStatus(Status.FAIL);
			Utils.writePrintStackTrace(context, e);
			Utils.testCleanUp(context);
			context.generateTestSummary(cls.getName());
		}
	}

	protected abstract void execute(TestContext context) throws Exception;

}
