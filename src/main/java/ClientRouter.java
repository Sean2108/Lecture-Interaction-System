import static spark.Spark.get;
import static spark.Spark.post;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.Naming;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class ClientRouter {
	/**
	 * allows pages to be displayed on client browser. /mcqAns is used for answering multiple choice questions. 
	 * Jsoup library is used to parse the html and
	 * replace the placeholder text with the question and options provided by the professor.
	 * VoterService's vote method will be called at the end to tally the vote and provide feedback.
	 * @param poll 0th index contains the question, the rest of the indices contain mcq options
	 * @param vi java RMI class that will be either have different implementations depending on the type of question.
	 * vi's vote method will add the vote to a hash map if the student has not voted yet.
	 * @return the route to the mcqAns page
	 * @throws RemoteException exception thrown when unable to get remote object
	 */
	private String setMCQRoutes(VoterService vi) throws RemoteException {
		String[] poll = vi.getPoll();
    	get("/mcqAns", (request, response) -> {
            response.type("text/html");
            String html = new String(Files.readAllBytes(Paths.get("src/main/resources/ClientUI/AnswerQuestionMC.html")));
            Document doc = Jsoup.parse(html);
            doc.selectFirst("p#q").text(poll[0]);
            doc.selectFirst("p#a").text(poll[1]);
            doc.selectFirst("p#b").text(poll[2]);
            doc.selectFirst("p#c").text(poll[3]);
            doc.selectFirst("p#d").text(poll[4]);
            return doc.toString();
        });
    	
    	post("/mcqAns", (request, response) -> {
            response.type("text/html");
            try {
                String id = request.queryParams("studentId");
                String ans = request.queryParams("ans");
                return vi.vote(id, ans);
            } catch(Exception e) {
                e.printStackTrace();
            }
            return null;
        });
    	
    	return "mcqAns";
    }
	
	/**
	 * allows pages to be displayed on client browser. /openAns is used for answering open ended questions. 
	 * Jsoup library is used to parse the html and replace the placeholder text 
	 * with the question and options provided by the professor.
	 * VoterService's vote method will be called at the end to tally the vote and provide feedback.
	 * @param poll 0th index contains the question, the rest of the indices contain open ended keywords
	 * @param vi java RMI class that will be either have different implementations depending on the type of question.
	 * vi's vote method will add the vote to a hash map if the student has not voted yet.
	 * @return the route to the openAns page
	 * @throws RemoteException Exception thrown when unable to get remote object
	 */
	private String setOpenEndedRoutes(VoterService vi) throws RemoteException {
		String[] poll = vi.getPoll();
		get("/openAns", (request, response) -> {
            response.type("text/html");
            String html = new String(Files.readAllBytes(Paths.get("src/main/resources/ClientUI/AnswerQuestionSA.html")));
            Document doc = Jsoup.parse(html);
            doc.selectFirst("p#q").text(poll[0]);
            return doc.toString();
        });
    	
    	post("/openAns", (request, response) -> {
            response.type("text/html");
            try {
                String id = request.queryParams("studentId");
                String ans = request.queryParams("sa");
                return vi.vote(id, ans);
            } catch(Exception e) {
                e.printStackTrace();
            }
            return null;
        });
    	
    	return "openAns";
	}
	
	/**
	 * set the front page of client program. redirects to mcqAns or openAns based on
	 * what vi.isMCQ() returns
	 */
	void setFrontPage() {
		get("/", (request, response) -> {
            response.type("text/html");
            return new String(Files.readAllBytes(Paths.get("src/main/resources/ClientUI/StudentEnterIP.html")));
        });
		
		post("/", (req, res) -> {
            String ipAdd = req.queryParams("ipAdd");
            try {
	            Registry registry = LocateRegistry.getRegistry(ipAdd, 2001);
	            VoterService vi = (VoterService) registry.lookup(VoterService.SERVICENAME);
	            String nextPage = "";
	            if (vi.isMCQ()) nextPage = setMCQRoutes(vi);
	            else nextPage = setOpenEndedRoutes(vi);
	            res.redirect("/" + nextPage);
	            return null;
            } catch (Exception e) {
				e.printStackTrace();
            	return "Unable to connect to IP address provided";
            }
        });
	}
}
