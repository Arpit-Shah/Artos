# Arpitos
## What is Arpitos
Arpitos is a test framework designed for functional testing. It can be used to perform unit testing if required.

## Features of Arpitos
## Inbuilt logging framework
* Two log layouts are supported
	* Text layout (Default)
	* HTML layout (Can be enabled)
* Three report files are generated.
	* General report : all logs (Same as console log).
	* Error report : error and exceptions only.
	* Summary report : Summarised pass/fail summary with test name and execution time.
* Log decoration
	* Decoration enabled pattern : `"[%-5level][%d{yyyy-MM-dd_HH:mm:ss.SSS}][%t][%F][%M][%c{1}] - %msg%n%throwable"`
	* Decoration disabled pattern : `"%msg%n%throwable"`
		Refer : [Log4j Pattern](https://logging.apache.org/log4j/2.x/manual/layouts.html).
* Log level support
	* Following log levels are supported
    	| Level | Description 																						|
		|-------|---------------------------------------------------------------------------------------------------|
		|DEBUG 	|Designates fine-grained informational events that are most useful to debug an application.			|
		|ERROR	|Designates error events that might still allow the application to continue running.				|
		|FATAL	|Designates severe error events that will presumably lead the application to abort.			|
		|INFO	|Designates informational messages that highlight the progress of the application at coarse level.	|
		|OFF	|The highest possible rank and is intended to turn off logging.										|
		|TRACE	|Designates finer-grained informational events than the DEBUG.										|
		|WARN	|Designates potentially harmful situations.															|
	* Note : Log level can be configured using `conf/Framework_Config.xml` file
* Log can be enabled/disabled dynamically (Only applicable to general report log)
	* Disable log dynamically : `context.getLogger().disableGeneralLog();`
	* Enable log dynamically : `context.getLogger().enableGeneralLog();`

### Configuration using XML file
* Test framework creates default configuration file at location `conf/Framework_Config.xml` if not present already.
* User can customise framework behaviour by changing appropriate argument from the `conf/Framework_Config.xml` file.
* Test framework upon launch reads `Framework_Config.xml` file and stores all preferences until context is alive. User can override some of the preferences using `Runner` object constructor.
* List of features which can be configured using `conf/Framework_Config.xml` file.
	* Add organisation information which will be stamped on top of each log file.
	* Change logger configuration.
		* Log base directory path.
		* Log sub directory name.
		* Enable/disable text logs.
		* Enable/disable html logs.
		* Enable/disable log decoration.
	* Configure email SMTP settings.
	* Enable/disable framework banner.
	* Enable/disable organisation information.
	* Enable/disable GUI test selector.

### Annotations to mark test cases
| Annotation 		| Usage 															|Mendatory/Optional	|
|-------------------|-------------------------------------------------------------------|-------------------|
|@BeforeTestSuite   |Above method which is executed prior to running a test suite		|Optional			|
|@AfterTestSuite   	|Above method which is executed post running a test suite			|Optional			|
|@BeforeTest		|Above method which is executed prior to running each test case		|Optional			|
|@AfterTest			|Above method which is executed post running each test case			|Optional			|
|@KnownToFail		|Above test class which is expected to fail	due to known issue		|Optional			|
|@TestCase			|Above test class which needs to recognised as a test case			|Mendatory			|
|@TestPlan			|Above test class. This provides basic info about test case			|Optional			|

#### @BeforeTestSuite @AfterTestSuite @BeforeTest @AfterTest
> 	public class Main implements PrePostRunnable {
>         public static ArrayList<TestExecutable> getTestList() throws Exception {
>             ...
>         }
>
>         public static void main(String[] args) throws Exception {
>             ...
>         }
>
>         @Override
>         @BeforeTestsuite
>         public void beforeTestsuite(TestContext context) throws Exception {
> 			...
>         }
>
>         @Override
>         @AfterTestsuite
>         public void beforeTestsuite(TestContext context) throws Exception {
> 			...
>         }
>
>         @Override
>         @BeforeTest
>         public void beforeTest(TestContext context) throws Exception {
> 			...
>         }
>
>         @Override
>         @AfterTest
>         public void afterTest(TestContext context) throws Exception {
> 			...
>         }
> 	}

#### @TestCase @TestPlan @KnownToFail
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
Each test suite consist of atleast two types of classes
* PrePostRunnable : A class which implements `PrePostRunnable` and has `main()` method.
* TestExecutable : A class which implements `TestExecutable` and above the class `@TestCase` annotation is applied.
* PrePostRunnable class is responsible for launching test runner which then executes test cases based on sequence.
* Test sequence can be decided three possible ways
	* User provides test list as `List<TestExecutable>` object.
		* List which provides test name and sequence in which test cases should be executed.
		* This method can gurantee the test case order of execution.
		* Test case(s) which are not added to `List<TestExecutable>` will be ignored during execution cycle.
		* Test case(s) marked with skip attribute will be skipped during test execution cycle.
	* User does not provide `List<TestExecutable>` object AND User defines test sequence using `@TestCase` annotation.
		* Tests will be executed in sequence provided in `sequence` attribute.
		* This method can gurantee the test case order of execution unless one or more test cases have same priority.
		* If two or more test cases are marked with same priority then scanning order priority will be considered.
		* Test case(s) marked with skip attribute will be skipped during test execution cycle.
	* User does not provide `List<TestExecutable>` object AND all test case priority is set to same.
		* Test cases will be executed in order of scan.
		* Scan order can not be guranteed.
		* This option can be exercised when test execution order is not important and test cases are independant.
		* This option can be exercised if test framework is used as unit test framework.

> Note : Test cases are only scanned within same package (inclusive of child packages). If user adds test case into `List<TestExecutable>` where test case is not within a scope of the current package then test case will be ignored.

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
>         	// User can rely on reflection to populate test cases
>         	if (tests.isEmpty()) {
>           	tests = (ArrayList<TestExecutable>) new ScanTestSuite("unit_test.Convert").getTestList(true, true);
>         	}
>         	return tests;
> 		}
>
> 		public static void main(String[] args) throws Exception {
> 			Runner runner = new Runner(Main.class);
> 			runner.run(args, getTestList(), 1);
> 		}
> 	}

