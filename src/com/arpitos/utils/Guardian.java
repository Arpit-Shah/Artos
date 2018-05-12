package com.arpitos.utils;

import java.util.Arrays;

import com.arpitos.framework.Static_Store;

public class Guardian {

	static Convert _con = new Convert();

	static String strEqual_fail = " values are not equal";
	static String strNotEqual_fail = " values are equal";
	static String strGreater_fail = " actual value is not greater than expected value";
	static String strLess_fail = " actual value is not less than expected value";
	static String strGreaterOrEqual_fail = " actual value is less than expected value";
	static String strLessOrEqual_fail = " actual value is greater than expected value";
	static String strFormatEqual_fail = " format are not same";
	static String strFormatNotEqual_fail = " format are same";

	static void print(String reference, String actual) {
		Static_Store.context.getLogger().info("Finding:" + "\nReference : " + reference + "\n   Actual : " + actual);
	}

	static void print(byte reference, byte actual) {
		Static_Store.context.getLogger().info(
				"Finding:" + "\nReference : " + _con.bytesToStringHex(reference, true) + "\n   Actual : " + _con.bytesToStringHex(actual, true));
	}

	static void print(byte[] reference, byte[] actual) {
		Static_Store.context.getLogger().info(
				"Finding:" + "\nReference : " + _con.bytesToStringHex(reference, true) + "\n   Actual : " + _con.bytesToStringHex(actual, true));
	}

	static void print(int reference, int actual) {
		Static_Store.context.getLogger().info("Finding:" + "\nReference : " + reference + "\n   Actual : " + actual);
	}

	static void print(boolean reference, boolean actual) {
		Static_Store.context.getLogger().info("Finding:" + "\nReference : " + reference + "\n   Actual : " + actual);
	}

	static void print(long reference, long actual) {
		Static_Store.context.getLogger().info("Finding:" + "\nReference : " + reference + "\n   Actual : " + actual);
	}

	// *******************************************************************************************
	// String
	// *******************************************************************************************
	/**
	 * Validates String values are equal
	 * 
	 * <PRE>
	 * Example: isEqual("01.02.0001", "01.02.0001");
	 * </PRE>
	 * 
	 * @param reference
	 *            reference value
	 * @param actual
	 *            actual value
	 * @return true | false
	 */
	public static boolean isEquals(String reference, String actual) {
		if (actual.equals(reference)) {
			return true;
		}
		return false;
	}

	/**
	 * Validates String values are equal
	 * 
	 * <PRE>
	 * Example: guardE("Version of the firmware", "01.02.0001", "01.02.0001");
	 * </PRE>
	 * 
	 * @param desc
	 *            pass or failure msg
	 * @param reference
	 *            reference value
	 * @param actual
	 *            actual value
	 * @throws Exception
	 *             if value condition is not met
	 */
	public static void guardEquals(String desc, String reference, String actual) throws Exception {
		print(reference, actual);
		if (!isEquals(reference, actual)) {
			throw new Exception(desc + strEqual_fail);
		}
	}

	/**
	 * Validates String values are not equal
	 * 
	 * <PRE>
	 * Example: guardE("Version of the firmware", "99.99.9999", "01.02.0001");
	 * </PRE>
	 * 
	 * @param desc
	 *            pass or failure msg
	 * @param reference
	 *            reference value
	 * @param actual
	 *            actual value
	 * @throws Exception
	 *             if value condition is not met
	 */
	public static void guardNotEquals(String desc, String reference, String actual) throws Exception {
		print(reference, actual);
		if (isEquals(reference, actual)) {
			throw new Exception(desc + strNotEqual_fail);
		}
	}

	// *******************************************************************************************
	// Boolean
	// *******************************************************************************************
	/**
	 * Validates Boolean values are equal
	 * 
	 * <PRE>
	 * Example: isEqual(true, true);
	 * </PRE>
	 * 
	 * @param reference
	 *            reference value
	 * @param actual
	 *            actual value
	 * @return true | false
	 */
	public static boolean isEquals(boolean reference, boolean actual) {
		if (reference == actual) {
			return true;
		}
		return false;
	}

