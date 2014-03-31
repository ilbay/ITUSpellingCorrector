package tr.edu.itu.bb.spellcorrection.util;

//import nlp.tools.turkish.ITUNormalizator;
import tr.edu.itu.bb.spellcorrection.Bootstrap;
import tr.edu.itu.bb.spellcorrection.levenshtein.Candidate;

import java.io.*;
import java.util.List;
import java.util.Locale;

/**
 * User: eren
 * Date: 5/7/13
 * Time: 9:32 PM
 */
public final class Evaluator {

    private String characterFile;
    private String trainingCorrectionsFile;
    private String testCorrectionsFile;
    private String stemsFile;
    private String evaluationFile;
    private int depth;
    private int candidates;
    private int windowSize;
    private Bootstrap.TurkishValidator turkishValidator;

    public Evaluator() {

        int defaultK = 1;
        int defaultDepth = 2;
        int defaultWindowSize = 3;

        this.characterFile = "data/model/characters.txt";
        this.trainingCorrectionsFile = "data/model/corrections.txt";
        this.stemsFile = "data/model/tdk-stems.txt";
//        this.stemsFile = "data/model/corpus-preprocessed.txt";

//        this.testCorrectionsFile = "data/evaluations/word-test-corrections.txt";
//        this.testCorrectionsFile = "data/evaluations/ManuelEditedTweets.txt";
        this.testCorrectionsFile = "data/evaluations/normalignerData.txt";
        this.evaluationFile = "data/evaluations/evaluations_depth_" + defaultDepth + "_k_" + defaultK + ".csv";

        this.turkishValidator = Bootstrap.TurkishValidator.ItuNlpTools;

        this.depth = defaultDepth;
        this.candidates = defaultK;
        this.windowSize = defaultWindowSize;

    }

    public static void main(String[] args) throws Exception {
        new Evaluator().evaluate();
    }

    private void evaluate() throws Exception {

        Locale trLocale = new Locale("tr", "TR");

        Bootstrap bootstrap = new Bootstrap(trainingCorrectionsFile, stemsFile, characterFile,windowSize, turkishValidator);
        bootstrap.setVerbose(false);
        bootstrap.init();

        List<String> lines = Util.readFile(testCorrectionsFile, Util.UTF8_ENCODING, true);

        BufferedWriter writer = new BufferedWriter(new FileWriter(evaluationFile));

        int correctedSize = 0;

        int words = 0;
        for (String line : lines) {

            words++;

            line = line.trim().toLowerCase(trLocale);

            String[] parts = line.split("=>");

            if(parts.length == 2 && !parts[0].equals(parts[1]) && !bootstrap.isTurkish(parts[0]) && parts[0].length() <= 15){

                long start = System.currentTimeMillis();

                String log = "original: \""  + parts[0] + "\"\tcorrected: \"";

                String candidates = bootstrap.findCandidates(parts[0]);

                long millis = System.currentTimeMillis() - start;

                if(candidates != null){
                    log += candidates + "\"\tsupposed: \"" + parts[1] + "\"";
                } else {
                    log += "N\\A" + "\"\tsupposed: \"" + parts[1] + "\"";
                }

                int index = getCorrectedIndex(candidates, parts[1]);
                if(index != -1){

                    correctedSize++;
                    writeEvaluation(writer, parts, candidates, String.valueOf(index), String.valueOf(millis));


                } else {

                    writeEvaluation(writer, parts, "", "-1", String.valueOf(millis));

                }

                System.out.println(log +" (isturkish count: " + bootstrap.getTurkishRequestCount() + ", for " + words + " words)");


            }

        }

        writer.flush();
        writer.close();

        System.out.println("corrected: " + correctedSize);

    }

    private void writeEvaluation(BufferedWriter writer, String[] parts, String candidate, String index, String millis) throws IOException {

        writer.write(parts[0]);
        writer.write(",");
        writer.write(parts[1]);
        writer.write(",");
        writer.write(candidate);
        writer.write(",");
        writer.write(index);
        writer.write(",");
        writer.write(millis);
        writer.newLine();

    }

    private int getCorrectedIndex(String candidates, String corrected) {

        int i = 0;
        if(candidates.equals(corrected)){
        	return i;
        }
        return -1;

    }
}
