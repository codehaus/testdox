package org.codehaus.testdox.intellij.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;

public class AutoscrollActionTest extends TestDoxActionTestCase {

    public void testIsEnabledIfEventOriginatedFromATestableProjectClass() {
        checkActionIsEnabled(createAction(), true);
    }

    public void testIsEnabledInTestdoxToolWindowIfEventOriginatedFromATestableProjectClass() {
        assertActionEnabledInTestDoxToolWindowIfEventOriginatedFromClassInProject(new AutoscrollAction(false, true), true);
    }

    public void testIsNotEnabledIfEventDidNotOriginateFromATestableProjectClass() {
        checkActionIsEnabled(createAction(), false);
    }

    public void testIsNotEnabledInTestDoxToolWindowIfEventDidNotOriginateFromATestableProjectClass() {
        assertActionEnabledInTestDoxToolWindowIfEventOriginatedFromClassInProject(new AutoscrollAction(false, true), false);
    }

    public void testTriggersTheReorderingOfTestdoxTestElementsWhenItsSelectionStatusIsChangedWithANonNullActionEvent() {
        boolean actionSelected = true;

        useMockTestDoxController();
        mockTestDoxController.expects(once()).method("updateAutoscroll").with(eq(actionSelected));

        ToggleAction action = createAction();
        AnActionEvent actionEvent = createAnActionEvent(action);
        assertFalse("action should not be selected", action.isSelected(actionEvent));

        action.setSelected(actionEvent, actionSelected);
        assertTrue("action should now be selected", action.isSelected(actionEvent));
    }

    public void testDoesNotTriggerTheReorderingOfTestdoxTestElementsWhenItsSelectionStatusIsChangedWithANullActionEvent() {
        AnActionEvent actionEvent = null;
        ToggleAction action = createAction();
        assertFalse("action should not be selected", action.isSelected(actionEvent));

        action.setSelected(actionEvent, true);
        assertTrue("action should now be selected", action.isSelected(actionEvent));
    }

    private ToggleAction createAction() {
        return new AutoscrollAction();
    }
}
