package org.codehaus.testdox.intellij;

import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerAdapter;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.refactoring.listeners.RefactoringElementListener;
import com.intellij.refactoring.listeners.RefactoringElementListenerProvider;
import com.intellij.refactoring.listeners.RefactoringListenerManager;
import static jedi.functional.Coercions.array;
import org.codehaus.testdox.intellij.config.ConfigurationBean;
import org.codehaus.testdox.intellij.panel.ItemSelectionUI;
import org.intellij.openapi.testing.MockApplication;
import org.intellij.openapi.testing.MockApplicationManager;
import org.intellij.openapi.testing.MockVirtualFile;
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;

import java.util.LinkedList;
import java.util.List;

public class IntelliJApiTest extends MockObjectTestCase {

    static {
        MockApplicationManager.reset();
    }

    protected final Mock mockPsiManager = mock(PsiManager.class);
    protected final Mock mockProjectRootManager = mock(ProjectRootManager.class);
    protected final Mock mockModuleRootManager = mock(ModuleRootManager.class);
    protected final Mock mockFileEditorManager = mock(FileEditorManager.class);
    protected final Mock mockRefactoringListenerManager = mock(RefactoringListenerManager.class);
    protected final Mock mockProject = mock(Project.class);
    protected final Mock mockModule = mock(Module.class);
    protected final Mock mockPsiJavaFile = mock(PsiJavaFile.class);
    protected final Mock mockVirtualFile = Mocks.createAndRegisterVirtualFileMock(this);
    protected final Mock mockCommandProcessor = mock(CommandProcessor.class);
    protected final Mock mockItemSelectionUI = mock(ItemSelectionUI.class);
    protected final Mock mockVirtualFileSelectionUI = mock(ItemSelectionUI.class);

    protected IntelliJApi intelliJApi;
    private ConfigurationBean config;

    protected void setUp() {
        config = new ConfigurationBean();
        config.setTestNameTemplate(TemplateNameResolver.DEFAULT_TEMPLATE);
        config.setTestMethodPrefix("pants");

        intelliJApi = new IntelliJApi((Project) mockProject.proxy(), new TemplateNameResolver(config), config) {

            protected void invokeLater(Runnable task) {
            }

            public void rename(PsiElement element) {
            }

            public void rename(PsiElement element, String newName) {
            }

            public boolean jumpToPsiElement(PsiElement psiElement) {
                return false;
            }

            protected boolean jumpToPsiClass(VirtualFile containtingFile, PsiClass psiClass, int offset) {
                return false;
            }

            protected ItemSelectionUI createItemSelectionDialog(String[] packages) {
                return (ItemSelectionUI) mockItemSelectionUI.proxy();
            }

            protected ItemSelectionUI createVirtualFileSelectionDialog(VirtualFile[] items) {
                return (ItemSelectionUI) mockVirtualFileSelectionUI.proxy();
            }

            protected MoveClassCommand createMoveClassCommand(PsiClass psiClass, PsiDirectory destinationPackage) {
                return null;
            }
        };

        mockProject.stubs().method("getComponent").with(eq(PsiManager.class)).will(returnValue(mockPsiManager.proxy()));
        mockProject.stubs().method("getComponent").with(eq(ProjectRootManager.class)).will(returnValue(mockProjectRootManager.proxy()));
        mockProject.stubs().method("getComponent").with(eq(FileEditorManager.class)).will(returnValue(mockFileEditorManager.proxy()));
        mockProject.stubs().method("getComponent").with(eq(RefactoringListenerManager.class)).will(returnValue(mockRefactoringListenerManager.proxy()));
        mockModule.stubs().method("getComponent").with(eq(ModuleRootManager.class)).will(returnValue(mockModuleRootManager.proxy()));

        MockApplication applicationMock = MockApplicationManager.getMockApplication();
        applicationMock.registerComponent(PsiManager.class, mockPsiManager.proxy());
        applicationMock.registerComponent(ProjectRootManager.class, mockProjectRootManager.proxy());
        applicationMock.registerComponent(ModuleRootManager.class, mockModuleRootManager.proxy());
        applicationMock.registerComponent(FileEditorManager.class, mockFileEditorManager.proxy());
        applicationMock.registerComponent(RefactoringListenerManager.class, mockRefactoringListenerManager.proxy());
        applicationMock.registerComponent(CommandProcessor.class, mockCommandProcessor.proxy());

        mockProject.stubs().method("getPicoContainer").will(returnValue(applicationMock.getPicoContainer()));
    }

