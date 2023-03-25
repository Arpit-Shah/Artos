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
package com.artos.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.artos.framework.infra.TestContext;

public class UtilsFramework {

	/**
	 * Writes Print StackTrace on console and log file as per chosen option
	 * 
	 * @param context Test context
	 * @param e Exception
	 */
	public static void writePrintStackTrace(TestContext context, Exception e) {
		context.getLogger().error(getPrintStackTraceAsString(e));
	}

	/**
	 * Writes Print StackTrace on console and log file as per chosen option
	 * 
	 * @param context Test context
	 * @param e Throwable
	 */
	public static void writePrintStackTrace(TestContext context, Throwable e) {
		context.getLogger().error(getPrintStackTraceAsString(e));
	}

	/**
	 * Gets Print StackTrace as a string
	 * 
	 * @param e Throwable
	 * @return String transformed stack trace
	 */
	public static String getPrintStackTraceAsString(Throwable e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}

}
