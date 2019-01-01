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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
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
import com.artos.interfaces.PrePostRunnable;
import com.artos.interfaces.TestExecutable;

public class Runner {

	Class<? extends PrePostRunnable> cls;
	// Default thread count should be 1
	int threadCount = 1;

	/**
	 * @param cls Class which contains main() method
	 * @see TestContext
	 */
	public Runner(Class<? extends PrePostRunnable> cls) {
		this.cls = cls;
	}

	/**
	 * Responsible for executing test cases.
	 * 
	 * <PRE>
	 * - Test script is provided in command line argument then test script will be used to generate test list and execute from that
	 * - Test script is not provided then test list will be prepared using reflection.
	 * </PRE>
	 * 
	 * @param args command line arguments
	 * @param loopCount test loop count
	 * 
	 * @throws ExecutionException if the computation threw an exception
	 * @throws InterruptedException if the current thread was interrupted while waiting
	 * @throws InvalidDataException if user provides invalid data
	 * @throws IOException if io operation error occurs
	 * @throws SAXException If any parse errors occur.
	 * @throws ParserConfigurationException if a DocumentBuildercannot be created which satisfies the configuration requested.
	 */
	public void run(String[] args, int loopCount)
			throws InterruptedException, ExecutionException, ParserConfigurationException, SAXException, IOException, InvalidDataException {

		// pass empty array list so reflection will be used
		run(args, new ArrayList<>(), loopCount, null);
	}

	/**
	 * Responsible for executing test cases.
	 * 
	 * <PRE>
	 * - Test script is provided in command line argument then test script will be used to generate test list and execute from that
	 * - Test script is not provided then test list will be prepared using reflection. supplied group list will be applied.
	 * </PRE>
	 * 
	 * @param args command line arguments
	 * @param loopCount test loop count
	 * @param groupList group list required to filter test cases
	 * @throws ExecutionException if the computation threw an exception
	 * @throws InterruptedException if the current thread was interrupted while waiting
	 * @throws InvalidDataException if user provides invalid data
	 * @throws IOException if io operation error occurs
	 * @throws SAXException If any parse errors occur.
	 * @throws ParserConfigurationException if a DocumentBuildercannot be created which satisfies the configuration requested.
	 */
	public void run(String[] args, int loopCount, List<String> groupList)
			throws InterruptedException, ExecutionException, ParserConfigurationException, SAXException, IOException, InvalidDataException {

		// pass empty array list so reflection will be used
		run(args, new ArrayList<>(), loopCount, groupList);
	}

	/**
	 * Responsible for executing test cases.
	 * 
	 * <PRE>
	 * - Test script is provided in command line argument then test script will be used to generate test list and execute from that
	 * - In absence of test script, ArrayList() provided by user will be used to generate test list. 
	 * - In absence of test script and ArrayList() is null or empty, reflection will be used to generate test list.
	 * </PRE>
	 * 
	 * @param args command line arguments
	 * @param testList testList provided by user
	 * @param loopCount test loop count
	 * @throws ExecutionException if the computation threw an exception
	 * @throws InterruptedException if the current thread was interrupted while waiting
	 * @throws InvalidDataException if user provides invalid data
	 * @throws IOException if io operation error occurs
	 * @throws SAXException If any parse errors occur.
	 * @throws ParserConfigurationException if a DocumentBuildercannot be created which satisfies the configuration requested.
	 */
	public void run(String[] args, List<TestExecutable> testList, int loopCount)
			throws InterruptedException, ExecutionException, ParserConfigurationException, SAXException, IOException, InvalidDataException {

		// pass empty array list so reflection will be used
		run(args, testList, loopCount, null);
	}

	/**
	 * Responsible for executing test cases.
	 * 
	 * <PRE>
	 * - Test script is provided in command line argument then test script will be used to generate test list and execute from that
	 * - In absence of test script, ArrayList() provided by user will be used to generate test list. 
	 * - In absence of test script and ArrayList() is null or empty, reflection will be used to generate test list.
	 * </PRE>
	 * 
	 * @param args command line arguments
	 * @param testList testList provided by user
	 * @param loopCount test loop count
	 * @param groupList group list required to filter test cases
	 * @throws ExecutionException if the computation threw an exception
	 * @throws InterruptedException if the current thread was interrupted while waiting
	 * @throws InvalidDataException if user provides invalid data
	 * @throws IOException if io operation error occurs
	 * @throws SAXException If any parse errors occur.
	 * @throws ParserConfigurationException if a DocumentBuildercannot be created which satisfies the configuration requested.
	 */
	@SuppressWarnings("unchecked")
	public void run(String[] args, List<TestExecutable> testList, int loopCount, List<String> groupList)
			throws InterruptedException, ExecutionException, ParserConfigurationException, SAXException, IOException, InvalidDataException {

		if (null == groupList || groupList.isEmpty()) {
			// Add a default group if user does not pass a group parameter
			groupList = new ArrayList<>();
			groupList.add("*");
		}

		// if loop count is set to 0 or negative then set to at least 1
		if (loopCount < 1) {
			loopCount = 1;
		}

		// Process command line arguments
		CliProcessor.proessCommandLine(args);
		provideSchema();
		FWStaticStore.frameworkConfig = new FrameworkConfig(true);
		generateRequiredFiles();

		// process test suites
		List<TestSuite> testSuiteList = createTestSuiteList();
		if (null != testSuiteList && !testSuiteList.isEmpty()) {
			// Thread count should be same as number of test suites
			threadCount = testSuiteList.size();
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
					// store loopCount from test suite
					context.setTotalLoopCount(testSuiteList.get(i).getLoopCount());
				} else {
					// if testSuite is not provided then take loopCount from
					// main() method
					context.setTotalLoopCount(loopCount);
				}

				// Launch a thread with runnable
				Future<?> f = service.submit(new SuiteTask(context, testList, groupList));
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
		if (FWStaticStore.frameworkConfig.isEnableExtentReport()) {
			// only create Extent config file if not present already
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
}

/**
 * Runnable class which will be used in each thread created for test suite
 */
class SuiteTask implements Runnable {

	TestContext context;
	List<TestExecutable> testList;
	List<String> groupList;
	CountDownLatch latch;

	/**
	 * Constructor for Runnable
	 *
	 * @param context test context
	 * @param testList list of TestExecutable (provided by user)
	 */
	public SuiteTask(TestContext context, List<TestExecutable> testList, List<String> groupList) {
		this.context = context;
		this.testList = testList;
		this.groupList = groupList;
	}

	@Override
	public void run() {
		try {
			// Create ArtosRunner per thread
			ArtosRunner artos = new ArtosRunner(context);
			artos.run(testList, groupList);
		} catch (Exception e) {
			e.printStackTrace();
			context.getLogger().error(e);
		}
	}
}
