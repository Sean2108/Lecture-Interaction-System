import static spark.Spark.*;
import org.apache.log4j.Logger;
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
		Logger.getRootLogger().setLevel(Level.WARN);
        staticFiles.location("/UIResources");
        port(4568);
        ClientRouter c = new ClientRouter();
		try {
			c.setFrontPage();
			System.out.println("Visit localhost:4568 to answer questions.");
		}catch(Exception e) {
			System.err.println("Remote Exception: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
