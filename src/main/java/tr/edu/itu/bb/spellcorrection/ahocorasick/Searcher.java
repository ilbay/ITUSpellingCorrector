package tr.edu.itu.bb.spellcorrection.ahocorasick;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * $Id$
 *
 * @author $Author$
 * @version $Revision$, $Date$
 */

public class Searcher<T> implements Iterator<SearchResult<T>> {

    private SearchResult<T> currentResults;
    private AhoCorasick<T> ahoCorasick;

    public Searcher(AhoCorasick<T> ahoCorasick, SearchResult<T> currentResults) {
        this.ahoCorasick = ahoCorasick;
        this.currentResults = currentResults;
    }

    @Override
    public boolean hasNext() {
        return this.currentResults != null;
    }

    @Override
    public SearchResult<T> next() {

        if(hasNext()){

            SearchResult searchResultToReturn = currentResults;

            currentResults = ahoCorasick.continueSearch(currentResults);

            return searchResultToReturn;

        } else {

            throw new NoSuchElementException();
        }

    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
