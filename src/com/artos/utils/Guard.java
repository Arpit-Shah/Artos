/*******************************************************************************
 * Copyright (C) 2018 Arpit Shah
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

import java.util.Arrays;

import com.artos.exception.ValueNotAsExpected;
import com.artos.exception.WrongFlow;
import com.artos.framework.infra.TestContext;

/**
 * 
 * 
 *
 */
public class Guard {

	static Transform _con = new Transform();
	static String strEqual_fail = " values are not equal";
	static String strNotEqual_fail = " values are equal";
	static String strGreater_fail = " actual value is not greater than expected value";
	static String strLess_fail = " actual value is not less than expected value";
	static String strGreaterOrEqual_fail = " actual value is less than expected value";
	static String strLessOrEqual_fail = " actual value is greater than expected value";
	static String strFormatEqual_fail = " format are not same";
	static String strFormatNotEqual_fail = " format are same";

	static void print(TestContext context, String reference, String actual) {
		context.getLogger().debug("Finding:\nReference : {}\n   Actual : {}", reference, actual);
	}

	static void print(TestContext context, byte reference, byte actual) {
		context.getLogger().debug("Finding:\nReference : {}\n   Actual : {}", _con.bytesToHexString(reference, true),
				_con.bytesToHexString(actual, true));
	}

	static void print(TestContext context, byte reference, byte actual, byte delta) {
		context.getLogger().debug("Finding:\nReference : {}\n   Actual : {}\n   Delta : {}", _con.bytesToHexString(reference, true),
				_con.bytesToHexString(actual, true), _con.bytesToHexString(delta, true));
	}

	static void print(TestContext context, byte[] reference, byte[] actual) {
		context.getLogger().debug("Finding:\nReference : {}\n   Actual : {}", _con.bytesToHexString(reference, true),
				_con.bytesToHexString(actual, true));
	}

	static void print(TestContext context, int reference, int actual) {
		context.getLogger().debug("Finding:\nReference : {}\n   Actual : {}", reference, actual);
	}

	static void print(TestContext context, int reference, int actual, int delta) {
		context.getLogger().debug("Finding:\nReference : {}\n   Actual : {}\n   Delta : {}", reference, actual, delta);
	}

	static void print(TestContext context, boolean reference, boolean actual) {
		context.getLogger().debug("Finding:\nReference : {}\n   Actual : {}", reference, actual);
	}

	static void print(TestContext context, long reference, long actual) {
		context.getLogger().debug("Finding:\nReference : {}\n   Actual : {}", reference, actual);
	}

	static void print(TestContext context, long reference, long actual, long delta) {
		context.getLogger().debug("Finding:\nReference : {}\n   Actual : {}\n   Delta : {}", reference, actual, delta);
	}

	// *******************************************************************************************
	// Null
	// *******************************************************************************************
	/**
	 * Validates if object is null
	 * 
	 * @param obj
	 *            Object to be verified
	 * @return true|false
	 */
	public static boolean isNull(Object obj) {
		if (null == obj) {
			return true;
		}
		return false;
	}

	// *******************************************************************************************
	// String
	// *******************************************************************************************
	/**
	 * Validates that two strings are equal and returns boolean as a result
	 * 
	 * @param reference
	 *            Reference string value
	 * @param actual
	 *            Actual string value
	 * @return true|false
	 */
	public static boolean isEquals(String reference, String actual) {
		if (actual.equals(reference)) {
			return true;
		}
		return false;
	}

