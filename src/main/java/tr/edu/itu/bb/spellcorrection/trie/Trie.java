package tr.edu.itu.bb.spellcorrection.trie;


import com.googlecode.concurrenttrees.common.Iterables;
import com.googlecode.concurrenttrees.common.PrettyPrinter;
import com.googlecode.concurrenttrees.radix.ConcurrentRadixTree;
import com.googlecode.concurrenttrees.radix.RadixTree;
import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharArrayNodeFactory;
import com.googlecode.concurrenttrees.radix.node.concrete.voidvalue.VoidValue;
import com.googlecode.concurrenttrees.radix.node.util.PrettyPrintable;
import com.googlecode.concurrenttrees.suffix.ConcurrentSuffixTree;
import com.googlecode.concurrenttrees.suffix.SuffixTree;

import tr.edu.itu.bb.spellcorrection.ahocorasick.AhoCorasick;
import tr.edu.itu.bb.spellcorrection.levenshtein.Rule;
import tr.edu.itu.bb.spellcorrection.suffixtree.PairedResponse;
import tr.edu.itu.bb.spellcorrection.util.CharacterUtil;
import tr.edu.itu.bb.spellcorrection.util.Util;

import java.io.IOException;
import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * User: eren
 * Date: 4/29/13
 * Time: 11:00 PM
 */
public class Trie implements Serializable{

    private Node rootNode;

    public Trie() {
        this.rootNode = Node.ROOT;
    }

    public void addWord(String word) {
        rootNode.expandNode(word.toCharArray());
    }
    
    public Node getRootNode()
    {
    	return this.rootNode;
    }

    public List<String> getOutputs(String word) {

        List<String> outputs = new ArrayList<String>();

        Node lastNode = rootNode;

        for (char c : word.toCharArray()) {

            lastNode = lastNode.getSubNode(c);

            if (lastNode == null) {

                break;

            } else {

                if (lastNode.getOutput() != null) {
                    outputs.add(lastNode.getOutput());
                }

            }

        }

        return outputs;

    }

    public boolean contains(String word) {

        Node lastNode = rootNode;

        int i = 0;
        for (char c : word.toCharArray()) {

            lastNode = lastNode.getSubNode(c);

            if (lastNode == null) {

                return false;

            } else {

                if (lastNode.getOutput() != null && i == word.length() - 1) {
                    return true;
                }

            }

            i++;

        }

        return false;

    }

    /**
     * @param word Represents the word to which rules are applied
     * @return The list of stem candidates of the word to which rules are applied
     */
    public List<Word> getRootWords(String word)
    {
    	ArrayList<Word> rootWords = new ArrayList<Word>();
    	Node currentNode = this.rootNode;
    	    	
    	for(char c : word.toCharArray())
    	{
    		currentNode = currentNode.getSubNode(c);

    		if(currentNode == null)
    		{
    			break;
    		}

    		if(currentNode.getOutput() != null)
    		{
    			Word rootWord = new Word();
    			rootWord.setStem(currentNode.getOutput());
    			rootWord.setSuffix(word.substring(currentNode.getOutput().length()));
    			rootWords.add(rootWord);
    		}
    	}

    	return rootWords;
    }
    
    public List<Word> getRootWords(String misspelledWord, AhoCorasick<Rule> rules)
    {
    	ArrayList<Word> rootWords = new ArrayList<Word>();
    	return rootWords;
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        test();

    }

    public static void test() throws IOException, InterruptedException {

        boolean intern = true;

        CharacterUtil.initCharacterMapping("data/model/characters.txt");
        Locale trLocale = new Locale("tr", "TR");
//        List<String> lines = Util.readFile("data/EntireMachineSuffixesForNoun_ba_", Util.UTF8_ENCODING, true, intern);
        List<String> lines = Util.readFile("data/model/corpus-preprocessed.txt", Util.UTF8_ENCODING, true, intern);
//        List<String> lines = Util.readFile("data/model/tdk-stems.txt", Util.UTF8_ENCODING, true, intern);

//        Trie trie = new Trie();
        RadixTree<Integer> tree = new ConcurrentRadixTree<Integer>(new DefaultCharArrayNodeFactory());
//        SuffixTree<Integer> tree = new ConcurrentSuffixTree<Integer>(new DefaultCharArrayNodeFactory());

        int i = 0;
        String lineProcessed = null;

        try {
            for (String line : lines) {

                line = line.trim().toLowerCase(trLocale);

                if (intern) {
                    line = line.intern();
                }

                lineProcessed = line;

//                trie.addWord(line);
                tree.put(line, i);
                i++;

                if(i % 1000 == 0) System.out.println(i);

            }
        } catch (Exception e) {
            System.out.println("index: " + i + ", line processed: " + lineProcessed);
            e.printStackTrace();
        }

        lines = null;

        while (true) {

            Runtime runtime = Runtime.getRuntime();
            runtime.gc();

            long total = 0;

            List<MemoryPoolMXBean> memoryPoolMXBeans = ManagementFactory.getMemoryPoolMXBeans();

            for (MemoryPoolMXBean memoryPoolMXBean : memoryPoolMXBeans) {
                System.out.println(memoryPoolMXBean.getName() + ": " + memoryPoolMXBean.getUsage().getUsed() + " / " + memoryPoolMXBean.getUsage().getMax());
                total += memoryPoolMXBean.getUsage().getUsed();
            }

            System.out.println("total: " + total);
            Thread.sleep(10000);
//            System.out.println(trie);
            System.out.println(tree);
            System.out.println();

        }

    }
}
