package com.arpitos.infra;

public class Enums {

	/**
	 * Enums for setting test status
	 * 
	 * @author ArpitS
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
	 * Enums for setting test status
	 * 
	 * @author ArpitS
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

}
