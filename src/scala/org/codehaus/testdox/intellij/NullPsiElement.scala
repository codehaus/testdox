package org.codehaus.testdox.intellij

import com.intellij.psi.PsiElement
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

object NullPsiElement {

  private val nullPsiElementInvocationHandler = new InvocationHandler() {
    override def invoke(proxy: Object, method: Method, args: Array[Object]): Object = null
  }

  private val interfaces = Array(classOf[PsiElement]).asInstanceOf[Array[Class[_]]]
  private val classLoader = nullPsiElementInvocationHandler.getClass().getClassLoader()

  val INSTANCE = Proxy.newProxyInstance(classLoader, interfaces, nullPsiElementInvocationHandler).asInstanceOf[PsiElement]
}
