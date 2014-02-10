package tr.edu.itu.bb.spellcorrection.levenshtein;

import java.util.ArrayList;
import java.util.List;

public class CandidateWord implements Comparable<CandidateWord>{
    private String candidateWord;
    private double totalWeight;
    private List<Rule> appliedRules;
    
    protected CandidateWord()
    {
    	
    }
    
    public CandidateWord(String candidateWord)
    {
    	this.candidateWord = candidateWord;
    	this.appliedRules = new ArrayList<Rule>();
    	this.totalWeight = 0;
    }
    
    public CandidateWord buildCandidateWord()
    {
    	CandidateWord newCandidateWord = (CandidateWord)this.clone();
    	int changeInIndex = 0;
    	String initialString = "";
    	for(Rule rule : this.appliedRules)
    	{
        	try
        	{
        		initialString = newCandidateWord.candidateWord.substring(0, rule.getIndex()+changeInIndex);
        	}
        	catch(IndexOutOfBoundsException ex)
        	{
        		initialString = "";
        	}
    		newCandidateWord.candidateWord = initialString + rule.getAfter() + newCandidateWord.candidateWord.substring(rule.getBefore().length()+changeInIndex);
    		changeInIndex += (rule.getAfter().length() - rule.getBefore().length());
    	}
    	return newCandidateWord;
    }
    
    public CandidateWord applyRule(Rule rule)
    {
    	CandidateWord newCandidateWord = (CandidateWord)this.clone();

    	newCandidateWord.appliedRules.add(rule);
    	newCandidateWord.totalWeight += rule.getLikelihood();
    	
    	return newCandidateWord;
    }
    
    public CandidateWord applyRule(int firstIndex, Rule rule)
    {
    	CandidateWord newCandidateWord = (CandidateWord)this.clone();
    	
    	int lastIndex = firstIndex;
    	for(char c : rule.getBefore().toCharArray())
    	{
    		if(lastIndex >= this.candidateWord.length() || c != this.candidateWord.charAt(lastIndex))
    			return null;
    		lastIndex++;
    	}

    	if( lastIndex > this.candidateWord.length() ) {
    		newCandidateWord.candidateWord = this.candidateWord.substring(0, firstIndex) + rule.getAfter();
    	}
    	else {
    		newCandidateWord.candidateWord = this.candidateWord.substring(0, firstIndex) + rule.getAfter() + this.candidateWord.substring(lastIndex);
    	}

    	newCandidateWord.appliedRules.add(rule);
    	newCandidateWord.totalWeight += rule.getLikelihood();
    	
    	return newCandidateWord;
    }
    
    @Override
    public int hashCode() {
        return candidateWord != null ? candidateWord.hashCode() : 0;
    }
    
    @Override
    public String toString() {
        return "Candidate{" +
                "candidateWord='" + candidateWord + '\'' +
                ", totalWeight=" + totalWeight +
                ", appliedRules=" + appliedRules +
                "}";
    }
    
    public Object clone()
    {
    	CandidateWord cw = new CandidateWord();
    	
    	cw.candidateWord = this.candidateWord;
    	cw.appliedRules = new ArrayList<Rule>();
    	cw.totalWeight = this.totalWeight;

    	for(Rule rule : this.appliedRules)
    	{
    		cw.appliedRules.add(rule);
    	}
    	
    	return cw;
    }
    
    public List<Rule> getAppliedRules()
    {
    	return this.appliedRules;
    }
    
    public String getCandidateWord()
    {
    	return this.candidateWord;
    }
    
    public double getTotalWeight()
    {
    	return this.totalWeight;
    }
    
    @Override
    public boolean equals(Object o)
    {
    	CandidateWord cw = (CandidateWord)o;
    	return cw.candidateWord.equals(this.candidateWord) && this.totalWeight == cw.totalWeight;
    }

	@Override
	public int compareTo(CandidateWord o) {
		return (int)Math.signum(o.totalWeight - this.totalWeight);
	}
}