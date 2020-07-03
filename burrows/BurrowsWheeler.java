/* *****************************************************************************
 *  Name: BurrowsWheeler.java
 *  Date: 07/04/2020
 *  Description: Burrows-Wheeler transform CLI utility.
 **************************************************************************** */

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {
    private static final int R = 256;

    // apply Burrows-Wheeler transform,
    // reading from standard input and writing to standard output
    public static void transform() {
        // read the input
        String s = BinaryStdIn.readString();
        CircularSuffixArray suffixArray = new CircularSuffixArray(s);
        char[] code = new char[suffixArray.length()];
        int startIdx = -1;
        for (int i = 0; i < code.length; ++i) {
            int idx = suffixArray.index(i);
            code[i] = s.charAt((code.length + idx - 1) % code.length);
            if (idx == 0) {
                startIdx = i;
            }
        }
        if (startIdx == -1) {
            throw new IllegalStateException("Encoding failure");
        }
        BinaryStdOut.write(startIdx);
        for (char c : code) {
            BinaryStdOut.write(c);
        }
        // close output stream
        BinaryStdOut.close();
    }

    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    public static void inverseTransform() {
        int startIdx = BinaryStdIn.readInt();
        char[] code = BinaryStdIn.readString().toCharArray();
        int[] next = new int[code.length];
        int[] counts = new int[R + 1];
        for (int c : code) {
            ++counts[c + 1];
        }
        for (int i = 0; i < R; ++i) {
            counts[i + 1] += counts[i];
        }
        for (int i = 0; i < code.length; ++i) {
            next[counts[code[i]]++] = i;
        }
        int position = next[startIdx];
        for (int i = 0; i < code.length; ++i) {
            BinaryStdOut.write(code[position]);
            position = next[position];
        }
        BinaryStdOut.close();
    }

    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        if      (args[0].equals("-")) transform();
        else if (args[0].equals("+")) inverseTransform();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}
