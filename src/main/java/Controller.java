import static spark.Spark.*;
import java.net.URL;
import java.nio.file.*;
import java.nio.charset.Charset;
import java.io.IOException;
import java.net.URISyntaxException;
public class Controller {
        
    public void getMCQ() throws IOException, URISyntaxException {
        get("/mcq", (request, response) -> {
            response.type("text/html");
            //URL url = ClassLoader.getSystemClassLoader().getResource("src/main/resources/UIResources/QuestionSetupMC.html");

            //Path path = Paths.get(url.toURI()); 
            //return new String(Files.readAllBytes(path));
            return new String(Files.readAllBytes(Paths.get("src/main/resources/UIResources/QuestionSetupMC.html")));
        });
    }
            
}
