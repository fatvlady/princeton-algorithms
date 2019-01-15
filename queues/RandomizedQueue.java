import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class RandomizedQueue<Item> implements Iterable<Item> {

    private Item[] buffer;         // array of items
    private int n;                 // number of elements on queue
    private int begin;             // position to dequeue from
    private int end;               // position to enqueue to


    /**
     * Initializes an empty queue.
     */
    public RandomizedQueue() {
        buffer = (Item[]) new Object[2];
        n = 0;
        begin = 0;
        end = 0;
    }

    /**
     * Is this queue empty?
     * @return true if this queue is empty; false otherwise
     */
    public boolean isEmpty() {
        return n == 0;
    }

    /**
     * Returns the number of items in the queue.
     * @return the number of items in the queue
     */
    public int size() {
        return n;
    }

    private int getItemPosition(int index) {
        int position = begin + index;
        if (position < buffer.length)
            return position;
        return position - buffer.length;
    }

    // copy queue contents to the array
    private void copyTo(Item[] array)
    {
        if (array.length < n)
            throw new ArrayIndexOutOfBoundsException("trying to copy contents to small array");
        int leftMidSentinel = Math.min(begin + n, buffer.length);
        for (int i = begin; i < leftMidSentinel; i++) {
            array[i - begin] = buffer[i];
        }
        int rightMidSentinel = Math.max(0, end - n);
        for (int i = leftMidSentinel - begin, j = rightMidSentinel; i < n; i++, j++) {
            array[i] = buffer[j];
        }
    }

    // resize the underlying array holding the elements
    private void resize(int capacity) {
        assert capacity >= n;

        // textbook implementation
        Item[] temp = (Item[]) new Object[capacity];
        copyTo(temp);
        buffer = temp;
        begin = 0;
        end = n;
    }

    /**
     * Adds the item to this queue.
     * @param item the item to add
     */
    public void enqueue(Item item) {
        if (item == null)
            throw new IllegalArgumentException();
        if (n == buffer.length) resize(2 * buffer.length);    // double size of array if necessary
        if (n > 0) {
            // add an item and swap it to preserve uniform distribution
            int draw = StdRandom.uniform(n + 1);
            if (draw < n) {
                int swapPosition = getItemPosition(draw);
                Item tmp = buffer[swapPosition];
                buffer[swapPosition] = item;
                item = tmp;
            }
        }
        buffer[end++] = item;       // add item
        if (end == buffer.length)
            end = 0;
        n++;
    }

    /**
     * Removes and returns the item most first added to this queue.
     * @return the item first added
     * @throws java.util.NoSuchElementException if this queue is empty
     */
    public Item dequeue() {
        if (isEmpty()) throw new NoSuchElementException("queue underflow");
        Item item = buffer[begin];
        buffer[begin++] = null;      // to avoid loitering
        n--;
        if (begin == buffer.length)
            begin = 0;
        // shrink size of array if necessary
        if (n > 0 && n == buffer.length/4) resize(buffer.length/2);
        return item;
    }

    /**
     * Removes and returns the item most first added to this queue.
     * @return the item first added
     * @throws java.util.NoSuchElementException if this queue is empty
     */
    public Item sample() {
        if (isEmpty()) throw new NoSuchElementException("queue underflow");
        return buffer[getItemPosition(StdRandom.uniform(n))];
    }

    /**
     * Returns an iterator to this queue that iterates through the items in LIFO order.
     * @return an iterator to this queue that iterates through the items in LIFO order.
     */
    public Iterator<Item> iterator() {
        return new RandomizedQueueIterator();
    }

    // an iterator, doesn't implement remove() since it's optional
    private class RandomizedQueueIterator implements Iterator<Item> {
        private final Item[] fullSample;
        private int position;

        public RandomizedQueueIterator() {
            fullSample = (Item[]) new Object[n];
            RandomizedQueue.this.copyTo(fullSample);
            StdRandom.shuffle(fullSample);
            position = 0;
        }

        public boolean hasNext() {
            return position < fullSample.length;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public Item next() {
            if (!hasNext()) throw new NoSuchElementException();
            return fullSample[position++];
        }
    }


    /**
     * Unit tests the {@code RandomizedQueue} data type.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        RandomizedQueue<String> rnq = new RandomizedQueue<String>();
        while (!StdIn.isEmpty()) {
            String item = StdIn.readString();
            if (!item.equals("-"))
                rnq.enqueue(item);
            else if (!rnq.isEmpty())
                rnq.dequeue();
            for (String s : rnq)
                StdOut.printf("%s ", s);
            StdOut.println();
        }
        StdOut.println("(" + rnq.size() + " left on queue)");
    }
}
