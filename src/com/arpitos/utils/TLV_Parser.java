package com.arpitos.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.arpitos.framework.infra.TestContext;
import com.arpitos.utils.Convert;

public class TLV_Parser {

	HashMap<Integer, HashMap<String, String>> hashMapIndexAndHashMapHoldingKeyValues = new HashMap<Integer, HashMap<String, String>>();

	String tlv = null;
	int pos = 0;
	int hashMapIndex = 0;

	public HashMap<Integer, HashMap<String, String>> parseTLV(byte[] tlvBytes) throws Exception {
		// convert bytes to string
		tlv = new Convert().bytesToHexString(tlvBytes);
		if (tlv == null || tlv.length() % 2 != 0) {
			throw new RuntimeException("Invalid tlv, null or odd length");
		}

		// if user has passed in complete invenco packet then remove unwanted
		// part of messages
		if (tlv.startsWith("02") && tlv.subSequence(tlv.length() - 6, tlv.length() - 4).equals("03")) {
			tlv = tlv.substring(18, tlv.length() - 6);
		}
		findKeyandValue();
		return returnHashMap();
	}

	public HashMap<Integer, HashMap<String, String>> parseTLV(String strtlv) {
		// convert bytes to string
		tlv = strtlv;
		if (tlv == null || tlv.length() % 2 != 0) {
			throw new RuntimeException("Invalid tlv, null or odd length");
		}

		// if user has passed in complete invenco packet then remove unwanted
		// part of messages
		if (tlv.startsWith("02") && tlv.subSequence(tlv.length() - 6, tlv.length() - 4).equals("03")) {
			tlv = tlv.substring(18, tlv.length() - 6);
		}

		findKeyandValue();
		return returnHashMap();
	}

	public byte[] constructTLV(TestContext context, String strTag, String strValue) throws Exception {
		Convert _con = new Convert();

		if (strValue != null) {
			byte[] value = _con.stringHexToByteArray(strValue);
			byte[] len = new byte[] { _con.intToByte(value.length) };
			return _con.concat(_con.stringHexToByteArray(strTag), len, value);
		} else {
			byte[] length = { 0x00 };
			return _con.concat(_con.stringHexToByteArray(strTag), length);
		}
	}

	public byte[] constructTLV(String strTag, String strValue) throws Exception {
		Convert _con = new Convert();

		if (strValue != null) {
			byte[] value = _con.stringHexToByteArray(strValue);
			byte[] len = new byte[] { _con.intToByte(value.length) };
			return _con.concat(_con.stringHexToByteArray(strTag), len, value);
		} else {
			byte[] length = { 0x00 };
			return _con.concat(_con.stringHexToByteArray(strTag), length);
		}
	}

	/**
	 * Prints TLV+Data in separate line, Maintains the sequence of data
	 * 
	 * @param context
	 * @param data
	 *            = TLV Payload
	 * @throws Exception
	 */
	public void parseAndPrintTLV(TestContext context, byte[] data) throws Exception {
		// Sometime TLV can be empty
		if (data.length == 0) {
			return;
		}
		HashMap<Integer, HashMap<String, String>> mapFromIndex = parseTLV(data);
		context.getLogger().info("\nParsed TLV:");
		for (int i = 0; i < mapFromIndex.size(); i++) {
			HashMap<String, String> map = mapFromIndex.get(new Integer(i));

			for (Map.Entry<String, String> entry : map.entrySet()) {
				context.getLogger().info(entry.getKey() + " = " + entry.getValue());
			}
		}
	}

	private HashMap<Integer, HashMap<String, String>> returnHashMap() {
		return hashMapIndexAndHashMapHoldingKeyValues;
	}

