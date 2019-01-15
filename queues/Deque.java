
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<Item> implements Iterable<Item> {
    private int n;          // size of the deque
    private final Node first;     // top of deque
    private final Node last;     // top of deque

    // helper linked list class
    private class Node {
        private Item item;
        private Node prev;
        private Node next;
    }

    /**
     * Initializes an empty deque.
     */
    public Deque() {
        first = new Node();
        last = new Node();
        first.next = last;
        last.prev = first;
        n = 0;
        assert check();
    }

    /**
     * Is this deque empty?
     * @return true if this deque is empty; false otherwise
     */
    public boolean isEmpty() {
        return n == 0;
    }

    /**
     * Returns the number of items in the deque.
     * @return the number of items in the deque
     */
    public int size() {
        return n;
    }

    /**
     * Adds the item to begin of this deque.
     * @param item the item to add
     * @throws IllegalArgumentException if item is null
     */
    public void addFirst(Item item) {
        if (item == null)
            throw new IllegalArgumentException();
        Node newNode = new Node();
        newNode.item = item;
        newNode.next = first.next;
        newNode.prev = first;
        first.next = newNode;
        newNode.next.prev = newNode;
        n++;
        assert check();
    }

    /**
     * Adds the item to end of this deque.
     * @param item the item to add
     * @throws IllegalArgumentException if item is null
     */
    public void addLast(Item item) {
        if (item == null)
            throw new IllegalArgumentException();
        Node newNode = new Node();
        newNode.item = item;
        newNode.prev = last.prev;
        newNode.next = last;
        last.prev = newNode;
        newNode.prev.next = newNode;
        n++;
        assert check();
    }

    /**
     * Removes and returns the item most recently added to this deque.
     * @return the item which was on the first place
     * @throws java.util.NoSuchElementException if this deque is empty
     */
    public Item removeFirst()  {
        if (isEmpty()) throw new NoSuchElementException("deque underflow");
        Item item = first.next.item;        // save item to return
        first.next = first.next.next;       // delete first node
        first.next.prev = first;            // reset new first node
        n--;
        assert check();
        return item;                        // return the saved item
    }

    /**
     * Removes and returns the item most recently added to this deque.
     * @return the item which was on the first place
     * @throws java.util.NoSuchElementException if this deque is empty
     */
    public Item removeLast() {
        if (isEmpty()) throw new NoSuchElementException("deque underflow");
        Item item = last.prev.item;        // save item to return
        last.prev = last.prev.prev;        // delete last node
        last.prev.next = last;             // reset new last node
        n--;
        assert check();
        return item;                       // return the saved item
    }

    /**
     * Returns an iterator to this deque that iterates through the items in LIFO order.
     * @return an iterator to this deque that iterates through the items in LIFO order.
     */
    public Iterator<Item> iterator() {
        return new DequeIterator();
    }

    // an iterator, doesn't implement remove() since it's optional
    private class DequeIterator implements Iterator<Item> {
        private Node current = first.next;

        public boolean hasNext() {
            return current.item != null;
        }
        public void remove() {
            throw new UnsupportedOperationException();
        }

        public Item next() {
            if (!hasNext()) throw new NoSuchElementException();
            Item item = current.item;
            current = current.next;
            return item;
        }
    }


    // check internal invariants
    private boolean check() {

        // check a few properties of instance variable 'first'
        if (first == null || last == null)
            return false;
        if (n < 0) {
            return false;
        }
        if (n == 0) {
            if (first.next != last || last.prev != first)
                return false;
        }
        else {
            if (first.next == last || last.prev == first)
                return false;
        }

        // check internal consistency of instance variable n
        int numberOfNodes = 0;
        for (Node x = first.next; x != last && numberOfNodes <= n; x = x.next) {
            numberOfNodes++;
        }
        if (numberOfNodes != n) return false;

        return true;
    }

    /**
     * Unit tests the {@code Deque} data type.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        Deque<String> dq = new Deque<String>();
        while (!StdIn.isEmpty()) {
            String item = StdIn.readString();
            if (!item.equals("-"))
                dq.addFirst(item);
            else if (!dq.isEmpty())
                dq.removeLast();
            for (String s : dq)
                StdOut.printf("%s ", s);
            StdOut.println();
        }
        StdOut.println("(" + dq.size() + " left on deque)");
    }
}

