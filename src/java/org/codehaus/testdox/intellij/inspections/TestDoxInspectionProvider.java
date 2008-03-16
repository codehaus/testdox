package org.codehaus.testdox.intellij.inspections;

import com.intellij.codeInspection.InspectionToolProvider;
import com.intellij.openapi.components.ApplicationComponent;
import static jedi.functional.Coercions.array;
import org.jetbrains.annotations.NotNull;

public class TestDoxInspectionProvider implements ApplicationComponent, InspectionToolProvider {

    // ApplicationComponent --------------------------------------------------------------------------------------------

    @NotNull
    public String getComponentName() {
        return "TestDoxInspectionProvider";
    }

    public void initComponent() {
    }

    public void disposeComponent() {
    }

    // InspectionToolProvider ------------------------------------------------------------------------------------------

    public Class[] getInspectionClasses() {
        return array(EmptyTestClassInspection.class, EmptyTestMethodInspection.class);
    }
}
