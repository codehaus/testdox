package org.codehaus.testdox.intellij.actions

import com.intellij.openapi.actionSystem.AnActionEvent

import org.codehaus.testdox.intellij.IconHelper
import org.codehaus.testdox.intellij.IconHelper._

class ActivateTestDoxPanelAction extends BaseAction("Toggle TestDox", "Shows/hides the TestDox panel for the current class", getIcon(IconHelper.TESTDOX_ICON)) {

  def actionPerformed(event: AnActionEvent) {
    actionEvents.getTestDoxController(event).toggleToolWindow()
  }
}
