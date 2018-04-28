package com.arpitos.utils;

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

}
