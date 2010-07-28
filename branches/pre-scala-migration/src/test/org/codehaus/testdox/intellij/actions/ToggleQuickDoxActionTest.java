package org.codehaus.testdox.intellij.actions;

public class ToggleQuickDoxActionTest extends TestDoxActionTestCase {

    public void testUsesTestdoxProjectComponentToToggleTheQuickdoxWindow() {
        useMockTestDoxController();
        mockTestDoxController.expects(once()).method("toggleQuickDox");
        executeAction(new ToggleQuickDoxAction());
    }
}
