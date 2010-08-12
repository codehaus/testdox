package org.codehaus.testdox.intellij

import org.specs.Specification
import org.codehaus.testdox.intellij.PackageResolver._

object PackageResolverSpec extends Specification {
  private val packageResolver = new PackageResolver("com.acme.foo.bar")

  "A PackageResolver must" >> {
    "return an empty package given a null template" in {
      packageResolver.getPackage(null) must be equalTo ""
    }

    "return an empty package given an empty template" in {
      packageResolver.getPackage("") must be equalTo ""
    }

    "return an unmodified package given a template that is just the package keyword" in {
      packageResolver.getPackage(PACKAGE_TOKEN) must be equalTo "com.acme.foo.bar"
    }

    "pop one package level up for one pop template" in {
      packageResolver.getPackage(PACKAGE_TOKEN + POP_TOKEN) must be equalTo "com.acme.foo"
    }

    "pop one package level up for each pop template found" in {
      packageResolver.getPackage(PACKAGE_TOKEN + POP_TOKEN + POP_TOKEN) must be equalTo "com.acme"
    }

    "ignore the package token if the source package itself is empty" in {
      new PackageResolver("").getPackage(PACKAGE_TOKEN + POP_TOKEN + POP_TOKEN) must be equalTo ""
    }

    "return an empty package if there are more pop templates than package levels" in {
      packageResolver.getPackage(PACKAGE_TOKEN + POP_TOKEN + POP_TOKEN + POP_TOKEN + POP_TOKEN + POP_TOKEN) must be equalTo ""
    }

    "append additional packages to the resolved source package" in {
      packageResolver.getPackage(PACKAGE_TOKEN + POP_TOKEN + "/test") must be equalTo "com.acme.foo.test"
    }

    "append additional packages after multiple pops" in {
      packageResolver.getPackage(PACKAGE_TOKEN + POP_TOKEN + POP_TOKEN + "/test") must be equalTo "com.acme.test"
    }
  }
}
