package org.codehaus.testdox.intellij

import org.codehaus.testdox.intellij.TestDoxInterface.TEST_ELEMENT
import org.codehaus.testdox.intellij.config.Configuration
import org.codehaus.testdox.intellij.ui.TestDoxTableModel
import org.specs.mock.JMocker
import org.specs.SpecificationWithJUnit

object TestDoxInterfaceSpec extends SpecificationWithJUnit with JMocker {

  "TestDoxInterface's test element" should {

    "have a textual representation" in {
      TEST_ELEMENT.displayString must be equalTo "<font color=\"gray\">Interfaces do not have unit tests.</font>"
    }

    "use the warning icon as its graphical representation" in {
      TEST_ELEMENT.icon must be(Icons.getIcon(Icons.WARNING_ICON))
    }

    "updateCallbackHook invokes the relevant method on the TestDox model" in {
      val editorApi = mock[EditorApi]
      val configuration = new Configuration()

      configuration.setTestNameTemplate(TemplateNameResolver.DEFAULT_TEMPLATE)

      val className = "com.acme.SomeInterface"
      val testInterface = new TestInterface(className, null, editorApi, new TemplateNameResolver(configuration))
      val testDoxInterface = new TestDoxInterface(null, className, testInterface, null)
      val testDoxModel = new TestDoxTableModel(configuration)

      testDoxInterface.updateModel(testDoxModel)

      testDoxModel.hasDox must be equalTo false // interfaces should not have TestDox data
      testDoxModel.getValueAt(0, 0) must be equalTo(testInterface)
      testDoxModel.getValueAt(1, 0) must be equalTo(TEST_ELEMENT)
    }
  }
}
