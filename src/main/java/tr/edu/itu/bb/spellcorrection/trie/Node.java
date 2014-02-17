package tr.edu.itu.bb.spellcorrection.trie;

import java.io.Serializable;

import tr.edu.itu.bb.spellcorrection.util.CharacterUtil;

/**
 * User: eren
 * Date: 4/29/13
 * Time: 10:52 PM
 */
public class Node implements Serializable{

    private int depth;
    private int subNodeCount;

    private String output;
    private char c;

    private Node parentNode;
    private Node[] subNodes;

    public static final Node ROOT = new Node();

    private Node() {
        this.depth = 0;
        this.parentNode = null;
        this.subNodes = new Node[CharacterUtil.getCharacterSize()];
    }

    public Node(Node parentNode, char c) {
        this.c = c;
        this.depth = parentNode.getDepth() + 1;
        this.parentNode = parentNode;
        this.subNodes = new Node[CharacterUtil.getCharacterSize()];
    }

    public Node(String label, Node parentNode, char c) {
        this(parentNode, c);
        this.output = label;
    }

    public void expandNode(char[] chars) {

        Node node = this;

        for (int i = 0; i < chars.length; i++) {

            int charIndex = CharacterUtil.getCharIndex(chars[i]);

            if(node.subNodes[charIndex] == null){ //should add new sub node

                Node newNode;
                if(i == chars.length -1){
                    newNode = new Node(new String(chars), node, chars[i]);
                } else {
                    newNode = new Node(node, chars[i]);
                }

                node.subNodes[charIndex] = newNode;
                node.subNodeCount++;
                node = newNode;

            } else {

                node = node.subNodes[charIndex];

            }

        }

    }

    public Node getSubNode(char c){
        return this.getSubNodes()[CharacterUtil.getCharIndex(c)];
    }

    public void setSubNode(char c, Node node) {
        this.getSubNodes()[CharacterUtil.getCharIndex(c)] = node;
    }

    public Node[] getSubNodes() {
        return subNodes;
    }

    public int getDepth() {
        return depth;
    }

    public Node getParentNode() {
        return parentNode;
    }

    public char getC() {
        return c;
    }

    public int getSubNodeCount() {
        return subNodeCount;
    }

    public String getOutput() {
        return output;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

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

    private static String getText(Node node) {

        char[] text = new char[node.getDepth()];

        Node parent = node;

        while (parent.getDepth() != 0) {

            text[parent.getDepth() - 1] = parent.getC();
            parent = parent.getParentNode();

        }

        return new String(text);

    }
}
