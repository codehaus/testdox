package org.codehaus.testdox.intellij;

import java.util.Collections;

import org.intellij.openapi.testing.MockApplicationManager;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;

import org.codehaus.testdox.intellij.config.ConfigurationBean;
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;

public class TestDoxFileFactoryTest extends MockObjectTestCase {

    static {
        MockApplicationManager.reset();
    }

    private static final String CLASS_NAME = "Foo";
    private static final String BASE_PACKAGE = "com.acme";
    private static final String ALTERNATIVE_PACKAGE = "com.xyz";
    private static final String FQN_CLASS_NAME = BASE_PACKAGE + '.' + CLASS_NAME;
    private static final String REAL_CLASS_NAME = BASE_PACKAGE + ".Bar";
    private static final TestMethod[] TEST_METHODS = new TestMethod[0];

    private final Mock mockVirtualFile = Mocks.createAndRegisterVirtualFileMock(this);
    private final Mock mockTestLookup = Mocks.createAndRegisterTestLookupMock(this);
    private final Mock mockPsiClass = mock(PsiClass.class);
    private final Mock mockNameResolver = mock(NameResolver.class);
    private final Mock mockEditorApi = mock(EditorApi.class);

    public void testReturnsFileWithOnlyAFileNameForNonJavaSourceFile() throws Exception {
        mockTestLookup.expects(once()).method("isJavaFile").with(isA(VirtualFile.class)).will(returnValue(false));

        TestDoxFileFactory factory = new TestDoxFileFactory((TestLookup) mockTestLookup.proxy(), null, null);
        TestDoxFile doxFile = factory.getTestDoxFile((VirtualFile) mockVirtualFile.proxy());

        assertNull(doxFile.className());
        assertNull(doxFile.testClass());
        assertNotNull(doxFile.testMethods());
        assertEquals(0, doxFile.testMethods().length);
    }

    public void testReturnsFileWithSourceClassForJavaFileWhereTestClassIsNotFound() throws Exception {
        String expectedClassName = "com.acme.FooBar";

        mockTestLookup.expects(once()).method("isJavaFile").with(isA(VirtualFile.class)).will(returnValue(true));
        mockTestLookup.expects(once()).method("getClassName").with(isA(VirtualFile.class)).will(returnValue(expectedClassName));
        mockTestLookup.expects(once()).method("getClass").with(isA(VirtualFile.class));
        mockTestLookup.expects(once()).method("getEditorApi").will(returnValue(mockEditorApi.proxy()));
        mockEditorApi.expects(once()).method("isInterface").with(eq(expectedClassName)).will(returnValue(false));

        TestDoxFileFactory factory = new TestDoxFileFactory((TestLookup) mockTestLookup.proxy(), null, null);
        TestDoxFile doxFile = factory.getTestDoxFile((VirtualFile) mockVirtualFile.proxy());

        assertEquals(expectedClassName, doxFile.className());
        assertNotNull(doxFile.testClass());
        assertNotNull(doxFile.testMethods());
        assertEquals(0, doxFile.testMethods().length);
    }

    // TODO: add test for non-project classes

    public void testReturnsFullyPopulatedFileForTestSourceFile() throws Exception {
        setLookupExpectationsForSourceFile(false, false);
        TestDoxFileFactory factory = new TestDoxFileFactory((TestLookup) mockTestLookup.proxy(), null, (NameResolver) mockNameResolver.proxy());
        TestDoxFile doxFile = factory.getTestDoxFile((VirtualFile) mockVirtualFile.proxy());

        assertEquals(REAL_CLASS_NAME, doxFile.className());
        assertNotNull(doxFile.testClass());
        assertNotNull(doxFile.testClass().psiElement());
        assertEquals(TEST_METHODS, doxFile.testMethods());
    }

    public void testReturnsFullyPopulatedFileForRealSourceFile() throws Exception {
        setLookupExpectationsForSourceFile(true, false);
        TestDoxFileFactory factory = new TestDoxFileFactory((TestLookup) mockTestLookup.proxy(), null, (NameResolver) mockNameResolver.proxy());
        TestDoxFile doxFile = factory.getTestDoxFile((VirtualFile) mockVirtualFile.proxy());

        assertEquals(FQN_CLASS_NAME, doxFile.className());
        assertNotNull(doxFile.testClass());
        assertNotNull(doxFile.testClass().psiElement());
        assertEquals(TEST_METHODS, doxFile.testMethods());
    }

