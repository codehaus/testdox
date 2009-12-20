package org.codehaus.testdox.intellij.config

trait ConfigurationUI {

  var customPackageMappings: List[String]

  var customMappingStatus: Boolean

  var testNameTemplate: String

  var createTestIfMissing: Boolean

  var useUnderscore: Boolean

  var showFullyQualifiedClassName: Boolean

  var autoApplyChangesToTests: Boolean

  var deletePackageOccurrences: Boolean

  var testMethodPrefix: String
}
