package org.codehaus.testdox.intellij.inspections

import org.specs.Specification

object InspectionProviderSpec extends Specification {

  "InspectionProvider" should {
    val inspectionProvider = new InspectionProvider()

    "define its component name" in {
      inspectionProvider.getComponentName must be equalTo "TestDoxInspectionProvider"
    }

    "do nothing when initialised by IntelliJ IDEA" in {
      inspectionProvider.initComponent().isExpectation
    }

    "do nothing when disposed by IntelliJ IDEA" in {
      inspectionProvider.disposeComponent().isExpectation
    }

    "return an array of all known inspection classes" in {
      inspectionProvider.getInspectionClasses().toSeq must be equalTo Seq(classOf[EmptyTestClassInspection], classOf[EmptyTestMethodInspection])
    }
  }
}
