package org.codehaus.testdox.intellij

import com.intellij.openapi.actionSystem.Presentation
import com.intellij.psi.PsiMethod
import javax.swing.Icon

class TestMethod(psiMethod: PsiMethod, editorApi: EditorApi, sentenceManager: SentenceManager)
    extends AbstractTestElement {

  def methodName: String = psiMethod.getName()

  override val psiElement = psiMethod

  override def jumpToPsiElement(): Boolean = editorApi.jumpToPsiElement(psiElement)

  override def displayString: String = sentenceManager.buildSentence(methodName)

  override def icon: Icon = IconHelper.getIcon(IconHelper.DOX_ICON)

  override def updatePresentation(presentation: Presentation) = presentation.setEnabled(true)

  override def rename(controller: TestDoxController) = controller.startRename(this)

  override def delete(controller: TestDoxController) = editorApi.delete(psiElement)

  override def compareTo(o: TestElement): Int = {
    if (o.isInstanceOf[TestMethod])
      displayString.compareTo(o.displayString)
    else
      0
  }
}

object TestMethod {

  val EMPTY_ARRAY = new Array[TestMethod](0)
}
