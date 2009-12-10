package org.codehaus.testdox.intellij;

import javax.swing.Icon;

import com.intellij.openapi.vfs.VirtualFile;

import org.codehaus.testdox.intellij.panel.TestDoxModel;

public class TestDoxNonProjectClass extends TestDoxFile {

    public static final TestElement TEST_ELEMENT = new AbstractTestElement() {
        public String displayString() {
            return "<font color=\"gray\">Not in current project</font>";
        }

        public Icon icon() {
            return IconHelper.getIcon(IconHelper.WARNING_ICON);
        }
    };

    public TestDoxNonProjectClass(VirtualFile file, String className, TestClass testClass, TestClass testedClass) {
        super(file, className, testClass, testedClass, TestMethod.EMPTY_ARRAY);
    }

    public void updateModel(TestDoxModel model) {
        model.setTestDoxForNonProjectClass(this);
    }
}
