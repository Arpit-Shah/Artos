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

		/**
		 * Get enum value
		 * 
		 * @return enum value
		 */
		public int getValue() {
			return status;
		}

		/**
		 * Get Enum name
		 * 
		 * @param status status
		 * @return Enum name
		 */
		public static String getEnumName(int status) {
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

		/**
		 * Get enum value
		 * 
		 * @return enum value
		 */
		public String getValue() {
			return status;
		}

		/**
		 * Get Enum name
		 * 
		 * @param status status
		 * @return Enum name
		 */
		public String getEnumName(String status) {
			for (ExceptionValue e : ExceptionValue.values()) {
				if (status.equals(e.getValue())) {
					return e.name();
				}
			}
			return null;
		}
	}

	/**
	 * Enum for imprtance which can be used anywhere in test framework
	 */
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

		/**
		 * Get enum value
		 * 
		 * @return enum value
		 */
		public int getValue() {
			return status;
		}

		/**
		 * Get Enum name
		 * 
		 * @param status status
		 * @return Enum name
		 */
		public static String getEnumName(int status) {
			for (Importance e : Importance.values()) {
				if (status == e.getValue()) {
					return e.name();
				}
			}
			return null;
		}

	}

	/**
	 * Enum for Script file type
	 * 
	 */
	public enum ScriptFileType {

		// @formatter:off
		
		TEST_SCRIPT(0),
		ERROR_SCRIPT(1);
		
		// @formatter:on

		private final int status;

		ScriptFileType(int status) {
			this.status = status;
		}

		/**
		 * Get enum value
		 * 
		 * @return enum value
		 */
		public int getValue() {
			return status;
		}

		/**
		 * Get Enum name
		 * 
		 * @param status status
		 * @return Enum name
		 */
		public String getEnumName(int status) {
			for (ScriptFileType e : ScriptFileType.values()) {
				if (status == e.getValue()) {
					return e.name();
				}
			}
			return null;
		}

	}

	/**
	 * Enum for Gherkin
	 *
	 */
	public enum Gherkin {

		// @formatter:off
		
		
		GIVEN("GIVEN"), // for any statement starting with Given
		AND("AND"), // for any statement starting with And
		BUT("BUT"), // for any statement starting with but
		WHEN("WHEN"), // for any statement starting with When
		THEN("THEN"); // for any statement starting with Then
		
		// @formatter:on

		private final String step;

		Gherkin(String status) {
			this.step = status;
		}

		/**
		 * Get enum value
		 * 
		 * @return enum value
		 */
		public String getValue() {
			return step;
		}

		/**
		 * Get Enum name
		 * 
		 * @param step step
		 * @return Enum name
		 */
		public String getEnumName(String step) {
			for (Gherkin e : Gherkin.values()) {
				if (step.equals(e.getValue())) {
					return e.name();
				}
			}
			return null;
		}

	}

}
