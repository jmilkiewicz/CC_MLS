import org.hamcrest.collection.IsEmptyCollection;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class SuggestionBuilderTest {

    @Test
    public void suggestionWhenEachSlotSmallerThanMAX_COMBINED_TOKENS() throws Exception {
        List<String> input = Arrays.asList(new String[]{"The", "beautiful", "girl", "from", "the", "farmers", "market", ".", "I", "like", "chewing", "gum", "."});
        HashSet<String> stopWords = new HashSet(Arrays.asList(new String[]{"is", "a", "can", "the"}));


        List<SuggestionBuilder.Suggestion> suggestions = SuggestionBuilder.buildSuggestionsFromTokenStream(input.iterator(), stopWords);

        List<SuggestionBuilder.Suggestion> expected = asListOfSuggestions(new String[]{
                "beautiful",
                "beautiful girl",
                "beautiful girl from",
                "girl",
                "girl from",
                "from",
                "farmers",
                "farmers market",
                "market",
                "like",
                "like chewing",
                "like chewing gum",
                "chewing",
                "chewing gum",
                "gum"});


        Assert.assertThat(suggestions,
                IsIterableContainingInOrder.contains(expected.toArray()));
    }


    @Test
    public void suggestionWhenASlotGreaterThanMAX_COMBINED_TOKENS() throws Exception {
        List<String> input = Arrays.asList(new String[]{"The", "beautiful", "girl", "from", "forest"});

        HashSet<String> stopWords = new HashSet(Arrays.asList(new String[]{"is", "a", "can", "the"}));

        List<SuggestionBuilder.Suggestion> suggestions = SuggestionBuilder.buildSuggestionsFromTokenStream(input.iterator(), stopWords);

        List<SuggestionBuilder.Suggestion> expected = asListOfSuggestions(new String[]{
                "beautiful",
                "beautiful girl",
                "beautiful girl from",
                "girl",
                "girl from",
                "girl from forest",
                "from",
                "from forest",
                "forest"
        });


        Assert.assertThat(suggestions,
                IsIterableContainingInOrder.contains(expected.toArray()));

    }

    private List<SuggestionBuilder.Suggestion> asListOfSuggestions(String[] expectdPhrases) {
        return Arrays.stream(expectdPhrases)
                .map(SuggestionBuilder.Suggestion::new)
                .collect(Collectors.toList());
    }


    @Test
    public void suggestionOnlyIgnoredTokens() throws Exception {
        List<String> input = Arrays.asList(new String[]{"a", ".", "abc", "de", "e"});

        HashSet<String> stopWords = new HashSet(Arrays.asList(new String[]{"abc", "de"}));

        List<SuggestionBuilder.Suggestion> suggestions = SuggestionBuilder.buildSuggestionsFromTokenStream(input.iterator(), stopWords);

        Assert.assertThat(suggestions, IsEmptyCollection.empty());
    }

    @Test
    public void suggestion2SubsequentIgnoredTokens() throws Exception {
        List<String> input = Arrays.asList(new String[]{"abc", "def", "the", ".", "xxx"});

        HashSet<String> stopWords = new HashSet(Arrays.asList(new String[]{"the"}));

        List<SuggestionBuilder.Suggestion> suggestions = SuggestionBuilder.buildSuggestionsFromTokenStream(input.iterator(), stopWords);

        List<SuggestionBuilder.Suggestion> expected = asListOfSuggestions(new String[]{
                "abc",
                "abc def",
                "def",
                "xxx"
        });

        Assert.assertThat(suggestions,
                IsIterableContainingInOrder.contains(expected.toArray()));

    }
}

