package com.arpitos.framework.infra;

import java.io.File;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Logger;

import com.arpitos.framework.Enums.TestStatus;

public class LogWrapper {
	static final String FQCN = LogWrapper.class.getName();
	OrganisedLog organisedLogger;
	Logger generalLogger;
	Logger summaryLogger;

	public LogWrapper(String logDir, String strTestName, boolean enableLogDecoration, boolean enableTextLog, boolean enableHTMLLog) {
		this.organisedLogger = new OrganisedLog(logDir, strTestName, enableLogDecoration, enableTextLog, enableHTMLLog);
		this.generalLogger = organisedLogger.getGeneralLogger();
		this.summaryLogger = organisedLogger.getSummaryLogger();
	}

	/**
	 * Append test summary to summary report
	 * 
	 * @param status
	 *            Test Status
	 * @param strTestName
	 *            TestName
	 * @param bugTrackingNumber
	 *            JIRA-BugTracking Number
	 * @param passCount
	 *            Passed Test Count
	 * @param failCount
	 *            Failed Test Count
	 * @param skipCount
	 *            Skipped Test Count
	 * @param ktfCount
	 *            Known to Fail Test Count
	 * @param totalTestTime
	 *            Test time
	 */
	public void appendSummaryReport(TestStatus status, String strTestName, String bugTrackingNumber, long passCount, long failCount, long skipCount,
			long ktfCount, long totalTestTime) {
		organisedLogger.appendSummaryReport(status, strTestName, bugTrackingNumber, passCount, failCount, skipCount, ktfCount, totalTestTime);
	}

	/**
	 * Disables Logging
	 */
	public void disableGeneralLog() {
		organisedLogger.disableGeneralLog();
	}

	/**
	 * Enable Logging
	 */
	public void enableGeneralLog() {
		organisedLogger.enableGeneralLog();
	}

	/**
	 * Returns current general error log files (Includes txt and html files)
	 * 
	 * @return General error log file list
	 */
	public List<File> getCurrentErrorLogFiles() {
		return organisedLogger.getCurrentErrorLogFiles();
	}

	/**
	 * Returns current general log files (Includes txt and html files)
	 * 
	 * @return General log file list
	 */
	public List<File> getCurrentGeneralLogFiles() {
		return organisedLogger.getCurrentGeneralLogFiles();
	}

	/**
	 * Returns current summary log files (Includes txt and html files)
	 * 
	 * @return Summary log file list
	 */
	public List<File> getCurrentSummaryLogFiles() {
		return organisedLogger.getCurrentSummaryLogFiles();
	}

	/**
	 * Print selected System Info to log file
	 * 
	 * @param generalLogger
	 */
	public void printUsefulInfo() {
		organisedLogger.printUsefulInfo();
	}

	/**
	 * Returns Log Files Base Directory
	 * 
	 * @return
	 */
	public String getLogBaseDir() {
		return organisedLogger.getLogBaseDir();
	}

	// ===================================================================
	// Trace
	// ===================================================================
	public void trace(Object msg, Throwable t) {
		generalLogger.logIfEnabled(FQCN, Level.TRACE, null, msg, t);
	}

