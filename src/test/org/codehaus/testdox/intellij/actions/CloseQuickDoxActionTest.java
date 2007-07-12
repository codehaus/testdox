package org.codehaus.testdox.intellij.actions;

public class CloseQuickDoxActionTest extends TestDoxActionTestCase {

    public void testUsesTestdoxProjectComponentToCloseTheQuickdoxWindow() {
        useMockTestDoxController();
        mockTestDoxController.expects(once()).method("closeQuickDox");
        executeAction(new CloseQuickDoxAction());
    }
}
