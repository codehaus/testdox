package org.codehaus.testdox.intellij;

import com.intellij.openapi.vfs.VirtualFileAdapter;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import org.codehaus.testdox.intellij.config.ConfigurationBean;

import java.util.ArrayList;
import java.util.List;

class DeletionShadowingManager extends VirtualFileAdapter {

    private final ConfigurationBean config;
    private final NameResolver nameResolver;
    private final EditorApi editorApi;

    private boolean deleting;
    private final Runnable taskCompletionMarker = new Runnable() {
        public void run() {
            deleting = false;
        }
    };

    public DeletionShadowingManager(EditorApi editorApi, ConfigurationBean config, NameResolver nameResolver) {
        this.config = config;
        this.nameResolver = nameResolver;
        this.editorApi = editorApi;
    }

    public void beforeFileDeletion(VirtualFileEvent event) {
        if (event.getRequestor() instanceof PsiManager) {
            if (event.getFile().isDirectory()) {
                if (config.isDeletePackageOccurrences()) {
                    deleteOtherPackageOccurrences(editorApi.getPsiDirectory(event.getFile()));
                }
            } else if (config.isAutoApplyChangesToTest()) {
                deleteCorrespondingTestClass(editorApi.getPsiJavaFile(event.getFile()));
            }
        }
    }

    private void deleteOtherPackageOccurrences(PsiDirectory deletedDirectory) {
        if ((!deleting) && (deletedDirectory != null) && (deletedDirectory.getPackage() != null)) {
            PsiDirectory[] deletableDirectories = retrieveOtherDeletablePackageOccurrences(deletedDirectory);
            if (deletableDirectories.length > 0) {
                String packageName = deletedDirectory.getPackage().getQualifiedName();
                editorApi.deleteAsynchronously(deletableDirectories, buildQuestion(packageName, deletableDirectories),
                    "Delete Other Package Occurrences", taskCompletionMarker);
                deleting = true;
            }
        }
    }

    private PsiDirectory[] retrieveOtherDeletablePackageOccurrences(PsiDirectory deletedDirectory) {
        List<PsiDirectory> deletablePackageOccurrences = new ArrayList<PsiDirectory>();
        for (PsiDirectory directory : deletedDirectory.getPackage().getDirectories()) {
            if ((!directory.equals(deletedDirectory)) && (directory.isWritable())) {
                deletablePackageOccurrences.add(directory);
            }
        }
        return deletablePackageOccurrences.toArray(PsiDirectory.EMPTY_ARRAY);
    }

    private String buildQuestion(String packageName, PsiDirectory[] deletableDirectories) {
        StringBuffer question = new StringBuffer("Do you also want to delete the following occurrences of package '");
        question.append(packageName).append("'?\n ");
        for (PsiDirectory deletableDirectory : deletableDirectories) {
            question.append('\n').append(deletableDirectory.getVirtualFile().getPath());
        }
        return question.toString();
    }

    private void deleteCorrespondingTestClass(PsiJavaFile deletedClassFile) {
        if (deletedClassFile != null) {
            String deletedClassName = deletedClassFile.getPackageName() + '.' + deletedClassFile.getClasses()[0].getName();
            if (nameResolver.isRealClass(deletedClassName)) {
                String testClassName = nameResolver.getTestClassName(deletedClassName);
                PsiClass testClass = editorApi.getPsiClass(testClassName);
                if (testClass != null) {
                    editorApi.deleteAsynchronously(testClass);
                }
            }
        }
    }
}
