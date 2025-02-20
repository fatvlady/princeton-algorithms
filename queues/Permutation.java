import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class Permutation {
    public static void main(String[] args) {
        if (args.length != 1)
            throw new IllegalArgumentException("should be exactly one command-line argument");
        RandomizedQueue<String> queue = new RandomizedQueue<>();
        while (!StdIn.isEmpty()) {
            String item = StdIn.readString();
            queue.enqueue(item);
        }
        int k = Integer.parseInt(args[0]);
        for (int i = 0; i < k; ++i) {
            StdOut.println(queue.dequeue());
        }
    }
}