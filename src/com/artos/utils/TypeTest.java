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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.artos.framework.Enums.ExceptionValue;

public class TypeTest {

	public static void main(String[] args) throws Exception {
		Transform _con = new Transform();

		List<Integer> intTestList = new TypeTest().getIntegerTestList();
		System.out.println("******* Integer ***********");
		for (Integer y : intTestList) {
			System.out.println(y);
		}

		List<Long> longTestList = new TypeTest().getLongTestList();
		System.out.println("******* Long ***********");
		for (Long y : longTestList) {
			System.out.println(y);
		}

		List<String> stringTestList = new TypeTest().getStringTestList(19);
		System.out.println("******* String ***********");
		for (String y : stringTestList) {
			System.out.println(y);
		}

		List<Byte> byteTestList = new TypeTest().getByteTestList();
		System.out.println("******* Byte ***********");
		for (byte y : byteTestList) {
			System.out.println(_con.bytesToHexString(y, false));
		}

		List<byte[]> byteArrayTestList = new TypeTest().getByteArrayTestList(19);
		System.out.println("******* Byte ***********");
		for (byte[] y : byteArrayTestList) {
			System.out.println(_con.bytesToHexString(y, true));
		}
	}

	public List<Integer> getIntegerTestList() {
		Transform _con = new Transform();
		List<Integer> intTestList = new ArrayList<>();

		// Max value
		intTestList.add(Integer.MAX_VALUE);
		// Min value
		intTestList.add(Integer.MIN_VALUE);
		// Center value
		intTestList.add(0);
		// random positive value
		intTestList.add(_con.randInt(0 + 1, Integer.MAX_VALUE - 1));
		// random negative value
		intTestList.add((_con.randInt(0 + 1, Integer.MAX_VALUE - 1)) * (-1));

		return intTestList;
	}

	public List<Long> getLongTestList() {
		Transform _con = new Transform();
		List<Long> longTestList = new ArrayList<>();

		// Max value
		longTestList.add(Long.MAX_VALUE);
		// Min value
		longTestList.add(Long.MIN_VALUE);
		// Center value
		longTestList.add(0l);
		// Random long value
		longTestList.add(_con.randLong());
		// random positive long value in the range of int
		longTestList.add((long) _con.randInt(0 + 1, Integer.MAX_VALUE - 1));
		// random negative long value in the range of int
		longTestList.add((long) ((_con.randInt(0 + 1, Integer.MAX_VALUE - 1)) * (-1)));

		return longTestList;
	}

	public List<String> getStringTestList(int maxLength) throws Exception {

		if (maxLength < 1) {
			throw new Exception(ExceptionValue.INVALID_INPUT.getValue());
		}

		Transform _con = new Transform();
		List<String> stringTestList = new ArrayList<>();

		// All Uppercase Char
		stringTestList.add("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
		// All Lowercase Char
		stringTestList.add("abcdefghijklmnopqrstuvwxyz");
		// All Digit
		stringTestList.add("0123456789");
		// Non-abcd char
		stringTestList.add("<>()!@#$%^&*?/\\+-~`,.\"'");
		// Empty String
		stringTestList.add("");
		// Null
		stringTestList.add(null);
		// Chinese char
		stringTestList.add("\u7684");
		// Non-printable char new line
		stringTestList.add("\n");
		// Non-printable char tab
		stringTestList.add("\t");
		// Non-printable char tab
		stringTestList.add("\r");
		// Non-printable char backspace
		stringTestList.add("\b");
		// Non-printable char form feed
		stringTestList.add("\f");
		// Random char String Full size
		stringTestList.add(_con.randString(maxLength));
		// Random char String half size
		stringTestList.add(_con.randString(maxLength / 2));

		return stringTestList;
	}

	public List<Byte> getByteTestList() {
		Transform _con = new Transform();
		List<Byte> byteTestList = new ArrayList<>();

		// Min value
		byteTestList.add((byte) 0x00);
		// Max value
		byteTestList.add((byte) 0xFF);
		// Random long value
		byteTestList.add((byte) _con.randInt(1, 255));

		return byteTestList;
	}

	public List<byte[]> getByteArrayTestList(int maxLength) throws Exception {

		if (maxLength < 1) {
			throw new Exception(ExceptionValue.INVALID_INPUT.getValue());
		}

		Transform _con = new Transform();
		List<byte[]> byteArrayTestList = new ArrayList<>();

		// Empty byte Array
		byte[] temp1 = new byte[] {};
		byteArrayTestList.add(temp1);
		// Max length filled with 0xFF
		byte[] temp2 = new byte[maxLength];
		Arrays.fill(temp2, (byte) 0xFF);
		byteArrayTestList.add(temp2);
		// Max length filled with 0x00
		byte[] temp3 = new byte[maxLength];
		Arrays.fill(temp3, (byte) 0x00);
		byteArrayTestList.add(temp3);
		// Random bytes array maxLength
		byteArrayTestList.add(_con.randBytes(maxLength));
		// Random bytes array half size
		byteArrayTestList.add(_con.randBytes(maxLength / 2));

		return byteArrayTestList;
	}

	public List<String> getHTMLTestList() {
		List<String> htmlTestList = new ArrayList<>();

		// cross scripting XSS, by making script to run
		htmlTestList.add("<script> alert(\"Alert\"); </script>");
		// Background colour attribute cross scription
		// <!-- background attribute -->
		// <body background="javascript:alert("XSS")">
		htmlTestList.add("javascript:alert(\"Alert\")");
		// Imag source cross scripting
		// <!-- <img> tag XSS -->
		// <img src="javascript:alert("XSS");">
		htmlTestList.add("javascript:alert(\"Alert\");");
		// division style cross scripting
		// <!-- <div> tag XSS -->
		// <div style="background-image: url(javascript:alert('XSS'))">
		htmlTestList.add("background-image: url(javascript:alert('XSS'))");

		return htmlTestList;
	}

}
