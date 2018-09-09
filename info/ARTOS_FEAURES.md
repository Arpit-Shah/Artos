# Artos (Art Of System Testing)
Artos is a test framework designed for functional, system, end to end and/or unit testing. The Artos was developed to provide a framework which can work out of the box so user can focus on writing test cases.

Artos consists of the following :
* Runner/Test Executor
* Log framework
* Configuration
* Test Context
* Utilities

Requirement :
* JDK 8 or higher

Definitions :

| Keyword			| Description 																			|
|-------------------|---------------------------------------------------------------------------------------|
| Test Suite		| Collection of test cases that are intended to be used to test a software program		|
| Test Script		| Set of instructions that will guide test runner on how to execute test cases		 	|
| Test Case			| Set of instructions that will be performed on the system under test					|

# Artos Features
* Runner which provides flexibility to execute test cases multiple way.
* Configurable framework.
* Inbuilt logging framework (based on log4j).
* Inbuilt log appenders and reports. (Text/HTML/Extent/Summary)
* Annotation based test case parsing.
* Real time logs for performance testing.
* Inbuilt socket connectors.
* Test status (with extra : Known to Fail status support).
* Fail stamp in log file.
* Inbuilt email client.
* Listeners for developing plugins.
* Auto generation of test scripts.
* Global parameter storage per test suite.
* GUI test selector for easy debugging.

* * *

## Runner/Test Executor

* Purpose:
	* Runner is responsible for sequencializing, executing and tracking test cases from start to end.

* Facilities:
	* Artos provides test runner which can execute multiple test suites in parallel.
	* Artos test runner can be launched using:
		* Command line by providing xml test script.
			OR
		* main() method execution via IDE.

> Note: Artos recognise test cases using `@TestCase` annotations, any class without appropriate annotation(s) will be ignored.

## Framework Configuration

* Purpose:
	* User can customise framework behaviour by changing appropriate argument(s) from `conf/Framework_Config.xml` file.

* Facilities:
	* If not present, Artos will generate default configuration file at location `conf/Framework_Config.xml`.
	* User can override some of the framework configuration using `Runner` object constructor.
	* List of configurable features:
		* Organisation information (Stamped on top of log file).
		* Change logger configuration:
			* Log level
			* Log base directory path.
			* Log sub directory path.
			* Enable/disable text logs.
			* Enable/disable html logs.
			* Enable/disable log decoration.
			* Enable/disable Extent report generation.
		* Configure email client settings.
		* Enable/disable:
        	* Enable/disable framework banner.
            * Enable/disable organisation information.
            * Enable/disable GUI test selector.
            * Enable/disable auto generation of Eclipse IDE default template.
            * Enable/disable auto generation of the test scripts.
            * Enable/disable stop on fail.

## Test Status

* Purpose:
	* Test status allows user to provide useful status updates during test case execution and conclude final outcome.
	* Test status accepts string argument, where user can provide short description/reason for status update.

* Facilities:
	* User can update test status multiple times within one test case execution.
	* Among all updated status, status with worst outcome will be considered as final outcome/result of the test case.
>	Example:
>	During test execution, if a test case status are updated in the following sequence:
>   	PASS => PASS => FAIL => PASS
>   Then final outcome of the test case will be FAIL because that is the worst outcome.

* Supported Test Status:

    | Status 	| Usage when							|Order (higher order = worst outcome)	|
    |-----------|---------------------------------------|---------------------------------------|
    |PASS		|Test case executed without any errors	|0										|
    |SKIP		|Test case is skipped					|1										|
    |KTF		|Test case is known to fail				|2										|
    |FAIL		|Test case failed						|3										|

* Usage:
> `context.setTestStatus(TestStatus.FAIL, "Did not expect to reach here");`

## Known to Fail

* Purpose:
	* There may be a test scenario(s) which are known to be the bug but can not be fixed straight away due to priority, project timelines and/or bug may not be worth fixing. In such case(s), those test case(s) status should be set to **KTF** upon test case failure, so those test case(s) does not end up in failed test cases list. This will help user identify known to fail test cases easily, thus do not waste their time in re-investigating.
	* Second requirement is that when those scenarios are silently fixed or behaviour changes then test engineer should be notified by marking test case with **FAIL**.

