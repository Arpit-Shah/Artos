package com.arpit.utils;

import java.util.Arrays;

import com.arpit.infra.OrganisedLog.LOG_LEVEL;
import com.arpit.infra.TestContext;

public class Guardian {

	static String strEqual_pass = " is as expected, expected to be same as Ref Data";
	static String strNotEqual_pass = " is as expected, expected to be different from Ref Data";
	static String strGreater_pass = " is as expected, expected value to be Greater than Ref Data";
	static String strLess_pass = " is as expected, expected value to be Less than Ref Data";
	static String strGreaterOrEqual_pass = " is as expected, expected value to be Greater or Equal to Ref Data";
	static String strLessOrEqual_pass = " is as expected, expected value to be Less or Equal to Ref Data";
	static String strFormatEqual_pass = " is as expected, exected format to match with ref format";
	static String strFormatNotEqual_pass = " is as expected, exected format not to match with ref format";

	static String strEqual_fail = " is not as expected, expected to be same as Ref Data";
	static String strNotEqual_fail = " is not as expected, expected to be different from Ref Data";
	static String strGreater_fail = " is not as expected, expected value to be Greater than Ref Data";
	static String strLess_fail = " is not as expected, expected value to be Less than Ref Data";
	static String strGreaterOrEqual_fail = " is not as expected, expected value to be Greater or Equal to Ref Data";
	static String strLessOrEqual_fail = " is not as expected, expected value to be Less or Equal to Ref Data";
	static String strFormatEqual_fail = " is not as expected, exected format to match with ref format";
	static String strFormatNotEqual_fail = " is not as expected, exected format not to match with ref format";

	public enum GuardCheckFor {
		EQUAL_TO, NOT_EQUAL_TO, GREATER_OR_EQUAL_TO, LESS_OR_EQUAL_TO, GREATER_THAN, LESS_THAN
	};

	/**
	 * Compares two string values as per requirement.
	 * 
	 * <PRE>
	 * Example: 
	 * xValue = "Test";
	 * Sample : Guard(context, GuardCheckFor.EQUAL_TO, "Height of the table", "Test", xValue);
	 * Result : true
	 * </PRE>
	 * 
	 * @param context
	 * @param check
	 *            = type of comparison
	 * @param desc
	 *            = pass or failure msg
	 * @param refData
	 *            = data which is used as reference during comparison, left side
	 *            of equation
	 * @param targetData
	 *            = data which is being validated, right side of equation
	 * @throws Exception
	 */
	public static void guard(TestContext context, GuardCheckFor check, String desc, String refData, String targetData) throws Exception {
		context.getLogger().println(LOG_LEVEL.INFO, "\nFinding");
		context.getLogger().println(LOG_LEVEL.INFO, "Ref : " + refData);
		context.getLogger().println(LOG_LEVEL.INFO, "Res : " + targetData);

		if (check == GuardCheckFor.EQUAL_TO) {
			if (!refData.equals(targetData)) {
				context.getLogger().println(LOG_LEVEL.INFO, desc + strEqual_fail);
				throw new Exception(desc + strEqual_fail);
			}
			context.getLogger().println(LOG_LEVEL.INFO, desc + strEqual_pass);
		} else if (check == GuardCheckFor.NOT_EQUAL_TO) {
			if (refData.equals(targetData)) {
				context.getLogger().println(LOG_LEVEL.INFO, desc + strNotEqual_fail);
				throw new Exception(desc + strNotEqual_fail);
			}
			context.getLogger().println(LOG_LEVEL.INFO, desc + strNotEqual_pass);
		} else {
			throw new Exception("Invalid comparision");
		}
	}

