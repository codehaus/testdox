package org.codehaus.testdox.intellij.actions

import com.intellij.openapi.actionSystem.AnActionEvent

import org.codehaus.testdox.intellij.Icons
import org.codehaus.testdox.intellij.Icons._

class ActivateTestDoxPanelAction extends BaseAction("Toggle TestDox", "Shows/hides the TestDox panel for the current class", getIcon(Icons.TESTDOX_ICON)) {

  def actionPerformed(event: AnActionEvent) { actionEvents.getTestDoxController(event).toggleToolWindow() }
}
