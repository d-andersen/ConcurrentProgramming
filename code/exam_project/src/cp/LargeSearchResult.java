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

public class LargeSearchResult {

    private final Path path;
    private final long number;

    /**
     * The file Path of this result.
     *
     * @return the file Path of this result.
     */
    public Path path() { return this.path; }

    /**
     * The number of this result.
     *
     * @return the number of this result as a long.
     */
    public long number() { return this.number; }

    /**
     * Constructor that creates a new LargeSearchResult, which can store a
     * number as a long instead of an int.
     *
     * @param path   a Path.
     * @param number a number.
     */
    public LargeSearchResult(Path path, long number) {
        this.path = path;
        this.number = number;
    }

} // end class