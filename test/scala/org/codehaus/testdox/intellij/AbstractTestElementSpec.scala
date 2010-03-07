package org.codehaus.testdox.intellij

import org.codehaus.testdox.intellij.actions.RenameTestAction
import org.intellij.openapi.testing.MockApplicationManager
import javax.swing.Icon
import org.specs.Specification
import org.specs.mock.Mockito

object AbstractTestElementSpec extends Specification with Mockito {

  private val testElement = new AbstractTestElement() {
    override val displayString = "dummy"
    override val icon: Icon = null
  }

  "A test element" should {
    "be associated to a null PsiElement by default" in {
      MockApplicationManager.reset()
      testElement.psiElement must be (NullPsiElement.INSTANCE)
    }

    "not jump to a PsiElement by default" in {
      testElement.jumpToPsiElement() must be (false)
    }

    "use reference equality to define the default natural order" in {
      val other = new AbstractTestElement() {
        override val displayString = "dummy"
        override val icon: Icon = null
      }
      testElement.compareTo(other) must be equalTo -1
      testElement.compareTo(testElement) must be equalTo 0
    }

    "always disable the representation of an action when asked to update it" in {
      val presentation = new RenameTestAction().getTemplatePresentation()
      presentation.setEnabled(true)
      testElement.update(presentation)
      presentation.isEnabled() must be (false)
    }

    "do nothing when asked to rename the underlying tested class" in {
      val controller = mock[TestDoxController]
      testElement.rename(controller)
      controller had noMoreCalls
    }

    "do nothing when asked to delete the underlying tested class" in {
      val controller = mock[TestDoxController]
      testElement.delete(controller)
      controller had noMoreCalls
    }
  }
}
