/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class BoggleSolver {
    private final BoggleTrieSET dictionary;

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        this.dictionary = new BoggleTrieSET();
        for (String s : dictionary) {
            this.dictionary.add(s);
        }
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        BoardSolver solver = new BoardSolver(dictionary, board);
        return solver.foundWords();
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (word == null) {
            throw new IllegalArgumentException("Null reference");
        }
        if (dictionary.contains(word)) {
            return getScore(word.length());
        }
        return 0;
    }

    private static int getScore(int length) {
        if (length < 3) return 0;
        if (length < 5) return 1;
        if (length < 6) return 2;
        if (length < 7) return 3;
        if (length < 8) return 5;
        return 11;
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        if (args.length > 1) {
            BoggleBoard board = new BoggleBoard(args[1]);
            int score = 0;
            for (String word : solver.getAllValidWords(board)) {
                StdOut.println(word);
                score += solver.scoreOf(word);
            }
            StdOut.println("Score = " + score);
        }
        else
        {
            // // stress-test
            // int count = 100000;
            // int size = 10;
            // BoggleBoard[] boards = new BoggleBoard[count];
            // for (int i = 0; i < boards.length; ++i) {
            //     boards[i] = new BoggleBoard(size, size);
            // }
            // long score = 0;
            // Stopwatch sw = new Stopwatch();
            // double t1 = sw.elapsedTime();
            // for (BoggleBoard board : boards) {
            //     for (String word : solver.getAllValidWords(board)) {
            //         score += solver.scoreOf(word);
            //     }
            // }
            // double t2 = sw.elapsedTime();
            // StdOut.printf("Speed: %.5f/s\n", count / (t2 - t1));
            // StdOut.printf("Score: %d\n", score);
        }
    }
}
