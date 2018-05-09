package puzzle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * Board class design to hold/store a two dimension sudoku
 * perform validation generate new twoDBoard
 */
public class Board {
	public final static int LENGTH = 9; // dimensions of sudoku board is 9x9
    /*  It is very tempting to use int instead of char but there
    are sudoku games that are 16x16 using 1, 2..9, A, B..G
    using char would make our program upgradable.  */
    private final Character[][] twoDBoard = new Character[LENGTH][LENGTH];
	public final static String SEPARATOR = "" + ' ';
	private final static int DIFFICULTY = 37;
	private final static Set<Character> ONE_TO_NINE = new HashSet<>(LENGTH);
	private final static List<String> emptyCells = new ArrayList<String>();

	/**
	 * default constructor which would init the twoDBoard as blank;
	 */
	public Board(){
		generateBlankBoard();
	}

	/**
	 * init twoDBoard to be blank
	 */
	private void generateBlankBoard(){
	    IntStream.range(0, LENGTH)
                .forEach(i -> IntStream.range(0, LENGTH)
                        .forEach(j -> twoDBoard[i][j] = new Character(' ')));

	}

	/**
	 * initialize twoDBoard based on provided input. If the new board is invalid an exception
     * would be thrown
	 * @param input input
	 * @throws Exception if any of the characters would make the twoDBoard in invalid sudoku twoDBoard
	 */
	public void setBoard(Character[][] input) throws Exception{
	    final AtomicBoolean isValid = new AtomicBoolean(true);
        IntStream.range(0, LENGTH)
                .forEach(i -> IntStream.range(0, LENGTH)
                        .forEach(j -> {
                            final Character entry = input[i][j];
                            isValid.set(insert(entry, i, j));
                            if(!isValid.get()) {
                                print(input);
                            }
                        }));
        if (!isValid.get()) {
            throw new Exception("Invalid input!");
        }
	}

	public List<String> getEmptyCells () {
	    return Collections.unmodifiableList(emptyCells);
    }

    /**
     * copy twoDBoard and return a copy of it
     * @return get a copy of twoDBoard
     */
	public Character[][] getTwoDBoard() {
	    final Character[][] retVal = new Character[LENGTH][LENGTH];
        IntStream.range(0, LENGTH)
                .forEach(i -> IntStream.range(0, LENGTH)
                        .forEach(j -> retVal[i][j] = twoDBoard[i][j]));
		return retVal;
	}

    /**
     * get value for a given cell
     * @param row target row
     * @param col target column
     * @return current value for the cell
     */
	public Character getCellValue(final int row, final int col) {
	    return twoDBoard[row][col];
    }
	/**
	 * @param entry input that will be written to the twoDBoard
	 * @param i row
	 * @param j column
	 * @return true of value was set, false otherwise
	 * @throws Exception if the entry is invalid or the entry would make the twoDBoard invalid
	 */
	public boolean insert (Character entry, final int i, final int j) {
	    if (isSolved()) {
	        return false;
        }
		boolean isValid = isValidEntry(entry, i, j);
		if (isValid) {
			twoDBoard[i][j] = entry;
		}
		return isValid;
	}

	/**
     * determines if an entry is valid or not for a particular cell
	 * @param entry potential entry for the twoDBoard
	 * @param row the row that entry is intended to be written to
	 * @param col the col that entry is intended to be written to
	 * @return true of the entry does not violate Sudoku rules
	 */
	public boolean isValidEntry(Character entry, final int row, final int col) {
	    //setting a cell to blank is always allowed.
	    if (entry.charValue() == ' ') {
	        return true;
        }

		//check to make sure that entry is between 1 - 9
		if (!ONE_TO_NINE.contains(entry)) {
			return false;
		}

		//check row
		for (int i = 0; i < LENGTH; i++) {
			if (twoDBoard[row][i].charValue() == entry.charValue()) {
				return false;
			}
		}

		//check column
		for (int i = 0; i < LENGTH; i++) {
			if (twoDBoard[i][col].charValue() == entry.charValue()) {
				return false;
			}
		}

		// check 3x3 block
		final int rowMin = getBlockCoordinate(row);
		final int colMin = getBlockCoordinate(col);
		for(int i = rowMin; i < (rowMin + 3); i++) {
			for (int j = colMin; j < (colMin + 3); j++) {
				if (twoDBoard[i][j].charValue() == entry.charValue()) {
					return false;
				}
			}
		}
		return true;
	}

