package tr.edu.itu.bb.spellcorrection.util;

import tr.edu.itu.bb.spellcorrection.Bootstrap;

import java.io.*;
import java.util.List;
import java.util.Locale;

/**
 * User: eren
 * Date: 5/8/13
 * Time: 11:07 PM
 */
public class FilterCorrections {

    private String characterFile;
    private String correctionsFile;
    private String stemsFile;
    private int depth;
    private int candidates;
    private int windowSize;
    private Bootstrap.TurkishValidator turkishValidator;

    private static final String trainingCorrectionsFile = "data/evaluations/training-corrections.txt";
    private static final String testCorrectionsFile = "data/evaluations/test-corrections.txt";

    public static void main(String[] args) throws Exception {
        new FilterCorrections().filter();
    }

    public FilterCorrections() {

        this.characterFile = "data/model/characters.txt";
        this.correctionsFile = "data/model/corrections.txt";
        this.stemsFile = "data/model/tdk-stems.txt";
        this.depth = 2;
        this.candidates = 1;
        this.windowSize = 3;

    }

    public void filter() throws Exception {

        Locale trLocale = new Locale("tr", "TR");

        Bootstrap bootstrap = new Bootstrap(correctionsFile, stemsFile, characterFile, windowSize, turkishValidator);
        bootstrap.setVerbose(false);
        bootstrap.init();

        List<String> lines = Util.readFile(correctionsFile, Util.UTF8_ENCODING, true);

        BufferedWriter trainingWriter = new BufferedWriter(new FileWriter(trainingCorrectionsFile));
        BufferedWriter testWriter = new BufferedWriter(new FileWriter(testCorrectionsFile));

        int testSize = 0;
        int trainingSize = 0;

        for (String line : lines) {

            String newLine = line.trim().toLowerCase(trLocale);

            String[] parts = newLine.split("=>");

            if(bootstrap.isTurkish(parts[1])){

                testSize++;
                testWriter.write(line);
                testWriter.newLine();

            } else {

                trainingSize++;
                trainingWriter.write(line);
                trainingWriter.newLine();

            }

        }


        trainingWriter.flush();
        trainingWriter.close();

        testWriter.flush();
        testWriter.close();

        System.out.println("trainingSize: " + trainingSize);
        System.out.println("testSize: " + testSize);

    }
}
