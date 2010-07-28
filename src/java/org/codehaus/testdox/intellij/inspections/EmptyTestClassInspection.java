package org.codehaus.testdox.intellij.inspections;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.PsiClass;
import static jedi.functional.Coercions.array;
import org.codehaus.testdox.intellij.TestDoxController;
import org.codehaus.testdox.intellij.TestDoxFile;
import org.codehaus.testdox.intellij.TestDoxFileFactory;
import org.codehaus.testdox.intellij.TestDoxProjectComponent;
import org.jetbrains.annotations.NotNull;

public class EmptyTestClassInspection extends AbstractTestDoxInspection {

    @NotNull
    public String getDisplayName() {
        return "Test class with no tests";
    }

    public ProblemDescriptor[] checkClass(@NotNull PsiClass psiClass, @NotNull InspectionManager manager, boolean isOnTheFly) {
        if (isInnerClass(psiClass) || isSuite(psiClass)) {
            return NO_PROBLEMS;
        }

        TestDoxController testDoxController = TestDoxProjectComponent.getInstance(manager.getProject()).getController();
        TestDoxFileFactory factory = testDoxController.getTestDoxFileFactory();
        TestDoxFile file = factory.getTestDoxFile(psiClass.getContainingFile().getVirtualFile());

        if ((!file.isTestedClass()) && (file.canNavigateToTestedClass()) && (file.getTestMethods().length == 0)) {
            return array(
                manager.createProblemDescriptor(
                    psiClass.getNameIdentifier(), getDisplayName(), new AddTestMethodQuickFix(), ProblemHighlightType.GENERIC_ERROR_OR_WARNING)
            );
        }

        return NO_PROBLEMS;
    }

    private boolean isSuite(PsiClass psiClass) {
        return (psiClass.findMethodsByName("suite", true).length > 0);
    }

    private boolean isInnerClass(PsiClass psiClass) {
        return (psiClass.getName() != null) && (psiClass.getContainingFile().getName().indexOf(psiClass.getName()) < 0);
    }
}
