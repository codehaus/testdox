package org.codehaus.testdox.intellij;

import com.intellij.history.LocalHistory;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.intellij.openapi.vfs.newvfs.impl.NullVirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiDirectoryMockBuilder;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiPackage;
import static jedi.functional.Coercions.array;
import org.codehaus.testdox.intellij.config.ConfigurationBean;
import org.intellij.openapi.testing.MockApplicationManager;
import org.jmock.Mock;

public class DeletionShadowingManagerTest extends TestDoxMockObjectTestCase {

    private static final String PACKAGE_PATH = "src/java/com/acme";
    private static final String PACKAGE_NAME = "com.acme";
    private static final String CLASS_NAME = "Foo";

    private static final String FULLY_QUALIFIED_CLASS_NAME = PACKAGE_NAME + '.' + CLASS_NAME;
    private static final String FULLY_QUALIFIED_TEST_CLASS_NAME = FULLY_QUALIFIED_CLASS_NAME + "Test";

    static {
        MockApplicationManager.reset();
    }

    private final ConfigurationBean config = new ConfigurationBean();

    // Package deletion shadowing --------------------------------------------------------------------------------------

    public void testDoesNotShadowDirectoryDeletionIfTestdoxIsNotConfiguredToDeletePackageOccurrences() {
        DeletionShadowingManager deletionShadowingManager = new DeletionShadowingManager(
            createdEditorApi(), config, createNameResolver());

        config.setDeletePackageOccurrences(false);
        deletionShadowingManager.beforeFileDeletion(createDirectoryDeletedEvent());
    }

    private NameResolver createNameResolver() {
        return (NameResolver) newDummy(NameResolver.class);
    }

    private EditorApi createdEditorApi() {
        return (EditorApi) newDummy(EditorApi.class);
    }

    public void testDoesNotShadowDirectoryDeletionWhenTriggedByCancelledDeletion() {
        DeletionShadowingManager deletionShadowingManager = new DeletionShadowingManager(
            createdEditorApi(), config, createNameResolver());

        VirtualFileEvent event = createVcsDirectoryPruningEvent();
        config.setDeletePackageOccurrences(false);

        deletionShadowingManager.beforeFileDeletion(event);
    }

    public void testDoesNotShadowDirectoryDeletionWhenTriggedByLocalVcsOperation() {
        DeletionShadowingManager deletionShadowingManager = new DeletionShadowingManager(
            createdEditorApi(), config, createNameResolver());

        VirtualFileEvent event = new VirtualFileEvent(mock(LocalHistory.class).proxy(), (VirtualFile) mock(VirtualFile.class).proxy(), PACKAGE_PATH, null);
        config.setDeletePackageOccurrences(false);
        deletionShadowingManager.beforeFileDeletion(event);
    }

    public void testDoesNotShadowDirectoryDeletionWhenTriggedByCvsDirectoryPrunner() {
        DeletionShadowingManager deletionShadowingManager = new DeletionShadowingManager(
            createdEditorApi(), config, createNameResolver());

        VirtualFileEvent event = createVcsDirectoryPruningEvent();
        config.setDeletePackageOccurrences(false);
        deletionShadowingManager.beforeFileDeletion(event);
    }

    private VirtualFileEvent createVcsDirectoryPruningEvent() {
        return new VirtualFileEvent(mock(LocalHistory.class).proxy(), (VirtualFile) mock(VirtualFile.class).proxy(), PACKAGE_PATH, null);
    }

    public void testDoesNotShadowDirectoryDeletionIfTheDirectoryBeingDeletedDoesNotRepresentAPackage() {
        EditorApi editorApi = mock().editorApi()
            .expectGetPsiDirectoryReturns(mock().psiDirectory()
                .expectGetPackageAtLeastOnceReturns(null)
                .build())
            .build();

        config.setDeletePackageOccurrences(true);
        new DeletionShadowingManager(editorApi, config, createNameResolver())
            .beforeFileDeletion(createDirectoryDeletedEvent());
    }

    public void testDoesNotShadowDirectoryDeletionIfTheDirectoryBeingDeletedIsTheOnlyOccurrenceOfThePackageItRepresents() {
        PsiDirectoryMockBuilder psiDirectoryMockBuilder = mock().psiDirectory();
        PsiDirectory deletedDirectory = psiDirectoryMockBuilder.build();

        psiDirectoryMockBuilder
            .expectGetPackageAtLeastOnceReturns(mock().psiPackage()
                .expectGetDirectories(array(deletedDirectory))
                .build())
            .build();

        EditorApi editorApi = mock().editorApi()
            .expectGetPsiDirectoryReturns(deletedDirectory)
            .build();

        config.setDeletePackageOccurrences(true);

        new DeletionShadowingManager(editorApi, config, createNameResolver())
            .beforeFileDeletion(createDirectoryDeletedEvent());
    }

