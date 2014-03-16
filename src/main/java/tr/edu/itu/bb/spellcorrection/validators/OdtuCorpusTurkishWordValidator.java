package tr.edu.itu.bb.spellcorrection.validators;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import tr.edu.itu.bb.spellcorrection.trie.Trie;

public class OdtuCorpusTurkishWordValidator implements TurkishWordValidator{
	
	private Trie corpusTrie = new Trie();
	private final String ODTU_CORPUS_FILE = "data\\model\\odtu\\odtu_corpus.txt";
	
	public OdtuCorpusTurkishWordValidator()
	{
		BufferedReader reader = null;
		try
		{
			File file = new File(ODTU_CORPUS_FILE);
			FileInputStream inputStream = new FileInputStream(file);
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF8");
			reader = new BufferedReader(inputStreamReader);
			
			String line;
			while ((line = reader.readLine()) != null)
			{
				this.corpusTrie.addWord(line.toLowerCase());
			}
			
			reader.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public boolean isTurkish(String word) {
		return this.corpusTrie.contains(word);
	}

}
