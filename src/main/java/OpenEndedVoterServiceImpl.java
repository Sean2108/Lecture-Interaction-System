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

/** This is the main class of the server */
public class OpenEndedVoterServiceImpl extends UnicastRemoteObject implements VoterService {
	public final static String SERVICENAME="VoteService";
	Map<String, Integer> hitCount = new HashMap<String, Integer>();
	Map<String, Integer> missCount = new HashMap<String, Integer>();
	ArrayList<String> voteOnce = new ArrayList<String>();
	Set<String> keywords = new HashSet<>();
	String question;
	SentenceDetectorME sentenceDetector = null;
	Tokenizer tokenizer = null;
	POSTaggerME tagger = null;
	DictionaryLemmatizer lemmatizer = null;
	
	public OpenEndedVoterServiceImpl() throws RemoteException {
		super();	// sets up networking
		initModels();
	}
	
	public OpenEndedVoterServiceImpl(List<String> keywords) throws RemoteException {
		super();	// sets up networking
		this.question = keywords.get(0);
		keywords.remove(0);
		initModels();
		this.keywords = parseKeywords(keywords);
	}
	
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
	
	public synchronized String vote(String studentId, String vote) throws java.rmi.RemoteException {
		if (voteOnce.contains(studentId)) return "You already voted!";
		return "You are " + (parse(vote) * 100 / (double) keywords.size()) + "% right!";
	}
			
	
	public String[] getPoll() throws java.rmi.RemoteException {
		return new String[]{question};
	}
	
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
				if (lemmas[i].equals("O") && keywords.contains(tokens[i])) {
					count++;
					seen.add(tokens[i]);
					hitCount.put(tokens[i], hitCount.getOrDefault(tokens[i], 0) + 1);
				}
				else if (!lemmas[i].equals("O")) {		
					if (keywords.contains(lemmas[i])) {
						count++;
						seen.add(lemmas[i]);
						hitCount.put(lemmas[i], hitCount.getOrDefault(lemmas[i], 0) + 1);
					}
					else {
//						TO DO: filter adverbs, pronouns, etc?
						missCount.put(lemmas[i], missCount.getOrDefault(lemmas[i], 0) + 1);
					}
				}
			}
		}
		return count;
	}
	
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
}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
	


		
