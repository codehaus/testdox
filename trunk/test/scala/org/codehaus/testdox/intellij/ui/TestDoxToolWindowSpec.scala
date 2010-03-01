package org.codehaus.testdox.intellij.ui

import org.specs.mock.Mockito
import org.specs.Specification
import org.codehaus.testdox.intellij.config.Configuration
import javax.swing.{JTable, JPanel}
import java.awt.event.{KeyEvent, MouseEvent}
import com.intellij.psi.PsiMethod
import org.codehaus.testdox.intellij._
import org.intellij.openapi.testing.MockApplicationManager

class TestDoxToolWindowSpec extends Specification with Mockito {

  private val controller = mock[TestDoxController]
  private val actionToolbarComponent = new JPanel()
  private val configuration = new Configuration()
  private val model = new TestDoxTableModel(configuration)
  private val table = new JTable()

  private val TEST_METHODS = Array(Mocks.createTestMethod("testOne"), Mocks.createTestMethod("testTwo"), Mocks.createTestMethod("testThree"))

  private var window: TestDoxToolWindow = _

  doBefore {
    configuration.setUnderscoreMode(true)
    table.setModel(model)

    controller.getConfiguration() returns configuration
    window = new TestDoxToolWindow(controller, table, actionToolbarComponent) {
      override def handleSelection() {}
    }

    model.addTableModelListener(window)
    controller.getModel() returns model
  }

  doAfter {
    configuration.removePropertyChangeListener(window)
    model.removeTableModelListener(window)
  }

  "TestDoxToolWindow" should {

    "show no dox message and disable the list of dox when setting Java file which has no dox" in {
      updateTestDoxModelUsingTestDoxFile(new TestDoxClass(null, "foo", true, Mocks.createTestClass(), null, TestMethod.EMPTY_ARRAY))
      table.isEnabled() must_== false
      table.getModel().getRowCount() must_== 2
    }

    "clears previous dox list when setting new dox" in {
      updateTestDoxModelUsingTestDoxFile(new TestDoxNonJavaFile(null))
      assertNoDox(table, TestDoxNonJavaFile.TEST_ELEMENT)
    }

    "shows no class message and disables dox list when setting no file" in {
      model.setNotJava()
      assertNoDox(table, TestDoxNonJavaFile.TEST_ELEMENT)
    }

    "shows no dox message at startup" in {
      model.setNotJava()
      assertNoDox(table, TestDoxNonJavaFile.TEST_ELEMENT)
    }

    "shows method based dox even if the TestDox file has no class references" in {
      updateTestDoxModelUsingTestDoxFile(new TestDoxClass(null, "foo", true, Mocks.createTestClass(), null, TEST_METHODS))
      assertDox(table, TEST_METHODS)
    }

    "navigates to source on double click" in {
      updateTestDoxModelUsingTestDoxFile(new TestDoxClass(null, "foo", true, Mocks.createTestClass(), null, TEST_METHODS))
      table.changeSelection(1, -1, false, false)

      controller.jumpToTestElement(TEST_METHODS(0), false)

      window.handleMouseEvent(new MouseEvent(table, 0, System.currentTimeMillis(), 0, 0, 0, 2, false))
      controller.jumpToTestElement(TEST_METHODS(0), false) was called
    }

    "forwards navigate to source command to project component if ENTER key is pressed" in {
      controller.jumpToTestElement(TEST_METHODS(0), false)

      updateTestDoxModelUsingTestDoxFile(new TestDoxClass(null, "foo", true, Mocks.createTestClass(), null, TEST_METHODS))
      table.changeSelection(1, -1, false, false)
      window.handleKeyEvent(createKeyEvent(KeyEvent.VK_ENTER, KeyEvent.VK_UNDEFINED))
    }

    "forwards rename command to project component if rename key is pressed" in {
      controller.startRename(TEST_METHODS(0))

      initialisePanelAndSelectFirstRow(TEST_METHODS)
      table.changeSelection(1, 1, false, false)
      window.handleKeyEvent(createKeyEvent(KeyEvent.VK_F6, KeyEvent.VK_UNDEFINED))
    }

    "ignores rename key event if selected item is not a test element" in {
      model.setNotJava()
      table.changeSelection(0, 0, false, false)
      window.handleKeyEvent(createKeyEvent(KeyEvent.VK_F6, KeyEvent.VK_UNDEFINED))
    }

    "uses the selected test element to delete itself when the delete key is pressed" in {
      MockApplicationManager.reset()
      val psiMethod = mock[PsiMethod]
      val editorApi = mock[EditorApi]

      psiMethod.getName() returns "testSomething"
      editorApi.delete(psiMethod)

      initialisePanelAndSelectFirstRow(Array(new TestMethod(psiMethod, editorApi, new SentenceManager(new Configuration()))))
      window.handleKeyEvent(createKeyEvent(KeyEvent.VK_DELETE, KeyEvent.VK_UNDEFINED))

      editorApi.delete(psiMethod) was called
    }
  }

  private def initialisePanelAndSelectFirstRow(testMethods: Array[TestMethod]) {
    updateTestDoxModelUsingTestDoxFile(new TestDoxClass(null, "foo", true, Mocks.createTestClass(), null, testMethods))
    table.changeSelection(1, -1, false, false)
  }

  private def assertDox(table: JTable, dox: Array[TestMethod]) = {
    table.isEnabled() must_== true

    val tableModel = table.getModel()
    tableModel.getRowCount() must_== dox.length + 1

    for (i <- 1 until tableModel.getRowCount()) {
      tableModel.getValueAt(i, 1).asInstanceOf[TestElement].displayString must_== dox(i - 1).displayString
    }
  }

  private def assertNoDox(table: JTable, noDoxElements: TestElement*) {
    table.isEnabled() must_== false
    table.getModel().getRowCount() must_== noDoxElements.length
    for (i <- 0 until noDoxElements.length) {
      table.getModel().getValueAt(i, 0) must_== noDoxElements(i)
    }
  }

  private def updateTestDoxModelUsingTestDoxFile(file: TestDoxFile) = file.updateModel(model)

  private def createKeyEvent(code: Int, modifiers: Int) = new KeyEvent(table, 0, System.currentTimeMillis(), modifiers, code, code.toChar())
}
  