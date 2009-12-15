package org.codehaus.testdox.intellij.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import org.codehaus.testdox.intellij.IconHelper._
import org.codehaus.testdox.intellij.IconHelper

class ToggleQuickDoxAction extends BaseAction("Show QuickDox", "Shows TestDox in a tooltip", getIcon(IconHelper.DOX_ICON)) {

  def actionPerformed(event: AnActionEvent) {
    actionEvents.getTestDoxController(event).toggleQuickDox()
  }
}
