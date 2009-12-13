package org.codehaus.testdox.intellij

import com.intellij.openapi.actionSystem.Presentation

abstract class AbstractTestElement extends TestElement {

  val psiElement = NullPsiElement.INSTANCE

  def jumpToPsiElement() = false

  def updatePresentation(presentation: Presentation): Unit = presentation.setEnabled(false)

  def rename(controller: TestDoxController) {}

  def delete(controller: TestDoxController) {}

  def compareTo(o: TestElement): Int = if (this == o) 0 else -1

  override def toString = displayString
}
