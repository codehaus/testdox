package org.codehaus.testdox.intellij.inspections;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;

public abstract class AbstractTestDoxInspection extends LocalInspectionTool {

    protected static final ProblemDescriptor[] NO_PROBLEMS = new ProblemDescriptor[0];

    public String getGroupDisplayName() {
        return "TestDox Issues";
    }

    public String getShortName() {
        String className = getClass().getName();
        return className.substring(className.lastIndexOf('.') + 1, className.indexOf("Inspection"));
    }
}
