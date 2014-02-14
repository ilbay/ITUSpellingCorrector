package tr.edu.itu.bb.spellcorrection.levenshtein;

public class CorrectedWord implements Comparable<CorrectedWord>{
	private String word;
	private double totalWeight;
	
	public CorrectedWord(String word, double totalWeight)
	{
		this.word = word;
		this.totalWeight = totalWeight;
	}

	public String getWord() {
		return word;
	}

	public double getTotalWeight() {
		return totalWeight;
	}
	
	public String toString()
	{
		return  " {" +
				"word='" + this.word + "', " +
				"totalWeight='" + this.totalWeight + "'" +
				"}";
	}
	
	public boolean equals(Object o)
	{
		CorrectedWord cw = (CorrectedWord)o;
		return this.word.equals(cw.word) && this.totalWeight == cw.totalWeight;
	}
	
	public int hashCode()
	{
		return this.word.hashCode() * (int)(-Math.round(this.totalWeight)) + this.word.hashCode();
	}

	@Override
	public int compareTo(CorrectedWord o) {
		return (int)Math.signum(this.totalWeight - o.totalWeight);
	}
}