package org.codehaus.testdox.intellij.inspections

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.psi.PsiClass

import org.jetbrains.annotations.NotNull
import org.codehaus.testdox.intellij.TestDoxProjectComponent

class EmptyTestClassInspection extends Inspection {

  @NotNull
  val getDisplayName = "Test class with no tests"

  override def checkClass(@NotNull psiClass: PsiClass, @NotNull manager: InspectionManager, isOnTheFly: Boolean): Array[ProblemDescriptor] = {
    if (isInnerClass(psiClass) || isSuite(psiClass)) {
      return Inspection.NO_PROBLEMS
    }

    val testDoxController = TestDoxProjectComponent.getInstance(manager.getProject()).getController()
    val factory = testDoxController.getTestDoxFileFactory()
    val file = factory.getTestDoxFile(psiClass.getContainingFile().getVirtualFile())

    if (!file.isTestedClass && file.canNavigateToTestedClass && file.testMethods.length == 0)
      Array(manager.createProblemDescriptor(psiClass.getNameIdentifier(), getDisplayName, new AddTestMethodQuickFix(), ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
    else
      Inspection.NO_PROBLEMS
  }

  private def isSuite(psiClass: PsiClass) = psiClass.findMethodsByName("suite", true).length > 0

  private def isInnerClass(psiClass: PsiClass) = {
    psiClass.getName() != null && psiClass.getContainingFile().getName().indexOf(psiClass.getName()) < 0
  }
}