    public void testDoesNotAttemptCustomPackageLookupIfTurnedOffInConfiguration() throws Exception {
        ConfigurationBean config = new ConfigurationBean();
        config.setAllowCustomPackages(false);
        config.setCustomPackages(Collections.singletonList("com.custom"));

        setLookupExpectationsForSourceFile(true, false);
        TestDoxFileFactory factory = new TestDoxFileFactory((TestLookup) mockTestLookup.proxy(), config, (NameResolver) mockNameResolver.proxy());
        TestDoxFile doxFile = factory.getTestDoxFile((VirtualFile) mockVirtualFile.proxy());

        assertEquals(FQN_CLASS_NAME, doxFile.className());
        assertNotNull(doxFile.testClass());
        assertNotNull(doxFile.testClass().psiElement());
        assertEquals(TEST_METHODS, doxFile.testMethods());
    }

    public void testAttemptsToFindTestClassesInAlternatePackagesIfTurnedOnInConfiguration() throws Exception {
        ConfigurationBean config = new ConfigurationBean();
        config.setAllowCustomPackages(true);
        config.setCustomPackages(Collections.singletonList(ALTERNATIVE_PACKAGE));

        setLookupExpectationsForSourceFile(true, true);
        TestDoxFileFactory factory = new TestDoxFileFactory((TestLookup) mockTestLookup.proxy(), config, (NameResolver) mockNameResolver.proxy());
        TestDoxFile doxFile = factory.getTestDoxFile((VirtualFile) mockVirtualFile.proxy());

        assertEquals(FQN_CLASS_NAME, doxFile.className());
        assertNotNull(doxFile.testClass());
        assertNotNull(doxFile.testClass().psiElement());
        assertEquals(TEST_METHODS, doxFile.testMethods());
    }

    private void setLookupExpectationsForSourceFile(boolean isRealClass, boolean lookInAlternatePackage) {
        PsiClass psiClassMock = (PsiClass) mockPsiClass.proxy();
        mockTestLookup.expects(once()).method("isJavaFile").with(isA(VirtualFile.class)).will(returnValue(true));
        mockTestLookup.expects(once()).method("getClassName").with(isA(VirtualFile.class)).will(returnValue(FQN_CLASS_NAME));
        mockTestLookup.expects(once()).method("getClass").with(isA(VirtualFile.class)).will(returnValue(psiClassMock));
        mockNameResolver.expects(once()).method("isRealClass").with(eq(FQN_CLASS_NAME)).will(returnValue(isRealClass));

        if (isRealClass) {
            mockNameResolver.expects(once()).method("getTestClassName").with(eq(FQN_CLASS_NAME)).will(returnValue(FQN_CLASS_NAME));
        } else {
            mockNameResolver.expects(once()).method("getRealClassName").with(eq(FQN_CLASS_NAME)).will(returnValue(REAL_CLASS_NAME));
        }

        String fullyQualifiedClassName = isRealClass ? FQN_CLASS_NAME : REAL_CLASS_NAME;
        PsiClass actualPsiClass = lookInAlternatePackage ? null : psiClassMock;
        mockTestLookup.expects(once()).method("getClass").with(eq(fullyQualifiedClassName)).will(returnValue(actualPsiClass));

        if (lookInAlternatePackage) {
            mockTestLookup.expects(once()).method("getClass").with(eq(ALTERNATIVE_PACKAGE + '.' + CLASS_NAME)).will(returnValue(psiClassMock));
        }
        mockTestLookup.expects(once()).method("getTestMethods").with(eq(psiClassMock)).will(returnValue(TEST_METHODS));
        mockTestLookup.expects(atLeastOnce()).method("getEditorApi").will(returnValue(mockEditorApi.proxy()));
        if (!isRealClass) {
            mockEditorApi.expects(once()).method("isInterface").with(eq(FQN_CLASS_NAME)).will(returnValue(false));
        }
        mockEditorApi.expects(atLeastOnce()).method("isInterface").with(eq(isRealClass ? FQN_CLASS_NAME
                                                                           : REAL_CLASS_NAME)).will(returnValue(false));
    }
}
