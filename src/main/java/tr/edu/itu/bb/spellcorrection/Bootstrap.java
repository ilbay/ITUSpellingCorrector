package tr.edu.itu.bb.spellcorrection;

import tr.edu.itu.bb.spellcorrection.ahocorasick.AhoCorasick;
import tr.edu.itu.bb.spellcorrection.ahocorasick.SearchResult;
import tr.edu.itu.bb.spellcorrection.levenshtein.*;
import tr.edu.itu.bb.spellcorrection.trie.CandidateSearcher;
import tr.edu.itu.bb.spellcorrection.trie.Trie;
import tr.edu.itu.bb.spellcorrection.util.CharacterUtil;
import tr.edu.itu.bb.spellcorrection.util.NotMisspelledWordException;
import tr.edu.itu.bb.spellcorrection.util.TurkishSyllableGenerator;
import tr.edu.itu.bb.spellcorrection.util.Util;
import tr.edu.itu.bb.spellcorrection.validators.DictionaryTurkishWordValidator;
import tr.edu.itu.bb.spellcorrection.validators.ItuNlpToolsTurkishWordValidator;
import tr.edu.itu.bb.spellcorrection.validators.OdtuCorpusTurkishWordValidator;
import tr.edu.itu.bb.spellcorrection.validators.TurkishWordValidator;
import tr.edu.itu.bb.spellcorrection.validators.ZemberekTurkishWordValidator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

/**
 * $Id$
 *
 * @author $Author$
 * @version $Revision$, $Date$
 */

public final class Bootstrap {

    private final int maxDepth;
    private final int candidateCount;
    private final int windowSize;
    private final String characterFile;
    private final String correctionsFile;
    private TurkishWordValidator turkishWordValidator;
    private Trie vocabularyTrie;
    private boolean TRAIN = true;

    private AhoCorasick<Rule> ahoCorasick;
    private List<Rule> allRules;

    private boolean verbose;
    private long isTurkishRequestCount = 0;

    public enum TurkishValidator{
        Zemberek,
        ItuNlpTools,
        Dictionary
    }

    public Bootstrap(String correctionsFile, String vocabularyFile, String characterFile, int maxDepth, int candidateCount, int windowSize, TurkishValidator turkishValidator) throws Exception {

        this.correctionsFile = correctionsFile;
        this.characterFile = characterFile;
        this.maxDepth = maxDepth;
        this.candidateCount = candidateCount;
        this.windowSize = windowSize;

        CharacterUtil.initCharacterMapping(characterFile);
        this.turkishWordValidator = getTurkishWordValidator(turkishValidator, vocabularyFile);
    }

    private TurkishWordValidator getTurkishWordValidator(TurkishValidator turkishValidator, String vocabularyFile) throws Exception {

        switch (turkishValidator) {

            case Zemberek:

                return new ZemberekTurkishWordValidator();

            case ItuNlpTools:

                return new ItuNlpToolsTurkishWordValidator();

            case Dictionary:

                CharacterUtil.initCharacterMapping(characterFile);
                return new DictionaryTurkishWordValidator(vocabularyFile);

            default:

                throw new IllegalArgumentException();

        }


    }

    public void init() throws Exception {
    	//this.turkishWordValidator = new OdtuCorpusTurkishWordValidator();

    	
        File ahoCorasickTrieFile = new File("data/model/AhoCorasickTrie.dat");
        if(this.TRAIN || !ahoCorasickTrieFile.exists() || ahoCorasickTrieFile.isDirectory())
        {
            this.allRules = buildSortedRules();
            this.ahoCorasick =  buildAhoCorasick(this.allRules);
        	FileOutputStream fileOut = new FileOutputStream(ahoCorasickTrieFile);
        	ObjectOutputStream out = new ObjectOutputStream(fileOut);
        	out.writeObject(this.ahoCorasick);         
        }
        else
        {
        	FileInputStream fileIn = new FileInputStream(ahoCorasickTrieFile);
        	ObjectInputStream in = new ObjectInputStream(fileIn);
        	this.ahoCorasick = (AhoCorasick<Rule>)in.readObject();
        }

        File vocabulayTrieFile = new File("data/model/VocabularyTrie.dat");
        if(this.TRAIN || !vocabulayTrieFile.exists() || vocabulayTrieFile.isDirectory())
        {
        	this.vocabularyTrie = buildVocabularyTrie();
        	FileOutputStream fileOut = new FileOutputStream(vocabulayTrieFile);
        	ObjectOutputStream out = new ObjectOutputStream(fileOut);
        	out.writeObject(this.vocabularyTrie);
        }
        else
        {
        	FileInputStream fileIn = new FileInputStream(vocabulayTrieFile);
        	ObjectInputStream in = new ObjectInputStream(fileIn);
        	this.vocabularyTrie = (Trie)in.readObject();
        }
        
    }

