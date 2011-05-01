package org.codehaus.testdox.intellij.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import org.codehaus.testdox.intellij.Icons._
import org.codehaus.testdox.intellij.Icons

class ToggleQuickDoxAction extends BaseAction("Show QuickDox", "Shows TestDox in a tooltip", getIcon(Icons.DOX_ICON)) {

  def actionPerformed(event: AnActionEvent) { actionEvents.getTestDoxController(event).toggleQuickDox() }
}
