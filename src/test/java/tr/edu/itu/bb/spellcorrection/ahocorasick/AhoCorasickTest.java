package tr.edu.itu.bb.spellcorrection.ahocorasick;

import org.junit.Test;
import tr.edu.itu.bb.spellcorrection.util.CharacterUtil;

import java.io.IOException;
import java.util.Iterator;

/**
 * $Id$
 *
 * @author $Author$
 * @version $Revision$, $Date$
 */

public class AhoCorasickTest {

    @Test
    public void test() throws IOException {

        String[] keywords = new String[]{"eren", "bekar", "itu", "bilgisayar", "mühendisliği", "test", "aho", "corasick", "beceren", "ren", "kar"};

        CharacterUtil.initCharacterMapping("data/model/characters.txt");

        AhoCorasick<String> ahoCorasick = new AhoCorasick<String>();

        for (String keyword : keywords) {
            ahoCorasick.addWord(keyword, keyword);
        }

        ahoCorasick.build();

        String text = "bikerenerdekar d";

        Iterator<SearchResult<String>> it = ahoCorasick.search(text.toCharArray());
        while (it.hasNext()){
            System.out.println("--------------next result--------------");
            SearchResult<String> searchResult = (SearchResult)it.next();
            for (String s : searchResult.getOutputs()) {
                System.out.println("found: " + s);
            }
        }

        System.out.println("done");

    }

}