	/**
	 * Validates that two strings are equal. If they are not, an
	 * {@link ValueNotAsExpected} is thrown with the given message.
	 * 
	 * @param context
	 *            test context
	 * @param desc
	 *            Message to be printed
	 * @param reference
	 *            Reference string value
	 * @param actual
	 *            Actual string value
	 * @throws ValueNotAsExpected
	 *             Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardEquals(TestContext context, String desc, String reference, String actual) throws ValueNotAsExpected {
		print(context, reference, actual);
		if (!isEquals(reference, actual)) {
			throw new ValueNotAsExpected(desc + strEqual_fail);
		}
	}

	/**
	 * Validates that two strings are <b>not</b> equal. If they are, an
	 * {@link ValueNotAsExpected} is thrown with the given message.
	 * 
	 * @param context
	 *            test context
	 * @param desc
	 *            Message to be printed
	 * @param reference
	 *            Reference string value
	 * @param actual
	 *            Actual string value
	 * @throws ValueNotAsExpected
	 *             Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardNotEquals(TestContext context, String desc, String reference, String actual) throws ValueNotAsExpected {
		print(context, reference, actual);
		if (isEquals(reference, actual)) {
			throw new ValueNotAsExpected(desc + strNotEqual_fail);
		}
	}

	// *******************************************************************************************
	// Boolean
	// *******************************************************************************************
	/**
	 * Validates that two boolean values are equal. Returns boolean response
	 * 
	 * @param reference
	 *            Reference boolean value
	 * @param actual
	 *            Actual boolean value
	 * @return true | false
	 */
	public static boolean isEquals(boolean reference, boolean actual) {
		if (reference == actual) {
			return true;
		}
		return false;
	}

	/**
	 * Validates that two boolean values are equal. If they are not, an
	 * {@link ValueNotAsExpected} is thrown with the given message.
	 * 
	 * @param context
	 *            test context
	 * @param desc
	 *            Message to be printed
	 * @param reference
	 *            Reference boolean value
	 * @param actual
	 *            Actual boolean value
	 * @throws ValueNotAsExpected
	 *             Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardEquals(TestContext context, String desc, boolean reference, boolean actual) throws ValueNotAsExpected {
		print(context, reference, actual);
		if (!isEquals(reference, actual)) {
			throw new ValueNotAsExpected(desc + strEqual_fail);
		}
	}

	/**
	 * Validates that two boolean values are <b>not</b> equal. If they are, an
	 * {@link ValueNotAsExpected} is thrown with the given message.
	 * 
	 * @param context
	 *            test context
	 * @param desc
	 *            Message to be printed
	 * @param reference
	 *            Reference boolean value
	 * @param actual
	 *            Actual boolean value
	 * @throws ValueNotAsExpected
	 *             Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardNotEquals(TestContext context, String desc, boolean reference, boolean actual) throws ValueNotAsExpected {
		print(context, reference, actual);
		if (isEquals(reference, actual)) {
			throw new ValueNotAsExpected(desc + strNotEqual_fail);
		}
	}

	/**
	 * Validates that passed value is true. If they are not, an
	 * {@link ValueNotAsExpected} is thrown with the given message.
	 * 
	 * @param context
	 *            test context
	 * @param desc
	 *            Message to be printed
	 * @param actual
	 *            Actual boolean value
	 * @throws ValueNotAsExpected
	 *             Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardTrue(TestContext context, String desc, boolean actual) throws ValueNotAsExpected {
		guardEquals(context, desc, true, actual);
	}

	/**
	 * Validates that passed value is false. If they are not, an
	 * {@link ValueNotAsExpected} is thrown with the given message.
	 * 
	 * @param context
	 *            test context
	 * @param desc
	 *            Message to be printed
	 * @param actual
	 *            Actual boolean value
	 * @throws ValueNotAsExpected
	 *             Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardFalse(TestContext context, String desc, boolean actual) throws ValueNotAsExpected {
		guardEquals(context, desc, false, actual);
	}

	// *******************************************************************************************
	// Format
	// *******************************************************************************************

	/**
	 * Validates that passed value follows reference format. Returns result in
	 * boolean format. Wild card character is <b>$</b>, if value 01.05.0012 require
	 * format checking where . and 05 are static values then {@code format} value
	 * can be $$.05.$$$$
	 * 
	 * <PRE>
	 * {@code
	 * Example : isFormatEquals("$$.$$.$$$$", "01.02.0001");
	 * }
	 * </PRE>
	 * 
	 * @param format
	 *            Format string (using $ as a wild card character)
	 * @param actual
	 *            Actual string to be compared
	 * @return true | false
	 */
	public static boolean isFormatEquals(String format, String actual) {
		if (UtilsString.compareStringFormat(actual, format)) {
			return true;
		}
		return false;
	}

