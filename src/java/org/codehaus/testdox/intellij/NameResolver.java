package org.codehaus.testdox.intellij;

public interface NameResolver {

    boolean isRealClass(String className);

    boolean isTestClass(String className);

    String getRealClassName(String className);

    String getTestClassName(String className);

    String getRealClassNameForDisplay(String className);
}
