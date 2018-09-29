package test.com.artos.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.artos.utils.Guard;

public class TestGuard {

	

	@Test
	public void testIsNull_true() {
		assertTrue(Guard.isNull(null));
	}
	
	@Test
	public void testIsNull_false() {
		assertFalse(Guard.isNull(new Object()));
	}

	@Test
	public void testIsEqualsString_StringMatch() {
		String actual = "testString";
		String expected = "testString";
		assertEquals(expected, actual);
	}
	
	@Test
	public void testIsEqualsString_StringCase() {
		String actual = "TestString";
		String expected = "testString";
		assertNotEquals(expected, actual);
	}
	
	@Test
	public void testIsEqualsString_Null() {
		String expected = "testString";
		String actual = null;
		assertNotEquals(expected, actual);
	}

//	@Test
//	public void testGuardEqualsTestContextStringStringString() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGuardNotEqualsTestContextStringStringString() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testIsEqualsBooleanBoolean() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGuardEqualsTestContextStringBooleanBoolean() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGuardNotEqualsTestContextStringBooleanBoolean() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGuardTrue() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGuardFalse() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testIsFormatEquals() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGuardFormatEquals() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGuardFormatNotEquals() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testIsEqualsByteByte() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testIsEqualsByteByteByte() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGuardEqualsTestContextStringByteByte() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGuardEqualsTestContextStringByteByteByte() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGuardNotEqualsTestContextStringByteByte() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGuardGreaterThanTestContextStringByteByte() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGuardGreaterOrEqualsToTestContextStringByteByte() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGuardLessThanTestContextStringByteByte() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGuardLessOrEqualsToTestContextStringByteByte() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testIsEqualsByteArrayByteArray() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGuardEqualsTestContextStringByteArrayByteArray() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGuardNotEqualsTestContextStringByteArrayByteArray() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testIsEqualsIntInt() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testIsEqualsIntIntInt() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGuardEqualsTestContextStringIntInt() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGuardEqualsTestContextStringIntIntInt() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGuardNotEqualsTestContextStringIntInt() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGuardGreaterThanTestContextStringIntInt() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGuardGreaterOrEqualsToTestContextStringIntInt() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGuardLessThanTestContextStringIntInt() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGuardLessOrEqualsToTestContextStringIntInt() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testIsEqualsLongLong() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testIsEqualsLongLongLong() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGuardEqualsTestContextStringLongLong() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGuardEqualsTestContextStringLongLongLong() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGuardNotEqualsTestContextStringLongLong() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGuardGreaterThanTestContextStringLongLong() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGuardGreaterOrEquals() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGuardLessThanTestContextStringLongLong() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGuardLessOrEqualsToTestContextStringLongLong() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGuardEqualsTestContextExceptionString() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGuardWrongFlow() {
//		fail("Not yet implemented");
//	}

}