	/**
	 * Validates that passed value follows reference format. If they are not, an
	 * {@link ValueNotAsExpected} is thrown with the given message. Wild card
	 * character is <b>$</b>, if value 01.05.0012 require format checking where .
	 * and 05 are static values then {@code format} value can be $$.05.$$$$
	 * 
	 * <PRE>
	 * {@code
	 * Example : isFormatEquals("Version format checking", "$$.$$.$$$$", "01.02.0001");
	 * }
	 * </PRE>
	 * 
	 * @param context
	 *            test context
	 * @param desc
	 *            Message to be printed
	 * @param format
	 *            Format string (using $ as a wild card character)
	 * @param actual
	 *            Actual string to be compared
	 * @throws ValueNotAsExpected
	 *             Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardFormatEquals(TestContext context, String desc, String format, String actual) throws ValueNotAsExpected {
		print(context, format, actual);
		if (!isFormatEquals(format, actual)) {
			throw new ValueNotAsExpected(desc + strFormatEqual_fail);
		}
	}

	/**
	 * Validates that passed value <b>does not</b> follow reference format. If it
	 * does, an {@link ValueNotAsExpected} is thrown with the given message. Wild
	 * card character is <b>$</b>, if value 01.05.0012 require format checking where
	 * . and 05 are static values then {@code format} value can be $$.05.$$$$
	 * 
	 * <PRE>
	 * {@code
	 * Example : guardFormatNotEquals("Version format checking", "$$.$$", "01.02.0001");
	 * }
	 * </PRE>
	 * 
	 * @param context
	 *            test context
	 * @param desc
	 *            Message to be printed
	 * @param format
	 *            Format string (using $ as a wild card character)
	 * @param actual
	 *            Actual string to be compared
	 * @throws ValueNotAsExpected
	 *             Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardFormatNotEquals(TestContext context, String desc, String format, String actual) throws ValueNotAsExpected {
		print(context, format, actual);
		if (isFormatEquals(format, actual)) {
			throw new ValueNotAsExpected(desc + strFormatNotEqual_fail);
		}
	}

	// *******************************************************************************************
	// Byte
	// *******************************************************************************************
	/**
	 * Validates that two byte values are equal. Appropriate boolean value will be
	 * returned based on comparison.
	 * 
	 * @param reference
	 *            Reference byte value
	 * @param actual
	 *            Actual byte value
	 * @return true | false
	 */
	public static boolean isEquals(byte reference, byte actual) {
		if (reference == actual) {
			return true;
		}
		return false;
	}

	/**
	 * Validates that two byte values are equal with allowed {@code delta} on either
	 * side. Appropriate boolean value will be returned based on comparison.
	 * 
	 * @param reference
	 *            Reference byte value
	 * @param actual
	 *            Actual byte value
	 * @param delta
	 *            Variant allowed on either side of the reference value
	 * @return true | false
	 */
	public static boolean isEquals(byte reference, byte actual, byte delta) {
		// To ensure we do not exceed integer max boundary
		byte MaxBoundry;
		if (reference >= Byte.MAX_VALUE - delta) {
			MaxBoundry = Byte.MAX_VALUE;
		} else {
			MaxBoundry = (byte) (reference + delta);
		}
		// To ensure we do not exceed integer min boundary
		Byte MinBoundry;
		if (reference <= Byte.MIN_VALUE + delta) {
			MinBoundry = Byte.MIN_VALUE;
		} else {
			MinBoundry = (byte) (reference - delta);
		}

		if (actual >= MinBoundry && actual <= MaxBoundry) {
			return true;
		}
		return false;
	}

