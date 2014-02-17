package tr.edu.itu.bb.spellcorrection.levenshtein;

import java.io.Serializable;

/**
 * User: eren
 * Date: 5/4/13
 * Time: 4:14 PM
 */
public class Rule implements Comparable<Rule>, Serializable{

    private String before;
    private String after;
    private double likelihood;
    private int count;
    private int index;

    public Rule(String before, String after) {
        this.after = after;
        this.before = before;
    }

    public String getBefore() {
        return before;
    }

    public String getAfter() {
        return after;
    }

    public double getLikelihood() {
        return likelihood;
    }

    public void setLikelihood(double likelihood) {
        this.likelihood = likelihood;
    }

    public int getCount() {
        return count;
    }
    
    public int getIndex()
    {
    	return index;
    }
    
    public void setIndex(int index)
    {
    	this.index = index;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Rule rule = (Rule) o;

        if (!after.equals(rule.after)) return false;
        if (!before.equals(rule.before)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = before.hashCode();
        result = 31 * result + after.hashCode();
        return result;
    }
    
    public Object clone()
    {
    	Rule rule = new Rule(this.before,this.after);
    	rule.likelihood = this.likelihood;
    	rule.index = this.index;
    	rule.count = this.count;
    	return rule;
    }

    @Override
    public String toString() {
        return "Rule{" +
                "before='" + before + '\'' +
                ", after='" + after + '\'' +
                ", likelihood=" + likelihood +
                ", count=" + count +
                ", index=" + index +
                "}";
    }

    @Override
    public int compareTo(Rule o) {

        int likelihood = Double.valueOf(o.getLikelihood()).compareTo(this.getLikelihood());

        if(likelihood == 0){

            if(this.getBefore().length() == o.getBefore().length()){

                return this.getBefore().compareTo(o.getBefore());

            } else {

                return Integer.valueOf(this.getBefore().length()).compareTo(o.getBefore().length());

            }


        } else {

            return likelihood;

        }

    }
}
