package edu.comp331;

import java.util.Objects;

public final class TestUtilsOld {

    private TestUtilsOld() {
    }

    /**
     * Counts whitespace-separated words in {@code text}.
     *
     * @param text the text to count words in
     * @return the number of words, or zero if the text is blank
     * @throws NullPointerException if {@code text} is null
     */
    public static int countWords(String text) {
        Objects.requireNonNull(text, "text must not be null");
        String trimmedText = text.strip();
        return trimmedText.isEmpty() ? 0 : trimmedText.split("(?U)\\s+").length;
    }
}