	/**
	 * Validates string format as per requirement. *
	 * 
	 * <PRE>
	 * Example: 
	 * xValue = "24.25.2266";
	 * Sample : Guard_Format_Compare(context, GuardCheckFor.EQUAL_TO, "Version of the firmware", "$$.$$.$$$$", xValue);
	 * Result : true
	 * </PRE>
	 * 
	 * @param context
	 * @param check
	 *            = type of comparison
	 * @param desc
	 *            = pass or failure msg
	 * @param format
	 *            = expected format "$$.$$.$$$$"
	 * @param targetData
	 *            = data which is being validated, right side of equation
	 * @throws Exception
	 */
	public static void guard_Format_Compare(TestContext context, GuardCheckFor check, String desc, String format, String targetData)
			throws Exception {
		context.getLogger().println(LOG_LEVEL.INFO, "\nFinding");
		context.getLogger().println(LOG_LEVEL.INFO, "Ref : " + format);
		context.getLogger().println(LOG_LEVEL.INFO, "Res : " + targetData);

		if (check == GuardCheckFor.EQUAL_TO) {
			if (!Utils.compareStringFormat(targetData, format)) {
				context.getLogger().println(LOG_LEVEL.INFO, desc + strFormatEqual_fail);
				throw new Exception(desc + strFormatEqual_fail);
			}
			context.getLogger().println(LOG_LEVEL.INFO, desc + strFormatEqual_pass);
		} else if (check == GuardCheckFor.NOT_EQUAL_TO) {
			if (Utils.compareStringFormat(targetData, format)) {
				context.getLogger().println(LOG_LEVEL.INFO, desc + strFormatNotEqual_fail);
				throw new Exception(desc + strFormatNotEqual_fail);
			}
			context.getLogger().println(LOG_LEVEL.INFO, desc + strFormatNotEqual_pass);
		} else {
			throw new Exception("Invalid comparision");
		}
	}

	/**
	 * Compares two byte arrays as per requirement.
	 * 
	 * <PRE>
	 * Example: 
	 * byte[] targetData = new byte(){0x01, 0x02, 0x03};
	 * byte[] refData = new byte(){0x01, 0x02, 0x03};
	 * Sample : Guard(context, GuardCheckFor.EQUAL_TO, "Version of the firmware", refData, targetData);
	 * Result : true
	 * </PRE>
	 * 
	 * @param context
	 * @param check
	 *            = type of comparison
	 * @param desc
	 *            = pass or failure msg
	 * @param refData
	 *            = data which is used as reference during comparison, left side
	 *            of equation
	 * @param targetData
	 *            = data which is being validated, right side of equation
	 * @throws Exception
	 */
	public static void guard(TestContext context, GuardCheckFor check, String desc, byte[] refData, byte[] targetData) throws Exception {
		Convert _con = new Convert();
		context.getLogger().println(LOG_LEVEL.INFO, "\nFinding");
		context.getLogger().println(LOG_LEVEL.INFO, "Ref : " + _con.bytesToStringHex(refData, true));
		context.getLogger().println(LOG_LEVEL.INFO, "Res : " + _con.bytesToStringHex(targetData, true));

		if (check == GuardCheckFor.EQUAL_TO) {
			if (!Arrays.equals(refData, targetData)) {
				context.getLogger().println(LOG_LEVEL.INFO, desc + strEqual_fail);
				throw new Exception(desc + strEqual_fail);
			}
			context.getLogger().println(LOG_LEVEL.INFO, desc + strEqual_pass);
		} else if (check == GuardCheckFor.NOT_EQUAL_TO) {
			if (Arrays.equals(refData, targetData)) {
				context.getLogger().println(LOG_LEVEL.INFO, desc + strNotEqual_fail);
				throw new Exception(desc + strNotEqual_fail);
			}
			context.getLogger().println(LOG_LEVEL.INFO, desc + strNotEqual_pass);
		} else {
			throw new Exception("Invalid comparision");
		}
	}

