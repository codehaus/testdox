package org.codehaus.testdox.intellij.inspections;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;

import org.codehaus.testdox.intellij.TestDoxProjectComponent;

class AddTestMethodQuickFix implements LocalQuickFix {

    public String getName() {
        return "Add Test";
    }

    public String getFamilyName() {
        return "TestDox Quick Fixes";
    }

    public void applyFix(Project project, ProblemDescriptor problemDescriptor) {
        TestDoxProjectComponent.getInstance(project).getController().addTest();
    }
}
