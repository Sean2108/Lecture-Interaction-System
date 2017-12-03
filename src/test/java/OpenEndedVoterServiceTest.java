

import static org.junit.Assert.*;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

public class OpenEndedVoterServiceTest {

	private VoterService vi;
	
	@Before
	public void setUp() throws Exception {
		vi = new OpenEndedVoterServiceImpl(new ArrayList<String>(Arrays.asList("question", "good", "great", "bad", "hard")));
	}

	/**
	 * voter 1 votes twice. only his first answer should be added to the vote map.
	 */
	@Test
	public void vote_VoteTwice_shouldReturnErrorMessageMapUnchanged() {
		try {
			assertEquals(vi.vote("1", "good bad"), "You are 50% right!");
			assertTrue(vi.getVoteCount().containsKey("good"));
			assertTrue(vi.getVoteCount().containsKey("bad"));
			assertEquals(vi.getVoteCount().size(), 2);
			assertTrue(vi.getMissCount().isEmpty());
			assertEquals((int) vi.getVoteCount().get("good"), 1);
			assertEquals((int) vi.getVoteCount().get("bad"), 1);
			assertEquals(vi.vote("1", "great hard"), "You already answered!");
			assertEquals(vi.getVoteCount().size(), 2);
			assertTrue(vi.getMissCount().isEmpty());
			assertEquals((int) vi.getVoteCount().get("good"), 1);
			assertEquals((int) vi.getVoteCount().get("bad"), 1);
		} catch (RemoteException e) {
			e.printStackTrace();
			fail("Remote exception");
		}
	}

	/**
	 * voter 1 answers "great" and "hard". Vote map should have 2 keys,
	 * with great -> 1 and hard -> 1. voter 2 votes "tested", "great"
	 * and "hard". Vote map should have 3 keys, with great -> 2, bad -> 1
	 * and hard -> 1. Wrong map should contain "test", the root word of 
	 * "tested" with value of 1.
	 */
	@Test
	public void vote_2Voters_shouldHaveCorrectNumOfVotes() {
		try {
			assertEquals(vi.vote("1", "great hard"), "You are 50% right!");
			assertTrue(vi.getVoteCount().containsKey("great"));
			assertTrue(vi.getVoteCount().containsKey("hard"));
			assertEquals(vi.getVoteCount().size(), 2);
			assertTrue(vi.getMissCount().isEmpty());
			assertEquals((int) vi.getVoteCount().get("great"), 1);
			assertEquals((int) vi.getVoteCount().get("hard"), 1);
			assertEquals(vi.vote("2", "tested great bad"), "You are 50% right!");
			assertEquals(vi.getVoteCount().size(), 3);
			assertEquals(vi.getMissCount().size(), 1);
			assertEquals((int) vi.getMissCount().get("test"), 1);
			assertEquals((int) vi.getVoteCount().get("great"), 2);
			assertEquals((int) vi.getVoteCount().get("bad"), 1);
			assertEquals((int) vi.getVoteCount().get("hard"), 1);
		} catch (RemoteException e) {
			e.printStackTrace();
			fail("Remote exception");
		}
	}
	
	/**
	 * professor does not provide any keywords. Maps should be empty
	 * and error message should be returned
	 */
	@Test
	public void vote_noValidKeywords_shouldReturnErrorMessageMapsUnchanged() {
		try {
			vi = new OpenEndedVoterServiceImpl(new ArrayList<>(Arrays.asList("question")));
			assertEquals(vi.vote("1", "good great hard"), "No valid keywords available");
			assertTrue(vi.getVoteCount().isEmpty());
			assertTrue(vi.getMissCount().isEmpty());
		} catch (RemoteException e) {
			e.printStackTrace();
			fail("Remote exception");
		}
	}
	
	/**
	 * should return 0th element of the poll as a 1 element string array
	 * @throws java.rmi.RemoteException cannot connect to rmi object
	 */
	@Test
	public void getPoll_shouldReturnCorrectSizeAndElements() throws java.rmi.RemoteException {
		assertEquals(vi.getPoll().length, 1);
		assertArrayEquals(vi.getPoll(), new String[]{"question"});
	}
	/**
	 * isMCQ should return false
	 * @throws java.rmi.RemoteException cannot connect to rmi object
	 */
	@Test
	public void isMCQ_shouldReturnFalse() throws java.rmi.RemoteException {
		assertFalse(vi.isMCQ());
	}

}
