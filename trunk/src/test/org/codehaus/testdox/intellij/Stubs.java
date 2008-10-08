package org.codehaus.testdox.intellij;

import com.intellij.openapi.application.ApplicationInfo;
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;

public final class Stubs {

    public static ApplicationInfo createApplicationInfo(MockObjectTestCase testCase) {
        Mock stubApplicationInfo = testCase.mock(ApplicationInfo.class);
        stubApplicationInfo.stubs().method("getVersionName").will(testCase.returnValue("IntelliJ IDEA - Diana"));
        stubApplicationInfo.stubs().method("getBuildNumber").will(testCase.returnValue("8858"));
        stubApplicationInfo.stubs().method("getMajorVersion").will(testCase.returnValue("8"));
        stubApplicationInfo.stubs().method("getMinorVersion").will(testCase.returnValue("0"));

        return (ApplicationInfo) stubApplicationInfo.proxy();
    }
}
