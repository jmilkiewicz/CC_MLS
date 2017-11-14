import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SuggestionBuilderOld implements SuggestionBuilder {

    private static boolean ignoreToken(String currentElem, Set<String> stopWords) {
        return currentElem.length() == 1 || stopWords.contains(currentElem) || stopWords.contains(currentElem.toLowerCase());
    }

    private static List<Suggestion> flushWindow(List<String> window) {
        return IntStream.range(0, window.size())
                .mapToObj(i -> window.subList(i, window.size()))
                .map(SuggestionBuilderOld::suggestionsFromWindow)
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

    public List<SuggestionBuilder.Suggestion> buildSuggestionsFromTokenStream(
            Iterator<String> tokens, Set<String> stopWords) {

        List<SuggestionBuilderOld.Suggestion> result = new LinkedList<>();
        List<String> window = new LinkedList<>();

        while (tokens.hasNext()) {

            String currentToken = tokens.next();

            if (SuggestionBuilderOld.ignoreToken(currentToken, stopWords)) {
                result.addAll(flushWindow(window));
                window.clear();
                continue;
            }

            if (window.size() < SuggestionBuilder.MAX_COMBINED_TOKENS) {
                window.add(currentToken);
            }

            if (window.size() == SuggestionBuilder.MAX_COMBINED_TOKENS) {
                result.addAll(suggestionsFromWindow(window));
                window.remove(0);
            }
        }

        result.addAll(flushWindow(window));
        return result;
    }


}

