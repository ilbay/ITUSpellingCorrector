package tr.edu.itu.bb.spellcorrection.ahocorasick;

import tr.edu.itu.bb.spellcorrection.util.CharacterUtil;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * $Id$
 *
 * @author $Author$
 * @version $Revision$, $Date$
 */

public class AhoCorasick<T> {

    private Node<T> rootNode;

    public AhoCorasick() {
        this.rootNode = Node.ROOT;
    }

    public void addWord(String word, T output){
        rootNode.expandNode(word.toCharArray(), output);
    }

    public void build() {

        Queue<Node<T>> queue = new LinkedList<Node<T>>();

        setFailFunctionsForLevelOne(queue);

        setMissingCharactersToRoot();

        while (!queue.isEmpty()) {

            Node<T> lastNode = queue.poll();
            char[] chars = CharacterUtil.getChars(lastNode);

            for (char aChar : chars) {

                Node<T> r = lastNode;

                Node<T> gotoOfChar = r.getSubNode(aChar);

                queue.add(gotoOfChar);

                r = getLastFail(r, aChar);

                gotoOfChar.setFailNode(r.getSubNode(aChar));
                gotoOfChar.getOutputs().addAll(r.getSubNode(aChar).getOutputs());

            }

        }

    }

    private void setMissingCharactersToRoot() {

        for (Character c : CharacterUtil.getValidCharacters()) {

            if(this.rootNode.getSubNode(c) == null){

                this.rootNode.setSubNode(c, this.rootNode);

            }

        }

    }

    private Node<T> getLastFail(Node<T> fromSatate, char c) {

        Node<T> fail = fromSatate.getFailNode();

        while (fail.getSubNode(c)== null) {

            fail = fail.getFailNode();

        }

        return fail;

    }

    private void setFailFunctionsForLevelOne(Queue<Node<T>> queue) {

        for (Node<T> node : this.rootNode.getSubNodes()) {

            if(node != null){
                node.setFailNode(this.rootNode);
                queue.add(node);
            }

        }

    }

    public Iterator<SearchResult<T>> search(char[] chars) {
        return new Searcher<T>(this, this.startSearch(chars));
    }

    public SearchResult<T> startSearch(char[] chars) {
        return continueSearch(new SearchResult<T>(this.rootNode, chars, 0));
    }

    public SearchResult<T> continueSearch(SearchResult<T> currentResults) {

        char[] chars = currentResults.getChars();

        Node<T> node = currentResults.getLastNode();

        for (int i = currentResults.getLastIndex(); i < chars.length; i++) {

            char b = chars[i];

            while (node.getSubNode(b) == null){
                node= node.getFailNode();
            }

            node = node.getSubNode(b);

            if (node.getOutputs().size() > 0){
                return new SearchResult<T>(node, chars, i + 1);
            }

        }

        return null;

    }

}
