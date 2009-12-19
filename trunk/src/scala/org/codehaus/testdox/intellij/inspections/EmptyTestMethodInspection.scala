package org.codehaus.testdox.intellij.inspections

import com.intellij.codeInspection.{LocalQuickFix, InspectionManager, ProblemDescriptor, ProblemHighlightType}
import com.intellij.psi.PsiEmptyStatement
import com.intellij.psi.PsiMethod

import org.jetbrains.annotations.NotNull
import org.codehaus.testdox.intellij.TestDoxProjectComponent

class EmptyTestMethodInspection extends Inspection {

  @NotNull
  val getDisplayName = "Empty test"

  override def checkMethod(@NotNull psiMethod: PsiMethod, @NotNull manager: InspectionManager, isOnTheFly: Boolean): Array[ProblemDescriptor] = {
    val testDoxController = TestDoxProjectComponent.getInstance(manager.getProject()).getController()
    val factory = testDoxController.getTestDoxFileFactory()
    val file = factory.getTestDoxFile(psiMethod.getContainingFile().getVirtualFile())

    val editorApi = testDoxController.getEditorApi()
    if (file.isTestedClass || !file.canNavigateToTestedClass || !editorApi.isTestMethod(psiMethod)) {
      return Inspection.NO_PROBLEMS
    }

    val codeBlock = psiMethod.getBody()
    if (codeBlock == null) {
      return Inspection.NO_PROBLEMS
    }

    for (val statement <- codeBlock.getStatements()) {
      if (!classOf[PsiEmptyStatement].isInstance(statement)) {
        return Inspection.NO_PROBLEMS
      }
    }
    return createProblemDescriptor(manager, psiMethod)
  }

  private def createProblemDescriptor(manager: InspectionManager, psiMethod: PsiMethod): Array[ProblemDescriptor] = {
    Array(manager.createProblemDescriptor(psiMethod.getNameIdentifier(), getDisplayName, null.asInstanceOf[LocalQuickFix], ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
  }
}
