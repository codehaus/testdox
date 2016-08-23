package org.codehaus.testdox.intellij;

import com.intellij.openapi.application.ApplicationInfo;
import org.picocontainer.MutablePicoContainer;

public class IntelliJApiFactory implements EditorApiFactory {

    private static final String MAIA_API_CLASS_NAME = "org.codehaus.testdox.intellij.maia.MaiaApi";

    private final MutablePicoContainer picoContainer;

    private Class editorApiClass;

    ApplicationInfo applicationInfo;

    public IntelliJApiFactory(MutablePicoContainer picoContainer) {
        this.picoContainer = picoContainer;
        applicationInfo = ApplicationInfo.getInstance();

        try {
            editorApiClass = Class.forName(MAIA_API_CLASS_NAME);
        } catch (ClassNotFoundException e) {
            throw newFactoryRuntimeException(getIdeaName(), getBuildNumber(), e);
        }
    }

    public EditorApi createEditorApi() {
        picoContainer.registerComponentImplementation(EditorApi.class, editorApiClass);

        EditorApi editorApi = (EditorApi) picoContainer.getComponentInstance(EditorApi.class);
        if (editorApi == null) {
            throw newFactoryRuntimeException(getIdeaName(), getBuildNumber());
        }

        return editorApi;
    }

    private RuntimeException newFactoryRuntimeException(String ideaName, String buildNumber) {
        return new RuntimeException("Could not load API connector for " + ideaName + " build #" + buildNumber);
    }

    private RuntimeException newFactoryRuntimeException(String ideaName, String buildNumber, Exception e) {
        return new RuntimeException("Could not load API connector for " + ideaName + " build #" + buildNumber, e);
    }

    public String getIdeaName() {
        return applicationInfo.getVersionName();
    }

    public String getBuildNumber() {
        return applicationInfo.getBuild().asString();
    }
}
