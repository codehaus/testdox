package org.codehaus.testdox.intellij.actions;

import com.intellij.openapi.actionSystem.AnAction;

public class ToggleTestClassActionTest extends TestDoxActionTestCase {

    public void testUsesTestdoxProjectComponentToToggleTestAndTestedClasses() {
        useMockTestDoxController();
        mockTestDoxController.expects(once()).method("toggleTestClassAndTestedClass");
        executeAction(createAction());
    }

    public void testIsDisabledWhenTheCurrentFileCannotBeUnitTested() {
        assertEnabled(false);
    }

    public void testIsEnabledWhenTheCurrentFileCanBeUnitTested() {
        assertEnabled(true);
    }

    private void assertEnabled(boolean currentFileCanBeUnitTested) {
        mockTestDoxController.expects(once()).method("canCurrentFileBeUnitTested")
                .will(returnValue(currentFileCanBeUnitTested));

        checkActionIsEnabled(createAction(), false, currentFileCanBeUnitTested);
    }
    
    protected AnAction createAction() {
        return new ToggleTestClassAction();
    }
}
