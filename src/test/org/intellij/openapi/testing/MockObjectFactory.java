package org.intellij.openapi.testing;

import com.intellij.psi.JavaDirectoryServiceMockBuilder;
import com.intellij.psi.PsiDirectoryMockBuilder;
import com.intellij.psi.PsiPackageMockBuilder;
import org.codehaus.testdox.intellij.EditorApiMockBuilder;
import org.jmock.cglib.MockObjectTestCase;

public class MockObjectFactory {

    private final MockObjectTestCase testCase;

    public MockObjectFactory(MockObjectTestCase testCase) {
        this.testCase = testCase;
    }

    public EditorApiMockBuilder editorApi() {
        return new EditorApiMockBuilder(testCase);
    }

    public PsiDirectoryMockBuilder psiDirectory() {
        return new PsiDirectoryMockBuilder(testCase);
    }

    public PsiPackageMockBuilder psiPackage() {
        return new PsiPackageMockBuilder(testCase);
    }

    public JavaDirectoryServiceMockBuilder javaDirectoryService() {
        return new JavaDirectoryServiceMockBuilder(testCase);
    }
}
