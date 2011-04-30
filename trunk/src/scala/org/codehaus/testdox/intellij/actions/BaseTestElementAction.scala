package org.codehaus.testdox.intellij.actions

import javax.swing.Icon

import com.intellij.openapi.actionSystem.AnActionEvent

abstract class BaseTestElementAction(text: String, description: String, icon: Icon, protected val useFromTestDoxToolWindow: Boolean)
    extends BaseAction(text, description, icon) {

  override def update(event: AnActionEvent) {
    selectController(event).update(event.getPresentation)
  }

  private def selectController(event: AnActionEvent) = {
    if (useFromTestDoxToolWindow)
      actionEvents.getToolWindowUI(event)
    else
      actionEvents.getTestDoxController(event)
  }
}
