package org.codehaus.testdox.intellij;

import com.intellij.ide.util.DeleteHandler;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandListener;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileListener;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.listeners.RefactoringElementListener;
import com.intellij.refactoring.listeners.RefactoringElementListenerProvider;
import com.intellij.refactoring.listeners.RefactoringListenerManager;
import com.intellij.refactoring.rename.RenameUtil;
import com.intellij.usageView.UsageInfo;
import com.intellij.util.IncorrectOperationException;
import static jedi.functional.Coercions.*;
import org.codehaus.testdox.intellij.config.ConfigurationBean;
import org.codehaus.testdox.intellij.panel.ItemSelectionDialog;
import org.codehaus.testdox.intellij.panel.ItemSelectionUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public abstract class IntelliJApi implements EditorApi {

    protected static final String RENAME_COMMAND_PREFIX = "Renaming class ";
    protected static final UsageInfo[] EMPTY_USAGE_INFO = new UsageInfo[0];

    protected static final RefactoringElementListener REFACTORING_ELEMENT_ADAPTER = new RefactoringElementListener() {
        public void elementMoved(@NotNull PsiElement psiElement) {
        }

        public void elementRenamed(@NotNull PsiElement psiElement) {
        }
    };

    protected static final Logger LOGGER = Logger.getInstance(IntelliJApi.class.getName());

    protected final Project project;
    protected final ConfigurationBean config;
    protected final NameResolver nameResolver;

    public IntelliJApi(Project project, NameResolver nameResolver, ConfigurationBean config) {
        this.project = project;
        this.config = config;
        this.nameResolver = nameResolver;
    }

    public ToolWindowManager getToolWindowManager() {
        return ToolWindowManager.getInstance(project);
    }

    public void activateSelectedTextEditor() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                getSelectedTextEditor().getContentComponent().requestFocus();
            }
        });
    }

    public Editor getSelectedTextEditor() {
        return getFileEditorManager().getSelectedTextEditor();
    }

    public VirtualFile getCurrentFile() {
        VirtualFile[] selectedFiles = getFileEditorManager().getSelectedFiles();
        return (selectedFiles.length > 0) ? selectedFiles[0] : null;
    }

    public VirtualFile getVirtualFile(PsiClass psiClass) {
        return psiClass.getContainingFile().getVirtualFile();
    }

    public PsiDirectory getPsiDirectory(VirtualFile file) {
        return getPsiManager().findDirectory(file);
    }

    public PsiJavaFile getPsiJavaFile(VirtualFile file) {
        if (file != null) {
            PsiFile psiFile = getPsiManager().findFile(file);
            if (psiFile instanceof PsiJavaFile) {
                return (PsiJavaFile) psiFile;
            }
        }
        return null;
    }

    public PsiClass getPsiClass(String className) {
        return javaPsiFacade().findClass(className, GlobalSearchScope.projectScope(project));
    }

    public PsiMethod[] getMethods(PsiClass testClass) {
        return testClass.getAllMethods();
    }

    public boolean isJavaFile(VirtualFile file) {
        return (file != null) && (file.isValid()) && (!file.isDirectory()) && (getPsiJavaFile(file) != null);
    }

    public boolean isInterface(String className) {
        PsiClass psiClass = javaPsiFacade().findClass(className, GlobalSearchScope.allScope(project));
        return ((psiClass != null) && (psiClass.isInterface()));
    }

    public boolean jumpToPsiClass(PsiClass psiClass) {
        if (psiClass == null) {
            return false;
        }

        Editor matchingEditor = null;
        PsiFile containingPsiFile = psiClass.getContainingFile();
        VirtualFile containtingVirtualFile = (containingPsiFile != null) ? containingPsiFile.getVirtualFile() : null;

        if (containtingVirtualFile != null) {
            for (Editor editor : EditorFactory.getInstance().getAllEditors()) {
                PsiFile psiFile = getPsiDocumentManager().getPsiFile(editor.getDocument());
                if ((psiFile != null) && (psiFile.getVirtualFile() != null)) {
                    if (containtingVirtualFile.getPath().equals(psiFile.getVirtualFile().getPath())) {
                        matchingEditor = editor;
                        break;
                    }
                }
            }
        }

        int offset = (matchingEditor != null) ? matchingEditor.getCaretModel().getOffset() : 0;
        return jumpToPsiClass(containtingVirtualFile, psiClass, offset);

    }

    protected abstract boolean jumpToPsiClass(VirtualFile containtingFile, PsiClass psiClass, int offset);

    public void createTestClass(TestDoxFile testDoxFile) {
        ProjectFileIndex index = ProjectRootManager.getInstance(project).getFileIndex();
        Module module = index.getModuleForFile(testDoxFile.file());
        String testClassName = getTestClassName(testDoxFile);
        String testPackage = getTestPackage(testDoxFile);
        VirtualFile targetDirectory = getTestDirectory(module);

        if (targetDirectory != null && testPackage != null && testClassName != null) {
            runCommand(project, new CreateClassCommand(testClassName, testPackage, targetDirectory), "CreateTest", "TestDox");
        }
    }

    public void addMethod(PsiClass psiClass, String methodSignatureAndBody) {
        runCommand(project, new AddMethodCommand(psiClass, methodSignatureAndBody), "Add Method", "TestDox");
    }

    public void move(PsiClass psiClass, PsiDirectory destinationPackage) {
        runCommand(project, createMoveClassCommand(psiClass, destinationPackage), "Move Class", "TestDox");
    }

    public void renameTest(String className, TestDoxFileFactory testDoxFileFactory) {
        if (className != null && className.startsWith(RENAME_COMMAND_PREFIX)) {
            String name = className.substring(RENAME_COMMAND_PREFIX.length());
            String oldClassName = name.substring(0, name.indexOf(" ", 1)).trim();
            oldClassName = nameResolver.getTestClassName(oldClassName);
            String newName = nameResolver.getTestClassName(name.substring(name.lastIndexOf(" ") + 1));
            PsiClass renamedClass = getPsiClass(oldClassName);
            if (renamedClass != null) {
                TestDoxFile testDoxFile = testDoxFileFactory.getTestDoxFile(getVirtualFile(renamedClass));
                if (testDoxFile.canNavigateToTestClass()) {
                    rename(testDoxFile.testClass().psiElement(), newName);
                }
            }
        }
    }

    public void delete(PsiElement psiElement) {
        DeleteHandler.deletePsiElement(array(psiElement), project);
    }

    public void deleteAsynchronously(final PsiElement psiElement) {
        invokeLater(new Runnable() {
            public void run() {
                delete(psiElement);
            }
        });
    }

    public void deleteAsynchronously(final PsiDirectory[] directories, final String question, final String title, final Runnable callback) {
        invokeLater(new Runnable() {
            public void run() {
                if (Messages.showYesNoDialog(project, question, title, Messages.getQuestionIcon()) == DialogWrapper.OK_EXIT_CODE) {
                    runCommand(project, new DeleteDirectoriesCommand(directories), title, "TestDox");
                }
                callback.run();
            }
        });
    }

    private String getTestPackage(TestDoxFile testDoxFile) {
        String sourcePackage = getPsiJavaFile(testDoxFile.file()).getPackageName();
        List<String> customPackages = config.getCustomPackages();
        PackageManager packageManager = new PackageManager(sourcePackage);

        List<String> packages = new ArrayList<String>();
        packages.add(sourcePackage);

        for (String customPackage : customPackages) {
            packages.add(packageManager.getPackage(customPackage));
        }

        ItemSelectionUI dialog = createItemSelectionDialog(asArray(packages));
        dialog.show();

        if (dialog.isOK()) {
            return (String) dialog.getSelectedItem();
        }
        if (dialog.wasCancelled()) {
            return null;
        }
        return sourcePackage;
    }

    private String getTestClassName(TestDoxFile testDoxFile) {
        String testClassName = testDoxFile.className();
        int lastDot = testClassName.lastIndexOf(".");

        if ((lastDot > 0) && (lastDot < testClassName.length() - 1)) {
            testClassName = testClassName.substring(lastDot + 1);
        }
        return nameResolver.getTestClassName(testClassName);
    }

    public TestMethod getCurrentTestMethod(PsiElement element, SentenceManager sentenceManager, VirtualFile currentFile) {
        element = getPsiMethodForOffsetFollowingTestMethodClosingBrace(element, currentFile);
        while (!isTestMethod(element)) {
            if (element == null) {
                return null;
            }
            element = element.getParent();
        }
        return new TestMethod((PsiMethod) element, this, sentenceManager);
    }

    public boolean isTestMethod(PsiElement element) {
        if (element instanceof PsiMethod) {
            PsiMethod psiMethod = (PsiMethod) element;
            if (config.isUsingAnnotations()) {
                return matchingAnnotation(psiMethod, config.getTestMethodAnnotation());
            }
            return psiMethod.getName().startsWith(config.getTestMethodPrefix());
        }
        return false;
    }

    private boolean matchingAnnotation(PsiMethod psiMethod, String name) {
        for (PsiAnnotation annotation : psiMethod.getModifierList().getAnnotations()) {
            if (name.equals(annotation.getText())) {
                return true;
            }
        }
        return false;
    }

    private PsiElement getPsiMethodForOffsetFollowingTestMethodClosingBrace(PsiElement element, VirtualFile currentFile) {
        PsiJavaFile psiJavaFile = getPsiJavaFile(currentFile);
        if (psiJavaFile == null) {
            return null;
        }

        if (element instanceof PsiWhiteSpace) {
            int currentOffset = getSelectedTextEditor().getCaretModel().getOffset();
            if (currentOffset > 0) {
                PsiElement previousElement = psiJavaFile.findElementAt(currentOffset - 1);
                if ((previousElement instanceof PsiJavaToken) && ("}".equals(previousElement.getText())) && (isTestMethod(element.getPrevSibling()))) {
                    return element.getPrevSibling();
                }
            }
        }
        return element;
    }

    private VirtualFile getTestDirectory(Module module) {
        List<VirtualFile> testFolders = new ArrayList<VirtualFile>();
        List<VirtualFile> sourceFolders = new ArrayList<VirtualFile>();

        ContentEntry[] entries = ModuleRootManager.getInstance(module).getContentEntries();
        collectTargetFolders(entries, testFolders, sourceFolders);
        Set excludedFolders = collectExcludedFolders(entries);
        sourceFolders.removeAll(excludedFolders);
        testFolders.removeAll(excludedFolders);
        orderBySuitability(testFolders);

        ItemSelectionUI dialog = createVirtualFileSelectionDialog(testFolders.toArray(VirtualFile.EMPTY_ARRAY));
        dialog.show();

        if (dialog.isOK()) {
            return (VirtualFile) dialog.getSelectedItem();
        }
        if (dialog.wasCancelled()) {
            return null;
        }

        orderBySuitability(sourceFolders);
        dialog = createVirtualFileSelectionDialog(asArray(sourceFolders));
        dialog.show();

        if (dialog.isOK()) {
            return (VirtualFile) dialog.getSelectedItem();
        }
        if (dialog.wasCancelled()) {
            return null;
        }
        showErrorMessage("No suitable location could be found for the test class", "Destination not found");
        return null;
    }

    void orderBySuitability(List<VirtualFile> testFolders) {
        Collections.sort(testFolders, new Comparator<VirtualFile>() {
            public int compare(VirtualFile file1, VirtualFile file2) {
                TestSuitabilityDescriptor descriptor1 = new TestSuitabilityDescriptor(file1.getPath());
                TestSuitabilityDescriptor descriptor2 = new TestSuitabilityDescriptor(file2.getPath());
                return descriptor1.compareTo(descriptor2);
            }
        });
    }

    private Set collectExcludedFolders(ContentEntry[] entries) {
        Set<VirtualFile> excluded = new HashSet<VirtualFile>();
        for (ContentEntry entry : entries) {
            excluded.addAll(asList(entry.getExcludeFolderFiles()));
        }
        return excluded;
    }

    private void collectTargetFolders(ContentEntry[] entries, List<VirtualFile> testFolders, List<VirtualFile> sourceFolders) {
        for (ContentEntry entry : entries) {
            for (SourceFolder folder : entry.getSourceFolders()) {
                VirtualFile file = folder.getFile();
                if (file != null && file.isDirectory() && file.isWritable()) {
                    if (folder.isTestSource()) {
                        testFolders.add(file);
                    } else {
                        sourceFolders.add(file);
                    }
                }
            }
        }
    }

    protected ItemSelectionUI createItemSelectionDialog(String[] packages) {
        return new ItemSelectionDialog(project, packages,
                "Please select the destination package for the test", "Select package", null);
    }

    protected ItemSelectionUI createVirtualFileSelectionDialog(VirtualFile[] items) {
        return new VirtualFileSelectionDialog(project, items);
    }

    public void addFileEditorManagerListener(FileEditorManagerListener listener) {
        getFileEditorManager().addFileEditorManagerListener(listener);
    }

    public void removeFileEditorManagerListener(FileEditorManagerListener listener) {
        getFileEditorManager().removeFileEditorManagerListener(listener);
    }

    public void addRefactoringElementListenerProvider(RefactoringElementListenerProvider listener) {
        getRefactoringListenerManager().addListenerProvider(listener);
    }

    public void removeRefactoringElementListenerProvider(RefactoringElementListenerProvider listener) {
        getRefactoringListenerManager().removeListenerProvider(listener);
    }

    public void addPsiTreeChangeListener(PsiTreeChangeListener listener) {
        getPsiManager().addPsiTreeChangeListener(listener);
    }

    public void removePsiTreeChangeListener(PsiTreeChangeListener listener) {
        getPsiManager().removePsiTreeChangeListener(listener);
    }

    public void addCommandListener(CommandListener listener) {
        getCommandProcessor().addCommandListener(listener);
    }

    public void removeCommandListener(CommandListener listener) {
        getCommandProcessor().removeCommandListener(listener);
    }

    public void addVirtualFileListener(VirtualFileListener listener) {
        getVirtualFileManager().addVirtualFileListener(listener);
    }

    public void removeVirtualFileListener(VirtualFileListener listener) {
        getVirtualFileManager().removeVirtualFileListener(listener);
    }

    public void commitAllDocuments() {
        getPsiDocumentManager().commitAllDocuments();
    }

    protected void runCommand(Project project, Runnable command, String name, String commandID) {
        getCommandProcessor().executeCommand(project, command, name, commandID);
    }

    protected abstract void invokeLater(Runnable task);

    protected void showErrorMessage(String message, String title) {
        Messages.showErrorDialog(project, message, title);
    }

    protected PsiManager getPsiManager() {
        return PsiManager.getInstance(project);
    }

    protected FileEditorManager getFileEditorManager() {
        return FileEditorManager.getInstance(project);
    }

    private PsiDocumentManager getPsiDocumentManager() {
        return PsiDocumentManager.getInstance(project);
    }

    private RefactoringListenerManager getRefactoringListenerManager() {
        return RefactoringListenerManager.getInstance(project);
    }

    private CommandProcessor getCommandProcessor() {
        return CommandProcessor.getInstance();
    }

    private VirtualFileManager getVirtualFileManager() {
        return VirtualFileManager.getInstance();
    }

    private class VirtualFileSelectionDialog extends ItemSelectionDialog {

        public VirtualFileSelectionDialog(Project project, VirtualFile[] items) {
            super(project, items, "Please select the destination folder for the test", "Select folder", new DefaultListCellRenderer() {
                public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                                                              boolean cellHasFocus) {
                    if (value instanceof VirtualFile) {
                        value = ((VirtualFile) value).getPath();
                    }
                    return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                }
            });
        }
    }

    private class CreateClassCommand implements Runnable {

        private final String className;
        private final String targetPackage;
        private final VirtualFile directory;

        public CreateClassCommand(String className, String targetPackage, VirtualFile directory) {
            this.className = className;
            this.targetPackage = targetPackage;
            this.directory = directory;
        }

        public void run() {
            ApplicationManager.getApplication().runWriteAction(new Runnable() {
                public void run() {
                    try {
                        PsiDirectory targetPsiDirectory = getPsiManager().findDirectory(directory);
                        targetPsiDirectory = createOrMoveToCorrectDirectory(targetPsiDirectory, targetPackage);
                        PsiClass createdClass = JavaDirectoryService.getInstance().createClass(targetPsiDirectory, className);
                        decorateClassWithTestTemplate(createdClass, getPsiManager());
                        jumpToPsiElement(createdClass);
                    } catch (IncorrectOperationException e) {
                        LOGGER.error(e);
                        showErrorMessage("Could not create test: " + e.getMessage(), "Warning");
                    }
                }
            });
        }
    }

    public PsiDirectory createOrMoveToCorrectDirectory(PsiDirectory startDirectory, String targetPackage) throws IncorrectOperationException {
        PsiDirectory currentDirectory = startDirectory;

        for (StringTokenizer tokenizer = new StringTokenizer(targetPackage, "."); tokenizer.hasMoreTokens();) {
            String packageElement = tokenizer.nextToken();
            PsiDirectory subdirectory = currentDirectory.findSubdirectory(packageElement);
            currentDirectory = (subdirectory != null) ? subdirectory
                    : currentDirectory.createSubdirectory(packageElement);
        }

        return currentDirectory;
    }

    public void decorateClassWithTestTemplate(PsiClass aClass, PsiManager psiManager) throws IncorrectOperationException {
        PsiElementFactory elementFactory = elementFactory();

        PsiReferenceList extendsList = aClass.getExtendsList();
        extendsList.add(elementFactory.createKeyword("extends"));
        extendsList.add(elementFactory.createReferenceElementByFQClassName("junit.framework.TestCase", GlobalSearchScope.allScope(project)));
    }

    private PsiElementFactory elementFactory() {
        return javaPsiFacade().getElementFactory();
    }

    private JavaPsiFacade javaPsiFacade() {
        return JavaPsiFacade.getInstance(project);
    }

    private class AddMethodCommand implements Runnable {

        private final PsiClass psiClass;
        private final String methodSignatureAndBody;

        public AddMethodCommand(PsiClass psiClass, String methodSignatureAndBody) {
            this.psiClass = psiClass;
            this.methodSignatureAndBody = methodSignatureAndBody;
        }

        public void run() {
            ApplicationManager.getApplication().runWriteAction(new Runnable() {
                public void run() {
                    try {
                        PsiManager psiManager = getPsiManager();
                        PsiMethod newMethod = elementFactory().createMethodFromText(methodSignatureAndBody, psiClass);

                        PsiMethod methodAnchor = findNearestMethodAnchorInSelectedFile();
                        if (methodAnchor != null) {
                            if (getSelectedTextEditor().getCaretModel().getOffset() < methodAnchor.getTextOffset()) {
                                methodAnchor.getParent().addBefore(newMethod, methodAnchor);
                            } else {
                                methodAnchor.getParent().addAfter(newMethod, methodAnchor);
                            }
                        } else {
                            psiClass.getNavigationElement().add(newMethod);
                        }

                        psiManager.getCodeStyleManager().reformat(newMethod);
                        newMethod = psiClass.findMethodBySignature(newMethod, false);
                        jumpToPsiElement(newMethod);

                        PsiWhiteSpace whiteSpace = PsiTreeUtil.getChildOfType(newMethod.getBody(), PsiWhiteSpace.class);
                        getSelectedTextEditor().getCaretModel().moveToOffset(whiteSpace.getTextRange().getStartOffset());
                    } catch (IncorrectOperationException e) {
                        LOGGER.error(e);
                        showErrorMessage("Could not add method: " + e.getMessage(), "Warning");
                    }
                }
            });
        }
    }

    public PsiMethod findNearestMethodAnchorInSelectedFile() {
        PsiFile psiFile = getPsiDocumentManager().getPsiFile(getSelectedTextEditor().getDocument());
        int offset = getSelectedTextEditor().getCaretModel().getOffset();
        PsiElement currentElement = psiFile.findElementAt(offset);
        if (currentElement == null) {
            return null;
        }

        PsiElement nearestMethod = PsiTreeUtil.getParentOfType(currentElement, PsiMethod.class);
        if (nearestMethod == null) {
            nearestMethod = PsiTreeUtil.getPrevSiblingOfType(currentElement, PsiMethod.class);
        }
        if (nearestMethod == null) {
            nearestMethod = PsiTreeUtil.getNextSiblingOfType(currentElement, PsiMethod.class);
        }
        return (PsiMethod) nearestMethod;
    }

    protected abstract MoveClassCommand createMoveClassCommand(PsiClass psiClass, PsiDirectory destinationPackage);

    protected abstract class MoveClassCommand implements Runnable {

        protected final PsiDirectory destinationPackage;
        protected PsiClass psiClass;

        public MoveClassCommand(PsiClass psiClass, PsiDirectory destinationPackage) {
            this.psiClass = psiClass;
            this.destinationPackage = destinationPackage;
        }

        public void run() {
            ApplicationManager.getApplication().runWriteAction(new Runnable() {
                public void run() {
                    move();
                }
            });
        }

        protected abstract void move();

        protected VirtualFile findSourceFolder() throws IncorrectOperationException {
            VirtualFile currentFile = psiClass.getContainingFile().getVirtualFile();
            Module module = ProjectRootManager.getInstance(project).getFileIndex().getModuleForFile(currentFile);
            for (ContentEntry entry : ModuleRootManager.getInstance(module).getContentEntries()) {
                for (VirtualFile folder : entry.getSourceFolderFiles()) {
                    if (currentFile.getPath().indexOf(folder.getPath()) > -1) {
                        return folder;
                    }
                }
            }
            throw new IncorrectOperationException("Could not locate current source folder!");
        }
    }

    protected class RenameCommand implements Runnable {

        private final PsiElement element;
        private final String name;

        public RenameCommand(PsiElement element, String name) {
            this.element = element;
            this.name = name;
        }

        public void run() {
            ApplicationManager.getApplication().runWriteAction(new Runnable() {
                public void run() {
                    RenameUtil.doRename(element, name, EMPTY_USAGE_INFO, project, REFACTORING_ELEMENT_ADAPTER);
                }
            });
        }
    }

    private class DeleteDirectoriesCommand implements Runnable {

        private final PsiDirectory[] directories;

        public DeleteDirectoriesCommand(PsiDirectory[] directories) {
            this.directories = directories;
        }

        public void run() {
            ApplicationManager.getApplication().runWriteAction(new Runnable() {
                public void run() {
                    try {
                        for (PsiDirectory directory : directories) {
                            if (directory.isValid()) {
                                directory.delete();
                            }
                        }
                    } catch (IncorrectOperationException e) {
                        LOGGER.error(e);
                        showErrorMessage("Could not delete directories: " + e.getMessage(), "Warning");
                    }
                }
            });
        }
    }

    private static class TestSuitabilityDescriptor {

        private static final String[] PATTERNS = array("|test|", "|tests|", "|test", "test|", "test");
        private String path;

        public TestSuitabilityDescriptor(String path) {
            this.path = path.toLowerCase();
            this.path = this.path.replace('\\', '|');
            this.path = this.path.replace('/', '|');
        }

        public int compareTo(TestSuitabilityDescriptor other) {
            for (String pattern : PATTERNS) {
                int result = compareProperty(other, path.indexOf(pattern) >= 0, other.path.indexOf(pattern) >= 0);
                if (result != 0) {
                    return result;
                }
            }
            return 0;
        }

        private int compareProperty(TestSuitabilityDescriptor other, boolean thisValue, boolean otherValue) {
            if (thisValue && otherValue) {
                return this.path.length() - other.path.length();
            }
            if (thisValue && !otherValue) {
                return -1;
            }
            if (otherValue) {
                return 1;
            }
            return 0;
        }
    }
}
