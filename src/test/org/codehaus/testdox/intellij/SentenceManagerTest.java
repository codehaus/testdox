package org.codehaus.testdox.intellij;

import junit.framework.TestCase;

import org.codehaus.testdox.intellij.config.Configuration;

public class SentenceManagerTest extends TestCase {

    public void testReturnsBlankForNullString() {
        assertEquals("", converter().buildSentence(null));
    }

    public void testReturnsBlankForEmptyString() {
        assertEquals("", converter().buildSentence(""));
    }

    public void testReturnsWordForSingleWord() {
        assertEquals("foo", converter().buildSentence("Foo"));
    }

    public void testLowercasesFirstWordOfSentence() {
        assertEquals("foo", converter().buildSentence("Foo"));
    }

    public void testCreatesWordsWhereACaseChangeOccurs() {
        assertEquals("foo bar", converter().buildSentence("fooBar"));
    }

    public void testCreatesNewWordOnAnUnderscore() {
        assertEquals("foo bar", converter().buildSentence("foo_bar"));
    }

    public void testTrimsMultipleSpacesToASingleSpace() {
        assertEquals("foo bar", converter().buildSentence("foo    Bar"));
    }

    public void testTrimsMethodNamePrefixFromSentenceIfPresent() {
        assertEquals("foo", converter().buildSentence("testFoo"));
    }

    public void testReturnsBlankForMethodPrefix() {
        assertEquals("", converter().buildSentence("test"));
    }

    public void testWorksForAnyMethodPrefix() {
        String prefix = "slartibartfast";

        Configuration configuration = new Configuration();
        configuration.setUnderscoreMode(false);
        configuration.setTestMethodPrefix(prefix);

        assertEquals("", converter(configuration).buildSentence(prefix));
        assertEquals("foo", converter(configuration).buildSentence(prefix + "Foo"));
    }

    public void testGroupsNumbersIntoSingleWords() {
        assertEquals("has 1024 foos but only 56 bars", converter().buildSentence("has1024FoosButOnly56Bars"));
    }

    public void testCapitalisesAnyCharacterSequenceEnclosedInUnderscores() {
        assertEquals("this should leave my TLA TLA as a TLA", converter().buildSentence("thisShouldLeaveMy_TLA__TLA_AsA_TLA_"));
    }

    public void testIgnoresUnderscoresIfNotInUnderscoreMode() {
        Configuration configuration = new Configuration();
        configuration.setUnderscoreMode(false);
        assertEquals("this should leave my TLA TLA as a TLA", converter(configuration).buildSentence("thisShouldLeaveMy_TLA__TLA_AsA_TLA_"));
    }

    public void testGeneratesDefaultTestNameForNull() {
        assertEquals(SentenceManager.DEFAULT_TEST_METHOD_NAME(), converter().buildMethodName(null));
    }

    public void testGeneratesDefaultTestNameForEmptyString() {
        assertEquals(SentenceManager.DEFAULT_TEST_METHOD_NAME(), converter().buildMethodName(""));
    }

    public void testGeneratesTestWordForSingleWord() {
        assertEquals("testFoo", converter().buildMethodName("Foo"));
    }

    public void testGeneratesMethodNamesInCamelCase() {
        assertEquals("testFoo", converter().buildMethodName("foo"));
    }

    public void testGeneratesMethodNamesByConcatenatingWordsInSentenceWithNoWhitespace() {
        assertEquals("testFooBarBaz", converter().buildMethodName("foo bar baz"));
    }

    public void testKeepsNumbersAsASingleTokenInMethodNames() {
        assertEquals("testFoo1245Baz", converter().buildMethodName("foo 1245 baz"));
    }

    public void testKeepsAcronymsInUpperCaseInMethodNames() {
        assertEquals("testFoo_FKHKGHDS__IOC_Foo", converter().buildMethodName("foo FKHKGHDS IOC Foo"));
    }

    public void testIgnoresAcronymsIfNotInUnderscoreMode() {
        Configuration configuration = new Configuration();
        configuration.setUnderscoreMode(false);
        assertEquals("testFooFKHKGHDSIOCFoo", converter(configuration).buildMethodName("foo FKHKGHDS IOC Foo"));
    }

    public void testDoesNotCapitaliseFirstLetterIfUsingAnnotations() {
        Configuration configuration = new Configuration();
        configuration.setTestMethodAnnotation("@Foo");
        configuration.setUsingAnnotations(true);
        assertEquals("fooFKHKGHDSIOCFoo", converter(configuration).buildMethodName("Foo FKHKGHDS IOC Foo"));
    }

    private static SentenceManager converter() {
        Configuration configuration = new Configuration();
        configuration.setUnderscoreMode(true);
        configuration.setTestMethodPrefix("test");
        return converter(configuration);
    }

    private static SentenceManager converter(Configuration configuration) {
        return new SentenceManager(configuration);
    }
}