	/**
	 * Validates Boolean values are equal
	 * 
	 * <PRE>
	 * Example: guardE("Version of the firmware", true, true);
	 * </PRE>
	 * 
	 * @param desc
	 *            pass or failure msg
	 * @param reference
	 *            reference value
	 * @param actual
	 *            actual value
	 * @throws Exception
	 *             if value condition is not met
	 */
	public static void guardEquals(String desc, boolean reference, boolean actual) throws Exception {
		print(reference, actual);
		if (!isEquals(reference, actual)) {
			throw new Exception(desc + strEqual_fail);
		}
	}

	/**
	 * Validates Boolean values are not equal
	 * 
	 * <PRE>
	 * Example: guardE("Version of the firmware", true, true);
	 * </PRE>
	 * 
	 * @param desc
	 *            pass or failure msg
	 * @param reference
	 *            reference value
	 * @param actual
	 *            actual value
	 * @throws Exception
	 *             if value condition is not met
	 */
	public static void guardNotEquals(String desc, boolean reference, boolean actual) throws Exception {
		print(reference, actual);
		if (isEquals(reference, actual)) {
			throw new Exception(desc + strNotEqual_fail);
		}
	}

	// *******************************************************************************************
	// Format
	// *******************************************************************************************

	/**
	 * Validates String follows reference format
	 * 
	 * <PRE>
	 * Example: isFormatEqual("$$.$$.$$$$", "01.02.0001");
	 * </PRE>
	 * 
	 * @param format
	 *            String format ($ as wildcard)
	 * @param actual
	 *            actual value
	 * @return true | false
	 */
	public static boolean isFormatEquals(String format, String actual) {
		if (UtilsString.compareStringFormat(format, actual)) {
			return true;
		}
		return false;
	}

	/**
	 * Validates String follows reference format
	 * 
	 * <PRE>
	 * Example: guardFormatE("Version of the firmware", "$$.$$.$$$$", "01.02.0001");
	 * </PRE>
	 * 
	 * @param desc
	 *            pass or failure msg
	 * @param format
	 *            expected format "$$.$$.$$$$"
	 * @param actual
	 *            data which is being validated, right side of equation
	 * @throws Exception
	 *             if value condition is not met
	 */
	public static void guardFormatEquals(String desc, String format, String actual) throws Exception {
		print(format, actual);
		if (!isFormatEquals(format, actual)) {
			throw new Exception(desc + strFormatEqual_fail);
		}
	}

	/**
	 * Validates String is different than reference format
	 * 
	 * <PRE>
	 * Example: guardFormatNE("Version of the firmware", "$$.$$.$$$$", "01.02.0001");
	 * </PRE>
	 * 
	 * @param desc
	 *            pass or failure msg
	 * @param format
	 *            expected format "$$.$$.$$$$"
	 * @param actual
	 *            data which is being validated, right side of equation
	 * @throws Exception
	 */
	public static void guardFormatNotEquals(String desc, String format, String actual) throws Exception {
		print(format, actual);
		if (isFormatEquals(format, actual)) {
			throw new Exception(desc + strFormatNotEqual_fail);
		}
	}

	// *******************************************************************************************
	// Byte
	// *******************************************************************************************
	/**
	 * Validates Byte values are equal
	 * 
	 * <PRE>
	 * Example: isEqual((byte) 0x01, (byte) 0x01);
	 * </PRE>
	 * 
	 * @param reference
	 *            reference value
	 * @param actual
	 *            actual value
	 * @return true | false
	 */
	public static boolean isEquals(byte reference, byte actual) {
		if (reference == actual) {
			return true;
		}
		return false;
	}

	/**
	 * Validates Byte values are equal
	 * 
	 * <PRE>
	 * Example: guardE("Version of the firmware", (byte) 0x01, (byte) 0x01);
	 * </PRE>
	 * 
	 * @param desc
	 *            pass or failure msg
	 * @param reference
	 *            expected String value
	 * @param actual
	 *            actual value to be compared
	 * @throws Exception
	 *             if value condition is not met
	 */
	public static void guardEquals(String desc, byte reference, byte actual) throws Exception {
		print(reference, actual);
		if (!isEquals(reference, actual)) {
			throw new Exception(desc + strEqual_fail);
		}
	}

	/**
	 * Validates Byte values are not equal
	 * 
	 * <PRE>
	 * Example: guardE("Version of the firmware", (byte) 0x01, (byte) 0x01);
	 * </PRE>
	 * 
	 * @param desc
	 *            pass or failure msg
	 * @param reference
	 *            expected String value
	 * @param actual
	 *            actual value to be compared
	 * @throws Exception
	 */
	public static void guardNotEquals(String desc, byte reference, byte actual) throws Exception {
		print(reference, actual);
		if (isEquals(reference, actual)) {
			throw new Exception(desc + strNotEqual_fail);
		}
	}

