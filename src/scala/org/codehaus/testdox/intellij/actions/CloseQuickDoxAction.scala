package org.codehaus.testdox.intellij.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import org.codehaus.testdox.intellij.Icons
import org.codehaus.testdox.intellij.Icons._

class CloseQuickDoxAction extends BaseAction("Close QuickDox", "Closes the TestDox tooltip", getIcon(Icons.DOX_ICON)) {

  def actionPerformed(event: AnActionEvent) {
    actionEvents.getTestDoxController(event).closeQuickDox()
  }
}
