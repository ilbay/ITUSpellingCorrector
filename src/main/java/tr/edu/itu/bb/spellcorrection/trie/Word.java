package tr.edu.itu.bb.spellcorrection.trie;

public class Word {
	
	public String getStem() {
		return stem;
	}
	
	public void setStem(String stem) {
		this.stem = stem;
	}
	
	public String getSuffix() {
		return suffix;
	}
	
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	
	private String stem;
	private String suffix;

}
