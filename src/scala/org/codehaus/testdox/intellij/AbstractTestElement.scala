package org.codehaus.testdox.intellij

import com.intellij.openapi.actionSystem.Presentation

abstract class AbstractTestElement extends TestElement {

  override val psiElement = NullPsiElement.INSTANCE

  def jumpToPsiElement() = false

  def update(presentation: Presentation): Unit = presentation.setEnabled(false)

  def rename(controller: TestDoxController) {}

  def delete(controller: TestDoxController) {}

  def compare(that: TestElement): Int = if (this == that) 0 else -1

  override def toString = displayString
}
