package com.arpitos.interfaces;

import com.arpitos.infra.Enums.TestStatus;
import com.arpitos.infra.TestContext;
import com.arpitos.utils.Utils;

/**
 * Implemented by each test cases. Provides minimum decoration require for each
 * test cases
 * 
 * @author ArpitS
 *
 */
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
			context.setCurrentTestStatus(TestStatus.FAIL);
			Utils.writePrintStackTrace(context, e);
			context.generateTestSummary(cls.getName());
		}
	}

	public void onExecute(TestContext context) throws Exception;

	public void execute(TestContext context) throws Exception;

}
