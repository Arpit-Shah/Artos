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

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.artos.framework.Enums.ExceptionValue;

public class Transform {

	static final String HEXES = "0123456789ABCDEF";
	static final ByteOrder BYTE_ORDER_DEFAULT = ByteOrder.LITTLE_ENDIAN;
	// ===================================================================
	// Bytes related manipulation
	// ===================================================================

	/**
	 * concatenates multiple byte arrays.
	 * 
	 * @param arrays = byte arrays
	 * @return concatenated byte array
	 */
	public byte[] concat(byte[]... arrays) {
		int totalLength = 0;
		for (byte[] array : arrays) {
			totalLength += array.length;
		}
		byte[] result = new byte[totalLength];
		int offset = 0;
		for (byte[] array : arrays) {
			System.arraycopy(array, 0, result, offset, array.length);
			offset += array.length;
		}
		return result;
	}

	/**
	 * concatenate chain of single byte in order it was provided in.
	 * 
	 * @param data = byte(s) in sequence
	 * @return concatenated byte array
	 */
	@SuppressWarnings("unused")
	public byte[] concat(byte... data) {
		int totalLength = 0;
		for (byte currentByte : data) {
			totalLength += 1;
		}
		byte[] result = new byte[totalLength];
		int offset = 0;
		for (byte currentByte : data) {
			result[offset] = currentByte;
			offset++;
		}
		return result;
	}

	/**
	 * concatenates byte array with subsequent single byte in order it was provided in.
	 * 
	 * @param byteArray = first byte array
	 * @param rest = following byte array
	 * @return = concatenated byte array
	 */
	@SuppressWarnings("unused")
	public byte[] concat(byte[] byteArray, byte... rest) {
		int totalLength = byteArray.length;
		for (byte currentByte : rest) {
			totalLength += 1;
		}
		byte[] result = Arrays.copyOf(byteArray, totalLength);
		int offset = byteArray.length;
		for (byte currentByte : rest) {
			result[offset] = currentByte;
			offset++;
		}
		return result;
	}

	/**
	 * concatenates byte with subsequent byte arrays in order it was provided in.
	 * 
	 * @param data = first byte
	 * @param rest = following byte array
	 * @return = concatenated byte array
	 */
	public byte[] concat(byte data, byte[]... rest) {
		int totalLength = 1;
		for (byte[] array : rest) {
			totalLength += array.length;
		}
		byte[] result = new byte[totalLength];
		result[0] = data;
		int offset = 1;
		for (byte[] array : rest) {
			System.arraycopy(array, 0, result, offset, array.length);
			offset += array.length;
		}
		return result;
	}

	/**
	 * Converts Byte to Hex String
	 * 
	 * <PRE>
	 * Example: 
	 * Sample : bytesToHexString((byte)0xFF);
	 * Result : FF
	 * </PRE>
	 * 
	 * @param data = data to be converted
	 * @return Hex formatted string
	 */
	public String bytesToHexString(byte data) {
		return bytesToHexString(data, false);
	}

	/**
	 * Converts Bytes to Hex String
	 * 
	 * <PRE>
	 * Example: 
	 * Sample : bytesToHexString(new byte[]{0x01, 0x02, 0xFF});
	 * Result : 0102FF
	 * </PRE>
	 * 
	 * @param data = data to be converted
	 * @return Hex formatted string
	 */
	public String bytesToHexString(byte[] data) {
		return bytesToHexString(data, false);
	}

	/**
	 * Converts Bytes to Hex String
	 * 
	 * <PRE>
	 * Example: 
	 * Sample : bytesToHexString(new byte[]{0x01, 0x02, 0xFF}, true);
	 * Result : [3][01 02 FF]
	 * Sample : bytesToHexString(new byte[]{0x01, 0x02, 0xFF}, false);
	 * Result : 0102FF
	 * </PRE>
	 * 
	 * @param data = data to be converted
	 * @param bDisplaySize = true/false
	 * @return Hex formatted string
	 */
	public String bytesToHexString(byte[] data, boolean bDisplaySize) {
		if (null == data) {
			throw new NullPointerException();
		}
		StringBuilder hex = new StringBuilder();
		if (bDisplaySize) {
			hex.append("[" + data.length + "][");
		}

		int count = 1;
		for (byte b : data) {
			hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(HEXES.charAt((b & 0x0F)));
			if (bDisplaySize && count < data.length) {
				hex.append(" ");
			}
			count++;
		}
		if (bDisplaySize) {
			hex.append("]");
		}
		return hex.toString();
	}

