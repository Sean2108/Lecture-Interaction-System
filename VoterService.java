import java.rmi.*;
import java.util.*;

public interface VoterService extends java.rmi.Remote {
	public final static String SERVICENAME="VoteService";
	public String vote(String StudentId, String vote) throws java.rmi.RemoteException;
	public String[] getPoll() throws java.rmi.RemoteException;
}