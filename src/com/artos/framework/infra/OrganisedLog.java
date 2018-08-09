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
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
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

import com.artos.framework.FWStatic_Store;
import com.artos.framework.SystemProperties;
import com.artos.framework.Version;
import com.artos.framework.Enums.TestStatus;

/**
 * This class is responsible for initialising all log streams which may require
 * during test suit execution
 */
class OrganisedLog {

	private org.apache.logging.log4j.core.Logger generalLogger;
	private org.apache.logging.log4j.core.Logger summaryLogger;
	private org.apache.logging.log4j.core.Logger realTimeLogger;

	private String logBaseDir = "./reporting";

	/**
	 * Class Constructor
	 * 
	 * <PRE>
	 * {
	 * 	&#64;code
	 * 	OrganisedLog logger = new OrganisedLog("./reporting/A123456789", "com.artos.test", true);
	 * }
	 * </PRE>
	 * 
	 * @param logDirPath
	 *            Log base directory absolute or reference path
	 * @param testCaseFQCN
	 *            Test case fully qualified class name (Example : com.test.unit.Abc)
	 * @param enableLogDecoration
	 *            Enables/disable log decoration (Time-stamp, source package, thread
	 *            number etc..)
	 * @param enableTextLog
	 *            Enables/disable text log
	 * @param enableHTMLLog
	 *            Enable/disable HTML log
	 */
	public OrganisedLog(String logDirPath, String testCaseFQCN, boolean enableLogDecoration, boolean enableTextLog, boolean enableHTMLLog) {

		// System.setProperty("log4j.configurationFile",
		// "./conf/log4j2.properties");

		// Log Base Dir = logDir + testCaseFQCN
		setLogBaseDir(logDirPath + File.separator + testCaseFQCN);
		LoggerContext loggerContext = dynamicallyConfigureLog4J(getLogBaseDir(), testCaseFQCN + "_" + System.currentTimeMillis(), enableLogDecoration,
				enableTextLog, enableHTMLLog);
		setGeneralLogger(loggerContext.getLogger("TestLog"));
		setSummaryLogger(loggerContext.getLogger("Summary"));
		setRealTimeLogger(loggerContext.getLogger("RealTimeLog"));
		printMendatoryInfo();
		printUsefulInfo();
	}

	/**
	 * Append test summary to summary report
	 * 
	 * @param status
	 *            Test status
	 * @param strTestFQCN
	 *            Test fully qualified class name (Example : com.test.unit.Abc)
	 * @param bugTrackingNumber
	 *            BugTracking Number
	 * @param passCount
	 *            Current passed test count
	 * @param failCount
	 *            Current failed test count
	 * @param skipCount
	 *            Current skipped test count
	 * @param ktfCount
	 *            Current known to fail test count
	 * @param testDuration
	 *            Test duration
	 */
	public void appendSummaryReport(TestStatus status, String strTestFQCN, String bugTrackingNumber, long passCount, long failCount, long skipCount,
			long ktfCount, long testDuration) {

		long hours = TimeUnit.MILLISECONDS.toHours(testDuration);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(testDuration) - TimeUnit.HOURS.toMinutes(hours);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(testDuration) - TimeUnit.HOURS.toSeconds(hours) - TimeUnit.MINUTES.toSeconds(minutes);
		long millis = testDuration - TimeUnit.HOURS.toMillis(hours) - TimeUnit.MINUTES.toMillis(minutes) - TimeUnit.SECONDS.toMillis(seconds);
		String testTime = String.format("duration:%3d:%2d:%2d.%2d", hours, minutes, seconds, millis).replace(" ", "0");

		String testStatus = String.format("%-" + 4 + "s", status.getEnumName(status.getValue()));
		String testName = String.format("%-" + 100 + "s", strTestFQCN).replace(" ", ".");
		String JiraRef = String.format("%-" + 15 + "s", bugTrackingNumber);
		String PassCount = String.format("%-" + 4 + "s", passCount);
		String FailCount = String.format("%-" + 4 + "s", failCount);
		String SkipCount = String.format("%-" + 4 + "s", skipCount);
		String KTFCount = String.format("%-" + 4 + "s", ktfCount);

		getSummaryLogger().info(testStatus + " = " + testName + " " + JiraRef + " P:" + PassCount + " F:" + FailCount + " S:" + SkipCount + " K:"
				+ KTFCount + " " + testTime);
	}

	/** Disable Logging */
	public void disableGeneralLog() {
		getGeneralLogger().setLevel(Level.OFF);
	}

	/** Enable Logging */
	public void enableGeneralLog() {
		getGeneralLogger().setLevel(getLoglevelFromXML());
	}

