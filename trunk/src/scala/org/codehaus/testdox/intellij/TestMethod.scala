package org.codehaus.testdox.intellij

import com.intellij.openapi.actionSystem.Presentation
import com.intellij.psi.PsiMethod
import javax.swing.Icon

class TestMethod(psiMethod: PsiMethod, editorApi: EditorApi, sentenceManager: SentenceManager) extends AbstractTestElement {

  def methodName: String = psiMethod.getName()

  override val psiElement = psiMethod

  override def jumpToPsiElement(): Boolean = editorApi.jumpToPsiElement(psiElement)

  override def displayString: String = sentenceManager.buildSentence(methodName)

  override def icon: Icon = Icons.getIcon(Icons.DOX_ICON)

  override def update(presentation: Presentation) = presentation.setEnabled(true)

  override def rename(controller: TestDoxController) = controller.startRename(this)

  override def delete(controller: TestDoxController) = editorApi.delete(psiElement)

  override def compare(that: TestElement): Int = {
    if (that.isInstanceOf[TestMethod])
      this.displayString.compareTo(that.displayString)
    else
      0
  }
}

object TestMethod {

  val EMPTY_ARRAY = new Array[TestMethod](0)
}
