package org.codehaus.testdox.intellij.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;

public class DeleteTestActionTest extends BaseTestMethodIntentionActionTest {

    public void testUsesTestdoxProjectComponentToDeleteTheCurrentTestMethodWhenReceivingAnActionEvent() {
        useMockTestDoxController();
        mockActionEvents.expects(once()).method("getTargetPsiElement").with(isA(AnActionEvent.class));
        mockTestDoxController.expects(once()).method("delete");

        executeAction(createAction());
    }

    public void testUsesTestdoxToolWindowToDeleteTheCurrentTestMethodWhenReceivingAnActionEvent() {
        useMockTestDoxToolWindowUI();
        mockTestDoxToolWindowUI.expects(once()).method("deleteSelectedTestElement");

        DeleteTestAction action = new DeleteTestAction(true);
        action.actionPerformed(createAnActionEvent(action));
    }

    public void testUsesTestdoxProjectComponentToDeleteTheCurrentTestMethodAsAnIntention() throws IncorrectOperationException {
        mockTestDoxController.expects(once()).method("delete").with(isA(PsiElement.class));
        invokeTestMethodIntention(createAction());
    }

    private DeleteTestAction createAction() {
        return new DeleteTestAction();
    }
}
