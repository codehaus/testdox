package org.codehaus.testdox.intellij.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import static jedi.functional.Coercions.array;
import org.codehaus.testdox.intellij.NullPsiElement;
import org.codehaus.testdox.intellij.TestDoxController;
import org.codehaus.testdox.intellij.TestDoxNonJavaFile;
import org.codehaus.testdox.intellij.TestDoxProjectComponent;
import org.codehaus.testdox.intellij.panel.TestDoxToolWindowUI;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ActionEvents {

    static ActionEvents instance = new ActionEvents();

    public TestDoxController getTestDoxController(AnActionEvent event) {
        TestDoxProjectComponent testDoxProjectComponent = getTestDoxProjectComponent(event);
        return (testDoxProjectComponent != null) ? testDoxProjectComponent.getController()
            : Nulls.TESTDOX_CONTROLLER;
    }

    public TestDoxToolWindowUI getTestDoxToolWindowUI(AnActionEvent event) {
        TestDoxProjectComponent testDoxProjectComponent = getTestDoxProjectComponent(event);
        return (testDoxProjectComponent != null) ? testDoxProjectComponent.getTestDoxToolWindowUI()
            : Nulls.TESTDOX_TOOL_WINDOW;
    }

    private TestDoxProjectComponent getTestDoxProjectComponent(AnActionEvent event) {
        return TestDoxProjectComponent.getInstance(event.getData(DataKeys.PROJECT));
    }

    public boolean isJavaFile(AnActionEvent event) {
        TestDoxController testDoxController = getTestDoxController(event);
        VirtualFile file = event.getData(DataKeys.VIRTUAL_FILE);
        return (testDoxController != null) && (file != null) && (testDoxController.getEditorApi().isJavaFile(file));
    }

    public PsiElement getTargetPsiElement(AnActionEvent event) {
        Editor editor = event.getData(DataKeys.EDITOR);
        if (editor == null) {
            return NullPsiElement.INSTANCE();
        }

        PsiFile psiFile = event.getData(DataKeys.PSI_FILE);
        if (psiFile == null) {
            return NullPsiElement.INSTANCE();
        }

        return psiFile.findElementAt(editor.getCaretModel().getOffset());
    }

    static class Nulls {

        static final TestDoxToolWindowUI TESTDOX_TOOL_WINDOW;
        static final TestDoxController TESTDOX_CONTROLLER;

        private static final TestDoxNonJavaFile NON_JAVA_TEST_DOX_FILE = new TestDoxNonJavaFile(null);

        static {
            InvocationHandler invocationHandler = new InvocationHandler() {
                public Object invoke(Object proxy, Method method, Object[] args) {
                    if ("hasActiveEditors".equals(method.getName())) {
                        return false;
                    }
                    if ("canCurrentFileBeUnitTested".equals(method.getName())) {
                        return false;
                    }
                    if (("updatePresentation".equals(method.getName())) && (args[0] instanceof Presentation)) {
                        ((Presentation) args[0]).setEnabled(false);
                    }
                    if ("getCurrentTestDoxFile".equals(method.getName())) {
                        return NON_JAVA_TEST_DOX_FILE;
                    }
                    return null;
                }
            };

            ClassLoader classLoader = invocationHandler.getClass().getClassLoader();
            Class[] interfaces = array(TestDoxToolWindowUI.class, TestDoxController.class);

            Object nullObject = Proxy.newProxyInstance(classLoader, interfaces, invocationHandler);
            TESTDOX_TOOL_WINDOW = (TestDoxToolWindowUI) nullObject;
            TESTDOX_CONTROLLER = (TestDoxController) nullObject;
        }

        private Nulls() {
        }
    }
}
