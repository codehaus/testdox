package org.codehaus.testdox.intellij;

import java.util.Iterator;
import java.util.List;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;

import org.codehaus.testdox.intellij.config.Configuration;

public class TestDoxFileFactory {

    private final TestLookup testLookup;
    private final Configuration config;
    private final NameResolver nameResolver;

    public TestDoxFileFactory(TestLookup testLookup, Configuration config, NameResolver nameResolver) {
        this.testLookup = testLookup;
        this.config = config;
        this.nameResolver = nameResolver;
    }

    public TestDoxFile getTestDoxFile(VirtualFile file) {
        if (!testLookup.isJavaFile(file)) {
            return new TestDoxNonJavaFile(file);
        }

        String className = testLookup.getClassName(file);
        PsiClass psiClass = testLookup.getClass(file);
        TestClass testClass = createTestClass(className, psiClass, testLookup.editorApi());
        TestClass testedClass = null;

        if (psiClass == null) {
            return new TestDoxNonProjectClass(file, className, testClass, testedClass);
        }

        boolean isTestedClass;
        if (nameResolver.isRealClass(className)) {
            psiClass = findTestClass(nameResolver, className);
            isTestedClass = true;
        } else {
            className = nameResolver.getRealClassName(className);
            testedClass = createTestClass(className, testLookup.getClass(className), testLookup.editorApi());
            isTestedClass = false;
        }

        if (psiClass != null) {
            testClass = createTestClass(className, psiClass, testLookup.editorApi());
        }

        if ((testClass != null) && (testClass instanceof TestInterface)) {
            return new TestDoxInterface(file, className, testClass, testedClass);
        }

        return new TestDoxClass(file, className, isTestedClass, testClass, testedClass, testLookup.getTestMethods(psiClass));
    }

    private PsiClass findTestClass(NameResolver resolver, String className) {
        String testClassName = resolver.getTestClassName(className);
        PsiClass psiClass = testLookup.getClass(testClassName);
        if ((psiClass == null) && (config != null) && (config.getCustomPackagesAllowed())) {
            List packages = config.getCustomPackages();
            PackageManager packageManager = new PackageManager(getPackage(testClassName));
            testClassName = trimPackage(testClassName);
            for (Iterator iterator = packages.iterator(); iterator.hasNext();) {
                psiClass = testLookup.getClass(packageManager.getPackage((String) iterator.next()) + "." + testClassName);
                if (psiClass != null) {
                    return psiClass;
                }
            }
        }
        return psiClass;
    }

    private TestClass createTestClass(String className, PsiClass psiClass, EditorApi editorApi) {
        return (editorApi.isInterface(className))
                ? new TestInterface(className, psiClass, editorApi, nameResolver)
                : new TestClass(className, psiClass, editorApi, nameResolver);
    }

    private String getPackage(String testClassName) {
        int index = testClassName.lastIndexOf(".");
        return (index >= 0) ? testClassName.substring(0, index) : testClassName;
    }

    private String trimPackage(String testClassName) {
        return testClassName.substring(testClassName.lastIndexOf(".") + 1);
    }
}
