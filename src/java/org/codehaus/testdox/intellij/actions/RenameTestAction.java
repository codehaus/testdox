package org.codehaus.testdox.intellij.actions;

import com.intellij.psi.PsiElement;

import org.codehaus.testdox.intellij.IconHelper;
import org.codehaus.testdox.intellij.TestDoxController;
import org.codehaus.testdox.intellij.panel.TestDoxToolWindowUI;

public class RenameTestAction extends BaseTestMethodIntentionAction {

    public static final String ID = "TestDox.RenameTest";

    public RenameTestAction() {
        this(DO_NOT_USE_FROM_TESTDOX_TOOL_WINDOW);
    }

    public RenameTestAction(boolean useFromTestDoxToolWindow) {
        super("Rename Test", "Renames the current test", IconHelper.getIcon(IconHelper.RENAME_ICON), useFromTestDoxToolWindow);
    }

    void execute(TestDoxController testDoxController, PsiElement targetPsiElement) {
        testDoxController.startRename(targetPsiElement);
    }

    void executeUsingTestDoxToolWindow(TestDoxToolWindowUI testDoxToolWindow) {
        testDoxToolWindow.renameSelectedTestElement();
    }
}
