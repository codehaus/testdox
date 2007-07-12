package org.codehaus.testdox.intellij.demetra;

import junitx.framework.StringAssert;
import org.codehaus.testdox.intellij.EditorApi;
import org.codehaus.testdox.intellij.IntelliJApiFactory;
import org.codehaus.testdox.intellij.IntelliJApiFactoryTest;

public class DemetraApiTest extends IntelliJApiFactoryTest {

    public void testCreatesEditorApiForDemetraIfVersionNameMatches() {
        mockApplicationInfo.expects(once()).method("getVersionName").will(returnValue("a string containing the name dEMeTrA..."));
        mockApplicationInfo.expects(once()).method("getBuildNumber").will(returnValue("-1"));

        EditorApi editorApi = new IntelliJApiFactory(picoContainer).createEditorApi();
        StringAssert.assertContains("DemetraApi", editorApi.getClass().getName());
    }

    public void testCreatesEditorApiForDemetraWhenBuildNumberIsGreaterThanLowerBound() {
        mockApplicationInfo.expects(once()).method("getVersionName").will(returnValue("don't care"));
        mockApplicationInfo.expects(once()).method("getBuildNumber").will(returnValue("5780"));

        EditorApi editorApi = new IntelliJApiFactory(picoContainer).createEditorApi();
        StringAssert.assertContains("DemetraApi", editorApi.getClass().getName());
    }
}
