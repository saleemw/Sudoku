package puzzle;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A class that would traverse through all a node and all its children
 * in search of solved node.
 */
public class SolveNode {
    private Node root;
    private List<String> emptyCells;
    private Character[][] solved = null;
    private static final long TIMEOUT_IN_SEC = 10L;
    private long startTime = System.currentTimeMillis();

    /**
     * public constructor
     * @param node root node
     * @param inEmptyCells a list of all empty cell on the puzzle
     * @throws Exception exception thrown if parameters are invalid
     */
    public SolveNode (final Node node, List<String> inEmptyCells) throws Exception{
        if (node == null || inEmptyCells == null) {
            throw new Exception("node and emptyCell list cannot be null");
        }
        root = node;
        emptyCells = inEmptyCells;
    }

    /**
     * solves a sudoku puzzle return the value
     * @return a solved sudoku puzzle
     * @throws Exception if any
     */
    public Character[][] solve(final AtomicBoolean status) throws Exception{
        startTime = System.currentTimeMillis();
        solve(root, 0);
        if (solved == null) {
            status.set(false);
            return root.getData();
        }
        status.set(true);
        return solved;
    }

    /**
     * solves a sudoku puzzle using DFS and return the solved puzzle
     * @param node current node
     * @param index this would be the nth empty cell on the board with the format of "row col"
     * @throws Exception if any
     */
    private void solve(final Node node, final int index) throws Exception{
        if (node.isSolved()) {
            solved = node.getData();
            return;
        }

        if(timeout()) {
            /*
            * if we have not found a solution by this time stop generating
            * further nodes to avoid a crash. Still we might be able to solve
            * the puzzle
            */
            return;
        }

        final String rowCol[] = emptyCells.get(index).split(Board.SEPARATOR);
        final int row = Integer.parseInt(rowCol[0]);
        final int col = Integer.parseInt(rowCol[1]);
        node.generateChildren(row, col);
        final List<Node> children = node.getChildren();
        children.forEach(child -> {
            if (child.isSolved()) {
                solved = child.getData();
                return;
            }
            final int nextIndex = index + 1;
            if (nextIndex < emptyCells.size()) {
                try {
                    solve(child, nextIndex);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean timeout() {
        final long duration = (System.currentTimeMillis() - startTime) / 1000L;
        return duration > TIMEOUT_IN_SEC;
    }

}
