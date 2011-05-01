package org.codehaus.testdox.intellij.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;

public class RenameTestActionTest extends BaseTestMethodIntentionActionTest {

    public void testUsesTestdoxControllerToRenameTheCurrentTestMethodWhenReceivingAnActionEvent() {
        useMockTestDoxController();
        mockActionEvents.expects(once()).method("getTargetPsiElement").with(isA(AnActionEvent.class));
        mockTestDoxController.expects(once()).method("startRename");
        executeAction(new RenameTestAction());
    }

    public void testUsesTestdoxToolWindowToDeleteTheCurrentTestMethodWhenReceivingAnActionEvent() {
        useMockTestDoxToolWindowUI();
        mockTestDoxToolWindowUI.expects(once()).method("renameSelectedTestElement");
        executeAction(new RenameTestAction(true));
    }

    public void testUsesTestdoxControllerToRenameTheCurrentTestMethodAsAnIntention() throws IncorrectOperationException {
        mockTestDoxController.expects(once()).method("startRename").with(isA(PsiElement.class));
        invokeTestMethodIntention(new RenameTestAction());
    }
}
