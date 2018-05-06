package com.arpitos.utils;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

import com.arpitos.framework.infra.TestContext;

public class Utils {

	/**
	 * Writes Print StackTrace on console and log file as per choosen option
	 * 
	 * @param context
	 * @param e
	 */
	public static void writePrintStackTrace(TestContext context, Exception e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		context.getLogger().error(sw.toString());
	}

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
