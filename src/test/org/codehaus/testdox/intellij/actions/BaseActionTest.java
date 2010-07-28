package org.codehaus.testdox.intellij.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;

public class BaseActionTest extends TestDoxActionTestCase {

    private BaseAction action;

    protected void setUp() {
        super.setUp();

        action = new BaseAction(null, null, null) {
            public void actionPerformed(AnActionEvent event) { }
        };
    }

    public void testIsDisabledByDefault() {
        assertFalse(action.getClass().getSimpleName() + " should be disabled", action.getTemplatePresentation().isEnabled());
    }

    public void testIsDisabledWhenNoProjectIsOpen() {
        mockActionEvents.expects(once()).method("getTestDoxController").with(isA(AnActionEvent.class))
                .will(returnValue(ActionEvents.Nulls.TESTDOX_CONTROLLER));

        assertActionEnabledIfTheSelectedElementIsAProjectElement(false);
    }

    public void testIsDisabledWhenThereAreNoActiveEditorsForAGivenProject() {
        assertActionEnabled(false);
    }

    public void testIsEnabledForAGivenOpenProject() {
        assertActionEnabled(true);
    }

    private void assertActionEnabled(boolean hasActiveEditors) {
        useMockTestDoxController();
        mockTestDoxController.expects(once()).method("hasActiveEditors").will(returnValue(hasActiveEditors));

        assertActionEnabledIfTheSelectedElementIsAProjectElement(hasActiveEditors);
    }

    private void assertActionEnabledIfTheSelectedElementIsAProjectElement(boolean isProjectElement) {
        action.getTemplatePresentation().setEnabled(!isProjectElement);

        AnActionEvent actionEvent = createAnActionEvent(action);
        action.update(actionEvent);

        assertEquals(action.getClass().getSimpleName() + " enabled", isProjectElement, action.getTemplatePresentation().isEnabled());
    }
}
