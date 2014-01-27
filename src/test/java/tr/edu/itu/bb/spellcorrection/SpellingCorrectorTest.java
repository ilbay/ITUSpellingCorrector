package tr.edu.itu.bb.spellcorrection;

import org.junit.Test;
import tr.edu.itu.bb.spellcorrection.ahocorasick.AhoCorasick;
import tr.edu.itu.bb.spellcorrection.util.CharacterUtil;
import tr.edu.itu.bb.spellcorrection.ahocorasick.SearchResult;
import tr.edu.itu.bb.spellcorrection.levenshtein.*;
import tr.edu.itu.bb.spellcorrection.trie.Trie;
import tr.edu.itu.bb.spellcorrection.util.Util;

import java.io.*;
import java.util.*;

/**
 * User: eren
 * Date: 5/5/13
 * Time: 5:35 PM
 */
public class SpellingCorrectorTest {

    private static final int MAX_DEPTH = 2;
    private static final int WINDOW_SIZE = 3;
    
    public static void main(String[] args) throws Exception
    {
    	SpellingCorrectorTest test = new SpellingCorrectorTest();
    	test.testSpellingCorrector();
    }

    @Test
    public void testSpellingCorrector() throws IOException {

        String misspelledWord = "arablarda";

        long start = System.currentTimeMillis();

        init();

        Trie trie = buildVocabularyTrie();
        
        System.out.println(trie.contains("teşbih"));
        
        System.out.println(trie.getRootWords("teşbihte"));

        List<Rule> rules = buildSortedRules();

        AhoCorasick<Rule> ahoCorasick = buildAhoCorasick(rules);

        System.out.println("Spelling corrector constructed in " + (System.currentTimeMillis() - start) + " millis");
        System.out.println();

        start = System.currentTimeMillis();

        Iterator<SearchResult<Rule>> result = ahoCorasick.search(misspelledWord.toCharArray());

        List<Rule> rulesAvailable = new ArrayList<Rule>();

        while (result.hasNext()){

            SearchResult<Rule> searchResult = result.next();

            for (Rule rule : searchResult.getOutputs()) {
                rulesAvailable.add(rule);
            }

        }

        Collections.sort(rulesAvailable);

        System.out.println(rulesAvailable.size() + " rules found!");

        List<Candidate> correctedWords = new ArrayList<Candidate>();
        findCorrectedWords(rulesAvailable, trie, 0, Candidate.fromMisspelled(misspelledWord), correctedWords);

        System.out.println("Correction finished in " + (System.currentTimeMillis() - start) + " millis");

        Collections.sort(correctedWords);

        for (Candidate correctedWord : correctedWords) {
            System.out.println(correctedWord);
        }

    }

    private void findCorrectedWords(List<Rule> rulesAvailable, Trie trie, int depth, Candidate fromCandidate, List<Candidate> correctedWords){

        depth++;

        for (Rule rule : rulesAvailable) {

            List<Candidate> candidates = fromCandidate.buildCandidates(rule);

            for (Candidate candidate : candidates) {

                if(trie.contains(candidate.getCandidateWord())){

                    if(correctedWords.contains(candidate)){

                        Candidate priorCandidate = correctedWords.get(correctedWords.indexOf(candidate));

                        if(candidate.getTotalWeight() > priorCandidate.getTotalWeight()){

                            correctedWords.remove(priorCandidate);
                            correctedWords.add(candidate);

                        }

                    } else {

                        correctedWords.add(candidate);

                    }

                }

                if(depth < MAX_DEPTH){

                    findCorrectedWords(rulesAvailable, trie, depth, candidate, correctedWords);

                }

            }

        }

    }

    private void init() throws IOException {

        CharacterUtil.initCharacterMapping("data/model/characters.txt");

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

    private AhoCorasick<Rule> buildAhoCorasick(List<Rule> rules) {

        long start = System.currentTimeMillis();

        AhoCorasick<Rule> ahoCorasick = new AhoCorasick<Rule>();

        for (Rule rule : rules) {
            ahoCorasick.addWord(rule.getBefore(), rule);
        }

        ahoCorasick.build();

        System.out.println("AhoCorasick (" + String.valueOf(rules.size()) + " rules) constructed in " + (System.currentTimeMillis() - start) + " millis");

        return ahoCorasick;

    }

    private List<Rule> buildSortedRules() throws IOException {

        long start = System.currentTimeMillis();

        Map<Rule, Integer> ruleCounts = new HashMap<Rule, Integer>();

        Locale trLocale = new Locale("tr", "TR");

        List<String> lines = Util.readFile("data/model/corrections.txt", Util.UTF8_ENCODING, true);
        int i = 0;

        for (String line : lines) {

            line = line.trim().toLowerCase(trLocale);

            String[] parts = line.split("=>");

            List<Transformation> transformations = LevenshteinDistance.computeLevenshteinDistance(parts[0], parts[1], WINDOW_SIZE);

            for (Transformation transformation : transformations) {

                if (transformation.getTransformationType() != TransformationType.NO_CHANGE) {

                    for (Rule rule : transformation.getRules()) {

                        int count = 1;
                        if (ruleCounts.containsKey(rule)) {
                            count += ruleCounts.get(rule);
                        }

                        ruleCounts.put(rule, count);

                    }


                }

            }

            i++;

        }


        System.out.println("Levenshtein distances (" + String.valueOf(i) + " pairs) estimated in " + (System.currentTimeMillis() - start) + " millis");

        return setLikelihoods(ruleCounts);

    }

    private List<Rule> setLikelihoods(Map<Rule, Integer> ruleCounts) {

        int maxCount = getMaxCount(ruleCounts);

        for (Rule rule : ruleCounts.keySet()) {

            int ruleCount = ruleCounts.get(rule);

            rule.setCount(ruleCount);
            rule.setLikelihood(((double)ruleCount / (double)maxCount) - 1d);//normalize likelihood between 0 and 1 and then make it negative

        }

        List<Rule> rules = new ArrayList<Rule>(ruleCounts.keySet());

        Collections.sort(rules);

        return rules;

    }

    private int getMaxCount(Map<Rule, Integer> ruleCounts) {

        int maxCount = Integer.MIN_VALUE;

        for (Rule rule : ruleCounts.keySet()) {

            int count = ruleCounts.get(rule);
            if(count > maxCount) {
                maxCount = count;
            }

        }

        return maxCount;

    }

}
