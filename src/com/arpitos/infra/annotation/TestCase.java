package com.arpitos.infra.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;

//Make the annotation available at runtime:
@Retention(RetentionPolicy.RUNTIME)
// Allow to use only on types:
@Target(ElementType.TYPE)
public @interface TestCase {
	boolean skip();

	int sequence();

	String label();
}
