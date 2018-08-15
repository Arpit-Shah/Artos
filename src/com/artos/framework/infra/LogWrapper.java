// Copyright <2018> <Artos>

// Permission is hereby granted, free of charge, to any person obtaining a copy of this software
// and associated documentation files (the "Software"), to deal in the Software without restriction,
// including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
// and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
// subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
// 
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
// INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
// IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
// WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
// OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
package com.artos.framework.infra;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.RollingFileAppender;

/** Wrapper class which provides abstraction for logging mechanism */
public class LogWrapper {
	static final String FQCN = LogWrapper.class.getName();
	int threadNumber;
	LoggerContext loggerContext;
	Logger generalLogger;
	Logger summaryLogger;
	Logger realTimeLogger;

	/**
	 * Constructor for LogWrapper. This class accepts LoggerContext from Log4j
	 * and pull GeneralLogger, SummaryLogger and RealTimeLogger for given thread
	 * value
	 * 
	 * @param loggerContext
	 *            LoggerContext from log4j
	 * @param threadNumber
	 *            Thread sequence number for assigning logger
	 */
	public LogWrapper(LoggerContext loggerContext, int threadNumber) {
		this.loggerContext = loggerContext;
		setGeneralLogger(loggerContext.getLogger(OrganisedLog.GENERAL_LOGGER_NAME_STX + threadNumber));
		setSummaryLogger(loggerContext.getLogger(OrganisedLog.SUMMARY_LOGGER_NAME_STX + threadNumber));
		setRealTimeLogger(loggerContext.getLogger(OrganisedLog.REALTIME_LOGGER_NAME_STX + threadNumber));
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
	// Log files tracker
	// ===================================================================

	/**
	 * Get all log files relevant to current context (Includes .txt and .html
	 * files)
	 * 
	 * @return List of log files relevant to current context
	 */
	public List<File> getCurrentGeneralLogFiles() {
		List<File> textLog = new ArrayList<File>();
		List<File> htmlLog = new ArrayList<File>();
		getFilesFromGeneralLogAppenders("all-log-text" + getThreadNumber(), textLog);
		getFilesFromGeneralLogAppenders("all-log-html" + getThreadNumber(), htmlLog);
		List<File> mergedList = new ArrayList<>();
		if (null != textLog) {
			mergedList.addAll(textLog);
		}
		if (null != htmlLog) {
			mergedList.addAll(htmlLog);
		}
		return mergedList;
	}

	/**
	 * Get all summary log files relevant to current context (Includes .txt and
	 * .html files)
	 * 
	 * @return List of summary log files relevant to current context
	 */
	public List<File> getCurrentSummaryLogFiles() {
		List<File> textLog = new ArrayList<File>();
		List<File> htmlLog = new ArrayList<File>();
		getFilesFromSummaryLogAppenders("summary-log-text" + getThreadNumber(), textLog);
		getFilesFromSummaryLogAppenders("summary-log-html" + getThreadNumber(), htmlLog);
		List<File> mergedList = new ArrayList<>();
		if (null != textLog) {
			mergedList.addAll(textLog);
		}
		if (null != htmlLog) {
			mergedList.addAll(htmlLog);
		}
		return mergedList;
	}

	/**
	 * Get all realTime log files relevant to current context (Includes .txt and
	 * .html files)
	 * 
	 * @return List of realtime log files relevant to current context
	 */
	public List<File> getCurrentRealTimeLogFiles() {
		List<File> textLog = new ArrayList<File>();
		List<File> htmlLog = new ArrayList<File>();
		getFilesFromRealTimeLogAppenders("realtime-log-text" + getThreadNumber(), textLog);
		getFilesFromRealTimeLogAppenders("realtime-log-html" + getThreadNumber(), htmlLog);
		List<File> mergedList = new ArrayList<>();
		if (null != textLog) {
			mergedList.addAll(textLog);
		}
		if (null != htmlLog) {
			mergedList.addAll(htmlLog);
		}
		return mergedList;
	}

	/**
	 * Provides list of current general log files attached to provided appender
	 * name
	 * 
	 * @param appenderName
	 *            Appender Name
	 * @param textLog
	 *            File List
	 */
	private void getFilesFromGeneralLogAppenders(String appenderName, List<File> textLog) {
		Map<String, Appender> appenders = generalLogger.getAppenders();
		Iterator<Entry<String, Appender>> it = appenders.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Appender> pair = (Map.Entry<String, Appender>) it.next();

			generalLogger.trace(pair.getKey() + "=" + pair.getValue());
			if (pair.getValue() instanceof RollingFileAppender) {
				if (appenderName.equals(pair.getValue().getName())) {
					String appender = ((RollingFileAppender) pair.getValue()).getFileName();
					textLog.add(new File(appender));
					generalLogger.trace(((RollingFileAppender) pair.getValue()).getFileName());
				}
			}
		}
	}

	/**
	 * Provides list of current summary log files attached to provided appender
	 * name
	 * 
	 * @param appenderName
	 *            Appender Name
	 * @param textLog
	 *            File List
	 */
	private void getFilesFromSummaryLogAppenders(String appenderName, List<File> logFiles) {
		Map<String, Appender> appenders = summaryLogger.getAppenders();
		Iterator<Entry<String, Appender>> it = appenders.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Appender> pair = (Map.Entry<String, Appender>) it.next();
			if (pair.getValue() instanceof RollingFileAppender) {
				if (appenderName.equals(pair.getValue().getName())) {
					logFiles.add(new File(((RollingFileAppender) pair.getValue()).getFileName()));
				}
			}
		}
	}

	/**
	 * Provides list of current realtime log files attached to provided appender
	 * name
	 * 
	 * @param appenderName
	 *            Appender Name
	 * @param textLog
	 *            File List
	 */
	private void getFilesFromRealTimeLogAppenders(String appenderName, List<File> logFiles) {
		Map<String, Appender> appenders = realTimeLogger.getAppenders();
		Iterator<Entry<String, Appender>> it = appenders.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Appender> pair = (Map.Entry<String, Appender>) it.next();
			if (pair.getValue() instanceof RollingFileAppender) {
				if (appenderName.equals(pair.getValue().getName())) {
					logFiles.add(new File(((RollingFileAppender) pair.getValue()).getFileName()));
				}
			}
		}
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

	public Logger getRealTimeLogger() {
		return realTimeLogger;
	}

	public void setRealTimeLogger(Logger realTimeLogger) {
		this.realTimeLogger = realTimeLogger;
	}

	public int getThreadNumber() {
		return threadNumber;
	}

	public void setThreadNumber(int threadNumber) {
		this.threadNumber = threadNumber;
	}
}
