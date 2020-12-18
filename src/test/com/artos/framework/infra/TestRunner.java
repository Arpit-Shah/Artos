package test.com.artos.framework.infra;

import com.artos.framework.infra.Runner;
import org.junit.Test;
import test.com.artos.testcase.RunnerObject;

public class TestRunner {

    @Test()
    public void test01_Runner() {
        RunnerObject runnerObject = new RunnerObject();
        Runner runner = new Runner(RunnerObject.class);
        runner.setTestList(runnerObject.tests);
        try {
            runner.run(new String[]{});
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}


