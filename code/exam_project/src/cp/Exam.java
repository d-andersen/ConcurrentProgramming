package cp;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

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
public class Exam {

    /** Try to adapt concurrency level to system */
    private static final int CORES = Runtime.getRuntime().availableProcessors() + 1;

    /** Executors for methods m1, m2, and m3 respectively */
    private static final ExecutorService EXECUTOR_M1 = Executors.newFixedThreadPool(CORES / 2);
    private static final ExecutorService EXECUTOR_M2 = Executors.newFixedThreadPool(CORES / 2);
    private static final ExecutorService EXECUTOR_M3 = Executors.newFixedThreadPool(CORES / 2);

    /** Futures created in relation to methods m1, m2, and m3 respectively */
    private static final ConcurrentLinkedDeque<Future<Result>> FUTURES_M1 = new ConcurrentLinkedDeque<>();
    private static final ConcurrentLinkedDeque<Future<Result>> FUTURES_M2 = new ConcurrentLinkedDeque<>();
    private static final ConcurrentLinkedDeque<Future<Tuple>> FUTURES_M3 = new ConcurrentLinkedDeque<>();

    /** CountDownLatches for methods m1, m2, and m3 respectively */
    private static final CountDownLatch LATCH_M1 = new CountDownLatch(1);
    private static final CountDownLatch LATCH_M2 = new CountDownLatch(1);
    private static final CountDownLatch LATCH_M3 = new CountDownLatch(1);

