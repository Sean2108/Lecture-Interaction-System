import java.io.*;
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
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String ans ="", id = "";
		Logger.getRootLogger().setLevel(Level.WARN);
        staticFiles.location("/UIResources");
		
		try {
			VoterService e = (VoterService) Naming.lookup(VoterService.SERVICENAME);
			poll = e.getPoll();
			setRoutes(poll);
			for (int i = 0; i < poll.length; i++) {
				System.out.println(poll[i]);
			}
			System.out.println("Enter answer:  ");
			
			try {
			   ans = br.readLine();
		       } catch(IOException ioe) {
			        System.err.println(ioe.getMessage());
		    }
			System.out.println("Enter Student ID");
			try {
				id = br.readLine();
		    } catch(IOException ioe) {
		    	System.err.println(ioe.getMessage());
		    }
			
            System.out.println(e.vote(id, ans));			
		}catch(Exception e) {
			System.err.println("Remote Exception: " + e.getMessage());
			e.printStackTrace();
		}
		
		
	}
	
	private static void setRoutes(String[] poll) {
    	get("/mcqAns", (request, response) -> {
            response.type("text/html");
            String html = new String(Files.readAllBytes(Paths.get("src/main/resources/UIResources/AnswerQuestionMC.html")));
            Document doc = Jsoup.parse(html);
            doc.selectFirst("p#q").text(poll[0]);
            doc.selectFirst("p#a").text(poll[1]);
            doc.selectFirst("p#b").text(poll[2]);
            doc.selectFirst("p#c").text(poll[3]);
            doc.selectFirst("p#d").text(poll[4]);
            return doc.toString();
        });
    	
    	get("/openAns", (request, response) -> {
            response.type("text/html");
            String html = new String(Files.readAllBytes(Paths.get("src/main/resources/UIResources/AnswerQuestionSA.html")));
            Document doc = Jsoup.parse(html);
            doc.selectFirst("p#q").text(poll[0]);
            return doc.toString();
        });
    	
    }
}
