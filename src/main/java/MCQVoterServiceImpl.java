import java.rmi.*;
import java.rmi.server.*;
import java.util.*;

/** This is the main class of the server */
public class MCQVoterServiceImpl extends UnicastRemoteObject implements VoterService {
	public final static String SERVICENAME="VoteService";
	HashMap<String, Integer> numberOfVotes = new HashMap<String, Integer>();
	ArrayList<String> voteOnce = new ArrayList<String>();
	List<String> poll;
	
	
	public MCQVoterServiceImpl() throws RemoteException {
		super();	// sets up networking
	}
	
	public MCQVoterServiceImpl(List<String> poll) throws RemoteException {
		super();	// sets up networking
		this.poll = poll;
	}
	
	
	
	public synchronized String vote(String studentId, String vote) throws java.rmi.RemoteException {
		if (voteOnce.contains(studentId)) return "You already voted!";
		else {
			numberOfVotes.put(vote, numberOfVotes.getOrDefault(vote, 0) + 1);
			voteOnce.add(studentId);
		}
		printVoteCount();
		return "You voted for " + vote;
//		for (String id : voteOnce) {
//			if (id.equals(studentId)) {
//				System.out.println("You already voted!");
//			}
//			else {
//				if (numberOfVotes.get(vote) != null)
//					numberOfVotes.put(vote, (numberOfVotes.get(vote) + 1));
//				else
//					numberOfVotes.put(vote, 1);
//		    }
//	    }
	}
			
	
	public String[] getPoll() throws java.rmi.RemoteException {
		return poll.toArray(new String[poll.size()]);
	}
	
	private void printVoteCount() {
		for (String key : numberOfVotes.keySet()) {
			System.out.println(key + " -> " + numberOfVotes.get(key) + " votes");
		}
		System.out.println();
	}
}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
	


		
