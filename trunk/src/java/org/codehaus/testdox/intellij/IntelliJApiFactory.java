package org.codehaus.testdox.intellij;

import com.intellij.openapi.application.ApplicationInfo;
import org.picocontainer.MutablePicoContainer;

public class IntelliJApiFactory implements EditorApiFactory {

    private static final String DEMETRA  = "org.codehaus.testdox.intellij.demetra.DemetraApi";

    private final MutablePicoContainer picoContainer;

    private String ideaName;
    private String buildNumber;
    private Class editorApiClass;

    public IntelliJApiFactory(MutablePicoContainer picoContainer) {
        this.picoContainer = picoContainer;

        ApplicationInfo applicationInfo = ApplicationInfo.getInstance();
        ideaName = applicationInfo.getVersionName();
        buildNumber = applicationInfo.getBuildNumber();

        try {
            editorApiClass = Class.forName(getClassForIDEA(ideaName, Integer.parseInt(buildNumber)));
        } catch (NumberFormatException e) {
            throw newFactoryRuntimeException(ideaName, buildNumber, e);
        } catch (ClassNotFoundException e) {
            throw newFactoryRuntimeException(ideaName, buildNumber, e);
        }
    }

    public EditorApi createEditorApi() {
        picoContainer.registerComponentImplementation(EditorApi.class, editorApiClass);

        EditorApi editorApi = (EditorApi) picoContainer.getComponentInstance(EditorApi.class);
        if (editorApi == null) {
            throw newFactoryRuntimeException(ideaName, buildNumber);
        }

        return editorApi;
    }

    private String getClassForIDEA(String ideaName, int buildNumber) {
        if (ideaName.toLowerCase().matches(".*demetra.*") || buildNumber >= 5755) {
            return DEMETRA;
        }
        throw newFactoryRuntimeException(ideaName, String.valueOf(buildNumber));
    }

    private RuntimeException newFactoryRuntimeException(String ideaName, String buildNumber) {
        return new RuntimeException("Could not load API connector for " + ideaName + " build #" + buildNumber);
    }

    private RuntimeException newFactoryRuntimeException(String ideaName, String buildNumber, Exception e) {
        return new RuntimeException("Could not load API connector for " + ideaName + " build #" + buildNumber, e);
    }
}
