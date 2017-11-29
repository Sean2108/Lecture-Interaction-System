import java.io.*;
import java.rmi.Naming;
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
 * @author robert
 */
public class VoterClient {
	public static void main(String[] args) {
		String[] poll;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String ans ="", id = "";
		
		try {
			VoterService e = (VoterService) Naming.lookup(VoterService.SERVICENAME);
			poll = e.getPoll();
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
}
