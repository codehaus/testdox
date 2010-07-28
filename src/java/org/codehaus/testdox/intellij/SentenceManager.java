package org.codehaus.testdox.intellij;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

import org.codehaus.testdox.intellij.config.ConfigurationBean;

public class SentenceManager {

    public static final String DEFAULT_TEST_METHOD_NAME = "testNoInformationKnownAboutThisTest";
    public static final String ACRONYM_FINDER = "[A-Z]{2,}";

    private final ConfigurationBean config;

    public SentenceManager(ConfigurationBean config) {
        this.config = config;
    }

    public String buildSentence(String text) {
        if (text == null) {
            return "";
        }
        if (config.getTestMethodPrefix() != null && text.startsWith(config.getTestMethodPrefix())) {
            text = text.substring(config.getTestMethodPrefix().length());
        }
        if (text.length() == 0) {
            return "";
        }
        boolean first = true;
        boolean caps = false;
        int index = 0;
        StringBuffer buffer = new StringBuffer();
        StringCharacterIterator iterator = new StringCharacterIterator(text);
        for (char c = iterator.first(); c != CharacterIterator.DONE; c = iterator.next()) {
            if (isWordBreak(c) && !first) {
                char lastChar = buffer.charAt(index - 1);
                if (lastChar != ' ' && !inNumber(lastChar, c)) {
                    if (!caps) {
                        buffer.append(' ');
                        index++;
                    }
                }
                if (!shouldMask(c)) {
                    if (caps) {
                        buffer.append(Character.toUpperCase(c));
                    } else {
                        buffer.append(Character.toLowerCase(c));
                    }
                    index++;
                } else if (config.isUnderscoreMode()) {
                    caps = !caps;
                }
            } else {
                buffer.append(c);
                index++;
            }
            first = false;
        }

        char firstChar = Character.toLowerCase(buffer.charAt(0));
        buffer.deleteCharAt(0);
        buffer.insert(0, firstChar);
        return buffer.toString().trim().replaceAll("\\s+", " ");
    }

    public String buildMethodName(String sentence) {
        if (sentence == null || sentence.length() == 0) {
            return DEFAULT_TEST_METHOD_NAME;
        }

        boolean capitalise = true;
        if (config.isUnderscoreMode()) {
            sentence = underscorify(sentence);
        }
        StringBuffer buf = new StringBuffer();
        if (!config.isUsingAnnotations()) {
            buf.append(config.getTestMethodPrefix());
        }
        StringCharacterIterator iter = new StringCharacterIterator(sentence);
        for (char c = iter.first(); c != CharacterIterator.DONE; c = iter.next()) {
            if (Character.isWhitespace(c)) {
                capitalise = true;
            } else if (capitalise) {
                buf.append(Character.toUpperCase(c));
                capitalise = false;
            } else {
                buf.append(c);
            }
        }

        String name = buf.toString();
        if (config.isUsingAnnotations()) {
            name = name.substring(0, 1).toLowerCase() + name.substring(1);
        }
        return name;
    }

    private String underscorify(String sentence) {
        StringBuffer buf = new StringBuffer();
        String[] bits = sentence.split("\\s+");
        for (int i = 0; i < bits.length; i++) {
            if (bits[i].matches(ACRONYM_FINDER)) {
                buf.append("_");
                buf.append(bits[i].toUpperCase());
                buf.append("_");
            } else {
                buf.append(bits[i]);
            }
            buf.append(" ");
        }
        return buf.toString();
    }

    private boolean inNumber(char lastChar, char currentChar) {
        return Character.isDigit(lastChar) && Character.isDigit(currentChar);
    }

    private boolean shouldMask(char c) {
        return c == '_';
    }

    private boolean isWordBreak(char c) {
        return Character.isUpperCase(c) || c == '_' || Character.isDigit(c);
    }
}