	/**
	 * Converts Byte to Hex String
	 * 
	 * <PRE>
	 * Example: 
	 * Sample : bytesToHexString((byte)0xFF, true);
	 * Result : [1][FF]
	 * Sample : bytesToHexString((byte)0xFF, false);
	 * Result : FF
	 * </PRE>
	 * 
	 * @param data = data to be converted
	 * @param bDisplaySize = true/false
	 * @return Hex formatted string
	 */
	public String bytesToHexString(byte data, boolean bDisplaySize) {
		return bytesToHexString(new byte[] { data }, bDisplaySize);
	}

	/**
	 * <PRE>
	 * Sample : bytesToAscii(new byte[]{(byte)0x54,(byte)0x45,(byte)0x53,(byte)0x54});
	 * Answer : TEST
	 * </PRE>
	 * 
	 * @param data = data to be converted
	 * @return = Ascii formatted String
	 * @throws UnsupportedEncodingException Exception is thrown if invalid input is provided
	 */
	public String bytesToAscii(byte[] data) throws UnsupportedEncodingException {
		return new String(data, "UTF-8");
	}

	/**
	 * <PRE>
	 * Sample : bytesToAscii((byte)0x54);
	 * Answer : T
	 * </PRE>
	 * 
	 * @param data = data to be converted
	 * @return = Ascii formatted String
	 * @throws Exception = Exception is returned if invalid input data is provided
	 */
	public String bytesToAscii(byte data) throws Exception {
		return bytesToAscii(new byte[] { data });
	}

	/**
	 * 
	 * @param byteArray Reads the first eight bytes, composing them into a long value according to the byte order
	 * @return long value
	 */
	public long bytesToLong(byte[] byteArray) {
		return bytesToLong(byteArray, BYTE_ORDER_DEFAULT);
	}

	/**
	 * <pre>
	 * Sample : bytesToLong(new byte[]{0D, E0, B6, B3, A7, 63, FF, FF}, ByteOrder.BIG_ENDIAN);
	 * Answer : 999999999999999999
	 * </pre>
	 * 
	 * @param bytes = data to be converted
	 * @param bo byte order before converting it to long
	 * @return = long formatted data
	 */
	public long bytesToLong(byte[] bytes, ByteOrder bo) {
		ByteBuffer buffer = ByteBuffer.wrap(bytes);
		buffer.order(bo);
		return buffer.getLong();
	}

	/**
	 * <pre>
	 * Sample : bytesToDecimals(new byte[]{00, 00, 00, 03});
	 * Answer : 3
	 * </pre>
	 * 
	 * @param bytes = data to be converted
	 * @return = long formatted data
	 */
	public long bytesToDecimals(byte[] bytes) {
		return bytesToDecimals(bytes, ByteOrder.BIG_ENDIAN);
	}

	/**
	 * <pre>
	 * Sample : bytesToDecimals(new byte[]{00, 00, 00, 03}, ByteOrder.BIG_ENDIAN);
	 * Sample : bytesToDecimals(new byte[]{03, 00, 00, 00}, ByteOrder.LITTLE_ENDIAN);
	 * Answer : 3
	 * </pre>
	 * 
	 * @param bytes = data to be converted
	 * @param bo byte order before converting it to long
	 * @return = long formatted data
	 */
	public long bytesToDecimals(byte[] bytes, ByteOrder bo) {
		int size = 8;
		byte[] temp = new byte[size];
		Arrays.fill(temp, (byte) 0x00);

		if (bo == ByteOrder.BIG_ENDIAN) {
			for (int i = bytes.length - 1; i >= 0; i--) {
				temp[size - 1] = bytes[i];
				size--;
			}
		} else {
			for (int i = 0; i < bytes.length; i++) {
				temp[i] = bytes[i];
			}
		}
		return bytesToLong(temp, bo);
	}

