package org.codehaus.testdox.intellij.actions

import com.intellij.psi.{PsiElement, PsiFile}
import com.intellij.openapi.editor.Editor
import org.codehaus.testdox.intellij.ui.ToolWindowUI
import org.codehaus.testdox.intellij.{TestDoxController, TestDoxNonJavaFile, NullPsiElement, TestDoxProjectComponent}
import java.lang.reflect.{Proxy, Method, InvocationHandler}
import com.intellij.openapi.actionSystem._

class ActionEvents {

  def getTestDoxController(event: AnActionEvent) = {
    val testDoxProjectComponent = getTestDoxProjectComponent(event)
    if (testDoxProjectComponent != null) testDoxProjectComponent.getController else Nulls.TESTDOX_CONTROLLER
  }

  def getToolWindowUI(event: AnActionEvent) = {
    val testDoxProjectComponent = getTestDoxProjectComponent(event)
    if (testDoxProjectComponent != null) testDoxProjectComponent.getToolWindowUI else Nulls.TESTDOX_TOOL_WINDOW
  }

  private def getTestDoxProjectComponent(event: AnActionEvent) = {
    TestDoxProjectComponent.getInstance(event.getData(PlatformDataKeys.PROJECT))
  }

  def isJavaFile(event: AnActionEvent) = {
    val testDoxController = getTestDoxController(event)
    val file = event.getData(PlatformDataKeys.VIRTUAL_FILE)
    testDoxController != null && file != null && testDoxController.getEditorApi.isJavaFile(file)
  }

  def getTargetPsiElement(event: AnActionEvent): PsiElement = {
    val editor: Editor = event.getData(PlatformDataKeys.EDITOR)
    if (editor == null) return NullPsiElement.INSTANCE

    val psiFile: PsiFile = event.getData(LangDataKeys.PSI_FILE)
    if (psiFile == null) return NullPsiElement.INSTANCE

    return psiFile.findElementAt(editor.getCaretModel.getOffset)
  }
}

object ActionEvents {
  var instance = new ActionEvents()
  def setInstance(actionEvents: ActionEvents) { instance = actionEvents }
}

object Nulls {

  private val invocationHandler = new InvocationHandler() {
    private val NON_JAVA_TEST_DOX_FILE = new TestDoxNonJavaFile(null)

    def invoke(proxy: Object, method: Method, args: Array[Object]): Object = {
      if ("hasActiveEditors" == method.getName) return boolean2Boolean(false)
      else if ("canCurrentFileBeUnitTested" == method.getName) return boolean2Boolean(false)
      else if ("update" == method.getName && args(0).isInstanceOf[Presentation]) {
        (args(0).asInstanceOf[Presentation]).setEnabled(false)
      }
      if ("getCurrentTestDoxFile".equals(method.getName)) return NON_JAVA_TEST_DOX_FILE
      return null
    }
  }

  private val classLoader = invocationHandler.getClass.getClassLoader
  private val interfaces = Array(classOf[ToolWindowUI], classOf[TestDoxController])
  private val nullObject = Proxy.newProxyInstance(classLoader, interfaces.asInstanceOf[Array[Class[_]]], invocationHandler)

  val TESTDOX_TOOL_WINDOW = nullObject.asInstanceOf[ToolWindowUI]
  val TESTDOX_CONTROLLER = nullObject.asInstanceOf[TestDoxController]
}
