package com.intellij.psi;

import org.jmock.cglib.MockObjectTestCase;
import sugar.test.MockBuilder;

public class JavaDirectoryServiceMockBuilder extends MockBuilder<JavaDirectoryService> {

    public JavaDirectoryServiceMockBuilder(MockObjectTestCase testCase) {
        super(testCase, JavaDirectoryService.class);
    }

    public JavaDirectoryServiceMockBuilder expectGetPackageAtLeastOnceReturns(PsiDirectory psiDirectory, PsiPackage psiPackage) {
        expects(atLeastOnce()).method("getPackage").with(same(psiDirectory)).will(returnValue(psiPackage));
        return this;
    }
}