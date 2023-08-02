package cp;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

public class SearchStats implements Stats {

    private int mostFrequent;
    private int leastFrequent;
    private int currentMaxCount = 0;
    private int currentMinCount = Integer.MAX_VALUE;
    private Map<Integer, Integer> histogram = new HashMap<>();
    private final List<LargeSearchResult> resultList = new ArrayList<>();

    /**
     * Returns the number of times that a number was found (across all files).
     *
     * @param number the number to look up in the histogram.
     * @return the number of times the look-up number was found or -1 if no such
     *         number was found.
     */
    @Override
    public int occurrences(int number) {
        if (this.histogram.get(number) != null) {
            return this.histogram.get(number);
        } else
            return -1;
    }

    /**
     * Returns the number that was found the most times (across all files).
     *
     * @return the number found the most times.
     */
    @Override
    public int mostFrequent() {
        if (!this.histogram.isEmpty()) {
            histogram.forEach((key, value) -> {
                if (value > currentMaxCount) {
                    this.currentMaxCount = value;
                    this.mostFrequent = key;
                }
            });
            return this.mostFrequent;
        } else
            return -1;
    }

    /**
     * Returns the number that was found the least times (across all files).
     *
     * @return the number found the most times.
     */
    @Override
    public int leastFrequent() {
        if (!this.histogram.isEmpty()) {
            histogram.forEach((key, value) -> {
                if (value < currentMinCount) {
                    this.currentMinCount = value;
                    this.leastFrequent = key;
                }
            });
            return this.leastFrequent;
        } else
            return -1;
    }

    /**
     * Returns a list of all the files found in the directory, ordered from the
     * one that contains numbers whose sum across all lines is the smallest
     * (first of the list), to the one that contains numbers whose sum is the
     * greatest (last of the list).
     *
     * @return a List of Path objects sorted by the sum of the file each Path object
     *         represents.
     */
    @Override
    public List<Path> byTotals() {
        List<Path> sortedBySumList = new ArrayList<>(resultList.size());

        resultList.sort(Comparator.comparingLong(LargeSearchResult::number));

        resultList.forEach(result -> {
            sortedBySumList.add(result.path());
        });
        return sortedBySumList;
    }

    /**
     * Adds a result to the resultList.
     *
     * @param result a Result to be added to the resultList.
     */
    public void addToResultList(LargeSearchResult result) {
        this.resultList.add(result);
    }

    /**
     * Add a map representing a histogram for a file to the total histogram.
     * This method takes in a map representing a histogram for a file, and then
     * uses a stream to combine the input map with the map acting as the
     * collecting histogram being kept by this class.
     *
     * @param map a map to be added to the total histogram.
     */
    public void addToHistogram(Map<Integer, Integer> map) {
        this.histogram = Stream.concat(this.histogram.entrySet().stream(),
            map.entrySet().stream()).collect(Collectors.toMap(
                Map.Entry::getKey, Map.Entry::getValue, Integer::sum));
    }

} // end class