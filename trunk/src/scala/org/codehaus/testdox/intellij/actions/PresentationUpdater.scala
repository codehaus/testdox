package org.codehaus.testdox.intellij.actions

import com.intellij.openapi.actionSystem.Presentation

trait PresentationUpdater {

  def update(presentation: Presentation): Unit
}
