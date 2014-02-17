package tr.edu.itu.bb.spellcorrection.levenshtein;

import java.util.ArrayList;
import java.util.List;

public class Candidate implements Comparable<Candidate>{
    private String candidateWord;
    private double totalWeight;
    private List<Rule> appliedRules;
    
    protected Candidate()
    {
    }
    
    public Candidate(String candidateWord)
    {
    	this.candidateWord = candidateWord;
    	this.appliedRules = new ArrayList<Rule>();
    	this.totalWeight = 0;
    }
    
    public Candidate buildCandidateWord()
    {
    	Candidate newCandidateWord = (Candidate)this.clone();
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
    		newCandidateWord.candidateWord = initialString + rule.getAfter() + newCandidateWord.candidateWord.substring(rule.getIndex()+rule.getBefore().length()+changeInIndex);
    		changeInIndex += (rule.getAfter().length() - rule.getBefore().length());
    	}
    	return newCandidateWord;
    }
    
    public Candidate applyRule(Rule rule)
    {
    	Candidate newCandidateWord = (Candidate)this.clone();

    	newCandidateWord.appliedRules.add(rule);
    	newCandidateWord.totalWeight += rule.getLikelihood();
    	
    	return newCandidateWord;
    }
    
    public Candidate applyRule(int firstIndex, Rule rule)
    {
    	Candidate newCandidateWord = (Candidate)this.clone();
    	
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
    	Candidate cw = new Candidate();
    	
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
    	Candidate cw = (Candidate)o;
    	return cw.candidateWord.equals(this.candidateWord) && this.totalWeight == cw.totalWeight;
    }

	@Override
	public int compareTo(Candidate o) {
		return (int)Math.signum(o.totalWeight - this.totalWeight);
	}
}