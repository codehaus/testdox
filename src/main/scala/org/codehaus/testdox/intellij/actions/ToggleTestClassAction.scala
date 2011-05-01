package org.codehaus.testdox.intellij.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import org.codehaus.testdox.intellij.Icons
import org.codehaus.testdox.intellij.Icons._

class ToggleTestClassAction extends BaseAction("Toggle Class/Test", "Switches back and forth between a class and its unit test class", getIcon(Icons.DOX_ICON)) {

  def actionPerformed(event: AnActionEvent) { actionEvents.getTestDoxController(event).toggleTestClassAndTestedClass() }

  override def update(event: AnActionEvent) { event.getPresentation.setEnabled(actionEvents.getTestDoxController(event).canCurrentFileBeUnitTested) }
}
