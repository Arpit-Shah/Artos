package test.com.artos.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.artos.exception.WrongFlowException;
import com.artos.utils.Guard;
import com.artos.utils.Transform;

public class TestGuard {

	Transform _transform = new Transform();

	// *******************************************************************************************
	// NULL
	// *******************************************************************************************

	@Test
	public void testIsNull() {
		assertTrue(Guard.isNull(null));
		assertFalse(Guard.isNull(new Object()));
	}

	// *******************************************************************************************
	// String
	// *******************************************************************************************

	@Test
	public void testIsEqualsString() {
		// same value
		String actual = "testString";
		String expected = "testString";
		assertTrue(Guard.isEquals(expected, actual));

		// same null value
		expected = null;
		actual = null;
		assertTrue(Guard.isEquals(expected, actual));

		// different value
		actual = "TestString";
		expected = "testString";
		assertFalse(Guard.isEquals(expected, actual));

		// different null value
		expected = "testString";
		actual = null;
		assertFalse(Guard.isEquals(expected, actual));

		// different null value
		expected = null;
		actual = "testString";
		assertFalse(Guard.isEquals(expected, actual));

		// regex comparision
		expected = "(\\d{2}\\.\\d{2}\\.\\d{4}(\\.\\d*)*)";
		actual = "01.08.0082.150";
		assertTrue(Guard.isEquals(expected, actual));

		// regex comparision
		expected = "(\\d{2}\\.\\d{2}\\.\\d{4}(\\.\\d*)*)";
		actual = "01.08.0082";
		assertTrue(Guard.isEquals(expected, actual));

		// regex comparision
		expected = "(\\d{2}\\.\\d{2}\\.\\d{4}(\\.\\d*)*)";
		actual = "01.08.008";
		assertFalse(Guard.isEquals(expected, actual));
	}

	@Test
	public void testGuardEqualsString() throws Exception {
		// same value
		String actual = "testString";
		String expected = "testString";
		Guard.guardEquals(expected, actual);

		// same null value
		expected = null;
		actual = null;
		Guard.guardEquals(expected, actual);

		try {
			// different value
			actual = "TestString";
			expected = "testString";
			Guard.guardEquals(expected, actual);

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "values are not equal");
		}

