package org.codehaus.testdox.intellij.actions

import com.intellij.openapi.actionSystem.AnActionEvent

import org.codehaus.testdox.intellij.IconHelper
import org.codehaus.testdox.intellij.IconHelper._

class AutoScrollAction(private var selected: Boolean, useFromTestDoxToolWindow: Boolean)
    extends BaseToggleAction("Auto-scroll To Source", "Toggle auto-scrolling", getIcon(IconHelper.AUTO_SCROLL_ICON), useFromTestDoxToolWindow) {

  def this() = this(false, false)

  def isSelected(event: AnActionEvent) = selected

  def setSelected(event: AnActionEvent, selected: Boolean) {
    this.selected = selected
    if (event != null) actionEvents.getTestDoxController(event).updateAutoScroll(selected)
  }
}

object AutoScrollAction {
  val ID = "TestDox.AutoScroll"
}
