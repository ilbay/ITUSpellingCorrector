package tr.edu.itu.bb.spellcorrection.util;

import tr.edu.itu.bb.spellcorrection.ahocorasick.Node;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * $Id$
 *
 * @author $Author$
 * @version $Revision$, $Date$
 */

public final class CharacterUtil {

    private static Map<Character, Integer> chacarterMapping;

    public static void initCharacterMapping(String charFile) throws IOException {

        long start = System.currentTimeMillis();

        CharacterUtil.chacarterMapping = new HashMap<Character, Integer>();

        List<String> lines = Util.readFile(charFile, Util.UTF8_ENCODING, true);

        int i = 0;

        for (String line : lines) {

            Character c = line.charAt(0);

            if(!CharacterUtil.chacarterMapping.containsKey(c)){
                CharacterUtil.chacarterMapping.put(c, i);
                i++;
            }

        }

        System.out.println("Character file (" + String.valueOf(CharacterUtil.chacarterMapping.keySet().size()) + " chars) read in " + (System.currentTimeMillis() - start) + " millis");

    }

    public static int getCharacterSize() {
        return CharacterUtil.chacarterMapping.size();
    }

    public static Set<Character> getValidCharacters() {
        return CharacterUtil.chacarterMapping.keySet();
    }

    public static int getCharIndex(char c) {

        if(CharacterUtil.chacarterMapping.containsKey(c)){

            return CharacterUtil.chacarterMapping.get(c);

        } else {

            throw new IllegalArgumentException("unknown char: \"" + c + "\", code: " + (int)c);

        }

    }

    public static char[] getChars(Node node) {

        char[] chars = new char[node.getSubNodeCount()];

        int i = 0;
        for (Node subNode : node.getSubNodes()) {

            if (subNode != null) {
                chars[i++] = subNode.getC();
            }

        }

        return chars;

    }

    public static String getText(Node node) {

        char[] text = new char[node.getDepth()];

        Node parent = node;

        while (parent.getDepth() != 0) {

            text[parent.getDepth() - 1] = parent.getC();
            parent = parent.getParentNode();

        }

        return new String(text);

    }


}

