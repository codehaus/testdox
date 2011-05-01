package org.codehaus.testdox.intellij.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;

public class SortTestDoxActionTest extends TestDoxActionTestCase {

    public void testIsEnabledIfEventOriginatedFromATestableProjectClass() {
        checkActionIsEnabled(createAction(), true);
    }

    public void testIsEnabledInTestDoxToolWindowIfEventOriginatedFromAReachableTestdoxTestElement() {
        assertActionEnabledInTestDoxToolWindowIfEventOriginatedFromClassInProject(new SortTestDoxAction(false, true), true);
    }

    public void testIsNotEnabledIfEventDidNotOriginateFromATestableProjectClass() {
        checkActionIsEnabled(createAction(), false);
    }

    public void testIsNotEnabledInTestDoxToolWindowIfEventDidNotOriginateFromAReachableTestdoxTestElement() {
        assertActionEnabledInTestDoxToolWindowIfEventOriginatedFromClassInProject(new SortTestDoxAction(false, true), false);
    }

    public void testTriggersTheReorderingOfTestdoxTestElementsWhenItsSelectionStatusIsChangedWithANonNullActionEvent() {
        useMockTestDoxController();
        boolean actionSelected = true;
        mockTestDoxController.expects(once()).method("updateSort").with(eq(actionSelected));

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
        return new SortTestDoxAction();
    }
}
