package com.arpitos.infra.annotation;

import java.util.Map;

import org.apache.logging.log4j.core.Logger;

import com.arpitos.infra.TestContext;

public class GenerateTestPlan {

	public GenerateTestPlan(TestContext context, String packageName) {
		Logger logger = context.getLogger();
		Map<String, TestObjectWrapper> testMap = new ScanTestSuitUsingReflection(context, packageName).invoke();
		
		for (Map.Entry<String, TestObjectWrapper> entry : testMap.entrySet()) {
			
			logger.trace("key = " +  entry.getKey());
			logger.trace("value = "+ entry.getValue());
			
			
//		    if(entry.getKey().equals(packageName)){
		    	TestObjectWrapper testObject = entry.getValue();
		    	logger.info("\nScenario : " + testObject.getTestPlanScenario());
		    	logger.info("Description : " + testObject.getTestPlanDescription());
		    	logger.info("PreparedBy : " + testObject.getTestPlanPreparedBy());
		    	logger.info("PreparationDate : " + testObject.getTestPlanPreparationDate());
		    	logger.info("ReviewedBy : " + testObject.getTestreviewedBy());
		    	logger.info("ReviewedDate : " + testObject.getTestReviewDate());
//		    }
			
			
		}
	}

}
