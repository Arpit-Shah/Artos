package test.com.artos.testcase;

import com.artos.interfaces.TestExecutable;

import java.util.ArrayList;

public class RunnerObject {
    public static ArrayList<TestExecutable> tests = new ArrayList<TestExecutable>();
    public RunnerObject(){
        tests.add(new TestCase_1());
        tests.add(new TestCase_2());
    }
}