    public void testDoesNotShadowDirectoryDeletionIfTheOtherOccurrencesOfThePackageRepresentedByTheDirectoryBeingDeletedAreFlaggedAsReadOnly() {
        PsiDirectory deletedDirectory = mock().psiDirectory()
            .expectGetPackageAtLeastOnceReturns(mock().psiPackage()
                .expectGetDirectories(array(mock().psiDirectory().withWritable(false).build()))
                .build())
            .build();

        EditorApi editorApi = mock().editorApi()
            .expectGetPsiDirectoryReturns(deletedDirectory)
            .build();

        config.setDeletePackageOccurrences(true);
        new DeletionShadowingManager(editorApi, config, createNameResolver())
            .beforeFileDeletion(createDirectoryDeletedEvent());
    }

    public void testAsynchronouslyDeletesOtherOccurrencesOfThePackageRepresentedByTheDirectoryBeingDeleted() {
        Mock mockAnotherVirtualFile = Mocks.createAndRegisterVirtualFileMock(this);
        mockAnotherVirtualFile.expects(once()).method("getPath").will(returnValue("src/test/com/acme"));

        PsiPackage psiPackage = mock().psiPackage()
            .withQualifiedName(PACKAGE_NAME)
            .expectGetDirectories(array(mock().psiDirectory()
                .withWritable(true)
                .withVirtualFile((VirtualFile) mockAnotherVirtualFile.proxy())
                .build()))
            .build();

        PsiDirectory deletedDirectory = mock().psiDirectory()
            .expectGetPackageAtLeastOnceReturns(psiPackage)
            .build();

        EditorApi editorApi = mock().editorApi()
            .expectGetPsiDirectoryReturns(deletedDirectory)
            .expectDeleteAsynchronously()
            .build();

        config.setDeletePackageOccurrences(true);
        new DeletionShadowingManager(editorApi, config, createNameResolver())
            .beforeFileDeletion(createDirectoryDeletedEvent());
    }

    // Class deletion shadowing ----------------------------------------------------------------------------------------

    public void testDoesNotShadowClassDeletionIfTestdoxIsNotConfiguredToAutomaticallyApplyChangesToTests() {
        DeletionShadowingManager deletionShadowingManager = new DeletionShadowingManager(
            createdEditorApi(), config, createNameResolver());

        config.setAutoApplyChangesToTests(false);
        deletionShadowingManager.beforeFileDeletion(createFileDeletedEvent());
    }

    public void testDoesNotShadowClassDeletionWhenTriggedByCancelledDeletion() {
        DeletionShadowingManager deletionShadowingManager = new DeletionShadowingManager(
            createdEditorApi(), config, createNameResolver());

        VirtualFileEvent event = new VirtualFileEvent(new Object(), NullVirtualFile.INSTANCE, CLASS_NAME + ".java", null);
        config.setAutoApplyChangesToTests(true);
        deletionShadowingManager.beforeFileDeletion(event);
    }

    public void testDoesNotShadowClassDeletionWhenTriggedByLocalVcsOperation() {
        DeletionShadowingManager deletionShadowingManager = new DeletionShadowingManager(
            createdEditorApi(), config, createNameResolver());

        VirtualFileEvent event = createVcsDirectoryPruningEvent();
        config.setAutoApplyChangesToTests(true);
        deletionShadowingManager.beforeFileDeletion(event);
    }

    public void testDoesNotShadowClassDeletionWhenTriggedByCvsDirectoryPruner() {
        DeletionShadowingManager deletionShadowingManager = new DeletionShadowingManager(
            createdEditorApi(), config, createNameResolver());

        VirtualFileEvent event = createVcsDirectoryPruningEvent();
        config.setAutoApplyChangesToTests(true);
        deletionShadowingManager.beforeFileDeletion(event);
    }

    public void testDoesNotShadowClassDeletionIfTheFileBeingDeletedIsNotAProjectClass() {
//        mockEditorApi.expects(once()).method("getPsiJavaFile").with(isA(VirtualFile.class));
        DeletionShadowingManager deletionShadowingManager = new DeletionShadowingManager(
            createdEditorApi(), config, createNameResolver());


        config.setAutoApplyChangesToTests(true);
        deletionShadowingManager.beforeFileDeletion(createFileDeletedEvent());
    }

