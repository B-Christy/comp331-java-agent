package edu.comp331;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TextUtils {

    private static final Pattern WORD_PATTERN = Pattern.compile("(?U)\\S+");

    private TextUtils() {
    }

    /**
     * Counts non-empty words separated by whitespace.
     *
     * @param text the text to count words in
     * @return the number of words, or zero for null or blank text
     */
    public static int countWords(String text) {
        if (text == null) {
            return 0;
        }

        Matcher matcher = WORD_PATTERN.matcher(text);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }
}
