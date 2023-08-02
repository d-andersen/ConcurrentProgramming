package cp;

import java.nio.file.Path;

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

public class SearchResult implements Result {

    private final Path path;
    private final int number;

    /**
     * The file Path of this result.
     *
     * @return the file Path of this result.
     */
    @Override
    public Path path() { return this.path; }

    /**
     * The number of this result.
     *
     * @return the number of this result.
     */
    @Override
    public int number() { return this.number; }

    /**
     * Constructor that creates a new Result, with a given Path and a number.
     *
     * @param path   a Path.
     * @param number a number.
     */
    public SearchResult(Path path, int number) {
        this.path = path;
        this.number = number;
    }

} // end class