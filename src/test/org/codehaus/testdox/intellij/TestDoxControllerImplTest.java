package org.codehaus.testdox.intellij;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethod;
import com.intellij.testFramework.LightVirtualFile;
import org.codehaus.testdox.intellij.config.Configuration;
import org.codehaus.testdox.intellij.ui.RenameUI;
import org.codehaus.testdox.intellij.ui.TestDoxTableModel;
import org.intellij.openapi.testing.MockApplicationManager;
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;

public class TestDoxControllerImplTest extends MockObjectTestCase {

    static {
        MockApplicationManager.reset();
    }

    private final Mock mockProject = mock(Project.class);
    private final Mock mockEditorApi = mock(EditorApi.class);
    private final Mock mockTestDoxFileFactory = Mocks.createAndRegisterTestDoxFileFactoryMock(this);
    private final Mock mockTestDoxFile = Mocks.createAndRegisterTestDoxFileMock(this);
    private final Mock mockVirtualFile = Mocks.createAndRegisterVirtualFileMock(this);
    private final Mock mockPsiClass = mock(PsiClass.class);
    private final Mock mockNameResolver = mock(NameResolver.class);
    private final Mock mockSentenceManager = Mocks.createAndRegisterSentenceManagerMock(this);
    private final Mock mockAddTestDialog = mock(RenameUI.class);
    private final Mock mockRenameDialog = mock(RenameUI.class);
    private final Mock mockToolWindow = mock(ToolWindow.class);

    private final Configuration configuration = new Configuration();
    private final TestDoxTableModel testDoxModel = new TestDoxTableModel(configuration);

    private TestDoxControllerImpl controller;
    private boolean shouldCreateTestClass;
    private boolean dialogShown;

    protected void setUp() {
        controller = new TestDoxControllerImpl((Project) mockProject.proxy(), (EditorApi) mockEditorApi.proxy(),
                                               testDoxModel, configuration, (NameResolver) mockNameResolver.proxy(),
                                               (SentenceManager) mockSentenceManager.proxy(),
                                               (TestDoxFileFactory) mockTestDoxFileFactory.proxy()) {
            {
                toolWindow = (ToolWindow) mockToolWindow.proxy();
            }

            protected boolean shouldCreateTestClass() {
                dialogShown = true;
                return shouldCreateTestClass;
            }

            protected RenameUI createAddTestDialog() {
                return (RenameUI) mockAddTestDialog.proxy();
            }

            protected RenameUI createRenameDialog(String testMethodName) {
                return (RenameUI) mockRenameDialog.proxy();
            }
        };
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        shouldCreateTestClass = false;
        dialogShown = false;
    }

    public void testUpdatesTestdoxModelWhenPsiTreechangeEventIsReceived() {
        Mock mockTestDoxClass = Mocks.createAndRegisterTestDoxClassMock(this);

        mockEditorApi.expects(once()).method("getCurrentFile").will(returnValue(mockVirtualFile.proxy()));
        mockTestDoxFileFactory.expects(once()).method("getTestDoxFile").with(isA(VirtualFile.class)).will(returnValue(mockTestDoxClass.proxy()));
        mockTestDoxClass.expects(once()).method("updateModel").with(isA(TestDoxTableModel.class));

        controller.psiTreeChangeListener.childrenChanged(null);
    }

    public void testSetsNotJavaOnModelIfFileIsNull() throws Exception {
        mockToolWindow.expects(once()).method("setTitle").with(eq(""));
        controller.selectedFileChanged((VirtualFile) null);
        assertEquals(TestDoxNonJavaFile.TEST_ELEMENT(), testDoxModel.getValueAt(0, 0));
    }

    public void testUpdatesModelWithDoxFileFromFactoryWhenFileSelected() throws Exception {
        String fileNameWithoutExtension = "Foo";
        VirtualFile virtualFileMock = (VirtualFile) mockVirtualFile.proxy();
        TestDoxFile testDoxFile = new TestDoxNonJavaFile(virtualFileMock);

        mockTestDoxFileFactory.expects(once()).method("getTestDoxFile").with(eq(virtualFileMock)).will(returnValue(testDoxFile));
        mockVirtualFile.expects(once()).method("getNameWithoutExtension").will(returnValue(fileNameWithoutExtension));
        mockToolWindow.expects(once()).method("setTitle").with(eq(fileNameWithoutExtension));

        controller.selectedFileChanged(virtualFileMock);
        assertEquals(1, testDoxModel.getRowCount());
        assertEquals(TestDoxNonJavaFile.TEST_ELEMENT(), testDoxModel.getValueAt(0, 0));
    }

