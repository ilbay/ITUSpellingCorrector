package tr.edu.itu.bb.spellcorrection.suffixtree;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import tr.edu.itu.bb.spellcorrection.ahocorasick.AhoCorasick;
import tr.edu.itu.bb.spellcorrection.trie.Trie;
import tr.edu.itu.bb.spellcorrection.util.CharacterUtil;
import tr.edu.itu.bb.spellcorrection.util.Util;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * @author erenbekar
 * @since 9/2/13 9:16 PM
 */

public class MemoryTest {

    private enum DataStructure {

        SuffixTree,
        Trie,
        AhoCorasick

    }

    private static Locale trLocale;

    private DataStructure dataStructure;
    private boolean intern;
    private static List<String> lines;

    @BeforeClass
    public static void setUpClass() throws Exception {
        CharacterUtil.initCharacterMapping("data/model/characters.txt");
        trLocale =  new Locale("tr", "TR");
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws IOException {
        this.intern = true;
        lines = Util.readFile("data/model/corpus-preprocessed.txt", Util.UTF8_ENCODING, true, intern);
        this.dataStructure = DataStructure.SuffixTree;
    }

    @After
    public void tearDown() {
    }

    @Test
    public void construct() throws IOException, InterruptedException {

        switch (dataStructure) {

            case SuffixTree:
                constructSuffixTree();
                break;
            case Trie:
                constructTrie();
                break;
            case AhoCorasick:
                constructAhoCorasick();
                break;
        }

        lines = null;

        printMemoryUsage();

        Thread.sleep(10000 * 60);

    }

    private void printMemoryUsage() {

        Runtime runtime = Runtime.getRuntime();
        runtime.gc();

        long memory = runtime.totalMemory() - runtime.freeMemory();

        List<MemoryPoolMXBean> memoryPoolMXBeans = ManagementFactory.getMemoryPoolMXBeans();

        for (MemoryPoolMXBean memoryPoolMXBean : memoryPoolMXBeans) {
            System.out.println(memoryPoolMXBean.getName() + ": " + memoryPoolMXBean.getUsage().getUsed());
        }

        String title = dataStructure.name();
        title += intern ? " w/ intern" : " w/o intern";

        System.out.println(title);
        System.out.println("Used memory is bytes: " + memory);

    }

    private void constructAhoCorasick() throws IOException {

        AhoCorasick<String> ahoCorasick = new AhoCorasick<>();

        for (String line : lines) {

            line = line.trim().toLowerCase(trLocale);

            if(intern){
                line = line.intern();
            }

            ahoCorasick.addWord(line, line);

        }

        ahoCorasick.build();

    }

    private void constructTrie() throws IOException {

        Trie trie = new Trie();


        for (String line : lines) {

            line = line.trim().toLowerCase(trLocale);

            if(intern){
                line = line.intern();
            }

            trie.addWord(line);

        }


    }

    public void constructSuffixTree() throws InterruptedException, IOException {

        SuffixTree suffixTree = new SuffixTree(intern);

        int i = 0;
        for (String line : lines) {

            line = line.trim().toLowerCase(trLocale);

            if(intern){
                line = line.intern();
            }

            suffixTree.addWord(new PairedResponse<>(i, line));
            i++;

        }

    }

}
