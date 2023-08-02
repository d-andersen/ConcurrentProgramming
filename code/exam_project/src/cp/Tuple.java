package cp;

import java.util.Map;

/**
 * DM519 Concurrent Programming
 *
 * Exam Project
 *
 * Dennis Andersen -- deand17
 * University of Southern Denmark
 * May 6, 2018
 *
 * Supervisor: Fabrizio Montesi <fmontesi@imada.sdu.dk>
 */

public class Tuple {

    private final Map map;
    private final LargeSearchResult result;

    /**
     * Constructor that creates a Tuple containing a Map and a LargeSearchResult.
     *
     * @param map    a Map.
     * @param result a LargeSearchResult.
     */
    public Tuple(Map map, LargeSearchResult result) {
        this.map = map;
        this.result = result;
    }

    /**
     * The Map of this Tuple.
     *
     * @return the Map of this Tuple.
     */
    public Map getMap() { return this.map; }

    /**
     * The LargeSearchResult of this Tuple.
     *
     * @return the LargeSearchResult of this Tuple.
     */
    public LargeSearchResult getResult() { return this.result; }

} // end class