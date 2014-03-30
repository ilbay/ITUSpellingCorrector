package tr.edu.itu.bb.spellcorrection.util;

import java.util.ArrayList;
import java.util.List;

public class TurkishSyllableGenerator {
	
	public static void main(String[] args)
	{
		System.out.println(TurkishSyllableGenerator.generateSyllableList("unutmak"));
		System.out.println(TurkishSyllableGenerator.generateSyllableList("yaşantı"));
		System.out.println(TurkishSyllableGenerator.generateSyllableList("asmak"));
		System.out.println(TurkishSyllableGenerator.generateSyllableList("kesmek"));
		System.out.println(TurkishSyllableGenerator.generateSyllableList("iste"));
		System.out.println(TurkishSyllableGenerator.generateSyllableList("seniseviyorum"));
		System.out.println(TurkishSyllableGenerator.generateSyllableList("akşamdan"));
		System.out.println(TurkishSyllableGenerator.generateSyllableList("sponsorluğu"));
	}
	
	public static List<String> generateSyllableList(String word)
	{
		if(word == null || word.length() == 0)
			return new ArrayList<String>();
		
		List<String> syllableList = new ArrayList<String>();
		int startIndex = 0;
		boolean previousCharacterConsonant = false;

		for(int i = 1; i < word.length(); ++i)
		{
			if(TurkishPhonologyEvaluator.isVowel(word.charAt(i)))
			{
				if(previousCharacterConsonant)
				{
					syllableList.add(word.substring(startIndex, i-1));
					startIndex = i-1;
					previousCharacterConsonant = false;
				}
			}
			else if (TurkishPhonologyEvaluator.isConsonant(word.charAt(i)))
			{
				if(!previousCharacterConsonant)
				{
					previousCharacterConsonant = true;
				}
				else
				{
					syllableList.add(word.substring(startIndex, i));
					startIndex = i;
					previousCharacterConsonant = false;
				}
			}
			else
			{
				//There is a character that does not belong to Turkish alphabet.
				return new ArrayList<String>();
			}
		}
		
		syllableList.add(word.substring(startIndex, word.length()));
		
		return syllableList;
	}
	
}
