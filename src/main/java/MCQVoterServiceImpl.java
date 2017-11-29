import java.rmi.*;
import java.rmi.server.*;
import java.util.*;

/**
 * implementation to handle MCQ
 * @author robert
 *
 */
public class MCQVoterServiceImpl extends UnicastRemoteObject implements VoterService {
	public final static String SERVICENAME="VoteService";
	Map<String, Integer> numberOfVotes = new HashMap<String, Integer>();
	List<String> voteOnce = new ArrayList<String>();
	List<String> poll;
	
	/**
	 * zero parameter constructor
	 * @throws RemoteException VoterService class cannot be found
	 */
	public MCQVoterServiceImpl() throws RemoteException {
		super();	// sets up networking
	}
	
	/**
	 * obtains question and list of options from professor
	 * @param poll index 0 is the question, the rest of the indices are the available options
	 * @throws RemoteException VoterService class cannot be found
	 */
	public MCQVoterServiceImpl(List<String> poll) throws RemoteException {
		super();	// sets up networking
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
}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
	


		
