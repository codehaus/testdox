package org.codehaus.testdox.intellij;

import com.intellij.openapi.localVcs.LvcsObject;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiPackage;
import org.intellij.openapi.testing.MockApplicationManager;
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;

import static jedi.functional.FunctionalPrimitives.array;

import org.codehaus.testdox.intellij.config.ConfigurationBean;

public class DeletionShadowingManagerTest extends MockObjectTestCase {

    private static final String PACKAGE_PATH = "src/java/com/acme";
    private static final String PACKAGE_NAME = "com.acme";
    private static final String CLASS_NAME = "Foo";

    private static final String FULLY_QUALIFIED_CLASS_NAME = PACKAGE_NAME + '.' + CLASS_NAME;
    private static final String FULLY_QUALIFIED_TEST_CLASS_NAME = FULLY_QUALIFIED_CLASS_NAME + "Test";

    static {
        MockApplicationManager.reset();
    }

    private final Mock mockPsiManager = mock(PsiManager.class);
    private final Mock mockPsiClass = mock(PsiClass.class);
    private final Mock mockEditorApi = mock(EditorApi.class);
    private final Mock mockNameResolver = mock(NameResolver.class);
    private final Mock mockLvcsObject = mock(LvcsObject.class);
    private final Mock mockVirtualFile = Mocks.createAndRegisterVirtualFileMock(this);
    private final Mock mockPsiDirectory = mock(PsiDirectory.class);
    private final Mock mockPsiPackage = mock(PsiPackage.class);
    private final Mock mockPsiJavaFile = mock(PsiJavaFile.class);

    private PsiManager psiManagerMock = (PsiManager) mockPsiManager.proxy();
    private VirtualFile virtualFileMock = (VirtualFile) mockVirtualFile.proxy();
    private ConfigurationBean config = new ConfigurationBean();

    private VirtualFileEvent fileDeletedEvent;
    private VirtualFileEvent directoryDeletedEvent;
    private DeletionShadowingManager deletionShadowingManager;

    protected void setUp() {
        deletionShadowingManager = new DeletionShadowingManager((EditorApi) mockEditorApi.proxy(), config,
                                                                (NameResolver) mockNameResolver.proxy());

        fileDeletedEvent = new VirtualFileEvent(psiManagerMock, virtualFileMock, CLASS_NAME + ".java", false, null);
        directoryDeletedEvent = new VirtualFileEvent(psiManagerMock, virtualFileMock, PACKAGE_PATH, true, null);
    }

    // Package deletion shadowing --------------------------------------------------------------------------------------

    public void testDoesNotShadowDirectoryDeletionIfTestdoxIsNotConfiguredToDeletePackageOccurrences() {
        config.setDeletePackageOccurrences(false);
        deletionShadowingManager.beforeFileDeletion(directoryDeletedEvent);
    }

    public void testDoesNotShadowDirectoryDeletionWhenTriggedByCancelledDeletion() {
        VirtualFileEvent event = new VirtualFileEvent(new Object(), virtualFileMock, PACKAGE_PATH, true, null);
        config.setDeletePackageOccurrences(false);
        deletionShadowingManager.beforeFileDeletion(event);
    }

    public void testDoesNotShadowDirectoryDeletionWhenTriggedByLocalVcsOperation() {
        VirtualFileEvent event = new VirtualFileEvent(mockLvcsObject.proxy(), virtualFileMock, PACKAGE_PATH, true, null);
        config.setDeletePackageOccurrences(false);
        deletionShadowingManager.beforeFileDeletion(event);
    }

    public void testDoesNotShadowDirectoryDeletionWhenTriggedByCvsDirectoryPrunner() {
        VirtualFileEvent event = new VirtualFileEvent(new Object(), virtualFileMock, PACKAGE_PATH, true, null);
        config.setDeletePackageOccurrences(false);
        deletionShadowingManager.beforeFileDeletion(event);
    }

    public void testDoesNotShadowDirectoryDeletionIfTheDirectoryBeingDeletedDoesNotRepresentAPackage() {
        mockEditorApi.expects(once()).method("getPsiDirectory").with(isA(VirtualFile.class)).will(returnValue(mockPsiDirectory.proxy()));
        mockPsiDirectory.expects(once()).method("getPackage");

        config.setDeletePackageOccurrences(true);
        deletionShadowingManager.beforeFileDeletion(directoryDeletedEvent);
    }