	private void findKeyandValue() {

		for (; pos < tlv.length();) {

			try {
				String key = findTLVTagKey();
				int valueLength = findTLVTagValueLength();
				String value = findTLVTagValue(valueLength);
				appendKeyValueToHashMap(key, value);

				if (key.equals("df54") || key.equals("df55") || key.equals("df56") || key.equals("df57") || key.equals("ff01")) {
					String childTLV = value;
					// System.out.println(childTLV);
					Map<Integer, HashMap<String, String>> childHashMapIndexAndHashMapHoldingKeyValues = new HashMap<Integer, HashMap<String, String>>();
					childHashMapIndexAndHashMapHoldingKeyValues = new TLV_Parser().parseTLV(childTLV);
					for (int i = 0; i < childHashMapIndexAndHashMapHoldingKeyValues.size(); i++) {

						// get map as per index and append into existing HashMap
						HashMap<String, String> map = childHashMapIndexAndHashMapHoldingKeyValues.get(new Integer(i));
						for (Map.Entry<String, String> entry : map.entrySet()) {
							appendKeyValueToHashMap(entry.getKey(), entry.getValue());
						}
					}
				}

			} catch (NumberFormatException e) {
				throw new RuntimeException("Error parsing number", e);
			} catch (IndexOutOfBoundsException e) {
				throw new RuntimeException("Error processing field", e);
			}
		}
	}

	private String findTLVTagValue(int valueLength) throws NumberFormatException {
		int length = valueLength;
		String value = tlv.substring(pos, pos = pos + length);
		return value;
	}

	private int findTLVTagValueLength() throws NumberFormatException {
		String len = tlv.substring(pos, pos = pos + 2);
		int length = Integer.parseInt(len, 16);

		if (length > 127) {
			// more than 1 byte for length
			int bytesLength = length - 128;
			len = tlv.substring(pos, pos = pos + (bytesLength * 2));
			length = Integer.parseInt(len, 16);
		}
		length *= 2;
		return length;
	}

	private String findTLVTagKey() throws NumberFormatException {
		String key = tlv.substring(pos, pos = pos + 2);
		String keysub2 = null;
		// If First byte has last five bits set to 1 then there is one
		// more byte to follow
		if ((Integer.parseInt(key, 16) & 0x1F) == 0x1F) {
			// store second byte to process later
			keysub2 = tlv.substring(pos, pos + 2);
			key += tlv.substring(pos, pos = pos + 2);
		}
		// if second byte has first bit set to 1 then there is one more
		// byte to follow
		if (keysub2 != null && (Integer.parseInt(keysub2, 16) >= 128)) {
			key += tlv.substring(pos, pos = pos + 2);
		}
		return key;
	}

	private void appendKeyValueToHashMap(String key, String value) {
		HashMap<String, String> hashMapKeyValues = new HashMap<String, String>();
		// System.out.println(key+" = "+value);
		hashMapKeyValues.put(key, value);
		hashMapIndexAndHashMapHoldingKeyValues.put(hashMapIndex, hashMapKeyValues);
		hashMapIndex++;
	}