	/**
	 * Validates that two byte values are equal. If they are not, an
	 * {@link ValueNotAsExpected} is thrown with the given message.
	 * 
	 * @param context
	 *            test context
	 * @param desc
	 *            Message to be printed
	 * @param reference
	 *            Reference byte value
	 * @param actual
	 *            Actual byte value
	 * @throws ValueNotAsExpected
	 *             Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardEquals(TestContext context, String desc, byte reference, byte actual) throws ValueNotAsExpected {
		print(context, reference, actual);
		if (!isEquals(reference, actual)) {
			throw new ValueNotAsExpected(desc + strEqual_fail);
		}
	}

	/**
	 * Validates that two byte values are equal with allowed {@code delta}. If they
	 * are not, an {@link ValueNotAsExpected} is thrown with the given message.
	 * 
	 * @param context
	 *            test context
	 * @param desc
	 *            Message to be printed
	 * @param reference
	 *            Reference byte value
	 * @param delta
	 *            Variant allowed on either side of reference value
	 * @param actual
	 *            Actual byte value
	 * @throws ValueNotAsExpected
	 *             Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardEquals(TestContext context, String desc, byte reference, byte actual, byte delta) throws ValueNotAsExpected {
		print(context, reference, actual, delta);
		if (!isEquals(reference, actual, delta)) {
			throw new ValueNotAsExpected(desc + strEqual_fail);
		}
	}

	/**
	 * Validates that two byte values are <b>not</b> equal. If they are, an
	 * {@link ValueNotAsExpected} is thrown with the given message.
	 * 
	 * @param context
	 *            test context
	 * @param desc
	 *            Message to be printed
	 * @param reference
	 *            Reference byte value
	 * @param actual
	 *            Actual byte value
	 * @throws ValueNotAsExpected
	 *             Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardNotEquals(TestContext context, String desc, byte reference, byte actual) throws ValueNotAsExpected {
		print(context, reference, actual);
		if (isEquals(reference, actual)) {
			throw new ValueNotAsExpected(desc + strNotEqual_fail);
		}
	}

	/**
	 * Validates that {@code actual} byte value is greater than {@code reference}
	 * byte value. If they are not, an {@link ValueNotAsExpected} is thrown with the
	 * given message.
	 * 
	 * @param context
	 *            test context
	 * @param desc
	 *            Message to be printed
	 * @param reference
	 *            Reference byte value
	 * @param actual
	 *            Actual byte value
	 * @throws ValueNotAsExpected
	 *             Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardGreaterThan(TestContext context, String desc, byte reference, byte actual) throws ValueNotAsExpected {
		print(context, reference, actual);
		if (reference <= actual) {
			throw new ValueNotAsExpected(desc + strGreater_fail);
		}
	}

	/**
	 * Validates that {@code actual} byte value is greater than or equal to
	 * {@code reference} byte value. If they are not, an {@link ValueNotAsExpected}
	 * is thrown with the given message.
	 * 
	 * @param context
	 *            test context
	 * @param desc
	 *            Message to be printed
	 * @param reference
	 *            Reference byte value
	 * @param actual
	 *            Actual byte value
	 * @throws ValueNotAsExpected
	 *             Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardGreaterOrEqualsTo(TestContext context, String desc, byte reference, byte actual) throws ValueNotAsExpected {
		print(context, reference, actual);
		if (reference < actual) {
			throw new ValueNotAsExpected(desc + strGreaterOrEqual_fail);
		}
	}

	/**
	 * Validates that {@code actual} byte value is less than {@code reference} byte
	 * value. If they are not, an {@link ValueNotAsExpected} is thrown with the
	 * given message.
	 * 
	 * @param context
	 *            test context
	 * @param desc
	 *            Message to be printed
	 * @param reference
	 *            Reference byte value
	 * @param actual
	 *            Actual byte value
	 * @throws ValueNotAsExpected
	 *             Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardLessThan(TestContext context, String desc, byte reference, byte actual) throws ValueNotAsExpected {
		print(context, reference, actual);
		if (reference >= actual) {
			throw new ValueNotAsExpected(desc + strLess_fail);
		}
	}

	/**
	 * Validates that {@code actual} byte value is less than or equal to
	 * {@code reference} byte value. If they are not, an {@link ValueNotAsExpected}
	 * is thrown with the given message.
	 * 
	 * @param context
	 *            test context
	 * @param desc
	 *            Message to be printed
	 * @param reference
	 *            Reference byte value
	 * @param actual
	 *            Actual byte value
	 * @throws ValueNotAsExpected
	 *             Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardLessOrEqualsTo(TestContext context, String desc, byte reference, byte actual) throws ValueNotAsExpected {
		print(context, reference, actual);
		if (reference > actual) {
			throw new ValueNotAsExpected(desc + strLessOrEqual_fail);
		}
	}

	// *******************************************************************************************
	// Byte Array
	// *******************************************************************************************
	/**
	 * Validates that two byte arrays are equal. Appropriate boolean value will be
	 * returned based on comparison.
	 * 
	 * @param reference
	 *            Reference byte array
	 * @param actual
	 *            Actual byte array
	 * @return true | false
	 */
	public static boolean isEquals(byte[] reference, byte[] actual) {
		if (Arrays.equals(reference, actual)) {
			return true;
		}
		return false;
	}

