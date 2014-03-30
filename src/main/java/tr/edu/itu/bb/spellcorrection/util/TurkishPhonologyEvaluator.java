package tr.edu.itu.bb.spellcorrection.util;

import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;

public class TurkishPhonologyEvaluator {
	
	private static Character[] VOWELS = {'a', 'e', 'ı', 'i', 'o', 'ö', 'u', 'ü'};
	private static Set<Character> VOWELS_SET = new HashSet<>(Arrays.asList(VOWELS));
	
	private static Character[] CONSONATS = {'b', 'c', 'ç', 'd', 'f', 'g', 'ğ', 'h', 'j', 'k', 'l', 'm', 'n', 'p', 'r', 's', 'ş', 't', 'v', 'y', 'z'};
	private static Set<Character> CONSONATS_SET = new HashSet<>(Arrays.asList(CONSONATS));
	
	public static boolean isVowel(char ch)
	{
		return VOWELS_SET.contains(ch);
	}
	
	public static boolean isConsonant(char ch)
	{
		return CONSONATS_SET.contains(ch);
	}

}
