package com.arpit.framework;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import com.arpit.infra.OrganisedLog;
import com.arpit.infra.TestContext;
import com.arpit.interfaces.TestExecutor;

public class Tester {

	public static void run(TestExecutor start, ArrayList<TestExecutor> tests, TestExecutor finish, String TestPackageName, int LoopCycle)
			throws Exception {

		// -------------------------------------------------------------------//
		// Prep Conext
		String serialNumber = "A123456789";
		File logDir = new File("./reporting/" + serialNumber);
		//
		// OrganisedLog logger = new OrganisedLog(logDir, TestPackageName,
		// LOG_LEVEL.ALL);
		OrganisedLog logger = new OrganisedLog(logDir.getAbsolutePath(), TestPackageName, true);
		TestContext context = new TestContext(logger);

		long startTime = System.currentTimeMillis();
		context.getLogger().info("\n-------- Start -----------");
		start.onExecute(context);

		for (int index = 0; index < LoopCycle; index++) {

			context.getLogger().info("\n-------- (" + index + ") -----------");

			// --------------------------------------------------------------------------------------------
			for (TestExecutor t : tests) {
				t.onExecute(context);
			}
			// --------------------------------------------------------------------------------------------

		}

		finish.onExecute(context);
		context.getLogger().info("\n-------- Finished -----------");

		context.getLogger().info("PASS:" + context.getCurrentPassCount() + " FAIL:" + context.getCurrentFailCount() + " SKIP:"
				+ context.getCurrentSkipCount() + " KTF:" + context.getCurrentKTFCount() + " TOTAL:" + context.getTotalTestCount());
		long endTime = System.currentTimeMillis();
		context.getLogger()
				.info("Total Test Time : " + String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes((endTime - startTime)),
						TimeUnit.MILLISECONDS.toSeconds((endTime - startTime))
								- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((endTime - startTime)))));
		System.exit(0);
	}

}
