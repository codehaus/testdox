package org.codehaus.testdox.intellij

import com.intellij.openapi.actionSystem.Presentation
import com.intellij.psi.PsiClass

class TestClass(className: String, psiClass: PsiClass, editorApi: EditorApi, nameResolver: NameResolver)
    extends AbstractTestElement {

  def displayString: String = "<b>" + nameResolver.getRealClassNameForDisplay(className) + ":</b>"

  override val psiElement = psiClass

  override def jumpToPsiElement(): Boolean = editorApi.jumpToPsiClass(psiClass)

  def isTestClass = psiClass != null && nameResolver.isTestClass(psiClass.getName())

  val icon = {
    if (psiClass != null)
      Icons.getIcon(Icons.CLASS_ICON)
    else
      Icons.getLockedIcon(Icons.CLASS_ICON)
  }

  override def update(presentation: Presentation) {
    presentation.setEnabled(true);
  }

  override def rename(controller: TestDoxController) {
    editorApi.rename(testedClass);
  }

  override def delete(controller: TestDoxController) {
    editorApi.delete(testedClass);
  }

  override def compare(that: TestElement): Int = {
    if (that.isInstanceOf[TestClass])
      this.displayString.compareTo(that.displayString)
    else
      1
  }

  override def equals(o: Any) = o.isInstanceOf[TestClass] && compareTo(o.asInstanceOf[TestElement]) == 0

  private def testedClass: PsiClass = editorApi.getPsiClass(nameResolver.getRealClassName(psiClass.getQualifiedName()))
}
