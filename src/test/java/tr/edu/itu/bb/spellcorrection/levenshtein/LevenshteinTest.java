package tr.edu.itu.bb.spellcorrection.levenshtein;

import org.junit.Test;
import tr.edu.itu.bb.spellcorrection.util.CharacterUtil;
import tr.edu.itu.bb.spellcorrection.util.Util;

import java.io.*;
import java.util.*;

/**
 * User: eren
 * Date: 5/2/13
 * Time: 10:04 PM
 */
public class LevenshteinTest {

    @Test
    public void testLevenshtein() throws IOException {


        long start = System.currentTimeMillis();

        Map<Rule, Integer> ruleCounts = new HashMap<Rule, Integer>();

        CharacterUtil.initCharacterMapping("data/model/characters.txt");

        Locale trLocale = new Locale("tr", "TR");

        List<String> lines = Util.readFile("data/model/corrections.txt", Util.UTF8_ENCODING, true);

        int i = 0;

        for (String line : lines) {

            line = line.trim().toLowerCase(trLocale);

            String[] parts = line.split("=>");

            List<Transformation> transformations = LevenshteinDistance.computeLevenshteinDistance(parts[0], parts[1], 3);

            for (Transformation transformation : transformations) {

                if(transformation.getTransformationType() != TransformationType.NO_CHANGE){

                    for (Rule rule : transformation.getRules()) {

                        int count = 1;
                        if(ruleCounts.containsKey(rule)){
                            count += ruleCounts.get(rule);
                        }

                        ruleCounts.put(rule, count);

                    }


                }

            }

            i++;
        }

        System.out.println(i + " levenshtein distances estimated in " + (System.currentTimeMillis() - start) + " millis");
        System.out.println("unique transformations: " + ruleCounts.size());

        for (Rule rule : ruleCounts.keySet()) {
            System.out.println(rule.getBefore() + "->" + rule.getAfter() + " found " + ruleCounts.get(rule) + " times");
        }

    }

    private void printRules(List<Transformation> transformations){


        int cost = 0;

        StringBuilder sb = new StringBuilder();

        for (Transformation filteredTransformation : transformations) {

            sb.append(filteredTransformation.getChange());
            cost += filteredTransformation.getTransformationType().getCost();

        }

        System.out.println("cost: " + cost + " for " + sb.toString());

    }

}
