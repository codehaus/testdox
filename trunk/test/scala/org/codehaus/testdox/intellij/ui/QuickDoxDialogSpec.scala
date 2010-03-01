package org.codehaus.testdox.intellij.ui

import org.codehaus.testdox.intellij.config.Configuration
import org.codehaus.testdox.intellij.{TestDoxClass, TestClass, EditorApi, Mocks}
import org.codehaus.testdox.intellij.Mocks.MockableTestClass
import org.specs.Specification
import org.specs.mock.Mockito

class QuickDoxDialogSpec extends Specification with Mockito {

  "QuickDoxDialog" should {
    "only be visible when explicitly shown" in {
      val testClass = mock[MockableTestClass]
      testClass.displayString returns "FooClass"

      val editorApi = mock[EditorApi]
      val configuration = new Configuration()
      val model = new TestDoxTableModel(configuration)

      val testDoxClass = createTestDoxFileRepresentingAProjectClass(testClass)
      testDoxClass.updateModel(model)

      val dialog = new QuickDoxDialog(null, editorApi, model, configuration)
      dialog.isVisible() must be (false)

      dialog.show()
      dialog.isVisible() must be (true)

      dialog.hide()
      dialog.isVisible() must be (false)

      configuration.removePropertyChangeListener(dialog)
      editorApi.activateSelectedTextEditor was called.times(2)
    }
  }

  private def createTestDoxFileRepresentingAProjectClass(testClass: TestClass) =
    new TestDoxClass(null, "blarg", true, testClass, null,
      Array(Mocks.createTestMethod("foo"), Mocks.createTestMethod("bar"), Mocks.createTestMethod("baz"))
    )
}
