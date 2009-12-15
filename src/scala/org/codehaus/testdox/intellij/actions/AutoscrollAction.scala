package org.codehaus.testdox.intellij.actions

import com.intellij.openapi.actionSystem.AnActionEvent

import org.codehaus.testdox.intellij.IconHelper
import org.codehaus.testdox.intellij.IconHelper._

class AutoscrollAction(private var selected: Boolean, useFromTestDoxToolWindow: Boolean)
    extends BaseToggleAction("Autoscroll To Source", "Toggle autoscrolling", getIcon(IconHelper.AUTOSCROLL_ICON), useFromTestDoxToolWindow) {

  def this() = this (false, false)

  def isSelected(event: AnActionEvent) = selected

  def setSelected(event: AnActionEvent, selected: Boolean) {
    this.selected = selected
    if (event != null) actionEvents.getTestDoxController(event).updateAutoscroll(selected)
  }
}

object AutoscrollAction {
  val ID = "TestDox.Autoscroll"
}