    public void testDoesNotShadowDirectoryDeletionIfTheDirectoryBeingDeletedIsTheOnlyOccurrenceOfThePackageItRepresents() {
        PsiDirectory deletedDirectory = (PsiDirectory) mockPsiDirectory.proxy();
        mockEditorApi.expects(once()).method("getPsiDirectory").with(isA(VirtualFile.class)).will(returnValue(deletedDirectory));
        mockPsiDirectory.expects(atLeastOnce()).method("getPackage").will(returnValue(mockPsiPackage.proxy()));
        mockPsiPackage.expects(once()).method("getDirectories").will(returnValue(array(deletedDirectory)));

        config.setDeletePackageOccurrences(true);
        deletionShadowingManager.beforeFileDeletion(directoryDeletedEvent);
    }

    public void testDoesNotShadowDirectoryDeletionIfTheOtherOccurrencesOfThePackageRepresentedByTheDirectoryBeingDeletedAreFlaggedAsReadOnly() {
        PsiDirectory deletedDirectory = (PsiDirectory) mockPsiDirectory.proxy();
        mockEditorApi.expects(once()).method("getPsiDirectory").with(isA(VirtualFile.class)).will(returnValue(deletedDirectory));
        mockPsiDirectory.expects(atLeastOnce()).method("getPackage").will(returnValue(mockPsiPackage.proxy()));

        Mock mockAnotherPsiDirectory = mock(PsiDirectory.class);
        PsiDirectory[] otherDirectories = array((PsiDirectory) mockAnotherPsiDirectory.proxy());
        mockPsiPackage.expects(once()).method("getDirectories").will(returnValue(otherDirectories));
        mockAnotherPsiDirectory.expects(once()).method("isWritable").will(returnValue(false));

        config.setDeletePackageOccurrences(true);
        deletionShadowingManager.beforeFileDeletion(directoryDeletedEvent);
    }

    public void testAsynchronouslyDeletesOtherOccurrencesOfThePackageRepresentedByTheDirectoryBeingDeleted() {
        PsiDirectory deletedDirectory = (PsiDirectory) mockPsiDirectory.proxy();
        mockEditorApi.expects(once()).method("getPsiDirectory").with(isA(VirtualFile.class)).will(returnValue(deletedDirectory));
        mockPsiDirectory.expects(atLeastOnce()).method("getPackage").will(returnValue(mockPsiPackage.proxy()));

        Mock mockAnotherPsiDirectory = mock(PsiDirectory.class);
        PsiDirectory[] otherDirectories = array((PsiDirectory) mockAnotherPsiDirectory.proxy());
        mockPsiPackage.expects(once()).method("getDirectories").will(returnValue(otherDirectories));
        mockPsiPackage.expects(once()).method("getQualifiedName").will(returnValue(PACKAGE_NAME));

        Mock mockAnotherVirtualFile = Mocks.createAndRegisterVirtualFileMock(this);
        mockAnotherPsiDirectory.expects(once()).method("getVirtualFile").will(returnValue(mockAnotherVirtualFile.proxy()));
        mockAnotherVirtualFile.expects(once()).method("getPath").will(returnValue("src/test/com/acme"));
        mockAnotherPsiDirectory.expects(once()).method("isWritable").will(returnValue(true));

        mockEditorApi.expects(once()).method("deleteAsynchronously").with(isA(PsiDirectory[].class), isA(String.class), isA(String.class), isA(Runnable.class));

        config.setDeletePackageOccurrences(true);
        deletionShadowingManager.beforeFileDeletion(directoryDeletedEvent);
    }

    // Class deletion shadowing ----------------------------------------------------------------------------------------

    public void testDoesNotShadowClassDeletionIfTestdoxIsNotConfiguredToAutomaticallyApplyChangesToTests() {
        config.setAutoApplyChangesToTests(false);
        deletionShadowingManager.beforeFileDeletion(fileDeletedEvent);
    }

    public void testDoesNotShadowClassDeletionWhenTriggedByCancelledDeletion() {
        VirtualFileEvent event = new VirtualFileEvent(new Object(), virtualFileMock, CLASS_NAME + ".java", false, null);
        config.setAutoApplyChangesToTests(true);
        deletionShadowingManager.beforeFileDeletion(event);
    }

    public void testDoesNotShadowClassDeletionWhenTriggedByLocalVcsOperation() {
        VirtualFileEvent event = new VirtualFileEvent(mockLvcsObject.proxy(), virtualFileMock, CLASS_NAME + ".java", false, null);
        config.setAutoApplyChangesToTests(true);
        deletionShadowingManager.beforeFileDeletion(event);
    }