	/**
	 * Validates Actual Byte Value is greater than Reference Value
	 * 
	 * <PRE>
	 * Example: guardG("Version of the firmware", (byte) 0x01, (byte) 0x03);
	 * </PRE>
	 * 
	 * @param desc
	 *            pass or failure msg
	 * @param reference
	 *            expected String value
	 * @param actual
	 *            actual value to be compared
	 * @throws Exception
	 *             if value condition is not met
	 */
	public static void guardGreaterThan(String desc, byte reference, byte actual) throws Exception {
		print(reference, actual);
		if (reference <= actual) {
			throw new Exception(desc + strGreater_fail);
		}
	}

	/**
	 * Validates Actual Byte Value is greater or Equal to Reference Value
	 * 
	 * <PRE>
	 * Example: guardGE("Version of the firmware", (byte) 0x01, (byte) 0x03);
	 * </PRE>
	 * 
	 * @param desc
	 *            pass or failure msg
	 * @param reference
	 *            expected String value
	 * @param actual
	 *            actual value to be compared
	 * @throws Exception
	 *             if value condition is not met
	 */
	public static void guardGreaterOrEqualsTo(String desc, byte reference, byte actual) throws Exception {
		print(reference, actual);
		if (reference < actual) {
			throw new Exception(desc + strGreaterOrEqual_fail);
		}
	}

	/**
	 * Validates Actual Byte Value is less than Reference Value
	 * 
	 * <PRE>
	 * Example: guardL("Version of the firmware", (byte) 0x01, (byte) 0x03);
	 * </PRE>
	 * 
	 * @param desc
	 *            pass or failure msg
	 * @param reference
	 *            expected String value
	 * @param actual
	 *            actual value to be compared
	 * @throws Exception
	 *             if value condition is not met
	 */
	public static void guardLessThan(String desc, byte reference, byte actual) throws Exception {
		print(reference, actual);
		if (reference >= actual) {
			throw new Exception(desc + strLess_fail);
		}
	}

	/**
	 * Validates Actual Byte Value is less or equal to Reference Value
	 * 
	 * <PRE>
	 * Example: guardLE("Version of the firmware", (byte) 0x01, (byte) 0x03);
	 * </PRE>
	 * 
	 * @param desc
	 *            pass or failure msg
	 * @param reference
	 *            expected String value
	 * @param actual
	 *            actual value to be compared
	 * @throws Exception
	 *             if value condition is not met
	 */
	public static void guardLessOrEqualsTo(String desc, byte reference, byte actual) throws Exception {
		print(reference, actual);
		if (reference > actual) {
			throw new Exception(desc + strLessOrEqual_fail);
		}
	}

	// *******************************************************************************************
	// Byte Array
	// *******************************************************************************************
	/**
	 * Validates Byte Arrays are equal
	 * 
	 * <PRE>
	 * Example: isEqual(new byte(){0x01, 0x02, 0x03}, new byte(){0x01, 0x02, 0x03});
	 * </PRE>
	 * 
	 * @param reference
	 *            reference value
	 * @param actual
	 *            actual value
	 * @return true | false
	 */
	public static boolean isEquals(byte[] reference, byte[] actual) {
		if (Arrays.equals(reference, actual)) {
			return true;
		}
		return false;
	}

	/**
	 * Validates Byte Arrays are equal
	 * 
	 * <PRE>
	 * Example: guardE("Version of the firmware", new byte(){0x01, 0x02, 0x03}, new byte(){0x01, 0x02, 0x03});
	 * </PRE>
	 * 
	 * @param desc
	 *            pass or failure msg
	 * @param reference
	 *            expected String value
	 * @param actual
	 *            actual value to be compared
	 * @throws Exception
	 *             if value condition is not met
	 */
	public static void guardEquals(String desc, byte[] reference, byte[] actual) throws Exception {
		print(reference, actual);
		if (!isEquals(reference, actual)) {
			throw new Exception(desc + strEqual_fail);
		}
	}

