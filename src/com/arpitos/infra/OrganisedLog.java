package com.arpitos.infra;

import java.io.File;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
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

import com.arpitos.infra.TestContext.Status;

public class OrganisedLog {

	private Logger generalLogger;
	private Logger summaryLogger;

	/**
	 * Class Constructor
	 * 
	 * <PRE>
	 * OrganisedLog logger = new OrganisedLog("A123456789", "com.arpitos.test", LOG_LEVEL.ALL);
	 * </PRE>
	 * 
	 * @param strDestDirName
	 *            Directory name which will contain logs for current context
	 * @param strTestName
	 *            Test Name which will be used as log file name
	 * @param logLevel
	 *            Log level allows user to only print Log with level selected or
	 *            below
	 * @param bEnableTimeStamp
	 *            Enable/Disable time stamp printing
	 */
	public OrganisedLog(String logDir, String strTestName, boolean disableLogDecoration) {

		// System.setProperty("log4j.configurationFile",
		// "./conf/log4j2.properties");
		LoggerContext loggerContext = dynamicallyConfigureLog4J(logDir + File.separator + strTestName, strTestName + "_" + System.currentTimeMillis(),
				disableLogDecoration);
		setGeneralLogger(loggerContext.getLogger("TestLog"));
		setSummaryLogger(loggerContext.getLogger("Summary"));
	}

	/**
	 * Generate summary report
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
	 */
	public void appendSummaryReport(Status status, String strTestName, String bugTrackingNumber, long passCount, long failCount, long skipCount,
			long ktfCount) {

		String testStatus = String.format("%-" + 4 + "s", status.getEnumName(status.getValue()));
		String testName = String.format("%-" + 100 + "s", strTestName).replace(" ", ".");
		String JiraRef = String.format("%-" + 15 + "s", bugTrackingNumber);
		String PassCount = String.format("%-" + 10 + "s", passCount);
		String FailCount = String.format("%-" + 10 + "s", failCount);
		String SkipCount = String.format("%-" + 10 + "s", skipCount);
		String KTFCount = String.format("%-" + 10 + "s", ktfCount);
		getSummaryLogger()
				.info(testStatus + " = " + testName + " " + JiraRef + " P:" + PassCount + " F:" + FailCount + " S:" + SkipCount + " K:" + KTFCount);
	}

	/**
	 *@return 
	 * @return 
	 * @formatter:off
	 * <PRE>
	 * 
	 * <?xml version="1.0" encoding="UTF-8"?>
	 * <Configuration status="WARN">
	 * 	<Properties>
	 * 		<Property name="log-path">./reporting/logs</Property>
	 * 	</Properties>
	 * 
	 * 	<Appenders>
	 * 		<Console name="console-log" target="SYSTEM_OUT">
	 * 			<PatternLayout pattern="[%-5level][%d{yyyy-MM-dd_HH:mm:ss.SSS}][%t][%F][%M][%c{1}] - %msg%n%throwable" />
	 * 		</Console>
	 * 
	 * 		<RollingFile name="all-log" fileName="${log-path}/arpitos-all.log" filePattern="${log-path}/arpitos-all-%d{yyyy-MM-dd_HH.mm.ss.SSS}.log">
	 * 			<PatternLayout>
	 * 				<pattern>[%-5level][%d{yyyy-MM-dd_HH:mm:ss.SSS}][%t][%F][%M][%c{1}] - %msg%n%throwable</pattern>
	 * 			</PatternLayout>
	 * 			<Policies>
	 * 				<SizeBasedTriggeringPolicy size="200MB" />
	 * 			</Policies>
	 * 		</RollingFile>
	 * 
	 * 		<RollingFile name="error-log" fileName="${log-path}/arpitos-error.log" filePattern="${log-path}/arpitos-error-%d{yyyy-MM-dd_HH.mm.ss.SSS}.log">
	 * 			<PatternLayout>
	 * 				<pattern>[%-5level][%d{yyyy-MM-dd_HH:mm:ss.SSS}][%t][%F][%M][%c{1}] - %msg%n%throwable</pattern>
	 * 			</PatternLayout>
	 * 			<Policies>
	 * 				<SizeBasedTriggeringPolicy size="200MB" />
	 * 			</Policies>
	 * 		</RollingFile>
	 * 	</Appenders>
	 * 
	 * 	<Loggers>
	 * 		<Logger name="TestLog" level="debug" additivity="false">
	 * 			<appender-ref ref="console-log" level="all" />
	 * 			<appender-ref ref="all-log" level="all" />
	 * 			<appender-ref ref="error-log" level="error" />
	 * 		</Logger>
	 * 	 	<Logger name="Summary" level="debug" additivity="false">
	 * 			<appender-ref ref="summary-log" level="all" />
	 * 		</Logger>
	 * 		<Root level="all" additivity="false">
	 * 			<AppenderRef ref="console-log" />
	 * 		</Root>
	 * 	</Loggers>
	 * </Configuration>
	 * 
	 * </PRE>
	 * @formatter:on
	 */
	public static LoggerContext dynamicallyConfigureLog4J(String fileroot, String fileName, boolean disableLogDecoration) {

		if (!fileroot.endsWith("/") || !fileroot.endsWith("\\")) {
			fileroot = fileroot + File.separator;
		}

		ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
		builder.setStatusLevel(Level.WARN);
		builder.setConfigurationName("RollingBuilder");

		// Log Layout with timestamp
		LayoutComponentBuilder layoutBuilder1 = builder.newLayout("PatternLayout");
		if (disableLogDecoration) {
			layoutBuilder1.addAttribute("pattern", "%msg%n%throwable");
		} else {
			/*
			 * @formatter:off
			 * [%-5level] = Log level upto 5 char max
			 * [%d{yyyy-MM-dd_HH:mm:ss.SSS}] = Date and time 
			 * [%t] = Thread number
			 * [%F] = File where logs are coming from
			 * [%M] = Method which generated log
			 * [%c{-1}] = ClassName which issued logCommand 
			 * %msg = Actual msg to be logged 
			 * %n = new line
			 * %throwable = log exception
			 * @formatter:on
			 */
			layoutBuilder1.addAttribute("pattern", "[%-5level][%d{yyyy-MM-dd_HH:mm:ss.SSS}][%t][%F][%M][%c{1}] - %msg%n%throwable");
		}

		// Log Layour without decoration
		LayoutComponentBuilder layoutBuilder2 = builder.newLayout("PatternLayout");
		layoutBuilder2.addAttribute("pattern", "%msg%n%throwable");

		// File size based rollver Trigger Policy
		ComponentBuilder<?> triggeringPolicy = builder.newComponent("Policies");
		triggeringPolicy.addComponent(builder.newComponent("SizeBasedTriggeringPolicy").addAttribute("size", "200MB"));

		// create a console appender
		AppenderComponentBuilder appenderBuilder1 = builder.newAppender("console-log", "CONSOLE");
		appenderBuilder1.addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT);
		appenderBuilder1.add(layoutBuilder1);
		builder.add(appenderBuilder1);

