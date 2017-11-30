import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.*;
import java.util.*;
import static spark.Spark.*;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;

/**
 * starts the server for the system
 *
 */
public class VoterServer {
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		if (System.getSecurityManager() == null) System.setSecurityManager(new RMISecurityManager());
        Logger.getRootLogger().setLevel(Level.WARN);
        staticFiles.location("/UIResources");
        setFrontPage();
        System.out.println("Visit localhost:4567 to start.");
	}

	/**
	 * displays the front page. when user selects a question type to be asked, he will be redirected to
	 * the relevant page (/mcq for mcq questions and /open for open ended questions)
	 */
	private static void setFrontPage() {
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
//		setChartRoutes("mcq", null);
	}
	
	/**
	 * displays page to set up multiple choice questions. initializes VoterService instance to MCQVoterServiceImpl
	 * after user inputs his question and options. Also calls setChartRoute to allow chart to be displayed.
	 */
    private static void setMCQRoutes() {
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
    private static void setOpenEndedRoutes() {
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
    
    private static void setConfirmRoutes() {
    	get("/confirm", (request, response) -> {
            response.type("text/html");
            return new String(Files.readAllBytes(Paths.get("src/main/resources/ServerUI/ProfConfirmation.html")));
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
    private static void setChartRoutes(String type, VoterService vi) {
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
                PriorityQueue<Map.Entry<String, Integer>> topEntries = new PriorityQueue<>((x, y) -> y.getValue() - x.getValue());
                for (Map.Entry<String, Integer> voteEntry : vi.getMissCount().entrySet()) {
                	topEntries.add(voteEntry);
                }
                for (int i = 0; i < 10; i++) {
                	Map.Entry<String, Integer> highest = topEntries.poll();
                    redirectUrl.append("wrong_" + highest.getKey() + "=" + highest.getValue() + "&");
                }
            }
            redirectUrl.setLength(redirectUrl.length() - 1);
			System.out.println(redirectUrl.toString());
            response.redirect(redirectUrl.toString());
//            response.redirect("/chart/values?a=5&b=2&c=4&d=7");
            return null;
        });
    	
    	get("/chart/values", (request, response) -> {
            response.type("text/html");
            return new String(Files.readAllBytes(Paths.get("src/main/resources/ServerUI/" + type + "Chart.html")));
        });
    }
}
