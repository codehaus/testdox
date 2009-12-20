package org.codehaus.testdox.intellij;

import junitx.framework.StringAssert;

import org.intellij.openapi.testing.MockApplicationManager;

import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.project.Project;

import org.codehaus.testdox.intellij.config.Configuration;
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

public class IntelliJApiFactoryTest extends MockObjectTestCase {

    protected final Mock mockApplicationInfo = mock(ApplicationInfo.class);

    protected final MutablePicoContainer picoContainer = new DefaultPicoContainer();

    protected void setUp() {
        picoContainer.registerComponentInstance(Project.class, mock(Project.class).proxy());
        picoContainer.registerComponentImplementation(Configuration.class);
        picoContainer.registerComponentImplementation(NameResolver.class, TemplateNameResolver.class);

        mockApplicationInfo.stubs().method("getMajorVersion");
        mockApplicationInfo.stubs().method("getMinorVersion");

        MockApplicationManager.getMockApplication().registerComponent(ApplicationInfo.class, mockApplicationInfo.proxy());
    }

    protected void tearDown() {
        MockApplicationManager.getMockApplication().removeComponent(ApplicationInfo.class);
    }

    public void testThrowsARuntimeExceptionWhenAttemptingToCreateEditorApiWithInvalidBuildNumber() {
        mockApplicationInfo.expects(once()).method("getVersionName").will(returnValue("IDEA version name"));
        mockApplicationInfo.expects(once()).method("getBuildNumber").will(returnValue("invalid build number"));
        try {
            new IntelliJApiFactory(picoContainer).createEditorApi();
            fail("A RuntimeException should have been thrown for this unsupported version of IntelliJ IDEA!");
        } catch (RuntimeException expected) {
            StringAssert.assertContains("Could not load API connector for", expected.getMessage());
        }
    }

    public void testThrowsARuntimeExceptionWhenAttemptingToCreateEditorApiUnderUnsupportedVersionOfIntellijIdea() {
        mockApplicationInfo.expects(once()).method("getVersionName").will(returnValue("a string that contains the name AriAdNa..."));
        mockApplicationInfo.expects(once()).method("getBuildNumber").will(returnValue("700"));
        try {
            new IntelliJApiFactory(picoContainer).createEditorApi();
            fail("A RuntimeException should have been thrown for this unsupported version of IntelliJ IDEA!");
        } catch (RuntimeException expected) {
            StringAssert.assertContains("Could not load API connector for", expected.getMessage());
        }
    }
}
