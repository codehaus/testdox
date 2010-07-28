package org.codehaus.testdox.intellij.inspections;

import com.intellij.codeInspection.BaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractTestDoxInspection extends BaseJavaLocalInspectionTool {

    protected static final ProblemDescriptor[] NO_PROBLEMS = new ProblemDescriptor[0];

    @NotNull
    public String getGroupDisplayName() {
        return "TestDox Issues";
    }

    @NotNull
    public String getShortName() {
        String className = getClass().getName();
        return className.substring(className.lastIndexOf('.') + 1, className.indexOf("Inspection"));
    }
}
