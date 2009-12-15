package org.codehaus.testdox.intellij.actions

import javax.swing.Icon

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.ToggleAction

abstract class BaseToggleAction(text: String, description: String, icon: Icon, useFromTestDoxToolWindow: Boolean)
    extends ToggleAction(text, description, icon) {

  protected val actionEvents = ActionEvents.instance

  override def update(event: AnActionEvent) {
    super.update(event)
    selectController(event).update(event.getPresentation())
  }

  private def selectController(event: AnActionEvent) = {
    if (useFromTestDoxToolWindow)
      actionEvents.getTestDoxToolWindowUI(event)
    else
      actionEvents.getTestDoxController(event)
  }
}
