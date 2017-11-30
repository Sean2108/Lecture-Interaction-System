import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.Naming;

import static spark.Spark.*;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.apache.log4j.Level;
// Voting on a poll question:
// Right now the question is hard-coded in the Server
// There are always four answers (a,b,c,d)
//
// 1. "start rmiregistry" 
// 2. run VoterServiceImpl with "java -Djava.security.policy=policy.txt VoterServiceImpl"
// 3. In another command window run "java VoterClient"
//
//  TO DO:  Need to display the poll result back to the client eventually in the form of a nice graph

/**
 * client program for students to view the question and respond.
 */
public class VoterClient {
	public static void main(String[] args) {
		String[] poll;
		Logger.getRootLogger().setLevel(Level.WARN);
        staticFiles.location("/UIResources");
        port(4568);
		try {
			VoterService e = (VoterService) Naming.lookup(VoterService.SERVICENAME);
			poll = e.getPoll();
			setRoutes(poll, e);
			System.out.println("Visit localhost:4567/mcqAns to answer multiple choice questions, " + 
			"or localhost:4567:openAns to answer open ended questions.");
		}catch(Exception e) {
			System.err.println("Remote Exception: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * allows pages to be displayed on client browser. /mcqAns is used for answering multiple choice questions,
	 * and /openAns is used for answering open ended questions. Jsoup library is used to parse the html and
	 * replace the placeholder text with the question and options provided by the professor.
	 * VoterService's vote method will be called at the end to tally the vote and provide feedback.
	 * @param poll 0th index contains the question, the rest of the indices contain mcq options or open ended keywords
	 * @param vi java RMI class that will be either have different implementations depending on the type of question.
	 * vi's vote method will add the vote to a hash map if the student has not voted yet.
	 */
	private static void setRoutes(String[] poll, VoterService vi) {
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
    }
}