	/**
	 * <pre>
	 * Sample : bytesToInteger(new byte[]{00, 00, 00, 03}, ByteOrder.BIG_ENDIAN);
	 * Answer : 3
	 * </pre>
	 * 
	 * @param bytes = data to be converted
	 * @param bo byte order before converting it to long
	 * @return = integer formatted data
	 */
	public int bytesToInteger(byte[] bytes, ByteOrder bo) {
		ByteBuffer buffer = ByteBuffer.wrap(bytes);
		buffer.order(bo);
		return buffer.getInt();
	}

	/**
	 * @deprecated we recommend to use {@link #bytesToShort(byte[], ByteOrder)} instead
	 * @param bytes Byte array
	 * @param bo Byte order
	 * @return Integer value
	 */
	@Deprecated
	public int bytes2ToInt(byte[] bytes, ByteOrder bo) {
		return bytesToShort(bytes, bo);
	}

	/**
	 * 
	 * @param bytes Byte array
	 * @param bo Byte order
	 * @return Short value
	 */
	public short bytesToShort(byte[] bytes, ByteOrder bo) {
		ByteBuffer buffer = ByteBuffer.wrap(bytes);
		buffer.order(bo);
		return buffer.getShort();
	}

	/**
	 * Generates requested number of random bytes. Uses SecureRandom function
	 * 
	 * @param numberOfBytes Number of bytes
	 * @return random number byte array
	 */
	public byte[] generateRandomBytes(int numberOfBytes) {
		SecureRandom random = new SecureRandom();
		byte[] bytes = new byte[numberOfBytes];
		random.nextBytes(bytes);

		return bytes;
	}

	/**
	 * Reverse order of bytes
	 * 
	 * @param data Byte array
	 * @return reversed byte array
	 */
	public byte[] bytesReverseOrder(byte[] data) {
		byte[] reverseData = new byte[data.length];

		int j = data.length - 1;
		for (int i = 0; i < data.length; i++) {
			reverseData[j] = data[i];
			j--;
		}
		return reverseData;
	}

	/**
	 * 
	 * @param list List of byte
	 * @return byte array
	 */
	public byte[] listToByteArray(List<Byte> list) {
		byte[] byteArray = new byte[list.size()];
		for (int i = 0; i < list.size(); i++) {
			byteArray[i] = list.get(i).byteValue();
		}
		return byteArray;
	}

	// ===================================================================
	// Bits related manipulation
	// ===================================================================

	/**
	 * Returns Low Nibble of the byte.
	 * 
	 * <PRE>
	 * Sample : getLowNibble((byte)0xFF)
	 * Answer : 0xF0
	 * </PRE>
	 * 
	 * @param data Byte value
	 * @return Low Nibble in byte format
	 */
	public byte getLowNibble(byte data) {
		byte lowNibble = (byte) (data & 0x0F);
		return lowNibble;
	}

	/**
	 * Returns High Nibble of the byte.
	 * 
	 * <PRE>
	 * Sample : getLowNibble((byte)0xFF)
	 * Answer : 0x0F
	 * </PRE>
	 * 
	 * @param data Byte Value
	 * @return High Nibble in byte format
	 */
	public byte getHighNibble(byte data) {
		byte lowNibble = (byte) ((data >> 4) & 0x0F);
		return lowNibble;
	}

	/**
	 * Sets Particular bit of the byte to high
	 * 
	 * @param data Byte Value
	 * @param pos Bit position
	 * @return Byte with set bit
	 */
	public byte setBitOfTheByte(byte data, int pos) {
		return (byte) (data | (1 << pos));
	}

	/**
	 * Sets Particular bit of the byte to low
	 * 
	 * @param data Byte Value
	 * @param pos Bit Position
	 * @return Byte with cleated bit
	 */
	public byte clearBitOfTheByte(byte data, int pos) {
		return (byte) (data & ~(1 << pos));
	}

	/**
	 * Toggles particular bit of the byte
	 * 
	 * @param data Byte value
	 * @param pos Bit position
	 * @return Byte with toogled bit
	 */
	public byte toogleBitOfTheByte(byte data, int pos) {
		return (byte) (data ^ (1 << pos));
	}