	/**
	 * Validates that two byte arrays are equal. If they are not, an
	 * {@link ValueNotAsExpected} is thrown with the given message.
	 * 
	 * @param context
	 *            test context
	 * @param desc
	 *            Message to be printed
	 * @param reference
	 *            Reference byte array
	 * @param actual
	 *            Actual byte array
	 * @throws ValueNotAsExpected
	 *             Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardEquals(TestContext context, String desc, byte[] reference, byte[] actual) throws ValueNotAsExpected {
		print(context, reference, actual);
		if (!isEquals(reference, actual)) {
			throw new ValueNotAsExpected(desc + strEqual_fail);
		}
	}

	/**
	 * Validates that two byte arrays are <b>not</b> equal. If they are, an
	 * {@link ValueNotAsExpected} is thrown with the given message.
	 * 
	 * @param context
	 *            test context
	 * @param desc
	 *            Message to be printed
	 * @param reference
	 *            Reference byte array
	 * @param actual
	 *            Actual byte array
	 * @throws ValueNotAsExpected
	 *             Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardNotEquals(TestContext context, String desc, byte[] reference, byte[] actual) throws ValueNotAsExpected {
		print(context, reference, actual);
		if (isEquals(reference, actual)) {
			throw new ValueNotAsExpected(desc + strNotEqual_fail);
		}
	}

	// *******************************************************************************************
	// Integer
	// *******************************************************************************************
	/**
	 * Validates that two integer values are equal. Appropriate boolean value will
	 * be returned based on comparison.
	 * 
	 * @param reference
	 *            Reference integer value
	 * @param actual
	 *            Actual integer value
	 * @return true | false
	 */
	public static boolean isEquals(int reference, int actual) {
		if (reference == actual) {
			return true;
		}
		return false;
	}

	/**
	 * Validates that two integer values are equal with allowed {@code delta} on
	 * either side. Appropriate boolean value will be returned based on comparison.
	 * 
	 * @param reference
	 *            Reference integer value
	 * @param actual
	 *            Actual integer value
	 * @param delta
	 *            Variant allowed on either side of the reference value
	 * @return true | false
	 */
	public static boolean isEquals(int reference, int actual, int delta) {
		// To ensure we do not exceed integer max boundary
		int MaxBoundry;
		if (reference >= Integer.MAX_VALUE - delta) {
			MaxBoundry = Integer.MAX_VALUE;
		} else {
			MaxBoundry = reference + delta;
		}
		// To ensure we do not exceed integer min boundary
		int MinBoundry;
		if (reference <= Integer.MIN_VALUE + delta) {
			MinBoundry = Integer.MIN_VALUE;
		} else {
			MinBoundry = reference - delta;
		}

		if (actual >= MinBoundry && actual <= MaxBoundry) {
			return true;
		}
		return false;
	}

