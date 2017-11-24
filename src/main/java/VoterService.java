import java.util.Map;

public interface VoterService extends java.rmi.Remote {
	public final static String SERVICENAME="VoteService";
	public String vote(String StudentId, String vote) throws java.rmi.RemoteException;
	public String[] getPoll() throws java.rmi.RemoteException;
	public Map<String, Integer> getVoteCount() throws java.rmi.RemoteException;
	public Map<String, Integer> getMissCount() throws java.rmi.RemoteException;
}