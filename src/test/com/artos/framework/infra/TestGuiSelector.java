package test.com.artos.framework.infra;

import com.artos.framework.FWStaticStore;
import com.artos.framework.infra.GuiSelector;
import com.artos.framework.infra.TestContext;
import com.artos.framework.infra.TestObjectWrapper;
import com.artos.framework.parser.FrameworkConfigParser;
import com.artos.framework.parser.TestSuite;
import com.artos.interfaces.TestRunnable;
import org.junit.Before;
import org.junit.Test;
import test.com.artos.testcase.RunnerObject;
import test.com.artos.testcase.TestCase_1;
import test.com.artos.testcase.TestCase_2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;

/**
 * the GuiSelector is a tool for the user to manually select test cases to run by a runner.
 */
public class TestGuiSelector {
    // the list test cases for the GUI selector to display 
    private static TestData inputData;
    // the selected test cases that GUI selector will transfer to the test runner
    private static TestData outputData;

    @Before
    public void init() {
        FWStaticStore.frameworkConfig = new FrameworkConfigParser(false, "dev");
        inputData = new TestData();
        outputData = new TestData();
    }

    /**
     * Execute GuiSelector in a separate thread, manually select 2 tests and set loop to 10.
     */
    @Test
    public void testRunInNewThread() {
        int threadCount = 1;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount + 5);
        CountDownLatch latch = new CountDownLatch(threadCount);
        inputData.context.setThreadLatch(latch);
        inputData.setDoubleTestCases();

        Future<?> future = executor.submit(() -> {
            try {
                new GuiSelector(inputData.context,
                        inputData.testList,
                        new DummyTestRunnable()
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        try {
            future.get();
            executor.shutdownNow();
            latch.await();
            // the expected 2 selections are manually selected from the GUI
            assertEquals(2, outputData.testList.size());
            assertEquals("loop count is transferred to runner", 10, outputData.context.getTestSuite().getLoopCount());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * automatically select the 2nd test case to execute.
     */
    @Test
    public void testRunInSameThread() {
        CountDownLatch latch = new CountDownLatch(1);
        inputData.context.setThreadLatch(latch);
        inputData.setDoubleTestCases();
        try {
            GuiSelector selector = new GuiSelector(
                    inputData.context,
                    inputData.testList,
                    new DummyTestRunnable()
            );
            selector.testTableView.getSelectionModel().setSelectionInterval(1, 1);
            selector.btnExecuteSelected.doClick();
            latch.await();
            assertEquals("One TestCase should be selected", 1, outputData.testList.size());
            String selectedTest = outputData.testList.get(0).getTestClassObject().getName();
            String listedTest = inputData.testList.get(1).getTestClassObject().getName();
            assertEquals("The selected TestCase name should equal to the 2nd", 0, selectedTest.compareTo(listedTest));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * If only one test case, then execute directly, do not display GUI selector.
     */
    @Test
    public void testRunOneTestCase() {
        CountDownLatch latch = new CountDownLatch(1);
        inputData.setSingleTestCase();
        inputData.context.setThreadLatch(latch);
        try {
            new GuiSelector(
                    inputData.context,
                    inputData.testList,
                    new DummyTestRunnable()
            );
            latch.await();
            assertEquals("One TestCase should be selected", 1, outputData.testList.size());
            String selectedTest = outputData.testList.get(0).getTestClassObject().getName();
            String listedTest = inputData.testList.get(0).getTestClassObject().getName();
            assertEquals("The selected TestCase name should equal to the input", 0, selectedTest.compareTo(listedTest));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    @Test
    public void testRunMultipleTestCases() {
        int testCaseCount = 2000;
        CountDownLatch latch = new CountDownLatch(1);
        inputData.setTestCases(testCaseCount);
        inputData.context.setThreadLatch(latch);
        try {
            GuiSelector selector = new GuiSelector(
                    inputData.context,
                    inputData.testList,
                    new DummyTestRunnable()
            );
            selector.testTableView.getSelectionModel().setSelectionInterval(0, testCaseCount - 1);
            selector.btnExecuteSelected.doClick();
            latch.await();
            assertEquals("One TestCase should be selected", testCaseCount, outputData.testList.size());
            String lastSelectedTest = outputData.testList.get(testCaseCount - 1).getTestClassObject().getName();
            String lastListedTest = inputData.testList.get(testCaseCount - 1).getTestClassObject().getName();
            assertEquals("The selected TestCase name should equal to the input", 0, lastSelectedTest.compareTo(lastListedTest));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testLoopCount() {
        CountDownLatch latch = new CountDownLatch(1);
        inputData.setDoubleTestCases();
        inputData.context.setThreadLatch(latch);
        int loop = 10;
        try {
            GuiSelector selector = new GuiSelector(
                    inputData.context,
                    inputData.testList,
                    new DummyTestRunnable()
            );
            selector.testTableView.getSelectionModel().setSelectionInterval(0, 1);
            selector.loopCountField.setText(String.valueOf(loop));
            selector.btnExecuteSelected.doClick();
            latch.await();
            assertEquals("One TestCase should be selected", 2, outputData.testList.size());
            assertEquals("loop count is transferred to runner", loop, outputData.context.getTestSuite().getLoopCount());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * A callback function for the Test Cases to execute.
     *
     * @param context  to countdown the runnable thread
     * @param testList the test case list for execution. Can be used to check the selection result.
     */
    private void runTest(TestContext context, List<TestObjectWrapper> testList) {
        outputData.testList = testList;
        outputData.context = (TestContextWrapper) context;
        outputData.context.getThreadLatch().countDown();
        System.out.println("selected test cases to be executed: " + testList.size());
    }

    /**
     * the input/output test data for the GUITestSelector constructor and testExecutor.
     */
    private static class TestData {
        TestContextWrapper context;
        List<TestObjectWrapper> testList;
        TestObjectWrapper testObj1;
        TestObjectWrapper testObj2;

        TestData() {
            context = new TestContextWrapper();
            context.setPrePostRunnableObj(RunnerObject.class);
            context.setTestSuite(new TestSuite());

            testList = new ArrayList<>();

            testObj1 = new TestObjectWrapper(
                    TestCase_1.class, false, 1, "",
                    100, "", true);
            testObj2 = new TestObjectWrapper(
                    TestCase_2.class, false, 2, "",
                    100, "", true);
        }

        void setSingleTestCase() {
            testList.clear();
            testList.add(testObj1);
        }

        void setDoubleTestCases() {
            testList.clear();
            testList.add(testObj1);
            testList.add(testObj2);
        }

        void setTestCases(int count) {
            testList.clear();
            for (int i = 0; i < count; i++) {
                testList.add(testObj1);
            }
        }
    }

    /**
     * Extends TestContext to provide some necessary getters and setters.
     */
    private static class TestContextWrapper extends TestContext {
        protected void setPrePostRunnableObj(Class<?> prePostRunnableObj) {
            super.setPrePostRunnableObj(prePostRunnableObj);
        }

        protected void setTestSuite(TestSuite testSuite) {
            super.setTestSuite(testSuite);
        }

        protected void setThreadLatch(CountDownLatch latch) {
            super.setThreadLatch(latch);
        }

        protected CountDownLatch getThreadLatch() {
            return super.getThreadLatch();
        }

        protected TestSuite getTestSuite(){
            return super.getTestSuite();
        }
    }

    /**
     * A dummy TestRunnable class to execute the runTest() method.
     */
    private class DummyTestRunnable implements TestRunnable {
        @Override
        public void executeTest(TestContext context, List<TestObjectWrapper> testList) {
            runTest(context, testList);
        }
    }

}
