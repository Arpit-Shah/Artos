package test.com.artos.utils;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.nio.BufferUnderflowException;
import java.nio.ByteOrder;

import org.junit.Test;

import com.artos.utils.Transform;

public class TestTransform {
	Transform _tfm = new Transform();

	@Test
	public void testConcatByteArray_ArrayWithArray() {
		byte[] test1 = new byte[] { 0, 1, 2, 3, 4 };
		byte[] test2 = new byte[] { 5, 6, 7, 8 };
		byte[] test3 = new byte[] { 9 };
		byte[] test4 = new byte[] { 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, (byte) 255 };
		byte[] expectedResult = new byte[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, (byte) 255 };
		byte[] resultArray = _tfm.concat(test1, test2, test3, test4);
		assertArrayEquals(expectedResult, resultArray);
	}

	@Test
	public void testConcatByteArray_ByteWithByte() {
		byte test5 = 1;
		byte test6 = 10;
		byte test7 = (byte) 255;
		byte test8 = 0;
		byte[] expectedResult = new byte[] { 1, 10, (byte) 255, 0 };
		byte[] resultArray = _tfm.concat(test5, test6, test7, test8);
		assertArrayEquals(expectedResult, resultArray);
	}

	@Test
	public void testConcatByteArray_ArrayWithByte() {
		byte[] test9 = { 0, 1, 2, 10, (byte) 255 };
		byte test10 = 10;
		byte test11 = (byte) 255;
		byte test12 = 0;
		byte[] expectedResult = new byte[] { 0, 1, 2, 10, (byte) 255, 10, (byte) 255, 0 };
		byte[] resultArray = _tfm.concat(test9, test10, test11, test12);
		assertArrayEquals(expectedResult, resultArray);
	}

	@Test
	public void testConcatByteArray_ByteWithArray() {
		byte test9 = 1;
		byte[] test10 = new byte[] { 10 };
		byte[] test11 = new byte[] { 1, 10, (byte) 255, 0 };
		byte[] expectedResult = new byte[] { 1, 10, 1, 10, (byte) 255, 0 };
		byte[] resultArray = _tfm.concat(test9, test10, test11);
		assertArrayEquals(expectedResult, resultArray);
	}

	@Test
	public void testBytesToHexString_Array() {
		byte[] test1 = { 0, 1, 2, 10, (byte) 255 };
		String expectedResult1 = "[5][00 01 02 0A FF]";
		String resultArray1 = _tfm.bytesToHexString(test1, true);
		assertEquals(expectedResult1, resultArray1);

		byte[] test2 = { 0, 1, 2, 10, (byte) 255 };
		String expectedResult2 = "0001020AFF";
		String resultArray2 = _tfm.bytesToHexString(test2, false);
		assertEquals(expectedResult2, resultArray2);
	}

	@Test
	public void testBytesToHexString_SingleByte() {
		byte test3 = (byte) 255;
		String expectedResult3 = "[1][FF]";
		String resultArray3 = _tfm.bytesToHexString(test3, true);
		assertEquals(expectedResult3, resultArray3);

		byte test4 = (byte) 255;
		String expectedResult4 = "FF";
		String resultArray4 = _tfm.bytesToHexString(test4, false);
		assertEquals(expectedResult4, resultArray4);
	}

	@Test
	public void testBytesToAscii_ByteArray() throws Exception {
		byte[] test1 = { (byte) 0x54, (byte) 0x45, (byte) 0x53, (byte) 0x54 };
		String expectedResult1 = "TEST";
		String resultArray1 = _tfm.bytesToAscii(test1);
		assertEquals(expectedResult1, resultArray1);
	}

	@Test
	public void testBytesToAscii_SingleByte() throws Exception {
		byte test2 = (byte) 0x54;
		String expectedResult2 = "T";
		String resultArray2 = _tfm.bytesToAscii(test2);
		assertEquals(expectedResult2, resultArray2);
	}

