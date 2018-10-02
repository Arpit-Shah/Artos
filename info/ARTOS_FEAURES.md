# Artos (Art of System Testing)
Artos is a test framework designed for functional, system, end to end and/or unit testing. The Artos was developed to provide a framework which can work out of the box so user can focus on writing test cases.

Artos consists of the following:
* Runner/Test Executor
* Log framework
* Configuration
* Test Context
* Utilities

Maven repository
```
<dependency>
  <groupId>com.theartos</groupId>
  <artifactId>artos</artifactId>
  <version>0.0.1</version>
</dependency>
```

Requirement:
* JDK 8 or higher

Definitions:

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
	* Runner is responsible for sequentializing, executing and tracking test cases from start to end.

* Facilities:
	* Artos provides test runner which can execute multiple test suites in parallel.
	* Artos test runner can be launched using:
		* Command line by providing xml test script.
			OR
		* main() method execution via IDE.

> Note: Artos recognise test cases using `@TestCase` annotations, any class without appropriate annotation(s) will be ignored.

## Framework Configuration

* Purpose:
	* User can customise framework behaviour by changing appropriate argument(s) from `conf/framework_configuration.xml` file.

* Facilities:
	* If not present, Artos will generate default configuration file at location `conf/framework_configuration.xml`.
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
	* Test status allows user to provide useful status updates during test case execution and conclude outcome.
	* User can provide short description/reason string during test status update.

* Facilities:
	* User can update test status multiple times within one test case execution.
	* Among all updated status, status with worst outcome will be considered as outcome/result of the test case.
>	Example:
>	During single test case execution, if a test case status are updated in the following sequence:
>   	PASS => PASS => FAIL => PASS
>   Then outcome of the test case will be **FAIL** because that is the worst outcome.

* Supported Test Status:

    | Status 	| Usage when							|Order (higher order = worst outcome)	|
    |-----------|---------------------------------------|---------------------------------------|
    |PASS		|Test case executed without any errors	|0										|
    |SKIP		|Test case is skipped					|1										|
    |KTF		|Test case is known to fail				|2										|
    |FAIL		|Test case failed						|3										|

* Usage:
Test status can be updated using following method :
> `TestContext().setTestStatus(TestStatus.FAIL, "Did not expect to reach here");`

## Logging Framework

* Purpose:
	* Logs and reports are heart of any test framework.
	* Artos log framework:
		* Pre-configured and ready to be used.
        * Capable of producing text and/or html log files.
		* Generates real-time logs which can be used for performance measurement.
		* Generates summary report which can be used as customer facing reports. (Separate to Extent Report)
		* Provides features which can be very useful in test development.

