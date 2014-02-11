package tr.edu.itu.bb.spellcorrection.levenshtein;

public class WordInformation implements Comparable<WordInformation>{

	private String root;
	private String suffix;
	private double totalWeight;
	private int index;
	
	public WordInformation(String root, String suffix, double totalWeight, int index)
	{
		this.root = root;
		this.suffix = suffix;
		this.totalWeight = totalWeight;
		this.index = index;
	}

	public String getRoot() {
		return root;
	}

	public String getSuffix() {
		return suffix;
	}

	public double getTotalWeight() {
		return totalWeight;
	}
	
	public int getIndex()
	{
		return index;
	}
	
	@Override
	public String toString()
	{
		return this.root + this.suffix +
				" {" +
			   "root='" + this.root + "', " +
			   "suffix='" + this.suffix + "', " +
			   "totalWeight='" + this.totalWeight + "', " +
			   "index='" + this.index + "'" +
			   "}";
	}
	
	@Override
	public int hashCode()
	{
		return this.root.hashCode() * this.root.hashCode() + 2 * this.root.hashCode() * this.suffix.hashCode() + this.suffix.hashCode() * this.suffix.hashCode();
	}
	
	@Override
	public boolean equals(Object o)
	{
		WordInformation w = (WordInformation)o;
		return this.root.equals(w.root) && this.suffix.equals(w.suffix) && this.totalWeight == w.totalWeight;
	}

	@Override
	public int compareTo(WordInformation o) {
		return (int)Math.signum(o.totalWeight - this.totalWeight);
	}
}