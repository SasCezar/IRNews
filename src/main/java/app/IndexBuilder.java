package app;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
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
import java.util.List;

public class IndexBuilder {
    public static void main(String[] args) throws IOException {
        String filePath = new File("resources/documents/import.csv").getAbsolutePath();
        Path path = Paths.get(new File("resources/index/").getAbsolutePath());
        Directory dir = FSDirectory.open(path);
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setSimilarity(new BM25Similarity(1.2f,0.75f));
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter iwriter = new IndexWriter(dir,config);

        List<NewsTweet> tweets = TweetReader.read(filePath);

        for (NewsTweet tweet : tweets) {
            Document document = new Document();
            Field username = new TextField("username", tweet.username, Field.Store.YES);
            document.add(username);
            Field date = new StringField("date", DateTools.dateToString(tweet.date, DateTools.Resolution.DAY), Field.Store.YES);
            document.add(date);
            Field retweets = new IntPoint("retweets", tweet.retweets);
            document.add(retweets);
            Field text = new TextField("text", tweet.text, Field.Store.YES);
            document.add(text);
            Field hashtags = new TextField("hashtags", tweet.hashtags, Field.Store.YES);
            document.add(hashtags);
            Field id = new TextField("id", tweet.id, Field.Store.YES);
            document.add(id);
            Field permalink = new TextField("permalink", tweet.permalink, Field.Store.YES);
            document.add(permalink);
            Field news_url = new TextField("news_url", tweet.news_url, Field.Store.YES);
            document.add(news_url);
            Field news_text = new TextField("news_text", tweet.news_text, Field.Store.YES);
            document.add(news_text);

            Field image_url = new TextField("image_url", tweet.image_url, Field.Store.YES);
            document.add(image_url);

            iwriter.addDocument(document);
        }
        iwriter.commit();
        iwriter.close();
    }
}
