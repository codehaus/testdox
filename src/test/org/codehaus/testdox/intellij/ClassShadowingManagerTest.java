package org.codehaus.testdox.intellij;

import com.intellij.psi.PsiDirectory;
import org.intellij.openapi.testing.MockApplicationManager;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaFile;

import org.codehaus.testdox.intellij.config.Configuration;
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;

public class ClassShadowingManagerTest extends MockObjectTestCase {

    private static final String CLASS_NAME = "SelectedClass";

    static {
        MockApplicationManager.reset();
    }

    private final Mock mockPsiClass = mock(PsiClass.class);
    private final Mock mockTestDoxFileFactory = Mocks.createAndRegisterTestDoxFileFactoryMock(this);
    private final Mock mockEditorApi = mock(EditorApi.class);
    private final Mock mockConfiguration = mock(Configuration.class);
    private final Mock mockNameResolver = mock(NameResolver.class);
    private final Mock mockVirtualFile = Mocks.createAndRegisterVirtualFileMock(this);
    private final Mock mockTestDoxClass = Mocks.createAndRegisterTestDoxClassMock(this);
    private final Mock mockPsiJavaFile = mock(PsiJavaFile.class);
    private final Mock mockTestClass = Mocks.createAndRegisterTestClassMock(this);

    private ClassShadowingManager classShadowingManager;
    private PsiClass psiClassMock = (PsiClass) mockPsiClass.proxy();

    protected void setUp() {
        mockEditorApi.stubs().method("getVirtualFile").with(isA(PsiElement.class)).will(returnValue(mockVirtualFile.proxy()));
        mockTestDoxFileFactory.expects(once()).method("getTestDoxFile").with(isA(VirtualFile.class)).will(returnValue(mockTestDoxClass.proxy()));
        mockPsiClass.stubs().method("getContainingFile").will(returnValue(mockPsiJavaFile.proxy()));

        classShadowingManager = new ClassShadowingManager(psiClassMock,
                                                          (TestDoxFileFactory) mockTestDoxFileFactory.proxy(),
                                                          (EditorApi) mockEditorApi.proxy(),
                                                          (Configuration) mockConfiguration.proxy(),
                                                          (NameResolver) mockNameResolver.proxy());

        mockPsiJavaFile.expects(once()).method("getName").will(returnValue("/path/to/the/selected/class/" + CLASS_NAME + ".java"));
    }

    public void testDoesNotMoveTestClassIfShadowingIsDisabled() throws Exception {
        setExpectationsForDisabledShadowing();
        classShadowingManager.elementMoved(psiClassMock);
    }

    public void testDoesNotPerformMoveWhenClassBeingMovedIsATest() throws Exception {
        setExpectationsForChangedTestClass();
        classShadowingManager.elementMoved(psiClassMock);
    }

    public void testDoesNotPerformMoveForInnerClasses() throws Exception {
        setExpectationsForChangedInnerClass();
        classShadowingManager.elementMoved(psiClassMock);
    }

    public void testDelegatesMoveOperationToIdea() throws Exception {
        setExpectationsForChangedTestedClass();

        mockPsiJavaFile.expects(once()).method("getContainingDirectory").will(returnValue(newDummy(PsiDirectory.class)));
        mockEditorApi.expects(once()).method("move").with(isA(PsiClass.class), isA(PsiDirectory.class));

        classShadowingManager.elementMoved(psiClassMock);
    }

    public void testDoesNotRenameTestClassIfShadowingIsDisabled() throws Exception {
        setExpectationsForDisabledShadowing();
        classShadowingManager.elementRenamed(psiClassMock);
    }

    public void testDoesNotPerformRenameWhenClassBeingRenamedIsATest() throws Exception {
        setExpectationsForChangedTestClass();
        classShadowingManager.elementRenamed(psiClassMock);
    }

    public void testDoesNotPerformRenameForInnerClasses() throws Exception {
        setExpectationsForChangedInnerClass();
        classShadowingManager.elementRenamed(psiClassMock);
    }

    public void testDelegatesRenameOperationToIdea() throws Exception {
        setExpectationsForChangedTestedClass();

        mockNameResolver.expects(once()).method("getTestClassName").with(eq(CLASS_NAME)).will(returnValue(CLASS_NAME + "Test"));
        mockEditorApi.expects(once()).method("rename").with(isA(PsiClass.class), isA(String.class));

        classShadowingManager.elementRenamed(psiClassMock);
    }

    private void setExpectationsForDisabledShadowing() {
        mockPsiClass.expects(atLeastOnce()).method("getName").will(returnValue(CLASS_NAME));
        mockConfiguration.expects(once()).method("autoApplyChangesToTests").will(returnValue(false));
    }

    private void setExpectationsForChangedTestClass() {
        mockPsiClass.expects(atLeastOnce()).method("getName").will(returnValue(CLASS_NAME));
        mockConfiguration.expects(once()).method("autoApplyChangesToTests").will(returnValue(true));
        mockNameResolver.expects(once()).method("isRealClass").with(eq(CLASS_NAME)).will(returnValue(false));
    }

    private void setExpectationsForChangedInnerClass() {
        mockPsiClass.expects(once()).method("getName").will(returnValue(CLASS_NAME + "$InnerClass"));
    }

    private void setExpectationsForChangedTestedClass() {
        mockPsiClass.expects(atLeastOnce()).method("getName").will(returnValue(CLASS_NAME));
        mockConfiguration.expects(once()).method("autoApplyChangesToTests").will(returnValue(true));
        mockNameResolver.expects(once()).method("isRealClass").with(eq(CLASS_NAME)).will(returnValue(true));
        mockTestDoxClass.expects(once()).method("canNavigateToTestClass").will(returnValue(true));
        mockTestDoxClass.expects(once()).method("testClass").will(returnValue(mockTestClass.proxy()));
        mockTestClass.expects(once()).method("psiElement").will(returnValue(mockPsiClass.proxy()));
    }
}