    protected void tearDown() throws Exception {
        MockApplication applicationMock = MockApplicationManager.getMockApplication();
        applicationMock.removeComponent(PsiManager.class);
        applicationMock.removeComponent(ProjectRootManager.class);
        applicationMock.removeComponent(ModuleRootManager.class);
        applicationMock.removeComponent(FileEditorManager.class);
        applicationMock.removeComponent(RefactoringListenerManager.class);
        applicationMock.removeComponent(CommandProcessor.class);
    }

    public void testDeterminesWhetherAGivenVirtualFileIsAJavaFile() {
        mockVirtualFile.expects(once()).method("isValid").will(returnValue(true));
        mockVirtualFile.expects(once()).method("isDirectory").will(returnValue(false));
        mockPsiManager.expects(once()).method("findFile").with(isA(VirtualFile.class)).will(returnValue(mockPsiJavaFile.proxy()));

        assertTrue(intelliJApi.isJavaFile((VirtualFile) mockVirtualFile.proxy()));
    }

    public void testDeterminesWhetherAGivenPsiElementRepresentsATestMethod() {
        Mock mockPsiMethod = mock(PsiMethod.class);
        mockPsiMethod.expects(once()).method("getName").will(returnValue("pantsWhateverWeLike"));

        assertTrue(intelliJApi.isTestMethod((PsiElement) mockPsiMethod.proxy()));
    }

    public void testDetectsAnnotatedTestMethods() throws Exception {
        config.setTestMethodAnnotation("@Giraffe");
        config.setUsingAnnotations(true);
        Mock mockPsiMethod = mock(PsiMethod.class);
        Mock mockPsiModifierList = mock(PsiModifierList.class);
        Mock mockPsiAnnotation1 = mock(PsiAnnotation.class);
        Mock mockPsiAnnotation2 = mock(PsiAnnotation.class);
        mockPsiMethod.expects(once()).method("getModifierList").will(returnValue(mockPsiModifierList.proxy()));
        mockPsiModifierList.expects(once()).method("getAnnotations")
            .will(returnValue(array((PsiAnnotation) mockPsiAnnotation1.proxy(), (PsiAnnotation) mockPsiAnnotation2.proxy())));
        mockPsiAnnotation1.expects(once()).method("getText").will(returnValue("@Norbert"));
        mockPsiAnnotation2.expects(once()).method("getText").will(returnValue("@Giraffe"));

        assertTrue(intelliJApi.isTestMethod((PsiElement) mockPsiMethod.proxy()));
    }

    public void testDelegatesTestClassCreationToOpenApi() throws ClassNotFoundException {
        Mock mockProjectFileIndex = mock(ProjectFileIndex.class);
        Mock mockContentEntry = mock(ContentEntry.class);
        Mock mockSourceFolder = mock(SourceFolder.class);

        TestDoxClass testDoxClass = new TestDoxClass(new MockVirtualFile("path-to-file", false), "com.acme.MyClass", true, null, null, null);
        mockProjectRootManager.expects(once()).method("getFileIndex").will(returnValue(mockProjectFileIndex.proxy()));
        mockProjectFileIndex.expects(once()).method("getModuleForFile").with(isA(VirtualFile.class)).will(returnValue(mockModule.proxy()));
        mockPsiManager.expects(once()).method("findFile").with(isA(VirtualFile.class)).will(returnValue(mockPsiJavaFile.proxy()));
        mockPsiJavaFile.expects(once()).method("getPackageName").will(returnValue("com.acme"));
        mockModuleRootManager.expects(once()).method("getContentEntries").will(returnValue(array((ContentEntry) mockContentEntry.proxy())));
        mockContentEntry.expects(once()).method("getSourceFolders").will(returnValue(array((SourceFolder) mockSourceFolder.proxy())));
        mockSourceFolder.expects(once()).method("getFile").will(returnValue(new MockVirtualFile("src", true)));
        mockContentEntry.expects(once()).method("getExcludeFolderFiles").will(returnValue(new VirtualFile[0]));

        mockItemSelectionUI.expects(once()).method("show");
        mockItemSelectionUI.expects(once()).method("isOK").will(returnValue(true));
        mockItemSelectionUI.expects(once()).method("getSelectedItem").will(returnValue("testPackage"));

        mockVirtualFileSelectionUI.expects(once()).method("show");
        mockVirtualFileSelectionUI.expects(once()).method("isOK").will(returnValue(true));
        mockVirtualFileSelectionUI.expects(once()).method("getSelectedItem").will(returnValue(mockVirtualFile.proxy()));

        mockCommandProcessor.expects(once()).method("executeCommand").withAnyArguments();

        intelliJApi.createTestClass(testDoxClass);
    }

