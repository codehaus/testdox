package org.codehaus.testdox.intellij

import junit.framework.Assert._
import junit.framework.TestCase
import org.codehaus.testdox.intellij.actions.RenameTestAction
import org.intellij.openapi.testing.MockApplicationManager
import javax.swing.Icon

class AbstractTestElementTest extends TestCase {

  private val abstractTestElement = new AbstractTestElement() {
    override val displayString = "dummy"
    override val icon: Icon = null
  }

  def testIsAssociatedToANullPsiElementByDefault() {
    MockApplicationManager.reset()
    assertSame(NullPsiElement.INSTANCE, abstractTestElement.psiElement)
  }

  def testDoesNotJumpToPsiElementByDefault() {
    assertFalse(abstractTestElement.jumpToPsiElement())
  }

  def testUsesReferenceEqualityToDefineTheDefaultNaturalOrder() {
    assertEquals(-1, abstractTestElement.compareTo(new AbstractTestElement() {
      override val displayString = "dummy"
      override val icon: Icon = null
    }))

    assertEquals(0, abstractTestElement.compareTo(abstractTestElement))
  }

  def testAlwaysDisablesTheRepresentationOfAnActionWhenAskedToUpdateIt() {
    val presentation = new RenameTestAction().getTemplatePresentation()
    presentation.setEnabled(true)

    abstractTestElement.update(presentation)
    assertFalse("action representation should have been disabled", presentation.isEnabled())
  }

  def testDoesNothingWhenAskedToRenameTheUnderlyingTestedClass() {
    abstractTestElement.rename(null)
  }

  def testDoesNothingWhenAskedToDeleteTheUnderlyingTestedClass() {
    abstractTestElement.delete(null)
  }
}
