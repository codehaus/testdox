package org.codehaus.testdox.intellij

import javax.swing.Icon;

import com.intellij.openapi.actionSystem.Presentation
import com.intellij.psi.PsiElement

trait TestElement extends Comparable[TestElement] {

  def psiElement: PsiElement

  def jumpToPsiElement(): Boolean

  def displayString: String

  def icon: Icon

  def updatePresentation(presentation: Presentation): Unit

  def rename(controller: TestDoxController): Unit

  def delete(controller: TestDoxController): Unit
}