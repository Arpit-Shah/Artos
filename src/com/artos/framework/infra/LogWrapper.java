/*******************************************************************************
 * Copyright (C) 2018-2019 Arpit Shah
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

import com.artos.framework.FWStaticStore;
import com.relevantcodes.extentreports.ExtentReports;

/** Wrapper class which provides abstraction for logging mechanism */
public class LogWrapper {
	static final String FQCN = LogWrapper.class.getName();
	int threadNumber;
	LoggerContext loggerContext;
	Logger generalLogger;
	Logger summaryLogger;
	Logger realTimeLogger;
	ExtentReports extent = null;

	/**
	 * Constructor responsible for providing logWrapperObject per test suite
	 * thread, To ensure each thread logs in separate log file, each
	 * loggerContext object will fetch appropriate logger out of LoggerContext
	 * (which is global)
	 * 
	 * @param loggerContext
	 *            LoggerContext from log4j
	 * @param threadNumber
	 *            Thread number is used to uniquely identify loggers per thread,
	 *            user must ensure that each LogWrapper object is initialised
	 *            using unique threadNumber. If same threadNumber is used then
	 *            logs for different treads will be logged into same log file.
	 */
	public LogWrapper(LoggerContext loggerContext, int threadNumber) {
		this.loggerContext = loggerContext;
		this.threadNumber = threadNumber;
		setGeneralLogger(loggerContext.getLogger(OrganisedLog.GENERAL_LOGGER_NAME_STX + threadNumber));
		setSummaryLogger(loggerContext.getLogger(OrganisedLog.SUMMARY_LOGGER_NAME_STX + threadNumber));
		setRealTimeLogger(loggerContext.getLogger(OrganisedLog.REALTIME_LOGGER_NAME_STX + threadNumber));
		if (FWStaticStore.frameworkConfig.isEnableExtentReport()) {
			while (getCurrentGeneralLogFiles().isEmpty()) {
				// wait until file is created
				System.err.print(".");
			}
			String logFilePath = getCurrentGeneralLogFiles().get(0).getAbsolutePath();
			extent = new ExtentReports(logFilePath.substring(0, logFilePath.lastIndexOf(".")) + "-extent.html", true);
			extent.loadConfig(new File(FWStaticStore.CONFIG_BASE_DIR + File.separator + "extent_configuration.xml"));
		}
	}

	/** Disable Logging */
	public void disableGeneralLog() {
		getGeneralLogger().setLevel(Level.OFF);
	}

	/** Enable Logging */
	public void enableGeneralLog() {
		getGeneralLogger().setLevel(FWStaticStore.frameworkConfig.getLoglevelFromXML());
	}

