package tr.edu.itu.bb.spellcorrection.validators;

import tr.edu.itu.bb.spellcorrection.trie.Trie;
import tr.edu.itu.bb.spellcorrection.util.Util;

import java.util.List;
import java.util.Locale;

/**
 * @author erenbekar@gmail.com
 * @version 1.0.0
 * @since 2013-12-08
 */
public class DictionaryTurkishWordValidator implements TurkishWordValidator {

    private Trie trie;

    public DictionaryTurkishWordValidator(String vocabularyFile) throws Exception {
        this.trie = buildVocabularyTrie(vocabularyFile);
    }

    private Trie buildVocabularyTrie(String vocabularyFile) throws Exception {

        int i = 0;

        try {

            Trie trie = new Trie();

            Locale trLocale = new Locale("tr", "TR");

            List<String> lines = Util.readFile(vocabularyFile, Util.UTF8_ENCODING, true);

            for (String line : lines) {

                line = line.trim().toLowerCase(trLocale);
                trie.addWord(line);
                i++;

            }

            return trie;

        } catch (Exception e) {
            System.out.println("Exception while adding word on line: " + i);
            throw e;
        }

    }

    @Override
    public boolean isTurkish(String word) {
        return trie.contains(word);
    }

}
