package puzzle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static puzzle.Board.LENGTH;

public class Solver {

    public Solver() {

    }

    /**
     * solves a sudoku game by Depth First Search
     *
     * @param board board to solve
     */
    public void solve(Board board) {
        try {
            solveByDFS(board);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * solves the puzzle using an instance of the sudoku.puzzle.SolveNode class
     * @param board board to solve
     * @throws Exception if any
     */
    private void solveByDFS(final Board board) throws Exception{
        final Character[][] data = board.getTwoDBoard();
        Board.print(data);
        final List<String> emptyCell = board.getEmptyCells();
        final Node root = new Node(data);
        SolveNode solve = new SolveNode(root, emptyCell);
        final AtomicBoolean status = new AtomicBoolean(false);
        final Character[][] solved = solve.solve(status);
        if (status.get()) {
            System.out.println("_________________________________________");
            Board.print(solved);
            emptyCell.forEach(cell -> {
                final String[] rowCol = cell.split(Board.SEPARATOR);
                final int row = Integer.parseInt(rowCol[0]);
                final int col = Integer.parseInt(rowCol[1]);
                board.insert(solved[row][col], row, col);
            });
        }
    }


    /**
     * solve by eliminating values using sudoku rule
     * this method not be able to solve a puzzle all the time
     * @param board board to solve
     */
    @SuppressWarnings("unused")
    public void solveByElimination(Board board) {
        final Character[][] brd = board.getTwoDBoard();
        final List<String> emptyCells = board.getEmptyCells();
        final HashMap<String, Set<Character>> cellValues = new HashMap<String, Set<Character>>();
        emptyCells.forEach(cell -> cellValues.put(cell, Board.getChar1To9()));


        int count = LENGTH * LENGTH * LENGTH;

        while (!board.isSolved()) {
            for(Map.Entry<String, Set<Character>> entry : cellValues.entrySet()) {
                final String[] element = entry.getKey().split(" ");
                final int row = Integer.parseInt(element[0]);
                final int col = Integer.parseInt(element[1]);

                final Character currentValue = board.getCellValue(row, col);
                if (currentValue.charValue() != ' ') {
                    continue;
                }
                final Set<Character> values = entry.getValue();

                if (values.size() > 1) {
                    for (int i = 0; i < LENGTH; i++) {
                        values.remove(brd[row][i]);
                    }

                    for (int i = 0; i < LENGTH; i++) {
                       values.remove(brd[i][col]);
                    }

                    final int bRow = Board.getBlockCoordinate(row);
                    final int bCol = Board.getBlockCoordinate(col);
                    for (int i = bRow; i < (bRow + 3); i++) {
                        for (int j = bCol; j < (bCol + 3); j++) {
                            values.remove(brd[i][j]);
                        }
                    }
                }
                if (values.size() == 1) {
                    final Character sol = values.iterator().next();
                    board.insert(sol, row, col);
                    brd[row][col] = sol;
                    values.clear();
                }
            }
            count--;
            if (count < 0) {
                System.out.println("elimination algorithm returns unsolved");
                return;
            }
        }
    }
}
