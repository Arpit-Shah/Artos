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
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.AppenderRefComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.api.LayoutComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.LoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.RootLoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

import com.artos.framework.FWStaticStore;
import com.artos.framework.xml.TestSuite;

/**
 * This class is responsible for initialising all log streams which may require
 * during test suite execution
 */
class OrganisedLog {

	private LoggerContext loggerContext;
	private String logBaseDir;
	public static String GENERAL_LOGGER_NAME_STX = "General_";
	public static String SUMMARY_LOGGER_NAME_STX = "Summary_";
	public static String REALTIME_LOGGER_NAME_STX = "RealTime_";

	/**
	 * Constructor responsible for creating log base directory and Log4J
	 * loggerContext using Framework configuration and TestSuite information
	 * 
	 * @param logDirPath
	 *            Log base directory absolute or reference path
	 * @param testCaseFQCN
	 *            Test case fully qualified class name (Example :
	 *            com.test.unit.Abc)
	 * @param enableLogDecoration
	 *            Enables/disable log decoration (Time-stamp, source package,
	 *            thread number etc..)
	 * @param enableTextLog
	 *            Enables/disable text log
	 * @param enableHTMLLog
	 *            Enable/disable HTML log
	 * @param testSuiteList
	 *            list of testSuite (if any) or pass null
	 */
	public OrganisedLog(String logDirPath, String testCaseFQCN, boolean enableLogDecoration, boolean enableTextLog, boolean enableHTMLLog,
			List<TestSuite> testSuiteList) {

		// System.setProperty("log4j.configurationFile",
		// "./conf/log4j2.properties");
		
		if(null == logDirPath){
			logDirPath = FWStaticStore.LOG_BASE_DIR;
		}

		if (!logDirPath.endsWith("/") && !logDirPath.endsWith("\\")) {
			logDirPath = logDirPath + File.separator;
		}
		
		setLogBaseDir(logDirPath + testCaseFQCN + File.separator);
		setLoggerContext(dynamicallyConfigureLog4J(getLogBaseDir(), testCaseFQCN, enableLogDecoration, enableTextLog, enableHTMLLog, testSuiteList));
	}

	/**
	 * Dynamically Generates and applies Log4J configuration XML as per test
	 * suite requirement
	 * 
	 * @param baseDir
	 *            Log file root directory
	 * @param testFQCN
	 *            Log file name
	 * @param enableLogDecoration
	 *            Enable/disable log decoration (Time-stamp, source package,
	 *            thread number etc..)
	 * @param enableTextLog
	 *            Enables/disable text log
	 * @param enableHTMLLog
	 *            Enable/disable HTML log
	 * @param testSuiteList
	 *            testSuiteLite (if any) or pass null
	 * @return {@code LoggerContext}
	 * 
	 * @see LoggerContext
	 */
	private LoggerContext dynamicallyConfigureLog4J(String baseDir, String testFQCN, boolean enableLogDecoration, boolean enableTextLog,
			boolean enableHTMLLog, List<TestSuite> testSuiteList) {

		int numberOfAppenders = 1;

		if (!baseDir.endsWith("/") && !baseDir.endsWith("\\")) {
			baseDir = baseDir + File.separator;
		}

		// If test script is not passed from command line then testSuite can be
		// null
		if (null != testSuiteList && !testSuiteList.isEmpty()) {
			numberOfAppenders = testSuiteList.size();
		}

		ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
		builder.setStatusLevel(Level.WARN);
		builder.setConfigurationName("RollingBuilder");

		// Log Layout for general logs
		LayoutComponentBuilder logFileLayout = builder.newLayout("PatternLayout");
		// Log Layout for real time logs
		LayoutComponentBuilder realTimeLogLayout = builder.newLayout("PatternLayout");
		// Log Layout for summary file
		LayoutComponentBuilder summaryFileLayout = builder.newLayout("PatternLayout");
		/*
		* @formatter:off
		*
		* %highlight{} Highlights log levels in different colour
		* [%-5level] = Log level upto 5 char max
		* [%d{yyyy-MM-dd_HH:mm:ss.SSS}] = Date and time 
		* [%t] = Thread number
		* [%F] = File where logs are coming from
		* [%M] = Method which generated log
		* [%c{-1}] = ClassName which issued logCommand 
		* %msg = Actual msg to be logged 
		* %n = new line
		* %throwable = log exception
		* 
		* @formatter:on
		*/
		{
			if (enableLogDecoration) {
				logFileLayout.addAttribute("pattern", "%highlight{[%-5level][%d{yyyy-MM-dd_HH:mm:ss.SSS}][%t][%F][%M][%c{1}] - %msg%n%throwable}");
			} else {
				logFileLayout.addAttribute("pattern", "%highlight{%msg%n%throwable}");
			}

			realTimeLogLayout.addAttribute("pattern", "[%-5level][%d{yyyy-MM-dd_HH:mm:ss.SSS}][%t] - %msg%n%throwable");
			summaryFileLayout.addAttribute("pattern", "%msg%n%throwable");
		}

		// HTML Log Layout for general logs
		LayoutComponentBuilder htmlLogFileLayout = builder.newLayout("HTMLLayout");
		htmlLogFileLayout.addAttribute("title", "Test Logs");
		// expensive if enabled
		htmlLogFileLayout.addAttribute("locationInfo", false);
		htmlLogFileLayout.addAttribute("fontSize", "small");

		// HTML Log Layout for summary logs
		LayoutComponentBuilder htmlSummaryFileLayout = builder.newLayout("HTMLLayout");
		htmlSummaryFileLayout.addAttribute("title", "Test Summary");
		// expensive if enabled
		htmlSummaryFileLayout.addAttribute("locationInfo", false);
		htmlLogFileLayout.addAttribute("fontSize", "small");

		// HTML Log Layout for realtime logs
		LayoutComponentBuilder htmlrealTimeLogLayout = builder.newLayout("HTMLLayout");
		htmlrealTimeLogLayout.addAttribute("title", "Test Summary");
		// expensive if enabled
		htmlrealTimeLogLayout.addAttribute("locationInfo", false);
		htmlrealTimeLogLayout.addAttribute("fontSize", "small");

		// File size based rollver Trigger Policy
		ComponentBuilder<?> triggeringPolicy = builder.newComponent("Policies");
		triggeringPolicy.addComponent(builder.newComponent("SizeBasedTriggeringPolicy").addAttribute("size", "10MB"));

		// create a console appender
		{
			AppenderComponentBuilder appenderBuilder1 = builder.newAppender("console-log", "CONSOLE");
			appenderBuilder1.addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT);
			appenderBuilder1.add(logFileLayout);
			builder.add(appenderBuilder1);
		}

