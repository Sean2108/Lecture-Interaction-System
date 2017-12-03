import static org.junit.Assert.*;

import java.rmi.RemoteException;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

/**
 * unit test for MCQVoterServiceImpl class
 * @author Sean
 *
 */
public class MCQVoterServiceTest {

	private VoterService vi;
	
	@Before
	public void setUp() throws Exception {
		vi = new MCQVoterServiceImpl(Arrays.asList("question", "ansA", "ansB", "ansC", "ansD"));
	}

	/**
	 * voter 1 votes for a. map should have 1 key, where a -> 1.
	 * voter 1 then votes again for a. map should have 1 key, 
	 * where a -> 1 as repeat votes are rejected
	 */
	@Test
	public void vote_VoteTwice_shouldReturnErrorMessageMapUnchanged() {
		try {
			assertEquals(vi.vote("1", "a"), "You voted for a");
			assertTrue(vi.getVoteCount().containsKey("a"));
			assertEquals((int) vi.getVoteCount().size(), 1);
			assertEquals((int) vi.getVoteCount().get("a"), 1);
			assertEquals(vi.vote("1", "a"), "You already voted!");
			assertEquals((int) vi.getVoteCount().size(), 1);
			assertEquals((int) vi.getVoteCount().get("a"), 1);
		} catch (RemoteException e) {
			e.printStackTrace();
			fail("Remote exception");
		}
	}

	/**
	 * voter 1 votes for a. map should have 1 key, where a -> 1.
	 * voter 2 then votes for a. map should have 1 key, where a -> 2.
	 */
	@Test
	public void vote_2VotersVoteForSameOption_shouldContain2VotesForA() {
		try {
			assertEquals(vi.vote("1", "a"), "You voted for a");
			assertTrue(vi.getVoteCount().containsKey("a"));
			assertEquals(vi.getVoteCount().size(), 1);
			assertEquals((int) vi.getVoteCount().get("a"), 1);
			assertEquals(vi.vote("2", "a"), "You voted for a");
			assertEquals(vi.getVoteCount().size(), 1);
			assertEquals((int) vi.getVoteCount().get("a"), 2);
		} catch (RemoteException e) {
			e.printStackTrace();
			fail("Remote exception");
		}
	}
	
	/**
	 * voter 1 votes for a. map should have 1 key, where a -> 1.
	 * voter 2 then votes for b. map should have 2 keys, where a -> 1 and b -> 1.
	 */
	@Test
	public void vote_2VotersVoteForDifferentOption_shouldContain1VoteForEachOption() {
		try {
			assertEquals(vi.vote("1", "a"), "You voted for a");
			assertTrue(vi.getVoteCount().containsKey("a"));
			assertEquals(vi.getVoteCount().size(), 1);
			assertEquals((int) vi.getVoteCount().get("a"), 1);
			assertEquals(vi.vote("2", "b"), "You voted for b");
			assertEquals(vi.getVoteCount().size(), 2);
			assertEquals((int) vi.getVoteCount().get("a"), 1);
			assertEquals((int) vi.getVoteCount().get("b"), 1);
		} catch (RemoteException e) {
			e.printStackTrace();
			fail("Remote exception");
		}
	}
	
	/**
	 * should return correct poll values as a String array
	 * @throws java.rmi.RemoteException cannot find rmi object
	 */
	@Test
	public void getPoll_shouldReturnCorrectSizeAndElements() throws java.rmi.RemoteException {
		assertEquals(vi.getPoll().length, 5);
		assertArrayEquals(vi.getPoll(), new String[]{"question", "ansA", "ansB", "ansC", "ansD"});
	}
	
	/**
	 * checks if isMCQ method returns correct value of true
	 * @throws java.rmi.RemoteException cannot find rmi object
	 */
	@Test
	public void isMCQ_shouldReturnTrue() throws java.rmi.RemoteException {
		assertTrue(vi.isMCQ());
	}
}
