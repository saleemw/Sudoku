package test;

import junit.framework.TestCase;
import puzzle.Board;


public class BoardTestSuite extends TestCase {

    public BoardTestSuite (final String str) {
        super(str);
    }

    public void testInsertValue() throws Exception {
        final Board board = getEmptyBoard();
        assertTrue("setting row 0, col 0 to 1", board.insert('1', 0, 0));
        assertFalse("row is already set. ", board.insert('1', 0, 0));
        assertFalse("row already contains 1", board.insert('1', 0, 8));
        assertFalse("col already contains 1", board.insert('1', 8, 0));
        assertFalse("block already contains 1", board.insert('1', 2, 2));
        assertTrue("insert 1 into new block", board.insert('1', 3, 3));
        assertFalse("cell 3,3 already contains 1", board.insert('1', 3, 3));
        assertFalse("block already contains 1", board.insert('1', 4, 4));
        assertFalse("row already contains 1", board.insert('1', 3, 5));
        assertFalse("col already contains 1", board.insert('1', 5, 3));
    }

    public void testIsSolved() throws Exception{
        final Board solvedBoard = Board.createBoard(true);
        assertTrue("board should be solved", solvedBoard.isSolved());

        final Board unsolvedBoard = Board.createBoard(false);
        assertFalse("board should be unsolved", unsolvedBoard.isSolved());
    }

    private Board getEmptyBoard() throws Exception {
        Board board = new Board();
        return board;
    }
}
