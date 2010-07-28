package org.codehaus.testdox.intellij.panel;

import com.intellij.openapi.project.Project;

public class AddTestDialog extends RenameDialog {

    public AddTestDialog(Project project) {
        super(project, "");
        setTitle("Add Test");
    }
}
