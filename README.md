### DM519 Concurrent Programming Spring 2018

This project contains the individual final exam for the course Concurrent Programming.

The aim of this project was to use Java concurrency to search through a large number of files in a directory tree. Specifically, we were to implement 3 methods `m1`, `m2`, and `m3`, where

- `m1` recursively visits a directory to find all the text files contained in it and its subdirectories. The method returns a list containing a `Result` object for each text file found, containing the path of its text file and the minimum number found inside the text file.

- `m2` recursively visits a directory to find all the text files with suffix `.dat` contained in it and its subdirectories. The method looks for a `.dat` file that contains a line whose numbers, when added together (total), amount to at least (>=) parameter `min`. Once this is found, the method can return immediately (without waiting to analyse the other files). The method returns a `Result` object containing the path of its text file and the line that satisfies the condition.

- `m3` computes overall statistics about the occurrences of numbers in a directory. Recursively searches the directory for all numbers in all lines of `.txt` and `.dat` files and returns a `Stats` object containing the statistics of interest.

#### How to build and run

You need at least Java 8 (1.8) to run this.

As part of this project, we were required to use the given project structure and encouraged to use the Netbeans IDE, so if you use that, you should be able to load and run the project without any problems.

However, if you just want to quickly compile and run the project, you can do the following.

Assuming you're on Linux and you've cloned the project and are now in the `ConcurrentProgramming` directory, you can compile the project by running the command

```javac -d . code/exam_project/src/cp/*.java```

This will create the directory `cp` containing the `.class` files and you can then run the program by entering

```java cp.Main```

which will run the program on the files in the `data_example` folder.
