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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.logging.log4j.core.LoggerContext;

import com.artos.framework.FWStaticStore;
import com.artos.framework.xml.TestScriptParser;
import com.artos.framework.xml.TestSuite;
import com.artos.interfaces.PrePostRunnable;
import com.artos.interfaces.TestExecutable;

public class Runner {

	Class<? extends PrePostRunnable> cls;
	List<TestSuite> testSuiteList = null;
	List<TestContext> testContextList = new ArrayList<>();
	LoggerContext loggerContext;

	/**
	 * @param cls
	 *            Class which contains main() method
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
	 * @param args
	 *            command line arguments
	 * @param loopCycle
	 *            test loop cycle
	 * @throws ExecutionException
	 *             if the computation threw an exception
	 * @throws InterruptedException
	 *             if the current thread was interrupted while waiting
	 */
	public void run(String[] args, int loopCycle) throws InterruptedException, ExecutionException {

		// pass empty array list so reflection will be used
		run(args, new ArrayList<>(), loopCycle);
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
	 * @param args
	 *            command line arguments
	 * @param testList
	 *            testList provided by user
	 * @param loopCycle
	 *            test loop cycle
	 * @throws ExecutionException
	 *             if the computation threw an exception
	 * @throws InterruptedException
	 *             if the current thread was interrupted while waiting
	 */
	@SuppressWarnings("unchecked")
	public void run(String[] args, List<TestExecutable> testList, int loopCycle) throws InterruptedException, ExecutionException {
		
		// Process command line arguments
		CliProcessor.proessCommandLine(args);

		// Default thread count should be 1
		int threadCount = 1;

		// If test script is provided via command line then parse test script
		if (null != CliProcessor.testScriptFile) {
			TestScriptParser xml = new TestScriptParser();
			testSuiteList = xml.readTestScript(CliProcessor.testScriptFile);

			// Thread count should be same as number of test suite
			threadCount = testSuiteList.size();
		}

		// Create loggerContext with all possible thread appenders
		{
			/**
			 * Package name can not be used for log sub-directory name in case
			 * where test cases are launched from project root directory, thus
			 * log will come out in logging base directory.
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
			loggerContext = organisedLog.getLoggerContext();
		}

		// Start Executor service
		{
			ExecutorService service = Executors.newFixedThreadPool(threadCount + 20);
			List<Future<Runnable>> futures = new ArrayList<>();
			CountDownLatch latch = new CountDownLatch(threadCount);

			// create thread per test suite
			for (int i = 0; i < threadCount; i++) {

				// Create new context for each thread
				TestContext context = new TestContext();
				testContextList.add(context);

				// Get logger for particular thread and set to context object
				LogWrapper logWrapper = new LogWrapper(loggerContext, i);
				context.setOrganisedLogger(logWrapper);

				// If TestSuite is present then store it in context
				if (null != testSuiteList && !testSuiteList.isEmpty()) {
					context.setGlobalObject(FWStaticStore.GLOBAL_TEST_SUITE, testSuiteList.get(i));
				}

				// Launch a thread with runnable
				Future<?> f = service.submit(new SuiteTask(cls, testList, loopCycle, context, latch));
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
}

/**
 * Runnable class which will be used in each thread created for test suite
 */
class SuiteTask implements Runnable {

	Class<? extends PrePostRunnable> cls;
	List<TestExecutable> testList;
	int loopCycle;
	TestContext context;
	CountDownLatch latch;

	/**
	 * Constructor for Runnable
	 * 
	 * @param cls
	 *            class containing main() method
	 * @param testList
	 *            list of TestExecutable (provided by user)
	 * @param loopCycle
	 *            number of loop cycle
	 * @param context
	 *            test context
	 * @param latch
	 *            CountDownLatch to provide latch mechanism for each thread
	 */
	public SuiteTask(Class<? extends PrePostRunnable> cls, List<TestExecutable> testList, int loopCycle, TestContext context, CountDownLatch latch) {
		this.cls = cls;
		this.testList = testList;
		this.loopCycle = loopCycle;
		this.context = context;
		this.latch = latch;
	}

	@Override
	public void run() {
		try {
			// Create ArtosRunner per thread
			ArtosRunner artos = new ArtosRunner(cls, context, latch);
			artos.run(testList, loopCycle);
		} catch (Exception e) {
			e.printStackTrace();
			context.getLogger().error(e);
		}
	}
}
