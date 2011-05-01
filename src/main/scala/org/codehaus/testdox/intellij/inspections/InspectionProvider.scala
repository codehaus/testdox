package org.codehaus.testdox.intellij.inspections

import com.intellij.codeInspection.InspectionToolProvider
import com.intellij.openapi.components.ApplicationComponent
import org.jetbrains.annotations.NotNull

class InspectionProvider extends InspectionToolProvider with ApplicationComponent {

  val getInspectionClasses = Array(classOf[EmptyTestClassInspection], classOf[EmptyTestMethodInspection]).asInstanceOf[Array[Class[_]]]

  @NotNull
  val getComponentName = "TestDoxInspectionProvider"

  def initComponent() {}

  def disposeComponent() {}
}
