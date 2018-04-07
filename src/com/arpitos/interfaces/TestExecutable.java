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

			// @formatter:off
			context.getLogger().info("*************************************************************************"
									+ "\nTest Name	: " + cls.getName()
									+ "\nWritten BY	: " + author
									+ "\nDate		: " + date
									+ "\nShort Desc	: " + description
									+ "\n-------------------------------------------------------------------------");
			// @formatter:on

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
