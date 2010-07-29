package org.codehaus.testdox.intellij.maia;

import com.intellij.openapi.util.BuildNumber;
import junitx.framework.StringAssert;
import org.codehaus.testdox.intellij.EditorApi;
import org.codehaus.testdox.intellij.IntelliJApiFactory;
import org.codehaus.testdox.intellij.IntelliJApiFactoryTest;

public class MaiaApiTest extends IntelliJApiFactoryTest {

    public void testCreatesEditorApiForMaiaIfVersionNameMatches() {
        mockApplicationInfo.expects(once()).method("getVersionName").will(returnValue("a string containing the name mAiA..."));
        mockApplicationInfo.expects(once()).method("getBuild").will(returnValue(new BuildNumber("IC", 95, 429)));

        EditorApi editorApi = new IntelliJApiFactory(picoContainer).createEditorApi();
        StringAssert.assertContains("MaiaApi", editorApi.getClass().getName());
    }

    public void testCreatesEditorApiForMaiaWhenBuildNumberIsGreaterThanLowerBound() {
        mockApplicationInfo.expects(once()).method("getVersionName").will(returnValue("don't care"));
        mockApplicationInfo.expects(once()).method("getBuild").will(returnValue(new BuildNumber("IC", 97, 9999)));

        EditorApi editorApi = new IntelliJApiFactory(picoContainer).createEditorApi();
        StringAssert.assertContains("MaiaApi", editorApi.getClass().getName());
    }
}