    public void testDoesNotShadowClassDeletionWhenTriggedByCvsDirectoryPruner() {
        VirtualFileEvent event = new VirtualFileEvent(new Object(), virtualFileMock, CLASS_NAME + ".java", false, null);
        config.setAutoApplyChangesToTests(true);
        deletionShadowingManager.beforeFileDeletion(event);
    }

    public void testDoesNotShadowClassDeletionIfTheFileBeingDeletedIsNotAProjectClass() {
        mockEditorApi.expects(once()).method("getPsiJavaFile").with(isA(VirtualFile.class));

        config.setAutoApplyChangesToTests(true);
        deletionShadowingManager.beforeFileDeletion(fileDeletedEvent);
    }

    public void testDoesNotShadowClassDeletionIfTheFileBeingDeletedIsATestClass() {
        mockEditorApi.expects(once()).method("getPsiJavaFile").with(isA(VirtualFile.class)).will(returnValue(mockPsiJavaFile.proxy()));
        mockPsiJavaFile.expects(once()).method("getPackageName").will(returnValue(PACKAGE_NAME));
        mockPsiJavaFile.expects(once()).method("getClasses").will(returnValue(array((PsiClass) mockPsiClass.proxy())));
        mockPsiClass.expects(once()).method("getName").will(returnValue("FooTest"));
        mockNameResolver.expects(once()).method("isRealClass").with(eq(FULLY_QUALIFIED_TEST_CLASS_NAME)).will(returnValue(false));

        config.setAutoApplyChangesToTests(true);
        deletionShadowingManager.beforeFileDeletion(fileDeletedEvent);
    }

    public void testDoesNotShadowClassDeletionIfTheTestClassCannotBeFound() {
        mockEditorApi.expects(once()).method("getPsiJavaFile").with(isA(VirtualFile.class)).will(returnValue(mockPsiJavaFile.proxy()));
        mockPsiJavaFile.expects(once()).method("getPackageName").will(returnValue(PACKAGE_NAME));
        mockPsiJavaFile.expects(once()).method("getClasses").will(returnValue(array((PsiClass) mockPsiClass.proxy())));
        mockPsiClass.expects(once()).method("getName").will(returnValue(CLASS_NAME));
        mockNameResolver.expects(once()).method("isRealClass").with(eq(FULLY_QUALIFIED_CLASS_NAME)).will(returnValue(true));
        mockNameResolver.expects(once()).method("getTestClassName").with(eq(FULLY_QUALIFIED_CLASS_NAME)).will(returnValue(FULLY_QUALIFIED_TEST_CLASS_NAME));
        mockEditorApi.expects(once()).method("getPsiClass").with(eq(FULLY_QUALIFIED_TEST_CLASS_NAME));

        config.setAutoApplyChangesToTests(true);
        deletionShadowingManager.beforeFileDeletion(fileDeletedEvent);
    }

    public void testAsynchronouslyDeletesATestClassUsingTheEditorApiWhenTheCorrespondingTestedClassIsBeingDeleted() {
        mockEditorApi.expects(once()).method("getPsiJavaFile").with(isA(VirtualFile.class)).will(returnValue(mockPsiJavaFile.proxy()));
        mockPsiJavaFile.expects(once()).method("getPackageName").will(returnValue(PACKAGE_NAME));
        mockPsiJavaFile.expects(once()).method("getClasses").will(returnValue(array((PsiClass) mockPsiClass.proxy())));
        mockPsiClass.expects(once()).method("getName").will(returnValue(CLASS_NAME));
        mockNameResolver.expects(once()).method("isRealClass").with(eq(FULLY_QUALIFIED_CLASS_NAME)).will(returnValue(true));
        mockNameResolver.expects(once()).method("getTestClassName").with(eq(FULLY_QUALIFIED_CLASS_NAME)).will(returnValue(FULLY_QUALIFIED_TEST_CLASS_NAME));
        mockEditorApi.expects(once()).method("getPsiClass").with(eq(FULLY_QUALIFIED_TEST_CLASS_NAME)).will(returnValue(mockPsiClass.proxy()));
        mockEditorApi.expects(once()).method("deleteAsynchronously").with(isA(PsiClass.class));

        config.setAutoApplyChangesToTests(true);
        deletionShadowingManager.beforeFileDeletion(fileDeletedEvent);
    }
}
