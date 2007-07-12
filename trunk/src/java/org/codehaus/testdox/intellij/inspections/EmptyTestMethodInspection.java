package org.codehaus.testdox.intellij.inspections;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiEmptyStatement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiStatement;
import org.jetbrains.annotations.NotNull;

import static jedi.functional.FunctionalPrimitives.array;

import org.codehaus.testdox.intellij.EditorApi;
import org.codehaus.testdox.intellij.TestDoxController;
import org.codehaus.testdox.intellij.TestDoxFile;
import org.codehaus.testdox.intellij.TestDoxFileFactory;
import org.codehaus.testdox.intellij.TestDoxProjectComponent;

public class EmptyTestMethodInspection extends AbstractTestDoxInspection {

    @NotNull
    public String getDisplayName() {
        return "Empty test";
    }

    public ProblemDescriptor[] checkMethod(@NotNull PsiMethod psiMethod, @NotNull InspectionManager manager, boolean isOnTheFly) {
        TestDoxController testDoxController = TestDoxProjectComponent.getInstance(manager.getProject()).getController();
        TestDoxFileFactory factory = testDoxController.getTestDoxFileFactory();
        TestDoxFile file = factory.getTestDoxFile(psiMethod.getContainingFile().getVirtualFile());

        EditorApi editorApi = testDoxController.getEditorApi();
        if ((file.isTestedClass()) || (!file.canNavigateToTestedClass()) || (!editorApi.isTestMethod(psiMethod))) {
            return NO_PROBLEMS;
        }

        PsiCodeBlock codeBlock = psiMethod.getBody();
        if (codeBlock == null) {
            return NO_PROBLEMS;
        }

        for (PsiStatement statement : codeBlock.getStatements()) {
            if (!PsiEmptyStatement.class.isInstance(statement)) {
                return NO_PROBLEMS;
            }
        }

        return createProblemDescriptor(manager, psiMethod);

    }

    private ProblemDescriptor[] createProblemDescriptor(InspectionManager manager, PsiMethod psiMethod) {
        return array(
                manager.createProblemDescriptor(
                        psiMethod.getNameIdentifier(), getDisplayName(), (LocalQuickFix) null,
                        ProblemHighlightType.GENERIC_ERROR_OR_WARNING)
        );
    }
}