	/**
	 * Get all error log files relevant to current context (Includes .txt and .html
	 * files)
	 * 
	 * @return List of Error log files relevant to current context
	 */
	public List<File> getCurrentErrorLogFiles() {
		List<File> textLog = new ArrayList<File>();
		List<File> htmlLog = new ArrayList<File>();
		getFilesFromGeneralLogAppenders("error-log-text", textLog);
		getFilesFromGeneralLogAppenders("error-log-html", htmlLog);
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
	 * Get all log files relevant to current context (Includes .txt and .html files)
	 * 
	 * @return List of log files relevant to current context
	 */
	public List<File> getCurrentGeneralLogFiles() {
		List<File> textLog = new ArrayList<File>();
		List<File> htmlLog = new ArrayList<File>();
		getFilesFromGeneralLogAppenders("all-log-text", textLog);
		getFilesFromGeneralLogAppenders("all-log-html", htmlLog);
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
		getFilesFromSummaryLogAppenders("summary-log-text", textLog);
		getFilesFromSummaryLogAppenders("summary-log-html", htmlLog);
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
		getFilesFromRealTimeLogAppenders("realtime-log-text", textLog);
		getFilesFromRealTimeLogAppenders("realtime-log-html", htmlLog);
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
	 * Provides list of current general log files attached to provided appender name
	 * 
	 * @param appenderName
	 *            Appender Name
	 * @param textLog
	 *            File List
	 */
	private void getFilesFromGeneralLogAppenders(String appenderName, List<File> textLog) {
		Map<String, Appender> appenders = getGeneralLogger().getAppenders();
		Iterator<Entry<String, Appender>> it = appenders.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Appender> pair = (Map.Entry<String, Appender>) it.next();

			getGeneralLogger().trace(pair.getKey() + "=" + pair.getValue());
			if (pair.getValue() instanceof RollingFileAppender) {
				if (appenderName.equals(pair.getValue().getName())) {
					String appender = ((RollingFileAppender) pair.getValue()).getFileName();
					textLog.add(new File(appender));
					getGeneralLogger().trace(((RollingFileAppender) pair.getValue()).getFileName());
				}
			}
		}
	}

	/**
	 * Provides list of current summary log files attached to provided appender name
	 * 
	 * @param appenderName
	 *            Appender Name
	 * @param textLog
	 *            File List
	 */
	private void getFilesFromSummaryLogAppenders(String appenderName, List<File> logFiles) {
		Map<String, Appender> appenders = getSummaryLogger().getAppenders();
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
		Map<String, Appender> appenders = getRealTimeLogger().getAppenders();
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
	 * Dynamically Generates and applies Log4J configuration XML as per test suit
	 * requirement
	 * 
	 * @param fileroot
	 *            Log file root directory
	 * @param fileName
	 *            Log file name
	 * @param enableLogDecoration
	 *            Enable/disable log decoration (Time-stamp, source package, thread
	 *            number etc..)
	 * @param enableTextLog
	 *            Enables/disable text log
	 * @param enableHTMLLog
	 *            Enable/disable HTML log
	 * @return {@code LoggerContext}
	 * 
	 * @see LoggerContext
	 */
	private static LoggerContext dynamicallyConfigureLog4J(String fileroot, String fileName, boolean enableLogDecoration, boolean enableTextLog,
			boolean enableHTMLLog) {

		if (!fileroot.endsWith("/") || !fileroot.endsWith("\\")) {
			fileroot = fileroot + File.separator;
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
				logFileLayout.addAttribute("pattern",
						"%highlight{[%-5level][%d{yyyy-MM-dd_HH:mm:ss.SSS}][%t][%F][%M][%c{1}] - %msg%n%throwable}{FATAL=white, ERROR=red, WARN=blue, INFO=black, DEBUG=green, TRACE=blue}");
			} else {
				logFileLayout.addAttribute("pattern",
						"%highlight{%msg%n%throwable}{FATAL=white, ERROR=red, WARN=blue, INFO=black, DEBUG=green, TRACE=blue}");
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

		// create a rolling file appender for general logs
		{
			// Text
			AppenderComponentBuilder appenderBuilder2 = builder.newAppender("all-log-text", "RollingFile");
			appenderBuilder2.addAttribute("fileName", fileroot + fileName + "-all.log");
			appenderBuilder2.addAttribute("filePattern", fileroot + fileName + "-all-%d{yyyy-MM-dd_HH.mm.ss.SSS}.log");
			appenderBuilder2.add(logFileLayout);
			appenderBuilder2.addComponent(triggeringPolicy);
			if (enableTextLog) {
				builder.add(appenderBuilder2);
			}

			// HTML
			AppenderComponentBuilder appenderBuilder3 = builder.newAppender("all-log-html", "RollingFile");
			appenderBuilder3.addAttribute("fileName", fileroot + fileName + "-all.html");
			appenderBuilder3.addAttribute("filePattern", fileroot + fileName + "-all-%d{yyyy-MM-dd_HH.mm.ss.SSS}.html");
			appenderBuilder3.add(htmlLogFileLayout);
			appenderBuilder3.addComponent(triggeringPolicy);
			if (enableHTMLLog) {
				builder.add(appenderBuilder3);
			}
		}

		// create a rolling file appender for error
		{
			// Text
			AppenderComponentBuilder appenderBuilder4 = builder.newAppender("error-log-text", "RollingFile");
			appenderBuilder4.addAttribute("fileName", fileroot + fileName + "-error.log");
			appenderBuilder4.addAttribute("filePattern", fileroot + fileName + "-error-%d{yyyy-MM-dd_HH.mm.ss.SSS}.log");
			appenderBuilder4.add(logFileLayout);
			appenderBuilder4.addComponent(triggeringPolicy);
			// if (enableTextLog) {
			// builder.add(appenderBuilder4);
			// }

			// HTML
			AppenderComponentBuilder appenderBuilder5 = builder.newAppender("error-log-html", "RollingFile");
			appenderBuilder5.addAttribute("fileName", fileroot + fileName + "-error.html");
			appenderBuilder5.addAttribute("filePattern", fileroot + fileName + "-error-%d{yyyy-MM-dd_HH.mm.ss.SSS}.html");
			appenderBuilder5.add(htmlLogFileLayout);
			appenderBuilder5.addComponent(triggeringPolicy);
			// if (enableHTMLLog) {
			// builder.add(appenderBuilder5);
			// }
		}

		// create a rolling file appender for summary
		{
			// Text
			AppenderComponentBuilder appenderBuilder6 = builder.newAppender("summary-log-text", "RollingFile");
			appenderBuilder6.addAttribute("fileName", fileroot + fileName + "-summary.log");
			appenderBuilder6.addAttribute("filePattern", fileroot + fileName + "-summary-%d{yyyy-MM-dd_HH.mm.ss.SSS}.log");
			appenderBuilder6.add(summaryFileLayout);
			appenderBuilder6.addComponent(triggeringPolicy);
			if (enableTextLog) {
				builder.add(appenderBuilder6);
			}

			// HTML
			AppenderComponentBuilder appenderBuilder7 = builder.newAppender("summary-log-html", "RollingFile");
			appenderBuilder7.addAttribute("fileName", fileroot + fileName + "-summary.html");
			appenderBuilder7.addAttribute("filePattern", fileroot + fileName + "-summary-%d{yyyy-MM-dd_HH.mm.ss.SSS}.html");
			appenderBuilder7.add(htmlSummaryFileLayout);
			appenderBuilder7.addComponent(triggeringPolicy);
			if (enableHTMLLog) {
				builder.add(appenderBuilder7);
			}
		}

		// create a rolling file appender for real time logs
		{
			// Text
			AppenderComponentBuilder appenderBuilder8 = builder.newAppender("realtime-log-text", "RollingFile");
			appenderBuilder8.addAttribute("fileName", fileroot + fileName + "-realtime.log");
			appenderBuilder8.addAttribute("filePattern", fileroot + fileName + "-realtime-%d{yyyy-MM-dd_HH.mm.ss.SSS}.log");
			appenderBuilder8.add(realTimeLogLayout);
			appenderBuilder8.addComponent(triggeringPolicy);
			if (enableTextLog) {
				builder.add(appenderBuilder8);
			}

			// HTML
			AppenderComponentBuilder appenderBuilder9 = builder.newAppender("realtime-log-html", "RollingFile");
			appenderBuilder9.addAttribute("fileName", fileroot + fileName + "-realtime.html");
			appenderBuilder9.addAttribute("filePattern", fileroot + fileName + "-realtime-%d{yyyy-MM-dd_HH.mm.ss.SSS}.html");
			appenderBuilder9.add(htmlrealTimeLogLayout);
			appenderBuilder9.addComponent(triggeringPolicy);
			if (enableHTMLLog) {
				builder.add(appenderBuilder9);
			}
		}

		{
			// create the new logger
			Level loglevel = getLoglevelFromXML();
			LoggerComponentBuilder generalLoggerBuilder = builder.newLogger("TestLog", loglevel);
			{
				generalLoggerBuilder.addAttribute("additivity", false);
				AppenderRefComponentBuilder appendRef1 = builder.newAppenderRef("console-log");
				appendRef1.addAttribute("level", Level.ALL);
				AppenderRefComponentBuilder appendRef2 = builder.newAppenderRef("all-log-text");
				appendRef2.addAttribute("level", Level.ALL);
				AppenderRefComponentBuilder appendRef3 = builder.newAppenderRef("all-log-html");
				appendRef3.addAttribute("level", Level.ALL);
				AppenderRefComponentBuilder appendRef4 = builder.newAppenderRef("error-log-text");
				appendRef4.addAttribute("level", Level.ERROR);
				AppenderRefComponentBuilder appendRef5 = builder.newAppenderRef("error-log-html");
				appendRef5.addAttribute("level", Level.ERROR);

				generalLoggerBuilder.add(appendRef1);

				if (enableTextLog) {
					generalLoggerBuilder.add(appendRef2);
					generalLoggerBuilder.add(appendRef4);
				}
				if (enableHTMLLog) {
					generalLoggerBuilder.add(appendRef3);
					generalLoggerBuilder.add(appendRef5);
				}
			}

			// create new logger for summary
			LoggerComponentBuilder summaryLoggerBuilder = builder.newLogger("Summary", Level.ALL);
			summaryLoggerBuilder.addAttribute("additivity", false);
			{
				AppenderRefComponentBuilder appendRef11 = builder.newAppenderRef("summary-log-text");
				appendRef11.addAttribute("level", Level.ALL);
				AppenderRefComponentBuilder appendRef12 = builder.newAppenderRef("summary-log-html");
				appendRef12.addAttribute("level", Level.ALL);

				if (enableTextLog) {
					summaryLoggerBuilder.add(appendRef11);
				}
				if (enableHTMLLog) {
					summaryLoggerBuilder.add(appendRef12);
				}
			}

			// create new logger for real time logs
			LoggerComponentBuilder realTimeLoggerBuilder = builder.newLogger("RealTimeLog", Level.ALL);
			realTimeLoggerBuilder.addAttribute("additivity", false);
			{
				AppenderRefComponentBuilder appendRef21 = builder.newAppenderRef("realtime-log-text");
				appendRef21.addAttribute("level", Level.ALL);
				AppenderRefComponentBuilder appendRef22 = builder.newAppenderRef("realtime-log-html");
				appendRef22.addAttribute("level", Level.ALL);

				if (enableTextLog) {
					realTimeLoggerBuilder.add(appendRef21);
				}
				if (enableHTMLLog) {
					realTimeLoggerBuilder.add(appendRef22);
				}
			}

			// create root logger
			RootLoggerComponentBuilder rootlogggerBuilder = builder.newRootLogger(Level.ALL);
			rootlogggerBuilder.addAttribute("additivity", false);
			{
				AppenderRefComponentBuilder appendRef0 = builder.newAppenderRef("console-log");
				rootlogggerBuilder.add(appendRef0);
			}

			builder.add(generalLoggerBuilder);
			builder.add(summaryLoggerBuilder);
			builder.add(realTimeLoggerBuilder);
			builder.add(rootlogggerBuilder);
		}

		// System.out.println(builder.toXmlConfiguration().toString());

		LoggerContext loggerContext = Configurator.initialize(builder.build());
		// Print logger xml to log file
		loggerContext.getLogger("TestLog").trace(builder.toXmlConfiguration().toString());

		return loggerContext;
	}

	/**
	 * Returns Log Level Enum value based on Framework configuration set in XML file
	 * 
	 * @see Level
	 * 
	 * @return
	 */
	private static Level getLoglevelFromXML() {
		if (FWStatic_Store.context.getFrameworkConfig().getLogLevel().equals("info")) {
			return Level.INFO;
		}
		if (FWStatic_Store.context.getFrameworkConfig().getLogLevel().equals("all")) {
			return Level.ALL;
		}
		if (FWStatic_Store.context.getFrameworkConfig().getLogLevel().equals("fatal")) {
			return Level.FATAL;
		}
		if (FWStatic_Store.context.getFrameworkConfig().getLogLevel().equals("trace")) {
			return Level.TRACE;
		}
		if (FWStatic_Store.context.getFrameworkConfig().getLogLevel().equals("warn")) {
			return Level.WARN;
		}
		if (FWStatic_Store.context.getFrameworkConfig().getLogLevel().equals("debug")) {
			return Level.DEBUG;
		}
		return Level.DEBUG;
	}

	/**
	 * Prints Organisation details to each log files
	 */
	private void printMendatoryInfo() {
		//@formatter:off
		
		String organisationInfo = "\n************************************ Header Start ******************************************"
								 +"\nOrganisation_Name : " + FWStatic_Store.context.getFrameworkConfig().getOrganisation_Name()
								 +"\nOrganisation_Country : " + FWStatic_Store.context.getFrameworkConfig().getOrganisation_Country()
								 +"\nOrganisation_Address : " + FWStatic_Store.context.getFrameworkConfig().getOrganisation_Address()
								 +"\nOrganisation_Phone : " + FWStatic_Store.context.getFrameworkConfig().getOrganisation_Contact_Number()
								 +"\nOrganisation_Email : " + FWStatic_Store.context.getFrameworkConfig().getOrganisation_Email()
								 +"\nOrganisation_Website : " + FWStatic_Store.context.getFrameworkConfig().getOrganisation_Website()
								 +"\n************************************ Header End ********************************************";
		//@formatter:on

		if (FWStatic_Store.context.getFrameworkConfig().isEnableBanner()) {
			getGeneralLogger().info(Banner.getBanner());
			getSummaryLogger().info(Banner.getBanner());
			getRealTimeLogger().info(Banner.getBanner());
		}

		if (FWStatic_Store.context.getFrameworkConfig().isEnableOrganisationInfo()) {
			getGeneralLogger().info(organisationInfo);
			getSummaryLogger().info(organisationInfo);
			getRealTimeLogger().info(organisationInfo);
		}
	}

	/**
	 * Print selected System Info to log file
	 * 
	 * @param generalLogger
	 */
	public void printUsefulInfo() {

		SystemProperties sysProp = FWStatic_Store.context.getSystemProperties();
		Logger logger = getGeneralLogger();

		// @formatter:off
		
		logger.debug("\nTest FrameWork Info");
		logger.debug("* Artos version => " + Version.id());
		logger.debug("* Java Runtime Environment version => " + sysProp.getJavaRuntimeEnvironmentVersion()); 
		logger.debug("* Java Virtual Machine specification version => " + sysProp.getJavaVirtualMachineSpecificationVersion());
		logger.debug("* Java Runtime Environment specification version => " + sysProp.getJavaRuntimeEnvironmentSpecificationVersion());
		logger.debug("* Java class path => " + sysProp.getJavaClassPath());
		logger.debug("* List of paths to search when loading libraries => " + sysProp.getListofPathstoSearchWhenLoadingLibraries());
		logger.debug("* Operating system name => " + sysProp.getOperatingSystemName());
		logger.debug("* Operating system architecture => " + sysProp.getOperatingSystemArchitecture());
		logger.debug("* Operating system version => " + sysProp.getOperatingSystemVersion());
		logger.debug("* File separator (\"/\" on UNIX) => " + sysProp.getFileSeparator());
		logger.debug("* Path separator (\":\" on UNIX) => " + sysProp.getPathSeparator());
		logger.debug("* User's account name => " + sysProp.getUserAccountName());
		logger.debug("* User's home directory => " + sysProp.getUserHomeDir());
		
		// @formatter:on
	}

	/**
	 * Get {@code Logger} object which is responsible for logging raw and error logs
	 * 
	 * @return General Logger
	 */
	public org.apache.logging.log4j.core.Logger getGeneralLogger() {
		return generalLogger;
	}

	private void setGeneralLogger(org.apache.logging.log4j.core.Logger generalLogger) {
		this.generalLogger = generalLogger;
	}

	/**
	 * Get {@code Logger} object which is responsible for logging summary logs
	 * 
	 * @return Summary Logger
	 */
	public org.apache.logging.log4j.core.Logger getSummaryLogger() {
		return summaryLogger;
	}

	private void setSummaryLogger(org.apache.logging.log4j.core.Logger summaryLogger) {
		this.summaryLogger = summaryLogger;
	}

	/** Returns Log Files Base Directory */
	public String getLogBaseDir() {
		return logBaseDir;
	}

	/** Sets Log Files Base Directory */
	public void setLogBaseDir(String logBaseDir) {
		this.logBaseDir = logBaseDir;
	}

	/**
	 * Get {@code Logger} object which is responsible for logging realtime logs
	 * 
	 * @return RealTime Logger
	 */
	public org.apache.logging.log4j.core.Logger getRealTimeLogger() {
		return realTimeLogger;
	}

	public void setRealTimeLogger(org.apache.logging.log4j.core.Logger realTimeLogger) {
		this.realTimeLogger = realTimeLogger;
	}

}
