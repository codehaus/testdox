package org.codehaus.testdox.intellij;

import javax.swing.Icon;

import com.intellij.openapi.vfs.VirtualFile;

import org.codehaus.testdox.intellij.panel.TestDoxModel;

public class TestDoxClass extends TestDoxFile {

    public static final TestElement NO_DOX_ELEMENT = new AbstractTestElement() {
        public String displayString() {
            return "<font color=\"red\">No tests found for current class!</font>";
        }

        public Icon icon() {
            return IconHelper.getIcon(IconHelper.NO_TESTS_ICON);
        }
    };
    private boolean isTestedClass;

    public TestDoxClass(VirtualFile file, String className, boolean isTestedClass, TestClass testClass,
                        TestClass testedClass, TestMethod[] testMethods) {
        super(file, className, testClass, testedClass, testMethods);
        this.isTestedClass = isTestedClass;
    }

    public void updateModel(TestDoxModel model) {
        model.setTestDoxForClass(this);
    }

    public boolean isTestedClass() {
        return isTestedClass;
    }

    public boolean canBeUnitTested() {
        return true;
    }

    public boolean canNavigateToTestClass() {
        return (testClass() != null) && (testClass().isTestClass());
    }

    public boolean canNavigateToTestedClass() {
        return (testedClass() != null) && (!testedClass().isTestClass());
    }
}
