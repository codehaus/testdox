package org.codehaus.testdox.intellij.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;

public class AutoScrollActionTest extends TestDoxActionTestCase {

    public void testIsEnabledIfEventOriginatedFromATestableProjectClass() {
        checkActionIsEnabled(createAction(), true);
    }

    public void testIsEnabledInTestdoxToolWindowIfEventOriginatedFromATestableProjectClass() {
        assertActionEnabledInTestDoxToolWindowIfEventOriginatedFromClassInProject(new AutoScrollAction(false, true), true);
    }

    public void testIsNotEnabledIfEventDidNotOriginateFromATestableProjectClass() {
        checkActionIsEnabled(createAction(), false);
    }

    public void testIsNotEnabledInTestDoxToolWindowIfEventDidNotOriginateFromATestableProjectClass() {
        assertActionEnabledInTestDoxToolWindowIfEventOriginatedFromClassInProject(new AutoScrollAction(false, true), false);
    }

    public void testTriggersTheReorderingOfTestDoxTestElementsWhenItsSelectionStatusIsChangedWithANonNullActionEvent() {
        boolean actionSelected = true;

        useMockTestDoxController();
        mockTestDoxController.expects(once()).method("updateAutoScroll").with(eq(actionSelected));

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
        return new AutoScrollAction();
    }
}
