/* *****************************************************************************
 *  Name: MoveToFrontBST.java
 *  Date: 07/04/2020
 *  Description: Move-to-front implementation with O(N log R) worst case
 *  complexity based on counting RedBlackBST.
 **************************************************************************** */

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;
import edu.princeton.cs.algs4.RedBlackBST;

public class MoveToFrontBST {
    private static final int R = 256;
    private RedBlackBST<Integer, Character> table;
    private final int[] encodeIndex;
    private int counter;

    private MoveToFrontBST() {
        table = new RedBlackBST<>();
        encodeIndex = new int[R];
        for (int i = 0; i < R; ++i) {
            encodeIndex[i] = i;
            table.put(i, (char) i);
        }
        counter = -1;
    }

    private void reset() {
        for (int i = 0; i < R; ++i) {
            encodeIndex[i] = table.rank(encodeIndex[i]);
        }
        table = new RedBlackBST<>();
        for (int i = 0; i < R; ++i) {
            table.put(encodeIndex[i], (char) i);
        }
        counter = -1;
    }

    private void playOne(char c) {
        int index = encodeIndex[c];
        table.delete(index);
        encodeIndex[c] = counter;
        table.put(counter--, c);
        if (counter == Integer.MIN_VALUE) {
            reset();
        }
    }

    private char encodeOne(char c) {
        int index = encodeIndex[c];
        char rank = (char) table.rank(index);
        playOne(c);
        return rank;
    }

    private char decodeOne(char i) {
        int index = table.select(i);
        char c = table.get(index);
        playOne(c);
        return c;
    }


    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        MoveToFrontBST encoder = new MoveToFrontBST();
        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            BinaryStdOut.write(encoder.encodeOne(c));
        }
        BinaryStdOut.close();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        MoveToFrontBST decoder = new MoveToFrontBST();
        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            BinaryStdOut.write(decoder.decodeOne(c));
        }
        BinaryStdOut.close();
    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {
        if      (args[0].equals("-")) encode();
        else if (args[0].equals("+")) decode();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}
