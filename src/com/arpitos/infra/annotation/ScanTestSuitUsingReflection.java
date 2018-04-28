package com.arpitos.infra.annotation;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.core.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import com.arpitos.infra.TestContext;

public class ScanTestSuitUsingReflection {
	TestContext context;
	String packageName;
	Reflections reflaction;
	Logger logger;

	public ScanTestSuitUsingReflection(TestContext context, String packageName) {
		this.context = context;
		this.packageName = packageName;
		this.logger = context.getLogger();
	}

	private void prepareReflectionObject() {

		logger.trace("Scanning package using Reflections : " + packageName);
		reflaction = new Reflections(packageName, new MethodAnnotationsScanner(), new TypeAnnotationsScanner(), new SubTypesScanner(false));
	}

	public Map<String, TestObjectWrapper> invoke() {
		prepareReflectionObject();
		
		Map<String, TestObjectWrapper> testMap = new HashMap<String, TestObjectWrapper>();
		for (Class<?> cl : reflaction.getTypesAnnotatedWith(Testcase.class)) {
			Testcase testcase = cl.getAnnotation(Testcase.class);

			// @formatter:off
			logger.trace("@Testcase = " + cl.getName()
			+ "\nskip : " + testcase.skip()
			+ "\nscenario : " + testcase.scenario()
			+ "\ndecription : " + testcase.decription()
			+ "\npreparedBy : " + testcase.preparedBy()
			+ "\npreparationDate : " + testcase.preparationDate()
			+ "\nreviewedBy : " + testcase.reviewedBy()
			+ "\nreviewDate : " + testcase.reviewDate()
			);
			// @formatter:on
			
			TestObjectWrapper testobj = new TestObjectWrapper(cl, testcase.skip(), testcase.scenario(), testcase.decription(), testcase.preparedBy(),
					testcase.preparationDate(), testcase.reviewedBy(), testcase.reviewDate());
			testMap.put(cl.getName(), testobj);
		}
		for (Method method : reflaction.getMethodsAnnotatedWith(BeforeTest.class)) {
			logger.trace("@BeforeTest = " + method.getName() + " : " + method.getDeclaringClass().getName());
		}
		for (Method method : reflaction.getMethodsAnnotatedWith(BeforeTestsuit.class)) {
			logger.trace("@BeforeTestsuit = " + method.getName() + " : " + method.getDeclaringClass().getName());
		}
		for (Method method : reflaction.getMethodsAnnotatedWith(AfterTest.class)) {
			logger.trace("@AfterTest = " + method.getName() + " : " + method.getDeclaringClass().getName());
		}
		for (Method method : reflaction.getMethodsAnnotatedWith(AfterTestsuit.class)) {
			logger.trace("@AfterTestsuit = " + method.getName() + " : " + method.getDeclaringClass().getName());
		}

		return testMap;
	}
}
