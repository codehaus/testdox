package org.codehaus.testdox.intellij.ui

import com.intellij.openapi.project.Project
import org.codehaus.testdox.intellij.panel.RenameDialog

class AddTestDialog(project: Project) extends RenameDialog(project, "") {

  setTitle("Add Test")
}
