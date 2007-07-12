package org.codehaus.testdox.intellij.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import org.codehaus.testdox.intellij.Mocks;
import org.jmock.Mock;

public class AddTestActionTest extends TestDoxActionTestCase {

    public void testUsesTestdoxProjectComponentToAddATestToTheCurrentTestClass() {
        useMockTestDoxController();
        mockTestDoxController.expects(once()).method("addTest");
        executeAction(new AddTestAction());
    }

    public void testIsEnabledIfEventOriginatedFromAProjectTestClass() {
        assertActionEnabledIfEventOriginatedFromTestClass(true);
    }

    public void testIsNotEnabledIfEventDidNotOriginateFromAProjectTestClass() {
        assertActionEnabledIfEventOriginatedFromTestClass(false);
    }

    private void assertActionEnabledIfEventOriginatedFromTestClass(boolean isProjectTestClass) {
        useMockTestDoxController();

        Mock mockTestDoxFile = Mocks.createAndRegisterTestDoxFileMock(this);
        mockTestDoxController.expects(once()).method("getCurrentTestDoxFile").will(returnValue(mockTestDoxFile.proxy()));
        mockTestDoxFile.expects(once()).method("canNavigateToTestedClass").will(returnValue(isProjectTestClass));

        AddTestAction action = new AddTestAction();
        action.getTemplatePresentation().setEnabled(!isProjectTestClass);

        AnActionEvent actionEvent = createAnActionEvent(action);
        action.update(actionEvent);
    }
}
