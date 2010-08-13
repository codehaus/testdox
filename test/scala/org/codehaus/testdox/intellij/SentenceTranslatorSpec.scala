package org.codehaus.testdox.intellij

import org.codehaus.testdox.intellij.SentenceTranslator.DEFAULT_TEST_METHOD_NAME
import org.codehaus.testdox.intellij.config.Configuration

import org.specs.Specification

object SentenceTranslatorSpec extends Specification {

  "SentenceTranslator.buildSentence must" >> {

    "return an empty string when given a null string" in {
      translator().buildSentence(null) must be equalTo ""
    }

    "return an empty string when given an empty string" in {
      translator().buildSentence("") must be equalTo ""
    }

    "return a word in lowercase when given a capitalised word" in {
      translator().buildSentence("Foo") must be equalTo "foo"
    }

    "lowercase the first words of a given sentence" in {
      translator().buildSentence("This Is A Sentence") must be equalTo "this is a sentence"
    }

    "create a word where a case change occurs" in {
      translator().buildSentence("fooBar") must be equalTo "foo bar"
    }

    "create a word when encountering an underscore" in {
      translator().buildSentence("foo_bar") must be equalTo "foo bar"
    }

    "reduces multiple spaces into a single space" in {
      translator().buildSentence("Foo   Bar") must be equalTo "foo bar"
    }

    "removes the method name prefix if present" in {
      translator().buildSentence("testFooBar") must be equalTo "foo bar"
    }

    "return an empty string when given the method name prefix only" in {
      translator().buildSentence("test") must be equalTo ""
    }

    "work for any method prefix" in {
      val prefix = "slartibartfast"

      val configuration = new Configuration()
      configuration.setUnderscoreMode(false)
      configuration.setTestMethodPrefix(prefix)

      translator(configuration).buildSentence(prefix) must be equalTo ""
      translator(configuration).buildSentence(prefix + "Foo") must be equalTo "foo"
    }

    "group numbers into single words" in {
      translator().buildSentence("has1024FoosButOnly56Bars") must be equalTo "has 1024 foos but only 56 bars"
    }

    "capitalise any character sequence enclosed in underscores" in {
      translator().buildSentence("thisShouldLeaveMy_TLA__TLA_AsA_TLA_") must be equalTo "this should leave my TLA TLA as a TLA"
    }

    "ignore underscores if not in underscore mode" in {
      val configuration = new Configuration()
      configuration.setUnderscoreMode(false)

      translator(configuration).buildSentence("thisShouldLeaveMy_TLA__TLA_AsA_TLA_") must be equalTo "this should leave my TLA TLA as a TLA"
    }
  }

  "SentenceTranslator.buildMethodName must" >> {

    "return the default test method name when given null" in {
      translator().buildMethodName(null) must be equalTo DEFAULT_TEST_METHOD_NAME
    }

    "return the default test method name when given an empty string" in {
      translator().buildMethodName("") must be equalTo DEFAULT_TEST_METHOD_NAME
    }

    "concatenate uppercased words into a sentence containing no whitespaces and prefixed with the test method name prefix" in {
      translator().buildMethodName("foo bar baz") must be equalTo "testFooBarBaz"
    }

    "keep numbers as a single token in method names" in {
      translator().buildMethodName("foo 1245 baz") must be equalTo "testFoo1245Baz"
    }

    "keep acronyms in upper case in method names" in {
      translator().buildMethodName("foo FKHKGHDS IOC Foo") must be equalTo "testFoo_FKHKGHDS__IOC_Foo"
    }

    "ignore acronyms when not in underscore mode" in {
      val configuration = new Configuration()
      configuration.setUnderscoreMode(false)

      translator(configuration).buildMethodName("foo FKHKGHDS IOC Foo") must be equalTo "testFooFKHKGHDSIOCFoo"
    }

    "not capitalise the first letter when using annotations" in {
      val configuration = new Configuration()
      configuration.setTestMethodAnnotation("@Foo")
      configuration.setUsingAnnotations(true)

      translator(configuration).buildMethodName("Foo FKHKGHDS IOC Foo") must be equalTo "fooFKHKGHDSIOCFoo"
    }
  }

  private def translator(): SentenceTranslator = {
    val configuration = new Configuration()
    configuration.setUnderscoreMode(true)
    configuration.setTestMethodPrefix("test")
    translator(configuration)
  }

  private def translator(configuration: Configuration): SentenceTranslator = {
    new SentenceTranslator(configuration)
  }
}
