package tr.edu.itu.bb.spellcorrection.suffixtree;

/**
 * @author: erenbekar
 * @since: 9/2/13 10:44 PM
 */

public class PairedResponse<X, Y> {

    private X first;
    private Y second;

    public PairedResponse(X first, Y second) {

        this.first = first;
        this.second = second;

    }

    public X getFirst() {
        return first;
    }

    public Y getSecond() {
        return second;
    }
}
