import java.io.*;
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
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        Logger.getRootLogger().setLevel(Level.WARN);
        staticFiles.location("/UIResources");
        setRoutes();
		try {
			System.out.println("Enter 1 for MCQ, 2 for Open ended question.");
			boolean isMCQ = br.readLine().equals("1") ? true : false;
			VoterService vi = null;
			List<String> poll = new ArrayList<>();
			System.out.println("What is the question?");
			poll.add(br.readLine());
			vi = getOptionsAndKeywords(poll, isMCQ, br);
			System.out.print("Set timer in seconds: ");
			int timeout = Integer.parseInt(br.readLine());
			System.out.println("Waiting for answers...");
			Naming.rebind(VoterService.SERVICENAME, vi);
			Thread.sleep(timeout * 1000);
			displayVotes(vi, isMCQ);
			System.exit(0);
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * helper method to display key value pairs in maps
	 * @param votes map to be displayed
	 */
	private static void printMap(Map<String, Integer> votes) {
		for (String key : votes.keySet()) {
			System.out.println(key + " -> " + votes.get(key) + " votes");
		}
		System.out.println();
	}
	
	/**
	 * helper method to display votes for MCQs or common correct or wrong words for open ended questions
	 * @param vi VoterService class that holds the maps storing the vote counts
	 * @param isMCQ true if professor is asking a multiple choice question, false if 
	 * professor is asking open ended question
	 * @throws RemoteException Exception thrown if VoterService cannot be accessed.
	 */
	private static void displayVotes(VoterService vi, boolean isMCQ) throws RemoteException {
		Map<String, Integer> voteCount = vi.getVoteCount();
		if (isMCQ) {
			System.out.println("Votes for multiple choice question: ");
			printMap(voteCount);
		}
		else {
			System.out.println("Correct keywords: ");
			printMap(voteCount);
			System.out.println("Wrong keywords: ");
			printMap(vi.getMissCount());
		}
	}
	
	/**
	 * prompts user for input for options (for MCQ) or keywords (for open ended questions)
	 * @param poll list of options/keywords to be passed to the voter service class
	 * @param isMCQ true if professor is asking a multiple choice question, false if 
	 * professor is asking open ended question
	 * @param br reader to read input from keyboard
	 * @return MCQVoterServiceImpl implementation class or OpenEndedVoterServiceImpl implementation 
	 * class for MCQ and Open ended questions respectively, or null if there is an exception
	 */
	private static VoterService getOptionsAndKeywords(List<String> poll, boolean isMCQ, BufferedReader br) {
		if (isMCQ) System.out.println("What are the options? (Input \"e\" to end)");
		else System.out.println("What are the keywords? (Input \"e\" to end)");
		try {
			while (true) {
				String option = br.readLine();
				if (option.equals("e")) break;
				poll.add(option);
			}
			if (isMCQ) return new MCQVoterServiceImpl(poll);
			return new OpenEndedVoterServiceImpl(poll);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

    private static void setRoutes() {
    	get("/mcq", (request, response) -> {
            response.type("text/html");
            return new String(Files.readAllBytes(Paths.get("src/main/resources/UIResources/QuestionSetupMC.html")));
        });
    	
    	post("/mcq", (req, res) -> {
            StringBuilder message = new StringBuilder();
            List<String> poll = new ArrayList<>();
            try {
                String question = req.queryParams("question");
                String[] a = req.queryParamsValues("a");
                message.append(question + "<br><br>");
                poll.add(question);
                for (int i = 0; i < a.length; i++) {
                	message.append((char)('a' + i) + ". " + a[i]);
                	message.append("<br>");
                	poll.add(a[i]);
                }
                Naming.rebind(VoterService.SERVICENAME, new MCQVoterServiceImpl(poll));
            } catch(Exception e) {
                e.printStackTrace();
            }
            return message.toString();
        });
    	
    	get("/open", (request, response) -> {
            response.type("text/html");
            return new String(Files.readAllBytes(Paths.get("src/main/resources/UIResources/QuestionSetupSA.html")));
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
            return question;
        });
    }
}
