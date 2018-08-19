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
	 *            Test class with main method
	 * @see TestContext
	 */
	public Runner(Class<? extends PrePostRunnable> cls) throws Exception {
		this.cls = cls;
	}

	/**
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
		// Only process command line argument if provided
		CliProcessor.proessCommandLine(args);

		// Default thread count should be 1
		int threadCount = 1;

		// If test script is provided via command line then parse test script
		if (null != FWStaticStore.testScriptFile) {
			TestScriptParser xml = new TestScriptParser();
			testSuiteList = xml.readTestScript(FWStaticStore.testScriptFile);

			// Thread count should be same as number of test suite
			threadCount = testSuiteList.size();
		}

		// Create loggerContext with all possible thread appenders
		{
			String logDirPath = FWStaticStore.frameworkConfig.getLogRootDir();
			String logSubDir = cls.getPackage().getName();
			boolean enableLogDecoration = FWStaticStore.frameworkConfig.isEnableLogDecoration();
			boolean enableTextLog = FWStaticStore.frameworkConfig.isEnableTextLog();
			boolean enableHTMLLog = FWStaticStore.frameworkConfig.isEnableHTMLLog();

			OrganisedLog organisedLog = new OrganisedLog(logDirPath, logSubDir, enableLogDecoration, enableTextLog, enableHTMLLog, testSuiteList);
			loggerContext = organisedLog.getLoggerContext();
		}

		// Start Executor service
		{
			ExecutorService service = Executors.newFixedThreadPool(threadCount + 20);
			List<Future<Runnable>> futures = new ArrayList<>();
			CountDownLatch latch = new CountDownLatch(threadCount);
			
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

			latch.await();
			System.exit(0);
		}
	}
}

/**
 * Runnable class for executor service. Launches seperate runner for each thread
 * 
 */
class SuiteTask implements Runnable {

	Class<? extends PrePostRunnable> cls;
	List<TestExecutable> testList;
	int loopCycle;
	TestContext context;
	CountDownLatch latch;

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
			ArtosRunner artos = new ArtosRunner(cls, context, latch);
			artos.run(testList, loopCycle);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
