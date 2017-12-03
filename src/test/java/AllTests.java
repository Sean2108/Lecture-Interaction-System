import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ MCQVoterServiceTest.class, OpenEndedVoterServiceTest.class, ServerRouterTest.class })
public class AllTests {

}
