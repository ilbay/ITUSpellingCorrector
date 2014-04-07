package tr.edu.itu.bb.spellcorrection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;

public class TrainingTest {
	public static void main(String[] args)
	{
		ItuNlpTools tools = ItuNlpTools.getInstance();

		String inputFile = "data/model/TestFileContainingSpace.txt";
		String outputFile = "data/evaluations/TotalOutputUniqueTrainingNotUniqueTest2014_04_06_4letters.txt";
		String notFoundData = "data/evaluations/TotalNotFoundUniqueTrainingNotUniqueTest2014_04_06_4letters.txt";
		
		int total = 0;
		int found = 0;
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(inputFile));
			BufferedWriter output = new BufferedWriter(new FileWriter(outputFile));
			BufferedWriter notFound = new BufferedWriter(new FileWriter(notFoundData));
			
			String line = br.readLine();
			
			while(line != null)
			{
				total++;
				line = line.toLowerCase();
				String words[] = line.split("\t");
				System.out.println(words[0] + " " + total);
				
				String correct = tools.execute(words[0]);
				output.write(words[0]+ "\t" + words[1] + "\t" + correct + "\n");
				
				if(!words[1].equals(correct))
				{
					notFound.write(words[0] + "\t" + words[1] + "\t" + correct + "\n");
				}
				else
				{
					found++;
				}
				
				line = br.readLine();
			}
			
			output.write("Found " + found + " of " + total + " words\n");
			
			br.close();
			output.close();
			notFound.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
