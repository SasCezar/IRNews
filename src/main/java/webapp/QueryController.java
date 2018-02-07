package webapp;


import app.Result;
import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import service.NewsSearcherService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
public class QueryController {

    @Autowired
    private NewsSearcherService searcher;

    @RequestMapping("/search")
    public String query(@RequestParam Map<String,String> requestParams, Model model) {
        return "search";
    }

    @RequestMapping("/compute")
    public String search(@RequestParam Map<String,String> requestParams, Model model) throws IOException, ParseException {
        String query = requestParams.get("query");
        String user = requestParams.get("ic-current-url").split("=")[1];
        String topic = requestParams.get("topic");

        if(query.trim().isEmpty()){
            return "results";
        }

        model.addAttribute("topic", topic);
        model.addAttribute("user", user);
        model.addAttribute("query", query);
        List<Result> results = searcher.search(query);
        model.addAttribute("results", results);
        return "results";
    }
}