    public void testDoesNotShadowClassDeletionIfTheFileBeingDeletedIsATestClass() {
/*
        mockEditorApi.expects(once()).method("getPsiJavaFile").with(isA(VirtualFile.class)).will(returnValue(mockPsiJavaFile.proxy()));
        mockPsiJavaFile.expects(once()).method("getPackageName").will(returnValue(PACKAGE_NAME));
        mockPsiJavaFile.expects(once()).method("getClasses").will(returnValue(array((PsiClass) mockPsiClass.proxy())));
        mockPsiClass.expects(once()).method("getName").will(returnValue("FooTest"));
        mockNameResolver.expects(once()).method("isRealClass").with(eq(FULLY_QUALIFIED_TEST_CLASS_NAME)).will(returnValue(false));
*/
        DeletionShadowingManager deletionShadowingManager = new DeletionShadowingManager(
            createdEditorApi(), config, createNameResolver());


        config.setAutoApplyChangesToTests(true);
        deletionShadowingManager.beforeFileDeletion(createFileDeletedEvent());
    }

    public void testDoesNotShadowClassDeletionIfTheTestClassCannotBeFound() {
/*
        mockEditorApi.expects(once()).method("getPsiJavaFile").with(isA(VirtualFile.class)).will(returnValue(mockPsiJavaFile.proxy()));
        mockPsiJavaFile.expects(once()).method("getPackageName").will(returnValue(PACKAGE_NAME));
        mockPsiJavaFile.expects(once()).method("getClasses").will(returnValue(array((PsiClass) mockPsiClass.proxy())));
        mockPsiClass.expects(once()).method("getName").will(returnValue(CLASS_NAME));
        mockNameResolver.expects(once()).method("isRealClass").with(eq(FULLY_QUALIFIED_CLASS_NAME)).will(returnValue(true));
        mockNameResolver.expects(once()).method("getTestClassName").with(eq(FULLY_QUALIFIED_CLASS_NAME)).will(returnValue(FULLY_QUALIFIED_TEST_CLASS_NAME));
        mockEditorApi.expects(once()).method("getPsiClass").with(eq(FULLY_QUALIFIED_TEST_CLASS_NAME));
*/
        DeletionShadowingManager deletionShadowingManager = new DeletionShadowingManager(
            createdEditorApi(), config, createNameResolver());


        config.setAutoApplyChangesToTests(true);
        deletionShadowingManager.beforeFileDeletion(createFileDeletedEvent());
    }

    public void testAsynchronouslyDeletesATestClassUsingTheEditorApiWhenTheCorrespondingTestedClassIsBeingDeleted() {
/*
        mockEditorApi.expects(once()).method("getPsiJavaFile").with(isA(VirtualFile.class)).will(returnValue(mockPsiJavaFile.proxy()));
        mockPsiJavaFile.expects(once()).method("getPackageName").will(returnValue(PACKAGE_NAME));
        mockPsiJavaFile.expects(once()).method("getClasses").will(returnValue(array((PsiClass) mockPsiClass.proxy())));
        mockPsiClass.expects(once()).method("getName").will(returnValue(CLASS_NAME));
        mockNameResolver.expects(once()).method("isRealClass").with(eq(FULLY_QUALIFIED_CLASS_NAME)).will(returnValue(true));
        mockNameResolver.expects(once()).method("getTestClassName").with(eq(FULLY_QUALIFIED_CLASS_NAME)).will(returnValue(FULLY_QUALIFIED_TEST_CLASS_NAME));
        mockEditorApi.expects(once()).method("getPsiClass").with(eq(FULLY_QUALIFIED_TEST_CLASS_NAME)).will(returnValue(mockPsiClass.proxy()));
        mockEditorApi.expects(once()).method("deleteAsynchronously").with(isA(PsiClass.class));
*/

        DeletionShadowingManager deletionShadowingManager = new DeletionShadowingManager(
            createdEditorApi(), config, createNameResolver());

        config.setAutoApplyChangesToTests(true);
        deletionShadowingManager.beforeFileDeletion(createFileDeletedEvent());
    }

    private VirtualFileEvent createDirectoryDeletedEvent() {
        return real().virtualFileEvent()
            .withRequestor(mock(PsiManager.class).proxy())
            .withFileName(PACKAGE_PATH)
            .withIsDirectory(true)
            .build();
    }

    private VirtualFileEvent createFileDeletedEvent() {
        return real().virtualFileEvent()
            .withFileDeleted()
            .build();
    }
}
