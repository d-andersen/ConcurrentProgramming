package cp;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * DM519 Concurrent Programming
 *
 * Exam Project
 *
 * Dennis Andersen -- deand17
 * University of Southern Denmark
 * May 2, 2018
 *
 *
 * This class is present only for helping you in testing your software.
 * It will be completely ignored in the evaluation.
 *
 * @author Fabrizio Montesi <fmontesi@imada.sdu.dk>
 */
public class Main {

    public static void main(String[] args) {
        // String f = new File(".").getAbsolutePath();
        Path p = Paths.get("data_example/").getFileName();
        // System.out.println(p);
        // System.exit(0);

        long t1 = System.currentTimeMillis();
        List<Result> resultOfm1 = Exam.m1(p);
        long t2 = System.currentTimeMillis() - t1;
        System.out.println("Execution of m1 took: " + t2 + "ms");
        System.out.println();
        System.out.println("The Results found are:");
        int i = 0;
        for (Result r : resultOfm1) {
            System.out.println(i + " Path: " + r.path() + " had lowest number: " + r.number());
            i++;
        }
        System.out.println(i + " .txt files found");

        System.out.println();

        long t3 = System.currentTimeMillis();
        Result resultOfm2 = Exam.m2(p, 40580400); // 973905250
        long t4 = System.currentTimeMillis() - t3;
        System.out.println("Execution of m2 took: " + t4 + "ms");
        System.out.println(
                "And path returned was: " + resultOfm2.path() + " and linenumber returned was: " + resultOfm2.number());
        System.out.println();

        long t5 = System.currentTimeMillis();
        Stats resultOfm3 = Exam.m3(p);
        long t6 = System.currentTimeMillis() - t5;
        System.out.println("Execution of m3 took: " + t6 + "ms");
        System.out.println();

        System.out.println("The number found the most times is: " + resultOfm3.mostFrequent());
        System.out.println("The number found the least times is: " + resultOfm3.leastFrequent());
        System.out.println("The number " + 123 + " was found: " + resultOfm3.occurrences(123) + " times");
        System.out.println();

        List<Path> paths = resultOfm3.byTotals();
        for (Path p1 : paths) {
            System.out.println(p1);
        }
        System.out.println(paths.size() + " files found");
    }
}
