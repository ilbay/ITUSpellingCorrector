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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

public class Node {

    private static final int START_SIZE = 0;
    private static final int INCREMENT = 1;

    private String[] labels;
    private int wordIdSize = 0;
    private Edge[] edges;
    private Node suffixLink;

    public Node() {

        edges = new Edge[CharacterUtil.getCharacterSize()];
        suffixLink = null;
        labels = new String[START_SIZE];

    }

    public Collection<String> getLabels() {

        Collection<String> ret = new HashSet<>();

        for (String label : labels) {
            ret.add(label);
        }

        for (Edge e : edges) {

                if(e != null){

                    for (String label : e.getDestination().getLabels()) {

                        ret.add(label);

                    }

                }

        }

        return ret;

    }

    public void addReferencedWordId(PairedResponse<Integer, String> wordId) {

        if (contains(wordId)) {
            return;
        }

        addWordId(wordId);

        Node iter = this.suffixLink;

        while (iter != null) {

            if (iter.contains(wordId)) {
                break;
            }

            iter.addReferencedWordId(wordId);
            iter = iter.suffixLink;

        }

    }

    private boolean contains(PairedResponse<Integer, String> index) {
        return Arrays.binarySearch(labels, 0, wordIdSize, index.getSecond()) >= 0;
    }

    public Edge[] getEdges() {
        return edges;
    }

    public void addEdge(char ch, Edge e) {
        edges[CharacterUtil.getCharIndex(ch)] = e;
    }

    public Edge getEdge(char ch) {
        return edges[CharacterUtil.getCharIndex(ch)];
    }

    public Node getSuffixLink() {
        return suffixLink;
    }

    public void setSuffixLink(Node suffixLink) {
        this.suffixLink = suffixLink;
    }

    private void addWordId(PairedResponse<Integer, String> wordId) {

        if (wordIdSize == labels.length) {

            String[] copyLabels = new String[labels.length + INCREMENT];
            System.arraycopy(labels, 0, copyLabels, 0, labels.length);
            labels = copyLabels;

        }

        labels[wordIdSize] = wordId.getSecond();

        wordIdSize++;

    }


}
