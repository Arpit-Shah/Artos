package com.arpit.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class Convert {

	static final String HEXES = "0123456789ABCDEF";

	// ===================================================================
	// Bytes related manipulation
	// ===================================================================

	/**
	 * concatenates multiple byte arrays.
	 * 
	 * @param first
	 * @param rest
	 * @return concatenated byte array
	 */
	public byte[] concat(byte[] first, byte[]... rest) {
		int totalLength = first.length;
		for (byte[] array : rest) {
			totalLength += array.length;
		}
		byte[] result = Arrays.copyOf(first, totalLength);
		int offset = first.length;
		for (byte[] array : rest) {
			System.arraycopy(array, 0, result, offset, array.length);
			offset += array.length;
		}
		return result;
	}

	/**
	 * concatenate chain of single byte in order it was provided in.
	 * 
	 * @param first
	 * @param rest
	 * @return concatenated byte array
	 */
	@SuppressWarnings("unused")
	public byte[] concat(byte first, byte... rest) {
		int totalLength = 1;
		for (byte currentByte : rest) {
			totalLength += 1;
		}
		byte[] result = new byte[totalLength];
		result[0] = first;
		int offset = 1;
		for (byte currentByte : rest) {
			result[offset] = currentByte;
			offset++;
		}
		return result;
	}

	/**
	 * concatenates byte array with subsequent single byte in order it was
	 * provided in.
	 * 
	 * @param byteArray
	 * @param singleByte
	 * @return concatenated byte array
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
	 * Converts Bytes to Hex String
	 * 
	 * <PRE>
	 * Example: 
	 * Sample : bytesToStringHex(new byte[]{0x01, 0x02, 0xFF}, true);
	 * Result : [3][01 02 FF]
	 * Sample : bytesToStringHex(new byte[]{0x01, 0x02, 0xFF}, false);
	 * Result : 0102FF
	 * </PRE>
	 * 
	 * @param data
	 * @param bDisplaySize
	 *            = true/false
	 * @return Hex formatted string
	 */
	public String bytesToStringHex(byte[] data, boolean bDisplaySize) {
		if (null == data) {
			return null;
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
	 * Converts Bytes to Hex String
	 * 
	 * <PRE>
	 * Example: 
	 * Sample : bytesToStringHex((byte)0xFF, true);
	 * Result : [1][FF]
	 * Sample : bytesToStringHex((byte)0xFF, false);
	 * Result : FF
	 * </PRE>
	 * 
	 * @param data
	 * @param bDisplaySize
	 *            = true/false
	 * @return Hex formatted string
	 */
	public String bytesToStringHex(byte data, boolean bDisplaySize) {
		return bytesToStringHex(new byte[] { data }, bDisplaySize);
	}

	/**
	 * <PRE>
	 * Sample : bytesToAscii(new byte[]{(byte)0x54,(byte)0x45,(byte)0x53,(byte)0x54});
	 * Answer : TEST
	 * </PRE>
	 * 
	 * @param bytes
	 * @return
	 * @throws Exception
	 */
	public String bytesToAscii(byte[] data) throws Exception {
		return new String(data, "UTF-8");
	}

	/**
	 * <PRE>
	 * Sample : bytesToAscii((byte)0x54);
	 * Answer : T
	 * </PRE>
	 * 
	 * @param bytes
	 * @return
	 * @throws Exception
	 */
	public String bytesToAscii(byte data) throws Exception {
		return bytesToAscii(new byte[] { data });
	}

	/**
	 * <pre>
	 * Sample : bytesToLong(new byte[]{0D, E0, B6, B3, A7, 63, FF, FF}, ByteOrder.BIG_ENDIAN);
	 * Answer : 999999999999999999
	 * </pre>
	 * 
	 * @param bytes
	 * @param bo
	 *            byte order before converting it to long
	 * @return
	 */
	public long bytesToLong(byte[] bytes, ByteOrder bo) {
		ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
		buffer.order(bo);
		buffer.put(bytes);
		buffer.flip();// need flip
		return buffer.getLong();
	}

	/**
	 * <pre>
	 * Sample : bytesToDecimals(new byte[]{00, 00, 00, 03}, ByteOrder.BIG_ENDIAN);
	 * Answer : 3
	 * </pre>
	 * 
	 * @param bytes
	 * @param bo
	 *            byte order before converting it to long
	 * @return
	 */
	public int bytesToDecimals(byte[] bytes, ByteOrder bo) {
		return bytesToInteger(bytes, bo);
	}

	/**
	 * <pre>
	 * Sample : bytesToInteger(new byte[]{00, 00, 00, 03}, ByteOrder.BIG_ENDIAN);
	 * Answer : 3
	 * </pre>
	 * 
	 * @param bytes
	 * @param bo
	 *            byte order before converting it to long
	 * @return
	 */
	public int bytesToInteger(byte[] bytes, ByteOrder bo) {
		ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
		buffer.order(bo);
		buffer.put(bytes);
		buffer.flip();// need flip
		return buffer.getInt();
	}

	/**
	 * 
	 * @param bytes
	 * @param flipbytes
	 * @return
	 * @throws Exception
	 */
	public int bytes2ToInt(byte[] bytes, ByteOrder bo) throws Exception {
		if (bytes.length != 2) {
			throw new Exception("Bytes length is not as expected");
		}
		if (bo == ByteOrder.BIG_ENDIAN) {
			return ((bytes[0] & 0xff) << 8) | (bytes[1] & 0xff);
		} else {
			return ((bytes[1] & 0xff) << 8) | (bytes[0] & 0xff);
		}
	}

	/**
	 * Generates requested number of random bytes. Uses SecureRandom function
	 * 
	 * @param numberOfBytes
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
	 * @param data
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
	 * @param list
	 * @return
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
	 * @param data
	 * @return
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
	 * @param data
	 * @return
	 */
	public byte getHighNibble(byte data) {
		byte lowNibble = (byte) ((data >> 4) & 0x0F);
		return lowNibble;
	}

	/**
	 * Sets Particular bit of the byte to high
	 * 
	 * @param data
	 * @param pos
	 * @return
	 */
	public byte setBitOfTheByte(byte data, int pos) {
		return (byte) (data | (1 << pos));
	}

	/**
	 * Sets Particular bit of the byte to low
	 * 
	 * @param data
	 * @param pos
	 * @return
	 */
	public byte unSetBitOfTheByte(byte data, int pos) {
		return (byte) (data & ~(1 << pos));
	}

	/**
	 * Toggles particular bit of the byte
	 * 
	 * @param data
	 * @param pos
	 * @return
	 */
	public byte toogleBitOfTheByte(byte data, int pos) {
		return (byte) (data ^ (1 << pos));
	}

	/**
	 * Returns particular bit of the byte
	 * 
	 * @param data
	 * @param pos
	 * @return
	 */
	public byte getBitOfTheByte(byte data, int pos) {
		return (byte) (data & (0x01 << pos));
	}

	// ===================================================================
	// String related manipulation
	// ===================================================================

	public byte[] stringToByteArray(String str) {
		return str.getBytes();
	}

	public byte[] stringHexToByteArray(String strHex) {
		return stringHexToByteArray(strHex, true);
	}

	public byte[] stringHexToByteArray(String strHex, boolean removeWhiteSpaceChar) {

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

	public byte[] stringAsciiToByteArray(String strAscii) {
		return strAscii.getBytes();
	}

	public int stringToInteger(String string) {
		return Integer.parseInt(string);
	}

	/**
	 * Converts String Hex to ASCII
	 * 
	 * @param strHex
	 * @return
	 * @throws Exception
	 */
	public String stringHexToAscii(String strHex) throws Exception {

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
	 * @param asciiValue
	 * @return
	 */
	public String asciiToStringHex(String asciiValue) {
		String result = "";
		byte[] asciiArray = asciiValue.getBytes();
		for (byte s : asciiArray) {
			result += String.format("%02x", s);
		}
		return result;
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

	public byte[] intTo4Bytes(int x, ByteOrder bo) throws Exception {
		if (x > Integer.MAX_VALUE) {
			throw new Exception("Given value is not an integer");
		}
		ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
		buffer.order(bo);
		buffer.putInt(x);
		return buffer.array();
	}

	public byte[] intTo2Bytes(int x, ByteOrder bo) throws Exception {
		if (x > 65535) {
			throw new Exception("Given value can not fit in two bytes");
		}
		ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
		buffer.order(bo);
		buffer.putInt(x);
		byte[] bytes = buffer.array();
		return Arrays.copyOfRange(bytes, bytes.length - 2, bytes.length);
	}

	public String integerToString(int x) {
		return Integer.toString(x);
	}

	/**
	 * Returns fix length string regardless of integer value
	 * 
	 * @param nNumber
	 * @param nFixLenght
	 * @return
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
	 * @param b
	 * @return
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
		if (str.equals("0") || str.equals("false") || str.equals("FALSE") || str.equals("False")) {
			return false;
		} else if (str.equals("1") || str.equals("true") || str.equals("TRUE") || str.equals("True")) {
			return true;
		} else {
			throw new Exception("Invalid input : " + str);
		}
	}

	// *******************************************************************************************
	// Time related manipulation
	// *******************************************************************************************

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

	// ===================================================================
	// Other manipulation
	// ===================================================================

	/**
	 * Generate Random number between provided high and low value
	 * 
	 * @param low
	 * @param high
	 * @return
	 */
	public int randInt(int low, int high) {
		Random r = new Random();
		int R = r.nextInt(high - low) + low;
		return R;
	}

}
