package tr.edu.itu.bb.spellcorrection;

import tr.edu.itu.bb.spellcorrection.ahocorasick.AhoCorasick;
import tr.edu.itu.bb.spellcorrection.ahocorasick.SearchResult;
import tr.edu.itu.bb.spellcorrection.levenshtein.*;
import tr.edu.itu.bb.spellcorrection.trie.CandidateSearcher;
import tr.edu.itu.bb.spellcorrection.trie.Trie;
import tr.edu.itu.bb.spellcorrection.util.CharacterUtil;
import tr.edu.itu.bb.spellcorrection.util.NotMisspelledWordException;
import tr.edu.itu.bb.spellcorrection.util.Util;
import tr.edu.itu.bb.spellcorrection.validators.DictionaryTurkishWordValidator;
import tr.edu.itu.bb.spellcorrection.validators.ItuNlpToolsTurkishWordValidator;
import tr.edu.itu.bb.spellcorrection.validators.TurkishWordValidator;
import tr.edu.itu.bb.spellcorrection.validators.ZemberekTurkishWordValidator;

import java.io.IOException;
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
    private final TurkishWordValidator turkishWordValidator;
    private Trie vocabularyTrie;

    private AhoCorasick<Rule> ahoCorasick;
    private List<Rule> allRules;
    private List<Rule> addOneCharRules;

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
        this.turkishWordValidator = getTurkishWordValidator(turkishValidator, vocabularyFile);

        CharacterUtil.initCharacterMapping(characterFile);

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

        this.allRules = buildSortedRules();
        this.addOneCharRules = getAddOneCharRules(this.allRules); //bir karakter ekleme kuralları
        this.ahoCorasick =  buildAhoCorasick(this.allRules);
        this.vocabularyTrie = buildVocabularyTrie();
    }

    private List<Rule> getAddOneCharRules(List<Rule> rules) {

        List<Rule> addOneCharRules = new ArrayList<Rule>();

        for (Rule rule : rules) {

            if(rule.getBefore().equals("")){
                addOneCharRules.add(rule);
            }

        }

        return addOneCharRules;

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
    
    public List<Candidate> findCandidates2(String misspelled)
    {
    	long start = System.currentTimeMillis();
    	Iterator<SearchResult<Rule>> result = ahoCorasick.search(misspelled.toCharArray());
    	
    	//Key=>Holds position where the rules must be applied to misspelled word
    	Map<Byte, List<Rule>> rulesAvailable = getAvailableRulesAsMap(result);

    	//Sort rules
    	Iterator<Map.Entry<Byte, List<Rule>>> it = rulesAvailable.entrySet().iterator();
    	while(it.hasNext())
    	{
    		Map.Entry<Byte, List<Rule>> pairs = (Map.Entry<Byte, List<Rule>>)it.next();
    		Collections.sort((List<Rule>)pairs.getValue());
    	}
    	
        List<CandidateWord> correctedWords = new ArrayList<CandidateWord>();
        
        this.findCorrectedWords2(rulesAvailable, 10, new CandidateWord(misspelled), correctedWords);
    	
    	return null;
    }

    public List<Candidate> findCandidates(String misspelled) {

        long start = System.currentTimeMillis();

        Iterator<SearchResult<Rule>> result = ahoCorasick.search(misspelled.toCharArray());

        List<Rule> rulesAvailable = getAvailableRules(result);
        
        /*
        * TODO: tek harf eklemeli kurallarin weight i cok fazla oldugu icin tum kurallarin onune geciyor.
        * Bizim hatalarda en cok bu tarz hatalar oldugu icin en olasi kural olarak bunlari buluyor.
        * Eger weight ini azaltabilirsek daha basarili sonuc verebilir!
        * */
        rulesAvailable.addAll(addOneCharRules);

        Collections.sort(rulesAvailable);

        List<Candidate> correctedWords = new ArrayList<Candidate>();
        
        findCorrectedWords(rulesAvailable, 0, Candidate.fromMisspelled(misspelled), correctedWords);
        
        System.out.println(isTurkishRequestCount);

        log("Correction finished in " + (System.currentTimeMillis() - start) + " millis");
        
        if(candidateCount != -1 && correctedWords.size() > candidateCount){
            return correctedWords.subList(0, candidateCount);
        } else {
            return correctedWords;
        }

    }

    private List<Rule> getAvailableRules(Iterator<SearchResult<Rule>> result) {

        List<Rule> rulesAvailable = new ArrayList<Rule>();

        while (result.hasNext()){

            SearchResult<Rule> searchResult = result.next();

            for (Rule rule : searchResult.getOutputs()) {
            	rule.setIndex(searchResult.getLastIndex() - rule.getBefore().length());
                rulesAvailable.add(rule);
            }

        }

        return rulesAvailable;

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
    
    private void findCorrectedWords2(Map<Byte, List<Rule>> rulesAvailable, int depth, CandidateWord candidateWord, List<CandidateWord> correctedWords){
        
    	/**
    	 * Finds root candidates
    	 */
    	CandidateSearcher searcher = new CandidateSearcher(candidateWord, this.vocabularyTrie, rulesAvailable);
        List<WordInformation> candidateWordList = searcher.buildCandidateList(10);
        System.out.println("Root Candidate Size: " + candidateWordList.size());
        
        TreeSet<CorrectedWord> correctedWordSet = new TreeSet<>();
        
        for(WordInformation word : candidateWordList)
        {
        	String suffix = word.getSuffix();
        	int rootLength = candidateWord.getCandidateWord().length() - suffix.length();
        	if(word.getSuffix().equals(""))
        	{
    			if((depth == correctedWordSet.size() && correctedWordSet.first().getTotalWeight() < word.getTotalWeight()) || depth > correctedWordSet.size())
    			{
    				if(isTurkish(word.getRoot()))
    				{
    					if(depth == correctedWordSet.size())
    						correctedWordSet.remove(correctedWordSet.first());
        				correctedWordSet.add(new CorrectedWord(word.getRoot(), word.getTotalWeight()));
    				}
    			}
    			else
    			{
    				break;
    			}
        	}
        	
        	for(int i = word.getIndex(); i < suffix.length(); i++)
        	{
        		List<Rule> ruleList = rulesAvailable.get((byte)i);
    			int index = i - rootLength;
    			
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
        			if((depth == correctedWordSet.size() && correctedWordSet.first().getTotalWeight() < newTotalWeight) || depth > correctedWordSet.size())
        			{
        				if(isTurkish(newWord))
        				{
        					if(depth == correctedWordSet.size())
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
        
        for(CorrectedWord cw : correctedWordSet)
        {
        	System.out.println(cw);
        }
    }

    private void findCorrectedWords(List<Rule> rulesAvailable, int depth, Candidate fromCandidate, List<Candidate> correctedWords){

        depth++;
        
        for (Rule rule : rulesAvailable) {

            List<Candidate> candidates = fromCandidate.buildCandidates(rule);

            for (Candidate candidate : candidates) {
            	System.out.println( "Imma try this now: " + candidate.getCandidateWord() );
                if(isTurkish(candidate.getCandidateWord())){

                    if(correctedWords.contains(candidate)){

                        Candidate priorCandidate = correctedWords.get(correctedWords.indexOf(candidate));

                        if(candidate.getTotalWeight() > priorCandidate.getTotalWeight()){

                            correctedWords.remove(priorCandidate);
                            correctedWords.add(candidate);
                            Collections.sort(correctedWords);

                        }

                    } else {

                        correctedWords.add(candidate);
                        Collections.sort(correctedWords);

                    }

                }

                /*
                * if max depth is exceeded, do not continue applying more rules
                * */
                if(depth < maxDepth){

                    /*
                    * if current candidates weight is less then the min weight in list,
                    * no need to continue applying other rules on that candidate
                    * */
                    if(canAddMoreCandidates(correctedWords, candidate)){

                        findCorrectedWords(rulesAvailable, depth, candidate, correctedWords);

                    }

                }

            }

            /*
            * if we add next rule weight to fromCandidate and can not take place in top k,
            * so no need to continue looping rules
            * */
            if(!nextRulesHaveChance(correctedWords, fromCandidate, rule.getLikelihood())){
                return;
            }

        }

    }

    private boolean nextRulesHaveChance(List<Candidate> correctedWords, Candidate fromCandidate, double likelihood) {

        if(candidateCount == -1 || correctedWords.size() < candidateCount){

            return true;

        } else {

            double threshold = correctedWords.get(candidateCount - 1).getTotalWeight();
            double minWeight = fromCandidate.getTotalWeight() + likelihood;

            if(threshold < minWeight){

                return true;

            } else {

                return false;

            }

        }

    }

    private boolean canAddMoreCandidates(List<Candidate> correctedWords, Candidate candidate) {

        if(candidateCount == -1 || correctedWords.size() < candidateCount){

            return true;

        } else {

            double threshold = correctedWords.get(candidateCount - 1).getTotalWeight();

            return candidate.getTotalWeight() >= threshold;

        }

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
