import java.io.*;
import java.rmi.*;
import java.rmi.server.*;
import java.util.*;

/** This is the main class of the server */
public class VoterServer {
	public static void main(String[] args) {
		System.setSecurityManager(new RMISecurityManager());
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			System.out.println("Enter 1 for MCQ, 2 for Open ended question.");
			String input = br.readLine();
			VoterService vi = null;
			List<String> poll = new ArrayList<>();
			System.out.println("What is the question?");
			poll.add(br.readLine());
			if (input.equals("1")) {
				System.out.println("What are the options? (Input \"e\" to end)");
				while (true) {
					String option = br.readLine();
					if (option.equals("e")) break;
					poll.add(option);
				}
				vi = new MCQVoterServiceImpl(poll);
			}
			else {
				vi = new OpenEndedVoterServiceImpl(poll);
			}
			Naming.rebind(VoterService.SERVICENAME, vi);
			System.out.println("Published in RMI registry, ready...");
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
			
}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
	


		
