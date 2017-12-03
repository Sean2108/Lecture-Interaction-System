import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * tests ServerRouter class TopTenToURL method
 * @author Sean
 *
 */
public class ServerRouterTest {

	private Map<String, Integer> testMap;
	private static ServerRouter testRouter;
	
	@BeforeClass
	public static void setUpClass() throws Exception {
		testRouter = new ServerRouter();
	}

	@Before
	public void setUp() throws Exception {
		testMap = new HashMap<>();
	}
	
	/**
	 * sets up 26 mock values in the map. should return correct query string
	 * containing top 10 values from highest value to lowest value.
	 */
	@Test
	public void addTopTenToURL_moreThan10Values_shouldReturnCorrectString() {
		testMap.put("ansA", 150);
		testMap.put("ansB", 200);
		testMap.put("ansC", 99);
		testMap.put("ansD", 10);
		testMap.put("ansE", 15);
		testMap.put("ansF", 85);
		testMap.put("ansG", 250);
		testMap.put("ansH", 221);
		testMap.put("ansI", 245);
		testMap.put("ansJ", 157);
		testMap.put("ansK", 195);
		testMap.put("ansL", 174);
		testMap.put("ansM", 103);
		testMap.put("ansN", 102);
		testMap.put("ansO", 201);
		testMap.put("ansP", 274);
		testMap.put("ansQ", 355);
		testMap.put("ansR", 108);
		testMap.put("ansS", 11);
		testMap.put("ansT", 51);
		testMap.put("ansU", 73);
		testMap.put("ansV", 49);
		testMap.put("ansW", 88);
		testMap.put("ansX", 99);
		testMap.put("ansY", 258);
		testMap.put("ansZ", 376);
		StringBuilder ans = new StringBuilder();
		ans.append("wrong_ansZ=376&");
		ans.append("wrong_ansQ=355&");
		ans.append("wrong_ansP=274&");
		ans.append("wrong_ansY=258&");
		ans.append("wrong_ansG=250&");
		ans.append("wrong_ansI=245&");
		ans.append("wrong_ansH=221&");
		ans.append("wrong_ansO=201&");
		ans.append("wrong_ansB=200&");
		ans.append("wrong_ansK=195&");
		assertEquals(testRouter.addTopTenToURL(testMap), ans.toString());
	}

	/**
	 * sets up 6 mock values in the map. should return correct query string
	 * containing all values ordered from highest value to lowest value.
	 */
	@Test
	public void addTopTenToURL_lessThan10Values_returnCorrectString() {
		testMap.put("ansA", 150);
		testMap.put("ansB", 200);
		testMap.put("ansC", 99);
		testMap.put("ansD", 10);
		testMap.put("ansE", 15);
		testMap.put("ansF", 85);
		StringBuilder ans = new StringBuilder();
		ans.append("wrong_ansB=200&");
		ans.append("wrong_ansA=150&");
		ans.append("wrong_ansC=99&");
		ans.append("wrong_ansF=85&");
		ans.append("wrong_ansE=15&");
		ans.append("wrong_ansD=10&");
		assertEquals(testRouter.addTopTenToURL(testMap), ans.toString());
	}
}
