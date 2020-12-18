package test.com.artos.testcase;

import com.artos.annotation.TestCase;
import com.artos.annotation.TestPlan;
import com.artos.annotation.Unit;
import com.artos.framework.infra.TestContext;
import com.artos.interfaces.TestExecutable;

@TestPlan(preparedBy = "Alan", preparationDate = "2019/11/6", bdd = "GIVEN..WHEN..AND..THEN..")
@TestCase
public class TestCase_2 implements TestExecutable {

    @Unit
    public void testUnit_2_1(TestContext context) {
        // --------------------------------------------------------------------------------------------
        System.out.println("testUnit_2_1");
        // --------------------------------------------------------------------------------------------
    }
}