	/**
	 * Validates that two integer values are equal. If they are not, an
	 * {@link ValueNotAsExpected} is thrown with the given message.
	 * 
	 * @param context
	 *            test context
	 * @param desc
	 *            Message to be printed
	 * @param reference
	 *            Reference integer value
	 * @param actual
	 *            Actual integer value
	 * @throws ValueNotAsExpected
	 *             Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardEquals(TestContext context, String desc, int reference, int actual) throws ValueNotAsExpected {
		print(context, reference, actual);
		if (!isEquals(reference, actual)) {
			throw new ValueNotAsExpected(desc + strEqual_fail);
		}
	}

	/**
	 * Validates that two integer values are equal with allowed {@code delta} on
	 * either side. If they are not, an {@link ValueNotAsExpected} is thrown with
	 * the given message.
	 * 
	 * @param context
	 *            test context
	 * @param desc
	 *            Message to be printed
	 * @param reference
	 *            Reference integer value
	 * @param actual
	 *            Actual integer value
	 * @param delta
	 *            Variant allowed on either side of the reference value
	 * @throws ValueNotAsExpected
	 *             Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardEquals(TestContext context, String desc, int reference, int actual, int delta) throws ValueNotAsExpected {
		print(context, reference, actual, delta);
		if (!isEquals(reference, actual, delta)) {
			throw new ValueNotAsExpected(desc + strEqual_fail);
		}
	}

	/**
	 * Validates that two integer values are <b>not</b> equal. If they are, an
	 * {@link ValueNotAsExpected} is thrown with the given message.
	 * 
	 * @param context
	 *            test context
	 * @param desc
	 *            Message to be printed
	 * @param reference
	 *            Reference integer value
	 * @param actual
	 *            Actual integer value
	 * @throws ValueNotAsExpected
	 *             Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardNotEquals(TestContext context, String desc, int reference, int actual) throws ValueNotAsExpected {
		print(context, reference, actual);
		if (isEquals(reference, actual)) {
			throw new ValueNotAsExpected(desc + strNotEqual_fail);
		}
	}

	/**
	 * Validates that {@code reference} integer value is greater than {@code actual}
	 * integer value. If they are not, an {@link ValueNotAsExpected} is thrown with
	 * the given message.
	 * 
	 * @param context
	 *            test context
	 * @param desc
	 *            Message to be printed
	 * @param reference
	 *            Reference integer value
	 * @param actual
	 *            Actual integer value
	 * @throws ValueNotAsExpected
	 *             Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardGreaterThan(TestContext context, String desc, int reference, int actual) throws ValueNotAsExpected {
		print(context, reference, actual);
		if (reference <= actual) {
			throw new ValueNotAsExpected(desc + strGreater_fail);
		}
	}

	/**
	 * Validates that {@code reference} integer value is greater than or equal to
	 * {@code actual} integer value. If they are not, an {@link ValueNotAsExpected}
	 * is thrown with the given message.
	 * 
	 * @param context
	 *            test context
	 * @param desc
	 *            Message to be printed
	 * @param reference
	 *            Reference integer value
	 * @param actual
	 *            Actual integer value
	 * @throws ValueNotAsExpected
	 *             Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardGreaterOrEqualsTo(TestContext context, String desc, int reference, int actual) throws ValueNotAsExpected {
		print(context, reference, actual);
		if (reference < actual) {
			throw new ValueNotAsExpected(desc + strGreaterOrEqual_fail);
		}
	}

	/**
	 * Validates that {@code reference} integer value is less than {@code actual}
	 * integer value. If they are not, an {@link ValueNotAsExpected} is thrown with
	 * the given message.
	 * 
	 * @param context
	 *            test context
	 * @param desc
	 *            Message to be printed
	 * @param reference
	 *            Reference integer value
	 * @param actual
	 *            Actual integer value
	 * @throws ValueNotAsExpected
	 *             Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardLessThan(TestContext context, String desc, int reference, int actual) throws ValueNotAsExpected {
		print(context, reference, actual);
		if (reference >= actual) {
			throw new ValueNotAsExpected(desc + strLess_fail);
		}
	}

	/**
	 * Validates that {@code reference} integer value is less than or equal to
	 * {@code actual} integer value. If they are not, an {@link ValueNotAsExpected}
	 * is thrown with the given message.
	 * 
	 * @param context
	 *            test context
	 * @param desc
	 *            Message to be printed
	 * @param reference
	 *            Reference integer value
	 * @param actual
	 *            Actual integer value
	 * @throws ValueNotAsExpected
	 *             Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardLessOrEqualsTo(TestContext context, String desc, int reference, int actual) throws ValueNotAsExpected {
		print(context, reference, actual);
		if (reference > actual) {
			throw new ValueNotAsExpected(desc + strLessOrEqual_fail);
		}
	}

	// *******************************************************************************************
	// Long
	// *******************************************************************************************
	/**
	 * Validates that two long values are equal. Appropriate boolean value will be
	 * returned based on comparison.
	 * 
	 * @param reference
	 *            Reference long value
	 * @param actual
	 *            Actual long value
	 * @return true | false
	 */
	public static boolean isEquals(long reference, long actual) {
		if (reference == actual) {
			return true;
		}
		return false;
	}

