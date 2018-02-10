package app;

import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class IndexBuilder {
    public static void main(String[] args) throws IOException, ParseException {
        String filePath = new File("resources/documents/import.csv").getAbsolutePath();
        Path path = Paths.get(new File("resources/index/").getAbsolutePath());
        Directory dir = FSDirectory.open(path);
        // Analyzer analyzer = new EnglishAnalyzer();

        // Map<String, Analyzer> analyzerPerField = new HashMap<>();
        // analyzerPerField.put("text", TextAnalyzer.textAnalyzer());
        // analyzerPerField.put("news_text", TextAnalyzer.textAnalyzer());
        // PerFieldAnalyzerWrapper aWrapper = new PerFieldAnalyzerWrapper(new StandardAnalyzer(), analyzerPerField);

        IndexWriterConfig config = new IndexWriterConfig(TextAnalyzer.textAnalyzer());
        config.setSimilarity(new BM25Similarity(1.2f, 0.75f));
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter iwriter = new IndexWriter(dir, config);

        List<NewsTweet> tweets = TweetReader.read(filePath);


        for (NewsTweet tweet : tweets) {
            Document document = new Document();
            Field username = new TextField("username", tweet.username, Field.Store.YES);
            document.add(username);
            Field date = new StringField("date", tweet.date, Field.Store.YES);
            document.add(date);
            Field text = new TextField("text", tweet.text.replace("http:// ", "http://"), Field.Store.YES);
            document.add(text);
            Field hashtags = new TextField("hashtags", tweet.hashtags, Field.Store.YES);
            document.add(hashtags);
            Field id = new TextField("id", tweet.id, Field.Store.YES);
            document.add(id);
            Field permalink = new StoredField("permalink", tweet.permalink);
            document.add(permalink);
            Field news_url = new StoredField("news_url", tweet.news_url);
            document.add(news_url);
            Field news_text = new TextField("news_text", tweet.news_text, Field.Store.YES);
            document.add(news_text);
            Field image_url = new StoredField("image_url", tweet.image_url);
            document.add(image_url);
            Field retweets = new StoredField("retweets", tweet.retweets);
            document.add(retweets);

            Field eta = new NumericDocValuesField("eta", getTimestamp(tweet.date));
            document.add(eta);
            Field num_retweets = new NumericDocValuesField("retweets", (long)tweet.retweets);
            document.add(num_retweets);

            iwriter.addDocument(document);
        }
        iwriter.commit();
        iwriter.close();

    }


    private static long getTimestamp(String date) throws ParseException {
        DateFormat df = new SimpleDateFormat("yyyy-mm-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(df.parse(date));
        long time = c.getTimeInMillis();
        long curr = System.currentTimeMillis();
        long diff = curr - time;    //Time difference in milliseconds
        return diff / 1000;
    }
}
