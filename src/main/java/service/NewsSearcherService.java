package service;


import app.Result;
import app.TextAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queries.CustomScoreQuery;
import org.apache.lucene.queries.function.FunctionQuery;
import org.apache.lucene.queries.function.valuesource.FloatFieldSource;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Component
public class NewsSearcherService {
    public List<Result> search(String string_query) throws ParseException, IOException {
        Analyzer analyzer = TextAnalyzer.textAnalyzer();
        final String INDEX_DIRECTORY = new File("resources/index/").getAbsolutePath();
        Directory index = FSDirectory.open(Paths.get(INDEX_DIRECTORY));
        IndexReader reader = DirectoryReader.open(index);
        MultiFieldQueryParser parser = new MultiFieldQueryParser(new String[]{"text", "news_text", "id", "hashtags"}, analyzer);
        IndexSearcher searcher = new IndexSearcher(reader);
        searcher.setSimilarity(new BM25Similarity(1.2f, 0.75f));

        List<Result> documents = searchDocuments(string_query, parser, searcher);
        if (documents.size() == 0) {
            documents = searchDocuments(string_query + "*", parser, searcher);
        }
        if (documents.size() == 0) {
            documents = searchDocuments(string_query + "~", parser, searcher);
        }

        return documents;
    }

    private List<Result> searchDocuments(String string_query, MultiFieldQueryParser parser, IndexSearcher searcher) throws ParseException, IOException {
        List<Result> documents = new ArrayList<>();

        if (string_query.length() < 1) {
            return documents;
        }

        Query query = parser.parse(string_query);
        FunctionQuery etaQueryBoost = new FunctionQuery(new FloatFieldSource("log_eta"));
        FunctionQuery retweetQueryBoost = new FunctionQuery(new FloatFieldSource("log_retweets"));
        Query customQuery = new CustomScoreQuery(query, etaQueryBoost, retweetQueryBoost);

        TopDocs results = searcher.search(customQuery, 25);
        ScoreDoc[] hits = results.scoreDocs;

        // ScoreDoc[] top_ten = Arrays.copyOfRange(hits, 0, 10);

        int i = 0;
        for (ScoreDoc hit : hits) {
            i++;
            Document doc = searcher.doc(hit.doc);
            String tweet_url = doc.get("permalink");
            String tweet_text = doc.get("formattedText");
            String tweet_image = doc.get("image_url");
            String news_url = doc.get("news_url");
            String user = doc.get("username");
            String date = doc.get("date");
            String retweets = doc.get("retweets");
            Result result = new Result(i, user, tweet_url, tweet_image, tweet_text, news_url, retweets, date);
            documents.add(result);
        }

        return documents;
    }
}
