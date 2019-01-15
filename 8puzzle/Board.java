/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class Board {

    private final int[] state;
    private final int   dim;
    private final int   dim2;
    private final int   holeCoordinate;

    // not final because it is lazy-calculated
    private int         hammingMetric;
    private final int   manhattanMetric;

    // construct a board from an n-by-n array of blocks
    // (where blocks[i][j] = block in row i, column j)
    public Board(int[][] blocks) {
        if (blocks == null) {
            throw new IllegalArgumentException("Null state passed to constructor.");
        }
        dim = blocks.length;
        if (dim < 2 || dim >= 128) {
            throw new IllegalArgumentException("Unsopported board size.");
        }
        if (Arrays.stream(blocks).anyMatch(row -> row == null || row.length != dim)) {
            throw new IllegalArgumentException("Not quadratic array passed to constructor.");
        }
        dim2 = dim * dim;
        state = new int[dim2];
        for (int i = 0; i < dim; ++i) {
            for (int j = 0; j < dim; ++j) {
                state[dim * i + j] = blocks[i][j];
            }
        }
        hammingMetric = -1;
        manhattanMetric = manhattan();
        holeCoordinate = findHole();
        assert holeCoordinate >= 0;
    }

    private Board(int dim, int[] state) {
        assert state != null;
        this.dim = dim;
        dim2 = dim * dim;
        assert state.length == dim2;
        this.state = state;
        hammingMetric = -1;
        manhattanMetric = manhattan();
        holeCoordinate = findHole();
        assert holeCoordinate >= 0;
    }

    private Board(Board other, int dx, int dy) {
        assert other != null;
        assert Math.abs(dx) <= 1 && Math.abs(dy) <= 1 && Math.abs(dx + dy) == 1;
        this.dim = other.dim;
        this.dim2 = other.dim2;
        holeCoordinate = other.holeCoordinate + dx * dim + dy;
        assert holeCoordinate >= 0 && holeCoordinate < dim2;
        this.state = other.swapCreateState(other.holeCoordinate, holeCoordinate);
        this.hammingMetric = other.hamming();
        if (other.holeCoordinate == other.state[holeCoordinate] - 1) {
            --this.hammingMetric; // move tile into position
        } else if (holeCoordinate == other.state[holeCoordinate] - 1) {
            ++this.hammingMetric; // move tile out of the position
        }
        if (dx != 0) {
            int deltaX =  (other.state[holeCoordinate] - 1) / dim - holeCoordinate / dim;
            if (deltaX != 0) {
                this.manhattanMetric = other.manhattanMetric + Integer.signum(dx * deltaX);
            } else {
                this.manhattanMetric = other.manhattanMetric + 1;
            }
        } else if (dy != 0) {
            int deltaY =  (other.state[holeCoordinate] - 1) % dim - holeCoordinate % dim;
            if (deltaY != 0) {
                this.manhattanMetric = other.manhattanMetric + Integer.signum(dy * deltaY);
            } else {
                this.manhattanMetric = other.manhattanMetric + 1;
            }
        } else this.manhattanMetric = manhattan();
    }

    private int findHole() {
        for (int i = 0; i < dim2; ++i) {
            if (state[i] == 0) {
                return i;
            }
        }
        return -1;
    }

    private int[] swapCreateState(int coord1, int coord2) {
        int[] newState = Arrays.copyOf(state, state.length);
        int tmp = newState[coord1];
        newState[coord1] = newState[coord2];
        newState[coord2] = tmp;
        return newState;
    }

    // board dimension n
    public int dimension()
    {
        return dim;
    }

    // number of blocks out of place
    public int hamming() {
        if (hammingMetric < 0) {
            hammingMetric = -1; // to ignore the zero tile
            for (int i = 0; i < dim2; ++i) {
                if (state[i] != i + 1) {
                    ++hammingMetric;
                }
            }
        }
        return hammingMetric;
    }

    // sum of Manhattan distances between blocks and goal
    public int manhattan() {
        int manhattanMetric = 0;
        for (int i = 0; i < dim2; ++i) {
            int value = state[i] - 1;
            if (value >= 0) {
                manhattanMetric += Math.abs((i / dim) - (value / dim)) + Math.abs((i % dim) - (value % dim));
            }
        }
        return manhattanMetric;
    }

    // is this board the goal board?
    public boolean isGoal()
    {
        return hamming() == 0;
    }

    // a board that is obtained by exchanging any pair of blocks
    public Board twin() {
        int firstNonZero = -1;
        int secondNonZero = -1;
        for (int i = 0; i < dim2; ++i) {
            if (state[i] != 0) {
                if (firstNonZero == -1) {
                    firstNonZero = i;
                    continue;
                }
                secondNonZero = i;
                break;
            }
        }
        return new Board(dim, swapCreateState(firstNonZero, secondNonZero));
    }

    // does this board equal y?
    public boolean equals(Object y) {
        if (y == this) return true;
        if (y == null) return false;
        if (y.getClass() != this.getClass()) return false;
        Board that = (Board) y;
        return dim == that.dim && Arrays.equals(state, that.state);
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        List<Board> neighbors = new LinkedList<>();
        int holeX = holeCoordinate / dim;
        int holeY = holeCoordinate % dim;
        if (holeX > 0)
            neighbors.add(new Board(this, -1, 0));
        if (holeX < dim - 1)
            neighbors.add(new Board(this, 1, 0));
        if (holeY > 0)
            neighbors.add(new Board(this, 0, -1));
        if (holeY < dim - 1)
            neighbors.add(new Board(this, 0, 1));
        return neighbors;
    }

    // string representation of this board (in the output format specified below)
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(dim + "\n");
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                s.append(String.format("%2d ", state[i * dim + j]));
            }
            s.append("\n");
        }
        return s.toString();
    }

    public static void main(String[] args) {
        int[][] tiles = new int[2][];
        for (int i = 0; i < 2; ++i) {
            tiles[i] = new int[2];
        }
        tiles[0][0] = 1;
        tiles[0][1] = 2;
        tiles[1][0] = 3;
        tiles[1][1] = 0;
        Consumer<Board> printer = (Board b) -> StdOut.printf("%d %d\n", b.hamming(), b.manhattan());

        Board board = new Board(tiles);
        Board board2 = new Board(board, -1, 0);
        Board board3 = new Board(board2, 0, -1);
        Board board4 = new Board(board3, 1, 0);
        printer.accept(board);
        printer.accept(board2);
        printer.accept(board3);
        printer.accept(board4);
        board = new Board(board4, 0, 1);
        board2 = new Board(board, -1, 0);
        board3 = new Board(board2, 0, -1);
        board4 = new Board(board3, 1, 0);
        printer.accept(board);
        printer.accept(board2);
        printer.accept(board3);
        printer.accept(board4);
        board = new Board(board4, 0, 1);
        board2 = new Board(board, -1, 0);
        board3 = new Board(board2, 0, -1);
        board4 = new Board(board3, 1, 0);
        printer.accept(board);
        printer.accept(board2);
        printer.accept(board3);
        printer.accept(board4);
        board = new Board(board4, 0, 1);
        printer.accept(board);
    }
}
