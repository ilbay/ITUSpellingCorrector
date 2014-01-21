/**
 * Copyright 2012 Alessandro Bahgat Shehata
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tr.edu.itu.bb.spellcorrection.suffixtree;

import org.junit.*;
import tr.edu.itu.bb.spellcorrection.util.CharacterUtil;
import tr.edu.itu.bb.spellcorrection.util.Util;

import java.io.IOException;
import java.util.*;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.*;

public class SuffixTreeTest {

    @BeforeClass
    public static void setUpClass() throws Exception {
        CharacterUtil.initCharacterMapping("data/model/characters.txt");
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws IOException {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testTdkWords() throws IOException {

        SuffixTree suffixTree = new SuffixTree(true);

        Locale trLocale = new Locale("tr", "TR");

        List<String> lines = Util.readFile("data/model/tdk-stems.txt", Util.UTF8_ENCODING, true, true);

        int i = 0;
        for (String line : lines) {

            line = line.trim().toLowerCase(trLocale);
            suffixTree.addWord(new PairedResponse<>(i, line));
            i++;

        }

        testWord(suffixTree, "alem", lines);

    }

    @Test
    public void test(){

        SuffixTree suffixTree = new SuffixTree(true);

        suffixTree.addWord(new PairedResponse<Integer, String>(0, "dekiler"));
        suffixTree.addWord(new PairedResponse<Integer, String>(1, "kiler"));
        suffixTree.addWord(new PairedResponse<Integer, String>(2, "ler"));

        suffixTree.toString();

    }
    private void testWord(SuffixTree suffixTree, String word, List<String> words) {

        Collection<String> outputs = suffixTree.search(word);

        System.out.println("Searching for: " + word);

        for (String output : outputs) {
            System.out.println("Found: " + output);
        }

    }

}
