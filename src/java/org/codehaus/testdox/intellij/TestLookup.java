package org.codehaus.testdox.intellij;

import java.util.ArrayList;
import java.util.List;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;

public class TestLookup {

    private final EditorApi editorApi;
    private final SentenceManager sentenceManager;

    public TestLookup(EditorApi editorApi, SentenceManager sentenceManager) {
        this.editorApi = editorApi;
        this.sentenceManager = sentenceManager;
    }

    public EditorApi getEditorApi() {
        return editorApi;
    }

    public boolean isJavaFile(VirtualFile file) {
        return editorApi.isJavaFile(file);
    }

    public PsiClass getClass(String testClassName) {
        return editorApi.getPsiClass(testClassName);
    }

    public String getClassName(VirtualFile file) {
        PsiJavaFile javaFile = editorApi.getPsiJavaFile(file);
        if (javaFile != null) {
            return javaFile.getPackageName() + "." + file.getNameWithoutExtension();
        }
        return null;
    }

    public PsiClass getClass(VirtualFile file) {
        PsiJavaFile javaFile = editorApi.getPsiJavaFile(file);
        if (javaFile != null) {
            String className = javaFile.getPackageName() + "." + file.getNameWithoutExtension();
            return editorApi.getPsiClass(className);
        }
        return null;
    }

    public TestMethod[] getTestMethods(PsiClass testClass) {
        if (testClass == null) {
            return TestMethod.EMPTY_ARRAY();
        }

        List<TestMethod> testMethods = new ArrayList<TestMethod>();
        for (PsiMethod method : editorApi.getMethods(testClass)) {
            if (editorApi.isTestMethod(method)) {
                testMethods.add(new TestMethod(method, editorApi, sentenceManager));
            }
        }
        return testMethods.toArray(TestMethod.EMPTY_ARRAY());
    }
}
