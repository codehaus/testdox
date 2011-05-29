package org.codehaus.testdox.intellij.ui

import org.codehaus.testdox.intellij.{TestDoxClass, TestClass, EditorApi, Mocks}
import org.codehaus.testdox.intellij.Mocks.MockableTestClass
import org.codehaus.testdox.intellij.config.Configuration
import org.specs.SpecificationWithJUnit
import org.specs.mock.{ClassMocker, JMocker}

object QuickDoxDialogSpec extends SpecificationWithJUnit with JMocker with ClassMocker {

  "QuickDoxDialog" should {
    "only be visible when explicitly shown" in {
      val testClass = mock[MockableTestClass]
      val testDoxClass = createTestDoxFileRepresentingAProjectClass(testClass)

      expect { allowing(testClass).displayString() will returnValue("FooClass") }

      val configuration = new Configuration()
      val model = new TestDoxTableModel(configuration)
      testDoxClass.updateModel(model)

      val editorApi = mock[EditorApi]
      val dialog = new QuickDoxDialog(null, editorApi, model, configuration)

      expect { exactly(2).of(editorApi).activateSelectedTextEditor() }

      dialog.isVisible must be (false)

      dialog.show()
      dialog.isVisible must be (true)

      dialog.hide()
      dialog.isVisible must be (false)

      configuration.removePropertyChangeListener(dialog)
    }
  }

  private def createTestDoxFileRepresentingAProjectClass(testClass: TestClass) =
    new TestDoxClass(null, "blarg", true, testClass, null, Array(Mocks.createTestMethod("foo"), Mocks.createTestMethod("bar"), Mocks.createTestMethod("baz")))
}
