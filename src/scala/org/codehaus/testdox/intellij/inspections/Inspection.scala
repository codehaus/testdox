package org.codehaus.testdox.intellij.inspections

import com.intellij.codeInspection.BaseJavaLocalInspectionTool
import com.intellij.codeInspection.ProblemDescriptor
import org.jetbrains.annotations.NotNull

abstract class Inspection extends BaseJavaLocalInspectionTool {

  @NotNull
  val getGroupDisplayName = "TestDox Issues"

  @NotNull
  def getShortName: String = {
    val className = getClass().getName()
    className.substring(className.lastIndexOf('.') + 1, className.indexOf("Inspection"))
  }
}

object Inspection {
  val NO_PROBLEMS = new Array[ProblemDescriptor](0)
}
