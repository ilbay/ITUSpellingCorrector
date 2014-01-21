package tr.edu.itu.bb.spellcorrection.levenshtein;

import java.util.List;

/**
 * $Id$
 *
 * @author $Author$
 * @version $Revision$, $Date$
 */

public class Transformation {

    private TransformationType transformationType;
    private List<Rule> rules;
    private Transformation priorTransformation;
    private int i;
    private int j;

    public Transformation(List<Rule> rules, TransformationType transformationType, Transformation priorTransformation, int i, int j) {
        this.rules = rules;
        this.priorTransformation = priorTransformation;
        this.transformationType = transformationType;
        this.i = i;
        this.j = j;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public Transformation getPriorTransformation() {
        return priorTransformation;
    }

    public TransformationType getTransformationType() {
        return transformationType;
    }

    public int getI() {
        return i;
    }

    public int getJ() {
        return j;
    }

    public String getChange() {

        switch (transformationType) {

            case NO_CHANGE:
                return rules.get(0).getAfter();
            case ADD:
                return "(+" + rules.get(0).getAfter() + ")";
            case REMOVE:
                return "(-" + rules.get(0).getBefore() + ")";
            case SUBSTITUTE:
                return "(" + rules.get(0).getBefore() + "->" + rules.get(0).getAfter() + ")";
            default:
                throw new IllegalArgumentException();
        }

    }

    @Override
    public String toString() {

        if(priorTransformation == null){

            return getChange();

        } else {

            return getChange() + "(" + priorTransformation.getI() + "," + priorTransformation.getJ()+ ")";

        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Transformation that = (Transformation) o;

        if (i != that.i) return false;
        if (j != that.j) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = i;
        result = 31 * result + j;
        return result;
    }
}
