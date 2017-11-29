import java.io.*;
import java.rmi.*;
import java.util.*;

/** This is the main class of the server */
public class VoterServer {
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		System.setSecurityManager(new RMISecurityManager());
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			System.out.println("Enter 1 for MCQ, 2 for Open ended question.");
			boolean isMCQ = br.readLine().equals("1") ? true : false;
			VoterService vi = null;
			List<String> poll = new ArrayList<>();
			System.out.println("What is the question?");
			poll.add(br.readLine());
			if (isMCQ) {
				System.out.println("What are the options? (Input \"e\" to end)");
				while (true) {
					String option = br.readLine();
					if (option.equals("e")) break;
					poll.add(option);
				}
				vi = new MCQVoterServiceImpl(poll);
			}
			else {
				System.out.println("What are the keywords? (Input \"e\" to end)");
				while (true) {
					String option = br.readLine();
					if (option.equals("e")) break;
					poll.add(option);
				}
				vi = new OpenEndedVoterServiceImpl(poll);
			}
			Naming.rebind(VoterService.SERVICENAME, vi);
			System.out.print("Set timer in seconds: ");
			int timeout = Integer.parseInt(br.readLine());
			System.out.println("Waiting for answers...");
			Thread.sleep(timeout * 1000);
			Map<String, Integer> voteCount = vi.getVoteCount();
			if (isMCQ) {
				System.out.println("Votes for multiple choice question: ");
				displayVotes(voteCount);
			}
			else {
				System.out.println("Correct keywords: ");
				displayVotes(voteCount);
				System.out.println("Wrong keywords: ");
				displayVotes(vi.getMissCount());
			}
			System.exit(0);
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	
	private static void displayVotes(Map<String, Integer> votes) {
		for (String key : votes.keySet()) {
			System.out.println(key + " -> " + votes.get(key) + " votes");
		}
		System.out.println();
	}
}