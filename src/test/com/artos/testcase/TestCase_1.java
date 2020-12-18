package test.com.artos.testcase;

import com.artos.annotation.TestCase;
import com.artos.annotation.TestPlan;
import com.artos.annotation.Unit;
import com.artos.framework.infra.TestContext;
import com.artos.interfaces.TestExecutable;

@TestPlan(preparedBy = "Alan", preparationDate = "2019/11/6")
@TestCase
public class TestCase_1 implements TestExecutable {

    @Unit
    public void testUnit_1_1(TestContext context) {
        // --------------------------------------------------------------------------------------------
        System.out.println("testUnit_1_1");
        // --------------------------------------------------------------------------------------------
    }
}