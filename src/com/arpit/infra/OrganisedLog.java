package com.arpit.infra;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.arpit.infra.TestContext.Status;
import com.arpit.utils.Convert;

public class OrganisedLog {

	private String rootLogDir = "./reporting";
	private String currentLogDir = getRootLogDir();
	private String currentTestName = "NotSet";
	private FileWriter fwriter_log;
	private PrintWriter outputfile_log;
	private FileWriter fwriter_summary;
	private PrintWriter outputfile_summary;
	private boolean EnableConsolLog = true;
	private boolean EnableLogToFile = true;
	private boolean EnableTimeStamp = false;
	private LOG_LEVEL currentLogLevel = LOG_LEVEL.DEBUG;

	public OrganisedLog(String strDestDirName, String strTestName, LOG_LEVEL logLevel) {

		File file = new File(getRootLogDir());
		System.out.println(file.getAbsolutePath());
		if (!file.exists()) {
			file.mkdir();
		}

		setCurrentTestName(strTestName);
		setLogDestDir(strDestDirName);
		setTestName(strTestName);
		setCurrentLogLevel(logLevel);
	}

	/**
	 * Enums for setting log level
	 * 
	 * @author arpit_000
	 *
	 */
	public enum LOG_LEVEL {
		ALL(0), DEBUG(1), INFO(2), WARNING(3), SENSITIVE(4);

		private final int value;

		LOG_LEVEL(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public String getEnumName(int value) {
			for (Status e : Status.values()) {
				if (value == e.getValue()) {
					return e.name();
				}
			}
			return null;
		}
	}

