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

import tr.edu.itu.bb.spellcorrection.util.CharacterUtil;
import tr.edu.itu.bb.spellcorrection.util.Util;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.util.*;

public class SuffixTree {

    private int lastId ;
    private Node root;
    private Node activeNode;
    private boolean intern;

    public SuffixTree(boolean intern) {
        this.intern = intern;
        this.root = new Node();
        this.activeNode = root;
        this.lastId = 0;
    }

    public void addWord(PairedResponse<Integer, String> word) throws IllegalStateException {

        if (word.getFirst() < lastId) {
            throw new IllegalStateException("wordId should be greater than " + lastId);
        }

        lastId = word.getFirst();
        activeNode = root;

        Node s = root;

        String text = "";

        for (int i = 0; i < word.getSecond().length(); i++) {

            text += word.getSecond().charAt(i);

            if(this.intern){
                text = text.intern();
            }

            PairedResponse<Node, String> active = update(s, text, word.getSecond().substring(i), word);
            active = getLastNode(active.getFirst(), active.getSecond());

            s = active.getFirst();
            text = active.getSecond();

        }

        if (null == activeNode.getSuffixLink() && activeNode != root && activeNode != s) {

            activeNode.setSuffixLink(s);

        }

    }

    private PairedResponse<Node, String> update(final Node inputNode, final String stringPart, final String rest, final PairedResponse<Integer, String> value) {

        Node sourceNode = inputNode;

        String string2add = stringPart;

        Node oldroot = root;

        char lastChar = stringPart.charAt(stringPart.length() - 1);
        String exceptLastChar = string2add.substring(0, string2add.length() - 1);

        PairedResponse<Boolean, Node> ret = testAndSplit(sourceNode, exceptLastChar, lastChar, rest, value);

        Node r = ret.getSecond();
        boolean endpoint = ret.getFirst();

        Node leaf;

        while (!endpoint) {

            Edge tempEdge = r.getEdge(lastChar);

            if (null != tempEdge) {

                leaf = tempEdge.getDestination();

            } else {

                leaf = new Node();
                leaf.addReferencedWordId(value);
                Edge newedge = new Edge(rest, leaf);
                r.addEdge(lastChar, newedge);

            }

            if (activeNode != root) {
                activeNode.setSuffixLink(leaf);
            }

            activeNode = leaf;

            if (oldroot != root) {
                oldroot.setSuffixLink(r);
            }

            oldroot = r;

            if (null == sourceNode.getSuffixLink()) { // root node

                string2add = string2add.substring(1);

            } else {

                PairedResponse<Node, String> canret = getLastNode(sourceNode.getSuffixLink(), trimLastChar(string2add));
                sourceNode = canret.getFirst();

                if(this.intern){
                    string2add = (canret.getSecond() + string2add.charAt(string2add.length() - 1)).intern();
                } else {
                    string2add = (canret.getSecond() + string2add.charAt(string2add.length() - 1));
                }

            }

            ret = testAndSplit(sourceNode, trimLastChar(string2add), lastChar, rest, value);
            r = ret.getSecond();
            endpoint = ret.getFirst();

        }

        if (oldroot != root) {
            oldroot.setSuffixLink(r);
        }

        return new PairedResponse<>(sourceNode, string2add);

    }

    private PairedResponse<Boolean, Node> testAndSplit(final Node sourceNode, final String exceptLastChar, final char lastChar, final String remainderOfWord, final PairedResponse<Integer, String> value) {

        PairedResponse<Node, String> ret = getLastNode(sourceNode, exceptLastChar);
        Node lastNode = ret.getFirst();
        String remainingString = ret.getSecond();

        if (!"".equals(remainingString)) { //there is also some part of string that does not starts with any edge label of the lastNode

            Edge g = lastNode.getEdge(remainingString.charAt(0));

            String label = g.getLabel();

            if (label.length() > remainingString.length() && label.charAt(remainingString.length()) == lastChar) {

                return new PairedResponse<>(true, lastNode);

            } else {

                String newlabel = label.substring(remainingString.length());

                Node r = new Node();
                Edge newedge = new Edge(remainingString, r);

                g.setLabel(newlabel);

                r.addEdge(newlabel.charAt(0), g);
                lastNode.addEdge(remainingString.charAt(0), newedge);

                return new PairedResponse<>(false, r);

            }

        } else { // lastNode gives exactly the exceptLastChar string

            Edge e = lastNode.getEdge(lastChar);

            if (null == e) {

                return new PairedResponse<>(false, lastNode);//return lastNode and and a boolean which indicates there is no edge that starts with lastChar

            } else {

                if (remainderOfWord.equals(e.getLabel())) {

                    e.getDestination().addReferencedWordId(value);
                    return new PairedResponse<>(true, lastNode);

                } else if (remainderOfWord.startsWith(e.getLabel())) {

                    return new PairedResponse<>(true, lastNode);

                } else if (e.getLabel().startsWith(remainderOfWord)) { // edge label contains remainderOfWord

                    Node newNode = new Node();
                    newNode.addReferencedWordId(value);

                    Edge newEdge = new Edge(remainderOfWord, newNode);

                    e.setLabel(e.getLabel().substring(remainderOfWord.length()));

                    newNode.addEdge(e.getLabel().charAt(0), e);

                    lastNode.addEdge(lastChar, newEdge);

                    return new PairedResponse<>(false, lastNode);

                } else {

                    return new PairedResponse<>(true, lastNode);

                }

            }

        }

    }

