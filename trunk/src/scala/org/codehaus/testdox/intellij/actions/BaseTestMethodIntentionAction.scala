package org.codehaus.testdox.intellij.actions

import javax.swing.Icon

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.util.IncorrectOperationException
import org.codehaus.testdox.intellij.TestDoxController
import org.codehaus.testdox.intellij.TestDoxProjectComponent
import org.codehaus.testdox.intellij.panel.TestDoxToolWindowUI
import org.jetbrains.annotations.NotNull

abstract class BaseTestMethodIntentionAction(text: String, description: String, icon: Icon, useFromTestDoxToolWindow: Boolean)
    extends BaseTestElementAction(text, description, icon, useFromTestDoxToolWindow) with IntentionAction {

  override def actionPerformed(event: AnActionEvent) {
    if (useFromTestDoxToolWindow)
      executeUsingTestDoxToolWindow(actionEvents.getTestDoxToolWindowUI(event))
    else
      execute(actionEvents.getTestDoxController(event), actionEvents.getTargetPsiElement(event))
  }

  protected def execute(controller: TestDoxController, targetPsiElement: PsiElement)

  protected def executeUsingTestDoxToolWindow(testDoxToolWindow: TestDoxToolWindowUI)

  override def update(event: AnActionEvent) {
    if (useFromTestDoxToolWindow) {
      actionEvents.getTestDoxToolWindowUI(event).update(event.getPresentation())
    } else {
      val controller = actionEvents.getTestDoxController(event)
      controller.updatePresentation(event.getPresentation(), actionEvents.getTargetPsiElement(event))
    }
  }

  @NotNull
  val getText = getTemplatePresentation.getText

  @NotNull
  val getFamilyName = "TestDox.TestMethodIntentions"

  def isAvailable(project: Project, editor: Editor, file: PsiFile): Boolean = {
    val controller = TestDoxProjectComponent.getInstance(project).getController()
    val psiElement = file.findElementAt(editor.getCaretModel().getOffset())
    return psiElement != null && controller.getEditorApi().isTestMethod(psiElement.getParent())
  }

  @throws(classOf[IncorrectOperationException])
  def invoke(project: Project, editor: Editor, file: PsiFile) {
    execute(TestDoxProjectComponent.getInstance(project).getController(), file.findElementAt(editor.getCaretModel().getOffset()))
  }

  def startInWriteAction(): Boolean = false
}
