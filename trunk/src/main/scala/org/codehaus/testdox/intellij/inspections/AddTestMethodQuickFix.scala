package org.codehaus.testdox.intellij.inspections

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project

import org.codehaus.testdox.intellij.TestDoxProjectComponent

class AddTestMethodQuickFix extends LocalQuickFix {

  val getName = "Add Test"
  val getFamilyName = "TestDox Quick Fixes"

  def applyFix(project: Project, problemDescriptor: ProblemDescriptor) { TestDoxProjectComponent.getInstance(project).getController.addTest() }
}