		try {
			// different value
			actual = null;
			expected = "testString";
			Guard.guardEquals(expected, actual);

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "values are not equal");
		}

		try {
			// different value
			actual = "TestString";
			expected = null;
			Guard.guardEquals(expected, actual);

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "values are not equal");
		}
	}

	@Test
	public void testGuardNotEqualsString() throws Exception {
		try {
			// same value
			String actual = "testString";
			String expected = "testString";
			Guard.guardNotEquals(expected, actual);

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "values are equal");
		}

		try {
			// same null value
			String expected = null;
			String actual = null;
			Guard.guardNotEquals(expected, actual);

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "values are equal");
		}

		// different value
		String actual = "TestString";
		String expected = "testString";
		Guard.guardNotEquals(expected, actual);

		// different value
		actual = null;
		expected = "testString";
		Guard.guardNotEquals(expected, actual);

		// different value
		actual = "TestString";
		expected = null;
		Guard.guardNotEquals(expected, actual);
	}

	@Test
	public void testGuardEqualsIgnoreCaseString() throws Exception {
		// same value
		String actual = "testString";
		String expected = "testString";
		Guard.guardEqualsIgnoreCase(expected, actual);

		//Case-sensetive value
		actual = "TestString";
		expected = "TESTSTRING";
		Guard.guardEqualsIgnoreCase(expected, actual);

		//Case-sensetive value
		actual = "test.string.com";
		expected = "TEST.STRING.com";
		Guard.guardEqualsIgnoreCase(expected, actual);

		//Regex comparision
		actual = "323737bc-13ab-4d96-9708-54d19ae18d37";
		expected = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";
		Guard.guardEqualsIgnoreCase(expected, actual);

		//Regex comparision
		actual = "192.164.201.15";
		expected = "\\b((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(\\.|$)){4}\\b";
		Guard.guardEqualsIgnoreCase(expected, actual);

		// same null value
		expected = null;
		actual = null;
		Guard.guardEqualsIgnoreCase(expected, actual);

		try {
			// different value
			actual = "foo";
			expected = "bar";
			Guard.guardEqualsIgnoreCase(expected, actual);

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "values are not equal");
		}

		try {
			// different value
			actual = null;
			expected = "testString";
			Guard.guardEqualsIgnoreCase(expected, actual);

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "values are not equal");
		}

		try {
			// different value
			actual = "TestString";
			expected = null;
			Guard.guardEqualsIgnoreCase(expected, actual);

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "values are not equal");
		}

		try {
			// different value
			actual = "test string";
			expected = "testString";
			Guard.guardEqualsIgnoreCase(expected, actual);

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "values are not equal");
		}

		try {
			// different regex value
			actual = "323737BC-13ab-4d96-9708-54d19ae18d37";
			expected = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";
			Guard.guardEqualsIgnoreCase(expected, actual);

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "values are not equal");
		}
		try {
			// different value
			actual = "192.164.abc.15";
			expected = "\\b((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(\\.|$)){4}\\b";
			Guard.guardEqualsIgnoreCase(expected, actual);

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "values are not equal");
		}
	}

	// *******************************************************************************************
	// Boolean
	// *******************************************************************************************

	@Test
	public void testIsEqualsBoolean() {
		// same value
		assertTrue(Guard.isEquals(true, true));

		// same value
		assertTrue(Guard.isEquals(false, false));

		// different value
		assertFalse(Guard.isEquals(true, false));

		// different value
		assertFalse(Guard.isEquals(false, true));
	}

	@Test
	public void testGuardEqualsBoolean() throws Exception {
		// same value
		Guard.guardEquals(true, true);

		// same value
		Guard.guardEquals(false, false);

		try {
			// different value
			Guard.guardEquals(true, false);

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "values are not equal");
		}
		try {
			// different value
			Guard.guardEquals(false, true);

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "values are not equal");
		}
	}

	@Test
	public void testGuardNotEqualsBoolean() throws Exception {
		try {
			// same value
			Guard.guardNotEquals(true, true);

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "values are equal");
		}

		try {
			// same value
			Guard.guardNotEquals(false, false);

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "values are equal");
		}

		// different value
		Guard.guardNotEquals(true, false);

		// different value
		Guard.guardNotEquals(false, true);

	}

	@Test
	public void testGuardTrue() throws Exception {
		Guard.guardTrue(true);

		try {
			Guard.guardTrue(false);

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "values are not equal");
		}
	}

	@Test
	public void testGuardFalse() throws Exception {
		Guard.guardFalse(false);

		try {
			Guard.guardFalse(true);

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "values are not equal");
		}
	}

	// *******************************************************************************************
	// Format
	// *******************************************************************************************

	@Test
	public void testIsFormatEquals() {
		assertTrue(Guard.isFormatEquals("$$.$$.$$$$", "12.34.1234"));
		assertTrue(Guard.isFormatEquals("$$$$$$$$", "12341234"));
		assertTrue(Guard.isFormatEquals("1234", "1234"));
		assertTrue(Guard.isFormatEquals("$", "1"));
		assertTrue(Guard.isFormatEquals("1$1", "111"));
		assertTrue(Guard.isFormatEquals("1$ $1", "11 11"));
		assertTrue(Guard.isFormatEquals("1$\n$1", "11\n11"));
		assertTrue(Guard.isFormatEquals(null, null));

		assertFalse(Guard.isFormatEquals("$$$$", "12 34"));
		assertFalse(Guard.isFormatEquals("$$ $$", "1234"));
		assertFalse(Guard.isFormatEquals("$$.$$", "1234."));
		assertFalse(Guard.isFormatEquals("1$1", null));
		assertFalse(Guard.isFormatEquals(null, "1234"));
	}

	@Test
	public void testGuardFormatEquals() throws Exception {
		Guard.guardFormatEquals("$$.$$.$$$$", "12.34.1234");
		Guard.guardFormatEquals("$$$$$$$$", "12341234");
		Guard.guardFormatEquals("1234", "1234");
		Guard.guardFormatEquals("$", "1");
		Guard.guardFormatEquals("1$1", "111");
		Guard.guardFormatEquals("1$ $1", "11 11");
		Guard.guardFormatEquals("1$\n$1", "11\n11");
		Guard.guardFormatEquals(null, null);

		try {
			Guard.guardFormatEquals("$$$$", "12 34");

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "format are not same");
		}

		try {
			Guard.guardFormatEquals("$$ $$", "1234");

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "format are not same");
		}

		try {
			Guard.guardFormatEquals("$$.$$", "1234.");

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "format are not same");
		}

		try {
			Guard.guardFormatEquals("1$1", null);

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "format are not same");
		}

		try {
			Guard.guardFormatEquals(null, "1234");

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "format are not same");
		}

	}

	@Test
	public void testGuardFormatNotEquals() throws Exception {
		try {
			Guard.guardFormatNotEquals("$$.$$.$$$$", "12.34.1234");

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "format are same");
		}

		try {
			Guard.guardFormatNotEquals("$$$$$$$$", "12341234");

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "format are same");
		}

		try {
			Guard.guardFormatNotEquals("1234", "1234");

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "format are same");
		}

		try {
			Guard.guardFormatNotEquals("$", "1");

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "format are same");
		}

		try {
			Guard.guardFormatNotEquals("1$1", "111");

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "format are same");
		}

		try {
			Guard.guardFormatNotEquals("1$ $1", "11 11");

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "format are same");
		}

		try {
			Guard.guardFormatNotEquals("1$\n$1", "11\n11");

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "format are same");
		}

		try {
			Guard.guardFormatNotEquals(null, null);

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "format are same");
		}

		Guard.guardFormatNotEquals("$$$$", "12 34");
		Guard.guardFormatNotEquals("$$ $$", "1234");
		Guard.guardFormatNotEquals("$$.$$", "1234.");
		Guard.guardFormatNotEquals("1$1", null);
		Guard.guardFormatNotEquals(null, "1234");

	}

	// *******************************************************************************************
	// Byte
	// *******************************************************************************************

	@Test
	public void testIsEqualsByte() {

		assertTrue(Guard.isEquals((byte) 0x00, (byte) 0x00));
		assertTrue(Guard.isEquals((byte) 0xFF, (byte) 0xFF));

		assertFalse(Guard.isEquals((byte) 0xFF, (byte) 0xF1));
		assertFalse(Guard.isEquals((byte) 0x00, (byte) 0xFF));

	}

	@Test
	public void testGuardEqualsByte() throws Exception {

		Guard.guardEquals((byte) 0x00, (byte) 0x00);
		Guard.guardEquals((byte) 0xFF, (byte) 0xFF);
		Guard.guardEquals((byte) 0xFF, (byte) 0xFD, (byte) 0x02);
		Guard.guardEquals((byte) 0x00, (byte) 0x02, (byte) 0x02);
		Guard.guardEquals((byte) 0x00, (byte) 0xFF, (byte) 0xFF);
		Guard.guardEquals((byte) 0xFF, (byte) 0x00, (byte) 0xFF);

		try {
			Guard.guardEquals((byte) 0xFF, (byte) 0xF1);

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "values are not equal");
		}

		try {
			Guard.guardEquals((byte) 0x00, (byte) 0xFF);

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "values are not equal");
		}

		try {
			Guard.guardEquals((byte) 0x00, (byte) 0x05, (byte) 0x02);

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "values are not equal");
		}

		try {
			Guard.guardEquals((byte) 0xFF, (byte) 0x05, (byte) 0x02);

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "values are not equal");
		}

	}

	@Test
	public void testGuardNotEqualsByte() throws Exception {

		try {
			Guard.guardNotEquals((byte) 0x00, (byte) 0x00);

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "values are equal");
		}

		try {
			Guard.guardNotEquals((byte) 0xFF, (byte) 0xFF);

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "values are equal");
		}

		Guard.guardNotEquals((byte) 0xFF, (byte) 0xF1);
		Guard.guardNotEquals((byte) 0x00, (byte) 0xFF);
	}

	@Test
	public void testGuardGreaterThanByte() throws Exception {
		Guard.guardGreaterThan((byte) 0x00, (byte) 0x01);
		Guard.guardGreaterThan((byte) 0xFE, (byte) 0xFF);

		try {
			Guard.guardGreaterThan((byte) 0x00, (byte) 0x00);

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "actual value is not greater than expected value");
		}

		try {
			Guard.guardGreaterThan((byte) 0xFF, (byte) 0xFE);

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "actual value is not greater than expected value");
		}
	}

	@Test
	public void testGuardGreaterOrEqualsToByte() throws Exception {
		Guard.guardGreaterOrEqualsTo((byte) 0x00, (byte) 0x01);
		Guard.guardGreaterOrEqualsTo((byte) 0xFE, (byte) 0xFF);
		Guard.guardGreaterOrEqualsTo((byte) 0x00, (byte) 0x00);

		try {
			Guard.guardGreaterOrEqualsTo((byte) 0xFF, (byte) 0xFE);

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "actual value is less than expected value");
		}
	}

	@Test
	public void testGuardLessThanByte() throws Exception {
		Guard.guardLessThan((byte) 0x01, (byte) 0x00);
		Guard.guardLessThan((byte) 0xFF, (byte) 0xFE);

		try {
			Guard.guardLessThan((byte) 0x00, (byte) 0x00);

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "actual value is not less than expected value");
		}

		try {
			Guard.guardLessThan((byte) 0xFE, (byte) 0xFF);

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "actual value is not less than expected value");
		}
	}

	@Test
	public void testGuardLessOrEqualsToByte() throws Exception {
		Guard.guardLessOrEqualsTo((byte) 0x01, (byte) 0x00);
		Guard.guardLessOrEqualsTo((byte) 0xFF, (byte) 0xFE);
		Guard.guardLessOrEqualsTo((byte) 0x00, (byte) 0x00);

		try {
			Guard.guardLessOrEqualsTo((byte) 0xFE, (byte) 0xFF);

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "actual value is greater than expected value");
		}
	}

	// *******************************************************************************************
	// Byte Array
	// *******************************************************************************************

	@Test
	public void testIsEqualsByteArray() {

		byte[] testByteArray1 = _transform.strHexToByteArray("00 00 00 00");
		byte[] testByteArray2 = _transform.strHexToByteArray("FF FF FF FF");
		byte[] testByteArray3 = _transform.strHexToByteArray("FF FF 01 01");

		assertTrue(Guard.isEquals(testByteArray1, testByteArray1));
		assertTrue(Guard.isEquals(testByteArray2, testByteArray2));
		assertTrue(Guard.isEquals(testByteArray3, testByteArray3));

		assertFalse(Guard.isEquals(testByteArray1, testByteArray2));
		assertFalse(Guard.isEquals(testByteArray2, testByteArray3));

	}

	@Test
	public void testGuardEqualsByteArray() throws Exception {

		byte[] testByteArray1 = _transform.strHexToByteArray("00 00 00 00");
		byte[] testByteArray2 = _transform.strHexToByteArray("FF FF FF FF");
		byte[] testByteArray3 = _transform.strHexToByteArray("FF FF 01 01");

		Guard.guardEquals(testByteArray1, testByteArray1);
		Guard.guardEquals(testByteArray2, testByteArray2);
		Guard.guardEquals(testByteArray3, testByteArray3);

		try {
			Guard.guardEquals(testByteArray1, testByteArray2);

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "values are not equal");
		}

		try {
			Guard.guardEquals(testByteArray1, testByteArray3);

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "values are not equal");
		}

	}

	@Test
	public void testGuardNotEqualsByteArray() throws Exception {

		byte[] testByteArray1 = _transform.strHexToByteArray("00 00 00 00");
		byte[] testByteArray2 = _transform.strHexToByteArray("FF FF FF FF");
		byte[] testByteArray3 = _transform.strHexToByteArray("FF FF 01 01");

		try {
			Guard.guardNotEquals(testByteArray1, testByteArray1);

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "values are equal");
		}

		try {
			Guard.guardNotEquals(testByteArray2, testByteArray2);

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "values are equal");
		}

		Guard.guardNotEquals(testByteArray1, testByteArray2);
		Guard.guardNotEquals(testByteArray2, testByteArray3);
	}

	// *******************************************************************************************
	// Integer
	// *******************************************************************************************

	@Test
	public void testIsEqualsInteger() {

		assertTrue(Guard.isEquals(Integer.MAX_VALUE, Integer.MAX_VALUE));
		assertTrue(Guard.isEquals(Integer.MIN_VALUE, Integer.MIN_VALUE));
		assertTrue(Guard.isEquals(0, 0));
		assertTrue(Guard.isEquals(2147483647 * 2, 2147483647 * 2));

		assertFalse(Guard.isEquals(Integer.MAX_VALUE, Integer.MIN_VALUE));
		assertFalse(Guard.isEquals(Integer.MAX_VALUE, Integer.MAX_VALUE - 1));
		assertFalse(Guard.isEquals(Integer.MAX_VALUE, 0));

	}

	@Test
	public void testGuardEqualsInteger() throws Exception {

		Guard.guardEquals(Integer.MAX_VALUE, Integer.MAX_VALUE);
		Guard.guardEquals(Integer.MIN_VALUE, Integer.MIN_VALUE);
		Guard.guardEquals(0, 0);
		Guard.guardEquals(Integer.MAX_VALUE, Integer.MAX_VALUE - 2, 2);
		Guard.guardEquals(0, 2, 2);
		Guard.guardEquals(Integer.MIN_VALUE, Integer.MIN_VALUE + 2, 2);
		Guard.guardEquals(Integer.MIN_VALUE, 0 - 1, Integer.MAX_VALUE);
		Guard.guardEquals(Integer.MAX_VALUE, 0 + 1, Integer.MAX_VALUE);

		try {
			Guard.guardEquals(Integer.MAX_VALUE, Integer.MIN_VALUE);

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "values are not equal");
		}

		try {
			Guard.guardEquals(0, Integer.MAX_VALUE);

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "values are not equal");
		}

		try {
			Guard.guardEquals(0, 5, 2);

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "values are not equal");
		}

		try {
			Guard.guardEquals(Integer.MAX_VALUE, Integer.MAX_VALUE - 5, 2);

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "values are not equal");
		}

	}

	@Test
	public void testGuardNotEqualsInteger() throws Exception {

		try {
			Guard.guardNotEquals(Integer.MAX_VALUE, Integer.MAX_VALUE);

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "values are equal");
		}

		try {
			Guard.guardNotEquals(Integer.MIN_VALUE, Integer.MIN_VALUE);

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "values are equal");
		}

		Guard.guardNotEquals(Integer.MAX_VALUE, Integer.MIN_VALUE);
		Guard.guardNotEquals(0, Integer.MAX_VALUE);
		Guard.guardNotEquals(0, 1);
		Guard.guardNotEquals(-1, 1);
	}

	@Test
	public void testGuardGreaterThanInteger() throws Exception {
		Guard.guardGreaterThan(0, 1);
		Guard.guardGreaterThan(Integer.MAX_VALUE - 1, Integer.MAX_VALUE);
		Guard.guardGreaterThan(Integer.MIN_VALUE, Integer.MAX_VALUE);

		try {
			Guard.guardGreaterThan(0, 0);

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "actual value is not greater than expected value");
		}

		try {
			Guard.guardGreaterThan(Integer.MAX_VALUE, Integer.MAX_VALUE - 1);

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "actual value is not greater than expected value");
		}

		try {
			Guard.guardGreaterThan(Integer.MAX_VALUE, Integer.MIN_VALUE);

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "actual value is not greater than expected value");
		}
	}

	@Test
	public void testGuardGreaterOrEqualsToInteger() throws Exception {
		Guard.guardGreaterOrEqualsTo(0, 1);
		Guard.guardGreaterOrEqualsTo(Integer.MAX_VALUE - 1, Integer.MAX_VALUE);
		Guard.guardGreaterOrEqualsTo(0, 0);

		try {
			Guard.guardGreaterOrEqualsTo(Integer.MAX_VALUE, Integer.MAX_VALUE - 1);

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "actual value is less than expected value");
		}
	}

	@Test
	public void testGuardLessThanInteger() throws Exception {
		Guard.guardLessThan(1, 0);
		Guard.guardLessThan(Integer.MAX_VALUE, Integer.MAX_VALUE - 1);
		Guard.guardLessThan(Integer.MIN_VALUE + 1, Integer.MIN_VALUE);

		try {
			Guard.guardLessThan(0, 0);

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "actual value is not less than expected value");
		}

		try {
			Guard.guardLessThan(Integer.MAX_VALUE - 1, Integer.MAX_VALUE);

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "actual value is not less than expected value");
		}
	}

	@Test
	public void testGuardLessOrEqualsToInteger() throws Exception {
		Guard.guardLessOrEqualsTo(1, 0);
		Guard.guardLessOrEqualsTo(Integer.MAX_VALUE, Integer.MAX_VALUE - 1);
		Guard.guardLessOrEqualsTo(0, 0);

		try {
			Guard.guardLessOrEqualsTo(Integer.MAX_VALUE - 1, Integer.MAX_VALUE);

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "actual value is greater than expected value");
		}
	}

	// *******************************************************************************************
	// Long
	// *******************************************************************************************

	@Test
	public void testIsEqualsLong() {

		assertTrue(Guard.isEquals(Long.MAX_VALUE, Long.MAX_VALUE));
		assertTrue(Guard.isEquals(Long.MIN_VALUE, Long.MIN_VALUE));
		assertTrue(Guard.isEquals(0, 0));
		assertTrue(Guard.isEquals(2147483647 * 2, 2147483647 * 2));

		assertFalse(Guard.isEquals(Long.MAX_VALUE, Long.MIN_VALUE));
		assertFalse(Guard.isEquals(Long.MAX_VALUE, Long.MAX_VALUE - 1));
		assertFalse(Guard.isEquals(Long.MAX_VALUE, 0));

	}

	@Test
	public void testGuardEqualsLong() throws Exception {

		Guard.guardEquals(Long.MAX_VALUE, Long.MAX_VALUE);
		Guard.guardEquals(Long.MIN_VALUE, Long.MIN_VALUE);
		Guard.guardEquals(0, 0);
		Guard.guardEquals(Long.MAX_VALUE, Long.MAX_VALUE - 2, 2);
		Guard.guardEquals(0, 2, 2);
		Guard.guardEquals(Long.MIN_VALUE, Long.MIN_VALUE + 2, 2);
		Guard.guardEquals(Long.MIN_VALUE, 0 - 1, Long.MAX_VALUE);
		Guard.guardEquals(Long.MAX_VALUE, 0 + 1, Long.MAX_VALUE);

		try {
			Guard.guardEquals(Long.MAX_VALUE, Long.MIN_VALUE);

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "values are not equal");
		}

		try {
			Guard.guardEquals(0, Long.MAX_VALUE);

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "values are not equal");
		}

		try {
			Guard.guardEquals(0, 5, 2);

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "values are not equal");
		}

		try {
			Guard.guardEquals(Long.MAX_VALUE, Long.MAX_VALUE - 5, 2);

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "values are not equal");
		}

	}

	@Test
	public void testGuardNotEqualsLong() throws Exception {

		try {
			Guard.guardNotEquals(Long.MAX_VALUE, Long.MAX_VALUE);

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "values are equal");
		}

		try {
			Guard.guardNotEquals(Long.MIN_VALUE, Long.MIN_VALUE);

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "values are equal");
		}

		Guard.guardNotEquals(Long.MAX_VALUE, Long.MIN_VALUE);
		Guard.guardNotEquals(0, Long.MAX_VALUE);
		Guard.guardNotEquals(0, 1);
		Guard.guardNotEquals(-1, 1);
	}

	@Test
	public void testGuardGreaterThanLong() throws Exception {
		Guard.guardGreaterThan(0, 1);
		Guard.guardGreaterThan(Long.MAX_VALUE - 1, Long.MAX_VALUE);
		Guard.guardGreaterThan(Long.MIN_VALUE, Long.MAX_VALUE);

		try {
			Guard.guardGreaterThan(0, 0);

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "actual value is not greater than expected value");
		}

		try {
			Guard.guardGreaterThan(Long.MAX_VALUE, Long.MAX_VALUE - 1);

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "actual value is not greater than expected value");
		}

		try {
			Guard.guardGreaterThan(Long.MAX_VALUE, Long.MIN_VALUE);

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "actual value is not greater than expected value");
		}
	}

	@Test
	public void testGuardGreaterOrEqualsToLong() throws Exception {
		Guard.guardGreaterOrEqualsTo(0, 1);
		Guard.guardGreaterOrEqualsTo(Long.MAX_VALUE - 1, Long.MAX_VALUE);
		Guard.guardGreaterOrEqualsTo(0, 0);

		try {
			Guard.guardGreaterOrEqualsTo(Long.MAX_VALUE, Long.MAX_VALUE - 1);

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "actual value is less than expected value");
		}
	}

	@Test
	public void testGuardLessThanLong() throws Exception {
		Guard.guardLessThan(1, 0);
		Guard.guardLessThan(Long.MAX_VALUE, Long.MAX_VALUE - 1);
		Guard.guardLessThan(Long.MIN_VALUE + 1, Long.MIN_VALUE);

		try {
			Guard.guardLessThan(0, 0);

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "actual value is not less than expected value");
		}

		try {
			Guard.guardLessThan(Long.MAX_VALUE - 1, Long.MAX_VALUE);

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "actual value is not less than expected value");
		}
	}

	@Test
	public void testGuardLessOrEqualsToLong() throws Exception {
		Guard.guardLessOrEqualsTo(1, 0);
		Guard.guardLessOrEqualsTo(Long.MAX_VALUE, Long.MAX_VALUE - 1);
		Guard.guardLessOrEqualsTo(0, 0);

		try {
			Guard.guardLessOrEqualsTo(Long.MAX_VALUE - 1, Long.MAX_VALUE);

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "actual value is greater than expected value");
		}
	}

	// *******************************************************************************************
	// Exception msg checking
	// *******************************************************************************************

	@Test
	public void testExceptionMsgCheck() throws Exception {
		Exception e = new Exception("Test");
		Guard.guardEquals(e, "Test");
	}

	@Test(expected = Exception.class)
	public void testExceptionMsgCheckBadPath() throws Exception {
		try {
			Guard.guardEquals(Short.MAX_VALUE, Short.MIN_VALUE);

			Guard.guardWrongFlow("Did not expect to reach here");
		} catch (Exception e) {
			Guard.guardEquals(e, "123");
		}
	}

	// *******************************************************************************************
	// Wrong flow
	// *******************************************************************************************

	@Test(expected = WrongFlowException.class)
	public void testWrongFlow() throws Exception {
		Guard.guardWrongFlow("Wrong flow test");
	}

}
