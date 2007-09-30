package org.codehaus.testdox.intellij.config;

import java.util.List;

public interface ConfigurationUI {

    void setCustomPackageMappings(List<String> mappings);

    List<String> getCustomPackageMappings();

    void setCustomMappingStatus(boolean active);

    boolean getCustomMappingStatus();

    void setTestNameTemplate(String template);

    String getTestNameTemplate();

    void setCreateTestIfMissing(boolean createTestIfMissing);

    boolean getCreateTestIfMissing();

    void setUseUnderscore(boolean userUnderscore);

    boolean getUseUnderscore();

    void setShowFullyQualifiedClassName(boolean showFullyQualifiedClassName);

    boolean getShowFullyQualifiedClassName();

    void setAutoApplyChangesToTest(boolean autoApplyChangesToTest);

    boolean getAutoApplyChangesToTest();

    void setDeletePackageOccurrences(boolean deletePackageOccurrences);

    boolean getDeletePackageOccurrences();

    void setTestMethodPrefix(String prefix);

    String getTestMethodPrefix();
}