		// create a rolling file appender
		AppenderComponentBuilder appenderBuilder2 = builder.newAppender("all-log", "RollingFile");
		appenderBuilder2.addAttribute("fileName", fileroot + fileName + "-all.log");
		appenderBuilder2.addAttribute("filePattern", fileroot + fileName + "-all-%d{yyyy-MM-dd_HH.mm.ss.SSS}.log");
		appenderBuilder2.add(layoutBuilder1);
		appenderBuilder2.addComponent(triggeringPolicy);
		builder.add(appenderBuilder2);

		// create a rolling file appender for error
		AppenderComponentBuilder appenderBuilder3 = builder.newAppender("error-log", "RollingFile");
		appenderBuilder3.addAttribute("fileName", fileroot + fileName + "-error.log");
		appenderBuilder3.addAttribute("filePattern", fileroot + fileName + "-error-%d{yyyy-MM-dd_HH.mm.ss.SSS}.log");
		appenderBuilder3.add(layoutBuilder1);
		appenderBuilder3.addComponent(triggeringPolicy);
		builder.add(appenderBuilder3);

		// create a rolling file appender for error
		AppenderComponentBuilder appenderBuilder4 = builder.newAppender("summary-log", "RollingFile");
		appenderBuilder4.addAttribute("fileName", fileroot + fileName + "-summary.log");
		appenderBuilder4.addAttribute("filePattern", fileroot + fileName + "-summary-%d{yyyy-MM-dd_HH.mm.ss.SSS}.log");
		appenderBuilder4.add(layoutBuilder2);
		appenderBuilder4.addComponent(triggeringPolicy);
		builder.add(appenderBuilder4);

		// create the new logger
		LoggerComponentBuilder loggerBuilder1 = builder.newLogger("TestLog", Level.DEBUG);
		loggerBuilder1.addAttribute("additivity", false);
		AppenderRefComponentBuilder appendRef1 = builder.newAppenderRef("console-log");
		appendRef1.addAttribute("level", Level.ALL);
		AppenderRefComponentBuilder appendRef2 = builder.newAppenderRef("all-log");
		appendRef2.addAttribute("level", Level.ALL);
		AppenderRefComponentBuilder appendRef3 = builder.newAppenderRef("error-log");
		appendRef3.addAttribute("level", Level.ERROR);
		loggerBuilder1.add(appendRef1);
		loggerBuilder1.add(appendRef2);
		loggerBuilder1.add(appendRef3);

		builder.add(loggerBuilder1);

		// create the new logger
		LoggerComponentBuilder loggerBuilder2 = builder.newLogger("Summary", Level.DEBUG);
		loggerBuilder2.addAttribute("additivity", false);
		AppenderRefComponentBuilder appendRef11 = builder.newAppenderRef("summary-log");
		appendRef11.addAttribute("level", Level.ALL);
		loggerBuilder2.add(appendRef11);

		builder.add(loggerBuilder2);

		RootLoggerComponentBuilder rootlogggerBuilder = builder.newRootLogger(Level.ALL);
		rootlogggerBuilder.addAttribute("additivity", false);
		AppenderRefComponentBuilder appendRef0 = builder.newAppenderRef("console-log");
		rootlogggerBuilder.add(appendRef0);

		builder.add(rootlogggerBuilder);
		System.out.println(builder.toXmlConfiguration().toString());

		LoggerContext loggerContext = Configurator.initialize(builder.build());
		return loggerContext;
	}

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