    public void testClosesToolWindowOnNavigationIfInSlidingMode() throws Exception {
        mockEditorApi.expects(once()).method("jumpToPsiClass").withAnyArguments().will(returnValue(true));
        mockToolWindow.expects(once()).method("getType").will(returnValue(ToolWindowType.SLIDING));
        mockToolWindow.expects(once()).method("hide");

        controller.jumpToTestElement(new TestClass(null, null, (EditorApi) mockEditorApi.proxy(), null), false);
    }

    public void testClosesToolWindowOnNavigationIfDockedAndAutohiding() throws Exception {
        mockEditorApi.expects(once()).method("jumpToPsiClass").withAnyArguments().will(returnValue(true));
        mockToolWindow.expects(once()).method("getType").will(returnValue(ToolWindowType.DOCKED));
        mockToolWindow.expects(once()).method("isAutoHide").will(returnValue(true));
        mockToolWindow.expects(once()).method("hide");

        controller.jumpToTestElement(new TestClass(null, null, (EditorApi) mockEditorApi.proxy(), null), false);
    }

    public void testDoesNotCloseToolWindowOnNavigationIfDocked() throws Exception {
        mockEditorApi.expects(once()).method("jumpToPsiClass").withAnyArguments().will(returnValue(true));
        mockToolWindow.expects(once()).method("getType").will(returnValue(ToolWindowType.DOCKED));
        mockToolWindow.expects(once()).method("isAutoHide").will(returnValue(false));

        controller.jumpToTestElement(new TestClass(null, null, (EditorApi) mockEditorApi.proxy(), null), false);
    }

    public void testDoesNotCloseToolWindowOnNavigationIfFloating() throws Exception {
        mockEditorApi.expects(once()).method("jumpToPsiClass").withAnyArguments().will(returnValue(true));
        mockToolWindow.expects(once()).method("getType").will(returnValue(ToolWindowType.FLOATING));
        mockToolWindow.expects(once()).method("isAutoHide").will(returnValue(false));

        controller.jumpToTestElement(new TestClass(null, null, (EditorApi) mockEditorApi.proxy(), null), false);
    }

    public void testDelegatesTestdoxListSortingToModel() throws Exception {
        TestDoxClass testDoxClass = new TestDoxClass(null, null, true, null, null, new TestMethod[1]);
        testDoxClass.updateModel(testDoxModel);

        controller.updateSort(false);
        assertFalse(controller.getConfiguration().getAlphabeticalSorting());

        controller.updateSort(true);
        assertTrue(controller.getConfiguration().getAlphabeticalSorting());
    }

    public void testCanCurrentFileBeUnitTestedReturnsFalseWhenThereAreNoActiveEditors() {
        TestDoxFile nullFile = new TestDoxNonJavaFile(null);
        mockEditorApi.expects(once()).method("getCurrentFile").will(returnValue(nullFile.file()));
        mockTestDoxFileFactory.expects(once()).method("getTestDoxFile").with(NULL).will(returnValue(nullFile));
        assertFalse(controller.canCurrentFileBeUnitTested());
    }

    public void testCanCurrentFileBeUnitTestedReturnsFalseWhenTheCurrentFileIsNotATestableProjectClass() {
        TestDoxFile nonJavaFile = new TestDoxNonProjectClass(new LightVirtualFile(), null, null, null);
        mockEditorApi.expects(once()).method("getCurrentFile").will(returnValue(nonJavaFile.file()));
        mockTestDoxFileFactory.expects(once()).method("getTestDoxFile").with(same(nonJavaFile.file())).will(returnValue(nonJavaFile));
        assertFalse(controller.canCurrentFileBeUnitTested());
    }

