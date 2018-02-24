package service;

import app.Result;
import app.TextAnalyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ResultReranking {

    public List<Result> rank(List<Document> documents, Document userProfile) {
        return new ArrayList<>();
    }

    private List<String> getTerms(Document document, String field) throws IOException {
        TokenStream stream = TextAnalyzer.textAnalyzer().tokenStream(field, new StringReader(document.getField(field).stringValue()));

        List<String> result = new ArrayList<>();
        try {
            stream.reset();
            while (stream.incrementToken()) {
                result.add(stream.getAttribute(CharTermAttribute.class).toString());
            }
        } catch (IOException e) {
            // not thrown b/c we're using a string reader...
        }

        return result;

    }

    private Map<String, Integer> countFrequencies(List<String> terms) {
        Map<String, Integer> wordCount = new HashMap<>();

        for (String word : terms) {
            Integer count = wordCount.get(word);
            wordCount.put(word, (count == null) ? 1 : count + 1);
        }

        return wordCount;
    }
}
