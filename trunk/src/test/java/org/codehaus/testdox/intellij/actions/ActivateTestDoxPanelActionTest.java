package org.codehaus.testdox.intellij.actions;

public class ActivateTestDoxPanelActionTest extends TestDoxActionTestCase {

    public void testUsesTestdoxProjectComponentToToggleTestdoxWindow() {
        useMockTestDoxController();
        mockTestDoxController.expects(once()).method("toggleToolWindow");
        executeAction(new ActivateTestDoxPanelAction());
    }
}
