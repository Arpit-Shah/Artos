/*******************************************************************************
 * Copyright (C) 2018-2019 Arpit Shah and Artos Contributors
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.logging.log4j.core.LoggerContext;
import org.xml.sax.SAXException;

import com.artos.exception.InvalidDataException;
import com.artos.framework.FWStaticStore;
import com.artos.framework.xml.FrameworkConfig;
import com.artos.framework.xml.TestScriptParser;
import com.artos.framework.xml.TestSuite;
import com.artos.interfaces.TestExecutable;
import com.google.common.collect.Lists;

public class Runner {

	Class<?> cls;
	// Default thread count should be 1
	int threadCount = 1;
	String profile = null;
	TestSuite runnerTestSuite;

	/**
	 * @param cls Runner for the test Suite
	 * @see TestContext
	 */
	public Runner(Class<?> cls) {
		this.cls = cls;
		runnerTestSuite = new TestSuite();
		// This is false if object is created using main method information
		runnerTestSuite.setTestScriptProvided(false);
		runnerTestSuite.setEnable(true);
	}

	/**
	 * Responsible for executing test cases.
	 * 
	 * <PRE>
	 * - Test script is provided in command line argument then test script will be used to generate test suite. If more than one test suite is provided then they will run in parallel with unique TestContext object per test suite
	 * - In absence of test script, information provided via Runner class will be utilised to generate test suite.
	 * </PRE>
	 * 
	 * @param args command line arguments
	 * @throws ExecutionException if the computation threw an exception
	 * @throws InterruptedException if the current thread was interrupted while waiting
	 * @throws InvalidDataException if user provides invalid data
	 * @throws IOException if io operation error occurs
	 * @throws SAXException If any parse errors occur.
	 * @throws ParserConfigurationException if a DocumentBuildercannot be created which satisfies the configuration requested.
	 */
	@SuppressWarnings("unchecked")
	public void run(String[] args)
			throws InterruptedException, ExecutionException, ParserConfigurationException, SAXException, IOException, InvalidDataException {

		// Bad path protections for parameters passed using Runner class
		{
			// Add a default group if user does not pass a group parameter
			if (null == runnerTestSuite.getTestGroupList() || runnerTestSuite.getTestGroupList().isEmpty()) {
				runnerTestSuite.setTestGroupList(Lists.newArrayList("*"));
			}
			// Store only Upper case group names to avoid case sensitiveness
			runnerTestSuite.getTestGroupList().replaceAll(String::toUpperCase);

			// Add a default group if user does not pass a group parameter
			if (null == runnerTestSuite.getTestUnitGroupList() || runnerTestSuite.getTestUnitGroupList().isEmpty()) {
				runnerTestSuite.setTestUnitGroupList(Lists.newArrayList("*"));
			}
			// Store only Upper case group names to avoid case sensitiveness
			runnerTestSuite.getTestUnitGroupList().replaceAll(String::toUpperCase);

			// if loop count is set to 0 or negative then set to at least 1
			if (runnerTestSuite.getLoopCount() < 1) {
				System.err.println("[WARNING] Loop count <1. Default to 1");
				runnerTestSuite.setLoopCount(1);
			}

			// Trim TestSuite name to 10 char
			if (null != runnerTestSuite.getSuiteName()) {
				// Only allow A-Z or a-Z or 0-9 or - to be part of the name
				String suiteName = runnerTestSuite.getSuiteName().trim().replaceAll("[^A-Za-z0-9-_]", "");
				// Only allow maximum of 10 digit
				if (suiteName.length() > 10) {
					System.err.println("[WARNING] TestSuite name >10 char. It will be trimmed");
					suiteName = suiteName.substring(0, 10);
				}
				runnerTestSuite.setSuiteName(suiteName);
			}
		}

		// Process command line arguments
		CliProcessor.proessCommandLine(args);
		provideSchema();

		// Take profile name from the command line parameters, if not available then take it from the Runner class parameters
		FWStaticStore.frameworkConfig = new FrameworkConfig(true, null == CliProcessor.testScriptFile ? profile : CliProcessor.profile);
		
		// Generate files/configurations required for ARTOS
		generateRequiredFiles();

		// Read test script file to read all test suites
		List<TestSuite> testSuiteList = createTestSuiteList();

		// If test script is provided via command line and test suites are empty then stop execution
		if (null != CliProcessor.testScriptFile && (null == testSuiteList || testSuiteList.isEmpty())) {
			System.err.println("Test Suite is missing");
			System.exit(1);
		}

		// If test script is provided via command line and suites are present then set thread number
		if (null != testSuiteList && !testSuiteList.isEmpty()) {
			threadCount = testSuiteList.size();
		}

		// If test script is not provided via command line then add testSuite constructed using Runner class parameters
		if (null == CliProcessor.testScriptFile) {
			testSuiteList = Lists.newArrayList(runnerTestSuite);
		}

		// generate logger context
		LoggerContext loggerContext = createGlobalLoggerContext(testSuiteList);

		// Start Executor service
		{
			ExecutorService service = Executors.newFixedThreadPool(threadCount + 20);
			List<Future<Runnable>> futures = new ArrayList<>();
			CountDownLatch latch = new CountDownLatch(threadCount);

			// create thread per test suite
			for (int i = 0; i < threadCount; i++) {

				// Create new context for each thread
				TestContext context = new TestContext();

				// store main() class object
				context.setPrePostRunnableObj(cls);
				// store thread latch
				context.setThreadLatch(latch);

				// Get logger for particular thread and set to context object
				LogWrapper logWrapper = new LogWrapper(loggerContext, i);
				// store logger
				context.setOrganisedLogger(logWrapper);

				if (null != testSuiteList && !testSuiteList.isEmpty()) {
					// store test suite
					context.setTestSuite(testSuiteList.get(i));
				}

				// Launch a thread with runnable
				Future<?> f = service.submit(new SuiteTask(context));
				futures.add((Future<Runnable>) f);

			}

			// wait for all tasks to complete before continuing
			for (Future<Runnable> f : futures) {
				f.get();
			}

			// shut down the executor service so that this thread can exit
			service.shutdownNow();

			// Block until all threads complete execution
			latch.await();

			// Terminate JVM
			System.exit(0);
		}
	}

	private void provideSchema() throws IOException {
		// transfer XML validator
		boolean transferXSD = true;
		if (transferXSD) {
			// only create xsd file if not present already
			File targetFile = new File(FWStaticStore.CONFIG_BASE_DIR + File.separator + "framework_configuration.xsd");
			if (!targetFile.exists() || !targetFile.isFile()) {

				// create dir if not present
				File file = new File(FWStaticStore.CONFIG_BASE_DIR);
				if (!file.exists() || !file.isDirectory()) {
					file.mkdirs();
				}

				InputStream ins = getClass().getResourceAsStream("/com/artos/template/framework_configuration.xsd");
				byte[] buffer = new byte[ins.available()];
				ins.read(buffer);

				OutputStream outStream = new FileOutputStream(targetFile);
				outStream.write(buffer);
				outStream.flush();
				outStream.close();
				ins.close();
			}
		}
	}

	private void generateRequiredFiles() throws IOException {

		if (FWStaticStore.frameworkConfig.isGenerateEclipseTemplate()) {
			// transfer eclipse templates
			// only create template file if not present already
			File targetFile = new File(FWStaticStore.TEMPLATE_BASE_DIR + File.separator + "template.xml");
			if (!targetFile.exists() || !targetFile.isFile()) {

				// create dir if not present
				File file = new File(FWStaticStore.TEMPLATE_BASE_DIR);
				if (!file.exists() || !file.isDirectory()) {
					file.mkdirs();
				}

				InputStream ins = getClass().getResourceAsStream("/com/artos/template/template.xml");
				byte[] buffer = new byte[ins.available()];
				ins.read(buffer);

				OutputStream outStream = new FileOutputStream(targetFile);
				outStream.write(buffer);
				outStream.flush();
				outStream.close();
				ins.close();
			}
		}

		if (FWStaticStore.frameworkConfig.isGenerateIntelliJTemplate()) {
			// transfer IntelliJ templates
			// only create template file if not present already
			File targetFile = new File(FWStaticStore.TEMPLATE_BASE_DIR + File.separator + "IntelliJ_template.zip");
			if (!targetFile.exists() || !targetFile.isFile()) {

				// create dir if not present
				File file = new File(FWStaticStore.TEMPLATE_BASE_DIR);
				if (!file.exists() || !file.isDirectory()) {
					file.mkdirs();
				}

				InputStream ins = getClass().getResourceAsStream("/com/artos/template/IntelliJ_template.zip");
				byte[] buffer = new byte[ins.available()];
				ins.read(buffer);

				OutputStream outStream = new FileOutputStream(targetFile);
				outStream.write(buffer);
				outStream.flush();
				outStream.close();
				ins.close();
			}
		}

		if (FWStaticStore.frameworkConfig.isEnableExtentReport()) {
			// only create Extent configuration file if not present already
			File targetFile = new File(FWStaticStore.CONFIG_BASE_DIR + File.separator + "extent_configuration.xml");

			if (!targetFile.exists() || !targetFile.isFile()) {

				// create dir if not present
				File file = new File(FWStaticStore.CONFIG_BASE_DIR);
				if (!file.exists() || !file.isDirectory()) {
					file.mkdirs();
				}

				InputStream ins = getClass().getResourceAsStream("/com/artos/template/extent_configuration.xml");
				byte[] buffer = new byte[ins.available()];
				ins.read(buffer);

				OutputStream outStream = new FileOutputStream(targetFile);
				outStream.write(buffer);
				outStream.flush();
				outStream.close();
				ins.close();
			}
		}

		if (FWStaticStore.frameworkConfig.isEnableEmailClient()) {

			String emailAuthSettingsFilePath = FWStaticStore.frameworkConfig.getEmailAuthSettingsFilePath();
			// only create auth settings file if not present already
			File targetFile = new File(emailAuthSettingsFilePath);

			// if provided file is not named correctly then fail here
			if (!targetFile.getName().equals("user_auth_settings.xml")) {
				System.err.println("Invalid File Name : " + targetFile.getAbsolutePath());
			}

			if (!targetFile.exists() || !targetFile.isFile()) {

				// create dir if not present
				File file = new File(FWStaticStore.CONFIG_BASE_DIR);
				if (!file.exists() || !file.isDirectory()) {
					file.mkdirs();
				}

				InputStream ins = getClass().getResourceAsStream("/com/artos/template/user_auth_settings.xml");
				byte[] buffer = new byte[ins.available()];
				ins.read(buffer);

				OutputStream outStream = new FileOutputStream(targetFile);
				outStream.write(buffer);
				outStream.flush();
				outStream.close();
				ins.close();
			}
		}
	}

	/**
	 * If test script is provided via command line then parse test script and generate list of test suites
	 * 
	 * @return list of test suites
	 * 
	 * @throws InvalidDataException if user provides invalid data
	 * @throws IOException if io operation error occurs
	 * @throws SAXException If any parse errors occur.
	 * @throws ParserConfigurationException if a DocumentBuildercannot be created which satisfies the configuration requested.
	 */
	private List<TestSuite> createTestSuiteList() throws ParserConfigurationException, SAXException, IOException, InvalidDataException {
		if (null == CliProcessor.testScriptFile) {
			return null;
		}

		TestScriptParser xml = new TestScriptParser();
		List<TestSuite> testSuiteList = xml.readTestScript(CliProcessor.testScriptFile);

		return testSuiteList;
	}

	/**
	 * Creates appenders for number of suites provided incase parallel execution is required, if test script is not provided then appenders are
	 * created for one thread
	 * 
	 * @param testSuiteList list of testSuites
	 * @return LoggetContext
	 */
	private LoggerContext createGlobalLoggerContext(List<TestSuite> testSuiteList) {
		// Create loggerContext with all possible thread appenders
		/**
		 * Package name can not be used for log sub-directory name in case where test cases are launched from project root directory, thus log will
		 * come out in logging base directory.
		 */
		String logSubDir = "";
		if (null != cls.getPackage()) {
			logSubDir = cls.getPackage().getName();
		}

		// Get Framework configuration set by user
		String logDirPath = FWStaticStore.frameworkConfig.getLogRootDir();
		boolean enableLogDecoration = FWStaticStore.frameworkConfig.isEnableLogDecoration();
		boolean enableTextLog = FWStaticStore.frameworkConfig.isEnableTextLog();
		boolean enableHTMLLog = FWStaticStore.frameworkConfig.isEnableHTMLLog();

		// Create loggerContext
		OrganisedLog organisedLog = new OrganisedLog(logDirPath, logSubDir, enableLogDecoration, enableTextLog, enableHTMLLog, testSuiteList);
		return organisedLog.getLoggerContext();
	}

	/**
	 * Sets Test List Provided by the user
	 * 
	 * @param testList testList provided by user
	 */
	public void setTestList(List<TestExecutable> testList) {
		List<String> testFQCNList = new ArrayList<>();
		for (TestExecutable testE : testList) {
			testFQCNList.add(testE.getClass().getCanonicalName());
		}
		runnerTestSuite.setTestFQCNList(testFQCNList);
	}

	/**
	 * Sets loop count for test suite execution. If not set then suite will be run once.
	 * 
	 * @param loopCount test loop count
	 */
	public void setLoopCount(int loopCount) {
		runnerTestSuite.setLoopCount(loopCount);
	}

	/**
	 * Sets the group list for test cases. If not set then all test cases will be run
	 * 
	 * @param testGroupList group list required to filter test cases
	 */
	public void setTestGroupList(List<String> testGroupList) {
		runnerTestSuite.setTestGroupList(testGroupList);
	}

	/**
	 * Sets the group list for test units. If not set then all test units will be run
	 * 
	 * @param testUnitGroupList group list required to filter test units
	 */
	public void setTestUnitGroupList(List<String> testUnitGroupList) {
		runnerTestSuite.setTestUnitGroupList(testUnitGroupList);
	}

	/**
	 * allows to set test suite name, Test script takes higher priority so if test script is provided then this will be ignored
	 * 
	 * @param testSuiteName test suite name
	 */
	public void setTestSuiteName(String testSuiteName) {
		runnerTestSuite.setSuiteName(testSuiteName);
	}

	/**
	 * allows to set test suite global parameters
	 * 
	 * @param globalParameter global parameters
	 */
	public void setGlobalParameters(HashMap<String, String> globalParameter) {
		runnerTestSuite.setTestSuiteParameters(globalParameter);
	}

	/**
	 * Set Framework configuration profile name, Test script takes higher priority so if test script is provided then this will be ignored
	 * 
	 * @param profile framework configuration profile name
	 */
	public void setProfile(String profile) {
		this.profile = profile;
	}

}

/**
 * Runnable class which will be used in each thread created for test suite
 */
class SuiteTask implements Runnable {

	TestContext context;
	CountDownLatch latch;

	/**
	 * Constructor for Runnable
	 *
	 * @param context test context
	 */
	public SuiteTask(TestContext context) {
		this.context = context;
	}

	@Override
	public void run() {
		try {
			// Create ArtosRunner per thread
			new ArtosRunner(context).run();
		} catch (Exception e) {
			e.printStackTrace();
			context.getLogger().error(e);
		}
	}
}
