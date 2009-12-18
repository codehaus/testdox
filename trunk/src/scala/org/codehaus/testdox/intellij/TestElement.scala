package org.codehaus.testdox.intellij

import javax.swing.Icon;

import com.intellij.openapi.actionSystem.Presentation
import com.intellij.psi.PsiElement

trait TestElement extends Ordered[TestElement] {

  def psiElement: PsiElement

  def jumpToPsiElement(): Boolean

  def displayString: String

  def icon: Icon

  def update(presentation: Presentation): Unit

  def rename(controller: TestDoxController): Unit

  def delete(controller: TestDoxController): Unit
}
