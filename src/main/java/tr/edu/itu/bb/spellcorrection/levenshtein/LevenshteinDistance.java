package tr.edu.itu.bb.spellcorrection.levenshtein;

import tr.edu.itu.bb.spellcorrection.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * $Id$
 *
 * @author $Author$
 * @version $Revision$, $Date$
 */

public class LevenshteinDistance {
	
	public static int distance(String s1,String s2){
		int[][] d=new int[s1.length()+1][s2.length()+1];
		
		for(int i=0;i<=s1.length();i++)
			d[i][0]=i;
		
		for(int i=0;i<=s2.length();i++)
			d[0][i]=i;
		
		for(int j=1;j<=s2.length();j++){
			for(int i=1;i<=s1.length();i++){
				if(s1.charAt(i-1)==s2.charAt(j-1)){
					d[i][j]=d[i-1][j-1];
				}else{
					d[i][j]=Math.min(d[i-1][j]+1, Math.min(d[i][j-1]+1, d[i-1][j-1]+1));
				}
			}
		}
		
		return d[s1.length()][s2.length()];
	}

    public static List<Transformation> computeLevenshteinDistance(String misspelled, String correct, int windowSize) {

        double[][] distance = new double[misspelled.length() + 1][correct.length() + 1];
        Transformation[][] transformations = new Transformation[misspelled.length() + 1][correct.length() + 1];

        //init arrays start positions with 0 and null
        distance[0][0] = 0;
        transformations[0][0] = null;

        for (int i = 1; i <= misspelled.length(); i++){
            distance[i][0] = i;
            List<Rule> rules = getRemoveRules(misspelled, i, windowSize);
            transformations[i][0] = new Transformation(rules, TransformationType.REMOVE, transformations[i-1][0], i, 0);
        }

        for (int j = 1; j <= correct.length(); j++){
            distance[0][j] = j;
            List<Rule> rules = getAddRules(correct, j, windowSize);
            transformations[0][j] = new Transformation(rules, TransformationType.ADD, transformations[0][j-1], 0, j);
        }

        for (int i = 1; i <= misspelled.length(); i++){

            for (int j = 1; j <= correct.length(); j++){

                double removedCost = distance[i - 1][j] + TransformationType.REMOVE.getCost();
                double addedCost = distance[i][j - 1] + TransformationType.ADD.getCost();
                double swappedCost = distance[i - 1][j - 1] + ((misspelled.charAt(i - 1) == correct.charAt(j - 1)) ? 0d : TransformationType.SUBSTITUTE.getCost());

                if(swappedCost <= addedCost && swappedCost <= removedCost){

                    distance[i][j] = swappedCost;

                    if(misspelled.charAt(i - 1) == correct.charAt(j - 1)){

                        List<Rule> rules = getNoChangeRules(misspelled, correct, i, j);
                        transformations[i][j] = new Transformation(rules, TransformationType.NO_CHANGE, transformations[i-1][j-1], i, j);

                    } else {

                        List<Rule> rules = getSubstituteRules(misspelled, correct, i, j, windowSize);
                        transformations[i][j] = new Transformation(rules, TransformationType.SUBSTITUTE, transformations[i-1][j-1], i, j);

                    }

                } else {

                    if(removedCost <= addedCost && removedCost <= swappedCost){

                        distance[i][j] = removedCost;
                        List<Rule> rules = getRemoveRules(misspelled, i, windowSize);
                        transformations[i][j] = new Transformation(rules, TransformationType.REMOVE, transformations[i-1][j], i, j);

                    } else {

                        distance[i][j] = addedCost;
                        List<Rule> rules = getAddRules(correct, j, windowSize);
                        transformations[i][j] = new Transformation(rules, TransformationType.ADD, transformations[i][j-1], i, j);


                    }

                }

            }

        }

//        Util.printArray(distance);

        List<Transformation> filteredTransformations = getFilteredTransformations(transformations[misspelled.length()][correct.length()]);
//        Util.printArray(transformations, filteredTransformations);

        return filteredTransformations;

    }