	public void setLogDestDir(String strDestDirName) {
		try {
			File file = new File(getRootLogDir() + "/" + strDestDirName);
			if (!file.exists()) {
				file.mkdir();
			}
			setCurrentLogDir(getRootLogDir() + "/" + strDestDirName);
			setTestName(getCurrentTestName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setTestName(String strTestName) {
		try {
			if (null != getOutputfile_log()) {
				getFwriter_log().close();
				getOutputfile_log().close();
			}
			setCurrentTestName(strTestName);
			String strCurrentTime = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
			setFwriter_log(new FileWriter(getCurrentLogDir() + "/" + strCurrentTime + "_" + strTestName + ".log", true));
			setOutputfile_log(new PrintWriter(getFwriter_log()));
			setFwriter_summary(new FileWriter(getCurrentLogDir() + "/" + strCurrentTime + "_" + strTestName + ".summary", true));
			setOutputfile_summary(new PrintWriter(getFwriter_summary()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void appendSummaryReport(Status status, String strTestName, String JIRARef, long passCount, long failCount, long skipCount,
			long ktfCount) {
		String testStatus = String.format("%-" + 4 + "s", status.getEnumName(status.getValue()));
		String testName = String.format("%-" + 60 + "s", strTestName).replace(" ", ".");
		String JiraRef = String.format("%-" + 10 + "s", JIRARef);
		String PassCount = String.format("%-" + 10 + "s", passCount);
		String FailCount = String.format("%-" + 10 + "s", failCount);
		String SkipCount = String.format("%-" + 10 + "s", skipCount);
		String KTFCount = String.format("%-" + 10 + "s", ktfCount);
		getOutputfile_summary().println(
				testStatus + " = " + testName + " " + JiraRef + " P:" + PassCount + " F:" + FailCount + " S:" + SkipCount + " K:" + KTFCount);
		getOutputfile_summary().flush();
	}

	public void println(LOG_LEVEL logLevel, String strToWrite) {

		if (getCurrentLogLevel().value > logLevel.value) {
			return;
		}

		if (null == strToWrite) {
			strToWrite = "null";
		}
		String[] logString = strToWrite.split("\n");
		for (String log : logString) {
			if (isEnableTimeStamp()) {
				log = (new SimpleDateFormat("MM/dd/yyyy HH:mm:ss:SS").format(new Date()) + " " + log);
			}
			if (isEnableConsolLog()) {
				System.out.println(log);
			}
			if (isEnableLogToFile()) {
				getOutputfile_log().println(log);
				getOutputfile_log().flush();
			}
		}
	}

	public void println(LOG_LEVEL logLevel, byte[] bytearray) {

		if (getCurrentLogLevel().value > logLevel.value) {
			return;
		}

		if (null == bytearray) {
			String strToWrite = "null";
			println(logLevel, strToWrite);
		} else {
			String strToWrite = new Convert().bytesToStringHex(bytearray, true);
			println(logLevel, strToWrite);
		}
	}

	public void println(LOG_LEVEL logLevel, int integerValue) {

		if (getCurrentLogLevel().value > logLevel.value) {
			return;
		}

		String strToPrint = Integer.toString(integerValue);
		println(logLevel, strToPrint);
	}

	public void println(LOG_LEVEL logLevel, long longValue) {

		if (getCurrentLogLevel().value > logLevel.value) {
			return;
		}

		String strToPrint = Long.toString(longValue);
		println(logLevel, strToPrint);
	}

	public void print(LOG_LEVEL logLevel, String strToWrite) {

		if (getCurrentLogLevel().value > logLevel.value) {
			return;
		}

		if (null == strToWrite) {
			strToWrite = "null";
		}
		String[] logString = strToWrite.split("\n");
		for (String log : logString) {
			if (isEnableTimeStamp()) {
				log = (new SimpleDateFormat("MM/dd/yyyy HH:mm:ss:SS").format(new Date()) + " " + log);
			}
			if (isEnableConsolLog()) {
				System.out.print(log);
			}
			if (isEnableLogToFile()) {
				getOutputfile_log().print(log);
				getOutputfile_log().flush();
			}
		}
	}

	public void println_err(LOG_LEVEL logLevel, String strToWrite) {

		if (getCurrentLogLevel().value > logLevel.value) {
			return;
		}

		if (null == strToWrite) {
			strToWrite = "null";
		}
		String[] logString = strToWrite.split("\n");
		for (String log : logString) {
			if (isEnableTimeStamp()) {
				log = (new SimpleDateFormat("MM/dd/yyyy HH:mm:ss:SS").format(new Date()) + " " + log);
			}
			if (isEnableConsolLog()) {
				System.err.println(log);
			}
			if (isEnableLogToFile()) {
				getOutputfile_log().println(log);
				getOutputfile_log().flush();
			}
		}
	}

	private String getRootLogDir() {
		return rootLogDir;
	}

	@SuppressWarnings("unused")
	private void setRootLogDir(String rootLogDir) {
		this.rootLogDir = rootLogDir;
	}

	private String getCurrentLogDir() {
		return currentLogDir;
	}

	private void setCurrentLogDir(String currentLogDir) {
		this.currentLogDir = currentLogDir;
	}

	private String getCurrentTestName() {
		return currentTestName;
	}

	private void setCurrentTestName(String currentTestName) {
		this.currentTestName = currentTestName;
	}

	private FileWriter getFwriter_log() {
		return fwriter_log;
	}

	private void setFwriter_log(FileWriter fwriter) {
		this.fwriter_log = fwriter;
	}

	private PrintWriter getOutputfile_log() {
		return outputfile_log;
	}

	private void setOutputfile_log(PrintWriter outputfile) {
		this.outputfile_log = outputfile;
	}

	public boolean isEnableConsolLog() {
		return EnableConsolLog;
	}

	public void setEnableConsolLog(boolean enableConsolLog) {
		EnableConsolLog = enableConsolLog;
	}

	public boolean isEnableLogToFile() {
		return EnableLogToFile;
	}

	public void setEnableLogToFile(boolean enableLogToFile) {
		EnableLogToFile = enableLogToFile;
	}

	private FileWriter getFwriter_summary() {
		return fwriter_summary;
	}

	private void setFwriter_summary(FileWriter fwriter_summary) {
		this.fwriter_summary = fwriter_summary;
	}

	private PrintWriter getOutputfile_summary() {
		return outputfile_summary;
	}

	private void setOutputfile_summary(PrintWriter outputfile_summary) {
		this.outputfile_summary = outputfile_summary;
	}

	public boolean isEnableTimeStamp() {
		return EnableTimeStamp;
	}

	public void setEnableTimeStamp(boolean enableTimeStamp) {
		EnableTimeStamp = enableTimeStamp;
	}

	public LOG_LEVEL getCurrentLogLevel() {
		return currentLogLevel;
	}

	public void setCurrentLogLevel(LOG_LEVEL currentLogLevel) {
		this.currentLogLevel = currentLogLevel;
	}

}
