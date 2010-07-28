package org.intellij.openapi.testing;

import com.intellij.openapi.application.ApplicationManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class MockApplicationManager extends ApplicationManager {

    private static final String APPLICATION_CLASS_NAME = "org.intellij.openapi.testing.diana.MockApplication";

    private static MockApplication applicationMock = createMockApplication();

    public static void clear() {
        ourApplication = null;
    }

    public static void reset() {
        stubInternalApplication();
    }

    public static MockApplication getMockApplication() {
        if (ourApplication == null) {
            stubInternalApplication();
        }
        return applicationMock;
    }

    private static void stubInternalApplication() {
        ourApplication = applicationMock;
    }

    private static MockApplication createMockApplication() {
        try {
            Class clazz = Class.forName(APPLICATION_CLASS_NAME);
            Constructor constructor = clazz.getConstructor();
            return (MockApplication) constructor.newInstance();
        } catch (NumberFormatException e) {
            throw newFactoryRuntimeException(e);
        } catch (InstantiationException e) {
            throw newFactoryRuntimeException(e);
        } catch (IllegalAccessException e) {
            throw newFactoryRuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw newFactoryRuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw newFactoryRuntimeException(e);
        } catch (InvocationTargetException e) {
            throw newFactoryRuntimeException(e);
        }
    }

    private static RuntimeException newFactoryRuntimeException(Exception e) {
        return new RuntimeException("Could not load MockApplication", e);
    }
}
