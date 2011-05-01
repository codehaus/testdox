package org.codehaus.testdox.intellij

import javax.swing.Icon;

import com.intellij.openapi.actionSystem.Presentation
import com.intellij.psi.PsiElement

trait TestElement extends Ordered[TestElement] {

  def psiElement: PsiElement

  def jumpToPsiElement(): Boolean

  def displayString: String

  def icon: Icon

  def update(presentation: Presentation)

  def rename(controller: TestDoxController)

  def delete(controller: TestDoxController)

  def compare(that: TestElement): Int
}
