import static spark.Spark.get;
import static spark.Spark.post;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.Naming;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class ServerRouter {
	/**
	 * displays the front page. when user selects a question type to be asked, he will be redirected to
	 * the relevant page (/mcq for mcq questions and /open for open ended questions)
	 */
	public void setFrontPage() {
		get("/", (request, response) -> {
            response.type("text/html");
            return new String(Files.readAllBytes(Paths.get("src/main/resources/ServerUI/FrontPage.html")));
        });
		
		post("/", (req, res) -> {
            String choice = req.queryParams("choice");
            if (choice.equals("mcq")) setMCQRoutes();
            else setOpenEndedRoutes();
            res.redirect("/" + choice);
            return null;
        });
	}
	
	/**
	 * displays page to set up multiple choice questions. initializes VoterService instance to MCQVoterServiceImpl
	 * after user inputs his question and options. Also calls setChartRoute to allow chart to be displayed.
	 */
    private void setMCQRoutes() {
    	get("/mcq", (request, response) -> {
            response.type("text/html");
            return new String(Files.readAllBytes(Paths.get("src/main/resources/ServerUI/QuestionSetupMC.html")));
        });
    	
    	post("/mcq", (req, res) -> {
            List<String> poll = new ArrayList<>();
            try {
                String question = req.queryParams("question");
                String[] a = req.queryParamsValues("a");
                poll.add(question);
                for (int i = 0; i < a.length; i++) {
                	poll.add(a[i]);
                }
                VoterService vi = new MCQVoterServiceImpl(poll);
                Naming.rebind(VoterService.SERVICENAME, vi);
        		setChartRoutes("mcq", vi);
            } catch(Exception e) {
                e.printStackTrace();
            }
            setConfirmRoutes();
            res.redirect("/confirm");
            return null;
        });
    }
    
    /**
     * displays page to set up open ended questions. initializes VoterService instance to OpenEndedVoterServiceImpl
	 * after user inputs his question and keywords.
     */
    private void setOpenEndedRoutes() {
    	get("/open", (request, response) -> {
            response.type("text/html");
            return new String(Files.readAllBytes(Paths.get("src/main/resources/ServerUI/QuestionSetupSA.html")));
        });
    	
    	post("/open", (req, res) -> {
            String question = "";
            List<String> poll = new ArrayList<>();
            try {
                question = req.queryParams("question");
                String[] keywords = req.queryParams("keywords").split(" ");
                poll.add(question);
                for (String key : keywords) {
                	poll.add(key);
                }
                VoterService vi = new OpenEndedVoterServiceImpl(poll);
                Naming.rebind(VoterService.SERVICENAME, vi);
        		setChartRoutes("open", vi);
            } catch(Exception e) {
                e.printStackTrace();
            }
            setConfirmRoutes();
            res.redirect("/confirm");
            return null;
        });
    }
    
    /**
     * displays ip address on confirmation screen. When "View chart" button is clicked,
     * redirect to chart page
     */
    private void setConfirmRoutes() {
    	get("/confirm", (request, response) -> {
            response.type("text/html");
            String html = new String(Files.readAllBytes(Paths.get("src/main/resources/ServerUI/ProfConfirmation.html")));
            Document doc = Jsoup.parse(html);
            doc.selectFirst("span#ipAdd").text(getIP());
            return doc.toString();
        });
    	
    	post("/confirm", (request, response) -> {
            response.type("text/html");
            response.redirect("/chart");
            return null;
        });
    }
    
    /**
     * pass parameters to chart to display statistics
     * mcq has 1 map while open ended has 2 maps.
     * for open ended's wrong map, use a priority queue to get the top 10 counts
     * @param vi VoterService that contains maps containing key value pairs of vote to counts
     */
    private void setChartRoutes(String type, VoterService vi) {
    	get("/chart", (request, response) -> {
            response.type("text/html");
            Map<String, Integer> voteCount = vi.getVoteCount();
            StringBuilder redirectUrl = new StringBuilder("/chart/values?");
            if (type.equals("mcq")) {
                for (String vote : voteCount.keySet()) {
                	redirectUrl.append(vote + "=" + voteCount.get(vote) + "&");
                }
            }
            else {
                for (String vote : voteCount.keySet()) {
                	redirectUrl.append("key_" + vote + "=" + voteCount.get(vote) + "&");
                }
                redirectUrl.append(addTopTenToURL(vi.getMissCount()));
            }
            redirectUrl.setLength(redirectUrl.length() - 1);
            response.redirect(redirectUrl.toString());
            return null;
        });
    	
    	get("/chart/values", (request, response) -> {
            response.type("text/html");
            return new String(Files.readAllBytes(Paths.get("src/main/resources/ServerUI/" + type + "Chart.html")));
        });
    }
    
    /**
     * uses max heap to get the top 10 used words that are not in the professor's keywords
     * @param count map containing key value pairs of word -> count of occurrence
     * @return string to append as query string to end of redirectUrl
     */
    private String addTopTenToURL(Map<String, Integer> count) {
    	PriorityQueue<Map.Entry<String, Integer>> topEntries = new PriorityQueue<>((x, y) -> y.getValue() - x.getValue());
    	for (Map.Entry<String, Integer> voteEntry : count.entrySet()) {
        	topEntries.add(voteEntry);
        }
        StringBuilder query = new StringBuilder();
        int size = topEntries.size();
        for (int i = 0; i < Math.min(10, size); i++) {
        	Map.Entry<String, Integer> highest = topEntries.poll();
        	query.append("wrong_" + highest.getKey() + "=" + highest.getValue() + "&");
        }
        return query.toString();
    }
    
    /**
     * pings AWS in order to retrieve server's IP address
     * @return Server's public IP
     * @throws IOException Unable to read AWS's response
     */
	private String getIP() throws IOException {
		URL url = new URL("http://checkip.amazonaws.com/");
		BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
		return br.readLine();
	}
}
