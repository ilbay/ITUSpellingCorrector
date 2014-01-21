package tr.edu.itu.bb.spellcorrection.levenshtein;

/**
 * User: eren
 * Date: 5/2/13
 * Time: 9:59 PM
 */
public enum TransformationType {

    NO_CHANGE(0d),
    ADD(1d),
    REMOVE(1d),
    SUBSTITUTE(2d);

    private double cost;

    private TransformationType(double cost) {
        this.cost = cost;
    }

    public double getCost() {
        return cost;
    }

}
