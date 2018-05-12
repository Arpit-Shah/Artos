package com.arpitos.utils;

import java.util.Arrays;

public class UtilsString {
	/**
	 * Escape string to convert it to HTML
	 * 
	 * @param s
	 * @return
	 */
	public String escape(String s) {
		StringBuilder builder = new StringBuilder();
		boolean previousWasASpace = false;
		for (char c : s.toCharArray()) {
			if (c == ' ') {
				if (previousWasASpace) {
					builder.append("&nbsp;");
					previousWasASpace = false;
					continue;
				}
				previousWasASpace = true;
			} else {
				previousWasASpace = false;
			}
			switch (c) {
			case '<':
				builder.append("&lt;");
				break;
			case '>':
				builder.append("&gt;");
				break;
			case '&':
				builder.append("&amp;");
				break;
			case '"':
				builder.append("&quot;");
				break;
			case '\n':
				builder.append("<br>");
				break;
			// We need Tab support here, because we print StackTraces as HTML
			case '\t':
				builder.append("&nbsp; &nbsp; &nbsp;");
				break;
			default:
				if (c < 128) {
					builder.append(c);
				} else {
					builder.append("&#").append((int) c).append(";");
				}
			}
		}
		return builder.toString();
	}

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
	 * Function that matches input string with given wild card pattern
	 * 
	 * @param str
	 *            input string
	 * @param pattern
	 *            wild card pattern
	 * @return
	 */
	public static boolean wildCardMatch(String str, String pattern) {

		int n = str.length();
		int m = pattern.length();

		// empty pattern can only match with
		// empty string
		if (m == 0)
			return (n == 0);

		// lookup table for storing results of
		// subproblems
		boolean[][] lookup = new boolean[n + 1][m + 1];

		// initialise lookup table to false
		for (int i = 0; i < n + 1; i++)
			Arrays.fill(lookup[i], false);

		// empty pattern can match with empty string
		lookup[0][0] = true;

		// Only '*' can match with empty string
		for (int j = 1; j <= m; j++)
			if (pattern.charAt(j - 1) == '*')
				lookup[0][j] = lookup[0][j - 1];

		// fill the table in bottom-up fashion
		for (int i = 1; i <= n; i++) {
			for (int j = 1; j <= m; j++) {
				// Two cases if we see a '*'
				// a) We ignore '*'' character and move
				// to next character in the pattern,
				// i.e., '*' indicates an empty sequence.
				// b) '*' character matches with ith
				// character in input
				if (pattern.charAt(j - 1) == '*')
					lookup[i][j] = lookup[i][j - 1] || lookup[i - 1][j];

				// Current characters are considered as
				// matching in two cases
				// (a) current character of pattern is '?'
				// (b) characters actually match
				else if (pattern.charAt(j - 1) == '?' || str.charAt(i - 1) == pattern.charAt(j - 1))
					lookup[i][j] = lookup[i - 1][j - 1];

				// If characters don't match
				else
					lookup[i][j] = false;
			}
		}

		return lookup[n][m];
	}

}
