package com.arpitos.infra.annotation;

import java.util.Map;

import org.apache.logging.log4j.core.Logger;

import com.arpitos.framework.ArpitosStatic_Store;
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
		    	logger.info("\nPreparedBy : " + testObject.getTestPlanPreparedBy());
		    	logger.info("\nPreparationDate : " + testObject.getTestPlanPreparationDate());
		    	logger.info("\nReviewedBy : " + testObject.getTestreviewedBy());
		    	logger.info("\nReviewedDate : " + testObject.getTestReviewDate());
//		    }
			
			
		}
	}

}
