package org.codehaus.testdox.intellij.inspections;

import com.intellij.codeInspection.InspectionToolProvider;
import com.intellij.openapi.components.ApplicationComponent;
import org.jetbrains.annotations.NotNull;

import static jedi.functional.FunctionalPrimitives.array;

public class TestDoxInspectionProvider implements ApplicationComponent, InspectionToolProvider {

    // ApplicationComponent --------------------------------------------------------------------------------------------

    @NotNull
    public String getComponentName() {
        return "TestDoxInspectionProvider";
    }

    public void initComponent() { }

    public void disposeComponent() { }

    // InspectionToolProvider ------------------------------------------------------------------------------------------

    public Class[] getInspectionClasses() {
        return array(EmptyTestClassInspection.class, EmptyTestMethodInspection.class);
    }
}
