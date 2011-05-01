package org.codehaus.testdox.intellij;

import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.util.BuildNumber;
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;

public final class Stubs {

    public static ApplicationInfo createApplicationInfo(MockObjectTestCase testCase) {
        Mock stubApplicationInfo = testCase.mock(ApplicationInfo.class);
        stubApplicationInfo.stubs().method("getVersionName").will(testCase.returnValue("IntelliJ IDEA (Maia)"));
        stubApplicationInfo.stubs().method("getBuild").will(testCase.returnValue(new BuildNumber("IC", 93, 115)));
        stubApplicationInfo.stubs().method("getMajorVersion").will(testCase.returnValue("9"));
        stubApplicationInfo.stubs().method("getMinorVersion").will(testCase.returnValue("0"));

        return (ApplicationInfo) stubApplicationInfo.proxy();
    }
}
