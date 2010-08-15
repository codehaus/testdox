package org.codehaus.testdox.intellij

import org.specs.Specification

import org.codehaus.testdox.intellij.config.Configuration

object TemplateNameResolverSpec extends Specification {

  private var configuration: Configuration = _
  private var nameResolver: TemplateNameResolver = _

  "TemplateNameResolver.getTestClassName" should {

    doBefore {configuration = new Configuration()}
    doAfter {configuration.removePropertyChangeListener(nameResolver)}

    "substitute name template for class name with no additions" in {
      val template = TemplateNameResolver.NAME_TOKEN
      val className = "MyClass"

      createTemplateNameResolver(template).getTestClassName(className) must be equalTo className
    }

    "give supplied class name for test class name if template matches" in {
      val template = TemplateNameResolver.NAME_TOKEN + "Test"
      val className = "MyClassTest"

      createTemplateNameResolver(template).getTestClassName(className) must be equalTo className
    }

    "give correct concatenated real class name for test class name" in {
      val template = "TestThis" + TemplateNameResolver.NAME_TOKEN + "Test"
      val className = "MyClass"

      createTemplateNameResolver(template).getTestClassName(className) must be equalTo "TestThis" + className + "Test"
    }

    "leave non-token suffix of template in output test class name" in {
      val template = TemplateNameResolver.NAME_TOKEN + "Test"
      val className = "MyClass"

      createTemplateNameResolver(template).getTestClassName(className) must be equalTo className + "Test"
    }

    "leave non-token prefix of template in output test class name" in {
      val template = "Test" + TemplateNameResolver.NAME_TOKEN
      val className = "MyClass"

      createTemplateNameResolver(template).getTestClassName(className) must be equalTo "Test" + className
    }

    "give correct test class name when real class is under package and template has prefix only" in {
      val template = "Test" + TemplateNameResolver.NAME_TOKEN
      val className = "com.acme.foo.NonexistentClass"
      val testClassName = "com.acme.foo.TestNonexistentClass"

      createTemplateNameResolver(template).getTestClassName(className) must be equalTo testClassName
    }
  }

  "TemplateNameResolver.getRealClassName" should {

    doBefore {configuration = new Configuration()}
    doAfter {configuration.removePropertyChangeListener(nameResolver)}

    "give supplied class name for test name if template does not match" in {
      val template = "Test" + TemplateNameResolver.NAME_TOKEN + "YouTestyTestThing"
      val className = "TestMyClass"

      createTemplateNameResolver(template).getRealClassName(className) must be equalTo className
    }

    "trim test prefix and suffix from test class name to get real class name" in {
      val template = "Test" + TemplateNameResolver.NAME_TOKEN + "YouTestyTestThing"
      val className = "TestMyClassYouTestyTestThing"

      createTemplateNameResolver(template).getRealClassName(className) must be equalTo "MyClass"
    }

    "give real class name when test class is under package and template has prefix only" in {
      val template = "Test" + TemplateNameResolver.NAME_TOKEN
      val className = "com.acme.foo.NonexistentClass"
      val testClassName = "com.acme.foo.TestNonexistentClass"

      createTemplateNameResolver(template).getRealClassName(testClassName) must be equalTo className
    }
  }

  "TemplateNameResolver.getRealClassNameForDisplay" should {

    doBefore {configuration = new Configuration()}
    doAfter {configuration.removePropertyChangeListener(nameResolver)}

    "give fully qualified real class name for display when configured and test class is under package and template has prefix only" in {
      val template = "Test" + TemplateNameResolver.NAME_TOKEN
      val className = "com.acme.foo.NonexistentClass"
      val testClassName = "com.acme.foo.TestNonexistentClass"

      configuration.setShowFullyQualifiedClassName(true)
      createTemplateNameResolver(template).getRealClassNameForDisplay(testClassName) must be equalTo className
    }

    "give real class name without package prefix for display when TestDox is configured to do so and test class is under package and template has prefix only" in {
      val template = "Test" + TemplateNameResolver.NAME_TOKEN
      val testClassName = "com.acme.foo.TestNonexistentClass"

      configuration.setShowFullyQualifiedClassName(false)
      createTemplateNameResolver(template).getRealClassNameForDisplay(testClassName) must be equalTo "NonexistentClass"
    }
  }

  "TemplateNameResolver.isRealClass" should {

    doBefore {configuration = new Configuration()}
    doAfter {configuration.removePropertyChangeListener(nameResolver)}

    "detect real class name for name that does not match template" in {
      val template = TemplateNameResolver.NAME_TOKEN + "Test"
      val className = "MyClass"

      createTemplateNameResolver(template).isRealClass(className) must be(true)
    }

    "not detect real class name for name that matches template" in {
      val template = TemplateNameResolver.NAME_TOKEN + "Test"
      val className = "MyClassTest"

      createTemplateNameResolver(template).isRealClass(className) must be(false)
    }

    "not allow null as a real class" in {
      createTemplateNameResolver(TemplateNameResolver.NAME_TOKEN + "Test").isRealClass(null) must be(false)
    }

    "not allow null as a test class" in {
      createTemplateNameResolver(TemplateNameResolver.NAME_TOKEN + "Test").isTestClass(null) must be(false)
    }
  }

  "TemplateNameResolver.isTestClass" should {

    doBefore {configuration = new Configuration()}
    doAfter {configuration.removePropertyChangeListener(nameResolver)}

    "detect test class name for name that matches template" in {
      val template = "A" + TemplateNameResolver.NAME_TOKEN + "Test"
      val className = "AMyClassTest"

      createTemplateNameResolver(template).isTestClass(className) must be(true)
    }

    "not detect test class name for name that does not match template" in {
      val template = "A" + TemplateNameResolver.NAME_TOKEN + "Test"
      val className = "MyClassTes"

      createTemplateNameResolver(template).isTestClass(className) must be(false)
    }

    "listen to bean property changes and updates when a change occurs" in {
      val template = TemplateNameResolver.NAME_TOKEN + "Test"
      val testName = "MyClassTest"
      val otherTestName = "MyClassTestCase"

      createTemplateNameResolver(template)
      nameResolver.isTestClass(testName) must be(true)
      nameResolver.isTestClass(otherTestName) must be(false)

      configuration.setTestNameTemplate(TemplateNameResolver.NAME_TOKEN + "TestCase")
      nameResolver.isTestClass(testName) must be(false)
      nameResolver.isTestClass(otherTestName) must be(true)
    }
  }

  private def createTemplateNameResolver(template: String) = {
    configuration.setTestNameTemplate(template)
    nameResolver = new TemplateNameResolver(configuration)
    nameResolver
  }
}
