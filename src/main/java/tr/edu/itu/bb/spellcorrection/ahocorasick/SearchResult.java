package tr.edu.itu.bb.spellcorrection.ahocorasick;

import java.util.List;

/**
 * $Id$
 *
 * @author $Author$
 * @version $Revision$, $Date$
 */

public class SearchResult<T> {

    private Node<T> lastNode;
    private char[] chars;
    private int lastIndex;

    public SearchResult(Node<T> lastNode, char[] chars, int lastIndex) {
        this.lastNode = lastNode;
        this.chars = chars;
        this.lastIndex = lastIndex;
    }

    public int getLastIndex() {
        return lastIndex;
    }

    public char[] getChars() {
        return chars;
    }

    public Node<T> getLastNode() {
        return lastNode;
    }

    public List<T> getOutputs(){
        return lastNode.getOutputs();
    }

}