	/**
	 * Validates that two long values are equal with allowed {@code delta} on either
	 * side. Appropriate boolean value will be returned based on comparison.
	 * 
	 * @param reference
	 *            Reference long value
	 * @param actual
	 *            Actual long value
	 * @param delta
	 *            Variant allowed on either side of the reference value
	 * @return true | false
	 */
	public static boolean isEquals(long reference, long actual, long delta) {
		// To ensure we do not exceed long max boundary
		long MaxBoundry;
		if (reference >= Long.MAX_VALUE - delta) {
			MaxBoundry = Long.MAX_VALUE;
		} else {
			MaxBoundry = reference + delta;
		}
		// To ensure we do not exceed long min boundary
		long MinBoundry;
		if (reference <= Long.MIN_VALUE + delta) {
			MinBoundry = Long.MIN_VALUE;
		} else {
			MinBoundry = reference - delta;
		}

		if (actual >= MinBoundry && actual <= MaxBoundry) {
			return true;
		}
		return false;
	}

	/**
	 * Validates that two long values are equal. If they are not, an
	 * {@link ValueNotAsExpected} is thrown with the given message.
	 * 
	 * @param context
	 *            test context
	 * @param desc
	 *            Message to be printed
	 * @param reference
	 *            Reference long value
	 * @param actual
	 *            Actual long value
	 * @throws ValueNotAsExpected
	 *             Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardEquals(TestContext context, String desc, long reference, long actual) throws ValueNotAsExpected {
		print(context, reference, actual);
		if (!isEquals(reference, actual)) {
			throw new ValueNotAsExpected(desc + strEqual_fail);
		}
	}

	/**
	 * Validates that two long values are equal with allowed {@code delta} on either
	 * side. If they are not, an {@link ValueNotAsExpected} is thrown with the given
	 * message.
	 * 
	 * @param context
	 *            test context
	 * @param desc
	 *            Message to be printed
	 * @param reference
	 *            Reference long value
	 * @param actual
	 *            Actual long value
	 * @param delta
	 *            Variant allowed on either side of the reference value
	 * @throws ValueNotAsExpected
	 *             Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardEquals(TestContext context, String desc, long reference, long actual, long delta) throws ValueNotAsExpected {
		print(context, reference, actual, delta);
		if (!isEquals(reference, actual, delta)) {
			throw new ValueNotAsExpected(desc + strEqual_fail);
		}
	}

	/**
	 * Validates that two long values are <b>not</b> equal. If they are, an
	 * {@link ValueNotAsExpected} is thrown with the given message.
	 * 
	 * @param context
	 *            test context
	 * @param desc
	 *            Message to be printed
	 * @param reference
	 *            Reference long value
	 * @param actual
	 *            Actual long value
	 * @throws ValueNotAsExpected
	 *             Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardNotEquals(TestContext context, String desc, long reference, long actual) throws ValueNotAsExpected {
		print(context, reference, actual);
		if (isEquals(reference, actual)) {
			throw new ValueNotAsExpected(desc + strNotEqual_fail);
		}
	}

	/**
	 * Validates that {@code reference} long value is greater than {@code actual}
	 * long value. If they are not, an {@link ValueNotAsExpected} is thrown with the
	 * given message.
	 * 
	 * @param context
	 *            test context
	 * @param desc
	 *            Message to be printed
	 * @param reference
	 *            Reference long value
	 * @param actual
	 *            Actual long value
	 * @throws ValueNotAsExpected
	 *             Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardGreaterThan(TestContext context, String desc, long reference, long actual) throws ValueNotAsExpected {
		print(context, reference, actual);
		if (reference <= actual) {
			throw new ValueNotAsExpected(desc + strGreater_fail);
		}
	}

	/**
	 * Validates that {@code reference} long value is greater than or equal to
	 * {@code actual} long value. If they are not, an {@link ValueNotAsExpected} is
	 * thrown with the given message.
	 * 
	 * @param context
	 *            test context
	 * @param desc
	 *            Message to be printed
	 * @param reference
	 *            Reference long value
	 * @param actual
	 *            Actual long value
	 * @throws ValueNotAsExpected
	 *             Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardGreaterOrEquals(TestContext context, String desc, long reference, long actual) throws ValueNotAsExpected {
		print(context, reference, actual);
		if (reference < actual) {
			throw new ValueNotAsExpected(desc + strGreaterOrEqual_fail);
		}
	}

	/**
	 * Validates that {@code reference} long value is less than {@code actual} long
	 * value. If they are not, an {@link ValueNotAsExpected} is thrown with the
	 * given message.
	 * 
	 * @param context
	 *            test context
	 * @param desc
	 *            Message to be printed
	 * @param reference
	 *            Reference long value
	 * @param actual
	 *            Actual long value
	 * @throws ValueNotAsExpected
	 *             Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardLessThan(TestContext context, String desc, long reference, long actual) throws ValueNotAsExpected {
		print(context, reference, actual);
		if (reference >= actual) {
			throw new ValueNotAsExpected(desc + strLess_fail);
		}
	}

	/**
	 * Validates that {@code reference} long value is less than or equal to
	 * {@code actual} long value. If they are not, an {@link ValueNotAsExpected} is
	 * thrown with the given message.
	 * 
	 * @param context
	 *            test context
	 * @param desc
	 *            Message to be printed
	 * @param reference
	 *            Reference long value
	 * @param actual
	 *            Actual long value
	 * @throws ValueNotAsExpected
	 *             Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardLessOrEqualsTo(TestContext context, String desc, long reference, long actual) throws ValueNotAsExpected {
		print(context, reference, actual);
		if (reference > actual) {
			throw new ValueNotAsExpected(desc + strLessOrEqual_fail);
		}
	}

	// *******************************************************************************************
	// Exception
	// *******************************************************************************************
	/**
	 * Validates that exception msg matches, if it does not then same exception is
	 * thrown back.
	 * 
	 * <PRE>
	 * {@code
	 * Example : guardEquals(e, "invalid value");
	 * }
	 * </PRE>
	 * 
	 * @param context
	 *            test context
	 * @param e
	 *            Exception which required to be verified
	 * @param actual
	 *            Actual string value
	 * @throws Exception
	 *             if exception message is not as expected then same exception is
	 *             thrown again
	 */
	public static void guardEquals(TestContext context, Exception e, String actual) throws Exception {
		print(context, e.getMessage(), actual);
		if (!e.getMessage().contains(actual)) {
			throw e;
		}
	}

	// *******************************************************************************************
	// Flow
	// *******************************************************************************************
	/**
	 * Guard against wrong flow. If code hits this code then {@link WrongFlow} is
	 * thrown
	 * 
	 * <PRE>
	 * {@code
	 * Example : guardWrongFlow("invalid flow");
	 * }
	 * </PRE>
	 * 
	 * @param msg
	 *            Message to be printed
	 * @throws WrongFlow
	 *             Indicates that code is following wrong flow
	 */
	public static void guardWrongFlow(String msg) throws WrongFlow {
		throw new WrongFlow(msg);
	}

}
