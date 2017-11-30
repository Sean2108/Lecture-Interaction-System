import java.io.*;
import java.rmi.*;
import java.rmi.server.*;
import java.util.*;

import opennlp.tools.lemmatizer.DictionaryLemmatizer;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

/**
 * implementation to handle open ended questions
 * @author sean
 *
 */
public class OpenEndedVoterServiceImpl extends UnicastRemoteObject implements VoterService {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8524186864175925764L;
	public final static String SERVICENAME="VoteService";
	private Map<String, Integer> hitCount = new HashMap<String, Integer>();
	private Map<String, Integer> missCount = new HashMap<String, Integer>();
	private ArrayList<String> voteOnce = new ArrayList<String>();
	private Set<String> keywords = new HashSet<>();
	private String question;
	private SentenceDetectorME sentenceDetector = null;
	private Tokenizer tokenizer = null;
	private POSTaggerME tagger = null;
	private DictionaryLemmatizer lemmatizer = null;
	
	/**
	 * empty constructor, initializes models and feeds them training data
	 * @throws RemoteException
	 */
	public OpenEndedVoterServiceImpl() throws RemoteException {
		super();	// sets up networking
		initModels();
	}
	
	/**
	 * initializes models, separates question from keywords and calls parse on the keywords
	 * @param keywords index 0 is the question, the rest are the keywords that the professor is looking for
	 * @throws RemoteException thrown when the remote objects cannot be found
	 */
	public OpenEndedVoterServiceImpl(List<String> keywords) throws RemoteException {
		this();
		this.question = keywords.get(0);
		keywords.remove(0);
		this.keywords = parseKeywords(keywords);
	}
	
	/**
	 * initializes models and trains them with pre-created data
	 */
	private void initModels() {
		try {
			InputStream modelIn = new FileInputStream("src/main/resources/en-sent.bin");
			SentenceModel sModel = new SentenceModel(modelIn);
			sentenceDetector = new SentenceDetectorME(sModel);
			modelIn = new FileInputStream("src/main/resources/en-token.bin");
			TokenizerModel tModel = new TokenizerModel(modelIn);
			tokenizer = new TokenizerME(tModel);
			modelIn = new FileInputStream("src/main/resources/en-pos-maxent.bin");
		    POSModel posModel = new POSModel(modelIn);
		    tagger = new POSTaggerME(posModel);
		    modelIn = new FileInputStream("src/main/resources/en-lemmatizer.dict");
		    lemmatizer = new DictionaryLemmatizer(modelIn);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * checks if student has already answered. If not, calculate the correctness of his answer.
	 */
	public synchronized String vote(String studentId, String vote) throws java.rmi.RemoteException {
		if (voteOnce.contains(studentId)) return "You already answered!";
		if (!keywords.isEmpty()) return "You are " + Math.round(parse(vote) * 100 / (double) keywords.size()) + "% right!";
		else return "No valid keywords available";
	}
			
	/**
	 * returns an array with the question in it.
	 */
	public String[] getPoll() throws java.rmi.RemoteException {
		return new String[]{question};
	}
	
	/**
	 * parse student's answer, counts number of hits and adds hits and misses to the aggregated maps.\
	 * firstly splits answer into sentences, tokenizes student's answer, 
	 * then tags each answer to a type of word (eg verb, noun, adjective etc)
	 * and then lemmatizes it to its root word (eg drives / driven / drove -> drive)
	 * @param ans student's answer
	 * @return number of correct keywords that the student has in his answer.
	 */
	private int parse(String ans) {
		String sentences[] = sentenceDetector.sentDetect(ans);
		int count = 0;
		Set<String> seen = new HashSet<>();
		for (String sentence : sentences) {
			String tokens[] = tokenizer.tokenize(sentence);
			String tags[] = tagger.tag(tokens);
			String[] lemmas = lemmatizer.lemmatize(tokens, tags);
			for (int i = 0; i < lemmas.length; i++) {
				if (seen.contains(tokens[i]) || seen.contains(lemmas[i])) continue;
				if (isKeyword(seen, tokens[i], tags[i], lemmas[i])) count++;
			}
		}
		return count;
	}
	
	/**
	 * checks if a word in the student's answer is present in the set of keywords provided by professor.
	 * called once for each word in the student's answer.
	 * @param seen whether the word is repeated. if it is repeated, skip it.
	 * @param token the raw word that the student provides.
	 * @param tag type of word.
	 * @param lemma the root word of the token.
	 * @return true if it is in the professor's keywords, else false.
	 */
	private boolean isKeyword(Set<String> seen, String token, String tag, String lemma) {
		if (lemma.equals("O") && keywords.contains(token)) {
			seen.add(token);
			hitCount.put(token, hitCount.getOrDefault(token, 0) + 1);
			return true;
		}
		if (!lemma.equals("O")) {		
			if (keywords.contains(lemma)) {
				seen.add(lemma);
				hitCount.put(lemma, hitCount.getOrDefault(lemma, 0) + 1);
				return true;
			}
			else if (tag.startsWith("N") || tag.startsWith("J") || tag.startsWith("V")) {
//				TO DO: filter adverbs, pronouns, etc?
				missCount.put(lemma, missCount.getOrDefault(lemma, 0) + 1);
			}
		}
		return false;
	}
	
	/**
	 * parses the professor's keywords into their lemmas to make comparison easier.
	 * @param keywords list of keywords provided by professor.
	 * @return set of lemmas of the professor's keywords.
	 */
	private Set<String> parseKeywords(List<String> keywords) {
		String tokens[] = keywords.toArray(new String[keywords.size()]);
		String tags[] = tagger.tag(tokens);
		String[] lemmas = lemmatizer.lemmatize(tokens, tags);
		Set<String> keywordSet = new HashSet<>();
		for (int i = 0; i < lemmas.length; i++) {
			if (lemmas[i].equals("O")) keywordSet.add(tokens[i]);
			else keywordSet.add(lemmas[i]);
		}
		return keywordSet;
	}
	
	/**
	 * getter method to allow voter service to view map of correct keywords.
	 */
	public Map<String, Integer> getVoteCount() {
		return hitCount;
	}
	
	/**
	 * getter method to allow voter service to view map of wrong words.
	 */
	public Map<String, Integer> getMissCount() {
		return missCount;
	}
	
	/**
	 * method to identify the implementation. returns false as it is not MCQ
	 */
	public boolean isMCQ() {
		return false;
	}
}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
	


		
