import java.rmi.*;
import java.rmi.server.*;
import java.util.*;

/**
 * implementation to handle MCQ
 *
 */
public class MCQVoterServiceImpl extends UnicastRemoteObject implements VoterService {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8064717333958683642L;
	public final static String SERVICENAME = "VoteService";
	private Map<String, Integer> numberOfVotes;
	private Set<String> voteOnce;
	private List<String> poll;
	
	/**
	 * zero parameter constructor
	 * @throws RemoteException VoterService class cannot be found
	 */
	public MCQVoterServiceImpl() throws RemoteException {
		super();	// sets up networking
		numberOfVotes = new HashMap<>();
		voteOnce = new HashSet<>();
	}
	
	/**
	 * obtains question and list of options from professor
	 * @param poll index 0 is the question, the rest of the indices are the available options
	 * @throws RemoteException VoterService class cannot be found
	 */
	public MCQVoterServiceImpl(List<String> poll) throws RemoteException {
		this();	// sets up networking
		this.poll = poll;
	}
	
	/**
	 * checks if student has already voted. If not, tally his vote.
	 */
	public synchronized String vote(String studentId, String vote) throws java.rmi.RemoteException {
		if (voteOnce.contains(studentId)) return "You already voted!";
		else {
			numberOfVotes.put(vote, numberOfVotes.getOrDefault(vote, 0) + 1);
			voteOnce.add(studentId);
		}
		return "You voted for " + vote;
	}
			
	/**
	 * callback method for VoterClient to access the question and options
	 */
	public String[] getPoll() throws java.rmi.RemoteException {
		return poll.toArray(new String[poll.size()]);
	}
	
	/**
	 * returns vote map
	 */
	public Map<String, Integer> getVoteCount() {
		return numberOfVotes;
	}
	
	/**
	 * not relevant for MCQVoterServiceImpl, used only for 
	 * OpenEndedVoterServiceImpl
	 */
	public Map<String, Integer> getMissCount() {
		return null;
	}
	
	/**
	 * method to identify the implementation. returns true as it is MCQ
	 */
	public boolean isMCQ() {
		return true;
	}
}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
	


		
