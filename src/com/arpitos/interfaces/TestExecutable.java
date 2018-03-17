package com.arpitos.interfaces;

import com.arpitos.infra.TestContext;
import com.arpitos.infra.TestContext.Status;
import com.arpitos.utils.Utils;

public interface TestExecutable {
	
	default public void onExecute(TestContext context, Class<?> cls, String author, String date, String description) throws Exception {
		try {

			context.getLogger().info("*************************************************************************");
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
			context.generateTestSummary(cls.getName());
		}
	}
	
	public void onExecute(TestContext context) throws Exception;
	public void execute(TestContext context) throws Exception;

}