	/**
	 * This function takes single string value of of Ber-TLV Tag key It goes
	 * through properties file of supported TLV tags and returns description
	 * 
	 * <PRE>
	 * Example : tlvLookUp("9F29"); or tlLookUp("9f29");
	 * Return Value : ET_EXTENDED_SELECTION          0x9F29
	 * </PRE>
	 * 
	 * @param strTagKey
	 * @return
	 */
	public static String tlvLookUp(String strTagKey) {
		String TagDesc = "unknown";
		String tagKey = strTagKey.toUpperCase();

		Properties prop = new Properties();
		InputStream input = null;

		File confDir = new File("conf");
		File tlvPropertieFile = new File(confDir, "tlv.tag.properties");
		// System.out.println(tlvPropertieFile.getAbsolutePath());
		try {

			input = new FileInputStream(tlvPropertieFile);
			// load a properties file
			prop.load(input);
			// get the property value and print it out
			TagDesc = prop.getProperty(tagKey);

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return TagDesc;
	}

	/**
	 * Find Specific tag value/values
	 * 
	 * @param tlv
	 * @param strTLVKey
	 * @return
	 * @throws Exception
	 */
	public ArrayList<String> findValueForSpecificTag(byte[] tlv, String strTLVKey) throws Exception {

		ArrayList<String> TagValue = new ArrayList<String>();
		if (strTLVKey == null || strTLVKey.length() % 2 != 0) {
			throw new RuntimeException("Invalid tlvKey, null or odd length");
		}
		String searchKey = strTLVKey.toUpperCase();
		HashMap<Integer, HashMap<String, String>> mapFromIndex = new TLV_Parser().parseTLV(tlv);
		for (int i = 0; i < mapFromIndex.size(); i++) {
			HashMap<String, String> map = mapFromIndex.get(new Integer(i));
			for (Map.Entry<String, String> entry : map.entrySet()) {
				if (searchKey.equals(entry.getKey().toUpperCase())) {
					TagValue.add(entry.getValue());
				}
			}
		}

		return TagValue;
	}

	/**
	 * Find Specific tag value/values
	 * 
	 * @param tlv
	 * @param strTLVKey
	 * @return
	 */
	public ArrayList<String> findValueForSpecificTag(String tlv, String strTLVKey) {

		ArrayList<String> TagValue = new ArrayList<String>();
		if (strTLVKey == null || strTLVKey.length() % 2 != 0) {
			throw new RuntimeException("Invalid tlvKey, null or odd length");
		}
		String searchKey = strTLVKey.toUpperCase();
		HashMap<Integer, HashMap<String, String>> mapFromIndex = new TLV_Parser().parseTLV(tlv);
		for (int i = 0; i < mapFromIndex.size(); i++) {
			HashMap<String, String> map = mapFromIndex.get(new Integer(i));
			for (Map.Entry<String, String> entry : map.entrySet()) {
				if (searchKey.equals(entry.getKey().toUpperCase())) {
					TagValue.add(entry.getValue());
				}
			}
		}

		return TagValue;
	}

	public String parseTransactionOutcome(String strOutCome) {
		if (strOutCome != null) {
			if (strOutCome.equals("00")) {
				return "OC_APPROVED,       /// offline approved";
			}
			if (strOutCome.equals("01")) {
				return "OC_ONLINE_REQ,     /// requires online authorization";
			}
			if (strOutCome.equals("02")) {
				return "OC_DECLINED,       /// offline declined";
			}
			if (strOutCome.equals("03")) {
				return "OC_TRY_OTHER_IF,   /// can't complete contactless but contact or magstripe may be available";
			}
			if (strOutCome.equals("04")) {
				return "OC_END_APP,        /// error was encountered";
			}
			if (strOutCome.equals("05")) {
				return "OC_SELECT_NEXT,    /// combination unsuitable, try next (if any) (this outcome is internal to the framework)";
			}
			if (strOutCome.equals("06")) {
				return "OC_TRY_AGAIN,      /// kernel wants card to be presented again (this outcome is internal to the framework)";
			}
			if (strOutCome.equals("07")) {
				return "OC_WANTED_ONLINE,  /// kernel required online auth but is offline only - app should treat as declined";
			}
			if (strOutCome.equals("08")) {
				return "OC_SEE_PHONE       /// phone encountered error (context is conflicting, PIN required). Cardholder should rectify and try again.";
			}
		}
		return null;
	}

	public static byte[] getTLVTagLengthField(int reqDataLen) {
		int constructorLen;
		// Calculate len
		if (reqDataLen > 0xFF)
			constructorLen = 3; // long long form constructor length
		else if (reqDataLen > 0x7F)
			constructorLen = 2; // long form constructor length
		else
			constructorLen = 1; // short form constructor length

		// allocate space
		byte[] constructorLenField = new byte[constructorLen];

		// fill space
		if (reqDataLen > 0xFF) {
			constructorLenField[0] = (byte) 0x82;
			constructorLenField[1] = (byte) ((reqDataLen >> 8) & 0xFF);
			constructorLenField[2] = (byte) (reqDataLen & 0xFF);
		} else if (reqDataLen > 0x7F) {
			constructorLenField[0] = (byte) 0x81;
			constructorLenField[1] = (byte) (reqDataLen & 0xFF);
		} else {
			constructorLenField[0] = (byte) (reqDataLen & 0xFF);
		}

		return constructorLenField;
	}

}
