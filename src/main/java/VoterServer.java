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
	}
	
	/**
	 * displays page to set up multiple choice questions. initializes VoterService instance to MCQVoterServiceImpl
	 * after user inputs his question and options.
	 */
    private static void setMCQRoutes() {
    	get("/mcq", (request, response) -> {
            response.type("text/html");
            return new String(Files.readAllBytes(Paths.get("src/main/resources/ServerUI/QuestionSetupMC.html")));
        });
    	
    	get("/mcq?", (request, response) -> {
            response.redirect("/mcq");
            return null;
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
                Naming.rebind(VoterService.SERVICENAME, new MCQVoterServiceImpl(poll));
            } catch(Exception e) {
                e.printStackTrace();
            }
            return new String(Files.readAllBytes(Paths.get("src/main/resources/ServerUI/ProfConfirmation.html")));
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
    	
    	get("/open?", (request, response) -> {
            response.redirect("/open");
            return null;
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
                Naming.rebind(VoterService.SERVICENAME, new OpenEndedVoterServiceImpl(poll));
            } catch(Exception e) {
                e.printStackTrace();
            }
            return new String(Files.readAllBytes(Paths.get("src/main/resources/ServerUI/ProfConfirmation.html")));
        });
    }
}