### Inbuilt utilities for ease of use
Arpitos has many inbuilt public utilities which can make test case writing easy. More Utilities will be added in future releases. Refer to API document for full information.
Some of the examples are shown below :
* `Convert.java` : Provides data conversion methods which makes day to day transformation of objects easy.
* `Guardian.java` : provides method which can guard user (Similar to asserts in JUnit).
* `TCPClient.java` & `TCPServer.java` : Provides basic implementation of client/server to send/receive messages.
* `UDP.java` : Provides basic implementation of UDP client/server to send/receive messages.
* `Heartbeat.java` : Provides basic implementation of one way heartbeat.
* `PropertiesFileReader.java` : Provides easy way to read/write properties file in Java.
* `Tree.java` : Lets user print file tree (similar to Windows tree command output).
* `CustomPrompt.java` : Provides GUI PopUp launcher which can be run in blocking or non-blocking mode. User can show countdown timer *and/or* text *and/or* PNG file *and/or* buttons with custom text.
* `UtilsTar.jar` & `UtilsZip.jar` : Provides simple implementation of tarring, untarring, undo-tgz, zip and unzip archives.
* etc..

### Inbuilt exception handling for test cases
Arpitos handles Exception and Throwable during test case execution. In any Exception event, test case will be marked as failed and next test case executon will be attempted.

### TimeTracking
Arpitos track time per test case and for entire test suit execution.
* Individual test case duration can be found in summary log file.
* TestSuite duration can be found in summary log file and general log file.

> Note :
> * TestCase time duration does not include time taken by BeforeTestCase and AfterTestCase methods.
> * TestSuite time duration is inclusive of time taken by testcase(s), BeforeTestCase() and AfterTestCase() methods.

### TestStatus
* Arpitos support following test status.
| Status 		| Indicates 								|
|---------------|-------------------------------------------|
|PASS (default)	| Test case is successful without any error	|
|FAIL			| Test case is failed						|
|SKIP			| Test case will be skipped during execution|
|KTF			| Test case is know to fail					|