* Facilities:
    * Two log layouts are supported:
        * Text layout (Default enabled).
        * HTML layout (Default disabled).
    * Two log files are generated per test suite execution:
        * General log file: All logs (Same as console log).
        * Real Time log file: Send/receive message logs from connectable interfaces with timestamp.
	* Report is generated per test suite:
        * Summary report: Summarised pass/fail summary with test name, execution time and bug reference number.
    * Log patterns (decoration):
        * Decoration enabled pattern: `"[%-5level][%d{yyyy-MM-dd_HH:mm:ss.SSS}][%t][%F][%M][%c{1}] - %msg%n%throwable"`
        * Decoration disabled pattern: `"%msg%n%throwable"`
        Refer: [Log4j Pattern](https://logging.apache.org/log4j/2.x/manual/layouts.html).
    * Log level support:
        * Following log levels are supported:
            | Level | Description 																						|
            |-------|---------------------------------------------------------------------------------------------------|
            |DEBUG 	|Designates fine-grained informational events that are most useful to debug an application.			|
            |ERROR	|Designates error events that might still allow the application to continue running.				|
            |FATAL	|Designates severe error events that will presumably lead the application to abort.					|
            |INFO	|Designates informational messages that highlight the progress of the application at coarse level.	|
            |OFF	|The highest possible rank and is intended to turn off logging.										|
            |TRACE	|Designates finer-grained informational events than the DEBUG.										|
            |WARN	|Designates potentially harmful situations.															|
        * Note: Log level can be configured using `conf/framework_configuration.xml` file.
    * Log can be enabled/disabled dynamically (Only applicable to general log)
        * Disable log dynamically: `TestContext().getLogger().disableGeneralLog();`
        * Enable log dynamically: `TestContext().getLogger().enableGeneralLog();`
    * Parallel test suite execution will generate separate log files per test suite.
    	* Log files will be labelled using thread number.
    * Log files are organised under test suite name for ease of use
        * Log files are created under the following hierarchy. `RootDir => SubDir => TestSuiteName => Log file`.
        > Example: `./reporting/SN-123/com.test.testsuite1/..`
        * Root directory and sub directory location can be configured using `conf/framework_configuration.xml` file.
    * Log framework abstraction
        * Log framework is abstracted so that log framework can be changed in future without breaking existing test scripts.
    * Log file tracking
        * All log files are tracked during runtime. If user requires to retrieve current log files (inclusive of text/html) they can utilise this functionality.
        * This functionality will also be used to find current log files and attach to email client.
    * **FAIL** stamp injection
        * **FAIL** Stamp is injected to log stream straight after test status is updated to FAIL. This allows user to know at which exact line the test case failed during execution.
    * Log rollover policy
        * Current log rollover policy is triggered based on a file size of 20MB.
        * 20MB was chosen to meet emailing requirement. Trigger policy can be exposed to user in future.
	* Parameterised logging for efficiency
		* Parameterised logging is less efficient compare to string concatenation but it is efficient in case of log level is reduced to **INFO** or **ERROR**, because system do not have to spend time concatenating string.

* Usage:
	* Enable/disable text/html log files:
		* Can be configured using `conf/framework_configuration.xml` file.
	* Change log level and log directory:
		* Can be configured using `conf/framework_configuration.xml` file.
	* logging simple string with level info or debug:
	> `TestContext().getLogger().info("This is a test String" + "This is a test String 2");`
	> `TestContext().getLogger().debug("This is a test String" + "This is a test String 2");`
	* logging parameterised string with level info or debug:
	> `TestContext().getLogger().info("This is a test String {} {}", "one", "two");`
	> `TestContext().getLogger().debug("This is a test String {} {}", "one", "two");`
    * Disable logging during execution time:
    > `TestContext().getLogger().disableGeneralLog();`
    * Enable logging during execution time:
    > `TestContext().getLogger().enableGeneralLog();`

## Offline Extent Report

* Purpose:
	* Professional looking Extent report which can be distributed among customer or external parties.

* Facilities:
	* If Extent configuration file is not present, then framework will generate default configuration file at location `conf/extent_configuration.xml`.
	* Artos has inbuilt support for offline Extent reporting.
	* If enabled, Artos will produce Extent report for every test suite execution.
	* Extent report includes test name, test writer's name and test case duration.
	* Every test status update and description/reason will be reported via Extent report.
	* Final test result with bug reference number will be reported via Extent report.

* Usage:
	* Extent reporting can be enabled/disabled via `conf/framework_configuration.xml` file.

## Real Time Logging

* Purpose:
	* Performance testing requires real-time logs so time between messages can be measured accurately. Artos has inbuilt interface which can be used to provide real time logging.

* Facilities:
	* Artos has inbult hooks for real time logging. All inbuilt connectors support real-time logging.
	* User can write new connectors by implementing `Connectable` interface and use listener `RealTimeLogEventListener` to capture real-time log events.
	* Real time logs are printed with time stamp, user is not allowed to disable timestamp.
	* If test suites are executed in parallel then separate real time log file will be produced per test suite.
	* Real time log file will roll over at 20MB of file size.
	* Real time logs cannot be disabled. If `RealTimeLogEventListener` is not provided then log file will remain empty.

* Usage:
	* Collect send receive events in real time with time stamp.

## Stop on Fail

* Purpose:
	* Depending on the goal, some user may want to continue next test execution after a test case failure, in other case user may choose to stop after very first test case failure. Artos support both behaviours.

* Facilities:
	* By default, Artos is setup to stop on first failure. it can be configured to continue executing rest of the test cases.

* Usage:
	* Stop on Fail feature can be enabled/disabled via `conf/framework_configuration.xml` file.

## Test Plan Extraction

* Purpose:
	* Artos encourages user to write test plan OR short summary of test case purpose within test class. Test plan within same file encourages user to read test purpose prior to modifying/updating test case. There might be a time when user may want to output all test plan information in a file or on a console. Artos provides a way to extract test plan with few lines of code.

* Facilities:
	* Test plan can be written above each test class using `@TestPlan()` annotation.
	| Annotation tag	| Tag description 			|
	|-------------------|---------------------------|
	|description      	|test case description      |
    |preparedBy      	|test code writters name    |
    |preparationDate   	|test case creation date    |
    |reviewedBy      	|test code reviewer name    |
    |reviewDate      	|test case review date     	|
    |bdd		      	|test plan in Gherkin format|

* Usage:
	* Test Plan for entire test suite can be generated using following code:
```
	ScanTestSuite testPlan = new ScanTestSuite("");
	List<TestPlanWrapper> testPlanObjectList = testPlan.getTestPlan(context);
	for (TestPlanWrapper testPlanObject : testPlanObjectList) {
        StringBuilder sb = new StringBuilder();
        sb.append("\nTestCaseName : " + testPlanObject.getTestCaseName());
        sb.append("\nDescription : " + testPlanObject.getTestDescription());
        sb.append("\nPreparedBy : " + testPlanObject.getTestPreparedBy());
        sb.append("\nPreparationDate : " + testPlanObject.getTestPreparationDate());
        sb.append("\nReviewedBy : " + testPlanObject.getTestReviewedBy());
        sb.append("\nReviewedDate : " + testPlanObject.getTestReviewedDate());
        sb.append("\nBDD Test Plan : " + testPlanObject.getTestBDD());
        System.out.println(sb.toString());
    }
```

	* Test Plan for test cases inside specific package can be generated using following code:
```
	// replace com.artos.tests with fully qualified package name within test project
	ScanTestSuite testPlan = new ScanTestSuite("com.artos.tests");
	List<TestPlanWrapper> testPlanObjectList = testPlan.getTestPlan(context);
	for (TestPlanWrapper testPlanObject : testPlanObjectList) {
        StringBuilder sb = new StringBuilder();
        sb.append("\nTestCaseName : " + testPlanObject.getTestCaseName());
        sb.append("\nDescription : " + testPlanObject.getTestDescription());
        sb.append("\nPreparedBy : " + testPlanObject.getTestPreparedBy());
        sb.append("\nPreparationDate : " + testPlanObject.getTestPreparationDate());
        sb.append("\nReviewedBy : " + testPlanObject.getTestReviewedBy());
        sb.append("\nReviewedDate : " + testPlanObject.getTestReviewedDate());
        sb.append("\nBDD Test Plan : " + testPlanObject.getTestBDD());
        System.out.println(sb.toString());
    }
```

## Global parameters

* Purpose:
	* Test cases may require sharing object(s). Static objects are not always practical or could be thread unsafe. Sharing objects between test cases or test suites can create complex dependency. Global parameter concept was added to avoid all the above.
	* User may want to inject some parameters prior to test suite launch (Example: IP Address, Product Serial Number etc..), which could be unique per test suite. Global parameters can also be used to add parameters in TestContext() by setting them into XML test script.

* Facilities:
	* Parameters can be set into Global Parameters during run time and/or upfront via xml test script.
	* Parameters can always be updated during run time.
	* Parameters can be retrieved using string name assigned to each parameter.
	* Each Test Suite parameter(s) are maintained separately so they do not overwrite each other's value during execution time.
	* Parameterised testing can be supported using Global parameters.

* Usage:
	* Parameters can set during runtime using:
	> `TestContext().setGlobalObject("PARAMETER_1", "TEST123");`
    * Parameters can be retrieved during runtime using:
	> `(String) TestContext().getGlobalObject("PARAMETER_1");`
    * Parameters can be set via XML test script using following syntax:
>     <parameters>
>       <parameter name="PARAMETER_0">parameterValue_0</parameter>
>       <parameter name="PARAMETER_1">parameterValue_1</parameter>
>       <parameter name="PARAMETER_2">parameterValue_2</parameter>
>     </parameters>
>   Note: Only String type of parameters can be set using XML test script but `setGlobalObject() & getGlobalObject()` can be used to set/retrieve any type of object(s) during run time.

## GUI Test Selector

* Purpose:
	* GUI test selector is designed to help user during development/debugging of test cases.
	* Commenting out test cases or editing test sequence/script multiple time during development/debugging is error prone. GUI test selector is designed to avoids all listed issues.

* Facilities:
    * Following is allowed:
        * GUI test selector lets user run selective test cases and/or allows user to set test execution loop count.
        * GUI test selector also helps third party who may receive test suite in JAR format and may want to run selective test cases.
        * If user runs multiple test suites using test script then separate GUI test selector will be created per test suite.
    * Following is not allowed:
        * User cannot change test sequence using GUI test selector.
        This is to maintain test dependency. If test cases are independent, then sequence does not matter in any case thus GUI selector has no role to play. If user believes that sequence is wrong then it should be changed in main() method or via test script.
        * User cannot change test group selection using GUI test selector.
        Before test cases are populated into GUI test selector, test cases are filtered for specified group(s). If user believes that group assignment is wrong, then it should be change in test script prior to launching test suite.

* Usage:
	* GUI test selector can be enabled/disabled using `conf/framework_configuration.xml` file.

> Note: If tests are executed on build server than disable GUI test selector using `conf/framework_configuration.xml` file.

## Annotations to mark test cases
* Artos support many annotations to make test system more flexible.
| Annotation 		| Usage 																|Mandatory/Optional	|
|-------------------|-----------------------------------------------------------------------|-------------------|
|@BeforeTestSuite   |Above the method which is executed prior to running a test suite		|Optional			|
|@AfterTestSuite   	|Above the method which is executed post running a test suite			|Optional			|
|@BeforeTest		|Above the method which is executed prior to running each test case		|Optional			|
|@AfterTest			|Above the method which is executed post running each test case			|Optional			|
|@KnownToFail		|Above the test class which is expected to fail	due to known issue		|Optional			|
|@TestCase			|Above the test class which needs to recognised as a test case			|Mandatory			|
|@TestPlan			|Above the test class. This provides basic info about test case			|Optional			|
|@Group				|Above the test class. Assigns test case to a group or array of group	|Optional			|
|@ExpectedException	|Above the test class. Assigns expected Exception test may throw		|Optional			|

### @BeforeTestSuite @AfterTestSuite @BeforeTest @AfterTest

* Annotation Example:
```
 	public class Main implements PrePostRunnable {
         public static ArrayList<TestExecutable> getTestList() throws Exception {
             ...
         }

         public static void main(String[] args) throws Exception {
             ...
         }

         @BeforeTestsuite
         public void beforeTestsuite(TestContext context) throws Exception {
 			...
         }

         @AfterTestsuite
         public void beforeTestsuite(TestContext context) throws Exception {
 			...
         }

         @BeforeTest
         public void beforeTest(TestContext context) throws Exception {
 			...
         }

         @AfterTest
         public void afterTest(TestContext context) throws Exception {
 			...
         }
 	}
```

### @TestCase @TestPlan @KnownToFail @Group @ExpectedException

* Annotation Example:
```
     @Group(group = { "CI", "SEMI_AUTO" })
     @ExpectedException(expectedException = Exception.class, contains = "[^0-9]*[12]", enforce = true)
     @TestPlan(decription = "Test", preparedBy = "JohnM", preparationDate = "", reviewedBy = "", reviewDate = "")
     @TestCase(skip = false, sequence = 1, label = "regression")
     @KnownToFail(ktf = true, bugref = "JIRA-????")
     public class Test_Annotation implements TestExecutable {
         public void execute(TestContext context) throws Exception {
             // --------------------------------------------------------------------------------------------
             ...
             // --------------------------------------------------------------------------------------------
         }
     }
```

* Information populated using `@TestPlan` annotation is used in logging.
* Each new test case execution will print test plan in log file and then all the logs will be appended so user can easily identify which logs belongs to which test cases.
* If `@TestPlan` annotation is populated correctly then user can use generate test plan function to output a test plan for entire suite.
```
	// for all test cases in project
	ScanTestSuite testPlan = new ScanTestSuite("");
	logger.info(testPlan.getTestPlan(context));

	// for test cases within com.test.project package
	ScanTestSuite testPlan = new ScanTestSuite("com.test.project");
	logger.info(testPlan.getTestPlan(context));
```

### Known to Fail

* Purpose:
	* There may be a test scenario(s) which are known to be the bug but cannot be fixed straight away due to:
		> * Priority
		> OR
		> * Project timelines
		> OR
		> * Bug may not be worth fixing.

    In such case(s), those test case(s) should be marked as "known to fail" using @KnownToFail annotation and test status should be set to **KTF** upon test case failure. This will ensure that test case(s) does not end up in failed test case(s) list and user does not waste time re-investigating same issue(s).
	* Second requirement is that when "known to fail" test case(s) will start passing because:
		> * Bug is silently fixed
		> OR
		> * Software behaviour has changed

	Then test engineer should be notified by marking test case with **FAIL** status.

* Facilities:
	* Known to fail test case(s) end result is expected to be either **KTF** or **FAIL**. If test result is different then test case will be considered **FAIL** by the test framework. This will help user identify if bug was silently fixed or software behaviour has changed.
	* `@KnownToFail` annotation can also be used to mark test as "Known to Fail" and/or set bug reference number.
	* If bug reference number is provided, it will be printed in following reports:
		* Summary report
		* Extent report.

* Usage:
	* User can mark test case as "Known to Fail" and/or user can set bug reference using `@KnownToFail` annotation.
> Example: `@KnownToFail(ktf = true, bugref = "TICKET-123, TICKET-456")`

### Test Groups

* Purpose:
	* Test case can be grouped using `@Group` annotation. User can execute test case(s) which belong to specific group or groups. This can help segregate test cases based on features, automated/semiautomated/manual etc.. categories.

* Facilities:
    * Artos supports executing test cases based on assigned test group(s).
    * All test case(s) by default belong to group called `"*"`.
    * Group names are case in-sensitive.

* Usage:
	* User can assign each test cases to desired group(s) by using `@group` annotation.
	`@Group(group = { "CI", "SEMI_AUTO" })`.
	* Group policy can be configured using XML test script.
>	To run all test cases
```
	 	<groups>
      		<group name="*"/>
    	</groups>
```
>	To run test cases, belong to specific group or groups
```
	 	<groups>
      		<group name="CI"/>
      		<group name="SEMI_AUTO"/>
    	</groups>
```

### Test Exception

* Purpose:
	* Test case can throw an exception. By specifying expected exception and/or exception message description, user can pass exception verification responsibility to runner.

* Facilities:
    * Artos supports handling test exception on runner level.
    * Artos supports matching exception description using regular expression.

* Usage:
	* User can assign `@ExpectedException` annotation on top of test class as shown below.
	`@ExpectedException(expectedException = Exception.class)`
    OR
	`@ExpectedException(expectedException = Exception.class, contains = "[^0-9]*[12]?[0-9]{1,2}[^0-9]*")`
    OR
    `@ExpectedException(expectedException = Exception.class, contains = "[^0-9]*[12]?[0-9]{1,2}[^0-9]*", enforce = true)`
	* If only exception is specified then upon test exception, exception class will be matched.
	* If exception and exception message is specified then upon test exception, exception class and exception message will be matched (using regex).
	* If `enforce = true` then test case must throw expected exception otherwise test case will be marked failed.

### Test Sequence and Execution
* Each test suite consists of at least two types of classes:
	* PrePostRunnable : A class which implements `PrePostRunnable` and has `main()` method.
		* PrePostRunnable class is responsible for launching test runner which then executes test cases based on provided.
		* All test cases at the PrePostRunnable class package level and below will be considered in scope of testing.
	* TestExecutable : A class which implements `TestExecutable`.
		*  All test class must have `@TestCase` annotation.
* Test sequence can be decided four possible ways
	* CASE 1: Test list `List<TestExecutable>` is provided to `Runner` object constructor.
		* Test case(s) executed as per sequence provided in the `List<TestExecutable>`.
		* This method can guarantee the test case order of execution.
		* Test case(s) which are not added to `List<TestExecutable>` will be ignored.
	* CASE 2: `List<TestExecutable>` is empty AND `@TestCase` annotation =="sequence"== attribute is filled.
		* Tests will be executed in sequence provided in =="sequence"== attribute.
		* This method can guarantee the test case order of execution if each test cases are marked with unique sequence number.
		* If two or more test cases are marked with same sequence number, then scanning order priority will be considered for those test cases.
		* Test case(s) marked with ==skip== = ==true== attribute will be ignored during test execution cycle.
	* CASE 3: Test list `List<TestExecutable>` is empty AND all test case(s) sequence number are set to same value `@TestCase` annotation.
		* Test cases will be executed in the order of scan.
		* Scan order cannot be guaranteed.
		* This option can be exercised when test execution order is not important and test cases are independent.
		* This option can be exercised if test framework is used as unit test framework.
	* CASE 4: User provides test script via command line parameters
		* Test cases will be executed in the order provided via test script.
		* If test group is defined, then any test case(s) that do not belong to a specified group(s) will be ignored.
> WARNING: Test cases are only scanned within the same package (inclusive of child packages). If test case(s) specified in test script do not belong to the package scope, then those test case(s) will be ignored silently.

Example code for PrePostRunnable class which supports all above methods
```
     public class Main implements PrePostRunnable {
         public static ArrayList<TestExecutable> getTestList() throws Exception {
         	ArrayList<TestExecutable> tests = new ArrayList<TestExecutable>();
         	// ---------------------------------------------------------------------
         	// TODO User May Add Test Case Manually as show in sample below
         	// tests.add(new Test_123());
         	// tests.add(new Test_abc());
         	// ---------------------------------------------------------------------
         	return tests;
 		}

 		public static void main(String[] args) throws Exception {
 			Runner runner = new Runner(Main.class);
 			runner.run(args, getTestList(), 1);
 		}
 	}
```

## Inbuilt Utilities for ease of use
Artos has many inbuilt public utilities which can make test case writing easy. More Utilities will be added in future releases. Refer to API document for full information.
Some of the examples are shown below:
* `Transform.java`: Provides data conversion methods which makes day to day transformation of objects easy.
* `Guard.java`: Provides method which can guard user (Similar to asserts in JUnit).
* `TCPClient.java` and `TCPServer.java`: Provides basic implementation of client/server to send/receive messages.
* `UDP.java`: Provides basic implementation of UDP client/server to send/receive messages.
* `Heartbeat.java`: Provides basic implementation of one way heartbeat.
* `PropertiesFileReader.java`: Provides easy way to read/write properties file in Java.
* `Tree.java`: Lets user print file tree (similar to Windows tree command output).
* `CustomPrompt.java`: Provides GUI Popup launcher which can be run in blocking or non-blocking mode. User can show countdown timer *and/or* text *and/or* PNG file *and/or* buttons with custom text.
* `UtilsTar.jar` and `UtilsZip.jar`: Provides simple implementation of tarring, untarring, undo-tgz, zip and unzip archives.
* etc..

## Inbuilt Exception Handling
* Artos handles exception and throwable during test suite execution.
* If "stop on fail" is enabled and test case throws an exception, test case will be marked, test suite execution will stop and all the following test cases will be ignored.
* If "stop on fail" is disabled and test case throws an exception, test case will be marked as failed and next test case execution will be attempted.
* If an exception occurred outside test cases (Example: during before or after methods) then test suite execution will stop.

## Time Tracking
Artos tracks time per test case and for the test suite.
* Individual test case and test suite time duration can be found in summary report file.
* TestSuite time duration is also logged in general log file.

> Note:
> * TestCase time duration does not include time taken by BeforeTestCase and AfterTestCase methods.
> * TestSuite time duration is inclusive of time taken by testcase(s), BeforeTestCase() and AfterTestCase() methods.