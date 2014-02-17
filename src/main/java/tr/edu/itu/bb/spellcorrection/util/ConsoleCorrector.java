package tr.edu.itu.bb.spellcorrection.util;

import tr.edu.itu.bb.spellcorrection.Bootstrap;
import tr.edu.itu.bb.spellcorrection.levenshtein.Candidate;
import tr.edu.itu.bb.spellcorrection.levenshtein.Rule;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

/**
 * $Id$
 *
 * @author $Author$
 * @version $Revision$, $Date$
 */

public final class ConsoleCorrector {

    public static void main(String[] args) throws Exception {

        if(args.length != 7){

            System.out.println("Usage:");
            System.out.println("ConsoleCorrector <character-file-path> <vocabulary-file-path> <corrections-file-path> <max-candidates> <search-depth> <window-size> <use-zemberek-as-validator>");
            System.out.println("ConsoleCorrector \"data/model/characters.txt\" \"data/model/tdk-stems.txt\" \"data/model/corrections.txt\" 3 2 3 true");


        } else {

            String characterFile = args[0];
            String vocabularyFile = args[1];
            String correctionsFile = args[2];
            int maxCandidates = Integer.valueOf(args[3]);
            int depth = Integer.valueOf(args[4]);
            int windowSize = Integer.valueOf(args[5]);
            boolean useZemberekAsValidator= Boolean.valueOf(args[6]);

            Bootstrap bootstrap = new Bootstrap(correctionsFile, vocabularyFile, characterFile, depth, maxCandidates, windowSize, useZemberekAsValidator ? Bootstrap.TurkishValidator.Zemberek : Bootstrap.TurkishValidator.Dictionary);
            bootstrap.setVerbose(true);
            bootstrap.init();

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, "UTF8"));

            System.out.println("\nEnter a misspelled word or enter to exit:");
            String word = reader.readLine();

            while(word != null && word.length() > 0){

                if(word.startsWith("?")){

                    String[] parts = word.substring(1).split("->");
                    Rule rule = bootstrap.getRule(parts[0], parts[1]);

                    if(rule != null){

                        System.out.println(rule);

                    } else {

                        System.out.println("Rule not found!");

                    }

                    System.out.println("Enter a misspelled word or ?<rule> or enter to exit:");
                    word = reader.readLine();

                } else {

                    if(bootstrap.isTurkish(word)){

                        System.out.println("\n\"" + word + "\" is already in vocabulary.");
                        System.out.println("Enter a misspelled word or ?<rule> or enter to exit:");
                        word = reader.readLine();

                    } else if(useZemberekAsValidator && ZemberekUtil.canBeParsed(word)){

                        System.out.println("\n\"" + word + "\" can be postagged.");
                        System.out.println("Enter a misspelled word or ?<rule> or enter to exit:");
                        word = reader.readLine();

                    } else {

                        String candidates = bootstrap.findCandidates(word);
                        System.out.println(candidates);

                        System.out.println("\nEnter a misspelled word or ?<rule> or enter to exit:");
                        word = reader.readLine();

                    }

                }

            }

        }

    }

    private static void print(List<Candidate> candidates) {

        for (Candidate correctedWord : candidates) {

            StringBuilder sb = new StringBuilder();

            sb.append("Found: \"").append(correctedWord.getCandidateWord()).append("\" ");
            sb.append("Total weight: ").append(String.format("%.6f", correctedWord.getTotalWeight()));

            for (Rule rule : correctedWord.getAppliedRules()) {
                sb.append("\n\t\"").append(rule.getBefore()).append("\" -> \"").append(rule.getAfter()).append("\", weight: ").append(String.format("%.6f", rule.getLikelihood()));
            }

            System.out.println(sb.toString());
        }

    }
}
