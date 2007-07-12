package org.codehaus.testdox.intellij.demetra;

import com.intellij.ide.util.EditSourceUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.refactoring.MoveDestination;
import com.intellij.refactoring.PackageWrapper;
import com.intellij.refactoring.move.moveClassesOrPackages.AutocreatingSingleSourceRootMoveDestination;
import com.intellij.refactoring.move.moveClassesOrPackages.MoveClassesOrPackagesUtil;
import com.intellij.refactoring.rename.RenameDialog;
import com.intellij.refactoring.rename.RenameProcessor;
import com.intellij.util.IncorrectOperationException;
import org.codehaus.testdox.intellij.IntelliJApi;
import org.codehaus.testdox.intellij.NameResolver;
import org.codehaus.testdox.intellij.config.ConfigurationBean;

public class DemetraApi extends IntelliJApi {

    public DemetraApi(Project project, NameResolver nameResolver, ConfigurationBean config) {
        super(project, nameResolver, config);
    }

    protected void invokeLater(Runnable task) {
        ApplicationManager.getApplication().invokeLater(task);
    }

    public void rename(PsiElement element) {
        new RenameDialog(project, element, null, null).show();
    }

    public void rename(PsiElement element, String newName) {
        new RenameProcessor(project, element, newName, false, false).run();
        runCommand(project, new RenameCommand(element, newName), "Rename Element", "TestDox");
    }

    public boolean jumpToPsiElement(PsiElement psiElement) {
        return openInAssociatedTextEditor(EditSourceUtil.getDescriptor(psiElement));
    }

    protected boolean jumpToPsiClass(VirtualFile containtingFile, PsiClass psiClass, int offset) {
        Navigatable descriptor = (offset > 0) ? new OpenFileDescriptor(project, containtingFile, offset)
                                              : EditSourceUtil.getDescriptor(psiClass);
        return openInAssociatedTextEditor(descriptor);
    }

    private boolean openInAssociatedTextEditor(Navigatable navigatable) {
        if (navigatable != null) {
            navigatable.navigate(true);
            return navigatable.canNavigate();
        }

        return false;
    }

    protected MoveClassCommand createMoveClassCommand(PsiClass psiClass, String destinationPackageName) {
        return new DemetraMoveClassCommand(psiClass, destinationPackageName);
    }

    protected class DemetraMoveClassCommand extends MoveClassCommand {

        public DemetraMoveClassCommand(PsiClass psiClass, String destinationPackageName) {
            super(psiClass, destinationPackageName);
        }

        protected void move() {
            try {
                PackageWrapper packageWrapper = new PackageWrapper(getPsiManager(), destinationPackageName);
                MoveDestination moveDestination = new AutocreatingSingleSourceRootMoveDestination(packageWrapper, findSourceFolder());
                MoveClassesOrPackagesUtil.doMoveClass(psiClass, moveDestination);
            } catch (IncorrectOperationException e) {
                LOGGER.error(e);
                showErrorMessage("Could not add method: " + e.getMessage(), "Warning");
            }
        }
    }
}
