/* *****************************************************************************
 *  Name: MoveToFront.java
 *  Date: 07/04/2020
 *  Description: Naive move-to-front implementation with O(NR) worst case.
 **************************************************************************** */

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {
    private static final int R = 256;
    private final char[] table = new char[R];

    private MoveToFront() {
        for (int i = 0; i < R; ++i) {
            table[i] = (char) i;
        }
    }

    private void playOne(int index) {
        char c = table[index];
        while (index > 0) {
            table[index] = table[index - 1];
            --index;
        }
        table[0] = c;
    }

    private char encodeOne(char c) {
        int index = 0;
        while (table[index] != c) {
            ++index;
        }
        playOne(index);
        return (char) index;
    }

    private char decodeOne(char i) {
        char c = table[i];
        playOne(i);
        return c;
    }


    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        MoveToFront encoder = new MoveToFront();
        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            BinaryStdOut.write(encoder.encodeOne(c));
        }
        BinaryStdOut.close();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        MoveToFront decoder = new MoveToFront();
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
