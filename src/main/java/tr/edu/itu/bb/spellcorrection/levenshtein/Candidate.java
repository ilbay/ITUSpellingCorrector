package tr.edu.itu.bb.spellcorrection.levenshtein;

import java.util.ArrayList;
import java.util.List;

/**
 * User: eren
 * Date: 5/6/13
 * Time: 1:16 AM
 */
public final class Candidate implements Comparable<Candidate>{

    private String candidateWord;
    private double totalWeight;
    private List<Rule> appliedRules;
    private boolean normalized;

    private Candidate() {
    }

    private Candidate(String candidateWord) {

        this.appliedRules = new ArrayList<Rule>();
        this.candidateWord = candidateWord;

    }

    public static Candidate fromMisspelled(String misspelledWord) {
        return new Candidate(misspelledWord);
    }

    public static List<Candidate> buildCandidates(String normalized){

        List<Candidate> candidates = new ArrayList<Candidate>();

        candidates.add(new Candidate(normalized));

        return candidates;
    }

    public List<Candidate> buildCandidates(Rule anotherRule){ //kadar'a a->e uygularsan kedar da olabilir, kader de. hepsi donmeli

        int indexOf = 0;
        int beforeLength = anotherRule.getBefore().length();

        List<Candidate> candidates = new ArrayList<Candidate>();

        boolean first = true;

        do{

            if(first){
                indexOf = this.candidateWord.indexOf(anotherRule.getBefore(), indexOf);
                first = false;
            } else {
                indexOf = this.candidateWord.indexOf(anotherRule.getBefore(), indexOf + 1);
            }

            if(indexOf != -1){

                Candidate candidate = new Candidate();

                candidate.appliedRules = new ArrayList<Rule>();

                for (Rule appliedRule : this.appliedRules) {
                    candidate.appliedRules.add(appliedRule);
                }
                candidate.appliedRules.add(anotherRule);

                candidate.totalWeight = this.totalWeight + anotherRule.getLikelihood();

                candidate.candidateWord = candidateWord.substring(0, indexOf) + anotherRule.getAfter() + candidateWord.substring(indexOf + beforeLength);

                candidates.add(candidate);

            }

        } while(indexOf != -1 && indexOf != candidateWord.length());

        return candidates;

    }

    public String getCandidateWord() {
        return candidateWord;
    }

    public double getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(double totalWeight) {
        this.totalWeight = totalWeight;
    }

    public List<Rule> getAppliedRules() {
        return appliedRules;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Candidate candidate = (Candidate) o;

        if (candidateWord != null ? !candidateWord.equals(candidate.candidateWord) : candidate.candidateWord != null)
            return false;

        return true;
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

    @Override
    public int compareTo(Candidate o) {
        return Double.valueOf(o.getTotalWeight()).compareTo(this.getTotalWeight());
    }
}
