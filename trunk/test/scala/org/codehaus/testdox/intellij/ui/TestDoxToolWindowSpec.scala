package org.codehaus.testdox.intellij.ui

import org.specs.mock.Mockito
import org.specs.Specification
import org.codehaus.testdox.intellij.config.Configuration
import javax.swing.{JTable, JPanel}
import java.awt.event.{KeyEvent, MouseEvent}
import com.intellij.psi.PsiMethod
import org.codehaus.testdox.intellij._
import org.intellij.openapi.testing.MockApplicationManager

object TestDoxToolWindowSpec extends Specification with Mockito {

  val controller = mock[TestDoxController]
  val actionToolbarComponent = new JPanel()
  val configuration = new Configuration()
  val model = new TestDoxTableModel(configuration)
  val table = new JTable()

  val testMethods = Array(Mocks.createTestMethod("testOne"), Mocks.createTestMethod("testTwo"), Mocks.createTestMethod("testThree"))
  var window: TestDoxToolWindow = null

  "TestDoxToolWindow" should {

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
//      there were noMoreCallsTo(controller)
      configuration.removePropertyChangeListener(window)
      model.removeTableModelListener(window)
    }

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
      updateTestDoxModelUsingTestDoxFile(new TestDoxClass(null, "foo", true, Mocks.createTestClass(), null, testMethods))
      assertDox(table, testMethods)
    }

    "navigates to source on double click" in {
      updateTestDoxModelUsingTestDoxFile(new TestDoxClass(null, "foo", true, Mocks.createTestClass(), null, testMethods))
      table.changeSelection(1, -1, false, false)

      window.handleMouseEvent(new MouseEvent(table, 0, System.currentTimeMillis(), 0, 0, 0, 2, false))

      there was one(controller).jumpToTestElement(testMethods(0), false)
    }

    "forwards 'Navigate to source' command to project component if the ENTER key was pressed" in {
      updateTestDoxModelUsingTestDoxFile(new TestDoxClass(null, "foo", true, Mocks.createTestClass(), null, testMethods))
      table.changeSelection(1, -1, false, false)
      window.handleKeyEvent(createKeyEvent(KeyEvent.VK_ENTER, KeyEvent.VK_UNDEFINED))

      there was one(controller).jumpToTestElement(testMethods(0), false)
    }

    "forwards rename command to project component if rename key is pressed" in {
      initialisePanelAndSelectFirstRow(testMethods)
      table.changeSelection(1, 1, false, false)
      window.handleKeyEvent(createKeyEvent(KeyEvent.VK_F6, KeyEvent.VK_UNDEFINED))

      there was one(controller).startRename(testMethods(0))
    }

    "ignores rename key event if selected item is not a test element" in {
      model.setNotJava()
      table.changeSelection(0, 0, false, false)
      window.handleKeyEvent(createKeyEvent(KeyEvent.VK_F6, KeyEvent.VK_UNDEFINED))

      there was no(controller).startRename(testMethods(0))
    }

    "uses the selected test element to delete itself when the delete key is pressed" in {
      MockApplicationManager.reset()
      val psiMethod = mock[PsiMethod]
      val editorApi = mock[EditorApi]

      psiMethod.getName() returns "testSomething"

      initialisePanelAndSelectFirstRow(Array(new TestMethod(psiMethod, editorApi, new SentenceManager(new Configuration()))))
      window.handleKeyEvent(createKeyEvent(KeyEvent.VK_DELETE, KeyEvent.VK_UNDEFINED))

      there was one(editorApi).delete(psiMethod)
    }
  }

  private def initialisePanelAndSelectFirstRow(testMethods: Array[TestMethod]) {
    updateTestDoxModelUsingTestDoxFile(new TestDoxClass(null, "foo", true, Mocks.createTestClass(), null, testMethods))
    table.changeSelection(1, -1, false, false)
  }

  private def updateTestDoxModelUsingTestDoxFile(file: TestDoxFile) = file.updateModel(model)

  private def createKeyEvent(code: Int, modifiers: Int) = new KeyEvent(table, 0, System.currentTimeMillis(), modifiers, code, code.toChar())

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
}