	/**
	 * Returns particular bit of the byte
	 * 
	 * @param data Byte value
	 * @param pos Bit Position
	 * @return Value of the Bit (1 or 0)
	 */
	public int getBitOfTheByte(byte data, int pos) {
		return 0 != (data & (0x01 << pos)) ? 1 : 0;
	}

	public boolean isBitSet(byte data, int pos) {
		if (0 != getBitOfTheByte(data, pos)) {
			return true;
		}
		return false;
	}

	// ===================================================================
	// String related manipulation
	// ===================================================================

	public byte[] strToByteArray(String str) {
		return str.getBytes();
	}

	public byte strHexToByte(String strHex) {
		if (strHex.length() != 2) {
			throw new IllegalArgumentException("Input string must only contain 2 char");
		}
		return (strHexToByteArray(strHex)[0]);
	}

	public byte[] strHexToByteArray(String strHex) {
		return strHexToByteArray(strHex, true);
	}

	public byte[] strHexToByteArray(String strHex, boolean removeWhiteSpaceChar) {

		String s = strHex;
		if (removeWhiteSpaceChar) {
			s = strHex.replaceAll("\\s", "");
		}

		if ((s.length() % 2) != 0) {
			throw new IllegalArgumentException("Input string must contain an even number of characters");
		}

		byte data[] = new byte[s.length() / 2];
		for (int i = 0; i < s.length(); i += 2) {
			data[i / 2] = (Integer.decode("0x" + s.charAt(i) + s.charAt(i + 1))).byteValue();
		}
		return data;
	}

	public byte[] strAsciiToByteArray(String strAscii) {
		return strAscii.getBytes();
	}

	public int strToInt(String string) {
		return Integer.parseInt(string);
	}