	/**
	 * Compares two byte values as per requirement.
	 * 
	 * <PRE>
	 * Example: 
	 * byte targetData = (byte)0x01;
	 * byte refData = (byte)0x01;
	 * Sample : Guard(context, GuardCheckFor.EQUAL_TO, "Version of the firmware", refData, targetData);
	 * Result : true
	 * </PRE>
	 * 
	 * @param context
	 * @param check
	 *            = type of comparison
	 * @param desc
	 *            = pass or failure msg
	 * @param refData
	 *            = data which is used as reference during comparison, left side
	 *            of equation
	 * @param targetData
	 *            = data which is being validated, right side of equation
	 * @throws Exception
	 */
	public static void guard(TestContext context, GuardCheckFor check, String desc, byte refData, byte targetData) throws Exception {
		context.getLogger().println(LOG_LEVEL.INFO, "\nFinding");
		context.getLogger().println(LOG_LEVEL.INFO, "Ref : " + refData);
		context.getLogger().println(LOG_LEVEL.INFO, "Res : " + targetData);

		if (check == GuardCheckFor.EQUAL_TO) {
			if (refData != targetData) {
				context.getLogger().println(LOG_LEVEL.INFO, desc + strEqual_fail);
				throw new Exception(desc + strEqual_fail);
			}
			context.getLogger().println(LOG_LEVEL.INFO, desc + strEqual_pass);
		} else if (check == GuardCheckFor.NOT_EQUAL_TO) {
			if (refData == targetData) {
				context.getLogger().println(LOG_LEVEL.INFO, desc + strNotEqual_fail);
				throw new Exception(desc + strNotEqual_fail);
			}
			context.getLogger().println(LOG_LEVEL.INFO, desc + strNotEqual_pass);
		} else if (check == GuardCheckFor.GREATER_THAN) {
			if (refData <= targetData) {
				context.getLogger().println(LOG_LEVEL.INFO, desc + strGreater_fail);
				throw new Exception(desc + strGreater_fail);
			}
			context.getLogger().println(LOG_LEVEL.INFO, desc + strGreater_pass);
		} else if (check == GuardCheckFor.LESS_THAN) {
			if (refData >= targetData) {
				context.getLogger().println(LOG_LEVEL.INFO, desc + strLess_fail);
				throw new Exception(desc + strLess_fail);
			}
			context.getLogger().println(LOG_LEVEL.INFO, desc + strLess_pass);
		} else if (check == GuardCheckFor.GREATER_OR_EQUAL_TO) {
			if (refData < targetData) {
				context.getLogger().println(LOG_LEVEL.INFO, desc + strGreaterOrEqual_fail);
				throw new Exception(desc + strGreaterOrEqual_fail);
			}
			context.getLogger().println(LOG_LEVEL.INFO, desc + strGreaterOrEqual_pass);
		} else if (check == GuardCheckFor.LESS_OR_EQUAL_TO) {
			if (refData > targetData) {
				context.getLogger().println(LOG_LEVEL.INFO, desc + strLessOrEqual_fail);
				throw new Exception(desc + strLessOrEqual_fail);
			}
			context.getLogger().println(LOG_LEVEL.INFO, desc + strLessOrEqual_pass);
		} else {
			throw new Exception("Invalid comparision");
		}
	}

