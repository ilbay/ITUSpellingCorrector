package tr.edu.itu.bb.spellcorrection.util;

import tr.edu.itu.bb.spellcorrection.Bootstrap;
import tr.edu.itu.bb.spellcorrection.levenshtein.Candidate;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * User: erenbekar
 * Date: 5/14/13
 * Time: 9:17 PM
 */
public class ZemberekEvaluator {

    private String characterFile;
    private String trainingCorrectionsFile;
    private String testCorrectionsFile;
    private String stemsFile;
    private String evaluationFile;
    private int depth;
    private int candidates;
    private int windowSize;

    public ZemberekEvaluator() {

        int defaultK = -1;
        int defaultDepth = 2;
        int defaultWindowSize = 3;

        this.characterFile = "data/model/characters.txt";
        this.trainingCorrectionsFile = "data/model/corrections.txt";
        this.stemsFile = "data/model/tdk-stems.txt";

        this.testCorrectionsFile = "data/evaluations/ManuelEditedTweets.txt";
        this.evaluationFile = "data/evaluations/evaluations_depth_" + defaultDepth + "_k_" + defaultK + ".csv";

        this.depth = defaultDepth;
        this.candidates = defaultK;
        this.windowSize = defaultWindowSize;

    }

    public static void main(String[] args) throws Exception {

//        new ZemberekEvaluator().evaluate();
        new ZemberekEvaluator().evaluateOnlyZemberek();

    }

    private void evaluateOnlyZemberek() throws IOException {

        Locale trLocale = new Locale("tr", "TR");

        List<String> lines = Util.readFile(testCorrectionsFile, Util.UTF8_ENCODING, true);

        int corrected = 0;
        int failed = 0;

        for (String line : lines) {

            line = line.trim().toLowerCase(trLocale);

            String[] parts = line.split("=>");

            long zemberekStart = System.currentTimeMillis();
            boolean zemberekCorrected = ZemberekUtil.inTopN(parts[0], parts[1], this.candidates);
            long zemberekMillis = System.currentTimeMillis() - zemberekStart;

            if (zemberekCorrected) {

                corrected++;
                System.out.println(parts[0] + "," + parts[1] + ",corrected," + zemberekMillis);

            } else {

                failed++;
                System.out.println(parts[0] + "," + parts[1] + ",failed," + zemberekMillis);

            }

        }

        System.out.println("total corrected: " + corrected);
        System.out.println("total failed: " + failed);

    }

    private void evaluate() throws Exception {

        Locale trLocale = new Locale("tr", "TR");

        Bootstrap bootstrap = new Bootstrap(trainingCorrectionsFile, stemsFile, characterFile, windowSize, Bootstrap.TurkishValidator.Zemberek);
        bootstrap.setVerbose(false);
        bootstrap.init();

        List<String> lines = Util.readFile(testCorrectionsFile, Util.UTF8_ENCODING, true);

        BufferedWriter writer = new BufferedWriter(new FileWriter(evaluationFile));

        int bothPositive = 0;
        int bothNegative = 0;
        int mcPositiveZemberekNegative = 0;
        int mcNegativeZemberekPositive = 0;

        for (String line : lines) {

            line = line.trim().toLowerCase(trLocale);

            String[] parts = line.split("=>");

            if (bootstrap.isTurkish(parts[1])) {

                if (bootstrap.isTurkish(parts[0])) {

                    writeEvaluation(writer, parts, "", "INPUT IS ALREADY A VALID WORD", "", false, 0L);

                } else {


                    long start = System.currentTimeMillis();
                    String candidates = bootstrap.findCandidates(parts[0]);
                    long millis = System.currentTimeMillis() - start;

                    int index = getCorrectedIndex(candidates, parts[1]);

                    long zemberekStart = System.currentTimeMillis();
                    boolean zemberekCorrected = ZemberekUtil.inTopN(parts[0], parts[1], this.candidates);
                    long zemberekMillis = System.currentTimeMillis() - zemberekStart;

                    if (index != -1) {

                        if (zemberekCorrected) {
                            bothPositive++;
                        } else {
                            mcPositiveZemberekNegative++;
                        }

                        writeEvaluation(writer, parts, candidates, String.valueOf(index), String.valueOf(millis), zemberekCorrected, zemberekMillis);


                    } else {

                        writeEvaluation(writer, parts, "", "-1", String.valueOf(millis), zemberekCorrected, zemberekMillis);

                        if (zemberekCorrected) {
                            mcNegativeZemberekPositive++;
                        } else {
                            bothNegative++;
                        }

                    }

                }

            } else {

                writeEvaluation(writer, parts, "", "CORRECTED IS NOT A VALID WORD", "", false, 0L);

            }

        }

        writer.flush();
        writer.close();

        System.out.println("both positive: " + bothPositive);
        System.out.println("both negative: " + bothNegative);
        System.out.println("mc positive, zemberek negative: " + mcPositiveZemberekNegative);
        System.out.println("mc negative, zemberek positive: " + mcNegativeZemberekPositive);

    }

    private void writeEvaluation(BufferedWriter writer, String[] parts, String candidate, String index, String millis, boolean zemberekCorrected, long zemberekMillis) throws IOException {

        writer.write(parts[0]);
        writer.write(",");
        writer.write(parts[1]);
        writer.write(",");
        writer.write(candidate);
        writer.write(",");
        writer.write(index);
        writer.write(",");
        writer.write(millis);
        writer.write(",");
        writer.write(String.valueOf(zemberekCorrected));
        writer.write(",");
        writer.write(String.valueOf(zemberekMillis));
        writer.newLine();

    }

    private int getCorrectedIndex(String candidates, String corrected) {

        int i = 0;
        if (candidates.equals(corrected)) {
        	return i;
        }

        return -1;

    }

}
