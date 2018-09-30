package test.com.artos.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.artos.utils.Guard;

public class TestGuard {

	@Test
	public void testIsNull() {
		assertTrue(Guard.isNull(null));
		assertFalse(Guard.isNull(new Object()));
	}

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

	// @Test
	// public void testGuardGreaterOrEqualsToTestContextStringByteByte() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGuardLessThanTestContextStringByteByte() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGuardLessOrEqualsToTestContextStringByteByte() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testIsEqualsByteArrayByteArray() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGuardEqualsTestContextStringByteArrayByteArray() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGuardNotEqualsTestContextStringByteArrayByteArray() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testIsEqualsIntInt() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testIsEqualsIntIntInt() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGuardEqualsTestContextStringIntInt() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGuardEqualsTestContextStringIntIntInt() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGuardNotEqualsTestContextStringIntInt() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGuardGreaterThanTestContextStringIntInt() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGuardGreaterOrEqualsToTestContextStringIntInt() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGuardLessThanTestContextStringIntInt() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGuardLessOrEqualsToTestContextStringIntInt() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testIsEqualsLongLong() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testIsEqualsLongLongLong() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGuardEqualsTestContextStringLongLong() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGuardEqualsTestContextStringLongLongLong() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGuardNotEqualsTestContextStringLongLong() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGuardGreaterThanTestContextStringLongLong() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGuardGreaterOrEquals() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGuardLessThanTestContextStringLongLong() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGuardLessOrEqualsToTestContextStringLongLong() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGuardEqualsTestContextExceptionString() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGuardWrongFlow() {
	// fail("Not yet implemented");
	// }

}