    public void testCanCurrentFileBeUnitTestedReturnsTrueWhenTheCurrentFileIsATestableProjectClass() {
        TestDoxClass testableClass = new TestDoxClass(new LightVirtualFile(), null, true, null, null, TestMethod.EMPTY_ARRAY());
        mockEditorApi.expects(once()).method("getCurrentFile").will(returnValue(testableClass.file()));
        mockTestDoxFileFactory.expects(once()).method("getTestDoxFile").with(same(testableClass.file())).will(returnValue(testableClass));
        assertTrue(controller.canCurrentFileBeUnitTested());
    }

    public void testDoesNothingIfAttemptingToGoToTestFromAFileThatCannotBeUnitTested() throws Exception {
        Mock mockTestDoxFile = Mocks.createAndRegisterTestDoxFileMock(this);
        mockEditorApi.expects(once()).method("getCurrentFile");
        mockTestDoxFileFactory.expects(once()).method("getTestDoxFile").with(NULL).will(returnValue(mockTestDoxFile.proxy()));
        mockTestDoxFile.expects(once()).method("isTestedClass").will(returnValue(true));
        mockTestDoxFile.expects(once()).method("canNavigateToTestClass").will(returnValue(false));
        mockTestDoxFile.expects(once()).method("canBeUnitTested").will(returnValue(false));

        controller.toggleTestClassAndTestedClass();
    }

    public void testGoesToTestedClassUsingIdeaApiIfAttemptingToNavigateFromTestClass() throws Exception {
        Mock mockTestDoxFile = Mocks.createAndRegisterTestDoxFileMock(this);
        mockEditorApi.expects(once()).method("getCurrentFile");
        mockTestDoxFileFactory.expects(once()).method("getTestDoxFile").with(NULL).will(returnValue(mockTestDoxFile.proxy()));
        mockTestDoxFile.expects(once()).method("isTestedClass").will(returnValue(false));
        mockTestDoxFile.expects(once()).method("canNavigateToTestedClass").will(returnValue(true));

        TestClass testClass = new TestClass(null, (PsiClass) mockPsiClass.proxy(), (EditorApi) mockEditorApi.proxy(), null);
        mockTestDoxFile.expects(once()).method("testedClass").will(returnValue(testClass));
        mockEditorApi.expects(once()).method("jumpToPsiClass").withAnyArguments().will(returnValue(true));
        mockToolWindow.expects(once()).method("getType").will(returnValue(ToolWindowType.DOCKED));
        mockToolWindow.expects(once()).method("isAutoHide").will(returnValue(false));

        controller.toggleTestClassAndTestedClass();
    }

    public void testGoesToTestClassUsingIdeaApiIfAttemptingToNavigateFromTestedClass() throws Exception {
        Mock mockTestDoxFile = Mocks.createAndRegisterTestDoxFileMock(this);
        mockEditorApi.expects(once()).method("getCurrentFile");
        mockTestDoxFileFactory.expects(once()).method("getTestDoxFile").with(NULL).will(returnValue(mockTestDoxFile.proxy()));
        mockTestDoxFile.expects(once()).method("isTestedClass").will(returnValue(true));
        mockTestDoxFile.expects(once()).method("canNavigateToTestClass").will(returnValue(true));

        TestClass testClass = new TestClass(null, (PsiClass) mockPsiClass.proxy(), (EditorApi) mockEditorApi.proxy(), null);
        mockTestDoxFile.expects(once()).method("testClass").will(returnValue(testClass));
        mockEditorApi.expects(once()).method("jumpToPsiClass").withAnyArguments().will(returnValue(true));
        mockToolWindow.expects(once()).method("getType").will(returnValue(ToolWindowType.DOCKED));
        mockToolWindow.expects(once()).method("isAutoHide").will(returnValue(false));

        controller.toggleTestClassAndTestedClass();
    }

    public void testPromptsToCreateTestForJavaFileIfTestClassNotFoundAndDelegatesToIdeaApiIfUserClicksYes() throws Exception {
        TestDoxClass testDoxClass = new TestDoxClass(null, null, true, null, null, null) {
            public boolean canBeUnitTested() {
                return true;
            }

            public boolean canNavigateToTestClass() {
                return false;
            }
        };

        mockEditorApi.expects(once()).method("createTestClass").with(eq(testDoxClass));
        assertCreateTestDialogShown(testDoxClass, true);
    }

