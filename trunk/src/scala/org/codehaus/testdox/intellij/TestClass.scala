package org.codehaus.testdox.intellij

import javax.swing.Icon
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.psi.PsiClass

class TestClass(className: String, psiClass: PsiClass, editorApi: EditorApi, nameResolver: NameResolver)
    extends AbstractTestElement {

  def displayString: String = "<b>" + nameResolver.getRealClassNameForDisplay(className) + ":</b>"

  override def psiElement = psiClass

  override def jumpToPsiElement(): Boolean = editorApi.jumpToPsiClass(psiClass)

  def icon: Icon = {
    if (psiClass != null)
      IconHelper.getIcon(IconHelper.CLASS_ICON)
    else
      IconHelper.getLockedIcon(IconHelper.CLASS_ICON)
  }

  override def updatePresentation(presentation: Presentation) {
    presentation.setEnabled(true);
  }

  override def rename(controller: TestDoxController) {
    editorApi.rename(testedClass);
  }

  override def delete(controller: TestDoxController) {
    editorApi.delete(testedClass);
  }

  def isTestClass() = psiClass != null && nameResolver.isTestClass(psiClass.getName())

  override def compareTo(o: TestElement): Int = {
    if (o.isInstanceOf[TestClass])
      displayString.compareTo(o.displayString)
    else
      1
  }

  override def equals(o: Any) = o.isInstanceOf[TestClass] && compareTo(o.asInstanceOf[TestElement]) == 0

  private def testedClass: PsiClass = editorApi.getPsiClass(nameResolver.getRealClassName(psiClass.getQualifiedName()))
}
