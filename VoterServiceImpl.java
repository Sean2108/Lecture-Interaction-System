import java.rmi.*;
import java.rmi.server.*;
import java.util.*;

/** This is the main class of the server */
public class VoterServiceImpl extends UnicastRemoteObject implements VoterService {
	public final static String SERVICENAME="VoteService";
	HashMap<String, Integer> numberOfVotes = new HashMap<String, Integer>();
	ArrayList<String> voteOnce = new ArrayList<String>();
	
	
	public VoterServiceImpl() throws RemoteException {
		super();	// sets up networking
	}
	
	
	
	public synchronized void vote(String StudentId, String vote) throws java.rmi.RemoteException {
		
		for (String id : voteOnce) {
			if (id.equals(StudentId)) {
				System.out.println("You already voted!");
			}
			else {
				if (numberOfVotes.get(vote) != null)
					numberOfVotes.put(vote, (numberOfVotes.get(vote) + 1));
				else
					numberOfVotes.put(vote, 1);
	
		    }
	    }
	}
			
	
	public String[] getPoll() throws java.rmi.RemoteException {
		String[] answers = new String[5];
		answers[0] = "What is the proper value?";
		answers[1] = "1";
		answers[2] = "2";
		answers[3] = "3";
		answers[4] = "4";
		return answers;
	}
	
	
	public static void main(String[] args) {
		System.setSecurityManager(new RMISecurityManager());
		try {
			VoterServiceImpl vi = new VoterServiceImpl();
			Naming.rebind(VoterService.SERVICENAME, vi);
			System.out.println("Published in RMI registry, ready...");
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
			
}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
	


		