import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.*;
import static spark.Spark.*;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;

/**
 * starts the server for the system and constructs ServerRouter
 *
 */
public class VoterServer {
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		if (System.getSecurityManager() == null) System.setSecurityManager(new RMISecurityManager());
        Logger.getRootLogger().setLevel(Level.WARN);
        staticFiles.location("/UIResources");
        String hostname;
        try {
			hostname = InetAddress.getLocalHost().getHostAddress();
            // private ip address of host, uncomment and change to your ip if connection error occurs. Linux host address method behaves inconsistently.
			// hostname = "10.160.18.54";
            System.setProperty("java.rmi.server.hostname", hostname);
	        ServerRouter s = new ServerRouter();
	        s.setFrontPage();
	        System.out.println("Visit localhost:4567 to start.");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
}
