import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SuggestionBuilderReactor implements SuggestionBuilder {

    private static boolean ignoreToken(String currentElem, Set<String> stopWords) {
        return currentElem.length() == 1 || stopWords.contains(currentElem) || stopWords.contains(currentElem.toLowerCase());
    }

    private static List<Suggestion> suggestionsFromWindow(List<String> window) {
        return IntStream
                .range(0, window.size())
                .mapToObj(i -> window.subList(0, i + 1).stream().collect(Collectors.joining(" ")))
                .map(Suggestion::new)
                .collect(Collectors.toList());
    }


    public List<Suggestion> buildSuggestionsFromTokenStream(
            Iterator<String> tokens, Set<String> stopWords) {

        Flux<String> stringFlux = Flux.create((FluxSink<String> emitter) -> {
            while (tokens.hasNext()) {
                emitter.next(tokens.next());
            }
            emitter.complete();
        });

        Flux<Suggestion> suggestions = stringFlux.buffer(3, 1)
                .flatMapIterable(window -> suggestionsFromWindow(takeTillNonIngoredToken(stopWords, window)));

        return suggestions.collectList().block();
    }

    private static List<String> takeTillNonIngoredToken(Set<String> stopWords, List<String> window) {
        int indexOfFirstIngoredToken = IntStream
                .range(0, window.size())
                .filter(index -> ignoreToken(window.get(index), stopWords))
                .findFirst()
                .orElse(window.size());
        return window.subList(0, indexOfFirstIngoredToken);
    }
}