* Facilities:
	* Test case marked as known to fail, must fall into result catagory of **KTF** or **FAIL**. Any other test status will force test case as **FAIL**.
	* Bug/JIRA/Ticket number can be set to `@KnownToFail` attribute. If provided, it will be printed in following reports:
		* Summary report
		* Extent report.

* Usage:
	* User must set `@KnownToFail` annotation ==ktf== attribute to true for test case(s) which may fail with ==KTF== status.
		Example: `@KnownToFail(ktf = true, bugref = "TICKET-123, TICKET-456")`

## Inbuilt Logging Framework

* Purpose:
	* Logging is heart of any test framework.
	* Log framework should meet following requirements:
		* Logging should not introduce delay in test execution.
		* Log framework should be efficient and must not loose logs regardless of test case behaviour.
		* Log framework should have roll over policy to avoid large log files.
		* Seperate log files should be generated per test suite execution.
		* Log files should be stored in logical directory structure.
		* Log files should be tracked for email attachment.
		* Log framework should support log levels.
		* Logs framework should allow enabling/disabling logs dynamically during execution time.
		* Log should have time stamp.

* Facilities:
    * Two log layouts are supported:
        * Text layout (Default enabled).
        * HTML layout (Default disabled).
    * Three log files are generated per test suite execution:
        * General log file : All logs (Same as console log).
        * Real Time log file : Send/receive message logs from connectable interfaces with timestamp.
        * Summary report : Summarised pass/fail summary with test name and execution time and bug reference number.
    * Log decoration:
        * Decoration enabled pattern : `"[%-5level][%d{yyyy-MM-dd_HH:mm:ss.SSS}][%t][%F][%M][%c{1}] - %msg%n%throwable"`
        * Decoration disabled pattern : `"%msg%n%throwable"`
            Refer : [Log4j Pattern](https://logging.apache.org/log4j/2.x/manual/layouts.html).
    * Log level support:
        * Following log levels are supported:
            | Level | Description 																						|
            |-------|---------------------------------------------------------------------------------------------------|
            |DEBUG 	|Designates fine-grained informational events that are most useful to debug an application.			|
            |ERROR	|Designates error events that might still allow the application to continue running.				|
            |FATAL	|Designates severe error events that will presumably lead the application to abort.			|
            |INFO	|Designates informational messages that highlight the progress of the application at coarse level.	|
            |OFF	|The highest possible rank and is intended to turn off logging.										|
            |TRACE	|Designates finer-grained informational events than the DEBUG.										|
            |WARN	|Designates potentially harmful situations.															|
        * Note : Log level can be configured using `conf/Framework_Config.xml` file.
    * Log can be enabled/disabled dynamically (Only applicable to general log)
        * Disable log dynamically : `context.getLogger().disableGeneralLog();`
        * Enable log dynamically : `context.getLogger().enableGeneralLog();`
    * Parallel test suite execution will generate seperate log files per test suite.
    * Log files are organised under test suite name for ease of use
        * Log files are created under the following hierarchy. RootDir => SubDir => TestSuiteName => Log file.
        * Example : ./reporting/SN-123/com.test.testsuite1/..
        * Root directory and Sub directory location can be configured using `conf/Framework_Config.xml` file.
    * Log framework abstraction
        * Log framework is abstracted so that log framework can be changed in future without breaking existing test scripts.
    * Log file tracking
        * All log files are tracked during runtime. If user requires to retrieve current log files (inclusive of text/html) they can utilise this functionality.
        * This functionality will also be used to find current log files and attach to email client.
    * FAIL stamp injection
        * FAIL Stamp is injected to log stream straight after test status is updated to FAIL. This allows user to know at which exact line the test case failed during execution.
    * Log rollover policy
        * Current log rollover policy is triggered based on a file size of 20MB.
        * 20MB was chosen to meet emailing requirement. Trigger policy can be exposed to user in future.
	* Parameterised logging for efficiency
		* Parameterised logging is less efficient compare to string concatanation but it is efficient in case of log level is reduced to INFO or ERROR, because system do not have to spend time concaneting string.

* Usage:
	* Enable/disable text/html log files:
		* Can be configured using `conf/Framework_Config.xml` file.
	* Change log level and log directory:
		* Can be configured using `conf/Framework_Config.xml` file.
	* logging simple string with level info or debug:
	`context.getLogger().info("This is a test String" + "This is a test String 2");`
	`context.getLogger().debug("This is a test String" + "This is a test String 2");`
	* logging parameterised string with level info or debug:
	`context.getLogger().info("This is a test String {} {}", "one", "two");`
	`context.getLogger().debug("This is a test String {} {}", "one", "two");`
    * Disable logging during execution time:
    `context.getLogger().disableGeneralLog();`
    * Enable logging during execution time:
    `context.getLogger().enableGeneralLog();`

## Inbuilt Offline Extent Report

* Purpose:
	* Extent library generates professional looking report which can be distributed among customer or external parties.

* Facilities:
	* If Extent configuration file is not present, then framework will generate default configuration file at location `conf/Extent_Config.xml`.
	* Artos has inbuilt support for offline Extent reporting.
	* If enabled, Artos will produce Extent report for every test suite execution.
	* If test suites are run in parallel then seperate Extent report will be produced per test suite.
	* Every test status update and description/reason will be reported via Extent report.
	* Final test result with bug/JIRA/ticket number will be reported via Extent report.

* Usage:
	* Extent reporting can be enabled/disabled via `conf/Framework_Config.xml` file.

## Stop on Fail

* Purpose:
	* Depending on the goal, some user may want to continue testing after any test case failure, in other case user may choose to stop after very first test case failure. Artos support both behaviours.

* Facilities:
	* By default Artos is setup to stop on first failure. it can be configured to continue.

* Usage:
	* Stop on Fail feature can be enabled/disabled via `conf/Framework_Config.xml` file.

## Real Time Logging

* Purpose:
	* Performance testing requires realtime logs so time between messages can be measured accurately. Artos has inbuilt interface which can be used to provide real time logging.

* Facilities:
	* Artos has inbult hooks for real time logging. All inbuilt connectors supports realtime logging.
	* User can write new connectors by implementing `Connectable` interface and use `RealTimeLogEventListener` listener to capture realtime log events.
	* Real time logs are printed with time stamp, user is not allowed to disable timestamp.
	* If test suites are executed in parallel then seperate real time log file will be produced per test suite.
	* Real time log file will roll over at 20MB of file size.
	* Real time logs can not be disabled.

* Usage:
	* Collect real time logs in log file.

## Global parameters

* Purpose:
	* Test Cases may require sharing constructed object which could be of any type. Making an static object is not advisable all the time. Test case inter communication for sharing an object can create complex dependancy. Global parameter concept was brought in to avoid all of the above.
	* User may want to set some parameters prior to test suite launch (Example: IP Address, Product SerialNumber etc..), which could be unique per test suite. Global parameters can also be used to add parameters in TestContext by setting them into XML test script.

* Facilities:
	* Parameters can be set into Global Parameters during run time and/or upfront via xml test script.
	* Parameters can always be updated during run time.
	* Parameters can be polled using String name assigned to each parameters.
	* Each Test Suite parameters are maintained seperately so they do not overwrite each others values during execution time.
	* Parameterised testing can be supported using Global parameters.

* Usage:
	* Parametes can set during runtime using:
	`context.setGlobalObject("PARAMETER-1", "TEST123");`
    * Parametes can be polled during runtime using:
	`context.getGlobalObject("PARAMETER-1");`
    * Parameters can also be set via XML test script using following syntaxt:
>     <parameters>
>       <parameter name="PARAMETER_0">parameterValue_0</parameter>
>       <parameter name="PARAMETER_1">parameterValue_1</parameter>
>       <parameter name="PARAMETER_2">parameterValue_2</parameter>
>     </parameters>

## Test Groups

* Purpose:
	* Test case can be grouped using group labels so user can execute test cases which belongs to specific group. This can help segrigate test cases based on features, automated, semiautomated, manual etc.. catagories.

* Facilities:
    * Artos support executing test cases based on assigned test group(s).
    * All test case(s) by default belong to group called `"*"`.
    * Group names are case in-sensitive.

* Usage:
	* User can assign each test cases to desired group(s) by using `@group` annotation.
	`@Group(group = { "CI", "SEMI_AUTO" })`.
	* Group policy can be configured using XML test script.
>	To run all test cases
>	 	<groups>
>      		<group name="*"/>
>    	</groups>
>	To run test cases belong to specific group
>	 	<groups>
>      		<group name="CI"/>
>      		<group name="SEMI_AUTO"/>
>    	</groups>

## GUI Test Selector

* Purpose:
	* GUI test selector is designed to help user during development/debugging time. Commenting out test cases or editing test sequence/script multiple time during development/debugging is error prone. GUI test selector is designed to avoids all of listed issues.

* Facilities:
    * Following is allowed :
        * GUI test selector lets user run selective test cases and/or allows user to set test execution loop count. User can run only selective test cases during development/debugging time without modifying existing test sequence/script or re-run same test cases multiple time.
        * GUI test selector also helps third party who may receive test cases in JAR format and may want to run selective test cases.
        * If user runs multiple test suites using test script then seperate GUI test selector will be created per test suite.
    * Following is not allowed :
        * User can not change test sequence using GUI test selector. This is to maintain test dependancy. If test cases are independent, then sequence does not matter in any case thus GUI selector has no role to play. If user believes that sequence is wrong then it should be changed in main() method or via test script.
        * User can not change test group selection using GUI test selector. Before test cases are populated into the list of GUI test selector, test cases does not belong to specified group are removed and that is to maintain test purpose. If user believes that group assignment is wrong then it should be change in test script or test cases.

* Usage:
	* GUI test selector can be enabled/disabled using `conf/Framework_Config.xml` file.

> Note: If tests are executed on build server than disable GUI test selector using `conf/Framework_Config.xml` file.

## Annotations to mark test cases
* Artos support many annotation to make test system more flexible.

| Annotation 		| Usage 																|Mandatory/Optional	|
|-------------------|-----------------------------------------------------------------------|-------------------|
|@BeforeTestSuite   |Above the method which is executed prior to running a test suite		|Optional			|
|@AfterTestSuite   	|Above the method which is executed post running a test suite			|Optional			|
|@BeforeTest		|Above the method which is executed prior to running each test case		|Optional			|
|@AfterTest			|Above the method which is executed post running each test case			|Optional			|
|@KnownToFail		|Above the test class which is expected to fail	due to known issue		|Optional			|
|@TestCase			|Above the test class which needs to recognised as a test case			|Mandatory			|
|@TestPlan			|Above the test class. This provides basic info about test case			|Optional			|
|@Group				|Above the test class. Assigns test case to group or array of group		|Optional			|

### @BeforeTestSuite @AfterTestSuite @BeforeTest @AfterTest
> 	public class Main implements PrePostRunnable {
>         public static ArrayList<TestExecutable> getTestList() throws Exception {
>             ...
>         }
>
>         public static void main(String[] args) throws Exception {
>             ...
>         }
>
>         @BeforeTestsuite
>         public void beforeTestsuite(TestContext context) throws Exception {
> 			...
>         }
>
>         @AfterTestsuite
>         public void beforeTestsuite(TestContext context) throws Exception {
> 			...
>         }
>
>         @BeforeTest
>         public void beforeTest(TestContext context) throws Exception {
> 			...
>         }
>
>         @AfterTest
>         public void afterTest(TestContext context) throws Exception {
> 			...
>         }
> 	}

### @TestCase @TestPlan @KnownToFail @Group
>     @Group(group = { "CI", "SEMI_AUTO" })
>     @TestPlan(decription = "Test", preparedBy = "JohnM", preparationDate = "", reviewedBy = "", reviewDate = "")
>     @TestCase(skip = false, sequence = 1, label = "regression")
>     @KnownToFail(ktf = true, bugref = "JIRA-????")
>     public class Test_Annotation implements TestExecutable {
>         public void execute(TestContext context) throws Exception {
>             // --------------------------------------------------------------------------------------------
>             ...
>             // --------------------------------------------------------------------------------------------
>         }
>     }

### Test Sequence and Execution
* Each test suite consist of atleast two types of classes:
	* PrePostRunnable : A class which implements `PrePostRunnable` and has `main()` method.
		* PrePostRunnable class is responsible for launching test runner which then executes test cases based on provided.
		* All test cases at the PrePostRunnable class package level and below will be considered in scope of testing.
	* TestExecutable : A class which implements `TestExecutable`.
		*  All test class must have `@TestCase` annotation.
* Test sequence can be decided four possible ways
	* CASE 1 : Test list `List<TestExecutable>` is provided to `Runner` object constructor.
		* Test case(s) executed as per sequence provided in the `List<TestExecutable>`.
		* This method can guarantee the test case order of execution.
		* Test case(s) which are not added to `List<TestExecutable>` will be ignored.
	* CASE 2 : `List<TestExecutable>` is empty AND `@TestCase` annotation =="sequence"== attribute is filled.
		* Tests will be executed in sequence provided in =="sequence"== attribute.
		* This method can guarantee the test case order of execution as long as each test cases are marked with unique sequence number.
		* If two or more test cases are marked with same sequence number, then scanning order priority will be considered for those test cases.
		* Test case(s) marked with ==skip== = ==true== attribute will be ignored during test execution cycle.
	* CASE 3 : Test list `List<TestExecutable>` is empty AND all test case(s) sequence number are set to same value `@TestCase` annotation.
		* Test cases will be executed in the order of scan.
		* Scan order cannot be guaranteed.
		* This option can be exercised when test execution order is not important and test cases are independent.
		* This option can be exercised if test framework is used as unit test framework.
	* CASE 4 : User provides test script via command line parameters
		* Test cases will be executed in the order provided via test script.
		* If test group is defined, then any test case(s) that do not belong to a specified group(s) will be ignored.
> WARNING : Test cases are only scanned within the same package (inclusive of child packages). If test case(s) specified in test script do not belong to the package scope then those test case(s) will be ignored silently.

Example code for PrePostRunnable class which supports all above methods
>     public class Main implements PrePostRunnable {
>         public static ArrayList<TestExecutable> getTestList() throws Exception {
>         	ArrayList<TestExecutable> tests = new ArrayList<TestExecutable>();
>         	// ---------------------------------------------------------------------
>         	// TODO User May Add Test Case Manually as show in sample below
>         	// tests.add(new Test_123());
>         	// tests.add(new Test_abc());
>         	// ---------------------------------------------------------------------
>
>         	return tests;
> 		}
>
> 		public static void main(String[] args) throws Exception {
> 			Runner runner = new Runner(Main.class);
> 			runner.run(args, getTestList(), 1);
> 		}
> 	}

## Inbuilt Utilities for ease of use
Artos has many inbuilt public utilities which can make test case writing easy. More Utilities will be added in future releases. Refer to API document for full information.
Some of the examples are shown below :
* `Transform.java` : Provides data conversion methods which makes day to day transformation of objects easy.
* `Guard.java` : Provides method which can guard user (Similar to asserts in JUnit).
* `TCPClient.java` and `TCPServer.java` : Provides basic implementation of client/server to send/receive messages.
* `UDP.java` : Provides basic implementation of UDP client/server to send/receive messages.
* `Heartbeat.java` : Provides basic implementation of one way heartbeat.
* `PropertiesFileReader.java` : Provides easy way to read/write properties file in Java.
* `Tree.java` : Lets user print file tree (similar to Windows tree command output).
* `CustomPrompt.java` : Provides GUI PopUp launcher which can be run in blocking or non-blocking mode. User can show countdown timer *and/or* text *and/or* PNG file *and/or* buttons with custom text.
* `UtilsTar.jar` and `UtilsZip.jar` : Provides simple implementation of tarring, untarring, undo-tgz, zip and unzip archives.
* etc..

## Inbuilt Exception Handling
* Artos handles exception and throwable during test suite execution.
* If "stop on fail" is enabled and test case throws an exception, test case will be marked, test suite execution will stop and all the following test cases will be ignored.
* If "stop on fail" is disabled and test case throws an exception, test case will be marked as failed and next test case execution will be attempted.
* If an exception occured outside test cases (Example : during before or after methods) then test suite execution will stop.

## Time Tracking
Artos tracks time per test case and for the test suite.
* Individual test case duration can be found in summary log file.
* TestSuite and test case time duration are logged in summary report
* TestSuite time duration is also logged in general log file.

> Note :
> * TestCase time duration does not include time taken by BeforeTestCase and AfterTestCase methods.
> * TestSuite time duration is inclusive of time taken by testcase(s), BeforeTestCase() and AfterTestCase() methods.