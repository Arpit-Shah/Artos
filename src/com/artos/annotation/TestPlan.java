/*******************************************************************************
 * Copyright (C) 2018-2019 Arpit Shah and Artos Contributors
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package com.artos.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Make the annotation available at runtime:
@Retention(RetentionPolicy.RUNTIME)
// Allow to use only on types:
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface TestPlan {

	/**
	 * Test Description
	 * 
	 * @return test short description
	 */
	String description() default "";

	/**
	 * Name of the person who prepared the test
	 * 
	 * @return test engineer name
	 */
	String preparedBy() default "";

	/**
	 * Date of the test preparation
	 * 
	 * @return test case preparation date
	 */
	String preparationDate() default "";

	/**
	 * Test reviewer name
	 * 
	 * @return test case reviewer name
	 */
	String reviewedBy() default "";

	/**
	 * Test review date
	 * 
	 * @return test case review date
	 */
	String reviewDate() default "";

	/**
	 * Behaviour driven test plan, prefer to write it in Gherkin language
	 * 
	 * @return test plan in Gherkin format
	 */
	String bdd() default "";
}