    public void testRegistersFileEditorManagerListeners() {
        FileEditorManagerListener listener = new FileEditorManagerAdapter() {
        };

        mockFileEditorManager.expects(once()).method("addFileEditorManagerListener").with(same(listener));
        intelliJApi.addFileEditorManagerListener(listener);

        mockFileEditorManager.expects(once()).method("removeFileEditorManagerListener").with(same(listener));
        intelliJApi.removeFileEditorManagerListener(listener);
    }

    public void testRegistersRefactoringListenerProviders() {
        RefactoringElementListenerProvider listenerProvider = new RefactoringElementListenerProvider() {
            public RefactoringElementListener getListener(PsiElement element) {
                throw new UnsupportedOperationException();
            }
        };

        mockRefactoringListenerManager.expects(once()).method("addListenerProvider").with(same(listenerProvider));
        intelliJApi.addRefactoringElementListenerProvider(listenerProvider);

        mockRefactoringListenerManager.expects(once()).method("removeListenerProvider").with(same(listenerProvider));
        intelliJApi.removeRefactoringElementListenerProvider(listenerProvider);
    }

    public void testRegistersPsiTreeChangeListeners() {
        PsiTreeChangeListener listener = new PsiTreeChangeAdapter() {
        };

        mockPsiManager.expects(once()).method("addPsiTreeChangeListener").with(same(listener));
        intelliJApi.addPsiTreeChangeListener(listener);

        mockPsiManager.expects(once()).method("removePsiTreeChangeListener").with(same(listener));
        intelliJApi.removePsiTreeChangeListener(listener);
    }

    public void testChoosesAFolderWithTestAsWholePathPartAsMoreSuitableThanAMoreWordyFolderForTestDestination() {
        List<VirtualFile> folders = new LinkedList<VirtualFile>();
        folders.add(new MockVirtualFile("blah/foo", true));
        folders.add(new MockVirtualFile("foo/testicles", true));
        folders.add(new MockVirtualFile("foo/test", true));

        intelliJApi.orderBySuitability(folders);
        assertEquals("foo/test", folders.get(0).getName());
        assertEquals("foo/testicles", folders.get(1).getName());
        assertEquals("blah/foo", folders.get(2).getName());
    }

    public void testChoosesAFolderWithTestInItsPathAsMoreSuitableThanFolderWithoutTestInItsPathForTestDestination() {
        List<VirtualFile> folders = new LinkedList<VirtualFile>();
        folders.add(new MockVirtualFile("blah/foo", true));
        folders.add(new MockVirtualFile("foo/testicles", true));
        folders.add(new MockVirtualFile("blah/foonukr", true));

        intelliJApi.orderBySuitability(folders);
        assertEquals("foo/testicles", folders.get(0).getName());
        assertEquals("blah/foo", folders.get(1).getName());
        assertEquals("blah/foonukr", folders.get(2).getName());
    }

    public void testChoosesShortestMatchingFolderForTestDestination() {
        List<VirtualFile> folders = new LinkedList<VirtualFile>();
        folders.add(new MockVirtualFile("blah/foo", true));
        folders.add(new MockVirtualFile("foo/test/craphead", true));
        folders.add(new MockVirtualFile("bah/test/testify", true));

        intelliJApi.orderBySuitability(folders);
        assertEquals("bah/test/testify", folders.get(0).getName());
        assertEquals("foo/test/craphead", folders.get(1).getName());
        assertEquals("blah/foo", folders.get(2).getName());
    }

    public void testChoosesBestMatchingFolderWithShortestPathForTestDestination() {
        List<VirtualFile> folders = new LinkedList<VirtualFile>();
        folders.add(new MockVirtualFile("blah/test-acceptance", true));
        folders.add(new MockVirtualFile("blah/tests", true));

        intelliJApi.orderBySuitability(folders);
        assertEquals("blah/tests", folders.get(0).getName());
        assertEquals("blah/test-acceptance", folders.get(1).getName());
    }
}
