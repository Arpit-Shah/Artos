package com.arpitos.utils;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

import com.arpitos.framework.infra.TestContext;

public class Utils {

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

	/**
	 * Deletes all the content of the directory if present, If directory is not
	 * present and user has chosen appropriate flag then creates empty
	 * directory(s)
	 * 
	 * <PRE>
	 * {@code
	 * Example : cleanDir(new File("./conf", true);
	 * }
	 * </PRE>
	 * 
	 * @param dir
	 *            Target directory
	 * @param createIfNotPresent
	 *            Boolean flag which enables directory creation if not already
	 *            present
	 */
	public static void cleanDir(File dir, boolean createIfNotPresent) {
		if (dir.exists() && dir.isDirectory()) {
			cleanDir(dir);
		}
		dir.mkdirs();
	}

	private static void cleanDir(File dir) {
		for (File file : dir.listFiles()) {
			if (file.isDirectory()) {
				cleanDir(file);
			}
			file.delete();
		}
	}
}