    /**
     * This method recursively visits a directory to find all the text
     * files contained in it and its subdirectories.
     *
     * You must consider only files ending with a ".txt" suffix.
     * You are guaranteed that they will be text files.
     *
     * You can assume that each text file contains a (non-empty)
     * comma-separated sequence of numbers. For example: 100,200,34,25
     * There won't be any new lines, spaces, etc., and the sequence never
     * ends with a comma.
     * You are guaranteed that each number will be at least or equal to
     * 0 (zero), i.e., no negative numbers.
     *
     * The search is recursive: if the directory contains subdirectories,
     * these are also searched and so on so forth (until there are no more
     * subdirectories).
     *
     * This method returns a list of results.
     * The list contains a result for each text file that you find.
     * Each {@link Result} stores the path of its text file, and the lowest
     * number (minimum) found inside of the text file.
     *
     * @param dir the directory to search
     * @return a list of results ({@link Result}), each giving the lowest number
     *         found in a file
     */
    public static List<Result> m1(Path dir) {

        directorySearch(dir, 0, 1);

        try {
            // wait for at least one future to be made or until timeout
            LATCH_M1.await(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.out.println("Interrupted");
        }

        return processM1();
    }

    /**
     * This method recursively visits a directory for text files with suffix
     * ".dat" (notice that it is different than the one before)
     * contained in it and its subdirectories.
     *
     * You must consider only files ending with a .dat suffix.
     * You are guaranteed that they will be text files.
     *
     * Each .dat file contains some lines of text,
     * separated by the newline character "\n".
     * You can assume that each line contains a (non-empty) comma-separated
     * sequence of numbers. For example: 100,200,34,25
     *
     * This method looks for a .dat file that contains a line whose numbers,
     * when added together (total), amount to at least (>=) parameter min.
     * Once this is found, the method can return immediately (without waiting to
     * analyse also the other files).
     *
     * The return value is a result that contains:
     * - path: the path to the text file that contains the line that respects the
     * condition;
     * - number: the line number, starting from 1 (e.g., 1 if it is the first line,
     * 3 if it is the third, etc.)
     *
     */
    public static Result m2(Path dir, int min) {

        directorySearch(dir, min, 2);

        try {
            // wait for at least one future to be made or until timeout
            LATCH_M2.await(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.out.println("Interrupted");
        }

        return processM2(dir);
    }

    /**
     * Computes overall statistics about the occurrences of numbers in a directory.
     *
     * This method recursively searches the directory for all numbers in all lines
     * of .txt and .dat files and returns a {@link Stats} object containing the
     * statistics of interest. See the documentation of {@link Stats}.
     */
    public static Stats m3(Path dir) {

        directorySearch(dir, 0, 3);

        try {
            // wait for at least one future to be made or until timeout
            LATCH_M3.await(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.out.println("Interrupted");
        }

        return processM3();
    }

    // ----------------------------------------------------------------------
    // IMPLEMENTATION OF SUPPORT METHODS
    // ----------------------------------------------------------------------

    /**
     * Initiates a recursive search of the root directory. The search is
     * concurrent. The method recursively searches all sub directories and looks
     * for either .txt files, .dat files or both depending on the id of the
     * caller. For each file found, an appropriate search method is initiated
     * and submitted as a task to the respective executor.
     *
     * @param dir a Path to a directory.
     * @param min a minimum line sum (only relevant when method m2 initiates the
     *            search).
     * @param m   the id of the original caller method.
     */
    private static void directorySearch(Path dir, int min, int m) {
        // try-with-resource to have stream auto-close when done
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(dir)) {
            for (Path path : directoryStream) {
                try {
                    switch (m) {
                        case 1:
                            if (Files.isDirectory(path)) {
                                EXECUTOR_M1.submit(() -> directorySearch(path, min, m));
                            } else if (path.toString().endsWith(".txt")) {
                                FUTURES_M1.add(EXECUTOR_M1.submit(() -> searchForMinValue(path)));
                            }
                            break;
                        case 2:
                            if (Files.isDirectory(path)) {
                                EXECUTOR_M2.submit(() -> directorySearch(path, min, m));
                            } else if (path.toString().endsWith(".dat")) {
                                FUTURES_M2.add(EXECUTOR_M2.submit(() -> searchForLineSum(path, min)));
                            }
                            break;
                        case 3:
                            if (Files.isDirectory(path)) {
                                EXECUTOR_M3.submit(() -> directorySearch(path, min, m));
                            } else if (path.toString().endsWith(".txt") || path.toString().endsWith(".dat")) {
                                FUTURES_M3.add(EXECUTOR_M3.submit(() -> makeFileStats(path)));
                            }
                            break;
                        default:
                            break;
                    }
                } catch (RejectedExecutionException ignored) {
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Looks for the lowest number in this file. Once fully searched, a Result
     * containing the Path of this file and the lowest number found is returned.
     *
     * @param path the Path of this file.
     * @return a Result containing the Path of this file and the lowest number
     *         found.
     */
    private static Result searchForMinValue(Path path) {
        File fileIn = path.toFile();
        int i;
        int min = Integer.MAX_VALUE;

        // try-with-resource to have stream auto-close when done
        try (InputStream fileInputStream = new FileInputStream(fileIn);
                BufferedReader buffer = new BufferedReader(new InputStreamReader(fileInputStream))) {
            String line = buffer.readLine();
            String[] values = line.split(",");
            for (String str : values) {
                i = Integer.parseInt(str);
                if (i < min) {
                    min = i;
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        LATCH_M1.countDown();
        return new SearchResult(path, min);
    }

    /**
     * Searches all lines of this file for a line sum greater than or equal to
     * the parameter min. If such a line is found, the method immediately
     * returns a Result containing the Path of this file and the line number the
     * sum was found. As no further searching is needed, the method tries to
     * terminate the executor for method m2 and in addition releases the latch
     * for the main thread. If no line satisfying the condition is found, the
     * method returns the Path of this file and a line number -1.
     *
     * @param path the Path of this file.
     * @param min  the line sum to look for.
     * @return a Result containing the Path of this file and the line number in
     *         which a line sum greater than or equal to the min parameter was
     *         found. If no line satisfying the condition was found, the line
     *         number is set to -1.
     */
    private static Result searchForLineSum(Path path, int min) {
        File fileIn = path.toFile();
        int lineNum = 0;
        int lineSum = 0;
        String line;

        // try-with-resource to have stream auto-close when done
        try (InputStream fileInputStream = new FileInputStream(fileIn);
                BufferedReader buffer = new BufferedReader(new InputStreamReader(fileInputStream))) {
            while ((line = buffer.readLine()) != null) {
                lineNum++;
                String[] values = line.split(",");

                for (String str : values) {
                    lineSum += Integer.parseInt(str);
                }
                if (lineSum >= min) {
                    terminate(2);
                    LATCH_M2.countDown();
                    return new SearchResult(path, lineNum);
                }
                lineSum = 0;
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        return new SearchResult(path, -1);
    }

    /**
     * Gathers the statistics of a file. Specifically it makes a histogram of all
     * numbers in the file and also gathers the total sum of all these numbers.
     * Once fully searched, the statistics of this file is returned as a Tuple
     * containing the histogram and a LargeSearchResult, which contains the Path
     * and the total sum.
     *
     * @param path the Path of this file.
     * @return a Tuple containing the histogram of this file and a
     *         LargeSearchResult. The LargeSearchResult contains the Path of
     *         this file, and the total sum of this file.
     */
    private static Tuple makeFileStats(Path path) {
        File fileIn = path.toFile();
        Map<Integer, Integer> fileHistogram = new HashMap<>();
        int i;
        int lineSum = 0;
        long totalSum = 0;
        String line;

        // try-with-resource to have stream auto-close when done
        try (InputStream fileInputStream = new FileInputStream(fileIn);
                BufferedReader buffer = new BufferedReader(new InputStreamReader(fileInputStream))) {
            while ((line = buffer.readLine()) != null) {
                String[] values = line.split(",");
                for (String str : values) {
                    lineSum += Integer.parseInt(str);
                    i = Integer.parseInt(str);
                    fileHistogram.merge(i, 1, (a, b) -> a + b);
                }
                totalSum += lineSum;
                lineSum = 0;
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        LATCH_M3.countDown();
        return new Tuple(fileHistogram, new LargeSearchResult(path, totalSum));
    }

    /**
     * Collects the Results found for each .txt file in a List. Once the Results of
     * all files have been collected, the List of Results is then returned.
     *
     * @return a List of Results.
     */
    private static List<Result> processM1() {
        List<Result> resultList = new ArrayList<>();

        // runs in main thread
        while (!FUTURES_M1.isEmpty()) {
            try {
                try {
                    resultList.add(FUTURES_M1.removeFirst().get());
                } catch (NoSuchElementException e) {
                    System.out.println("No element found");
                }
            } catch (InterruptedException | ExecutionException e) {
                System.out.println("Interrupted");
            }
        }
        terminate(1);
        return resultList;
    }

    /**
     * Waits for the Result satisfying the desired condition to be found, and then
     * returns immediately. If no such Result is found, the method returns a Result
     * containing the Path of the root directory and a line number -1.
     *
     * @param rootDir the root directory of the caller.
     * @return a Result containing the Path of the file and the line number where
     *         a line sum greater than or equal to the desired value was found. If
     *         no file satisfying the conditions was found, the Path of the root
     *         directory and a line number of -1 is returned.
     */
    private static Result processM2(Path rootDir) {
        Result res;

        // runs in main thread
        while (!FUTURES_M2.isEmpty()) {
            try {
                try {
                    res = FUTURES_M2.peekFirst().get();
                    if (res.number() != -1) {
                        terminate(2);
                        return res;
                    }
                    FUTURES_M2.removeFirst();
                } catch (NoSuchElementException e) {
                    System.out.println("No element found");
                }
            } catch (InterruptedException | ExecutionException e) {
                System.out.println("Interrupted");
            }
        }
        terminate(2);
        return new SearchResult(rootDir, -1);
    }

    /**
     * Collects the statistics found for each file. Once the statistics of all files
     * have been collected, the combined statistics are returned as a Stats object.
     *
     * @return the Stats object for the root directory of the caller.
     */
    private static Stats processM3() {
        Tuple tuple;
        SearchStats stats = new SearchStats();

        // runs in main thread
        while (!FUTURES_M3.isEmpty()) {
            try {
                try {
                    tuple = FUTURES_M3.removeFirst().get();
                    stats.addToHistogram(tuple.getMap());
                    stats.addToResultList(tuple.getResult());
                } catch (NoSuchElementException e) {
                    System.out.println("No element found");
                }
            } catch (InterruptedException | ExecutionException e) {
                System.out.println("Interrupted");
            }
        }
        terminate(3);
        return stats;
    }

    /**
     * Terminates the executor of the corresponding m method. For example, an input
     * parameter of 1, terminates EXECUTOR_M1 servicing method m1 and so on.
     *
     * @param m the id of the method whose executor is to be terminated.
     */
    private static void terminate(int m) {
        switch (m) {
            case 1:
                EXECUTOR_M1.shutdownNow();
                try {
                    EXECUTOR_M1.awaitTermination(1, TimeUnit.MILLISECONDS);
                } catch (InterruptedException ignored) {
                }
                EXECUTOR_M1.shutdownNow();
                break;
            case 2:
                EXECUTOR_M2.shutdownNow();
                try {
                    EXECUTOR_M2.awaitTermination(1, TimeUnit.MILLISECONDS);
                } catch (InterruptedException ignored) {
                }
                EXECUTOR_M2.shutdownNow();
                break;
            case 3:
                EXECUTOR_M3.shutdownNow();
                try {
                    EXECUTOR_M3.awaitTermination(1, TimeUnit.MILLISECONDS);
                } catch (InterruptedException ignored) {
                }
                EXECUTOR_M3.shutdownNow();
                break;
            default:
                break;
        }
    }

} // end class