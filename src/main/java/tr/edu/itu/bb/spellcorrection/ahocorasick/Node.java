package tr.edu.itu.bb.spellcorrection.ahocorasick;

import tr.edu.itu.bb.spellcorrection.util.CharacterUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * $Id$
 *
 * @author $Author$
 * @version $Revision$, $Date$
 */

public final class Node<T> implements Serializable{

    private int depth;
    private int subNodeCount;

    private List<T> outputs;
    private char c;

    private Node<T> parentNode;
    private Node<T> failNode;
    private Node<T>[] subNodes;

    public static final Node ROOT = new Node(0);

    private Node(int depth) {
        this.depth = depth;
        this.parentNode = null;
        this.subNodes = new Node[CharacterUtil.getCharacterSize()];
        this.outputs = new ArrayList<T>();
    }

    public Node(Node<T> parentNode, char c) {
        if(0 == (int)c){
            throw new IllegalArgumentException();
        }
        this.c = c;
        this.depth = parentNode.getDepth() + 1;
        this.parentNode = parentNode;
        this.subNodes = new Node[CharacterUtil.getCharacterSize()];
        this.outputs = new ArrayList<T>();
    }

    public Node(T output, Node<T> parentNode, char c) {
        this(parentNode, c);
        this.outputs.add(output);
    }

    public void expandNode(char[] chars, T output) {

        Node<T> node = this;

        for (int i = 0; i < chars.length; i++) {

            if(chars[i] == 0){
                throw new IllegalArgumentException();
            }
            int charIndex = CharacterUtil.getCharIndex(chars[i]);

            if(node.subNodes[charIndex] == null){ //should add new sub node

                Node<T> newNode;
                if(i == chars.length - 1){
                    newNode = new Node<T>(output, node, chars[i]);
                } else {
                    newNode = new Node<T>(node, chars[i]);
                }

                node.subNodes[charIndex] = newNode;
                node.subNodeCount++;
                node = newNode;

            } else {

                node = node.subNodes[charIndex];

                if(i == chars.length - 1){
                    node.getOutputs().add(output);
                }

            }

        }

    }

    public Node<T> getSubNode(char c){
        return this.getSubNodes()[CharacterUtil.getCharIndex(c)];
    }

    public void setSubNode(char c, Node<T> node) {
        this.getSubNodes()[CharacterUtil.getCharIndex(c)] = node;
    }

    public Node<T>[] getSubNodes() {
        return subNodes;
    }

    public Node<T> getFailNode() {
        return failNode;
    }

    public void setFailNode(Node<T> failNode) {
        this.failNode = failNode;
    }

    public int getDepth() {
        return depth;
    }

    public Node<T> getParentNode() {
        return parentNode;
    }

    public char getC() {
        return c;
    }

    public int getSubNodeCount() {
        return subNodeCount;
    }

    public List<T> getOutputs() {
        return outputs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node<T> node = (Node<T>) o;

        if (depth != node.depth) return false;
        if (c != node.c) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = depth;
        result = 31 * result + (int) c;
        return result;
    }

}