	/**
	 * Compares two short values as per requirement.
	 * 
	 * <PRE>
	 * Example: 
	 * short targetData = 0x01;
	 * short refData = 0x01;
	 * Sample : Guard(context, GuardCheckFor.EQUAL_TO, "Version of the firmware", refData, targetData);
	 * Result : true
	 * </PRE>
	 * 
	 * @param context
	 * @param check
	 *            = type of comparison
	 * @param desc
	 *            = pass or failure msg
	 * @param refData
	 *            = data which is used as reference during comparison, left side
	 *            of equation
	 * @param targetData
	 *            = data which is being validated, right side of equation
	 * @throws Exception
	 */
	public static void guard(TestContext context, GuardCheckFor check, String desc, short refData, short targetData) throws Exception {
		context.getLogger().println(LOG_LEVEL.INFO, "\nFinding");
		context.getLogger().println(LOG_LEVEL.INFO, "Ref : " + refData);
		context.getLogger().println(LOG_LEVEL.INFO, "Res : " + targetData);

		if (check == GuardCheckFor.EQUAL_TO) {
			if (refData != targetData) {
				context.getLogger().println(LOG_LEVEL.INFO, desc + strEqual_fail);
				throw new Exception(desc + strEqual_fail);
			}
			context.getLogger().println(LOG_LEVEL.INFO, desc + strEqual_pass);
		} else if (check == GuardCheckFor.NOT_EQUAL_TO) {
			if (refData == targetData) {
				context.getLogger().println(LOG_LEVEL.INFO, desc + strNotEqual_fail);
				throw new Exception(desc + strNotEqual_fail);
			}
			context.getLogger().println(LOG_LEVEL.INFO, desc + strNotEqual_pass);
		} else if (check == GuardCheckFor.GREATER_THAN) {
			if (refData <= targetData) {
				context.getLogger().println(LOG_LEVEL.INFO, desc + strGreater_fail);
				throw new Exception(desc + strGreater_fail);
			}
			context.getLogger().println(LOG_LEVEL.INFO, desc + strGreater_pass);
		} else if (check == GuardCheckFor.LESS_THAN) {
			if (refData >= targetData) {
				context.getLogger().println(LOG_LEVEL.INFO, desc + strLess_fail);
				throw new Exception(desc + strLess_fail);
			}
			context.getLogger().println(LOG_LEVEL.INFO, desc + strLess_pass);
		} else if (check == GuardCheckFor.GREATER_OR_EQUAL_TO) {
			if (refData < targetData) {
				context.getLogger().println(LOG_LEVEL.INFO, desc + strGreaterOrEqual_fail);
				throw new Exception(desc + strGreaterOrEqual_fail);
			}
			context.getLogger().println(LOG_LEVEL.INFO, desc + strGreaterOrEqual_pass);
		} else if (check == GuardCheckFor.LESS_OR_EQUAL_TO) {
			if (refData > targetData) {
				context.getLogger().println(LOG_LEVEL.INFO, desc + strLessOrEqual_fail);
				throw new Exception(desc + strLessOrEqual_fail);
			}
			context.getLogger().println(LOG_LEVEL.INFO, desc + strLessOrEqual_pass);
		} else {
			throw new Exception("Invalid comparision");
		}
	}

	/**
	 * Compares two long values as per requirement.
	 * 
	 * <PRE>
	 * Example: 
	 * long targetData = 123456789l;
	 * long refData = 123456789l;
	 * Sample : Guard(context, GuardCheckFor.EQUAL_TO, "Version of the firmware", refData, targetData);
	 * Result : true
	 * </PRE>
	 * 
	 * @param context
	 * @param check
	 *            = type of comparison
	 * @param desc
	 *            = pass or failure msg
	 * @param refData
	 *            = data which is used as reference during comparison, left side
	 *            of equation
	 * @param targetData
	 *            = data which is being validated, right side of equation
	 * @throws Exception
	 */
	public static void guard(TestContext context, GuardCheckFor check, String desc, long refData, long targetData) throws Exception {
		context.getLogger().println(LOG_LEVEL.INFO, "\nFinding");
		context.getLogger().println(LOG_LEVEL.INFO, "Ref : " + refData);
		context.getLogger().println(LOG_LEVEL.INFO, "Res : " + targetData);

		if (check == GuardCheckFor.EQUAL_TO) {
			if (refData != targetData) {
				context.getLogger().println(LOG_LEVEL.INFO, desc + strEqual_fail);
				throw new Exception(desc + strEqual_fail);
			}
			context.getLogger().println(LOG_LEVEL.INFO, desc + strEqual_pass);
		} else if (check == GuardCheckFor.NOT_EQUAL_TO) {
			if (refData == targetData) {
				context.getLogger().println(LOG_LEVEL.INFO, desc + strNotEqual_fail);
				throw new Exception(desc + strNotEqual_fail);
			}
			context.getLogger().println(LOG_LEVEL.INFO, desc + strNotEqual_pass);
		} else if (check == GuardCheckFor.GREATER_THAN) {
			if (refData <= targetData) {
				context.getLogger().println(LOG_LEVEL.INFO, desc + strGreater_fail);
				throw new Exception(desc + strGreater_fail);
			}
			context.getLogger().println(LOG_LEVEL.INFO, desc + strGreater_pass);
		} else if (check == GuardCheckFor.LESS_THAN) {
			if (refData >= targetData) {
				context.getLogger().println(LOG_LEVEL.INFO, desc + strLess_fail);
				throw new Exception(desc + strLess_fail);
			}
			context.getLogger().println(LOG_LEVEL.INFO, desc + strLess_pass);
		} else if (check == GuardCheckFor.GREATER_OR_EQUAL_TO) {
			if (refData < targetData) {
				context.getLogger().println(LOG_LEVEL.INFO, desc + strGreaterOrEqual_fail);
				throw new Exception(desc + strGreaterOrEqual_fail);
			}
			context.getLogger().println(LOG_LEVEL.INFO, desc + strGreaterOrEqual_pass);
		} else if (check == GuardCheckFor.LESS_OR_EQUAL_TO) {
			if (refData > targetData) {
				context.getLogger().println(LOG_LEVEL.INFO, desc + strLessOrEqual_fail);
				throw new Exception(desc + strLessOrEqual_fail);
			}
			context.getLogger().println(LOG_LEVEL.INFO, desc + strLessOrEqual_pass);
		} else {
			throw new Exception("Invalid comparision");
		}
	}

