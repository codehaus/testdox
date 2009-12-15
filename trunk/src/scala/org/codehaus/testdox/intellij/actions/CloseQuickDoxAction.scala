package org.codehaus.testdox.intellij.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import org.codehaus.testdox.intellij.IconHelper
import org.codehaus.testdox.intellij.IconHelper._

class CloseQuickDoxAction extends BaseAction("Close QuickDox", "Closes the TestDox tooltip", getIcon(IconHelper.DOX_ICON)) {

  def actionPerformed(event: AnActionEvent) {
    actionEvents.getTestDoxController(event).closeQuickDox()
  }
}