	@Test
	public void testBytesToLong_ByteArray_DifferentByteOrder() {
		byte[] test1 = _tfm.strHexToByteArray("0D E0 B6 B3 A7 63 FF FF");
		long expectedResult1 = 999999999999999999l;
		long resultArray1 = _tfm.bytesToLong(test1, ByteOrder.BIG_ENDIAN);
		assertEquals(expectedResult1, resultArray1);

		byte[] test2 = _tfm.strHexToByteArray("FF FF 63 A7 B3 B6 E0 0D");
		long expectedResult2 = 999999999999999999l;
		long resultArray2 = _tfm.bytesToLong(test2, ByteOrder.LITTLE_ENDIAN);
		assertEquals(expectedResult2, resultArray2);
	}

	@Test
	public void testBytesToLong_BadPath_Overflow() {
		// bad path with extra byte, should only take first 8 bytes into
		// consideration
		byte[] test2 = _tfm.strHexToByteArray("FF FF 63 A7 B3 B6 E0 0D 01");
		long expectedResult3 = 999999999999999999l;
		long resultArray3 = _tfm.bytesToLong(test2, ByteOrder.LITTLE_ENDIAN);
		assertEquals(expectedResult3, resultArray3);
	}

	@Test(expected = BufferUnderflowException.class)
	public void testBytesToLong_BadPath_Underflow() {
		// bad path with value smaller than 8 bytes
		byte[] test2 = _tfm.strHexToByteArray("FF FF FF FF FF FF FF");
		_tfm.bytesToLong(test2, ByteOrder.LITTLE_ENDIAN);
	}

	@Test
	public void testBytesToLong_BadPath_OutofBound() {
		// bad path with value larger than 9,99,99,99,99,99,99,99,999
		byte[] test2 = _tfm.strHexToByteArray("FF FF FF FF FF FF FF FF");
		long expectedResult4 = -1;
		long resultArray4 = _tfm.bytesToLong(test2, ByteOrder.LITTLE_ENDIAN);
		assertEquals(expectedResult4, resultArray4);
	}

	@Test
	public void testBytesToDecimals() {
		byte[] test1 = _tfm.strHexToByteArray("0D E0 B6 B3 A7 63 FF FF");
		long expectedResult1 = 999999999999999999l;
		long resultArray1 = _tfm.bytesToDecimals(test1);
		assertEquals(expectedResult1, resultArray1);

		byte[] test2 = _tfm.strHexToByteArray("10");
		long expectedResult2 = 16;
		long resultArray2 = _tfm.bytesToDecimals(test2);
		assertEquals(expectedResult2, resultArray2);

		byte[] test3 = _tfm.strHexToByteArray("00 00 00 10");
		long expectedResult3 = 16;
		long resultArray3 = _tfm.bytesToDecimals(test3);
		assertEquals(expectedResult3, resultArray3);
	}

	@Test
	public void testBytesToDecimals_DifferentByteOrder() {
		byte[] test1 = _tfm.strHexToByteArray("0D E0 B6 B3 A7 63 FF FF");
		long expectedResult1 = 999999999999999999l;
		long resultArray1 = _tfm.bytesToDecimals(test1, ByteOrder.BIG_ENDIAN);
		assertEquals(expectedResult1, resultArray1);
		
		byte[] test11 = _tfm.strHexToByteArray("FF FF 63 A7 B3 B6 E0 0D");
		long expectedResult11 = 999999999999999999l;
		long resultArray11 = _tfm.bytesToDecimals(test11, ByteOrder.LITTLE_ENDIAN);
		assertEquals(expectedResult11, resultArray11);

		byte[] test2 = _tfm.strHexToByteArray("10");
		long expectedResult2 = 16;
		long resultArray2 = _tfm.bytesToDecimals(test2, ByteOrder.BIG_ENDIAN);
		assertEquals(expectedResult2, resultArray2);
		
		byte[] test22 = _tfm.strHexToByteArray("10");
		long expectedResult22 = 16;
		long resultArray22 = _tfm.bytesToDecimals(test22, ByteOrder.LITTLE_ENDIAN);
		assertEquals(expectedResult22, resultArray22);

		byte[] test3 = _tfm.strHexToByteArray("00 00 00 10");
		long expectedResult3 = 16;
		long resultArray3 = _tfm.bytesToDecimals(test3, ByteOrder.BIG_ENDIAN);
		assertEquals(expectedResult3, resultArray3);
		
		byte[] test33 = _tfm.strHexToByteArray("10 00 00 00");
		long expectedResult33 = 16;
		long resultArray33 = _tfm.bytesToDecimals(test33, ByteOrder.LITTLE_ENDIAN);
		assertEquals(expectedResult33, resultArray33);
	}

