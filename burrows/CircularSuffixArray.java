/* *****************************************************************************
 *  Name: CircularSuffixArray.java
 *  Date: 07/04/2020
 *  Description: Circular suffix array implementation based on 3-way radix
 *  quicksort. This implementation has O(N^2) worst case, but given that key
 *  domain is short (256 symbols) such deterioration is unlikely.
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;

public class CircularSuffixArray {
    private static final int CUTOFF =  3;

    private final char[] text;
    private final int[] index;
    private final int n;

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) {
            throw new IllegalArgumentException("Empty string passed to circular array");
        }
        text = s.toCharArray();
        index = new int[s.length()];
        n = text.length;
        for (int i = 0; i < n; ++i) {
            index[i] = i;
        }
        sort(0, n - 1, 0);
    }

    // 3-way string quicksort lo..hi starting at dth character
    private void sort(int lo, int hi, int d) {
        if (d == n) {
            return;
        }

        // cutoff to insertion sort for small subarrays
        if (hi <= lo + CUTOFF) {
            insertion(lo, hi, d);
            return;
        }

        int lt = lo, gt = hi;
        char v = text[(index[lo] + d) % n];
        int i = lo + 1;
        while (i <= gt) {
            char t = text[(index[i] + d) % n];
            if      (t < v) exch(lt++, i++);
            else if (t > v) exch(i, gt--);
            else            i++;
        }

        // a[lo..lt-1] < v = a[lt..gt] < a[gt+1..hi].
        sort(lo, lt-1, d);
        sort(lt, gt, d+1);
        sort(gt+1, hi, d);
    }

    // sort from a[lo] to a[hi], starting at the dth character
    private void insertion(int lo, int hi, int d) {
        for (int i = lo; i <= hi; i++)
            for (int j = i; j > lo && less(index[j], index[j-1], d); j--)
                exch(j, j-1);
    }

    // is text[i+d..i+n) < text[j+d..j+n) ?
    private boolean less(int i, int j, int d) {
        if (i == j) return false;
        while (d < n) {
            char ic = text[(i + d) % n];
            char jc = text[(j + d) % n];
            if (ic < jc) return true;
            if (ic > jc) return false;
            d++;
        }
        return false;
    }

    // exchange index[i] and index[j]
    private void exch(int i, int j) {
        int swap = index[i];
        index[i] = index[j];
        index[j] = swap;
    }

    // length of s
    public int length() {
        return n;
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || i >= n) {
            throw new IllegalArgumentException("Index out of range");
        }
        return index[i];
    }

    // unit testing (required)
    public static void main(String[] args) {
        String s = "ABRACADABRA!";
        CircularSuffixArray array = new CircularSuffixArray(s);
        for (int i = 0; i < s.length(); ++i) {
            int offset = array.index(i);
            StdOut.printf("%d %s%s\n", i, s.substring(offset), s.substring(0, offset));
        }
    }
}