	// ===================================================================
	// Trace
	// ===================================================================

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message
	 */
	public void trace(String msg) {
		generalLogger.logIfEnabled(FQCN, Level.TRACE, null, msg);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param message
	 *            The message
	 */
	public void trace(final Object message) {
		generalLogger.logIfEnabled(FQCN, Level.TRACE, null, message, null);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message
	 * @param t
	 *            the exception to log, including its stack trace.
	 */
	public void trace(Object msg, Throwable t) {
		generalLogger.logIfEnabled(FQCN, Level.TRACE, null, msg, t);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param p0
	 *            the message parameters
	 * @param p1
	 *            the message parameters
	 * @param p2
	 *            the message parameters
	 * @param p3
	 *            the message parameters
	 * @param p4
	 *            the message parameters
	 * @param p5
	 *            the message parameters
	 * @param p6
	 *            the message parameters
	 * @param p7
	 *            the message parameters
	 * @param p8
	 *            the message parameters
	 * @param p9
	 *            the message parameters
	 */
	public void trace(String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
		generalLogger.logIfEnabled(FQCN, Level.TRACE, null, msg, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param p0
	 *            the message parameters
	 * @param p1
	 *            the message parameters
	 * @param p2
	 *            the message parameters
	 * @param p3
	 *            the message parameters
	 * @param p4
	 *            the message parameters
	 * @param p5
	 *            the message parameters
	 * @param p6
	 *            the message parameters
	 * @param p7
	 *            the message parameters
	 * @param p8
	 *            the message parameters
	 */
	public void trace(String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
		generalLogger.logIfEnabled(FQCN, Level.TRACE, null, msg, p0, p1, p2, p3, p4, p5, p6, p7, p8);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param p0
	 *            the message parameters
	 * @param p1
	 *            the message parameters
	 * @param p2
	 *            the message parameters
	 * @param p3
	 *            the message parameters
	 * @param p4
	 *            the message parameters
	 * @param p5
	 *            the message parameters
	 * @param p6
	 *            the message parameters
	 * @param p7
	 *            the message parameters
	 */
	public void trace(String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
		generalLogger.logIfEnabled(FQCN, Level.TRACE, null, msg, p0, p1, p2, p3, p4, p5, p6, p7);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param p0
	 *            the message parameters
	 * @param p1
	 *            the message parameters
	 * @param p2
	 *            the message parameters
	 * @param p3
	 *            the message parameters
	 * @param p4
	 *            the message parameters
	 * @param p5
	 *            the message parameters
	 * @param p6
	 *            the message parameters
	 */
	public void trace(String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
		generalLogger.logIfEnabled(FQCN, Level.TRACE, null, msg, p0, p1, p2, p3, p4, p5, p6);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param p0
	 *            the message parameters
	 * @param p1
	 *            the message parameters
	 * @param p2
	 *            the message parameters
	 * @param p3
	 *            the message parameters
	 * @param p4
	 *            the message parameters
	 * @param p5
	 *            the message parameters
	 */
	public void trace(String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
		generalLogger.logIfEnabled(FQCN, Level.TRACE, null, msg, p0, p1, p2, p3, p4, p5);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param p0
	 *            the message parameters
	 * @param p1
	 *            the message parameters
	 * @param p2
	 *            the message parameters
	 * @param p3
	 *            the message parameters
	 * @param p4
	 *            the message parameters
	 */
	public void trace(String msg, Object p0, Object p1, Object p2, Object p3, Object p4) {
		generalLogger.logIfEnabled(FQCN, Level.TRACE, null, msg, p0, p1, p2, p3, p4);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param p0
	 *            the message parameters
	 * @param p1
	 *            the message parameters
	 * @param p2
	 *            the message parameters
	 * @param p3
	 *            the message parameters
	 */
	public void trace(String msg, Object p0, Object p1, Object p2, Object p3) {
		generalLogger.logIfEnabled(FQCN, Level.TRACE, null, msg, p0, p1, p2, p3);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param p0
	 *            the message parameters
	 * @param p1
	 *            the message parameters
	 * @param p2
	 *            the message parameters
	 */
	public void trace(String msg, Object p0, Object p1, Object p2) {
		generalLogger.logIfEnabled(FQCN, Level.TRACE, null, msg, p0, p1, p2);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param p0
	 *            the message parameters
	 * @param p1
	 *            the message parameters
	 */
	public void trace(String msg, Object p0, Object p1) {
		generalLogger.logIfEnabled(FQCN, Level.TRACE, null, msg, p0, p1);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param p0
	 *            the message parameters
	 */
	public void trace(String msg, Object p0) {
		generalLogger.logIfEnabled(FQCN, Level.TRACE, null, msg, p0);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param params
	 *            the message parameters
	 */
	public void trace(String msg, Object... params) {
		generalLogger.logIfEnabled(FQCN, Level.TRACE, null, msg, params);
	}

	// ===================================================================
	// Debug
	// ===================================================================

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message
	 */
	public void debug(String msg) {
		generalLogger.logIfEnabled(FQCN, Level.DEBUG, null, msg);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param message
	 *            The message
	 */
	public void debug(final Object message) {
		generalLogger.logIfEnabled(FQCN, Level.DEBUG, null, message, null);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message
	 * @param t
	 *            the exception to log, including its stack trace.
	 */
	public void debug(Object msg, Throwable t) {
		generalLogger.logIfEnabled(FQCN, Level.DEBUG, null, msg, t);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param p0
	 *            the message parameters
	 * @param p1
	 *            the message parameters
	 * @param p2
	 *            the message parameters
	 * @param p3
	 *            the message parameters
	 * @param p4
	 *            the message parameters
	 * @param p5
	 *            the message parameters
	 * @param p6
	 *            the message parameters
	 * @param p7
	 *            the message parameters
	 * @param p8
	 *            the message parameters
	 * @param p9
	 *            the message parameters
	 */
	public void debug(String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
		generalLogger.logIfEnabled(FQCN, Level.DEBUG, null, msg, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param p0
	 *            the message parameters
	 * @param p1
	 *            the message parameters
	 * @param p2
	 *            the message parameters
	 * @param p3
	 *            the message parameters
	 * @param p4
	 *            the message parameters
	 * @param p5
	 *            the message parameters
	 * @param p6
	 *            the message parameters
	 * @param p7
	 *            the message parameters
	 * @param p8
	 *            the message parameters
	 */
	public void debug(String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
		generalLogger.logIfEnabled(FQCN, Level.DEBUG, null, msg, p0, p1, p2, p3, p4, p5, p6, p7, p8);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param p0
	 *            the message parameters
	 * @param p1
	 *            the message parameters
	 * @param p2
	 *            the message parameters
	 * @param p3
	 *            the message parameters
	 * @param p4
	 *            the message parameters
	 * @param p5
	 *            the message parameters
	 * @param p6
	 *            the message parameters
	 * @param p7
	 *            the message parameters
	 */
	public void debug(String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
		generalLogger.logIfEnabled(FQCN, Level.DEBUG, null, msg, p0, p1, p2, p3, p4, p5, p6, p7);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param p0
	 *            the message parameters
	 * @param p1
	 *            the message parameters
	 * @param p2
	 *            the message parameters
	 * @param p3
	 *            the message parameters
	 * @param p4
	 *            the message parameters
	 * @param p5
	 *            the message parameters
	 * @param p6
	 *            the message parameters
	 */
	public void debug(String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
		generalLogger.logIfEnabled(FQCN, Level.DEBUG, null, msg, p0, p1, p2, p3, p4, p5, p6);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param p0
	 *            the message parameters
	 * @param p1
	 *            the message parameters
	 * @param p2
	 *            the message parameters
	 * @param p3
	 *            the message parameters
	 * @param p4
	 *            the message parameters
	 * @param p5
	 *            the message parameters
	 */
	public void debug(String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
		generalLogger.logIfEnabled(FQCN, Level.DEBUG, null, msg, p0, p1, p2, p3, p4, p5);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param p0
	 *            the message parameters
	 * @param p1
	 *            the message parameters
	 * @param p2
	 *            the message parameters
	 * @param p3
	 *            the message parameters
	 * @param p4
	 *            the message parameters
	 */
	public void debug(String msg, Object p0, Object p1, Object p2, Object p3, Object p4) {
		generalLogger.logIfEnabled(FQCN, Level.DEBUG, null, msg, p0, p1, p2, p3, p4);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param p0
	 *            the message parameters
	 * @param p1
	 *            the message parameters
	 * @param p2
	 *            the message parameters
	 * @param p3
	 *            the message parameters
	 */
	public void debug(String msg, Object p0, Object p1, Object p2, Object p3) {
		generalLogger.logIfEnabled(FQCN, Level.DEBUG, null, msg, p0, p1, p2, p3);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param p0
	 *            the message parameters
	 * @param p1
	 *            the message parameters
	 * @param p2
	 *            the message parameters
	 */
	public void debug(String msg, Object p0, Object p1, Object p2) {
		generalLogger.logIfEnabled(FQCN, Level.DEBUG, null, msg, p0, p1, p2);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param p0
	 *            the message parameters
	 * @param p1
	 *            the message parameters
	 */
	public void debug(String msg, Object p0, Object p1) {
		generalLogger.logIfEnabled(FQCN, Level.DEBUG, null, msg, p0, p1);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param p0
	 *            the message parameters
	 */
	public void debug(String msg, Object p0) {
		generalLogger.logIfEnabled(FQCN, Level.DEBUG, null, msg, p0);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param params
	 *            the message parameters
	 */
	public void debug(String msg, Object... params) {
		generalLogger.logIfEnabled(FQCN, Level.DEBUG, null, msg, params);
	}

	// ===================================================================
	// Info
	// ===================================================================

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message
	 */
	public void info(String msg) {
		generalLogger.logIfEnabled(FQCN, Level.INFO, null, msg);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param message
	 *            The message
	 */
	public void info(final Object message) {
		generalLogger.logIfEnabled(FQCN, Level.INFO, null, message, null);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message
	 * @param t
	 *            the exception to log, including its stack trace.
	 */
	public void info(Object msg, Throwable t) {
		generalLogger.logIfEnabled(FQCN, Level.INFO, null, msg, t);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param p0
	 *            the message parameters
	 * @param p1
	 *            the message parameters
	 * @param p2
	 *            the message parameters
	 * @param p3
	 *            the message parameters
	 * @param p4
	 *            the message parameters
	 * @param p5
	 *            the message parameters
	 * @param p6
	 *            the message parameters
	 * @param p7
	 *            the message parameters
	 * @param p8
	 *            the message parameters
	 * @param p9
	 *            the message parameters
	 */
	public void info(String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
		generalLogger.logIfEnabled(FQCN, Level.INFO, null, msg, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param p0
	 *            the message parameters
	 * @param p1
	 *            the message parameters
	 * @param p2
	 *            the message parameters
	 * @param p3
	 *            the message parameters
	 * @param p4
	 *            the message parameters
	 * @param p5
	 *            the message parameters
	 * @param p6
	 *            the message parameters
	 * @param p7
	 *            the message parameters
	 * @param p8
	 *            the message parameters
	 */
	public void info(String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
		generalLogger.logIfEnabled(FQCN, Level.INFO, null, msg, p0, p1, p2, p3, p4, p5, p6, p7, p8);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param p0
	 *            the message parameters
	 * @param p1
	 *            the message parameters
	 * @param p2
	 *            the message parameters
	 * @param p3
	 *            the message parameters
	 * @param p4
	 *            the message parameters
	 * @param p5
	 *            the message parameters
	 * @param p6
	 *            the message parameters
	 * @param p7
	 *            the message parameters
	 */
	public void info(String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
		generalLogger.logIfEnabled(FQCN, Level.INFO, null, msg, p0, p1, p2, p3, p4, p5, p6, p7);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param p0
	 *            the message parameters
	 * @param p1
	 *            the message parameters
	 * @param p2
	 *            the message parameters
	 * @param p3
	 *            the message parameters
	 * @param p4
	 *            the message parameters
	 * @param p5
	 *            the message parameters
	 * @param p6
	 *            the message parameters
	 */
	public void info(String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
		generalLogger.logIfEnabled(FQCN, Level.INFO, null, msg, p0, p1, p2, p3, p4, p5, p6);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param p0
	 *            the message parameters
	 * @param p1
	 *            the message parameters
	 * @param p2
	 *            the message parameters
	 * @param p3
	 *            the message parameters
	 * @param p4
	 *            the message parameters
	 * @param p5
	 *            the message parameters
	 */
	public void info(String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
		generalLogger.logIfEnabled(FQCN, Level.INFO, null, msg, p0, p1, p2, p3, p4, p5);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param p0
	 *            the message parameters
	 * @param p1
	 *            the message parameters
	 * @param p2
	 *            the message parameters
	 * @param p3
	 *            the message parameters
	 * @param p4
	 *            the message parameters
	 */
	public void info(String msg, Object p0, Object p1, Object p2, Object p3, Object p4) {
		generalLogger.logIfEnabled(FQCN, Level.INFO, null, msg, p0, p1, p2, p3, p4);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param p0
	 *            the message parameters
	 * @param p1
	 *            the message parameters
	 * @param p2
	 *            the message parameters
	 * @param p3
	 *            the message parameters
	 */
	public void info(String msg, Object p0, Object p1, Object p2, Object p3) {
		generalLogger.logIfEnabled(FQCN, Level.INFO, null, msg, p0, p1, p2, p3);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param p0
	 *            the message parameters
	 * @param p1
	 *            the message parameters
	 * @param p2
	 *            the message parameters
	 */
	public void info(String msg, Object p0, Object p1, Object p2) {
		generalLogger.logIfEnabled(FQCN, Level.INFO, null, msg, p0, p1, p2);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param p0
	 *            the message parameters
	 * @param p1
	 *            the message parameters
	 */
	public void info(String msg, Object p0, Object p1) {
		generalLogger.logIfEnabled(FQCN, Level.INFO, null, msg, p0, p1);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param p0
	 *            the message parameters
	 */
	public void info(String msg, Object p0) {
		generalLogger.logIfEnabled(FQCN, Level.INFO, null, msg, p0);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param params
	 *            the message parameters
	 */
	public void info(String msg, Object... params) {
		generalLogger.logIfEnabled(FQCN, Level.INFO, null, msg, params);
	}

	// ===================================================================
	// Error
	// ===================================================================

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message
	 */
	public void error(String msg) {
		generalLogger.logIfEnabled(FQCN, Level.ERROR, null, msg);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param message
	 *            The message
	 */
	public void error(final Object message) {
		generalLogger.logIfEnabled(FQCN, Level.ERROR, null, message, null);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message
	 * @param t
	 *            the exception to log, including its stack trace.
	 */
	public void error(Object msg, Throwable t) {
		generalLogger.logIfEnabled(FQCN, Level.ERROR, null, msg, t);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param p0
	 *            the message parameters
	 * @param p1
	 *            the message parameters
	 * @param p2
	 *            the message parameters
	 * @param p3
	 *            the message parameters
	 * @param p4
	 *            the message parameters
	 * @param p5
	 *            the message parameters
	 * @param p6
	 *            the message parameters
	 * @param p7
	 *            the message parameters
	 * @param p8
	 *            the message parameters
	 * @param p9
	 *            the message parameters
	 */
	public void error(String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
		generalLogger.logIfEnabled(FQCN, Level.ERROR, null, msg, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param p0
	 *            the message parameters
	 * @param p1
	 *            the message parameters
	 * @param p2
	 *            the message parameters
	 * @param p3
	 *            the message parameters
	 * @param p4
	 *            the message parameters
	 * @param p5
	 *            the message parameters
	 * @param p6
	 *            the message parameters
	 * @param p7
	 *            the message parameters
	 * @param p8
	 *            the message parameters
	 */
	public void error(String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
		generalLogger.logIfEnabled(FQCN, Level.ERROR, null, msg, p0, p1, p2, p3, p4, p5, p6, p7, p8);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param p0
	 *            the message parameters
	 * @param p1
	 *            the message parameters
	 * @param p2
	 *            the message parameters
	 * @param p3
	 *            the message parameters
	 * @param p4
	 *            the message parameters
	 * @param p5
	 *            the message parameters
	 * @param p6
	 *            the message parameters
	 * @param p7
	 *            the message parameters
	 */
	public void error(String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
		generalLogger.logIfEnabled(FQCN, Level.ERROR, null, msg, p0, p1, p2, p3, p4, p5, p6, p7);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param p0
	 *            the message parameters
	 * @param p1
	 *            the message parameters
	 * @param p2
	 *            the message parameters
	 * @param p3
	 *            the message parameters
	 * @param p4
	 *            the message parameters
	 * @param p5
	 *            the message parameters
	 * @param p6
	 *            the message parameters
	 */
	public void error(String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
		generalLogger.logIfEnabled(FQCN, Level.ERROR, null, msg, p0, p1, p2, p3, p4, p5, p6);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param p0
	 *            the message parameters
	 * @param p1
	 *            the message parameters
	 * @param p2
	 *            the message parameters
	 * @param p3
	 *            the message parameters
	 * @param p4
	 *            the message parameters
	 * @param p5
	 *            the message parameters
	 */
	public void error(String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
		generalLogger.logIfEnabled(FQCN, Level.ERROR, null, msg, p0, p1, p2, p3, p4, p5);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param p0
	 *            the message parameters
	 * @param p1
	 *            the message parameters
	 * @param p2
	 *            the message parameters
	 * @param p3
	 *            the message parameters
	 * @param p4
	 *            the message parameters
	 */
	public void error(String msg, Object p0, Object p1, Object p2, Object p3, Object p4) {
		generalLogger.logIfEnabled(FQCN, Level.ERROR, null, msg, p0, p1, p2, p3, p4);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param p0
	 *            the message parameters
	 * @param p1
	 *            the message parameters
	 * @param p2
	 *            the message parameters
	 * @param p3
	 *            the message parameters
	 */
	public void error(String msg, Object p0, Object p1, Object p2, Object p3) {
		generalLogger.logIfEnabled(FQCN, Level.ERROR, null, msg, p0, p1, p2, p3);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param p0
	 *            the message parameters
	 * @param p1
	 *            the message parameters
	 * @param p2
	 *            the message parameters
	 */
	public void error(String msg, Object p0, Object p1, Object p2) {
		generalLogger.logIfEnabled(FQCN, Level.ERROR, null, msg, p0, p1, p2);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param p0
	 *            the message parameters
	 * @param p1
	 *            the message parameters
	 */
	public void error(String msg, Object p0, Object p1) {
		generalLogger.logIfEnabled(FQCN, Level.ERROR, null, msg, p0, p1);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param p0
	 *            the message parameters
	 */
	public void error(String msg, Object p0) {
		generalLogger.logIfEnabled(FQCN, Level.ERROR, null, msg, p0);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param params
	 *            the message parameters
	 */
	public void error(String msg, Object... params) {
		generalLogger.logIfEnabled(FQCN, Level.ERROR, null, msg, params);
	}

	// ===================================================================
	// Warning
	// ===================================================================

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message
	 */
	public void warn(String msg) {
		generalLogger.logIfEnabled(FQCN, Level.WARN, null, msg);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param message
	 *            The message
	 */
	public void warn(final Object message) {
		generalLogger.logIfEnabled(FQCN, Level.WARN, null, message, null);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message
	 * @param t
	 *            the exception to log, including its stack trace.
	 */
	public void warn(Object msg, Throwable t) {
		generalLogger.logIfEnabled(FQCN, Level.WARN, null, msg, t);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param p0
	 *            the message parameters
	 * @param p1
	 *            the message parameters
	 * @param p2
	 *            the message parameters
	 * @param p3
	 *            the message parameters
	 * @param p4
	 *            the message parameters
	 * @param p5
	 *            the message parameters
	 * @param p6
	 *            the message parameters
	 * @param p7
	 *            the message parameters
	 * @param p8
	 *            the message parameters
	 * @param p9
	 *            the message parameters
	 */
	public void warn(String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
		generalLogger.logIfEnabled(FQCN, Level.WARN, null, msg, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param p0
	 *            the message parameters
	 * @param p1
	 *            the message parameters
	 * @param p2
	 *            the message parameters
	 * @param p3
	 *            the message parameters
	 * @param p4
	 *            the message parameters
	 * @param p5
	 *            the message parameters
	 * @param p6
	 *            the message parameters
	 * @param p7
	 *            the message parameters
	 * @param p8
	 *            the message parameters
	 */
	public void warn(String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
		generalLogger.logIfEnabled(FQCN, Level.WARN, null, msg, p0, p1, p2, p3, p4, p5, p6, p7, p8);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param p0
	 *            the message parameters
	 * @param p1
	 *            the message parameters
	 * @param p2
	 *            the message parameters
	 * @param p3
	 *            the message parameters
	 * @param p4
	 *            the message parameters
	 * @param p5
	 *            the message parameters
	 * @param p6
	 *            the message parameters
	 * @param p7
	 *            the message parameters
	 */
	public void warn(String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
		generalLogger.logIfEnabled(FQCN, Level.WARN, null, msg, p0, p1, p2, p3, p4, p5, p6, p7);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param p0
	 *            the message parameters
	 * @param p1
	 *            the message parameters
	 * @param p2
	 *            the message parameters
	 * @param p3
	 *            the message parameters
	 * @param p4
	 *            the message parameters
	 * @param p5
	 *            the message parameters
	 * @param p6
	 *            the message parameters
	 */
	public void warn(String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
		generalLogger.logIfEnabled(FQCN, Level.WARN, null, msg, p0, p1, p2, p3, p4, p5, p6);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param p0
	 *            the message parameters
	 * @param p1
	 *            the message parameters
	 * @param p2
	 *            the message parameters
	 * @param p3
	 *            the message parameters
	 * @param p4
	 *            the message parameters
	 * @param p5
	 *            the message parameters
	 */
	public void warn(String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
		generalLogger.logIfEnabled(FQCN, Level.WARN, null, msg, p0, p1, p2, p3, p4, p5);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param p0
	 *            the message parameters
	 * @param p1
	 *            the message parameters
	 * @param p2
	 *            the message parameters
	 * @param p3
	 *            the message parameters
	 * @param p4
	 *            the message parameters
	 */
	public void warn(String msg, Object p0, Object p1, Object p2, Object p3, Object p4) {
		generalLogger.logIfEnabled(FQCN, Level.WARN, null, msg, p0, p1, p2, p3, p4);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param p0
	 *            the message parameters
	 * @param p1
	 *            the message parameters
	 * @param p2
	 *            the message parameters
	 * @param p3
	 *            the message parameters
	 */
	public void warn(String msg, Object p0, Object p1, Object p2, Object p3) {
		generalLogger.logIfEnabled(FQCN, Level.WARN, null, msg, p0, p1, p2, p3);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param p0
	 *            the message parameters
	 * @param p1
	 *            the message parameters
	 * @param p2
	 *            the message parameters
	 */
	public void warn(String msg, Object p0, Object p1, Object p2) {
		generalLogger.logIfEnabled(FQCN, Level.WARN, null, msg, p0, p1, p2);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param p0
	 *            the message parameters
	 * @param p1
	 *            the message parameters
	 */
	public void warn(String msg, Object p0, Object p1) {
		generalLogger.logIfEnabled(FQCN, Level.WARN, null, msg, p0, p1);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param p0
	 *            the message parameters
	 */
	public void warn(String msg, Object p0) {
		generalLogger.logIfEnabled(FQCN, Level.WARN, null, msg, p0);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param params
	 *            the message parameters
	 */
	public void warn(String msg, Object... params) {
		generalLogger.logIfEnabled(FQCN, Level.WARN, null, msg, params);
	}

	// ===================================================================
	// Fatal
	// ===================================================================

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message
	 */
	public void fatal(String msg) {
		generalLogger.logIfEnabled(FQCN, Level.FATAL, null, msg);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param message
	 *            The message
	 */
	public void fatal(final Object message) {
		generalLogger.logIfEnabled(FQCN, Level.FATAL, null, message, null);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message
	 * @param t
	 *            the exception to log, including its stack trace.
	 */
	public void fatal(Object msg, Throwable t) {
		generalLogger.logIfEnabled(FQCN, Level.FATAL, null, msg, t);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param p0
	 *            the message parameters
	 * @param p1
	 *            the message parameters
	 * @param p2
	 *            the message parameters
	 * @param p3
	 *            the message parameters
	 * @param p4
	 *            the message parameters
	 * @param p5
	 *            the message parameters
	 * @param p6
	 *            the message parameters
	 * @param p7
	 *            the message parameters
	 * @param p8
	 *            the message parameters
	 * @param p9
	 *            the message parameters
	 */
	public void fatal(String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
		generalLogger.logIfEnabled(FQCN, Level.FATAL, null, msg, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param p0
	 *            the message parameters
	 * @param p1
	 *            the message parameters
	 * @param p2
	 *            the message parameters
	 * @param p3
	 *            the message parameters
	 * @param p4
	 *            the message parameters
	 * @param p5
	 *            the message parameters
	 * @param p6
	 *            the message parameters
	 * @param p7
	 *            the message parameters
	 * @param p8
	 *            the message parameters
	 */
	public void fatal(String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
		generalLogger.logIfEnabled(FQCN, Level.FATAL, null, msg, p0, p1, p2, p3, p4, p5, p6, p7, p8);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param p0
	 *            the message parameters
	 * @param p1
	 *            the message parameters
	 * @param p2
	 *            the message parameters
	 * @param p3
	 *            the message parameters
	 * @param p4
	 *            the message parameters
	 * @param p5
	 *            the message parameters
	 * @param p6
	 *            the message parameters
	 * @param p7
	 *            the message parameters
	 */
	public void fatal(String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
		generalLogger.logIfEnabled(FQCN, Level.FATAL, null, msg, p0, p1, p2, p3, p4, p5, p6, p7);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param p0
	 *            the message parameters
	 * @param p1
	 *            the message parameters
	 * @param p2
	 *            the message parameters
	 * @param p3
	 *            the message parameters
	 * @param p4
	 *            the message parameters
	 * @param p5
	 *            the message parameters
	 * @param p6
	 *            the message parameters
	 */
	public void fatal(String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
		generalLogger.logIfEnabled(FQCN, Level.FATAL, null, msg, p0, p1, p2, p3, p4, p5, p6);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param p0
	 *            the message parameters
	 * @param p1
	 *            the message parameters
	 * @param p2
	 *            the message parameters
	 * @param p3
	 *            the message parameters
	 * @param p4
	 *            the message parameters
	 * @param p5
	 *            the message parameters
	 */
	public void fatal(String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
		generalLogger.logIfEnabled(FQCN, Level.FATAL, null, msg, p0, p1, p2, p3, p4, p5);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param p0
	 *            the message parameters
	 * @param p1
	 *            the message parameters
	 * @param p2
	 *            the message parameters
	 * @param p3
	 *            the message parameters
	 * @param p4
	 *            the message parameters
	 */
	public void fatal(String msg, Object p0, Object p1, Object p2, Object p3, Object p4) {
		generalLogger.logIfEnabled(FQCN, Level.FATAL, null, msg, p0, p1, p2, p3, p4);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param p0
	 *            the message parameters
	 * @param p1
	 *            the message parameters
	 * @param p2
	 *            the message parameters
	 * @param p3
	 *            the message parameters
	 */
	public void fatal(String msg, Object p0, Object p1, Object p2, Object p3) {
		generalLogger.logIfEnabled(FQCN, Level.FATAL, null, msg, p0, p1, p2, p3);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param p0
	 *            the message parameters
	 * @param p1
	 *            the message parameters
	 * @param p2
	 *            the message parameters
	 */
	public void fatal(String msg, Object p0, Object p1, Object p2) {
		generalLogger.logIfEnabled(FQCN, Level.FATAL, null, msg, p0, p1, p2);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param p0
	 *            the message parameters
	 * @param p1
	 *            the message parameters
	 */
	public void fatal(String msg, Object p0, Object p1) {
		generalLogger.logIfEnabled(FQCN, Level.FATAL, null, msg, p0, p1);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param p0
	 *            the message parameters
	 */
	public void fatal(String msg, Object p0) {
		generalLogger.logIfEnabled(FQCN, Level.FATAL, null, msg, p0);
	}

	/**
	 * Logs a message if the specified level is active.
	 * 
	 * @param msg
	 *            The message format
	 * @param params
	 *            the message parameters
	 */
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

	public ExtentReports getExtent() {
		return extent;
	}

	public void setExtent(ExtentReports extent) {
		this.extent = extent;
	}
}