	public void trace(String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
		generalLogger.logIfEnabled(FQCN, Level.TRACE, null, msg, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
	}

	public void trace(String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
		generalLogger.logIfEnabled(FQCN, Level.TRACE, null, msg, p0, p1, p2, p3, p4, p5, p6, p7, p8);
	}

	public void trace(String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
		generalLogger.logIfEnabled(FQCN, Level.TRACE, null, msg, p0, p1, p2, p3, p4, p5, p6, p7);
	}

	public void trace(String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
		generalLogger.logIfEnabled(FQCN, Level.TRACE, null, msg, p0, p1, p2, p3, p4, p5, p6);
	}

	public void trace(String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
		generalLogger.logIfEnabled(FQCN, Level.TRACE, null, msg, p0, p1, p2, p3, p4, p5);
	}

	public void trace(String msg, Object p0, Object p1, Object p2, Object p3, Object p4) {
		generalLogger.logIfEnabled(FQCN, Level.TRACE, null, msg, p0, p1, p2, p3, p4);
	}

	public void trace(String msg, Object p0, Object p1, Object p2, Object p3) {
		generalLogger.logIfEnabled(FQCN, Level.TRACE, null, msg, p0, p1, p2, p3);
	}

	public void trace(String msg, Object p0, Object p1, Object p2) {
		generalLogger.logIfEnabled(FQCN, Level.TRACE, null, msg, p0, p1, p2);
	}

	public void trace(String msg, Object p0, Object p1) {
		generalLogger.logIfEnabled(FQCN, Level.TRACE, null, msg, p0, p1);
	}

	public void trace(String msg, Object p0) {
		generalLogger.logIfEnabled(FQCN, Level.TRACE, null, msg, p0);
	}

	public void trace(String msg) {
		generalLogger.logIfEnabled(FQCN, Level.TRACE, null, msg);
	}

	public void trace(final Object message) {
		generalLogger.logIfEnabled(FQCN, Level.TRACE, null, message, null);
	}

	// ===================================================================
	// Debug
	// ===================================================================
	public void debug(Object msg, Throwable t) {
		generalLogger.logIfEnabled(FQCN, Level.DEBUG, null, msg, t);
	}

	public void debug(String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
		generalLogger.logIfEnabled(FQCN, Level.DEBUG, null, msg, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
	}

	public void debug(String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
		generalLogger.logIfEnabled(FQCN, Level.DEBUG, null, msg, p0, p1, p2, p3, p4, p5, p6, p7, p8);
	}

	public void debug(String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
		generalLogger.logIfEnabled(FQCN, Level.DEBUG, null, msg, p0, p1, p2, p3, p4, p5, p6, p7);
	}

	public void debug(String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
		generalLogger.logIfEnabled(FQCN, Level.DEBUG, null, msg, p0, p1, p2, p3, p4, p5, p6);
	}

	public void debug(String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
		generalLogger.logIfEnabled(FQCN, Level.DEBUG, null, msg, p0, p1, p2, p3, p4, p5);
	}

	public void debug(String msg, Object p0, Object p1, Object p2, Object p3, Object p4) {
		generalLogger.logIfEnabled(FQCN, Level.DEBUG, null, msg, p0, p1, p2, p3, p4);
	}

	public void debug(String msg, Object p0, Object p1, Object p2, Object p3) {
		generalLogger.logIfEnabled(FQCN, Level.DEBUG, null, msg, p0, p1, p2, p3);
	}

	public void debug(String msg, Object p0, Object p1, Object p2) {
		generalLogger.logIfEnabled(FQCN, Level.DEBUG, null, msg, p0, p1, p2);
	}

	public void debug(String msg, Object p0, Object p1) {
		generalLogger.logIfEnabled(FQCN, Level.DEBUG, null, msg, p0, p1);
	}

	public void debug(String msg, Object p0) {
		generalLogger.logIfEnabled(FQCN, Level.DEBUG, null, msg, p0);
	}

	public void debug(String msg) {
		generalLogger.logIfEnabled(FQCN, Level.DEBUG, null, msg);
	}

	public void debug(final Object message) {
		generalLogger.logIfEnabled(FQCN, Level.DEBUG, null, message, null);
	}

	// ===================================================================
	// Info
	// ===================================================================
	public void info(Object msg, Throwable t) {
		generalLogger.logIfEnabled(FQCN, Level.INFO, null, msg, t);
	}

	public void info(String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
		generalLogger.logIfEnabled(FQCN, Level.INFO, null, msg, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
	}

	public void info(String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
		generalLogger.logIfEnabled(FQCN, Level.INFO, null, msg, p0, p1, p2, p3, p4, p5, p6, p7, p8);
	}

	public void info(String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
		generalLogger.logIfEnabled(FQCN, Level.INFO, null, msg, p0, p1, p2, p3, p4, p5, p6, p7);
	}

	public void info(String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
		generalLogger.logIfEnabled(FQCN, Level.INFO, null, msg, p0, p1, p2, p3, p4, p5, p6);
	}

	public void info(String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
		generalLogger.logIfEnabled(FQCN, Level.INFO, null, msg, p0, p1, p2, p3, p4, p5);
	}

	public void info(String msg, Object p0, Object p1, Object p2, Object p3, Object p4) {
		generalLogger.logIfEnabled(FQCN, Level.INFO, null, msg, p0, p1, p2, p3, p4);
	}

	public void info(String msg, Object p0, Object p1, Object p2, Object p3) {
		generalLogger.logIfEnabled(FQCN, Level.INFO, null, msg, p0, p1, p2, p3);
	}

	public void info(String msg, Object p0, Object p1, Object p2) {
		generalLogger.logIfEnabled(FQCN, Level.INFO, null, msg, p0, p1, p2);
	}

	public void info(String msg, Object p0, Object p1) {
		generalLogger.logIfEnabled(FQCN, Level.INFO, null, msg, p0, p1);
	}

	public void info(String msg, Object p0) {
		generalLogger.logIfEnabled(FQCN, Level.INFO, null, msg, p0);
	}

	public void info(String msg) {
		generalLogger.logIfEnabled(FQCN, Level.INFO, null, msg);
	}

	public void info(final Object message) {
		generalLogger.logIfEnabled(FQCN, Level.INFO, null, message, null);
	}

	// ===================================================================
	// Error
	// ===================================================================
	public void error(Object msg, Throwable t) {
		generalLogger.logIfEnabled(FQCN, Level.ERROR, null, msg, t);
	}

	public void error(String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
		generalLogger.logIfEnabled(FQCN, Level.ERROR, null, msg, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
	}

	public void error(String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
		generalLogger.logIfEnabled(FQCN, Level.ERROR, null, msg, p0, p1, p2, p3, p4, p5, p6, p7, p8);
	}

	public void error(String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
		generalLogger.logIfEnabled(FQCN, Level.ERROR, null, msg, p0, p1, p2, p3, p4, p5, p6, p7);
	}

	public void error(String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
		generalLogger.logIfEnabled(FQCN, Level.ERROR, null, msg, p0, p1, p2, p3, p4, p5, p6);
	}

	public void error(String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
		generalLogger.logIfEnabled(FQCN, Level.ERROR, null, msg, p0, p1, p2, p3, p4, p5);
	}

	public void error(String msg, Object p0, Object p1, Object p2, Object p3, Object p4) {
		generalLogger.logIfEnabled(FQCN, Level.ERROR, null, msg, p0, p1, p2, p3, p4);
	}

	public void error(String msg, Object p0, Object p1, Object p2, Object p3) {
		generalLogger.logIfEnabled(FQCN, Level.ERROR, null, msg, p0, p1, p2, p3);
	}

	public void error(String msg, Object p0, Object p1, Object p2) {
		generalLogger.logIfEnabled(FQCN, Level.ERROR, null, msg, p0, p1, p2);
	}

	public void error(String msg, Object p0, Object p1) {
		generalLogger.logIfEnabled(FQCN, Level.ERROR, null, msg, p0, p1);
	}

	public void error(String msg, Object p0) {
		generalLogger.logIfEnabled(FQCN, Level.ERROR, null, msg, p0);
	}

	public void error(String msg) {
		generalLogger.logIfEnabled(FQCN, Level.ERROR, null, msg);
	}

	public void error(final Object message) {
		generalLogger.logIfEnabled(FQCN, Level.ERROR, null, message, null);
	}

	// ===================================================================
	// Warning
	// ===================================================================
	public void warn(Object msg, Throwable t) {
		generalLogger.logIfEnabled(FQCN, Level.WARN, null, msg, t);
	}

	public void warn(String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
		generalLogger.logIfEnabled(FQCN, Level.WARN, null, msg, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
	}

	public void warn(String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
		generalLogger.logIfEnabled(FQCN, Level.WARN, null, msg, p0, p1, p2, p3, p4, p5, p6, p7, p8);
	}

	public void warn(String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
		generalLogger.logIfEnabled(FQCN, Level.WARN, null, msg, p0, p1, p2, p3, p4, p5, p6, p7);
	}

	public void warn(String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
		generalLogger.logIfEnabled(FQCN, Level.WARN, null, msg, p0, p1, p2, p3, p4, p5, p6);
	}

	public void warn(String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
		generalLogger.logIfEnabled(FQCN, Level.WARN, null, msg, p0, p1, p2, p3, p4, p5);
	}

	public void warn(String msg, Object p0, Object p1, Object p2, Object p3, Object p4) {
		generalLogger.logIfEnabled(FQCN, Level.WARN, null, msg, p0, p1, p2, p3, p4);
	}

	public void warn(String msg, Object p0, Object p1, Object p2, Object p3) {
		generalLogger.logIfEnabled(FQCN, Level.WARN, null, msg, p0, p1, p2, p3);
	}

	public void warn(String msg, Object p0, Object p1, Object p2) {
		generalLogger.logIfEnabled(FQCN, Level.WARN, null, msg, p0, p1, p2);
	}

	public void warn(String msg, Object p0, Object p1) {
		generalLogger.logIfEnabled(FQCN, Level.WARN, null, msg, p0, p1);
	}

	public void warn(String msg, Object p0) {
		generalLogger.logIfEnabled(FQCN, Level.WARN, null, msg, p0);
	}

	public void warn(String msg) {
		generalLogger.logIfEnabled(FQCN, Level.WARN, null, msg);
	}

	public void warn(final Object message) {
		generalLogger.logIfEnabled(FQCN, Level.WARN, null, message, null);
	}

	// ===================================================================
	// Fatal
	// ===================================================================
	public void fatal(Object msg, Throwable t) {
		generalLogger.logIfEnabled(FQCN, Level.FATAL, null, msg, t);
	}

	public void fatal(String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
		generalLogger.logIfEnabled(FQCN, Level.FATAL, null, msg, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
	}

	public void fatal(String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
		generalLogger.logIfEnabled(FQCN, Level.FATAL, null, msg, p0, p1, p2, p3, p4, p5, p6, p7, p8);
	}

	public void fatal(String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
		generalLogger.logIfEnabled(FQCN, Level.FATAL, null, msg, p0, p1, p2, p3, p4, p5, p6, p7);
	}

	public void fatal(String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
		generalLogger.logIfEnabled(FQCN, Level.FATAL, null, msg, p0, p1, p2, p3, p4, p5, p6);
	}

	public void fatal(String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
		generalLogger.logIfEnabled(FQCN, Level.FATAL, null, msg, p0, p1, p2, p3, p4, p5);
	}

	public void fatal(String msg, Object p0, Object p1, Object p2, Object p3, Object p4) {
		generalLogger.logIfEnabled(FQCN, Level.FATAL, null, msg, p0, p1, p2, p3, p4);
	}

	public void fatal(String msg, Object p0, Object p1, Object p2, Object p3) {
		generalLogger.logIfEnabled(FQCN, Level.FATAL, null, msg, p0, p1, p2, p3);
	}

	public void fatal(String msg, Object p0, Object p1, Object p2) {
		generalLogger.logIfEnabled(FQCN, Level.FATAL, null, msg, p0, p1, p2);
	}

	public void fatal(String msg, Object p0, Object p1) {
		generalLogger.logIfEnabled(FQCN, Level.FATAL, null, msg, p0, p1);
	}

	public void fatal(String msg, Object p0) {
		generalLogger.logIfEnabled(FQCN, Level.FATAL, null, msg, p0);
	}

	public void fatal(String msg) {
		generalLogger.logIfEnabled(FQCN, Level.FATAL, null, msg);
	}

	public void fatal(final Object message) {
		generalLogger.logIfEnabled(FQCN, Level.FATAL, null, message, null);
	}

	// ===================================================================
	// Getter Setter
	// ===================================================================

	public Logger getGeneralLogger() {
		return generalLogger;
	}

	public void setGeneralLogger(Logger generalLogger) {
		this.generalLogger = generalLogger;
	}

	public Logger getSummaryLogger() {
		return summaryLogger;
	}

	public void setSummaryLogger(Logger summaryLogger) {
		this.summaryLogger = summaryLogger;
	}

}