	@Test
	public void testBytesToInteger_ByteArray_DifferentByteOrder() {
		byte[] test1 = _tfm.strHexToByteArray("7F FF FF FF");
		int expectedResult1 = 2147483647;
		int resultArray1 = _tfm.bytesToInteger(test1, ByteOrder.BIG_ENDIAN);
		assertEquals(expectedResult1, resultArray1);

		byte[] test2 = _tfm.strHexToByteArray("FF FF FF 7F");
		int expectedResult2 = 2147483647;
		int resultArray2 = _tfm.bytesToInteger(test2, ByteOrder.LITTLE_ENDIAN);
		assertEquals(expectedResult2, resultArray2);
	}

	@Test
	public void testBytesToInteger_BadPath_Overflow() {
		// bad path with extra byte, should only take first 4 bytes into
		// consideration
		byte[] test2 = _tfm.strHexToByteArray("FF FF FF 7F 01");
		int expectedResult3 = 2147483647;
		int resultArray3 = _tfm.bytesToInteger(test2, ByteOrder.LITTLE_ENDIAN);
		assertEquals(expectedResult3, resultArray3);
	}

	@Test(expected = BufferUnderflowException.class)
	public void testBytesToInteger_BadPath_Underflow() {
		// bad path with value smaller than 4 bytes
		byte[] test2 = _tfm.strHexToByteArray("FF FF FF");
		_tfm.bytesToInteger(test2, ByteOrder.LITTLE_ENDIAN);
	}

	@Test
	public void testBytes2ToInt_ByteArray_DifferentByteOrder() {
		byte[] test1 = _tfm.strHexToByteArray("7F FF");
		int expectedResult1 = 32767;
		int resultArray1 = _tfm.bytes2ToInt(test1, ByteOrder.BIG_ENDIAN);
		assertEquals(expectedResult1, resultArray1);

		byte[] test2 = _tfm.strHexToByteArray("FF 7F");
		int expectedResult2 = 32767;
		int resultArray2 = _tfm.bytes2ToInt(test2, ByteOrder.LITTLE_ENDIAN);
		assertEquals(expectedResult2, resultArray2);
	}

	@Test
	public void testBytes2ToInt_BadPath_Overflow() {
		// bad path with extra byte, should only take first 4 bytes into
		// consideration
		byte[] test2 = _tfm.strHexToByteArray("FF 7F 01");
		int expectedResult3 = 32767;
		int resultArray3 = _tfm.bytes2ToInt(test2, ByteOrder.LITTLE_ENDIAN);
		assertEquals(expectedResult3, resultArray3);
	}

	@Test(expected = BufferUnderflowException.class)
	public void testBytes2ToInt_BadPath_Underflow() {
		// bad path with value smaller than 4 bytes
		byte[] test2 = _tfm.strHexToByteArray("FF");
		_tfm.bytes2ToInt(test2, ByteOrder.LITTLE_ENDIAN);
	}

	// @Test
	// public void testBytesToShort() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGenerateRandomBytes() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testBytesReverseOrder() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testListToByteArray() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGetLowNibble() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGetHighNibble() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testSetBitOfTheByte() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testClearBitOfTheByte() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testToogleBitOfTheByte() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGetBitOfTheByte() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testIsBitSet() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testStrToByteArray() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testStrHexToByte() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testStrHexToByteArrayString() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testStrHexToByteArrayStringBoolean() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testStrAsciiToByteArray() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testStrToInt() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testStrHexToAscii() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testAsciiToHexString() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testStrEscapeForXML() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testLongTo8Bytes() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testIsInteger() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testIntToByteArrayIntByteOrder() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testIntToByteArrayInt() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testIntToByte() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testIntToString() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testIntToFixLengthString() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testBooleanToInt() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testBooleanToStr() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testBooleanToStringBoolean() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testStringToBoolean() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testMilliSecondsToFormattedDate() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGetCurrentEPOCHTime() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGetEPOCHTimeDate() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGetEPOCHTimeString() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGetDateFromEPOCHTime() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGetCurrentTimeZone() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testRandInt() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testRandLong() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testRandChar() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testRandString() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testRandBytes() {
	// fail("Not yet implemented");
	// }

}