	/**
	 * Converts String Hex to ASCII
	 * 
	 * @param strHex Hex formatter string
	 * @return ASCII String
	 * @throws UnsupportedEncodingException Exception is thrown if invalid input is provided
	 */
	public String strHexToAscii(String strHex) throws UnsupportedEncodingException {

		if (strHex == null || strHex.length() % 2 != 0) {
			throw new RuntimeException("Invalid data, null or odd length");
		}
		int len = strHex.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(strHex.charAt(i), 16) << 4) + Character.digit(strHex.charAt(i + 1), 16));
		}
		return bytesToAscii(data);
	}

	/**
	 * 
	 * @param asciiValue ASCII string
	 * @return Hex String
	 */
	public String asciiToHexString(String asciiValue) {
		String result = "";
		byte[] asciiArray = asciiValue.getBytes();
		for (byte s : asciiArray) {
			result += String.format("%02x", s);
		}
		return result;
	}

	public String strEscapeForXML(String input) {
		StringBuilder builder = new StringBuilder();
		for (int index = 0; index < input.length(); index++) {
			char chr = input.charAt(index);
			if (chr == '<') {
				builder.append("&lt;");
			} else if (chr == '>') {
				builder.append("&gt;");
			} else if (chr == '&') {
				builder.append("&amp;");
			} else if (chr == '\'') {
				builder.append("&apos;");
			} else if (chr == '"') {
				builder.append("&quot;");
			} else {
				builder.append(chr);
			}
		}
		return builder.toString();
	}
	// ===================================================================
	// Long related manipulation
	// ===================================================================

	public byte[] longTo8Bytes(long x, ByteOrder bo) {
		ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
		buffer.order(bo);
		buffer.putLong(x);
		return buffer.array();
	}

	// ===================================================================
	// Integer related manipulation
	// ===================================================================

	public boolean isInteger(String str) {
		try {
			Integer.parseInt(str);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	public byte[] intToByteArray(int x, ByteOrder bo) {
		ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
		buffer.order(bo);
		buffer.putInt(x);
		return buffer.array();
	}

	public byte[] intToByteArray(int x) {
		return intToByteArray(x, BYTE_ORDER_DEFAULT);
	}

	public byte intToByte(int x) {
		return (byte) x;
	}

	public String intToString(int x) {
		return Integer.toString(x);
	}

	/**
	 * Returns fix length string regardless of integer value
	 * 
	 * @param nNumber Integer value
	 * @param nFixLenght String length
	 * @return Fix length string
	 */
	public String intToFixLengthString(int nNumber, int nFixLenght) {
		String numberAsString = String.format("%0" + nFixLenght + "d", nNumber);
		return numberAsString;
	}

	// ===================================================================
	// Boolean related manipulation
	// ===================================================================

	/**
	 * 
	 * @param b Boolean value
	 * @return integer representation of boolean value
	 */
	public int booleanToInt(boolean b) {
		return b ? 1 : 0;
	}

	public String booleanToStr(boolean b) {
		return b ? "1" : "0";
	}

	public String booleanToStringBoolean(boolean b) {
		return String.valueOf(b);
	}

	public boolean stringToBoolean(String str) throws Exception {
		if (str.trim().equals("1") || str.trim().toLowerCase().equals("true")) {
			return true;
		} else if (str.trim().equals("0") || str.trim().toLowerCase().equals("false")) {
			return false;
		}
		throw new Exception(ExceptionValue.INVALID_INPUT.getValue());
	}

	// *******************************************************************************************
	// Time related manipulation
	// *******************************************************************************************

	/**
	 * MilliSecondsToFormattedDate("dd-MM-yyyy hh:mm", System.milliseconds())
	 * 
	 * @param dateFormat the pattern describing the date and time format
	 * @param timeInMilliseconds EPoch time value
	 * @return date formatter with given pattern
	 */
	public String MilliSecondsToFormattedDate(String dateFormat, long timeInMilliseconds) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timeInMilliseconds);
		return simpleDateFormat.format(calendar.getTime());
	}

	public long getCurrentEPOCHTime() throws Exception {
		Date date = new Date();
		return getEPOCHTime(date);
	}

	public long getEPOCHTime(Date date) throws Exception {
		return date.getTime() / 1000;
	}

	@SuppressWarnings("deprecation")
	public long getEPOCHTime(String timeddMMyyyyHHmmss) throws Exception {
		// String str = "Jun 13 2003 23:11:52.454";
		SimpleDateFormat df = new SimpleDateFormat("ddMMyyyyHHmmss");
		Date date = df.parse(timeddMMyyyyHHmmss);

		// EPOCH returns time in milliseconds so divide by 1000
		// System.out.println("EPOCHTime :" +
		// ((date.getTime()-date.getTimezoneOffset()) / 1000));
		return ((date.getTime() - date.getTimezoneOffset()) / 1000);
	}

	public Calendar getDateFromEPOCHTime(long epochTime) throws Exception {
		Date date = new Date(epochTime);
		SimpleDateFormat format = new SimpleDateFormat("dd MM yyyy HH:mm:ss.SSS");
		String formattedDate = format.format(date);
		System.out.println("date :" + formattedDate);

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}

	public String getCurrentTimeZone() {
		TimeZone tz = Calendar.getInstance().getTimeZone();
		return tz.getID();
	}

	// ===================================================================
	// Other manipulation
	// ===================================================================

	/**
	 * Generate Random number between provided high and low value
	 * 
	 * @param low random number low boundary
	 * @param high random number high boundary
	 * @return random integer value between high and low boundary
	 */
	public int randInt(int low, int high) {
		SecureRandom r = new SecureRandom();
		int R = r.nextInt(high - low) + low;
		return R;
	}

	/**
	 * Generate Random long number
	 * 
	 * @return random long value
	 */
	public long randLong() {
		SecureRandom r = new SecureRandom();
		long R = r.nextLong();
		return R;
	}

	/**
	 * Generate Random char array
	 * 
	 * @param length Char array length
	 * @return random char array
	 */
	public char[] randChar(int length) {
		SecureRandom r = new SecureRandom();
		char[] chars = new char[length];

		for (int i = 0; i < length; i++) {
			chars[i] = (char) (r.nextInt(26) + 'a');
		}

		return chars;
	}

	/**
	 * Generate Random char String
	 * 
	 * @param length String length
	 * @return random character string
	 */
	public String randString(int length) {
		return String.valueOf(randChar(length));
	}

	/**
	 * Generate Random byte array
	 * 
	 * @param length Byte array length
	 * @return random value byte array
	 */
	public byte[] randBytes(int length) {
		byte[] b = new byte[length];
		SecureRandom r = new SecureRandom();
		r.nextBytes(b);

		return b;
	}

}
