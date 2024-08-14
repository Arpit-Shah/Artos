package test.com.artos.utils;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.nio.BufferUnderflowException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
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

		String resultArray3 = _tfm.bytesToHexString(test2);
		assertEquals(expectedResult2, resultArray3);
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

		String resultArray5 = _tfm.bytesToHexString(test4);
		assertEquals(expectedResult4, resultArray5);
	}

	@Test(expected = NullPointerException.class)
	public void testBytesToHexString_Null() {
		String resultArray2 = _tfm.bytesToHexString(null);
		assertEquals(null, resultArray2);
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

	@Test(expected = NullPointerException.class)
	public void testBytesToAscii_Null() throws Exception {
		String resultArray2 = _tfm.bytesToAscii(null);
		assertEquals(null, resultArray2);
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

		resultArray2 = _tfm.bytesToLong(test2);
		assertEquals(expectedResult2, resultArray2);

		byte[] test3 = _tfm.strHexToByteArray("00 00 00 00 00 00 00 04");
		long expectedResult3 = 4l;
		long resultArray3 = _tfm.bytesToLong(test3, ByteOrder.BIG_ENDIAN);
		assertEquals(expectedResult3, resultArray3);

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
	@SuppressWarnings("deprecation")
	public void testBytes2ToInt_ByteArray_DifferentByteOrder() {
		byte[] test1 = _tfm.strHexToByteArray("7F FF");
		int expectedResult1 = 32767;
		
		int resultArray1 = _tfm.bytes2ToInt(test1, ByteOrder.BIG_ENDIAN);
		assertEquals(expectedResult1, resultArray1);

		byte[] test2 = _tfm.strHexToByteArray("FF 7F");
		int expectedResult2 = 32767;
		int resultArray2 = _tfm.bytes2ToInt(test2, ByteOrder.LITTLE_ENDIAN);
		assertEquals(expectedResult2, resultArray2);

		byte[] test3 = _tfm.strHexToByteArray("02 00");
		int expectedResult3 = 2;
		int resultArray3 = _tfm.bytes2ToInt(test3, ByteOrder.LITTLE_ENDIAN);
		assertEquals(expectedResult3, resultArray3);
	}

	@Test
	@SuppressWarnings("deprecation")
	public void testBytes2ToInt_BadPath_Overflow() {
		// bad path with extra byte, should only take first 4 bytes into
		// consideration
		byte[] test2 = _tfm.strHexToByteArray("FF 7F 01");
		int expectedResult3 = 32767;
		int resultArray3 = _tfm.bytes2ToInt(test2, ByteOrder.LITTLE_ENDIAN);
		assertEquals(expectedResult3, resultArray3);
	}

	@Test(expected = BufferUnderflowException.class)
	@SuppressWarnings("deprecation")
	public void testBytes2ToInt_BadPath_Underflow() {
		// bad path with value smaller than 4 bytes
		byte[] test2 = _tfm.strHexToByteArray("FF");
		_tfm.bytes2ToInt(test2, ByteOrder.LITTLE_ENDIAN);
	}

	@Test
	public void testBytesShort_ByteArray_DifferentByteOrder() {
		byte[] test1 = _tfm.strHexToByteArray("7F FF");
		short expectedResult1 = 32767;
		short resultArray1 = _tfm.bytesToShort(test1, ByteOrder.BIG_ENDIAN);
		assertEquals(expectedResult1, resultArray1);

		byte[] test2 = _tfm.strHexToByteArray("FF 7F");
		short expectedResult2 = 32767;
		short resultArray2 = _tfm.bytesToShort(test2, ByteOrder.LITTLE_ENDIAN);
		assertEquals(expectedResult2, resultArray2);
	}

	@Test
	public void testBytesToShort_BadPath_Overflow() {
		// bad path with extra byte, should only take first 4 bytes into
		// consideration
		byte[] test2 = _tfm.strHexToByteArray("FF 7F 01");
		short expectedResult3 = 32767;
		short resultArray3 = _tfm.bytesToShort(test2, ByteOrder.LITTLE_ENDIAN);
		assertEquals(expectedResult3, resultArray3);
	}

	@Test(expected = BufferUnderflowException.class)
	public void testBytes2ToShort_BadPath_Underflow() {
		// bad path with value smaller than 4 bytes
		byte[] test2 = _tfm.strHexToByteArray("FF");
		_tfm.bytesToShort(test2, ByteOrder.LITTLE_ENDIAN);
	}

	@Test
	public void testGenerateRandomBytes() {
		byte[] random1 = _tfm.generateRandomBytes(Short.MAX_VALUE);
		byte[] random2 = _tfm.generateRandomBytes(Short.MAX_VALUE);
		assertEquals(Short.MAX_VALUE, random1.length);
		assertEquals(Short.MAX_VALUE, random2.length);
		Assert.assertNotEquals(random1, random2);
	}

	@Test
	public void testBytesReverseOrder() {
		byte[] test1 = _tfm.strHexToByteArray("7F FF FF FF");
		byte[] reversed = _tfm.bytesReverseOrder(test1);
		byte[] expected = _tfm.strHexToByteArray("FF FF FF 7F");
		assertArrayEquals(expected, reversed);
	}

	@Test
	public void testListToByteArray() {
		List<Byte> list = new ArrayList<>();
		list.add((byte) 0x00);
		list.add((byte) 0x01);
		list.add((byte) 0x02);
		list.add((byte) 0x04);
		byte[] array = _tfm.listToByteArray(list);
		byte[] expected = _tfm.strHexToByteArray("00 01 02 04");
		assertArrayEquals(expected, array);
	}

	@Test
	public void testGetLowNibble() {
		byte returned = _tfm.getLowNibble((byte) 0x01);
		assertEquals((byte) 0x01, returned);

		byte returned2 = _tfm.getLowNibble((byte) 0x3C);
		assertEquals((byte) 0x0C, returned2);

		byte returned3 = _tfm.getLowNibble((byte) 0xFF);
		assertEquals((byte) 0x0F, returned3);
	}

	@Test
	public void testGetHighNibble() {
		byte returned = _tfm.getHighNibble((byte) 0x01);
		assertEquals((byte) 0x00, returned);

		byte returned2 = _tfm.getHighNibble((byte) 0x3C);
		assertEquals((byte) 0x03, returned2);

		byte returned3 = _tfm.getHighNibble((byte) 0xFF);
		assertEquals((byte) 0x0F, returned3);
	}

	@Test
	public void testSetBitOfTheByte() {
		byte test1 = (byte) 0x00;
		test1 = _tfm.setBitOfTheByte(test1, 0);
		assertEquals((byte) 0x01, test1);
		test1 = _tfm.setBitOfTheByte(test1, 1);
		assertEquals((byte) 0x03, test1);
		test1 = _tfm.setBitOfTheByte(test1, 2);
		assertEquals((byte) 0x07, test1);
		test1 = _tfm.setBitOfTheByte(test1, 3);
		assertEquals((byte) 0x0F, test1);
		test1 = _tfm.setBitOfTheByte(test1, 4);
		assertEquals((byte) 0x1F, test1);
		test1 = _tfm.setBitOfTheByte(test1, 5);
		assertEquals((byte) 0x3F, test1);
		test1 = _tfm.setBitOfTheByte(test1, 6);
		assertEquals((byte) 0x7F, test1);
		test1 = _tfm.setBitOfTheByte(test1, 7);
		assertEquals((byte) 0xFF, test1);
	}

	@Test
	public void testClearBitOfTheByte() {
		byte test1 = (byte) 0xFF;
		test1 = _tfm.clearBitOfTheByte(test1, 7);
		assertEquals((byte) 0x7F, test1);
		test1 = _tfm.clearBitOfTheByte(test1, 6);
		assertEquals((byte) 0x3F, test1);
		test1 = _tfm.clearBitOfTheByte(test1, 5);
		assertEquals((byte) 0x1F, test1);
		test1 = _tfm.clearBitOfTheByte(test1, 4);
		assertEquals((byte) 0x0F, test1);
		test1 = _tfm.clearBitOfTheByte(test1, 3);
		assertEquals((byte) 0x07, test1);
		test1 = _tfm.clearBitOfTheByte(test1, 2);
		assertEquals((byte) 0x03, test1);
		test1 = _tfm.clearBitOfTheByte(test1, 1);
		assertEquals((byte) 0x01, test1);
		test1 = _tfm.clearBitOfTheByte(test1, 0);
		assertEquals((byte) 0x00, test1);
	}

	@Test
	public void testToogleBitOfTheByte() {
		byte test1 = (byte) 0xFF;
		test1 = _tfm.toogleBitOfTheByte(test1, 0);
		test1 = _tfm.toogleBitOfTheByte(test1, 1);
		test1 = _tfm.toogleBitOfTheByte(test1, 2);
		test1 = _tfm.toogleBitOfTheByte(test1, 3);
		test1 = _tfm.toogleBitOfTheByte(test1, 4);
		test1 = _tfm.toogleBitOfTheByte(test1, 5);
		test1 = _tfm.toogleBitOfTheByte(test1, 6);
		test1 = _tfm.toogleBitOfTheByte(test1, 7);
		assertEquals((byte) 0x00, test1);

	}

	@Test
	public void testGetBitOfTheByte() {
		byte test1 = (byte) 0xFF;
		assertEquals(1, _tfm.getBitOfTheByte(test1, 0));
		assertEquals(1, _tfm.getBitOfTheByte(test1, 1));
		assertEquals(1, _tfm.getBitOfTheByte(test1, 2));
		assertEquals(1, _tfm.getBitOfTheByte(test1, 3));
		assertEquals(1, _tfm.getBitOfTheByte(test1, 4));
		assertEquals(1, _tfm.getBitOfTheByte(test1, 5));
		assertEquals(1, _tfm.getBitOfTheByte(test1, 6));
		assertEquals(1, _tfm.getBitOfTheByte(test1, 7));

		test1 = (byte) 0x00;
		assertEquals(0, _tfm.getBitOfTheByte(test1, 0));
		assertEquals(0, _tfm.getBitOfTheByte(test1, 1));
		assertEquals(0, _tfm.getBitOfTheByte(test1, 2));
		assertEquals(0, _tfm.getBitOfTheByte(test1, 3));
		assertEquals(0, _tfm.getBitOfTheByte(test1, 4));
		assertEquals(0, _tfm.getBitOfTheByte(test1, 5));
		assertEquals(0, _tfm.getBitOfTheByte(test1, 6));
		assertEquals(0, _tfm.getBitOfTheByte(test1, 7));
	}

	@Test
	public void testIsBitSet() {
		byte test1 = (byte) 0xFF;
		assertEquals(true, _tfm.isBitSet(test1, 0));
		assertEquals(true, _tfm.isBitSet(test1, 1));
		assertEquals(true, _tfm.isBitSet(test1, 2));
		assertEquals(true, _tfm.isBitSet(test1, 3));
		assertEquals(true, _tfm.isBitSet(test1, 4));
		assertEquals(true, _tfm.isBitSet(test1, 5));
		assertEquals(true, _tfm.isBitSet(test1, 6));
		assertEquals(true, _tfm.isBitSet(test1, 7));

		test1 = (byte) 0x00;
		assertEquals(false, _tfm.isBitSet(test1, 0));
		assertEquals(false, _tfm.isBitSet(test1, 1));
		assertEquals(false, _tfm.isBitSet(test1, 2));
		assertEquals(false, _tfm.isBitSet(test1, 3));
		assertEquals(false, _tfm.isBitSet(test1, 4));
		assertEquals(false, _tfm.isBitSet(test1, 5));
		assertEquals(false, _tfm.isBitSet(test1, 6));
		assertEquals(false, _tfm.isBitSet(test1, 7));
	}

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
