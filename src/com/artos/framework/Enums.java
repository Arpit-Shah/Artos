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
package com.artos.framework;

/**
 * This class contains all generic enum which can be used by test framework
 * 
 * 
 *
 */
public class Enums {

	/**
	 * Enum for test status
	 * 
	 * 
	 *
	 */
	public enum TestStatus {

		// @formatter:off
		PASS(0),
		SKIP(1),
		KTF(2), 
		FAIL(3);
		// @formatter:on

		private final int status;

		TestStatus(int status) {
			this.status = status;
		}

		public int getValue() {
			return status;
		}

		public String getEnumName(int status) {
			for (TestStatus e : TestStatus.values()) {
				if (status == e.getValue()) {
					return e.name();
				}
			}
			return null;
		}
	}

	/**
	 * Enum for exception value which can be used anywhere in test framework
	 * 
	 * 
	 *
	 */
	public enum ExceptionValue {

		// @formatter:off
		
		INVALID_LENGTH("Invalid Length"),
		INVALID_INPUT("Invalid Input"),
		OVERSIZE_OBJECT("Object is oversize"), 
		INVALID_FILEPATH("Invalid File Path"),
		INVALID_LOCATION("Invalid Location"),
		OBJECTS_ARE_NOT_EQUAL("Objects are not equal");
		
		// @formatter:on

		private final String status;

		ExceptionValue(String status) {
			this.status = status;
		}

		public String getValue() {
			return status;
		}

		public String getEnumName(String status) {
			for (ExceptionValue e : ExceptionValue.values()) {
				if (status.equals(e.getValue())) {
					return e.name();
				}
			}
			return null;
		}
	}

	public enum Importance {

		// @formatter:off
		FATAL(0),
		CRITICAL(1),
		HIGH(2), 
		MEDIUM(3),
		LOW(4),
		UNDEFINED(5);
		// @formatter:on

		private final int status;

		Importance(int status) {
			this.status = status;
		}

		public int getValue() {
			return status;
		}

		public String getEnumName(int status) {
			for (TestStatus e : TestStatus.values()) {
				if (status == e.getValue()) {
					return e.name();
				}
			}
			return null;
		}

	}

}
