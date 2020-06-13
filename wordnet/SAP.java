/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class SAP {

    private final Digraph digraph;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null) {
            throw new IllegalArgumentException("Null digraph argument");
        }
        digraph = new Digraph(G);
    }

    private void validateVertex(int v) {
        if (v >= digraph.V()) {
            throw new IllegalArgumentException("Input vertex outside of digraph");
        }
    }

    private void validateVertexSequence(Iterable<Integer> v) {
        if (v == null) {
            throw new IllegalArgumentException("Null input iterable");
        }
        for (Integer i : v) {
            if (i == null) {
                throw new IllegalArgumentException("Null vertex index in input iterable");
            }
            validateVertex(i);
        }
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        validateVertex(v);
        validateVertex(w);
        BreadthFirstDirectedPaths pathsA = new BreadthFirstDirectedPaths(digraph, v);
        BreadthFirstDirectedPaths pathsB = new BreadthFirstDirectedPaths(digraph, w);
        int distance = Integer.MAX_VALUE;
        for (int u = 0; u < digraph.V(); ++u) {
            if (pathsA.hasPathTo(u) && pathsB.hasPathTo(u)
                    && pathsA.distTo(u) + pathsB.distTo(u) < distance) {
                distance = pathsA.distTo(u) + pathsB.distTo(u);
            }
        }
        if (distance == Integer.MAX_VALUE) {
            return -1;
        }
        return distance;
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        validateVertex(v);
        validateVertex(w);
        BreadthFirstDirectedPaths pathsA = new BreadthFirstDirectedPaths(digraph, v);
        BreadthFirstDirectedPaths pathsB = new BreadthFirstDirectedPaths(digraph, w);
        int distance = Integer.MAX_VALUE;
        int commonAncestor = -1;
        for (int u = 0; u < digraph.V(); ++u) {
            if (pathsA.hasPathTo(u) && pathsB.hasPathTo(u)
                    && pathsA.distTo(u) + pathsB.distTo(u) < distance) {
                distance = pathsA.distTo(u) + pathsB.distTo(u);
                commonAncestor = u;
            }
        }
        return commonAncestor;
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        validateVertexSequence(v);
        validateVertexSequence(w);
        if (!v.iterator().hasNext() || !w.iterator().hasNext()) return -1;
        BreadthFirstDirectedPaths pathsA = new BreadthFirstDirectedPaths(digraph, v);
        BreadthFirstDirectedPaths pathsB = new BreadthFirstDirectedPaths(digraph, w);
        int distance = Integer.MAX_VALUE;
        for (int u = 0; u < digraph.V(); ++u) {
            if (pathsA.hasPathTo(u) && pathsB.hasPathTo(u)
                    && pathsA.distTo(u) + pathsB.distTo(u) < distance) {
                distance = pathsA.distTo(u) + pathsB.distTo(u);
            }
        }
        if (distance == Integer.MAX_VALUE) return -1;
        return distance;
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        validateVertexSequence(v);
        validateVertexSequence(w);
        if (!v.iterator().hasNext() || !w.iterator().hasNext()) return -1;
        BreadthFirstDirectedPaths pathsA = new BreadthFirstDirectedPaths(digraph, v);
        BreadthFirstDirectedPaths pathsB = new BreadthFirstDirectedPaths(digraph, w);
        int distance = Integer.MAX_VALUE;
        int commonAncestor = -1;
        for (int u = 0; u < digraph.V(); ++u) {
            if (pathsA.hasPathTo(u) && pathsB.hasPathTo(u)
                    && pathsA.distTo(u) + pathsB.distTo(u) < distance) {
                distance = pathsA.distTo(u) + pathsB.distTo(u);
                commonAncestor = u;
            }
        }
        return commonAncestor;
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length   = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}
