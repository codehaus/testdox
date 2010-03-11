package org.codehaus.testdox.intellij.actions

import com.intellij.psi.PsiElement

import org.codehaus.testdox.intellij.Icons
import org.codehaus.testdox.intellij.Icons._
import org.codehaus.testdox.intellij.TestDoxController
import org.codehaus.testdox.intellij.ui.ToolWindowUI

class RenameTestAction(useFromTestDoxToolWindow: Boolean)
    extends BaseTestMethodIntentionAction("Rename Test", "Renames the current test", getIcon(Icons.RENAME_ICON), useFromTestDoxToolWindow) {

  def this() = this(BaseAction.DO_NOT_USE_FROM_TESTDOX_TOOL_WINDOW)

  protected def execute(controller: TestDoxController, targetPsiElement: PsiElement) {
    controller.startRename(targetPsiElement)
  }

  protected def executeUsingTestDoxToolWindow(toolWindow: ToolWindowUI) {
    toolWindow.renameSelectedTestElement()
  }
}

object RenameTestAction {
  val ID = "TestDox.RenameTest"
}
