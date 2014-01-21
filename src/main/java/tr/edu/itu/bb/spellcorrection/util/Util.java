package tr.edu.itu.bb.spellcorrection.util;

import tr.edu.itu.bb.spellcorrection.levenshtein.Transformation;
import tr.edu.itu.bb.spellcorrection.suffixtree.Edge;
import tr.edu.itu.bb.spellcorrection.suffixtree.Node;
import tr.edu.itu.bb.spellcorrection.suffixtree.SuffixTree;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * $Id$
 *
 * @author $Author$
 * @version $Revision$, $Date$
 */

public class Util {

    private static final String UTF8_BOM = "\uFEFF";
    public static final String UTF8_ENCODING = "UTF8";

    public static void printArray(double[][] arr) {

        for (int i = 0; i < arr.length; i++) {

            for (int j = 0; j < arr[i].length; j++) {

                System.out.print(arr[i][j]);
                System.out.print("\t");

            }

            System.out.println();

        }

    }

    public static void printArray(Transformation[][] arr, List<Transformation> transformations) {

        System.out.println();
        System.out.print(padRight("", 5));

        for (int j = 0; j < arr[0].length; j++) {

            System.out.print(padRight(String.valueOf(j), 15));

        }

        System.out.println();


        for (int i = 0; i < arr.length; i++) {

            System.out.print(padRight(String.valueOf(i), 5));

            for (int j = 0; j < arr[i].length; j++) {

                String str;

                if (arr[i][j] != null) {

                    if (transformations.contains(arr[i][j])) {
                        str = "*" + arr[i][j].toString();
                    } else {
                        str = arr[i][j].toString();
                    }

                } else {

                    str = "<null>";

                }

                System.out.print(padRight(str, 15));

            }

            System.out.println();

        }

        System.out.println();

    }

    private static String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);
    }

    public static void main(String[] args) throws IOException {

        printChars();

    }

    private static void printChars() throws IOException {

        Map<Character, Integer> charMap = new HashMap<Character, Integer>();

        String[] files = new String[]{"data/model/corpus-preprocessed.txt", "data/evaluations/normalignerData.txt"};

        for (String file : files) {

            addChars2Map(file, charMap);

        }

        List<Character> characters = new ArrayList<Character>();
        for (Character character : charMap.keySet()) {
            characters.add(character);
        }

        Collections.sort(characters);

        for (Character character : characters) {
            System.out.println(character + "=>" + (int) character);
        }

    }

    private static void addChars2Map(String file, Map<Character, Integer> charMap) throws IOException {

        List<String> lines = Util.readFile(file, Util.UTF8_ENCODING, true);

        for (String line : lines) {

            for (char c : line.toCharArray()) {

                c = Character.valueOf(c).toString().toLowerCase(new Locale("tr", "TR")).charAt(0);

                if (charMap.containsKey(c)) {

                    charMap.put(c, charMap.get(c) + 1);

                } else {

                    charMap.put(c, 0);

                }

            }

        }

    }

    public static List<String> readFile(String file, String encoding, boolean removeBOM) throws IOException {
        return readFile(file, encoding, removeBOM, false);
    }

    public static List<String> readFile(String file, String encoding, boolean removeBOM, boolean intern) throws IOException {

        BufferedReader bufferedReader = null;

        List<String> lines = new ArrayList<String>(1600000);

        try {

            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));

            String line;

            boolean firstLine = true;

            int i = 0;
            while ((line = bufferedReader.readLine()) != null) {

                if (firstLine && removeBOM) {
                    line = removeUTF8BOM(line);
                }

                if (intern) {
                    lines.add(removeEmptyChars(line).intern());
                } else {
                    lines.add(removeEmptyChars(line));
                }

                firstLine = false;


                i++;
                if(i % 1000 == 0) System.out.println(i);
            }

        } finally {

            if (bufferedReader != null) {
                bufferedReader.close();
            }

        }

        return lines;

    }

    private static String removeEmptyChars(String line) {

        StringBuilder sb = new StringBuilder(line.length());

        for (char c : line.toCharArray()) {

            if (c != 0) {
                sb.append(c);
            }
        }

        return sb.toString();

    }

    private static String removeUTF8BOM(String s) {
        if (s.startsWith(UTF8_BOM)) {
            s = s.substring(1);
        }
        return s;
    }

}
