package org.codehaus.testdox.intellij.actions;

import com.intellij.psi.PsiElement;

import org.codehaus.testdox.intellij.IconHelper;
import org.codehaus.testdox.intellij.TestDoxController;
import org.codehaus.testdox.intellij.panel.TestDoxToolWindowUI;

public class DeleteTestAction extends BaseTestMethodIntentionAction {

    public static final String ID = "TestDox.DeleteTest";

    public DeleteTestAction() {
        this(DO_NOT_USE_FROM_TESTDOX_TOOL_WINDOW);
    }

    public DeleteTestAction(boolean useFromTestDoxToolWindow) {
        super("Delete Test", "Deletes the current test", IconHelper.getIcon(IconHelper.DELETE_ICON), useFromTestDoxToolWindow);
    }

    void execute(TestDoxController testDoxController, PsiElement targetPsiElement) {
        testDoxController.delete(targetPsiElement);
    }

    void executeUsingTestDoxToolWindow(TestDoxToolWindowUI testDoxToolWindow) {
        testDoxToolWindow.deleteSelectedTestElement();
    }
}
