package service;


import app.Result;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
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
        Analyzer analyzer = new StandardAnalyzer();
        final String INDEX_DIRECTORY = new File("resources/index/").getAbsolutePath();
        Directory index = FSDirectory.open(Paths.get(INDEX_DIRECTORY));
        IndexReader reader = DirectoryReader.open(index);
        MultiFieldQueryParser parser = new MultiFieldQueryParser(new String[] {"text", "news_text"}, analyzer);
        IndexSearcher searcher  = new IndexSearcher(reader);
        searcher.setSimilarity(new BM25Similarity(1.2f, 0.75f));
        List<Result> documents = new ArrayList<>();
        if (string_query.length() < 1)
        {
            return documents;
        }

        Query query = parser.parse(string_query);
        TopDocs results = searcher.search(query, 25);
        ScoreDoc[] hits = results.scoreDocs;

        int i = 0;
        for (ScoreDoc hit : hits) {
            i++;
            Document doc = searcher.doc(hit.doc);
            String tweet_url = doc.get("permalink");
            String tweet_text = doc.get("text");
            String tweet_image = doc.get("image_url");
            String news_url = doc.get("news_url");
            String user = doc.get("username");
            Result result = new Result(i, user, tweet_url, tweet_image, tweet_text, news_url);
            documents.add(result);
        }

        return documents;
    }
}