    public void testPromptsToCreateTestForJavaFileIfTestClassNotFoundAndDoesNothingIfUserClicksNo() throws Exception {
        TestDoxClass testDoxClass = new TestDoxClass(null, null, true, null, null, null) {
            public boolean canBeUnitTested() {
                return true;
            }

            public boolean canNavigateToTestClass() {
                return false;
            }
        };

        assertCreateTestDialogShown(testDoxClass, false);
    }

    public void testShowsRenameDialogIfRenameActionFired() throws Exception {
        mockRenameDialog.expects(once()).method("show");
        mockRenameDialog.expects(once()).method("isOK").will(returnValue(false));

        controller.startRename(Mocks.createTestMethod("testAnythingThatWorks"));
    }

    public void testShowsRenameDialogIfRenameActionFiredFromWithinOrAroundTestMethodCodeBlock() throws Exception {
        Mock mockPsiIdentifier = mock(PsiIdentifier.class);
        Mock mockTestMethod = Mocks.createAndRegisterTestMethodMock(this);

        mockEditorApi.expects(once()).method("getCurrentFile");
        mockTestDoxFileFactory.expects(once()).method("getTestDoxFile").withAnyArguments().will(returnValue(mockTestDoxFile.proxy()));
        mockTestDoxFile.expects(once()).method("file");
        mockEditorApi.expects(once()).method("getCurrentTestMethod").with(eq(mockPsiIdentifier.proxy()), isA(SentenceManager.class), NULL).will(returnValue(mockTestMethod.proxy()));
        mockTestMethod.expects(once()).method("displayString").will(returnValue("does something useful"));

        mockRenameDialog.expects(once()).method("show");
        mockRenameDialog.expects(once()).method("isOK").will(returnValue(false));

        controller.startRename((PsiElement) mockPsiIdentifier.proxy());
    }

    public void testDoesNotShowRenameDialogIfRenameActionFiredFromOutsideTestMethodCodeBlock() throws Exception {
        mockEditorApi.expects(once()).method("getCurrentFile");
        mockTestDoxFileFactory.expects(once()).method("getTestDoxFile").withAnyArguments().will(returnValue(mockTestDoxFile.proxy()));
        mockTestDoxFile.expects(once()).method("file");
        mockEditorApi.expects(once()).method("getCurrentTestMethod").with(eq(mockPsiClass.proxy()), isA(SentenceManager.class), NULL).will(returnValue(null));
        controller.startRename((PsiElement) mockPsiClass.proxy());
    }

    public void testFiresRenameCommandThatDelegatesRenamingToIdeaIfOkPressedInRenameDialog() throws Exception {
        mockRenameDialog.expects(once()).method("show");
        mockRenameDialog.expects(once()).method("isOK").will(returnValue(true));
        mockRenameDialog.expects(once()).method("sentence").will(returnValue("whatever works"));
        mockSentenceManager.expects(once()).method("buildMethodName").will(returnValue("testWhateverWorks"));
        mockEditorApi.expects(once()).method("rename").with(NULL, eq("testWhateverWorks"));

        controller.startRename(Mocks.createTestMethod("testAnythingThatWorks"));
    }

    public void testDelegatesTestMethodDeletionToIdea() throws Exception {
        mockEditorApi.expects(once()).method("getCurrentFile");
        mockTestDoxFileFactory.expects(once()).method("getTestDoxFile").withAnyArguments().will(returnValue(mockTestDoxFile.proxy()));
        mockTestDoxFile.expects(once()).method("file");
        mockEditorApi.expects(once()).method("getCurrentTestMethod").withAnyArguments().will(returnValue(Mocks.createTestMethod("testAnythingThatWorks")));
        mockEditorApi.expects(once()).method("delete").withAnyArguments();

        controller.delete((PsiElement) mock(PsiMethod.class).proxy());
    }

