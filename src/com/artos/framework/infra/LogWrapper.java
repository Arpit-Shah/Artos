package com.artos.framework.infra;

import java.io.File;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Logger;

import com.artos.framework.Enums.TestStatus;

/** Wrapper class which provides abstraction for logging mechanism */
public class LogWrapper {
	static final String FQCN = LogWrapper.class.getName();
	OrganisedLog organisedLogger;
	Logger generalLogger;
	Logger summaryLogger;
	Logger realTimeLogger;

	/**
	 * Constructor
	 * 
	 * @param context
	 *            TestContext
	 * @param logDirPath
	 *            Log base directory path
	 * @param testFQCN
	 *            Test case fully qualified class name (Example : com.test.unit.Abc)
	 * @param enableLogDecoration
	 *            Enable|disable log decoration
	 * @param enableTextLog
	 *            Enable|disable text log
	 * @param enableHTMLLog
	 *            Enable|disable HTML log
	 */
	public LogWrapper(TestContext context, String logDirPath, String testFQCN, boolean enableLogDecoration, boolean enableTextLog,
			boolean enableHTMLLog) {
		this.organisedLogger = new OrganisedLog(context, logDirPath, testFQCN, enableLogDecoration, enableTextLog, enableHTMLLog);
		this.generalLogger = organisedLogger.getGeneralLogger();
		this.summaryLogger = organisedLogger.getSummaryLogger();
		this.realTimeLogger = organisedLogger.getRealTimeLogger();
	}

	/**
	 * Append test summary to summary report
	 * 
	 * @param status
	 *            Test Status {@code TestStatus}
	 * @param strTestFQCN
	 *            Test fully qualified class name (Example : com.test.unit.Abc)
	 * @param bugTrackingNumber
	 *            BugTracking Number
	 * @param passCount
	 *            Test current pass count
	 * @param failCount
	 *            Test current fail count
	 * @param skipCount
	 *            Test current skip count
	 * @param ktfCount
	 *            Test current know to fail count
	 * @param testDuration
	 *            Test duration
	 */
	public void appendSummaryReport(TestStatus status, String strTestFQCN, String bugTrackingNumber, long passCount, long failCount, long skipCount,
			long ktfCount, long testDuration) {
		organisedLogger.appendSummaryReport(status, strTestFQCN, bugTrackingNumber, passCount, failCount, skipCount, ktfCount, testDuration);
	}

	/** Disable Logging */
	public void disableGeneralLog() {
		organisedLogger.disableGeneralLog();
	}

	/** Enable Logging */
	public void enableGeneralLog() {
		organisedLogger.enableGeneralLog();
	}

	/** Print selected system information to log file */
	public void printUsefulInfo() {
		organisedLogger.printUsefulInfo();
	}

	// ===================================================================
	// Trace
	// ===================================================================

	public void trace(String msg) {
		generalLogger.logIfEnabled(FQCN, Level.TRACE, null, msg);
	}

	public void trace(final Object message) {
		generalLogger.logIfEnabled(FQCN, Level.TRACE, null, message, null);
	}

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

	public void trace(String msg, Object... params) {
		generalLogger.logIfEnabled(FQCN, Level.TRACE, null, msg, params);
	}

	// ===================================================================
	// Debug
	// ===================================================================

	public void debug(String msg) {
		generalLogger.logIfEnabled(FQCN, Level.DEBUG, null, msg);
	}

	public void debug(final Object message) {
		generalLogger.logIfEnabled(FQCN, Level.DEBUG, null, message, null);
	}

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

	public void debug(String msg, Object... params) {
		generalLogger.logIfEnabled(FQCN, Level.DEBUG, null, msg, params);
	}

	// ===================================================================
	// Info
	// ===================================================================

	public void info(String msg) {
		generalLogger.logIfEnabled(FQCN, Level.INFO, null, msg);
	}

	public void info(final Object message) {
		generalLogger.logIfEnabled(FQCN, Level.INFO, null, message, null);
	}

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

	public void info(String msg, Object... params) {
		generalLogger.logIfEnabled(FQCN, Level.INFO, null, msg, params);
	}

	// ===================================================================
	// Error
	// ===================================================================

	public void error(String msg) {
		generalLogger.logIfEnabled(FQCN, Level.ERROR, null, msg);
	}

	public void error(final Object message) {
		generalLogger.logIfEnabled(FQCN, Level.ERROR, null, message, null);
	}

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

	public void error(String msg, Object... params) {
		generalLogger.logIfEnabled(FQCN, Level.ERROR, null, msg, params);
	}

	// ===================================================================
	// Warning
	// ===================================================================

	public void warn(String msg) {
		generalLogger.logIfEnabled(FQCN, Level.WARN, null, msg);
	}

	public void warn(final Object message) {
		generalLogger.logIfEnabled(FQCN, Level.WARN, null, message, null);
	}

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

	public void warn(String msg, Object... params) {
		generalLogger.logIfEnabled(FQCN, Level.WARN, null, msg, params);
	}

	// ===================================================================
	// Fatal
	// ===================================================================

	public void fatal(String msg) {
		generalLogger.logIfEnabled(FQCN, Level.FATAL, null, msg);
	}

	public void fatal(final Object message) {
		generalLogger.logIfEnabled(FQCN, Level.FATAL, null, message, null);
	}

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

	public void fatal(String msg, Object... params) {
		generalLogger.logIfEnabled(FQCN, Level.FATAL, null, msg, params);
	}

	// ===================================================================
	// Getter Setter
	// ===================================================================
	/**
	 * Returns Log Files Base Directory
	 * 
	 * @return log base directory path
	 */
	public String getLogBaseDir() {
		return organisedLogger.getLogBaseDir();
	}

	/**
	 * Get {@code Logger} object which is responsible for logging raw and error logs
	 * 
	 * @return General Logger
	 */
	public Logger getGeneralLogger() {
		return generalLogger;
	}

	/**
	 * Get {@code Logger} object which is responsible for logging summary logs
	 * 
	 * @return Summary Logger
	 */
	public Logger getSummaryLogger() {
		return summaryLogger;
	}

	/**
	 * Get {@code Logger} object which is responsible for logging realtime logs
	 * 
	 * @return RealTime Logger
	 */
	public Logger getRealTimeLogger() {
		return realTimeLogger;
	}

	/**
	 * Get all error log files relevant to current context (Includes .txt and .html
	 * files)
	 * 
	 * @return List of Error log files relevant to current context
	 */
	public List<File> getCurrentErrorLogFiles() {
		return organisedLogger.getCurrentErrorLogFiles();
	}

	/**
	 * Get all log files relevant to current context (Includes .txt and .html files)
	 * 
	 * @return List of log files relevant to current context
	 */
	public List<File> getCurrentGeneralLogFiles() {
		return organisedLogger.getCurrentGeneralLogFiles();
	}

	/**
	 * Get all summary log files relevant to current context (Includes .txt and
	 * .html files)
	 * 
	 * @return List of summary log files relevant to current context
	 */
	public List<File> getCurrentSummaryLogFiles() {
		return organisedLogger.getCurrentSummaryLogFiles();
	}

	/**
	 * Get all realtime log files relevant to current context (Includes .txt and
	 * .html files)
	 * 
	 * @return List of realtime log files relevant to current context
	 */
	public List<File> getCurrentRealTimeLogFiles() {
		return organisedLogger.getCurrentRealTimeLogFiles();
	}

}