    /**
     * a method that determines if a given entry is valid for a given cell of a given board
     * @param current 9x9 sudoku board
     * @param entry candidate entry for the cell
     * @param row target row
     * @param col target column
     * @return is the entry a valid one for the given sudoku board
     */
    public static boolean isValidEntry(final Character[][] current,
                                       final Character entry,
                                       final int row,
                                       final int col) {

        //check to make sure that entry is between 1 - 9
        if (!ONE_TO_NINE.contains(entry)) {
            return false;
        }

        //check row
        for (int i = 0; i < LENGTH; i++) {
            if (current[row][i].charValue() == entry.charValue()) {
                return false;
            }
        }

        //check column
        for (int i = 0; i < LENGTH; i++) {
            if (current[i][col].charValue() == entry.charValue()) {
                return false;
            }
        }

        // check 3x3 block
        final int rowMin = getBlockCoordinate(row);
        final int colMin = getBlockCoordinate(col);
        for(int i = rowMin; i < (rowMin + 3); i++) {
            for (int j = colMin; j < (colMin + 3); j++) {
                if (current[i][j].charValue() == entry.charValue()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
	 * get the beginning row/col for the 3x3 block
	 * @param coordinate column or row
	 * @return minimum or beginning coordinate
	 * @throws Exception if input is invalid
	 */
	public static int getBlockCoordinate(final int coordinate) {
		if (coordinate < 3) {
			return 0;
		}

		if (coordinate < 6) {
			return 3;
		}

		if (coordinate < LENGTH) {
			return 6;
		}
		//we should never get here
		return LENGTH;
	}


    /**
     * Ina a sudoku board there are 9X9 blocks and there 9 of these blocks
     * all cells belong to one of the 9X9 block.
     * given a cell within a sudoku board, this method would determine if that
     * particular block is solved.
     * @param row target row
     * @param col target column
     * @param board board to check
     * @return true if solved
     */
    private static boolean isBlockSolved (int row, int col, Character[][] board) {
        Set<Character> characters = getChar1To9();
        for (int i = row; i < (row + 3); i++) {
            for (int j = col; j < (col + 3); j++) {
                characters.remove(board[i][j]);
            }
        }
        return characters.isEmpty();
    }

    /**
     * check if a current board is solved or not
     * @return true if twoDBoard is solved false otherwise
     */
	public boolean isSolved(){
        return isSolved(twoDBoard);
	}

    /**
     * check if a current board is solved or not
     * @return true if board is solved false otherwise
     */
    public static boolean isSolved(final Character[][] boardToCheck){
        //check for empty cells in the entire board
        for(int i = 0; i < LENGTH; i++) {
            for (int j = 0; j < LENGTH; j++) {
                if (!ONE_TO_NINE.contains(boardToCheck[i][j])) {
                    return false;
                }
            }
        }

        //row check
        for(int i = 0; i < LENGTH; i++){
            Set<Character> characters = getChar1To9();
            for (int j = 0; j < LENGTH; j++) {
                characters.remove(boardToCheck[i][j]);
            }
            if (!characters.isEmpty()) {
                return false;
            }
        }

        //col check
        for(int i = 0; i < LENGTH; i++){
            Set<Character> characters = getChar1To9();
            for (int j = 0; j < LENGTH; j++) {
                characters.remove(boardToCheck[j][i]);
            }
            if (!characters.isEmpty()) {
                return false;
            }
        }

        //check 3x3 blocks
        for (int i = 0; i < LENGTH; i+=3) {
            for (int j = 0; j < LENGTH; j+=3) {
                final boolean isSolved = isBlockSolved(i, j, boardToCheck);
                if (!isSolved) {
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * @return returns a set of characters 1 - 9 which are possible valid entries
     */
	public static Set<Character> getChar1To9() {
	    Set<Character> characters = new HashSet<>(LENGTH);
        characters.addAll(ONE_TO_NINE);
        return characters;
    }


    /**
     * randomly generate a new twoDBoard
     *
     * @param solved if true the game would be solved else unsolved
     * @return newly created twoDBoard
     * @throws Exception if any
     */
    public static Board createBoard(final boolean solved) throws Exception {
        final Character[][] board = new Character[LENGTH][LENGTH];
        final ArrayList<Character> numbers = new ArrayList<>();
        numbers.addAll(getChar1To9());
        Collections.shuffle(numbers);
        final List<Character> block1 = numbers.subList(0, 3);
        final List<Character> block2 = numbers.subList(3, 6);
        final List<Character> block3 = numbers.subList(6, LENGTH);
        //first set of 3X3 Grids
        int i = -1;
        i = fillBoard(board, numbers, i);

        //second set of 3X3 Grids
        shiftBlocks(block1, block2, block3, numbers);
        i = fillBoard(board, numbers, i);

        //third set of 3X3 Grids
        shiftBlocks(block1, block2, block3, numbers);

        fillBoard(board, numbers, i);
        if (!solved) {
            unSolve(board);
        }
        Board retValue = new Board();
        retValue.setBoard(board); //make sure that the solved twoDBoard is valid

        return retValue;
    }

    /**
     * erase some of the chars so that the twoDBoard is not solved anymore
     * and sets the difficulty to 40%
     * @param board twoDBoard to unSolve
     */
    private static void unSolve(Character[][] board) {
        emptyCells.clear();
        Random random = new Random(System.currentTimeMillis());
        IntStream.range(0, LENGTH)
                .forEach(row -> {
                    IntStream.range(0, LENGTH)
                    .forEach(col -> {
                        final boolean erase = (random.nextInt(100) > DIFFICULTY);
                        if(erase) {
                            board[row][col] = new Character(' ');
                            emptyCells.add(row + SEPARATOR + col);
                        }
                    });
        });
    }

    /**
     * shift numbers so that the sudoku is still valid
     * @param block1 3X3 block
     * @param block2 3X3 block
     * @param block3 3X3 block
     * @param numbers entire row
     */
    private static void shiftBlocks(final List<Character> block1,
                                    final List<Character> block2,
                                    final List<Character> block3,
                                    final List<Character> numbers) {
        shiftList(numbers, 3);
        shiftList(block1, 1);
        shiftList(block2, 1);
        shiftList(block3, 1);
    }

    /**
     * shifts a list of chars to the right
     * @param list list to shift
     * @param place how many places to shift
     */
    private static void shiftList(final List<Character> list, final int place) {
        if (list.size() <= 1) {
            return;
        }
        for (int count = 0; count < place; count++) {
            final Character last = list.get(list.size() - 1);
            for (int idx = list.size() - 1; idx >= 1; idx--) {
                final Character shiftItem = list.get(idx - 1);
                list.set(idx, shiftItem);
            }
            list.set(0, last);
        }
    }

    /**
     * helper method to fill the twoDBoard
     * @param board twoDBoard
     * @param numbers numbers that will go in each row
     * @param index row index
     * @return next index
     */
    private static int fillBoard(final Character[][] board, final List<Character> numbers, int index) {
        final AtomicInteger nextIndex = new AtomicInteger(index + 1);
        IntStream.range(0, LENGTH).forEach(j -> board[nextIndex.get()][j] = numbers.get(j));

        shiftList(numbers, 3);

        nextIndex.incrementAndGet();
        IntStream.range(0, LENGTH).forEach(j -> board[nextIndex.get()][j] = numbers.get(j));

        shiftList(numbers, 3);

        nextIndex.incrementAndGet();
        IntStream.range(0, LENGTH).forEach(j -> board[nextIndex.get()][j] = numbers.get(j));

        return nextIndex.get();
    }

    /**
     * print 2D twoDBoard to console
     * @param input
     */
    public static void print(Character[][] input) {
        if (input == null) {
            System.out.println("null");
            return;
        }

        IntStream.range(0, LENGTH)
                .forEach(i -> {
                    IntStream.range(0, LENGTH)
                            .forEach(j -> System.out.print(input[i][j]));
                    System.out.println();
                });
    }

    public static void print(final Character[][] input, final List<String> emptyCells) {

        emptyCells.forEach(cell -> {
            final String[] rowCol = cell.split(Board.SEPARATOR);
            final int row = Integer.parseInt(rowCol[0]);
            final int col = Integer.parseInt(rowCol[1]);
            System.out.print( input[row][col]);
        });
        System.out.println();
    }

    public static Board convertToBoard(final List<String> lines) throws Exception {
        if (lines == null) {
            throw new Exception("null input");
        }
        if (lines.size() < LENGTH) {
            throw new Exception("there should be " + LENGTH + " rows, but was " + lines.size());
        }

        final Board retVal = new Board();
        for (int row = 0; row < LENGTH; row++) {
            final String rowStr = lines.get(row);
            if (rowStr.length() != 9) {
                System.out.println(rowStr);
                throw new Exception("invalid column count expected: " + LENGTH + " but found: " + rowStr.length());
            }

            for (int col = 0; col < LENGTH; col++) {
                final Character entry = ONE_TO_NINE.contains(rowStr.charAt(col)) ? rowStr.charAt(col) : ' ';
                final boolean valid = retVal.insert(entry, row, col);
                if(!valid) {
                    System.out.println(lines);
                    throw new Exception("Invalid entry");
                }
            }
        }
        emptyCells.clear();
        IntStream.range(0, LENGTH).forEach(row -> {
            IntStream.range(0, LENGTH).forEach(col -> {
                final Character ch = retVal.getCellValue(row, col);
                if (!ONE_TO_NINE.contains(ch)) {
                    emptyCells.add(row + SEPARATOR + col);
                }
            });
        });
        return retVal;
    }

	static {
        IntStream.rangeClosed(1, LENGTH).forEach(i -> ONE_TO_NINE.add(new Character(("" + i).charAt(0))));
	}
}

