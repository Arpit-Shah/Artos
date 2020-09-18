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

public class SummaryBreakDown {

//	public static void main(String[] args) {
////		String summaryLineItem = "PASS = com.tests.feature1.TestCase_1....................................................................... P:1    S:0    K:0    F:0    [          ] duration:100:00:00.007 ....................";
//		String summaryLineItem = "  |--PASS = testUnit_1(context)............................................................................  :      :      :      :     [          ] duration:000:00:00.000 ....................";
//		SummaryBreakDown sb = new SummaryBreakDown(summaryLineItem);
//		System.out.println(sb.getResult());
//		System.out.println(sb.getTestName());
//		System.out.println(sb.getPassCount());
//		System.out.println(sb.getSkipCount());
//		System.out.println(sb.getFailCount());
//		System.out.println(sb.getKtfCount());
//		System.out.println(sb.getImportance());
//		System.out.println(sb.getDuration());
//		System.out.println(sb.getDurationInMilliseconds());
//		System.out.println(sb.getBugRef());
//	}

	int totalLength = 192;
	String summaryLine = null;
	String result = "";
	String testName = "";
	String passCount = "";
	String skipCount = "";
	String failCount = "";
	String ktfCount = "";
	String importance = "";
	String duration = "";
	String bugRef = "";

	public SummaryBreakDown(String summaryLineItem) {
		this.summaryLine = summaryLineItem;
		if (summaryLine.length() == totalLength && isTest()) {
			result = summaryLine.substring(0, 0 + 5).trim();
			testName = summaryLine.substring(7, 100 + 7).replaceAll("\\.", " ").trim().replaceAll("\\s+", ".");
			passCount = summaryLine.substring(110, 110 + 4).trim();
			skipCount = summaryLine.substring(117, 117 + 4).trim();
			ktfCount = summaryLine.substring(124, 124 + 4).trim();
			failCount = summaryLine.substring(131, 131 + 4).trim();
			importance = summaryLine.substring(137, 137 + 10).trim();
			duration = summaryLine.substring(158, 158 + 13).trim();
			bugRef = summaryLine.substring(172, 172 + 20).replaceAll("\\.", " ").trim();
		} else if (summaryLine.length() == totalLength && isTestUnit()) {
			result = summaryLine.substring(5, 5 + 5).trim();
			testName = summaryLine.substring(12, 95 + 12).replaceAll("\\.", " ").trim().replaceAll("\\s+", ".");
			passCount = summaryLine.substring(110, 110 + 4).trim();
			skipCount = summaryLine.substring(117, 117 + 4).trim();
			ktfCount = summaryLine.substring(124, 124 + 4).trim();
			failCount = summaryLine.substring(131, 131 + 4).trim();
			importance = summaryLine.substring(137, 137 + 10).trim();
			duration = summaryLine.substring(158, 158 + 13).trim();
			bugRef = summaryLine.substring(172, 172 + 20).replaceAll("\\.", " ").trim();
		} else {
			System.err.println("Invalid Input");
		}
	}

	public String getSummaryLine() {
		return summaryLine;
	}

	public void setSummaryLine(String summaryLine) {
		this.summaryLine = summaryLine;
	}

	public boolean isTest() {
		return summaryLine.startsWith("  |--") ? false : true;
	}

	public boolean isTestUnit() {
		return summaryLine.startsWith("  |--") ? true : false;
	}

	public int getTotalLength() {
		return totalLength;
	}

	public String getTestName() {
		return testName;
	}

	public String getResult() {
		return result;
	}

	public String getPassCount() {
		return passCount;
	}

	public String getSkipCount() {
		return skipCount;
	}

	public String getFailCount() {
		return failCount;
	}

	public String getKtfCount() {
		return ktfCount;
	}

	public String getImportance() {
		return importance;
	}

	public String getDuration() {
		return duration;
	}

	public int getDurationInMilliseconds() {
		int Hours = Integer.parseInt(getDuration().substring(0, 3));
		int Minutes = Integer.parseInt(getDuration().substring(4, 6));
		int seconds = Integer.parseInt(getDuration().substring(7, 9));
		int milliseconds = Integer.parseInt(getDuration().substring(10));
		return (Hours * 60 * 60 * 1000) + (Minutes * 60 * 1000) + (seconds * 1000) + milliseconds;
	}

	public String getBugRef() {
		return bugRef;
	}
}
