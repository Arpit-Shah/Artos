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

import com.arpitos.infra.Enums.TestStatus;

/**
 * This class is responsible for initializing all log streams which may require
 * during test suit execution
 * 
 * @author arpit
 *
 */
public class OrganisedLog {

	private Logger generalLogger;
	private Logger summaryLogger;

	/**
	 * Class Constructor
	 * 
	 * <PRE>
	 * {
	 * 	&#64;code
	 * 	OrganisedLog logger = new OrganisedLog("./reporting/A123456789", "com.arpitos.test", true);
	 * }
	 * </PRE>
	 * 
	 * @param strDestDirName
	 *            Log destination directory
	 * @param strTestName
	 *            TestSuit name for sub directory structure
	 * @param disableLogDecoration
	 *            disable decoration around test (Time-stamp, source package,
	 *            thread number etc..)
	 */
	public OrganisedLog(String logDir, String strTestName, boolean enableLogDecoration, boolean enableTextLog, boolean enableHTMLLog) {

		// System.setProperty("log4j.configurationFile",
		// "./conf/log4j2.properties");
		LoggerContext loggerContext = dynamicallyConfigureLog4J(logDir + File.separator + strTestName, strTestName + "_" + System.currentTimeMillis(),
				enableLogDecoration, enableTextLog, enableHTMLLog);
		setGeneralLogger(loggerContext.getLogger("TestLog"));
		setSummaryLogger(loggerContext.getLogger("Summary"));
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
	 */
	public void appendSummaryReport(TestStatus status, String strTestName, String bugTrackingNumber, long passCount, long failCount, long skipCount,
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
	 * Dynamically Generates and applies Log4J configuration XML as per test suit requirement
	 * 
	 * {@code
		LoggerContext loggerContext = dynamicallyConfigureLog4J("./reporting/A123456/com.arpitos.test", test + "_" + System.currentTimeMillis(), true);
	 * }
	 * @formatter:off
	 * <PRE>
	 * {@code
		 <?xml version="1.0" encoding="UTF-8"?>
		  <Configuration status="WARN">
		  	<Properties>
		  		<Property name="log-path">./reporting/logs</Property>
		  	</Properties>
		  
		  	<Appenders>
		  		<Console name="console-log" target="SYSTEM_OUT">
		  			<PatternLayout pattern="[%-5level][%d{yyyy-MM-dd_HH:mm:ss.SSS}][%t][%F][%M][%c{1}] - %msg%n%throwable" />
		  		</Console>
		  
		  		<RollingFile name="all-log" fileName="${log-path}/arpitos-all.log" filePattern="${log-path}/arpitos-all-%d{yyyy-MM-dd_HH.mm.ss.SSS}.log">
		  			<PatternLayout>
		  				<pattern>[%-5level][%d{yyyy-MM-dd_HH:mm:ss.SSS}][%t][%F][%M][%c{1}] - %msg%n%throwable</pattern>
		  			</PatternLayout>
		  			<Policies>
		  				<SizeBasedTriggeringPolicy size="200MB" />
		  			</Policies>
		  		</RollingFile>
		  
		  		<RollingFile name="error-log" fileName="${log-path}/arpitos-error.log" filePattern="${log-path}/arpitos-error-%d{yyyy-MM-dd_HH.mm.ss.SSS}.log">
		  			<PatternLayout>
		  				<pattern>[%-5level][%d{yyyy-MM-dd_HH:mm:ss.SSS}][%t][%F][%M][%c{1}] - %msg%n%throwable</pattern>
		  			</PatternLayout>
		  			<Policies>
		  				<SizeBasedTriggeringPolicy size="200MB" />
		  			</Policies>
		  		</RollingFile>
		  	</Appenders>
		  
		  	<Loggers>
		  		<Logger name="TestLog" level="debug" additivity="false">
		  			<appender-ref ref="console-log" level="all" />
		  			<appender-ref ref="all-log" level="all" />
		  			<appender-ref ref="error-log" level="error" />
		  		</Logger>
		  	 	<Logger name="Summary" level="debug" additivity="false">
		  			<appender-ref ref="summary-log" level="all" />
		  		</Logger>
		  		<Root level="all" additivity="false">
		  			<AppenderRef ref="console-log" />
		  		</Root>
		  	</Loggers>
		  </Configuration>
	 * }
	 * </PRE>
	 * @formatter:on
	 * @param fileroot log file root
	 * @param fileName log file name
	 * @param disableLogDecoration true/false Disables decoration for the logs
	 * @param disableHTMLLog 
	 * @param disableTextLog 
	 * @return
	 */
	public static LoggerContext dynamicallyConfigureLog4J(String fileroot, String fileName, boolean enableLogDecoration, boolean enableTextLog,
			boolean enableHTMLLog) {

		if (!fileroot.endsWith("/") || !fileroot.endsWith("\\")) {
			fileroot = fileroot + File.separator;
		}

		ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
		builder.setStatusLevel(Level.WARN);
		builder.setConfigurationName("RollingBuilder");

		// Log Layout with timestamp
		LayoutComponentBuilder logFileLayout = builder.newLayout("PatternLayout");
		if (enableLogDecoration) {
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
			logFileLayout.addAttribute("pattern", "[%-5level][%d{yyyy-MM-dd_HH:mm:ss.SSS}][%t][%F][%M][%c{1}] - %msg%n%throwable");
		} else {
			logFileLayout.addAttribute("pattern", "%msg%n%throwable");
		}

		// Log Layout without decoration
		LayoutComponentBuilder summaryFileLayout = builder.newLayout("PatternLayout");
		summaryFileLayout.addAttribute("pattern", "%msg%n%throwable");

		// HTML Log Layout for logs
		LayoutComponentBuilder htmlLogFileLayout = builder.newLayout("HTMLLayout");
		htmlLogFileLayout.addAttribute("title", "Test Logs");
		// expensive if enabled
		htmlLogFileLayout.addAttribute("locationInfo", false);
		htmlLogFileLayout.addAttribute("fontSize", "small");

		// HTML Log Layout for summary
		LayoutComponentBuilder htmlSummaryFileLayout = builder.newLayout("HTMLLayout");
		htmlSummaryFileLayout.addAttribute("title", "Test Summary");
		// expensive if enabled
		htmlSummaryFileLayout.addAttribute("locationInfo", false);
		htmlLogFileLayout.addAttribute("fontSize", "small");

		// File size based rollver Trigger Policy
		ComponentBuilder<?> triggeringPolicy = builder.newComponent("Policies");
		triggeringPolicy.addComponent(builder.newComponent("SizeBasedTriggeringPolicy").addAttribute("size", "200MB"));

		// create a console appender
		{
			AppenderComponentBuilder appenderBuilder1 = builder.newAppender("console-log", "CONSOLE");
			appenderBuilder1.addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT);
			appenderBuilder1.add(logFileLayout);
			builder.add(appenderBuilder1);
		}

		// create a rolling file appender for logs
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
			if (enableTextLog) {
				builder.add(appenderBuilder4);
			}

			// HTML
			AppenderComponentBuilder appenderBuilder5 = builder.newAppender("error-log-html", "RollingFile");
			appenderBuilder5.addAttribute("fileName", fileroot + fileName + "-error.html");
			appenderBuilder5.addAttribute("filePattern", fileroot + fileName + "-error-%d{yyyy-MM-dd_HH.mm.ss.SSS}.html");
			appenderBuilder5.add(htmlLogFileLayout);
			appenderBuilder5.addComponent(triggeringPolicy);
			if (enableHTMLLog) {
				builder.add(appenderBuilder5);
			}
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

		{
			// create the new logger
			LoggerComponentBuilder loggerBuilder1 = builder.newLogger("TestLog", Level.DEBUG);
			loggerBuilder1.addAttribute("additivity", false);
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

			loggerBuilder1.add(appendRef1);

			if (enableTextLog) {
				loggerBuilder1.add(appendRef2);
				loggerBuilder1.add(appendRef4);
			}
			if (enableHTMLLog) {
				loggerBuilder1.add(appendRef3);
				loggerBuilder1.add(appendRef5);
			}

			builder.add(loggerBuilder1);

			// create the new logger
			LoggerComponentBuilder loggerBuilder2 = builder.newLogger("Summary", Level.DEBUG);
			loggerBuilder2.addAttribute("additivity", false);
			AppenderRefComponentBuilder appendRef11 = builder.newAppenderRef("summary-log-text");
			appendRef11.addAttribute("level", Level.ALL);
			AppenderRefComponentBuilder appendRef12 = builder.newAppenderRef("summary-log-html");
			appendRef12.addAttribute("level", Level.ALL);

			if (enableTextLog) {
				loggerBuilder2.add(appendRef11);
			}
			if (enableHTMLLog) {
				loggerBuilder2.add(appendRef12);
			}

			builder.add(loggerBuilder2);

			RootLoggerComponentBuilder rootlogggerBuilder = builder.newRootLogger(Level.ALL);
			rootlogggerBuilder.addAttribute("additivity", false);
			AppenderRefComponentBuilder appendRef0 = builder.newAppenderRef("console-log");
			rootlogggerBuilder.add(appendRef0);

			builder.add(rootlogggerBuilder);
		}

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
