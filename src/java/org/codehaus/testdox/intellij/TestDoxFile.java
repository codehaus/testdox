package org.codehaus.testdox.intellij;

import com.intellij.openapi.vfs.VirtualFile;

import org.codehaus.testdox.intellij.panel.TestDoxModel;

public abstract class TestDoxFile {

    private VirtualFile file;
    private String className;
    private TestClass testClass;
    private TestClass testedClass;
    private TestMethod[] testMethods;

    public TestDoxFile(VirtualFile file, String className, TestClass testClass, TestClass testedClass,
                       TestMethod[] testMethods) {
        this.file = file;
        this.className = className;
        this.testClass = testClass;
        this.testedClass = testedClass;
        this.testMethods = testMethods;
    }

    public abstract void updateModel(TestDoxModel model);

    public VirtualFile getFile() {
        return file;
    }

    public String getClassName() {
        return className;
    }

    public TestClass getTestClass() {
        return testClass;
    }

    public TestClass getTestedClass() {
        return testedClass;
    }

    public TestMethod[] getTestMethods() {
        return testMethods;
    }

    public boolean isTestedClass() {
        return false;
    }

    public boolean canBeUnitTested() {
        return false;
    }

    public boolean canNavigateToTestClass() {
        return false;
    }

    public boolean canNavigateToTestedClass() {
        return false;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer(getClass().getName());
        buffer.append(" { file: ").append(file);
        buffer.append("; className: ").append(className);
        buffer.append("; testClass: ").append(testClass);
        buffer.append("; testedClass: ").append(testedClass);
        buffer.append(" }");
        return buffer.toString();
    }
}
