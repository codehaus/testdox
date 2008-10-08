package com.intellij.psi;

import com.intellij.openapi.vfs.VirtualFile;
import org.jmock.cglib.MockObjectTestCase;
import sugar.test.MockBuilder;

public class PsiDirectoryMockBuilder extends MockBuilder<PsiDirectory> {

    public PsiDirectoryMockBuilder(MockObjectTestCase testCase) {
        super(testCase, PsiDirectory.class);
    }

    public PsiDirectoryMockBuilder withWritable(boolean writable) {
        stubs().method("isWritable").will(returnValue(writable));
        return this;
    }

    public PsiDirectoryMockBuilder withVirtualFile(VirtualFile virtualFile) {
        stubs().method("getVirtualFile").will(returnValue(virtualFile));
        return this;
    }

    @Deprecated
    public PsiDirectoryMockBuilder expectGetPackageAtLeastOnceReturns(PsiPackage value) {
        expects(atLeastOnce()).method("getPackage").will(returnValue(value));
        return this;
    }
}
