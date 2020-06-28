/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import java.util.HashSet;

public class BoardSolver {
    private static int[] xMoves = {-1, -1, -1, 0, 0, 1, 1, 1};
    private static int[] yMoves = {-1, 0, 1, -1, 1, -1, 0, 1};

    private final BoggleTrieSET dictionary;
    private final BoggleBoard board;
    private final HashSet<String> foundWords;
    private boolean[][] used;
    private char[] charStack;
    private int stackSize = 0;

    public BoardSolver(BoggleTrieSET dictionary, BoggleBoard board) {
        this.dictionary = dictionary;
        this.board = board;
        foundWords = new HashSet<>();
        // multiply by 2 to ensure Qu exception to work
        charStack = new char[board.rows() * board.cols() * 2];
        // additional boundaries to avoid extra check in dfs
        used = new boolean[board.rows() + 2][board.cols() + 2];
        used[0][0] = true;
        for (int i = 1; i < board.rows() + 2; ++i) {
            used[i][0] = true;
            used[i][board.cols() + 1] = true;
        }
        for (int i = 1; i < board.cols() + 2; ++i) {
            used[0][i] = true;
            used[board.rows() + 1][i] = true;
        }
        for (int i = 0; i < board.rows(); ++i) {
            for (int j = 0; j < board.cols(); ++j) {
                dfsSearch(i, j, dictionary.getRoot());
            }
        }
    }

    public Iterable<String> foundWords() {
        return foundWords;
    }

    private void dfsSearch(int x, int y, BoggleTrieSET.Node parent) {
        if (!used[x + 1][y + 1]) {
            used[x + 1][y + 1] = true;
            char c = board.getLetter(x, y);
            charStack[stackSize] = c;
            ++stackSize;
            parent = dictionary.get(parent, c);
            if (c == 'Q') {
                charStack[stackSize] = 'U';
                ++stackSize;
                parent = dictionary.get(parent, 'U');
            }
            if (parent != null) {
                if (stackSize >= 3 && BoggleTrieSET.isTerminalNode(parent)) {
                    saveCurrentStack();
                }
                for (int i = 0; i < 8; ++i) {
                    dfsSearch(x + xMoves[i], y + yMoves[i], parent);
                }
            }
            if (c == 'Q') {
                --stackSize;
            }
            --stackSize;
            used[x + 1][y + 1] = false;
        }
    }

    private void saveCurrentStack() {
        foundWords.add(new String(charStack, 0, stackSize));
    }
}