    public boolean isTurkish(String word){
        isTurkishRequestCount++;
        
        if(word.contains("+")) return false;
        
        return turkishWordValidator.isTurkish(word);
    }

    public Rule getRule(String before, String after){

        Rule rule = new Rule(before, after);
        if(allRules.contains(rule)){
            return allRules.get(allRules.indexOf(rule));
        } else {
            return null;
        }
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
    
    public String findCandidates(String misspelled)
    {
    	long start = System.currentTimeMillis();
    	
//    	if(isTurkish(misspelled))
//    	{
//    		return misspelled;
//    	}
    	
    	Iterator<SearchResult<Rule>> result = null;
    	Map<Byte, List<Rule>> rulesAvailable = null;
    	try
    	{
    		result = ahoCorasick.search(misspelled.toCharArray());
        	rulesAvailable = getAvailableRulesAsMap(result);
    	}
    	catch(IllegalArgumentException ex)
    	{
    		return misspelled;
    	}
    	//Key=>Holds position where the rules must be applied to misspelled word

    	//Sort rules
    	Iterator<Map.Entry<Byte, List<Rule>>> it = rulesAvailable.entrySet().iterator();
    	while(it.hasNext())
    	{
    		Map.Entry<Byte, List<Rule>> pairs = (Map.Entry<Byte, List<Rule>>)it.next();
    		Collections.sort((List<Rule>)pairs.getValue());
    	}
        
        TreeSet<CorrectedWord> candidateList = this.findCorrectedWords(rulesAvailable, 1, new Candidate(misspelled));
    	
        if(candidateList.size() == 0)
        {
        	List<String> syllableList = TurkishSyllableGenerator.generateSyllableList(misspelled);
        	StringBuilder firstPart = new StringBuilder();
        	
        	for(int i = 0; i < syllableList.size()-1; ++i)
        	{
        		firstPart.append(syllableList.get(i));
        		String secondPart = misspelled.substring(firstPart.length());
        		if(isTurkish(firstPart.toString()) && isTurkish(secondPart))
        		{
        			return firstPart.toString()+ " " + secondPart;
        		}
        	}
        	
        	return misspelled;
        }
        
    	return candidateList.last().getWord();
    }
    
    private Map<Byte, List<Rule>> getAvailableRulesAsMap(Iterator<SearchResult<Rule>> result) {
    	HashMap<Byte, List<Rule>> rulesAvailable = new HashMap<Byte, List<Rule>>();
    	while(result.hasNext()){
    		SearchResult<Rule> searchResult = result.next();
    		for(Rule rule : searchResult.getOutputs()){
    			byte index = (byte)(searchResult.getLastIndex()-rule.getBefore().length());
    			Rule updatedRule = (Rule)rule.clone();
    			updatedRule.setIndex(index);
    			if(!rulesAvailable.containsKey(index))
    			{
    				rulesAvailable.put(index, new ArrayList<Rule>());
    			}
    			rulesAvailable.get(index).add(updatedRule);
    		}
    	}
    	return rulesAvailable;
    }

    private AhoCorasick<Rule> buildAhoCorasick(List<Rule> rules) {

        long start = System.currentTimeMillis();

        AhoCorasick<Rule> ahoCorasick = new AhoCorasick<Rule>();

        for (Rule rule : rules) {
            ahoCorasick.addWord(rule.getBefore(), rule);
        }

        ahoCorasick.build();

        log("AhoCorasick (" + String.valueOf(rules.size()) + " rules) constructed in " + (System.currentTimeMillis() - start) + " millis");

        return ahoCorasick;

    }
    
    private Trie buildVocabularyTrie() throws IOException {

        long start = System.currentTimeMillis();

        Trie trie = new Trie();

        Locale trLocale = new Locale("tr", "TR");

        List<String> lines = Util.readFile("data/model/tdk-stems.txt", Util.UTF8_ENCODING, true);

        int i = 0;

        for (String line : lines) {

            line = line.trim().toLowerCase(trLocale);
            trie.addWord(line);
            i++;

        }

        System.out.println("Trie (" + String.valueOf(i) + " items) constructed in " + (System.currentTimeMillis() - start) + " millis");

        return trie;

    }

    private List<Rule> buildSortedRules() throws IOException {

        long start = System.currentTimeMillis();

        Map<Rule, Integer> ruleCounts = new HashMap<Rule, Integer>();

        Locale trLocale = new Locale("tr", "TR");

        List<String> lines = Util.readFile(correctionsFile, Util.UTF8_ENCODING, true);

        int maxCount = Integer.MIN_VALUE;

        int i = 0;
        for (String line : lines) {

            line = line.trim().toLowerCase(trLocale);

            String[] parts = line.split("=>");

            //Buradaki windowSize, Wang[0]'in makalesinde kurallar oluşturulurken kullanılan sınır değerlerini belirtiyor.
            List<Transformation> transformations = LevenshteinDistance.computeLevenshteinDistance(parts[0], parts[1], windowSize);

            for (Transformation transformation : transformations) {

                if (transformation.getTransformationType() != TransformationType.NO_CHANGE) {

                    for (Rule rule : transformation.getRules()) {

                        int count = 1;
                        if (ruleCounts.containsKey(rule)) {
                            count += ruleCounts.get(rule);
                        }

                        ruleCounts.put(rule, count);

                        if(count > maxCount){
                            maxCount = count;
                        }
                    }


                }

            }

            i++;

        }

        log("Levenshtein distances (" + String.valueOf(i) + " pairs) estimated in " + (System.currentTimeMillis() - start) + " millis");

        return setLikelihoods(ruleCounts, maxCount);

    }

    private List<Rule> setLikelihoods(Map<Rule, Integer> ruleCounts, int maxCount) {

        for (Rule rule : ruleCounts.keySet()) {

            int ruleCount = ruleCounts.get(rule);

            rule.setCount(ruleCount);
            rule.setLikelihood(((double)ruleCount / (double)maxCount) - 1d);//normalize likelihood between 0 and 1 and then make it negative

        }

        List<Rule> rules = new ArrayList<Rule>(ruleCounts.keySet());

        Collections.sort(rules);

        return rules;

    }
    
    private TreeSet<CorrectedWord> findCorrectedWords(Map<Byte, List<Rule>> rulesAvailable, int maxCandidate, Candidate candidateWord){
        
    	/**
    	 * Finds root candidates
    	 */
    	CandidateSearcher searcher = new CandidateSearcher(candidateWord, this.vocabularyTrie, rulesAvailable);
        List<WordInformation> candidateWordList = searcher.buildCandidateList();
        
        TreeSet<CorrectedWord> correctedWordSet = new TreeSet<>();
        
        for(WordInformation word : candidateWordList)
        {
        	String suffix = word.getSuffix();
        	int rootLength = candidateWord.getCandidateWord().length() - suffix.length();
        	if(word.getSuffix().equals(""))
        	{
    			if((maxCandidate == correctedWordSet.size() && correctedWordSet.first().getTotalWeight() < word.getTotalWeight()) || maxCandidate > correctedWordSet.size())
    			{
    				if(isTurkish(word.getRoot()))
    				{
    					if(maxCandidate == correctedWordSet.size())
    						correctedWordSet.remove(correctedWordSet.first());
        				correctedWordSet.add(new CorrectedWord(word.getRoot(), word.getTotalWeight()));
    				}
    			}
    			else
    			{
    				break;
    			}
        	}else if(isTurkish(word.getRoot()+word.getSuffix()))
        	{
        		correctedWordSet.add(new CorrectedWord(word.getRoot()+word.getSuffix(), word.getTotalWeight()));
        	}
        	
        	for(int i = word.getIndex(); i < suffix.length(); i++)
        	{
        		List<Rule> ruleList = rulesAvailable.get((byte)i);
    			int index = i - rootLength;
    			
    			if(ruleList == null)
    			{
    				continue;
    			}
    			
        		for(Rule rule : ruleList)
        		{
        			String newSuffix = "";
        			try
        			{
        				newSuffix = suffix.substring(0, index) + rule.getAfter() + suffix.substring(index + rule.getBefore().length());
        			}
        			catch(java.lang.StringIndexOutOfBoundsException e)
        			{
        				e.printStackTrace();
        			}
        			String newWord = word.getRoot() + newSuffix;
        			double newTotalWeight = word.getTotalWeight() + rule.getLikelihood();
        			if((maxCandidate == correctedWordSet.size() && correctedWordSet.first().getTotalWeight() < newTotalWeight) || maxCandidate > correctedWordSet.size())
        			{
        				if(isTurkish(newWord))
        				{
        					if(maxCandidate == correctedWordSet.size())
        						correctedWordSet.remove(correctedWordSet.first());
            				correctedWordSet.add(new CorrectedWord(newWord, newTotalWeight));
        				}
        			}
        			else
        			{
        				break;
        			}
        		}
        	}
        	
        }
                
        return correctedWordSet;
    }

    private void log(String log){
        if(verbose){
            System.out.println(log);
        }
    }

    public long getTurkishRequestCount() {
        return isTurkishRequestCount;
    }
}
