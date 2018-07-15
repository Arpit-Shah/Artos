package com.arpitos.utils;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

import com.arpitos.framework.infra.TestContext;

public class UtilsFramework {

	/**
	 * Writes Print StackTrace on console and log file as per chosen option
	 * 
	 * @param context
	 *            Test context
	 * @param e
	 *            Exception
	 */
	public static void writePrintStackTrace(TestContext context, Exception e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		context.getLogger().error(sw.toString());
	}

	/**
	 * Writes Print StackTrace on console and log file as per chosen option
	 * 
	 * @param context
	 *            Test context
	 * @param e
	 *            Throwable
	 */
	public static void writePrintStackTrace(TestContext context, Throwable e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		context.getLogger().error(sw.toString());
	}

}
