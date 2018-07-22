// Copyright <2018> <Arpitos>

// Permission is hereby granted, free of charge, to any person obtaining a copy of this software
// and associated documentation files (the "Software"), to deal in the Software without restriction,
// including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
// and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
// subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
// 
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
// INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
// IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
// WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
// OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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