    public void testDoesNotDeleteAnElementThatIsNotATestMethod() throws Exception {
        mockEditorApi.expects(once()).method("getCurrentFile");
        mockTestDoxFileFactory.expects(once()).method("getTestDoxFile").withAnyArguments().will(returnValue(mockTestDoxFile.proxy()));
        mockTestDoxFile.expects(once()).method("file");
        mockEditorApi.expects(once()).method("getCurrentTestMethod").withAnyArguments();
        controller.delete(null);
    }

    private void assertCreateTestDialogShown(TestDoxFile testDoxFile, boolean clickYes) {
        Configuration configuration = new Configuration();
        configuration.setCreateTestIfMissing(true);
        controller.setConfiguration(configuration);
        shouldCreateTestClass = clickYes;

        mockTestDoxFileFactory.expects(once()).method("getTestDoxFile").withAnyArguments().will(returnValue(testDoxFile));
        mockEditorApi.expects(once()).method("getCurrentFile");

        controller.toggleTestClassAndTestedClass();
        assertTrue(dialogShown);
    }

    public void testShowsAddTestDialogAndDoesNothingIfCancelPressedWhenAddTestActionFired() throws Exception {
        mockAddTestDialog.expects(once()).method("show");
        mockAddTestDialog.expects(once()).method("isOK").will(returnValue(false));

        controller.addTest();
    }

    public void testFiresAddMethodCommandThatDelegatesToIdeaIfOkPressedInAddTestDialog() throws Exception {
        mockAddTestDialog.expects(once()).method("show");
        mockAddTestDialog.expects(once()).method("isOK").will(returnValue(true));
        mockAddTestDialog.expects(once()).method("sentence").will(returnValue("has an undefined behaviour"));

        mockSentenceManager.expects(once()).method("buildMethodName").will(returnValue("testHasAnUndefinedBehaviour"));
        mockEditorApi.expects(once()).method("getCurrentFile");

        TestDoxClass testDoxClass = new TestDoxClass(null, null, false, Mocks.createTestClass(), null, TestMethod.EMPTY_ARRAY());
        mockTestDoxFileFactory.expects(once()).method("getTestDoxFile").with(NULL).will(returnValue(testDoxClass));
        mockEditorApi.expects(once()).method("addMethod").with(NULL, eq("public void testHasAnUndefinedBehaviour() {\n}"));

        controller.addTest();
    }

    public void testAddsCorrectAnnotationToNewTestMethod() throws Exception {
        configuration.setTestMethodAnnotation("@Zebra");
        configuration.setUsingAnnotations(true);
        mockAddTestDialog.expects(once()).method("show");
        mockAddTestDialog.expects(once()).method("isOK").will(returnValue(true));
        mockAddTestDialog.expects(once()).method("sentence").will(returnValue("has an undefined behaviour"));

        mockSentenceManager.expects(once()).method("buildMethodName").will(returnValue("hasAnUndefinedBehaviour"));
        mockEditorApi.expects(once()).method("getCurrentFile");

        TestDoxClass testDoxClass = new TestDoxClass(null, null, false, Mocks.createTestClass(), null, TestMethod.EMPTY_ARRAY());
        mockTestDoxFileFactory.expects(once()).method("getTestDoxFile").with(NULL).will(returnValue(testDoxClass));
        mockEditorApi.expects(once()).method("addMethod").with(NULL, eq("@Zebra\npublic void hasAnUndefinedBehaviour() {\n}"));

        controller.addTest();
    }

    public void testDoesNothingWhenAFileIsOpened() {
        controller.fileOpened(null, null);
    }

    public void testDoesNothingWhenAFileIsClosed() {
        controller.fileClosed(null, null);
    }

    public void testHasActiveEditorsReturnsFalseWhenThereAreNoActiveEditors() {
        mockEditorApi.expects(once()).method("getCurrentFile").will(returnValue(null));
        assertFalse(controller.hasActiveEditors());
    }

    public void testHasActiveEditorsReturnsTrueWhenThereIsAtLeastOneActiveEditor() {
        mockEditorApi.expects(once()).method("getCurrentFile").will(returnValue(new LightVirtualFile()));
        assertTrue(controller.hasActiveEditors());
    }
}
