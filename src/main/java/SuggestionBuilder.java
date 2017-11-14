import java.util.Iterator;
import java.util.List;
import java.util.Set;

public interface SuggestionBuilder {

    /**
     * The maximum amount of words which can be combined to a suggestion
     */
    int MAX_COMBINED_TOKENS = 3;


    final class Suggestion {
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

    List<SuggestionBuilderOld.Suggestion> buildSuggestionsFromTokenStream(
            Iterator<String> tokens, Set<String> stopWords);
}
