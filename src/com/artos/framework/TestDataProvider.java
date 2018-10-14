package com.artos.framework;

import java.lang.reflect.Method;

public class TestDataProvider {

	String dataProviderName;
	Class<?> classOfTheMethod;
	Method method;
	boolean staticMethod;

	/**
	 * @param method method which has {@code DataProvider} annotation
	 * @param dataProviderName name given to {@code DataProvider}
	 * @param classOfTheMethod class object where method belongs to
	 * @param staticMethod true if method is static
	 */
	public TestDataProvider(Method method, String dataProviderName, Class<?> classOfTheMethod, boolean staticMethod) {
		super();
		this.method = method;
		this.dataProviderName = dataProviderName;
		this.classOfTheMethod = classOfTheMethod;
		this.staticMethod = staticMethod;
	}

	public String getDataProviderName() {
		return dataProviderName;
	}

	public Class<?> getClassOfTheMethod() {
		return classOfTheMethod;
	}

	public Method getMethod() {
		return method;
	}

	public boolean isStaticMethod() {
		return staticMethod;
	}
}
