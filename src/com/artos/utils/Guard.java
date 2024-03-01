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

import java.util.Arrays;
import java.util.Objects;

import com.artos.exception.ValueNotAsExpectedException;
import com.artos.exception.WrongFlowException;
import com.artos.framework.infra.TestContext;

/**
 * Provides method to guard against any unexpected behaviours. Guard throws {@code ValueNotAsExpected} exception if provided argument does not meet
 * the expectation
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

	private static String constructMsg(final String reference, final String actual) {
		return "Finding:\nReference : " + reference + "\n   Actual : " + actual;
	}

	private static String constructMsg(final byte reference, final byte actual) {
		return "Finding:\nReference : " + _con.bytesToHexString(reference, true) + "\n   Actual : " + _con.bytesToHexString(actual, true);
	}

	private static String constructMsg(final byte reference, final byte actual, final byte delta) {
		return "Finding:\nReference : " + _con.bytesToHexString(reference, true) + "\n   Actual : " + _con.bytesToHexString(actual, true)
				+ "\n   Delta : " + _con.bytesToHexString(delta, true);
	}

	private static String constructMsg(final byte[] reference, final byte[] actual) {
		return "Finding:\nReference : " + _con.bytesToHexString(reference, true) + "\n   Actual : " + _con.bytesToHexString(actual, true);
	}

	private static String constructMsg(final int reference, final int actual) {
		return "Finding:\nReference : " + reference + "\n   Actual : " + actual;
	}

	private static String constructMsg(final int reference, final int actual, final int delta) {
		return "Finding:\nReference : " + reference + "\n   Actual : " + actual + "\n   Delta : " + delta;
	}
	
	private static String constructMsg(final double reference, final double actual) {
		return "Finding:\nReference : " + reference + "\n   Actual : " + actual;
	}

	private static String constructMsg(final double reference, final double actual, final double delta) {
		return "Finding:\nReference : " + reference + "\n   Actual : " + actual + "\n   Delta : " + delta;
	}

	private static String constructMsg(final boolean reference, final boolean actual) {
		return "Finding:\nReference : " + reference + "\n   Actual : " + actual;
	}

	private static String constructMsg(final long reference, final long actual) {
		return "Finding:\nReference : " + reference + "\n   Actual : " + actual;
	}

	private static String constructMsg(final long reference, final long actual, final long delta) {
		return "Finding:\nReference : " + reference + "\n   Actual : " + actual + "\n   Delta : " + delta;
	}

	// *******************************************************************************************
	// Null
	// *******************************************************************************************
	/**
	 * Validates if object is null
	 * 
	 * @param obj Object to be verified
	 * @return true|false
	 */
	public static boolean isNull(final Object obj) {
		return null == obj ? true : false;
	}

	// *******************************************************************************************
	// String
	// *******************************************************************************************
	/**
	 * Validates that two strings are equal or reference value matches given regular expression and returns boolean as a result
	 * 
	 * @param reference Reference string value or regular expression
	 * @param actual Actual string value
	 * @return true|false
	 */
	public static boolean isEquals(final String reference, final String actual) {
		if (null == reference || null == actual) {
			return (reference == actual) ? true : false;
		}

		return actual.equals(reference) || actual.matches(reference);
	}

	/**
	 * Validates that two case-sensetive strings are equal or reference value matches given regular expression and returns boolean as a result.
	 *
	 * @param reference Reference string value or regular expression
	 * @param actual Actual string value
	 * @return true|false
	 */
	public static boolean isEqualsIgnoreCase(final String reference, final String actual) {
		if (null == reference || null == actual) {
			return Objects.equals(reference, actual);
		}
		return actual.equalsIgnoreCase(reference) || actual.matches(reference);
	}


	/**
	 * Validates that two strings are equal or reference value matches given regular expression. If they are not, an
	 * {@link ValueNotAsExpectedException} is thrown with the given message.
	 * 
	 * @deprecated recommended alternative {@link #guardEquals(String, String)}
	 * @param context test context
	 * @param desc Message to be printed
	 * @param reference Reference string value or regular expression
	 * @param actual Actual string value
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	@Deprecated
	public static void guardEquals(TestContext context, final String desc, final String reference, final String actual)
			throws ValueNotAsExpectedException {
		context.getLogger().debug(constructMsg(reference, actual));
		if (!isEquals(reference, actual)) {
			throw new ValueNotAsExpectedException(desc + strEqual_fail);
		}
	}

	/**
	 * Validates that two strings are equal or reference value matches given regular expression. If they are not, an
	 * {@link ValueNotAsExpectedException} is thrown with the given message.
	 * 
	 * @param reference Reference string value or regular expression
	 * @param actual Actual string value
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardEquals(final String reference, final String actual) throws ValueNotAsExpectedException {
		if (!isEquals(reference, actual)) {
			throw new ValueNotAsExpectedException(strEqual_fail + "\n" + constructMsg(reference, actual));
		}
	}

	/**
	 * Validates that two strings are <b>not</b> equal or reference value <b>does not</b> match given regular expression. If they are, an
	 * {@link ValueNotAsExpectedException} is thrown with the given message.
	 * 
	 * @deprecated recommended alternative {@link #guardNotEquals(String, String)}
	 * @param context test context
	 * @param desc Message to be printed
	 * @param reference Reference string value or regular expression
	 * @param actual Actual string value
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	@Deprecated
	public static void guardNotEquals(TestContext context, final String desc, final String reference, final String actual)
			throws ValueNotAsExpectedException {
		context.getLogger().debug(constructMsg(reference, actual));
		if (isEquals(reference, actual)) {
			throw new ValueNotAsExpectedException(desc + strNotEqual_fail);
		}
	}

	/**
	 * Validates that two strings are <b>not</b> equal or reference value <b>does not</b> match given regular expression. If they are, an
	 * {@link ValueNotAsExpectedException} is thrown with the given message.
	 * 
	 * @param reference Reference string value or regular expression
	 * @param actual Actual string value
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardNotEquals(final String reference, final String actual) throws ValueNotAsExpectedException {
		if (isEquals(reference, actual)) {
			throw new ValueNotAsExpectedException(strNotEqual_fail + "\n" + constructMsg(reference, actual));
		}
	}

	/**
	 *  Compares this {@code reference} to another {@code actual}, ignoring case considerations. Two strings are considered equal ignoring case if they
	 *  are of the same length and corresponding characters in the two strings are equal ignoring case.
	 * {@link ValueNotAsExpectedException} is thrown with the given message.
	 *
	 * @param reference Reference string value or regular expression
	 * @param actual Actual string value
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardEqualsIgnoreCase(final String reference, final String actual) throws ValueNotAsExpectedException {
		if (!isEqualsIgnoreCase(reference,actual)) {
			throw new ValueNotAsExpectedException(strEqual_fail + "\n" + constructMsg(reference, actual));
		}
	}


	// *******************************************************************************************
	// Boolean
	// *******************************************************************************************
	/**
	 * Validates that two boolean values are equal. Returns boolean response
	 * 
	 * @param reference Reference boolean value
	 * @param actual Actual boolean value
	 * @return true | false
	 */
	public static boolean isEquals(final boolean reference, final boolean actual) {
		return reference == actual ? true : false;
	}

	/**
	 * Validates that two boolean values are equal. If they are not, an {@link ValueNotAsExpectedException} is thrown with the given message.
	 * 
	 * @deprecated recommended alternative {@link #guardEquals(boolean, boolean)}
	 * @param context test context
	 * @param desc Message to be printed
	 * @param reference Reference boolean value
	 * @param actual Actual boolean value
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	@Deprecated
	public static void guardEquals(TestContext context, final String desc, final boolean reference, final boolean actual)
			throws ValueNotAsExpectedException {
		context.getLogger().debug(constructMsg(reference, actual));
		if (!isEquals(reference, actual)) {
			throw new ValueNotAsExpectedException(desc + strEqual_fail);
		}
	}

	/**
	 * Validates that two boolean values are equal. If they are not, an {@link ValueNotAsExpectedException} is thrown with the given message.
	 * 
	 * @param reference Reference boolean value
	 * @param actual Actual boolean value
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardEquals(final boolean reference, final boolean actual) throws ValueNotAsExpectedException {
		if (!isEquals(reference, actual)) {
			throw new ValueNotAsExpectedException(strEqual_fail + "\n" + constructMsg(reference, actual));
		}
	}

	/**
	 * Validates that two boolean values are <b>not</b> equal. If they are, an {@link ValueNotAsExpectedException} is thrown with the given message.
	 * 
	 * @deprecated recommended alternative {@link #guardNotEquals(boolean, boolean)}
	 * @param context test context
	 * @param desc Message to be printed
	 * @param reference Reference boolean value
	 * @param actual Actual boolean value
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	@Deprecated
	public static void guardNotEquals(TestContext context, final String desc, final boolean reference, final boolean actual)
			throws ValueNotAsExpectedException {
		context.getLogger().debug(constructMsg(reference, actual));
		if (isEquals(reference, actual)) {
			throw new ValueNotAsExpectedException(desc + strNotEqual_fail);
		}
	}

	/**
	 * Validates that two boolean values are <b>not</b> equal. If they are, an {@link ValueNotAsExpectedException} is thrown with the given message.
	 * 
	 * @param reference Reference boolean value
	 * @param actual Actual boolean value
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardNotEquals(final boolean reference, final boolean actual) throws ValueNotAsExpectedException {
		if (isEquals(reference, actual)) {
			throw new ValueNotAsExpectedException(strNotEqual_fail + "\n" + constructMsg(reference, actual));
		}
	}

	/**
	 * Validates that passed value is true. If they are not, an {@link ValueNotAsExpectedException} is thrown with the given message.
	 * 
	 * @deprecated recommended alternative {@link #guardTrue(boolean)}
	 * @param context test context
	 * @param desc Message to be printed
	 * @param actual Actual boolean value
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	@Deprecated
	public static void guardTrue(TestContext context, final String desc, final boolean actual) throws ValueNotAsExpectedException {
		guardEquals(context, desc, true, actual);
	}

	/**
	 * Validates that passed value is true. If they are not, an {@link ValueNotAsExpectedException} is thrown with the given message.
	 * 
	 * @param actual Actual boolean value
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardTrue(final boolean actual) throws ValueNotAsExpectedException {
		guardEquals(true, actual);
	}

	/**
	 * Validates that passed value is false. If they are not, an {@link ValueNotAsExpectedException} is thrown with the given message.
	 * 
	 * @deprecated recommended alternative {@link #guardFalse(boolean)}
	 * @param context test context
	 * @param desc Message to be printed
	 * @param actual Actual boolean value
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	@Deprecated
	public static void guardFalse(TestContext context, final String desc, final boolean actual) throws ValueNotAsExpectedException {
		guardEquals(context, desc, false, actual);
	}

	/**
	 * Validates that passed value is false. If they are not, an {@link ValueNotAsExpectedException} is thrown with the given message.
	 * 
	 * @param actual Actual boolean value
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardFalse(final boolean actual) throws ValueNotAsExpectedException {
		guardEquals(false, actual);
	}

	// *******************************************************************************************
	// Format
	// *******************************************************************************************

	/**
	 * Validates that passed value follows reference format. Returns result in boolean format. Wild card character is <b>$</b>, if value 01.05.0012
	 * require format checking where . and 05 are static values then {@code format} value can be $$.05.$$$$
	 * 
	 * <PRE>
	 * {@code
	 * Example : isFormatEquals("$$.$$.$$$$", "01.02.0001");
	 * }
	 * </PRE>
	 * 
	 * @param format Format string (using $ as a wild card character)
	 * @param actual Actual string to be compared
	 * @return true | false
	 */
	public static boolean isFormatEquals(final String format, final String actual) {
		return UtilsString.compareStringFormat(actual, format) ? true : false;
	}

	/**
	 * Validates that passed value follows reference format. If they are not, an {@link ValueNotAsExpectedException} is thrown with the given message.
	 * Wild card character is <b>$</b>, if value 01.05.0012 require format checking where . and 05 are static values then {@code format} value can be
	 * $$.05.$$$$
	 * 
	 * <PRE>
	 * {@code
	 * Example : isFormatEquals(context, "Version format checking", "$$.$$.$$$$", "01.02.0001");
	 * }
	 * </PRE>
	 * 
	 * @deprecated recommended alternative {@link #guardFormatEquals(String, String)}
	 * @param context test context
	 * @param desc Message to be printed
	 * @param format Format string (using $ as a wild card character)
	 * @param actual Actual string to be compared
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	@Deprecated
	public static void guardFormatEquals(TestContext context, final String desc, final String format, final String actual)
			throws ValueNotAsExpectedException {
		context.getLogger().debug(constructMsg(format, actual));
		if (!isFormatEquals(format, actual)) {
			throw new ValueNotAsExpectedException(desc + strFormatEqual_fail);
		}
	}

	/**
	 * Validates that passed value follows reference format. If they are not, an {@link ValueNotAsExpectedException} is thrown with the given message.
	 * Wild card character is <b>$</b>, if value 01.05.0012 require format checking where . and 05 are static values then {@code format} value can be
	 * $$.05.$$$$
	 * 
	 * <PRE>
	 * {@code
	 * Example : isFormatEquals("$$.$$.$$$$", "01.02.0001");
	 * }
	 * </PRE>
	 * 
	 * @param format Format string (using $ as a wild card character)
	 * @param actual Actual string to be compared
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardFormatEquals(final String format, final String actual) throws ValueNotAsExpectedException {
		if (!isFormatEquals(format, actual)) {
			throw new ValueNotAsExpectedException(strFormatEqual_fail + "\n" + constructMsg(format, actual));
		}
	}

	/**
	 * Validates that passed value <b>does not</b> follow reference format. If it does, an {@link ValueNotAsExpectedException} is thrown with the
	 * given message. Wild card character is <b>$</b>, if value 01.05.0012 require format checking where . and 05 are static values then
	 * {@code format} value can be $$.05.$$$$
	 * 
	 * <PRE>
	 * {@code
	 * Example : guardFormatNotEquals(context, "Version format checking", "$$.$$", "01.02.0001");
	 * }
	 * </PRE>
	 * 
	 * @deprecated recommended alternative {@link #guardFormatNotEquals(String, String)}
	 * @param context test context
	 * @param desc Message to be printed
	 * @param format Format string (using $ as a wild card character)
	 * @param actual Actual string to be compared
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	@Deprecated
	public static void guardFormatNotEquals(TestContext context, final String desc, final String format, final String actual)
			throws ValueNotAsExpectedException {
		context.getLogger().debug(constructMsg(format, actual));
		if (isFormatEquals(format, actual)) {
			throw new ValueNotAsExpectedException(desc + strFormatNotEqual_fail);
		}
	}

	/**
	 * Validates that passed value <b>does not</b> follow reference format. If it does, an {@link ValueNotAsExpectedException} is thrown with the
	 * given message. Wild card character is <b>$</b>, if value 01.05.0012 require format checking where . and 05 are static values then
	 * {@code format} value can be $$.05.$$$$
	 * 
	 * <PRE>
	 * {@code
	 * Example : guardFormatNotEquals("$$.$$", "01.02.0001");
	 * }
	 * </PRE>
	 * 
	 * @param format Format string (using $ as a wild card character)
	 * @param actual Actual string to be compared
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardFormatNotEquals(final String format, final String actual) throws ValueNotAsExpectedException {
		if (isFormatEquals(format, actual)) {
			throw new ValueNotAsExpectedException(strFormatNotEqual_fail + "\n" + constructMsg(format, actual));
		}
	}

	// *******************************************************************************************
	// Byte
	// *******************************************************************************************
	/**
	 * Validates that two byte values are equal. Appropriate boolean value will be returned based on comparison.
	 * 
	 * @param reference Reference byte value
	 * @param actual Actual byte value
	 * @return true | false
	 */
	public static boolean isEquals(final byte reference, final byte actual) {
		return reference == actual ? true : false;
	}

	/**
	 * Validates that two byte values are equal with allowed {@code delta} on either side. Appropriate boolean value will be returned based on
	 * comparison.
	 * 
	 * @param reference Reference byte value
	 * @param actual Actual byte value
	 * @param delta Variant allowed on either side of the reference value
	 * @return true | false
	 */
	public static boolean isEquals(final byte reference, final byte actual, final byte delta) {
		return isEquals(Byte.toUnsignedInt(reference), Byte.toUnsignedInt(actual), Byte.toUnsignedInt(delta));
	}

	/**
	 * Validates that two byte values are equal. If they are not, an {@link ValueNotAsExpectedException} is thrown with the given message.
	 * 
	 * @deprecated recommended alternative {@link #guardEquals(byte, byte)}
	 * @param context test context
	 * @param desc Message to be printed
	 * @param reference Reference byte value
	 * @param actual Actual byte value
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	@Deprecated
	public static void guardEquals(TestContext context, final String desc, final byte reference, final byte actual)
			throws ValueNotAsExpectedException {
		context.getLogger().debug(constructMsg(reference, actual));
		if (!isEquals(reference, actual)) {
			throw new ValueNotAsExpectedException(desc + strEqual_fail);
		}
	}

	/**
	 * Validates that two byte values are equal. If they are not, an {@link ValueNotAsExpectedException} is thrown with the given message.
	 * 
	 * @param reference Reference byte value
	 * @param actual Actual byte value
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardEquals(final byte reference, final byte actual) throws ValueNotAsExpectedException {
		if (!isEquals(reference, actual)) {
			throw new ValueNotAsExpectedException(strEqual_fail + "\n" + constructMsg(reference, actual));
		}
	}

	/**
	 * Validates that two byte values are equal with allowed {@code delta}. If they are not, an {@link ValueNotAsExpectedException} is thrown with the
	 * given message.
	 * 
	 * @deprecated recommended alternative {@link #guardEquals(byte, byte, byte)}
	 * @param context test context
	 * @param desc Message to be printed
	 * @param reference Reference byte value
	 * @param delta Variant allowed on either side of reference value
	 * @param actual Actual byte value
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	@Deprecated
	public static void guardEquals(TestContext context, final String desc, final byte reference, final byte actual, final byte delta)
			throws ValueNotAsExpectedException {
		context.getLogger().debug(constructMsg(reference, actual, delta));
		if (!isEquals(reference, actual, delta)) {
			throw new ValueNotAsExpectedException(desc + strEqual_fail);
		}
	}

	/**
	 * Validates that two byte values are equal with allowed {@code delta}. If they are not, an {@link ValueNotAsExpectedException} is thrown with the
	 * given message.
	 * 
	 * @param reference Reference byte value
	 * @param actual Actual byte value
	 * @param delta Variant allowed on either side of reference value
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardEquals(final byte reference, final byte actual, final byte delta) throws ValueNotAsExpectedException {
		if (!isEquals(reference, actual, delta)) {
			throw new ValueNotAsExpectedException(strEqual_fail + constructMsg(reference, actual, delta));
		}
	}

	/**
	 * Validates that two byte values are <b>not</b> equal. If they are, an {@link ValueNotAsExpectedException} is thrown with the given message.
	 * 
	 * @deprecated recommended alternative {@link #guardNotEquals(byte, byte)}
	 * @param context test context
	 * @param desc Message to be printed
	 * @param reference Reference byte value
	 * @param actual Actual byte value
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	@Deprecated
	public static void guardNotEquals(TestContext context, final String desc, final byte reference, final byte actual)
			throws ValueNotAsExpectedException {
		context.getLogger().debug(constructMsg(reference, actual));
		if (isEquals(reference, actual)) {
			throw new ValueNotAsExpectedException(desc + strNotEqual_fail);
		}
	}

	/**
	 * Validates that two byte values are <b>not</b> equal. If they are, an {@link ValueNotAsExpectedException} is thrown with the given message.
	 * 
	 * @param reference Reference byte value
	 * @param actual Actual byte value
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardNotEquals(final byte reference, final byte actual) throws ValueNotAsExpectedException {
		if (isEquals(reference, actual)) {
			throw new ValueNotAsExpectedException(strNotEqual_fail + "\n" + constructMsg(reference, actual));
		}
	}

	/**
	 * Validates that {@code actual} byte value is greater than {@code reference} byte value. If they are not, an {@link ValueNotAsExpectedException}
	 * is thrown with the given message.
	 * 
	 * @deprecated recommended alternative {@link #guardGreaterThan(byte, byte)}
	 * @param context test context
	 * @param desc Message to be printed
	 * @param reference Reference byte value
	 * @param actual Actual byte value
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	@Deprecated
	public static void guardGreaterThan(TestContext context, final String desc, final byte reference, final byte actual)
			throws ValueNotAsExpectedException {
		context.getLogger().debug(constructMsg(reference, actual));
		if (!(Byte.toUnsignedInt(actual) > Byte.toUnsignedInt(reference))) {
			throw new ValueNotAsExpectedException(desc + strGreater_fail);
		}
	}

	/**
	 * Validates that {@code actual} byte value is greater than {@code reference} byte value. If they are not, an {@link ValueNotAsExpectedException}
	 * is thrown with the given message.
	 * 
	 * @param reference Reference byte value
	 * @param actual Actual byte value
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardGreaterThan(final byte reference, final byte actual) throws ValueNotAsExpectedException {
		if (!(Byte.toUnsignedInt(actual) > Byte.toUnsignedInt(reference))) {
			throw new ValueNotAsExpectedException(strGreater_fail + "\n" + constructMsg(reference, actual));
		}
	}

	/**
	 * Validates that {@code actual} byte value is greater than or equal to {@code reference} byte value. If they are not, an
	 * {@link ValueNotAsExpectedException} is thrown with the given message.
	 * 
	 * @deprecated recommended alternative {@link #guardGreaterOrEqualsTo(byte, byte)}
	 * @param context test context
	 * @param desc Message to be printed
	 * @param reference Reference byte value
	 * @param actual Actual byte value
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	@Deprecated
	public static void guardGreaterOrEqualsTo(TestContext context, final String desc, final byte reference, final byte actual)
			throws ValueNotAsExpectedException {
		context.getLogger().debug(constructMsg(reference, actual));
		if (!(Byte.toUnsignedInt(actual) >= Byte.toUnsignedInt(reference))) {
			throw new ValueNotAsExpectedException(desc + strGreaterOrEqual_fail);
		}
	}

	/**
	 * Validates that {@code actual} byte value is greater than or equal to {@code reference} byte value. If they are not, an
	 * {@link ValueNotAsExpectedException} is thrown with the given message.
	 * 
	 * @param reference Reference byte value
	 * @param actual Actual byte value
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardGreaterOrEqualsTo(final byte reference, final byte actual) throws ValueNotAsExpectedException {
		if (!(Byte.toUnsignedInt(actual) >= Byte.toUnsignedInt(reference))) {
			throw new ValueNotAsExpectedException(strGreaterOrEqual_fail + "\n" + constructMsg(reference, actual));
		}
	}

	/**
	 * Validates that {@code actual} byte value is less than {@code reference} byte value. If they are not, an {@link ValueNotAsExpectedException} is
	 * thrown with the given message.
	 * 
	 * @deprecated recommended alternative {@link #guardLessThan(byte, byte)}
	 * @param context test context
	 * @param desc Message to be printed
	 * @param reference Reference byte value
	 * @param actual Actual byte value
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	@Deprecated
	public static void guardLessThan(TestContext context, final String desc, final byte reference, final byte actual)
			throws ValueNotAsExpectedException {
		context.getLogger().debug(constructMsg(reference, actual));
		if (!(Byte.toUnsignedInt(actual) < Byte.toUnsignedInt(reference))) {
			throw new ValueNotAsExpectedException(desc + strLess_fail);
		}
	}

	/**
	 * Validates that {@code actual} byte value is less than {@code reference} byte value. If they are not, an {@link ValueNotAsExpectedException} is
	 * thrown with the given message.
	 * 
	 * @param reference Reference byte value
	 * @param actual Actual byte value
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardLessThan(final byte reference, final byte actual) throws ValueNotAsExpectedException {
		if (!(Byte.toUnsignedInt(actual) < Byte.toUnsignedInt(reference))) {
			throw new ValueNotAsExpectedException(strLess_fail + "\n" + constructMsg(reference, actual));
		}
	}

	/**
	 * Validates that {@code actual} byte value is less than or equal to {@code reference} byte value. If they are not, an
	 * {@link ValueNotAsExpectedException} is thrown with the given message.
	 * 
	 * @deprecated recommended alternative {@link #guardLessOrEqualsTo(byte, byte)}
	 * @param context test context
	 * @param desc Message to be printed
	 * @param reference Reference byte value
	 * @param actual Actual byte value
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	@Deprecated
	public static void guardLessOrEqualsTo(TestContext context, final String desc, final byte reference, final byte actual)
			throws ValueNotAsExpectedException {
		context.getLogger().debug(constructMsg(reference, actual));
		if (!(Byte.toUnsignedInt(actual) <= Byte.toUnsignedInt(reference))) {
			throw new ValueNotAsExpectedException(desc + strLessOrEqual_fail);
		}
	}

	/**
	 * Validates that {@code actual} byte value is less than or equal to {@code reference} byte value. If they are not, an
	 * {@link ValueNotAsExpectedException} is thrown with the given message.
	 * 
	 * @param reference Reference byte value
	 * @param actual Actual byte value
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardLessOrEqualsTo(final byte reference, final byte actual) throws ValueNotAsExpectedException {
		if (!(Byte.toUnsignedInt(actual) <= Byte.toUnsignedInt(reference))) {
			throw new ValueNotAsExpectedException(strLessOrEqual_fail + "\n" + constructMsg(reference, actual));
		}
	}

	// *******************************************************************************************
	// Byte Array
	// *******************************************************************************************
	/**
	 * Validates that two byte arrays are equal. Appropriate boolean value will be returned based on comparison.
	 * 
	 * @param reference Reference byte array
	 * @param actual Actual byte array
	 * @return true | false
	 */
	public static boolean isEquals(final byte[] reference, final byte[] actual) {
		return Arrays.equals(reference, actual) ? true : false;
	}

	/**
	 * Validates that two byte arrays are equal. If they are not, an {@link ValueNotAsExpectedException} is thrown with the given message.
	 * 
	 * @deprecated recommended alternative {@link #guardEquals(byte[], byte[])}
	 * @param context test context
	 * @param desc Message to be printed
	 * @param reference Reference byte array
	 * @param actual Actual byte array
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	@Deprecated
	public static void guardEquals(TestContext context, final String desc, final byte[] reference, final byte[] actual)
			throws ValueNotAsExpectedException {
		context.getLogger().debug(constructMsg(reference, actual));
		if (!isEquals(reference, actual)) {
			throw new ValueNotAsExpectedException(desc + strEqual_fail);
		}
	}

	/**
	 * Validates that two byte arrays are equal. If they are not, an {@link ValueNotAsExpectedException} is thrown with the given message.
	 * 
	 * @param reference Reference byte array
	 * @param actual Actual byte array
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardEquals(final byte[] reference, final byte[] actual) throws ValueNotAsExpectedException {
		if (!isEquals(reference, actual)) {
			throw new ValueNotAsExpectedException(strEqual_fail + "\n" + constructMsg(reference, actual));
		}
	}

	/**
	 * Validates that two byte arrays are <b>not</b> equal. If they are, an {@link ValueNotAsExpectedException} is thrown with the given message.
	 * 
	 * @deprecated recommended alternative {@link #guardNotEquals(byte[], byte[])}
	 * @param context test context
	 * @param desc Message to be printed
	 * @param reference Reference byte array
	 * @param actual Actual byte array
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	@Deprecated
	public static void guardNotEquals(TestContext context, final String desc, final byte[] reference, final byte[] actual)
			throws ValueNotAsExpectedException {
		context.getLogger().debug(constructMsg(reference, actual));
		if (isEquals(reference, actual)) {
			throw new ValueNotAsExpectedException(desc + strNotEqual_fail);
		}
	}

	/**
	 * Validates that two byte arrays are <b>not</b> equal. If they are, an {@link ValueNotAsExpectedException} is thrown with the given message.
	 * 
	 * @param reference Reference byte array
	 * @param actual Actual byte array
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardNotEquals(final byte[] reference, final byte[] actual) throws ValueNotAsExpectedException {
		if (isEquals(reference, actual)) {
			throw new ValueNotAsExpectedException(strNotEqual_fail + "\n" + constructMsg(reference, actual));
		}
	}

	// *******************************************************************************************
	// Integer
	// *******************************************************************************************
	/**
	 * Validates that two integer values are equal. Appropriate boolean value will be returned based on comparison.
	 * 
	 * @param reference Reference integer value
	 * @param actual Actual integer value
	 * @return true | false
	 */
	public static boolean isEquals(final int reference, final int actual) {
		return (reference == actual) ? true : false;
	}

	/**
	 * Validates that two integer values are equal with allowed {@code delta} on either side. Appropriate boolean value will be returned based on
	 * comparison.
	 * 
	 * @param reference Reference integer value
	 * @param actual Actual integer value
	 * @param delta Variant allowed on either side of the reference value
	 * @return true | false
	 */
	public static boolean isEquals(final int reference, final int actual, final int delta) {
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

		return (actual >= MinBoundry && actual <= MaxBoundry) ? true : false;
	}

	/**
	 * Validates that two integer values are equal. If they are not, an {@link ValueNotAsExpectedException} is thrown with the given message.
	 * 
	 * @deprecated recommended alternative {@link #guardEquals(int, int)}
	 * @param context test context
	 * @param desc Message to be printed
	 * @param reference Reference integer value
	 * @param actual Actual integer value
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	@Deprecated
	public static void guardEquals(TestContext context, final String desc, final int reference, final int actual) throws ValueNotAsExpectedException {
		context.getLogger().debug(constructMsg(reference, actual));
		if (!isEquals(reference, actual)) {
			throw new ValueNotAsExpectedException(desc + strEqual_fail);
		}
	}

	/**
	 * Validates that two integer values are equal. If they are not, an {@link ValueNotAsExpectedException} is thrown with the given message.
	 * 
	 * @param reference Reference integer value
	 * @param actual Actual integer value
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardEquals(final int reference, final int actual) throws ValueNotAsExpectedException {
		if (!isEquals(reference, actual)) {
			throw new ValueNotAsExpectedException(strEqual_fail + "\n" + constructMsg(reference, actual));
		}
	}

	/**
	 * Validates that two integer values are equal with allowed {@code delta} on either side. If they are not, an {@link ValueNotAsExpectedException}
	 * is thrown with the given message.
	 * 
	 * @deprecated recommended alternative {@link #guardEquals(int, int, int)}
	 * @param context test context
	 * @param desc Message to be printed
	 * @param reference Reference integer value
	 * @param actual Actual integer value
	 * @param delta Variant allowed on either side of the reference value
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	@Deprecated
	public static void guardEquals(TestContext context, final String desc, final int reference, final int actual, final int delta)
			throws ValueNotAsExpectedException {
		context.getLogger().debug(constructMsg(reference, actual, delta));
		if (!isEquals(reference, actual, delta)) {
			throw new ValueNotAsExpectedException(desc + strEqual_fail);
		}
	}

	/**
	 * Validates that two integer values are equal with allowed {@code delta} on either side. If they are not, an {@link ValueNotAsExpectedException}
	 * is thrown with the given message.
	 * 
	 * @param reference Reference integer value
	 * @param actual Actual integer value
	 * @param delta Variant allowed on either side of the reference value
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardEquals(final int reference, final int actual, final int delta) throws ValueNotAsExpectedException {
		if (!isEquals(reference, actual, delta)) {
			throw new ValueNotAsExpectedException(strEqual_fail + "\n" + constructMsg(reference, actual, delta));
		}
	}

	/**
	 * Validates that two integer values are <b>not</b> equal. If they are, an {@link ValueNotAsExpectedException} is thrown with the given message.
	 * 
	 * @deprecated recommended alternative {@link #guardNotEquals(int, int)}
	 * @param context test context
	 * @param desc Message to be printed
	 * @param reference Reference integer value
	 * @param actual Actual integer value
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	@Deprecated
	public static void guardNotEquals(TestContext context, final String desc, final int reference, final int actual)
			throws ValueNotAsExpectedException {
		context.getLogger().debug(constructMsg(reference, actual));
		if (isEquals(reference, actual)) {
			throw new ValueNotAsExpectedException(desc + strNotEqual_fail);
		}
	}

	/**
	 * Validates that two integer values are <b>not</b> equal. If they are, an {@link ValueNotAsExpectedException} is thrown with the given message.
	 * 
	 * @param reference Reference integer value
	 * @param actual Actual integer value
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardNotEquals(final int reference, final int actual) throws ValueNotAsExpectedException {
		if (isEquals(reference, actual)) {
			throw new ValueNotAsExpectedException(strNotEqual_fail + "\n" + constructMsg(reference, actual));
		}
	}

	/**
	 * Validates that {@code reference} integer value is greater than {@code actual} integer value. If they are not, an
	 * {@link ValueNotAsExpectedException} is thrown with the given message.
	 * 
	 * @deprecated recommended alternative {@link #guardGreaterThan(int, int)}
	 * @param context test context
	 * @param desc Message to be printed
	 * @param reference Reference integer value
	 * @param actual Actual integer value
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	@Deprecated
	public static void guardGreaterThan(TestContext context, final String desc, final int reference, final int actual)
			throws ValueNotAsExpectedException {
		context.getLogger().debug(constructMsg(reference, actual));
		if (!(actual > reference)) {
			throw new ValueNotAsExpectedException(desc + strGreater_fail);
		}
	}

	/**
	 * Validates that {@code reference} integer value is greater than {@code actual} integer value. If they are not, an
	 * {@link ValueNotAsExpectedException} is thrown with the given message.
	 * 
	 * @param reference Reference integer value
	 * @param actual Actual integer value
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardGreaterThan(final int reference, final int actual) throws ValueNotAsExpectedException {
		if (!(actual > reference)) {
			throw new ValueNotAsExpectedException(strGreater_fail + "\n" + constructMsg(reference, actual));
		}
	}

	/**
	 * Validates that {@code reference} integer value is greater than or equal to {@code actual} integer value. If they are not, an
	 * {@link ValueNotAsExpectedException} is thrown with the given message.
	 * 
	 * @deprecated recommended alternative {@link #guardGreaterOrEqualsTo(int, int)}
	 * @param context test context
	 * @param desc Message to be printed
	 * @param reference Reference integer value
	 * @param actual Actual integer value
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	@Deprecated
	public static void guardGreaterOrEqualsTo(TestContext context, final String desc, final int reference, final int actual)
			throws ValueNotAsExpectedException {
		context.getLogger().debug(constructMsg(reference, actual));
		if (!(actual >= reference)) {
			throw new ValueNotAsExpectedException(desc + strGreaterOrEqual_fail);
		}
	}

	/**
	 * Validates that {@code reference} integer value is greater than or equal to {@code actual} integer value. If they are not, an
	 * {@link ValueNotAsExpectedException} is thrown with the given message.
	 * 
	 * @param reference Reference integer value
	 * @param actual Actual integer value
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardGreaterOrEqualsTo(final int reference, final int actual) throws ValueNotAsExpectedException {
		if (!(actual >= reference)) {
			throw new ValueNotAsExpectedException(strGreaterOrEqual_fail + "\n" + constructMsg(reference, actual));
		}
	}

	/**
	 * Validates that {@code reference} integer value is less than {@code actual} integer value. If they are not, an
	 * {@link ValueNotAsExpectedException} is thrown with the given message.
	 * 
	 * @deprecated recommended alternative {@link #guardLessThan(int, int)}
	 * @param context test context
	 * @param desc Message to be printed
	 * @param reference Reference integer value
	 * @param actual Actual integer value
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	@Deprecated
	public static void guardLessThan(TestContext context, final String desc, final int reference, final int actual)
			throws ValueNotAsExpectedException {
		context.getLogger().debug(constructMsg(reference, actual));
		if (!(actual < reference)) {
			throw new ValueNotAsExpectedException(desc + strLess_fail);
		}
	}

	/**
	 * Validates that {@code reference} integer value is less than {@code actual} integer value. If they are not, an
	 * {@link ValueNotAsExpectedException} is thrown with the given message.
	 * 
	 * @param reference Reference integer value
	 * @param actual Actual integer value
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardLessThan(final int reference, final int actual) throws ValueNotAsExpectedException {
		if (!(actual < reference)) {
			throw new ValueNotAsExpectedException(strLess_fail + "\n" + constructMsg(reference, actual));
		}
	}

	/**
	 * Validates that {@code reference} integer value is less than or equal to {@code actual} integer value. If they are not, an
	 * {@link ValueNotAsExpectedException} is thrown with the given message.
	 * 
	 * @deprecated recommended alternative {@link #guardLessOrEqualsTo(int, int)}
	 * @param context test context
	 * @param desc Message to be printed
	 * @param reference Reference integer value
	 * @param actual Actual integer value
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	@Deprecated
	public static void guardLessOrEqualsTo(TestContext context, final String desc, final int reference, final int actual)
			throws ValueNotAsExpectedException {
		context.getLogger().debug(constructMsg(reference, actual));
		if (!(actual <= reference)) {
			throw new ValueNotAsExpectedException(desc + strLessOrEqual_fail);
		}
	}

	/**
	 * Validates that {@code reference} integer value is less than or equal to {@code actual} integer value. If they are not, an
	 * {@link ValueNotAsExpectedException} is thrown with the given message.
	 * 
	 * @param reference Reference integer value
	 * @param actual Actual integer value
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardLessOrEqualsTo(final int reference, final int actual) throws ValueNotAsExpectedException {
		if (!(actual <= reference)) {
			throw new ValueNotAsExpectedException(strLessOrEqual_fail + "\n" + constructMsg(reference, actual));
		}
	}
	
	// *******************************************************************************************
	// Double
	// *******************************************************************************************
	/**
	 * Validates that two double values are equal. Appropriate boolean value will be returned based on comparison.
	 * 
	 * @param reference Reference double value
	 * @param actual Actual double value
	 * @return true | false
	 */
	public static boolean isEquals(final double reference, final double actual) {
		return (reference == actual) ? true : false;
	}

	/**
	 * Validates that two double values are equal with allowed {@code delta} on either side. Appropriate boolean value will be returned based on
	 * comparison.
	 * 
	 * @param reference Reference double value
	 * @param actual Actual double value
	 * @param delta Variant allowed on either side of the reference value
	 * @return true | false
	 */
	public static boolean isEquals(final double reference, final double actual, final double delta) {
		// To ensure we do not exceed double max boundary
		double MaxBoundry;
		if (reference >= Double.MAX_VALUE - delta) {
			MaxBoundry = Double.MAX_VALUE;
		} else {
			MaxBoundry = reference + delta;
		}
		// To ensure we do not exceed integer min boundary
		double MinBoundry;
		if (reference <= Double.MIN_VALUE + delta) {
			MinBoundry = Double.MIN_VALUE;
		} else {
			MinBoundry = reference - delta;
		}

		return (actual >= MinBoundry && actual <= MaxBoundry) ? true : false;
	}

	/**
	 * Validates that two double values are equal. If they are not, an {@link ValueNotAsExpectedException} is thrown with the given message.
	 * 
	 * @param reference Reference integer value
	 * @param actual Actual integer value
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardEquals(final double reference, final double actual) throws ValueNotAsExpectedException {
		if (!isEquals(reference, actual)) {
			throw new ValueNotAsExpectedException(strEqual_fail + "\n" + constructMsg(reference, actual));
		}
	}

	/**
	 * Validates that two double values are equal with allowed {@code delta} on either side. If they are not, an {@link ValueNotAsExpectedException}
	 * is thrown with the given message.
	 * 
	 * @param reference Reference double value
	 * @param actual Actual double value
	 * @param delta Variant allowed on either side of the reference value
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardEquals(final double reference, final double actual, final double delta) throws ValueNotAsExpectedException {
		if (!isEquals(reference, actual, delta)) {
			throw new ValueNotAsExpectedException(strEqual_fail + "\n" + constructMsg(reference, actual, delta));
		}
	}

	/**
	 * Validates that two double values are <b>not</b> equal. If they are, an {@link ValueNotAsExpectedException} is thrown with the given message.
	 * 
	 * @param reference Reference double value
	 * @param actual Actual double value
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardNotEquals(final double reference, final double actual) throws ValueNotAsExpectedException {
		if (isEquals(reference, actual)) {
			throw new ValueNotAsExpectedException(strNotEqual_fail + "\n" + constructMsg(reference, actual));
		}
	}

	/**
	 * Validates that {@code reference} double value is greater than {@code actual} double value. If they are not, an
	 * {@link ValueNotAsExpectedException} is thrown with the given message.
	 * 
	 * @param reference Reference double value
	 * @param actual Actual double value
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardGreaterThan(final double reference, final double actual) throws ValueNotAsExpectedException {
		if (!(actual > reference)) {
			throw new ValueNotAsExpectedException(strGreater_fail + "\n" + constructMsg(reference, actual));
		}
	}

	/**
	 * Validates that {@code reference} double value is greater than or equal to {@code actual} double value. If they are not, an
	 * {@link ValueNotAsExpectedException} is thrown with the given message.
	 * 
	 * @param reference Reference double value
	 * @param actual Actual double value
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardGreaterOrEqualsTo(final double reference, final double actual) throws ValueNotAsExpectedException {
		if (!(actual >= reference)) {
			throw new ValueNotAsExpectedException(strGreaterOrEqual_fail + "\n" + constructMsg(reference, actual));
		}
	}

	/**
	 * Validates that {@code reference} double value is less than {@code actual} double value. If they are not, an
	 * {@link ValueNotAsExpectedException} is thrown with the given message.
	 * 
	 * @param reference Reference double value
	 * @param actual Actual double value
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardLessThan(final double reference, final double actual) throws ValueNotAsExpectedException {
		if (!(actual < reference)) {
			throw new ValueNotAsExpectedException(strLess_fail + "\n" + constructMsg(reference, actual));
		}
	}

	/**
	 * Validates that {@code reference} double value is less than or equal to {@code actual} double value. If they are not, an
	 * {@link ValueNotAsExpectedException} is thrown with the given message.
	 * 
	 * @param reference Reference double value
	 * @param actual Actual double value
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardLessOrEqualsTo(final double reference, final double actual) throws ValueNotAsExpectedException {
		if (!(actual <= reference)) {
			throw new ValueNotAsExpectedException(strLessOrEqual_fail + "\n" + constructMsg(reference, actual));
		}
	}

	// *******************************************************************************************
	// Long
	// *******************************************************************************************
	/**
	 * Validates that two long values are equal. Appropriate boolean value will be returned based on comparison.
	 * 
	 * @param reference Reference long value
	 * @param actual Actual long value
	 * @return true | false
	 */
	public static boolean isEquals(final long reference, final long actual) {
		return (reference == actual) ? true : false;
	}

	/**
	 * Validates that two long values are equal with allowed {@code delta} on either side. Appropriate boolean value will be returned based on
	 * comparison.
	 * 
	 * @param reference Reference long value
	 * @param actual Actual long value
	 * @param delta Variant allowed on either side of the reference value
	 * @return true | false
	 */
	public static boolean isEquals(final long reference, final long actual, final long delta) {
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

		return (actual >= MinBoundry && actual <= MaxBoundry) ? true : false;
	}

	/**
	 * Validates that two long values are equal. If they are not, an {@link ValueNotAsExpectedException} is thrown with the given message.
	 * 
	 * @deprecated recommended alternative {@link #guardEquals(long, long)}
	 * @param context test context
	 * @param desc Message to be printed
	 * @param reference Reference long value
	 * @param actual Actual long value
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	@Deprecated
	public static void guardEquals(TestContext context, final String desc, final long reference, final long actual)
			throws ValueNotAsExpectedException {
		context.getLogger().debug(constructMsg(reference, actual));
		if (!isEquals(reference, actual)) {
			throw new ValueNotAsExpectedException(desc + strEqual_fail);
		}
	}

	/**
	 * Validates that two long values are equal. If they are not, an {@link ValueNotAsExpectedException} is thrown with the given message.
	 * 
	 * @param reference Reference long value
	 * @param actual Actual long value
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardEquals(final long reference, final long actual) throws ValueNotAsExpectedException {
		if (!isEquals(reference, actual)) {
			throw new ValueNotAsExpectedException(strEqual_fail + "\n" + constructMsg(reference, actual));
		}
	}

	/**
	 * Validates that two long values are equal with allowed {@code delta} on either side. If they are not, an {@link ValueNotAsExpectedException} is
	 * thrown with the given message.
	 * 
	 * @deprecated recommended alternative {@link #guardEquals(long, long, long)}
	 * @param context test context
	 * @param desc Message to be printed
	 * @param reference Reference long value
	 * @param actual Actual long value
	 * @param delta Variant allowed on either side of the reference value
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	@Deprecated
	public static void guardEquals(TestContext context, final String desc, final long reference, final long actual, final long delta)
			throws ValueNotAsExpectedException {
		context.getLogger().debug(constructMsg(reference, actual, delta));
		if (!isEquals(reference, actual, delta)) {
			throw new ValueNotAsExpectedException(desc + strEqual_fail);
		}
	}

	/**
	 * Validates that two long values are equal with allowed {@code delta} on either side. If they are not, an {@link ValueNotAsExpectedException} is
	 * thrown with the given message.
	 * 
	 * @param reference Reference long value
	 * @param actual Actual long value
	 * @param delta Variant allowed on either side of the reference value
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardEquals(final long reference, final long actual, final long delta) throws ValueNotAsExpectedException {
		if (!isEquals(reference, actual, delta)) {
			throw new ValueNotAsExpectedException(strEqual_fail + "\n" + constructMsg(reference, actual, delta));
		}
	}

	/**
	 * Validates that two long values are <b>not</b> equal. If they are, an {@link ValueNotAsExpectedException} is thrown with the given message.
	 * 
	 * @deprecated recommended alternative {@link #guardNotEquals(long, long)}
	 * @param context test context
	 * @param desc Message to be printed
	 * @param reference Reference long value
	 * @param actual Actual long value
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	@Deprecated
	public static void guardNotEquals(TestContext context, final String desc, final long reference, final long actual)
			throws ValueNotAsExpectedException {
		context.getLogger().debug(constructMsg(reference, actual));
		if (isEquals(reference, actual)) {
			throw new ValueNotAsExpectedException(desc + strNotEqual_fail);
		}
	}

	/**
	 * Validates that two long values are <b>not</b> equal. If they are, an {@link ValueNotAsExpectedException} is thrown with the given message.
	 * 
	 * @param reference Reference long value
	 * @param actual Actual long value
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardNotEquals(final long reference, final long actual) throws ValueNotAsExpectedException {
		if (isEquals(reference, actual)) {
			throw new ValueNotAsExpectedException(strNotEqual_fail + "\n" + constructMsg(reference, actual));
		}
	}

	/**
	 * Validates that {@code reference} long value is greater than {@code actual} long value. If they are not, an {@link ValueNotAsExpectedException}
	 * is thrown with the given message.
	 * 
	 * @deprecated recommended alternative {@link #guardGreaterThan(long, long)}
	 * @param context test context
	 * @param desc Message to be printed
	 * @param reference Reference long value
	 * @param actual Actual long value
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	@Deprecated
	public static void guardGreaterThan(TestContext context, final String desc, final long reference, final long actual)
			throws ValueNotAsExpectedException {
		context.getLogger().debug(constructMsg(reference, actual));
		if (!(actual > reference)) {
			throw new ValueNotAsExpectedException(desc + strGreater_fail);
		}
	}

	/**
	 * Validates that {@code reference} long value is greater than {@code actual} long value. If they are not, an {@link ValueNotAsExpectedException}
	 * is thrown with the given message.
	 * 
	 * @param reference Reference long value
	 * @param actual Actual long value
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardGreaterThan(final long reference, final long actual) throws ValueNotAsExpectedException {
		if (!(actual > reference)) {
			throw new ValueNotAsExpectedException(strGreater_fail + "\n" + constructMsg(reference, actual));
		}
	}

	/**
	 * Validates that {@code reference} long value is greater than or equal to {@code actual} long value. If they are not, an
	 * {@link ValueNotAsExpectedException} is thrown with the given message.
	 * 
	 * @deprecated recommended alternative {@link #guardGreaterOrEqualsTo(long, long)}
	 * @param context test context
	 * @param desc Message to be printed
	 * @param reference Reference long value
	 * @param actual Actual long value
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardGreaterOrEqualsTo(TestContext context, final String desc, final long reference, final long actual)
			throws ValueNotAsExpectedException {
		context.getLogger().debug(constructMsg(reference, actual));
		if (!(actual >= reference)) {
			throw new ValueNotAsExpectedException(desc + strGreaterOrEqual_fail);
		}
	}

	/**
	 * Validates that {@code reference} long value is greater than or equal to {@code actual} long value. If they are not, an
	 * {@link ValueNotAsExpectedException} is thrown with the given message.
	 * 
	 * @param reference Reference long value
	 * @param actual Actual long value
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardGreaterOrEqualsTo(final long reference, final long actual) throws ValueNotAsExpectedException {
		if (!(actual >= reference)) {
			throw new ValueNotAsExpectedException(strGreaterOrEqual_fail + "\n" + constructMsg(reference, actual));
		}
	}

	/**
	 * Validates that {@code reference} long value is less than {@code actual} long value. If they are not, an {@link ValueNotAsExpectedException} is
	 * thrown with the given message.
	 * 
	 * @deprecated recommended alternative {@link #guardLessThan(long, long)}
	 * @param context test context
	 * @param desc Message to be printed
	 * @param reference Reference long value
	 * @param actual Actual long value
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	@Deprecated
	public static void guardLessThan(TestContext context, final String desc, final long reference, final long actual)
			throws ValueNotAsExpectedException {
		context.getLogger().debug(constructMsg(reference, actual));
		if (!(actual < reference)) {
			throw new ValueNotAsExpectedException(desc + strLess_fail);
		}
	}

	/**
	 * Validates that {@code reference} long value is less than {@code actual} long value. If they are not, an {@link ValueNotAsExpectedException} is
	 * thrown with the given message.
	 * 
	 * @param reference Reference long value
	 * @param actual Actual long value
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardLessThan(final long reference, final long actual) throws ValueNotAsExpectedException {
		if (!(actual < reference)) {
			throw new ValueNotAsExpectedException(strLess_fail + "\n" + constructMsg(reference, actual));
		}
	}

	/**
	 * Validates that {@code reference} long value is less than or equal to {@code actual} long value. If they are not, an
	 * {@link ValueNotAsExpectedException} is thrown with the given message.
	 * 
	 * @deprecated recommended alternative {@link #guardLessOrEqualsTo(long, long)}
	 * @param context test context
	 * @param desc Message to be printed
	 * @param reference Reference long value
	 * @param actual Actual long value
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	@Deprecated
	public static void guardLessOrEqualsTo(TestContext context, final String desc, final long reference, final long actual)
			throws ValueNotAsExpectedException {
		context.getLogger().debug(constructMsg(reference, actual));
		if (!(actual <= reference)) {
			throw new ValueNotAsExpectedException(desc + strLessOrEqual_fail);
		}
	}

	/**
	 * Validates that {@code reference} long value is less than or equal to {@code actual} long value. If they are not, an
	 * {@link ValueNotAsExpectedException} is thrown with the given message.
	 * 
	 * @param reference Reference long value
	 * @param actual Actual long value
	 * @throws ValueNotAsExpectedException Exception is thrown if value is not meeting defined criteria
	 */
	public static void guardLessOrEqualsTo(final long reference, final long actual) throws ValueNotAsExpectedException {
		if (!(actual <= reference)) {
			throw new ValueNotAsExpectedException(strLessOrEqual_fail + "\n" + constructMsg(reference, actual));
		}
	}

	// *******************************************************************************************
	// Exception
	// *******************************************************************************************
	/**
	 * Validates that exception message contains provided string, if it does not then same exception is thrown back.
	 * 
	 * <PRE>
	 * {@code
	 * Example : guardEquals(context, e, "invalid value");
	 * }
	 * </PRE>
	 * 
	 * @deprecated recommended alternative {@link #guardEquals(Exception, String)}
	 * @param context test context
	 * @param e Exception which required to be verified
	 * @param exceptionMsg Exception msg
	 * @throws Exception if exception message is not as expected then same exception is thrown again
	 */
	@Deprecated
	public static void guardEquals(TestContext context, Exception e, final String exceptionMsg) throws Exception {
		context.getLogger().debug(constructMsg(e.getMessage(), exceptionMsg));
		if (!e.getMessage().contains(exceptionMsg)) {
			throw e;
		}
	}

	/**
	 * Validates that exception message contains provided string, if it does not then same exception is thrown back.
	 * 
	 * <PRE>
	 * {@code
	 * Example : guardEquals(e, "invalid value");
	 * }
	 * </PRE>
	 * 
	 * @param e Exception which required to be verified
	 * @param exceptionMsg Exception msg
	 * @throws Exception if exception message is not as expected then same exception is thrown again
	 */
	public static void guardEquals(Exception e, final String exceptionMsg) throws Exception {
		if (!e.getMessage().contains(exceptionMsg)) {
			throw e;
		}
	}

	// *******************************************************************************************
	// Flow
	// *******************************************************************************************
	/**
	 * Guard against wrong flow. If code hits this code then {@link WrongFlowException} is thrown
	 * 
	 * <PRE>
	 * {@code
	 * Example : guardWrongFlow("invalid flow");
	 * }
	 * </PRE>
	 * 
	 * @param msg Message to be printed
	 * @throws WrongFlowException Indicates that code is following wrong flow
	 */
	public static void guardWrongFlow(final String msg) throws WrongFlowException {
		throw new WrongFlowException(msg);
	}

}