    private static List<Transformation> getFilteredTransformations(Transformation transformation) {

        List<Transformation> filteredTransformations = new ArrayList<Transformation>();

        Transformation lastTransformation = transformation;

        while(lastTransformation != null){

            filteredTransformations.add(lastTransformation);

            lastTransformation = lastTransformation.getPriorTransformation();

        }

        Collections.reverse(filteredTransformations);

        return filteredTransformations;

    }

    private static List<Rule> getSubstituteRules(String misspelled, String correct, int i, int j, int windowSize) {

        List<Rule> rules = new ArrayList<Rule>();

        Rule mainRule = new Rule(String.valueOf(misspelled.charAt(i - 1)), String.valueOf(correct.charAt(j - 1)));
        rules.add(mainRule);

        for (int k = 1; k < windowSize; k++) {

            int leftSideStartIndex4Misspelled = i-1-k;
            int leftSideStartIndex4Correct = j-1-k;

            if(leftSideStartIndex4Correct >= 0 && leftSideStartIndex4Misspelled >= 0){

                String beforeRule = misspelled.substring(leftSideStartIndex4Misspelled, i);
                String afterRule = correct.substring(leftSideStartIndex4Correct, j);

                Rule rule = new Rule(beforeRule, afterRule);
                rules.add(rule);

            }

            int rightSideStartIndex4Misspelled = i-1+k;
            int rightSideStartIndex4Correct = j-1+k;
            if(rightSideStartIndex4Correct < correct.length()-1 && rightSideStartIndex4Misspelled < misspelled.length()-1){

                String afterRule = correct.substring(j - 1, rightSideStartIndex4Correct + 1);
                String beforeRule = misspelled.substring(i - 1, rightSideStartIndex4Misspelled + 1);

                Rule rule = new Rule(beforeRule, afterRule);
                rules.add(rule);

            }

        }

        return rules;

    }

    private static List<Rule> getNoChangeRules(String misspelled, String correct, int i, int j) {

        List<Rule> rules = new ArrayList<Rule>();

        Rule rule = new Rule(String.valueOf(misspelled.charAt(i - 1)), String.valueOf(correct.charAt(j - 1)));
        rules.add(rule);

        return rules;

    }

    private static List<Rule> getAddRules(String correct, int j, int windowSize) {

        List<Rule> rules = new ArrayList<Rule>();

        Rule mainRule = new Rule("", String.valueOf(correct.charAt(j-1)));
        rules.add(mainRule);

        for (int k = 1; k < windowSize; k++) {

            int leftSideStartIndex = j-1-k;
            if(leftSideStartIndex >= 0){

                String beforeRule = correct.substring(leftSideStartIndex, j-1);
                String afterRule = correct.substring(leftSideStartIndex, j);

                Rule rule = new Rule(beforeRule, afterRule);
                rules.add(rule);

            }

            int rightSideStartIndex = j-1+k;
            if(rightSideStartIndex < correct.length()-1){

                String afterRule = correct.substring(j-1, rightSideStartIndex + 1);
                String beforeRule = correct.substring(j, rightSideStartIndex + 1);

                Rule rule = new Rule(beforeRule, afterRule);
                rules.add(rule);

            }

        }

        return rules;

    }

    private static List<Rule> getRemoveRules(String misspelled, int i, int windowSize) {

        List<Rule> rules = new ArrayList<Rule>();

        Rule mainRule = new Rule(String.valueOf(misspelled.charAt(i - 1)), "");
        rules.add(mainRule);

        for (int k = 1; k < windowSize; k++) {

            int leftSideStartIndex = i-1-k;
            if(leftSideStartIndex >= 0){

                String afterRule = misspelled.substring(leftSideStartIndex, i-1);
                String beforeRule = misspelled.substring(leftSideStartIndex, i);

                Rule rule = new Rule(beforeRule, afterRule);
                rules.add(rule);

            }

            int rightSideStartIndex = i-1+k;
            if(rightSideStartIndex < misspelled.length()-1){

                String beforeRule = misspelled.substring(i-1, rightSideStartIndex + 1);
                String afterRule = misspelled.substring(i, rightSideStartIndex + 1);

                Rule rule = new Rule(beforeRule, afterRule);
                rules.add(rule);

            }

        }

        return rules;

    }

}