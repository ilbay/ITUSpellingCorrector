package tr.edu.itu.bb.spellcorrection.trie;

import org.junit.Test;
import tr.edu.itu.bb.spellcorrection.util.CharacterUtil;
import tr.edu.itu.bb.spellcorrection.util.Util;

import java.io.*;
import java.util.List;
import java.util.Locale;

/**
 * User: eren
 * Date: 4/29/13
 * Time: 11:08 PM
 */
public class TrieTest {

    @Test
    public void testTrie() throws IOException {

        long start = System.currentTimeMillis();

        CharacterUtil.initCharacterMapping("data/model/characters.txt");

        Trie trie = new Trie();

        Locale trLocale = new Locale("tr", "TR");

        List<String> lines = Util.readFile("data/model/tdk-stems.txt", Util.UTF8_ENCODING, true);

        for (String line : lines) {

            line = line.trim().toLowerCase(trLocale);
            trie.addWord(line);

        }

        System.out.println("Trie constructed in " + (System.currentTimeMillis() - start) + " millis");

        testWord(trie, "alem");

    }

    private void testWord(Trie trie, String word) {

        long start = System.currentTimeMillis();

        List<String> outputs = trie.getOutputs(word);

        long finish = System.currentTimeMillis() - start;

        System.out.println("Trie contains " + word + ": " + trie.contains(word));

        System.out.println("word: \"" + word + "\" found in " + finish + " millis. Outputs are: " + outputs);

    }
}
