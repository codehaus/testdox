package org.codehaus.testdox.intellij;

import javax.swing.Icon;

import com.intellij.openapi.vfs.VirtualFile;

import org.codehaus.testdox.intellij.panel.TestDoxModel;

public class TestDoxNonJavaFile extends TestDoxFile {

    public static final String NO_CLASS_MESSAGE = "No class selected";

    public static final TestElement TEST_ELEMENT = new AbstractTestElement() {
        public String displayString() {
            return NO_CLASS_MESSAGE;
        }

        public Icon icon() {
            return IconHelper.getIcon(IconHelper.NOT_JAVA_ICON);
        }
    };

    public TestDoxNonJavaFile(VirtualFile file) {
        super(file, null, null, null, TestMethod.EMPTY_ARRAY);
    }

    public void updateModel(TestDoxModel model) {
        model.setNotJava();
    }
}
