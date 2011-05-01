package org.codehaus.testdox.intellij;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import org.jmock.cglib.MockObjectTestCase;
import sugar.test.MockBuilder;

public class EditorApiMockBuilder extends MockBuilder<EditorApi> {

    public EditorApiMockBuilder(MockObjectTestCase testCase) {
        super(testCase, EditorApi.class);
    }

    public EditorApiMockBuilder expectGetPsiDirectoryReturns(PsiDirectory psiDirectory) {
        expects(once()).method("getPsiDirectory").with(isA(VirtualFile.class)).will(returnValue(psiDirectory));
        return this;
    }

    public EditorApiMockBuilder expectGetPsiDirectoryReturns(VirtualFile file, PsiDirectory psiDirectory) {
        expects(once()).method("getPsiDirectory").with(same(file)).will(returnValue(psiDirectory));
        return this;
    }

    public EditorApiMockBuilder expectDeleteAsynchronously() {
        expects(once()).method("deleteAsynchronously").with(isA(PsiDirectory[].class), isA(String.class), isA(String.class), isA(Runnable.class));
        return this;
    }
}
