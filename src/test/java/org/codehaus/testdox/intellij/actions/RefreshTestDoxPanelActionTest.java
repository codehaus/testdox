package org.codehaus.testdox.intellij.actions;

import com.intellij.openapi.actionSystem.AnAction;

public class RefreshTestDoxPanelActionTest extends TestDoxActionTestCase {

    public void testUsesTestdoxProjectComponentToRefreshTestdoxWindow() {
        useMockTestDoxController();
        mockTestDoxController.expects(once()).method("refreshToolWindow");
        executeAction(createAction());
    }

    public void testIsEnabledIfEventOriginatedFromATestableProjectClassWithinTheTestdoxToolWindow() {
        assertActionEnabledInTestDoxToolWindowIfEventOriginatedFromClassInProject(new RefreshTestDoxPanelAction(true), true);
    }

    public void testIsNotEnabledIfEventDidNotOriginateFromATestableProjectClassWithinTheTestdoxToolWindow() {
        assertActionEnabledInTestDoxToolWindowIfEventOriginatedFromClassInProject(new RefreshTestDoxPanelAction(true), false);
    }

    public void testIsEnabledIfEventOriginatedFromATestableProjectClassOutsideOfTheToolWindow() {
        checkActionIsEnabled(createAction(), true);
    }

    public void testIsNotEnabledIfEventDidNotOriginateFromATestableProjectClassOutsideOfTheToolWindow() {
        checkActionIsEnabled(createAction(), false);
    }

    private AnAction createAction() {
        return new RefreshTestDoxPanelAction();
    }
}
