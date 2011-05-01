package org.codehaus.testdox.intellij.actions

import com.intellij.openapi.actionSystem.AnActionEvent

import org.codehaus.testdox.intellij.Icons
import org.codehaus.testdox.intellij.Icons._

class RefreshTestDoxPanelAction(useFromTestDoxToolWindow: Boolean)
    extends BaseTestElementAction("Refresh", "Refresh TestDox", getIcon(Icons.REFRESH_ICON), useFromTestDoxToolWindow) {

  def this() = this(BaseAction.DO_NOT_USE_FROM_TESTDOX_TOOL_WINDOW)

  def actionPerformed(event: AnActionEvent) { actionEvents.getTestDoxController(event).refreshToolWindow() }
}

object RefreshTestDoxPanelAction {
  val ID = "TestDox.RefreshTestDoxPanel"
}
