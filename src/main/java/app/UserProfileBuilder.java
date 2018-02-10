package app;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UserProfileBuilder {
    public static void main(String[] args) throws IOException {
        String filePath = new File("resources/documents/import.csv").getAbsolutePath();
        Path path = Paths.get(new File("resources/profiles/").getAbsolutePath());
        Directory dir = FSDirectory.open(path);

        IndexWriterConfig config = new IndexWriterConfig(TextAnalyzer.textAnalyzer());
        config.setSimilarity(new BM25Similarity(1.2f, 0.75f));
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter iwriter = new IndexWriter(dir, config);


    }
}