	/**
	 * Validates Byte Arrays are not equal
	 * 
	 * <PRE>
	 * Example: guardE("Version of the firmware", new byte(){0x01, 0x02, 0x03}, new byte(){0x01, 0x02, 0x03});
	 * </PRE>
	 * 
	 * @param desc
	 *            pass or failure msg
	 * @param reference
	 *            expected String value
	 * @param actual
	 *            actual value to be compared
	 * @throws Exception
	 *             if value condition is not met
	 */
	public static void guardNotEquals(String desc, byte[] reference, byte[] actual) throws Exception {
		print(reference, actual);
		if (isEquals(reference, actual)) {
			throw new Exception(desc + strNotEqual_fail);
		}
	}

	// *******************************************************************************************
	// Integer
	// *******************************************************************************************
	/**
	 * Validates int values are equal
	 * 
	 * <PRE>
	 * Example: isEqual(1, 2);
	 * </PRE>
	 * 
	 * @param reference
	 *            reference value
	 * @param actual
	 *            actual value
	 * @return true | false
	 */
	public static boolean isEquals(int reference, int actual) {
		if (reference == actual) {
			return true;
		}
		return false;
	}

	/**
	 * Validates int values are equal
	 * 
	 * <PRE>
	 * Example: guardE("Version of the firmware", 1, 2);
	 * </PRE>
	 * 
	 * @param desc
	 *            pass or failure msg
	 * @param reference
	 *            expected String value
	 * @param actual
	 *            actual value to be compared
	 * @throws Exception
	 *             if value condition is not met
	 */
	public static void guardEquals(String desc, int reference, int actual) throws Exception {
		print(reference, actual);
		if (!isEquals(reference, actual)) {
			throw new Exception(desc + strEqual_fail);
		}
	}

	/**
	 * Validates int values are not equal
	 * 
	 * <PRE>
	 * Example: guardE("Version of the firmware", 1, 2);
	 * </PRE>
	 * 
	 * @param desc
	 *            pass or failure msg
	 * @param reference
	 *            expected String value
	 * @param actual
	 *            actual value to be compared
	 * @throws Exception
	 *             if value condition is not met
	 */
	public static void guardNotEquals(String desc, int reference, int actual) throws Exception {
		print(reference, actual);
		if (isEquals(reference, actual)) {
			throw new Exception(desc + strNotEqual_fail);
		}
	}

	/**
	 * Validates Actual int Value is greater than Reference Value
	 * 
	 * <PRE>
	 * Example: guardG("Version of the firmware", 1, 2);
	 * </PRE>
	 * 
	 * @param desc
	 *            pass or failure msg
	 * @param reference
	 *            expected String value
	 * @param actual
	 *            actual value to be compared
	 * @throws Exception
	 *             if value condition is not met
	 */
	public static void guardGreaterThan(String desc, int reference, int actual) throws Exception {
		print(reference, actual);
		if (reference <= actual) {
			throw new Exception(desc + strGreater_fail);
		}
	}

	/**
	 * Validates Actual int Value is greater or Equal to Reference Value
	 * 
	 * <PRE>
	 * Example: guardGE("Version of the firmware", 1, 2);
	 * </PRE>
	 * 
	 * @param desc
	 *            pass or failure msg
	 * @param reference
	 *            expected String value
	 * @param actual
	 *            actual value to be compared
	 * @throws Exception
	 *             if value condition is not met
	 */
	public static void guardGreaterOrEqualsTo(String desc, int reference, int actual) throws Exception {
		print(reference, actual);
		if (reference < actual) {
			throw new Exception(desc + strGreaterOrEqual_fail);
		}
	}

	/**
	 * Validates Actual int Value is less than Reference Value
	 * 
	 * <PRE>
	 * Example: guardL("Version of the firmware", 1, 2);
	 * </PRE>
	 * 
	 * @param desc
	 *            pass or failure msg
	 * @param reference
	 *            expected String value
	 * @param actual
	 *            actual value to be compared
	 * @throws Exception
	 *             if value condition is not met
	 */
	public static void guardLessThan(String desc, int reference, int actual) throws Exception {
		print(reference, actual);
		if (reference >= actual) {
			throw new Exception(desc + strLess_fail);
		}
	}

	/**
	 * Validates Actual int Value is less or equal to Reference Value
	 * 
	 * <PRE>
	 * Example: guardLE("Version of the firmware", 1, 2);
	 * </PRE>
	 * 
	 * @param desc
	 *            pass or failure msg
	 * @param reference
	 *            expected String value
	 * @param actual
	 *            actual value to be compared
	 * @throws Exception
	 *             if value condition is not met
	 */
	public static void guardLessOrEqualsTo(String desc, int reference, int actual) throws Exception {
		print(reference, actual);
		if (reference > actual) {
			throw new Exception(desc + strLessOrEqual_fail);
		}
	}

