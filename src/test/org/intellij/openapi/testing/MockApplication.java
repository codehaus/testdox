package org.intellij.openapi.testing;

import com.intellij.openapi.application.Application;

public interface MockApplication extends Application {

    <T> void registerComponent(Class<T> componentClass, Object component);

    <T> void removeComponent(Class<T> componentClass);
}
