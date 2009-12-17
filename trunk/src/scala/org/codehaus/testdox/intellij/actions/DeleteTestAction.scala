package org.codehaus.testdox.intellij.actions

import com.intellij.psi.PsiElement

import org.codehaus.testdox.intellij.IconHelper
import org.codehaus.testdox.intellij.IconHelper._
import org.codehaus.testdox.intellij.TestDoxController
import org.codehaus.testdox.intellij.ui.ToolWindowUI

class DeleteTestAction(useFromTestDoxToolWindow: Boolean)
    extends BaseTestMethodIntentionAction("Delete Test", "Deletes the current test", getIcon(IconHelper.DELETE_ICON), useFromTestDoxToolWindow) {

  def this() = this(BaseAction.DO_NOT_USE_FROM_TESTDOX_TOOL_WINDOW)

  protected def execute(controller: TestDoxController, targetPsiElement: PsiElement) {
    controller.delete(targetPsiElement)
  }

  protected def executeUsingTestDoxToolWindow(toolWindow: ToolWindowUI) {
    toolWindow.deleteSelectedTestElement()
  }
}

object DeleteTestAction {
  val ID = "TestDox.DeleteTest"
}
