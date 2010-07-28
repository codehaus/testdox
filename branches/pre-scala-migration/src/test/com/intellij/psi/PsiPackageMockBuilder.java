package com.intellij.psi;

import org.jmock.cglib.MockObjectTestCase;
import sugar.test.MockBuilder;

public class PsiPackageMockBuilder extends MockBuilder<PsiPackage> {

    public PsiPackageMockBuilder(MockObjectTestCase testCase) {
        super(testCase, PsiPackage.class);
    }

    public PsiPackageMockBuilder withQualifiedName(String name) {
        stubs().method("getQualifiedName").will(returnValue(name));
        return this;
    }

    public PsiPackageMockBuilder expectGetDirectories(PsiDirectory[] directories) {
        expects(once()).method("getDirectories").will(returnValue(directories));
        return this;
    }
}
