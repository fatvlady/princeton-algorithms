/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.StdOut;

import java.util.LinkedList;

public class Solver {

    private class SearchNode implements Comparable<SearchNode>
    {
        private final SearchNode predecessor;
        private final Board board;
        private final int movesToReach;
        private boolean isDual;

        SearchNode(Board board, boolean isDual)
        {
            assert board != null;
            predecessor = null;
            this.board = board;
            movesToReach = 0;
            this.isDual = isDual;
        }

        SearchNode(SearchNode node, Board child)
        {
            assert node != null;
            assert child != null;
            predecessor = node;
            board = child;
            movesToReach = node.movesToReach + 1;
            this.isDual = predecessor.isDual;
        }

        @Override
        public int compareTo(SearchNode other) {
            return movesToReach + board.manhattan() - other.movesToReach - other.board.manhattan();
        }
    }

    private final SearchNode solution;

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        solution = solve(initial);
    }

    private SearchNode solve(Board initial) {
        MinPQ<SearchNode> queue = new MinPQ<>();
        queue.insert(new SearchNode(initial, false));
        queue.insert(new SearchNode(initial.twin(), true));
        while (!queue.isEmpty())
        {
            SearchNode searchNode = queue.min();
            queue.delMin();
            if (searchNode.board.isGoal()) {
                return searchNode.isDual ? null : searchNode;
            }
            for (Board b : searchNode.board.neighbors()) {
                if (searchNode.predecessor == null || !searchNode.predecessor.board.equals(b)) {
                    queue.insert(new SearchNode(searchNode, b));
                }
            }
        }
        return null;
    }

    // is the initial board solvable?
    public boolean isSolvable() {
        return solution != null;
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        return solution == null ? -1 : solution.movesToReach;
    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        if (solution != null) {
            LinkedList<Board> boards = new LinkedList<>();
            SearchNode node = solution;
            while (node != null)
            {
                boards.addFirst(node.board);
                node = node.predecessor;
            }
            return boards;
        }
        return null;
    }

    public static void main(String[] args) {

        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] blocks = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                blocks[i][j] = in.readInt();
        Board initial = new Board(blocks);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}
