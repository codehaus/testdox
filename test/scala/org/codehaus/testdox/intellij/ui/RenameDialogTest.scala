package org.codehaus.testdox.intellij.ui

import com.intellij.openapi.project.Project
import junitx.framework.ObjectAssert
import org.intellij.openapi.testing.DialogCreator
import org.intellij.openapi.testing.MockApplicationManager
import org.jmock.MockObjectTestCase

import java.awt._
import javax.swing._
import junit.framework.Assert._

class RenameDialogTest extends MockObjectTestCase {

  protected val projectMock = mock(classOf[Project]).proxy().asInstanceOf[Project]
  private[ui] var dialog: RenameDialog = null

  override protected def setUp() {
      MockApplicationManager.clear()
      dialog = createDialog()
  }

  def testPlacesDefaultKeyboardFocusOnTheSentenceField() {
    val expectedSentence = "some random sentence"
    dialog.setSentence(expectedSentence)

    val preferredFocusedComponent = dialog.getPreferredFocusedComponent()
    ObjectAssert.assertInstanceOf("focused component type", classOf[JTextField], preferredFocusedComponent)

    val sentenceField = preferredFocusedComponent.asInstanceOf[JTextField]
    assertEquals("sentence", expectedSentence, sentenceField.getText())
  }

  def testEnablesOkIfInitialSentenceIsValid() {
    dialog.setSentence("foo bar baz")
    assertTrue(dialog.isOKActionEnabled())
  }

  def testDisablesOkIfTheFirstCharacterOfTheSentenceIsNotAValidStartOfAJavaIdentifier() {
    assertTrue("OK action should be enabled", dialog.isOKActionEnabled())
    dialog.setSentence("-foo")
    assertFalse("OK action should no longer be enabled", dialog.isOKActionEnabled())
  }

  def testDisablesOkIfTheRemainingOfTheTypedSentenceContainsACharacterThatCannotBeUsedInAJavaIdentifier() {
    assertTrue("OK action should be enabled", dialog.isOKActionEnabled())
    dialog.handleRename("mo&o")
    assertFalse("OK action should no longer be enabled", dialog.isOKActionEnabled())
  }

  def testReenablesOkWhenAnInvalidCharacterIsRemovedFromTheTypedSentence() {
    assertTrue("OK action should be enabled", dialog.isOKActionEnabled())

    val sentenceField = dialog.getPreferredFocusedComponent().asInstanceOf[JTextField]
    sentenceField.getDocument().insertString(0, "mo&o", null)
    assertFalse("OK action should no longer be enabled", dialog.isOKActionEnabled())

    sentenceField.getDocument().remove(2, 1)
    assertTrue("OK action should be re-enabled", dialog.isOKActionEnabled())
  }

  def testDisposesAndReturnsOriginalSentenceOnCancel() {
    val sentence = "foo"
    dialog.setSentence(sentence)
    EventQueue.invokeAndWait(new Runnable() {
      def run() {
        dialog.doCancelAction()
      }
    })
    assertEquals(sentence, dialog.sentence)
  }

  def testDisposesAndReturnsTypedSentenceOnOk() {
    val newName = "bar"
    dialog.setSentence("foo")
    dialog.handleRename(newName)
    EventQueue.invokeAndWait(new Runnable() {
      def run() {
        dialog.doOKAction()
      }
    })
    assertEquals(newName, dialog.sentence)
  }

  private[ui] def createDialog(): RenameDialog = {
    val dialogCreator = createDialogCreator()
    EventQueue.invokeAndWait(dialogCreator)
    val renameDialog = dialogCreator.getDialog()
    renameDialog.createCenterPanel()
    return renameDialog
  }

  protected def createDialogCreator() = new DialogCreator[RenameDialog]() {
    def create(): RenameDialog = new RenameDialog(projectMock, "")
  }
}