	// *******************************************************************************************
	// Long
	// *******************************************************************************************
	/**
	 * Validates Long values are equal
	 * 
	 * <PRE>
	 * Example: isEqual(1, 2);
	 * </PRE>
	 * 
	 * @param reference
	 *            reference value
	 * @param actual
	 *            actual value
	 * @return true | false
	 */
	public static boolean isEquals(long reference, long actual) {
		if (reference == actual) {
			return true;
		}
		return false;
	}

	/**
	 * Validates Long values are equal
	 * 
	 * <PRE>
	 * Example: guardE("Version of the firmware", 1, 2);
	 * </PRE>
	 * 
	 * @param desc
	 *            pass or failure msg
	 * @param reference
	 *            expected String value
	 * @param actual
	 *            actual value to be compared
	 * @throws Exception
	 *             if value condition is not met
	 */
	public static void guardEquals(String desc, long reference, long actual) throws Exception {
		print(reference, actual);
		if (!isEquals(reference, actual)) {
			throw new Exception(desc + strEqual_fail);
		}
	}

	/**
	 * Validates Long values are not equal
	 * 
	 * <PRE>
	 * Example: guardE("Version of the firmware", 1, 2);
	 * </PRE>
	 * 
	 * @param desc
	 *            pass or failure msg
	 * @param reference
	 *            expected String value
	 * @param actual
	 *            actual value to be compared
	 * @throws Exception
	 *             if value condition is not met
	 */
	public static void guardNotEquals(String desc, long reference, long actual) throws Exception {
		print(reference, actual);
		if (isEquals(reference, actual)) {
			throw new Exception(desc + strNotEqual_fail);
		}
	}

	/**
	 * Validates Actual Long Value is greater than Reference Value
	 * 
	 * <PRE>
	 * Example: guardG("Version of the firmware", 1, 2);
	 * </PRE>
	 * 
	 * @param desc
	 *            pass or failure msg
	 * @param reference
	 *            expected String value
	 * @param actual
	 *            actual value to be compared
	 * @throws Exception
	 *             if value condition is not met
	 */
	public static void guardGreaterThan(String desc, long reference, long actual) throws Exception {
		print(reference, actual);
		if (reference <= actual) {
			throw new Exception(desc + strGreater_fail);
		}
	}

	/**
	 * Validates Actual Long Value is greater or Equal to Reference Value
	 * 
	 * <PRE>
	 * Example: guardGE("Version of the firmware", 1, 2);
	 * </PRE>
	 * 
	 * @param desc
	 *            pass or failure msg
	 * @param reference
	 *            expected String value
	 * @param actual
	 *            actual value to be compared
	 * @throws Exception
	 *             if value condition is not met
	 */
	public static void guardGreaterOrEquals(String desc, long reference, long actual) throws Exception {
		print(reference, actual);
		if (reference < actual) {
			throw new Exception(desc + strGreaterOrEqual_fail);
		}
	}

	/**
	 * Validates Actual Long Value is less than Reference Value
	 * 
	 * <PRE>
	 * Example: guardL("Version of the firmware", 1, 2);
	 * </PRE>
	 * 
	 * @param desc
	 *            pass or failure msg
	 * @param reference
	 *            expected String value
	 * @param actual
	 *            actual value to be compared
	 * @throws Exception
	 *             if value condition is not met
	 */
	public static void guardLessThan(String desc, long reference, long actual) throws Exception {
		print(reference, actual);
		if (reference >= actual) {
			throw new Exception(desc + strLess_fail);
		}
	}

	/**
	 * Validates Actual Long Value is less or equal to Reference Value
	 * 
	 * <PRE>
	 * Example: guardLE("Version of the firmware", 1, 2);
	 * </PRE>
	 * 
	 * @param desc
	 *            pass or failure msg
	 * @param reference
	 *            expected String value
	 * @param actual
	 *            actual value to be compared
	 * @throws Exception
	 *             if value condition is not met
	 */
	public static void guardLessOrEqualsTo(String desc, long reference, long actual) throws Exception {
		print(reference, actual);
		if (reference > actual) {
			throw new Exception(desc + strLessOrEqual_fail);
		}
	}
}
