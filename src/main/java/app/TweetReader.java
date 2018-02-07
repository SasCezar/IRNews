package app;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TweetReader {
    public static List<NewsTweet> read(String path){
        String line;
        String cvsSplitBy = ";";
        DateFormat df = new SimpleDateFormat("yyyy-mm-dd");
        List<NewsTweet> result = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            br.readLine();
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] values = line.split(cvsSplitBy);
                // System.out.println("len = [" + values.length + "]");
                String username = values[0];
                Date date = df.parse(values[1]);
                int retweets = Integer.parseInt(values[2]);
                String text = values[3];
                String hashtags = values[4];
                String id = values[5];
                String permalink = values[6];
                String news_url = values.length >= 8 ? values[7] : "";
                String news_txt = values.length >= 9 ? values[8] : "";
                String image_url = values.length >= 10 ? values[9] : "";
                // System.out.println(image_url);


                NewsTweet tweet = new NewsTweet(username, date, retweets, text, hashtags, id, permalink, news_url, news_txt, image_url);
                result.add(tweet);
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return result;
    }
}
