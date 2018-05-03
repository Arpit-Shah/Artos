package com.arpitos.interfaces;

import com.arpitos.infra.Enums.TestStatus;
import com.arpitos.infra.annotation.TestObjectWrapper;

import java.util.Map;

import com.arpitos.framework.ArpitosStatic_Store;
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

	default public void onExecute(TestContext context, Class<?> cls) throws Exception {
		try {

			// @formatter:off
			@SuppressWarnings("unchecked")
			Map<String, TestObjectWrapper> testMap = (Map<String, TestObjectWrapper>) context.getGlobalObject(ArpitosStatic_Store.GLOBAL_ANNOTATED_TEST_MAP);
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
