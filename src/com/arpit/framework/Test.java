package com.arpit.framework;

import com.arpit.infra.OrganisedLog.LOG_LEVEL;
import com.arpit.infra.TestContext;
import com.arpit.infra.TestContext.Status;
import com.arpit.utils.Utils;

public abstract class Test {

	public void onExecute(TestContext context, Class<?> cls, String author, String date, String description) throws Exception {
		try {

			context.getLogger().println(LOG_LEVEL.INFO, "\n*************************************************************************");
			context.getLogger().println(LOG_LEVEL.INFO, "Test Name	: " + cls.getName());
			context.getLogger().println(LOG_LEVEL.INFO, "Written BY	: " + author);
			context.getLogger().println(LOG_LEVEL.INFO, "Date		: " + date);
			context.getLogger().println(LOG_LEVEL.INFO, "Short Desc	: " + description);
			context.getLogger().println(LOG_LEVEL.INFO, "-------------------------------------------------------------------------");

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