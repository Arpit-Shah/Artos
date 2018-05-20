package com.arpitos.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;

//Make the annotation available at runtime:
@Retention(RetentionPolicy.RUNTIME)
// Allow to use only on types:
@Target(ElementType.METHOD)
public @interface AfterTestsuit {
	
}
