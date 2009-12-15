package org.codehaus.testdox.intellij.actions

import javax.swing.Icon

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

abstract class BaseAction(text: String, description: String, icon: Icon) extends AnAction(text, description, icon) {

  getTemplatePresentation().setEnabled(false)

  protected val actionEvents = ActionEvents.instance

  override def update(event: AnActionEvent) {
    event.getPresentation().setEnabled(actionEvents.getTestDoxController(event).hasActiveEditors())
  }
}

object BaseAction {
  val DO_NOT_USE_FROM_TESTDOX_TOOL_WINDOW = false
}
