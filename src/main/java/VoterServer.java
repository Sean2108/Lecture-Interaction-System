import java.rmi.*;
import static spark.Spark.*;
import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;
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
        // host name of server
		System.setProperty("java.rmi.server.hostname", "10.160.16.84");
        ServerRouter s = new ServerRouter();
        s.setFrontPage();
        System.out.println("Visit localhost:4567 to start.");
	}
}
