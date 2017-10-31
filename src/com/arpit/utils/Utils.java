package com.arpit.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JFileChooser;

import com.arpit.infra.OrganisedLog.LOG_LEVEL;
import com.arpit.infra.TestContext;

public class Utils {

	/**
	 * This function is used to compare string format $ sign is used to ignore
	 * chars during format comparison
	 * 
	 * <PRE>
	 * Example: Boolean bSuccess = CompareStringFormat("1.00.0001", "$.$$.$$$$");
	 * </PRE>
	 * 
	 * @param strToCompare
	 *            :- String which requires format check
	 * @param strRef
	 *            :- String which will be used as reference
	 * @return :- True if string format is right otherwise false
	 */
	public static boolean compareStringFormat(String strToCompare, String strRef) {

		if (strToCompare == null || strRef == null) {
			return false;
		} else {
			byte[] byteToCompare = strToCompare.getBytes();
			byte[] byteRef = strRef.getBytes();

			if (byteToCompare.length != byteRef.length) {
				return false;
			}

			for (int i = 0; i < byteRef.length; i++) {
				if (byteRef[i] == '$') {
					// do nothing
				} else {
					if (byteRef[i] != byteToCompare[i]) {
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * This Function check and return the correct format of the version.
	 * 
	 * @param strSource
	 * @param format
	 * @return
	 */
	public static String fetchVersion(String strSource, String format) {
		if ((null == strSource) || (strSource.length() < format.length())) {
			return null;
		}
		for (int i = 0; i <= strSource.length() - format.length(); i++) {
			String ver = strSource.substring(i, format.length() + i);
			if (compareStringFormat(ver, format)) {
				return ver;
			}
		}
		return null;
	}

	/**
	 * Writes Print StackTrace on console and log file as per choosen option
	 * 
	 * @param context
	 * @param e
	 */
	public static void writePrintStackTrace(TestContext context, Exception e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		context.getLogger().print(LOG_LEVEL.INFO, sw.toString());
	}

	/**
	 * Performs Cleanup after test is completed
	 * 
	 * @param context
	 */
	public static void testCleanUp(TestContext context) {
		// TODO Auto-generated method stub
	}
	
	
}
