import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SuggestionBuilder {
    /**
     * The maximum amount of words which can be combined to a suggestion
     */
    private final static int MAX_COMBINED_TOKENS = 3;

    /**
     * Derives a list of suggestions from the given token stream. The given list
     * of tokens reflect a sorted list of tokens of a text. Each token reflects
     * either a single word or a punctuation mark like :.? A suggestion is
     * either a single word or a combination of following words (delimited by a
     * single space) and does not include any stopWord or a single character.
     * Combined word suggestions can maximal include MAX_COMBINED_TOKENS of
     * following words.
     * <p>
     * Example: Stop Words = {"is", "a", "can", "the"}
     * <p>
     * Token Stream = {"The", "beautiful", "girl", "from", "the", "farmers", "market", ".", "I", "like", "chewing", "gum", "." }
     * <p>
     * Suggestions:
     * "beautiful",
     * "beautiful girl",
     * "beautiful girl from",
     * "girl",
     * "girl from",
     * "from",
     * "farmers",
     * "farmers market",
     * "market",
     * "like",
     * "like chewing",
     * "like chewing gum",
     * "chewing",
     * "chewing gum",
     * "gum"
     */

    public static List<Suggestion> buildSuggestionsFromTokenStream(
            Iterator<String> tokens, Set<String> stopWords) {

        List<Suggestion> result = new LinkedList<>();
        List<String> window = new LinkedList<>();

        while (tokens.hasNext()) {

            String currentToken = tokens.next();

            if (ignoreToken(currentToken, stopWords)) {
                result.addAll(flushWindow(window));
                window.clear();
                continue;
            }

            if (window.size() < MAX_COMBINED_TOKENS) {
                window.add(currentToken);
            }

            if (window.size() == MAX_COMBINED_TOKENS) {
                result.addAll(suggestionsFromWindow(window));
                window.remove(0);
            }
        }

        result.addAll(flushWindow(window));
        return result;
    }

    private static boolean ignoreToken(String currentElem, Set<String> stopWords) {
        return currentElem.length() == 1 || stopWords.contains(currentElem) || stopWords.contains(currentElem.toLowerCase());
    }

    private static List<Suggestion> flushWindow(List<String> window) {
        return IntStream.range(0, window.size())
                .mapToObj(i -> window.subList(i, window.size()))
                .map(SuggestionBuilder::suggestionsFromWindow)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private static List<Suggestion> suggestionsFromWindow(List<String> window) {
        return IntStream
                .range(0, window.size())
                .mapToObj(i -> window.subList(0, i + 1).stream().collect(Collectors.joining(" ")))
                .map(Suggestion::new)
                .collect(Collectors.toList());
    }

    public static final class Suggestion {
        private final String text;

        public Suggestion(String text) {
            this.text = text;
        }

        public String toString() {
            return text;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Suggestion that = (Suggestion) o;

            return text != null ? text.equals(that.text) : that.text == null;
        }

        @Override
        public int hashCode() {
            return text != null ? text.hashCode() : 0;
        }
    }
}