	/**
	 * Compares two integer values as per requirement. *
	 * 
	 * <PRE>
	 * Example: 
	 * int targetData = 222;
	 * int refData = 222;
	 * Sample : Guard(context, GuardCheckFor.EQUAL_TO, "Version of the firmware", refData, targetData);
	 * Result : true
	 * </PRE>
	 * 
	 * @param context
	 * @param check
	 *            = type of comparison
	 * @param desc
	 *            = pass or failure msg
	 * @param refData
	 *            = data which is used as reference during comparison, left side
	 *            of equation
	 * @param targetData
	 *            = data which is being validated, right side of equation
	 * @throws Exception
	 */
	public static void guard(TestContext context, GuardCheckFor check, String desc, int refData, int targetData) throws Exception {
		context.getLogger().println(LOG_LEVEL.INFO, "\nFinding");
		context.getLogger().println(LOG_LEVEL.INFO, "Ref : " + refData);
		context.getLogger().println(LOG_LEVEL.INFO, "Res : " + targetData);

		if (check == GuardCheckFor.EQUAL_TO) {
			if (refData != targetData) {
				context.getLogger().println(LOG_LEVEL.INFO, desc + strEqual_fail);
				throw new Exception(desc + strEqual_fail);
			}
			context.getLogger().println(LOG_LEVEL.INFO, desc + strEqual_pass);
		} else if (check == GuardCheckFor.NOT_EQUAL_TO) {
			if (refData == targetData) {
				context.getLogger().println(LOG_LEVEL.INFO, desc + strNotEqual_fail);
				throw new Exception(desc + strNotEqual_fail);
			}
			context.getLogger().println(LOG_LEVEL.INFO, desc + strNotEqual_pass);
		} else if (check == GuardCheckFor.GREATER_THAN) {
			if (refData <= targetData) {
				context.getLogger().println(LOG_LEVEL.INFO, desc + strGreater_fail);
				throw new Exception(desc + strGreater_fail);
			}
			context.getLogger().println(LOG_LEVEL.INFO, desc + strGreater_pass);
		} else if (check == GuardCheckFor.LESS_THAN) {
			if (refData >= targetData) {
				context.getLogger().println(LOG_LEVEL.INFO, desc + strLess_fail);
				throw new Exception(desc + strLess_fail);
			}
			context.getLogger().println(LOG_LEVEL.INFO, desc + strLess_pass);
		} else if (check == GuardCheckFor.GREATER_OR_EQUAL_TO) {
			if (refData < targetData) {
				context.getLogger().println(LOG_LEVEL.INFO, desc + strGreaterOrEqual_fail);
				throw new Exception(desc + strGreaterOrEqual_fail);
			}
			context.getLogger().println(LOG_LEVEL.INFO, desc + strGreaterOrEqual_pass);
		} else if (check == GuardCheckFor.LESS_OR_EQUAL_TO) {
			if (refData > targetData) {
				context.getLogger().println(LOG_LEVEL.INFO, desc + strLessOrEqual_fail);
				throw new Exception(desc + strLessOrEqual_fail);
			}
			context.getLogger().println(LOG_LEVEL.INFO, desc + strLessOrEqual_pass);
		} else {
			throw new Exception("Invalid comparision");
		}
	}
}
