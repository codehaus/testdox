package org.codehaus.testdox.intellij

import com.intellij.openapi.actionSystem.Presentation

abstract class AbstractTestElement extends TestElement with Ordered[TestElement] {

  override val psiElement = NullPsiElement.INSTANCE

  def jumpToPsiElement() = false

  def update(presentation: Presentation) { presentation.setEnabled(false) }

  def rename(controller: TestDoxController) {}

  def delete(controller: TestDoxController) {}

//  override def <  (that: TestElement): Boolean = (this compare that) <  0
//  override def >  (that: TestElement): Boolean = (this compare that) >  0
//  override def <= (that: TestElement): Boolean = (this compare that) <= 0
//  override def >= (that: TestElement): Boolean = (this compare that) >= 0

  def compare(that: TestElement): Int = if (this eq that) 0 else -1

//  override def compareTo(that: TestElement): Int = compare(that)

  override def toString = displayString
}
