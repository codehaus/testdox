package org.codehaus.testdox.intellij.actions;

import javax.swing.Icon;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.codehaus.testdox.intellij.TestDoxController;
import org.codehaus.testdox.intellij.TestDoxProjectComponent;
import org.codehaus.testdox.intellij.panel.TestDoxToolWindowUI;
import org.jetbrains.annotations.NotNull;

abstract class BaseTestMethodIntentionAction extends BaseTestElementAction implements IntentionAction {

    BaseTestMethodIntentionAction(String text, String description, Icon icon, boolean useFromTestDoxToolWindow) {
        super(text, description, icon, useFromTestDoxToolWindow);
    }

    public void actionPerformed(AnActionEvent event) {
        if (useFromTestDoxToolWindow()) {
            executeUsingTestDoxToolWindow(actionEvents().getTestDoxToolWindowUI(event));
        } else {
            execute(actionEvents().getTestDoxController(event), actionEvents().getTargetPsiElement(event));
        }
    }

    public void invoke(Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        execute(TestDoxProjectComponent.getInstance(project).getController(),
                file.findElementAt(editor.getCaretModel().getOffset()));
    }

    abstract void execute(TestDoxController testDoxController, PsiElement targetPsiElement);

    abstract void executeUsingTestDoxToolWindow(TestDoxToolWindowUI testDoxToolWindow);

    public void update(AnActionEvent event) {
        if (useFromTestDoxToolWindow()) {
            actionEvents().getTestDoxToolWindowUI(event).update(event.getPresentation());
        } else {
            TestDoxController testDoxController = actionEvents().getTestDoxController(event);
            testDoxController.updatePresentation(event.getPresentation(), actionEvents().getTargetPsiElement(event));
        }
    }

    @NotNull
    public String getText() {
        return getTemplatePresentation().getText();
    }

    @NotNull
    public String getFamilyName() {
        return "TestDox.TestMethodIntentions";
    }

    public boolean isAvailable(Project project, Editor editor, PsiFile file) {
        TestDoxController testDoxController = TestDoxProjectComponent.getInstance(project).getController();
        PsiElement psiElement = file.findElementAt(editor.getCaretModel().getOffset());
        return (psiElement != null) && testDoxController.getEditorApi().isTestMethod(psiElement.getParent());
    }

    public boolean startInWriteAction() {
        return false;
    }
}
