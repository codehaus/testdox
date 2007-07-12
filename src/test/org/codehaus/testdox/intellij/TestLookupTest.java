package org.codehaus.testdox.intellij;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import org.intellij.openapi.testing.MockApplicationManager;
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;

import static jedi.functional.FunctionalPrimitives.array;

import org.codehaus.testdox.intellij.config.ConfigurationBean;

public class TestLookupTest extends MockObjectTestCase {

    static {
        MockApplicationManager.reset();
    }

    private final Mock mockEditorApi = mock(EditorApi.class);
    private final Mock mockPsiClass = mock(PsiClass.class);
    private final Mock mockVirtualFile = Mocks.createAndRegisterVirtualFileMock(this);
    private final Mock mockPsiJavaFile = mock(PsiJavaFile.class);

    private VirtualFile virtualFileMock = (VirtualFile) mockVirtualFile.proxy();
    private PsiClass psiClassMock = (PsiClass) mockPsiClass.proxy();
    private PsiJavaFile psiJavaFileMock = (PsiJavaFile) mockPsiJavaFile.proxy();

    private ConfigurationBean config = createConfig();

    private ConfigurationBean createConfig() {
        ConfigurationBean configurationBean = new ConfigurationBean();
        configurationBean.setTestMethodPrefix("pants");
        return configurationBean;
    }

    private TestLookup testLookup = new TestLookup((EditorApi) mockEditorApi.proxy(), null);

    public void testUsesEditorApiToDetermineWhetherAGivenFileIsAJavaFileInTheCurrentProject() {
        mockEditorApi.expects(once()).method("isJavaFile").with(isA(VirtualFile.class)).will(returnValue(true));
        assertTrue(testLookup.isJavaFile(virtualFileMock));
    }

    public void testUsesEditorApiToRetrieveThePsiClassForAGivenFullyQualifiedClassName() {
        mockEditorApi.expects(once()).method("getPsiClass").with(isA(String.class)).will(returnValue(psiClassMock));
        assertEquals(psiClassMock, testLookup.getClass("com.acme.Foo"));
    }

    public void testReturnsTheFullyQualifiedClassNameForAVirtualFileRepresentingAClassOutsideTheCurrentProjectScope() {
        mockEditorApi.expects(once()).method("getPsiJavaFile").with(isA(VirtualFile.class)).will(returnValue(psiJavaFileMock));
        mockPsiJavaFile.expects(once()).method("getPackageName").will(returnValue("javax.swing"));
        mockVirtualFile.expects(once()).method("getNameWithoutExtension").will(returnValue("JTable"));

        assertEquals("javax.swing.JTable", testLookup.getClassName(virtualFileMock));
    }

    public void testReturnsNullAsTheFullyQualifiedClassNameForAVirtualFileRepresentingANonJavaFile() {
        mockEditorApi.expects(once()).method("getPsiJavaFile").with(isA(VirtualFile.class));
        assertNull(testLookup.getClassName(virtualFileMock));
    }

    public void testReturnsNullAsThePsiClassInstanceForAVirtualFileRepresentingAClassOutsideTheCurrentProjectScope() {
        mockEditorApi.expects(once()).method("getPsiJavaFile").with(isA(VirtualFile.class)).will(returnValue(psiJavaFileMock));
        mockPsiJavaFile.expects(once()).method("getPackageName").will(returnValue("javax.swing"));
        mockVirtualFile.expects(once()).method("getNameWithoutExtension").will(returnValue("JTable"));
        mockEditorApi.expects(once()).method("getPsiClass").with(eq("javax.swing.JTable"));

        assertNull(testLookup.getClass(virtualFileMock));
    }

    public void testReturnsNullAsThePsiClassInstanceForAVirtualFileRepresentingANonJavaFile() {
        mockEditorApi.expects(once()).method("getPsiJavaFile").with(isA(VirtualFile.class));
        assertNull(testLookup.getClass(virtualFileMock));
    }

    public void testReturnsEmptyArrayForNullClass() throws Exception {
        TestElement[] testMethods = testLookup.getTestMethods(null);
        assertEquals(0, testMethods.length);
    }

    public void testReturnsEmptyArrayForAClassWithNoTestMethods() throws Exception {
        mockEditorApi.expects(once()).method("getMethods").with(isA(PsiClass.class)).will(returnValue(new PsiMethod[0]));

        TestElement[] testMethods = testLookup.getTestMethods(psiClassMock);
        assertEquals(0, testMethods.length);
    }

    public void testReturnsOnlyMethodsBeginningWithTestMethodPrefix() throws Exception {
        Mock mockPsiMethod1 = mock(PsiMethod.class);
        Mock mockPsiMethod2 = mock(PsiMethod.class);

        PsiMethod[] psiMethods = array((PsiMethod) mockPsiMethod1.proxy(), (PsiMethod) mockPsiMethod2.proxy());
        mockEditorApi.expects(once()).method("getMethods").with(isA(PsiClass.class)).will(returnValue(psiMethods));
        mockEditorApi.expects(once()).method("isTestMethod").with(eq(psiMethods[0])).will(returnValue(true));
        mockEditorApi.expects(once()).method("isTestMethod").with(eq(psiMethods[1])).will(returnValue(false));

        mockPsiMethod1.expects(atLeastOnce()).method("getName").will(returnValue("pantsMethod"));

        TestMethod[] testMethods = testLookup.getTestMethods(psiClassMock);
        assertEquals(1, testMethods.length);
        assertEquals("pantsMethod", testMethods[0].getMethodName());
    }
}
