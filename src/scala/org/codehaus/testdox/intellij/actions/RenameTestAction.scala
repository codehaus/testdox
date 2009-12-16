package org.codehaus.testdox.intellij.actions

import com.intellij.psi.PsiElement

import org.codehaus.testdox.intellij.IconHelper
import org.codehaus.testdox.intellij.IconHelper._
import org.codehaus.testdox.intellij.TestDoxController
import org.codehaus.testdox.intellij.panel.TestDoxToolWindowUI

class RenameTestAction(useFromTestDoxToolWindow: Boolean)
    extends BaseTestMethodIntentionAction("Rename Test", "Renames the current test", getIcon(IconHelper.RENAME_ICON), useFromTestDoxToolWindow) {

  def this() = this(BaseAction.DO_NOT_USE_FROM_TESTDOX_TOOL_WINDOW)

  protected def execute(controller: TestDoxController, targetPsiElement: PsiElement) {
    controller.startRename(targetPsiElement)
  }

  protected def executeUsingTestDoxToolWindow(toolWindow: TestDoxToolWindowUI) {
    toolWindow.renameSelectedTestElement()
  }
}

object RenameTestAction {
  val ID = "TestDox.RenameTest"
}
