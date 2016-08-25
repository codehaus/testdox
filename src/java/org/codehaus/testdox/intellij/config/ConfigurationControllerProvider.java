package org.codehaus.testdox.intellij.config;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurableProvider;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

public class ConfigurationControllerProvider extends ConfigurableProvider {

    Project project;

    public ConfigurationControllerProvider(Project project) {
        this.project = project;
    }

    @Nullable
    @Override
    public Configurable createConfigurable() {
        return project.getComponent(ConfigurationController.class);
    }
}

