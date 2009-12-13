package org.codehaus.testdox.intellij

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.refactoring.listeners.RefactoringElementListener

import org.codehaus.testdox.intellij.config.ConfigurationBean

class ClassShadowingManager(psiElement: PsiClass, testDoxFileFactory: TestDoxFileFactory, editorApi: EditorApi, config: ConfigurationBean, nameResolver: NameResolver)
    extends RefactoringElementListener {

  private val originalFile = testDoxFileFactory.getTestDoxFile(editorApi.getVirtualFile(psiElement))

  def elementMoved(psiElement: PsiElement) {
    if (isValidShadowing(psiElement)) {
      val newPackage = psiElement.getContainingFile().getContainingDirectory()
      editorApi.move(originalFile.testClass.psiElement.asInstanceOf[PsiClass], newPackage)
    }
  }

  def elementRenamed(psiElement: PsiElement) {
    if (isValidShadowing(psiElement)) {
      val newTestClassName = nameResolver.getTestClassName(psiElement.asInstanceOf[PsiClass].getName())
      editorApi.rename(originalFile.testClass.psiElement, newTestClassName)
    }
  }

  private def isValidShadowing(psiElement: PsiElement) = {
    isValidClass(psiElement) &&
        config.isAutoApplyChangesToTest() &&
        nameResolver.isRealClass(psiElement.asInstanceOf[PsiClass].getName()) &&
        originalFile.canNavigateToTestClass
  }

  private def isValidClass(psiElement: PsiElement) = {
    psiElement.isInstanceOf[PsiClass] && !isInnerClass(psiElement.asInstanceOf[PsiClass])
  }

  private def isInnerClass(psiClass: PsiClass) = {
    psiClass.getContainingFile().getName().indexOf(psiClass.getName()) < 0
  }
}
