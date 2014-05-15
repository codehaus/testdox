package org.codehaus.testdox.intellij;

import junit.framework.TestCase;
import org.codehaus.testdox.intellij.config.ConfigurationBean;

public class SentenceManagerTest extends TestCase {

    private SentenceManager builder;

    protected void setUp() {
        ConfigurationBean config = new ConfigurationBean();
        config.setUnderscoreMode(true);
        config.setTestMethodPrefix("test");
        builder = new SentenceManager(config);
    }

    public void testReturnsBlankForNullString() throws Exception {
        assertEquals("", builder.buildSentence(null));
    }

    public void testReturnsBlankForEmptyString() throws Exception {
        assertEquals("", builder.buildSentence(""));
    }

    public void testReturnsWordForSingleWord() throws Exception {
        assertEquals("foo", builder.buildSentence("Foo"));
    }

    public void testLowercasesFirstWordOfSentence() throws Exception {
        assertEquals("foo", builder.buildSentence("Foo"));
    }

    public void testCreatesWordsWhereACaseChangeOccurs() throws Exception {
        assertEquals("foo bar", builder.buildSentence("fooBar"));
    }

    public void testCreatesNewWordOnAnUnderscore() throws Exception {
        assertEquals("foo bar", builder.buildSentence("foo_bar"));
    }

    public void testTrimsMultipleSpacesToASingleSpace() throws Exception {
        assertEquals("foo bar", builder.buildSentence("foo    Bar"));
    }

    public void testTrimsMethodNamePrefixFromSentenceIfPresent() throws Exception {
        assertEquals("foo", builder.buildSentence("testFoo"));
    }

    public void testReturnsBlankForMethodPrefix() throws Exception {
        assertEquals("", builder.buildSentence("test"));
    }

    public void testWorksForAnyMethodPrefix() throws Exception {
        String prefix = "slartibartfast";
        ConfigurationBean config = new ConfigurationBean();
        config.setUnderscoreMode(false);
        config.setTestMethodPrefix(prefix);
        builder = new SentenceManager(config);
        assertEquals("", builder.buildSentence(prefix));
        assertEquals("foo", builder.buildSentence(prefix + "Foo"));
    }

    public void testGroupsNumbersIntoSingleWords() throws Exception {
        assertEquals("has 1024 foos but only 56 bars", builder.buildSentence("has1024FoosButOnly56Bars"));
    }

    public void testCapitalisesAnyCharacterSequenceEnclosedInUnderscores() throws Exception {
        assertEquals("this should leave my TLA TLA as a TLA", builder.buildSentence("thisShouldLeaveMy_TLA__TLA_AsA_TLA_"));
    }

    public void testIgnoresUnderscoresIfNotInUnderscoreMode() throws Exception {
        ConfigurationBean config = new ConfigurationBean();
        config.setUnderscoreMode(false);
        builder = new SentenceManager(config);
        assertEquals("this should leave my t l a t l a as a t l a", builder.buildSentence("thisShouldLeaveMy_TLA__TLA_AsA_TLA_"));
    }

    public void testGeneratesDefaultTestNameForNull() throws Exception {
        assertEquals(SentenceManager.DEFAULT_TEST_METHOD_NAME, builder.buildMethodName(null));
    }

    public void testGeneratesDefaultTestNameForEmptyString() throws Exception {
        assertEquals(SentenceManager.DEFAULT_TEST_METHOD_NAME, builder.buildMethodName(""));
    }

    public void testGeneratesTestWordForSingleWord() throws Exception {
        assertEquals("testFoo", builder.buildMethodName("Foo"));
    }

    public void testGeneratesMethodNamesInCamelCase() throws Exception {
        assertEquals("testFoo", builder.buildMethodName("foo"));
    }

    public void testGeneratesMethodNamesByConcatenatingWordsInSentenceWithNoWhitespace() throws Exception {
        assertEquals("testFooBarBaz", builder.buildMethodName("foo bar baz"));
    }

    public void testKeepsNumbersAsASingleTokenInMethodNames() throws Exception {
        assertEquals("testFoo1245Baz", builder.buildMethodName("foo 1245 baz"));
    }

    public void testKeepsAcronymsInUpperCaseInMethodNames() throws Exception {
        assertEquals("testFoo_FKHKGHDS__IOC_Foo", builder.buildMethodName("foo FKHKGHDS IOC Foo"));
    }

    public void testIgnoresAcronymsIfNotInUnderscoreMode() throws Exception {
        ConfigurationBean config = new ConfigurationBean();
        config.setTestMethodPrefix("test");
        config.setUnderscoreMode(false);
        builder = new SentenceManager(config);
        assertEquals("testFooFKHKGHDSIOCFoo", builder.buildMethodName("foo FKHKGHDS IOC Foo"));
    }

    public void testDoesNotCapitaliseFirstLetterIfUsingAnnotations() throws Exception {
        ConfigurationBean config = new ConfigurationBean();
        config.setTestMethodAnnotation("@Foo");
        config.setUsingAnnotations(true);
        builder = new SentenceManager(config);
        assertEquals("fooFKHKGHDSIOCFoo", builder.buildMethodName("Foo FKHKGHDS IOC Foo"));
    }
}