		for (int i = 0; i < numberOfAppenders; i++) {
			String logFileName = testFQCN + "_" + i + "_" + System.currentTimeMillis();

			// create a rolling file appender for general logs
			{
				// Text
				AppenderComponentBuilder appenderBuilder2 = builder.newAppender("all-log-text" + i, "RollingFile");
				appenderBuilder2.addAttribute("fileName", baseDir + logFileName + "-all.log");
				appenderBuilder2.addAttribute("filePattern", baseDir + logFileName + "-all-%d{yyyy-MM-dd_HH.mm.ss.SSS}.log");
				appenderBuilder2.add(logFileLayout);
				appenderBuilder2.addComponent(triggeringPolicy);
				if (enableTextLog) {
					builder.add(appenderBuilder2);
				}

				// HTML
				AppenderComponentBuilder appenderBuilder3 = builder.newAppender("all-log-html" + i, "RollingFile");
				appenderBuilder3.addAttribute("fileName", baseDir + logFileName + "-all.html");
				appenderBuilder3.addAttribute("filePattern", baseDir + logFileName + "-all-%d{yyyy-MM-dd_HH.mm.ss.SSS}.html");
				appenderBuilder3.add(htmlLogFileLayout);
				appenderBuilder3.addComponent(triggeringPolicy);
				if (enableHTMLLog) {
					builder.add(appenderBuilder3);
				}
			}

			// create a rolling file appender for summary
			{
				// Text
				AppenderComponentBuilder appenderBuilder6 = builder.newAppender("summary-log-text" + i, "RollingFile");
				appenderBuilder6.addAttribute("fileName", baseDir + logFileName + "-summary.log");
				appenderBuilder6.addAttribute("filePattern", baseDir + logFileName + "-summary-%d{yyyy-MM-dd_HH.mm.ss.SSS}.log");
				appenderBuilder6.add(summaryFileLayout);
				appenderBuilder6.addComponent(triggeringPolicy);
				if (enableTextLog) {
					builder.add(appenderBuilder6);
				}

				// HTML
				AppenderComponentBuilder appenderBuilder7 = builder.newAppender("summary-log-html" + i, "RollingFile");
				appenderBuilder7.addAttribute("fileName", baseDir + logFileName + "-summary.html");
				appenderBuilder7.addAttribute("filePattern", baseDir + logFileName + "-summary-%d{yyyy-MM-dd_HH.mm.ss.SSS}.html");
				appenderBuilder7.add(htmlSummaryFileLayout);
				appenderBuilder7.addComponent(triggeringPolicy);
				if (enableHTMLLog) {
					builder.add(appenderBuilder7);
				}
			}

			// create a rolling file appender for real time logs
			{
				// Text
				AppenderComponentBuilder appenderBuilder8 = builder.newAppender("realtime-log-text" + i, "RollingFile");
				appenderBuilder8.addAttribute("fileName", baseDir + logFileName + "-realtime.log");
				appenderBuilder8.addAttribute("filePattern", baseDir + logFileName + "-realtime-%d{yyyy-MM-dd_HH.mm.ss.SSS}.log");
				appenderBuilder8.add(realTimeLogLayout);
				appenderBuilder8.addComponent(triggeringPolicy);
				if (enableTextLog) {
					builder.add(appenderBuilder8);
				}

				// HTML
				AppenderComponentBuilder appenderBuilder9 = builder.newAppender("realtime-log-html" + i, "RollingFile");
				appenderBuilder9.addAttribute("fileName", baseDir + logFileName + "-realtime.html");
				appenderBuilder9.addAttribute("filePattern", baseDir + logFileName + "-realtime-%d{yyyy-MM-dd_HH.mm.ss.SSS}.html");
				appenderBuilder9.add(htmlrealTimeLogLayout);
				appenderBuilder9.addComponent(triggeringPolicy);
				if (enableHTMLLog) {
					builder.add(appenderBuilder9);
				}
			}

			String generalLoggerName = GENERAL_LOGGER_NAME_STX + i;
			String summaryLoggerName = SUMMARY_LOGGER_NAME_STX + i;
			String realtimeLoggerName = REALTIME_LOGGER_NAME_STX + i;

			// create the new logger
			Level loglevel = FWStaticStore.frameworkConfig.getLoglevelFromXML();
			LoggerComponentBuilder generalLoggerBuilder = builder.newLogger(generalLoggerName, loglevel);
			{
				generalLoggerBuilder.addAttribute("additivity", false);
				AppenderRefComponentBuilder appendRef1 = builder.newAppenderRef("console-log");
				appendRef1.addAttribute("level", Level.ALL);
				AppenderRefComponentBuilder appendRef2 = builder.newAppenderRef("all-log-text" + i);
				appendRef2.addAttribute("level", Level.ALL);
				AppenderRefComponentBuilder appendRef3 = builder.newAppenderRef("all-log-html" + i);
				appendRef3.addAttribute("level", Level.ALL);

				generalLoggerBuilder.add(appendRef1);

				if (enableTextLog) {
					generalLoggerBuilder.add(appendRef2);
				}
				if (enableHTMLLog) {
					generalLoggerBuilder.add(appendRef3);
				}
			}

			// create new logger for summary
			LoggerComponentBuilder summaryLoggerBuilder = builder.newLogger(summaryLoggerName, Level.ALL);
			summaryLoggerBuilder.addAttribute("additivity", false);
			{
				AppenderRefComponentBuilder appendRef11 = builder.newAppenderRef("summary-log-text" + i);
				appendRef11.addAttribute("level", Level.ALL);
				AppenderRefComponentBuilder appendRef12 = builder.newAppenderRef("summary-log-html" + i);
				appendRef12.addAttribute("level", Level.ALL);

				if (enableTextLog) {
					summaryLoggerBuilder.add(appendRef11);
				}
				if (enableHTMLLog) {
					summaryLoggerBuilder.add(appendRef12);
				}
			}

			// create new logger for real time logs
			LoggerComponentBuilder realTimeLoggerBuilder = builder.newLogger(realtimeLoggerName, Level.ALL);
			realTimeLoggerBuilder.addAttribute("additivity", false);
			{
				AppenderRefComponentBuilder appendRef21 = builder.newAppenderRef("realtime-log-text" + i);
				appendRef21.addAttribute("level", Level.ALL);
				AppenderRefComponentBuilder appendRef22 = builder.newAppenderRef("realtime-log-html" + i);
				appendRef22.addAttribute("level", Level.ALL);

				if (enableTextLog) {
					realTimeLoggerBuilder.add(appendRef21);
				}
				if (enableHTMLLog) {
					realTimeLoggerBuilder.add(appendRef22);
				}
			}

			builder.add(generalLoggerBuilder);
			builder.add(summaryLoggerBuilder);
			builder.add(realTimeLoggerBuilder);
		}

		// create root logger
		RootLoggerComponentBuilder rootlogggerBuilder = builder.newRootLogger(Level.ALL);
		rootlogggerBuilder.addAttribute("additivity", false);
		{
			AppenderRefComponentBuilder appendRef0 = builder.newAppenderRef("console-log");
			rootlogggerBuilder.add(appendRef0);
		}

		builder.add(rootlogggerBuilder);

		// System.out.println(builder.toXmlConfiguration().toString());

		LoggerContext loggerContext = Configurator.initialize(builder.build());
		// Print logger xml to log file
		loggerContext.getLogger(GENERAL_LOGGER_NAME_STX + 0).trace(builder.toXmlConfiguration().toString());

		return loggerContext;
	}

	/** Returns Log Files Base Directory */
	public String getLogBaseDir() {
		return logBaseDir;
	}

	/** Sets Log Files Base Directory */
	public void setLogBaseDir(String logBaseDir) {
		this.logBaseDir = logBaseDir;
	}

	public LoggerContext getLoggerContext() {
		return loggerContext;
	}

	public void setLoggerContext(LoggerContext loggerContext) {
		this.loggerContext = loggerContext;
	}
}