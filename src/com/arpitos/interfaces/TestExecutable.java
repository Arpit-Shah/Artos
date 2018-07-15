package com.arpitos.interfaces;

import java.util.Map;

import com.arpitos.framework.Enums.TestStatus;
import com.arpitos.framework.FWStatic_Store;
import com.arpitos.framework.TestObjectWrapper;
import com.arpitos.framework.infra.TestContext;
import com.arpitos.utils.UtilsFramework;

/**
 * Implemented by each test cases. Provides minimum decoration require for each
 * test cases
 * 
 * @author ArpitS
 *
 */
public interface TestExecutable {

	default public void onExecute(TestContext context, Class<?> cls) throws Exception {
		long testStartTime = System.currentTimeMillis();

		try {

			// @formatter:off
			@SuppressWarnings("unchecked")
			Map<String, TestObjectWrapper> testMap = (Map<String, TestObjectWrapper>) context.getGlobalObject(FWStatic_Store.GLOBAL_ANNOTATED_TEST_MAP);
			TestObjectWrapper testObject = testMap.get(cls.getName());
			
			context.getLogger().info("*************************************************************************"
									+ "\nTest Name	: " + cls.getName()
									+ "\nWritten BY	: " + testObject.getTestPlanPreparedBy()
									+ "\nDate		: " + testObject.getTestPlanPreparationDate()
									+ "\nShort Desc	: " + testObject.getTestPlanDescription()
									+ "\n-------------------------------------------------------------------------");
			// @formatter:on

			// --------------------------------------------------------------------------------------------
			execute(context);
			// --------------------------------------------------------------------------------------------

			context.generateTestSummary(cls.getName(), testStartTime, System.currentTimeMillis());
		} catch (Exception e) {
			context.setTestStatus(TestStatus.FAIL);
			UtilsFramework.writePrintStackTrace(context, e);
			context.generateTestSummary(cls.getName(), testStartTime, System.currentTimeMillis());
		} catch (Throwable ex) {
			context.setTestStatus(TestStatus.FAIL);
			UtilsFramework.writePrintStackTrace(context, ex);
			context.generateTestSummary(cls.getName(), testStartTime, System.currentTimeMillis());
		}
	}

	public void onExecute(TestContext context) throws Exception;

	public void execute(TestContext context) throws Exception;

}
