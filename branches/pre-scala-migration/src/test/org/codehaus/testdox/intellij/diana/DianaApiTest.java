package org.codehaus.testdox.intellij.diana;

import junitx.framework.StringAssert;
import org.codehaus.testdox.intellij.EditorApi;
import org.codehaus.testdox.intellij.IntelliJApiFactory;
import org.codehaus.testdox.intellij.IntelliJApiFactoryTest;

public class DianaApiTest extends IntelliJApiFactoryTest {

    public void testCreatesEditorApiForDianaIfVersionNameMatches() {
        mockApplicationInfo.expects(once()).method("getVersionName").will(returnValue("a string containing the name dIAnA..."));
        mockApplicationInfo.expects(once()).method("getBuildNumber").will(returnValue("-1"));

        EditorApi editorApi = new IntelliJApiFactory(picoContainer).createEditorApi();
        StringAssert.assertContains("DianaApi", editorApi.getClass().getName());
    }

    public void testCreatesEditorApiForDianaWhenBuildNumberIsGreaterThanLowerBound() {
        mockApplicationInfo.expects(once()).method("getVersionName").will(returnValue("don't care"));
        mockApplicationInfo.expects(once()).method("getBuildNumber").will(returnValue("8858"));

        EditorApi editorApi = new IntelliJApiFactory(picoContainer).createEditorApi();
        StringAssert.assertContains("DianaApi", editorApi.getClass().getName());
    }
}