    private PairedResponse<Node, String> getLastNode(final Node fromNode, final String inputstr) {

        if ("".equals(inputstr)) {

            return new PairedResponse<>(fromNode, inputstr);

        } else {

            Node currentNode = fromNode;
            String str = inputstr;
            Edge g = fromNode.getEdge(str.charAt(0));

            while (g != null && str.startsWith(g.getLabel())) { //string contains edge label

                str = str.substring(g.getLabel().length()); //get remaining string except edge label
                currentNode = g.getDestination();

                if (str.length() > 0) {
                    g = currentNode.getEdge(str.charAt(0)); //get the next edge to continue search
                }

            }

            return new PairedResponse<>(currentNode, str);

        }
    }

    public Collection<String> search(String word) {

        Node tmpNode = searchNode(word);

        if (tmpNode == null) {
            return null;
        }

        return tmpNode.getLabels();


    }

    private Node searchNode(String word) {

        Node currentNode = root;
        Edge currentEdge;

        for (int i = 0; i < word.length(); ++i) {

            char ch = word.charAt(i);
            currentEdge = currentNode.getEdge(ch);

            if (null == currentEdge) {

                return null;

            } else {

                String label = currentEdge.getLabel();
                int lenToMatch = Math.min(word.length() - i, label.length());

                if (!word.regionMatches(i, label, 0, lenToMatch)) {

                    return null;

                }

                if (label.length() >= word.length() - i) {

                    return currentEdge.getDestination();

                } else {

                    currentNode = currentEdge.getDestination();
                    i += lenToMatch - 1;

                }

            }

        }

        return null;
    }

    public Node getRoot() {
        return root;
    }

    private String trimLastChar(String word) {

        if (word.equals("")) {
            return "";
        } else {
            return word.substring(0, word.length() - 1);
        }

    }

    //TODO work on it!
    public static String printTreeForGraphViz(SuffixTree tree) {

        LinkedList<Node> stack = new LinkedList<>();
        stack.add(tree.getRoot());
        Map<Node, Integer> nodeMap = new HashMap<>();
        nodeMap.put(tree.getRoot(), 0);
        int nodeId = 1;

        StringBuilder sb = new StringBuilder("\ndigraph suffixTree{\n node [shape=circle, label=\"\", fixedsize=true, width=0.1, height=0.1]\n");

        while (stack.size() > 0) {

            LinkedList<Node> childNodes = new LinkedList<>();

            for (Node node : stack) {

                for (Edge edge : node.getEdges()) {

                    if(edge != null){

                        int id = nodeId++;

                        childNodes.push(edge.getDestination());
                        nodeMap.put(edge.getDestination(), id);

                        sb.append(nodeMap.get(node)).append(" -> ").append(id).append(" [label=\"");

                        for (String item : edge.getDestination().getLabels()) {
                            sb.append(item);

                        }

                        sb.append("\"];\n");
                    }
                }

            }

            stack = childNodes;

        }

        // loop again to find all suffix links.
        sb.append("edge [color=red]\n");

        for (Map.Entry<Node, Integer> entry : nodeMap.entrySet()) {

            Node n1 = entry.getKey();
            int id1 = entry.getValue();

            if (n1.getSuffixLink() != null) {
                Node n2 = n1.getSuffixLink();
                Integer id2 = nodeMap.get(n2);
                // if(id2 != null)
                sb.append(id1).append(" -> ").append(id2).append(" ;\n");
            }

        }

        sb.append("}");

        return (sb.toString());

    }

    public static void main(String[] args) throws IOException, InterruptedException {

        boolean intern = true;

        CharacterUtil.initCharacterMapping("data/model/characters.txt");
        Locale trLocale =  new Locale("tr", "TR");
//        List<String> lines = Util.readFile("data/model/corpus-preprocessed.txt", Util.UTF8_ENCODING, true, intern);
        List<String> lines = Util.readFile("data/model/tdk-stems.txt", Util.UTF8_ENCODING, true, intern);

        SuffixTree suffixTree = new SuffixTree(intern);

        int i = 0;
        String lineProcessed = null;

        try{
            for (String line : lines) {

                line = line.trim().toLowerCase(trLocale);

                if(intern){
                    line = line.intern();
                }

                lineProcessed = line;

                suffixTree.addWord(new PairedResponse<>(i, line));
                i++;

            }
        } catch (Exception e){
            System.out.println("index: " + i + ", line processed: " + lineProcessed);
            e.printStackTrace();
        }

        lines = null;

        while(true){

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
            System.out.println(suffixTree.root.getEdges().length);
            System.out.println();

        }

    }

}
