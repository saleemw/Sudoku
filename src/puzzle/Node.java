package puzzle;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static puzzle.Board.LENGTH;

public class Node {

    Character[][] data = null;
    List<Node> children = null;

    public Node (final Character[][] inData) {
        data = new Character[LENGTH][LENGTH];

        IntStream.range(0, LENGTH)
                 .forEach(i -> IntStream.range(0, LENGTH)
                                        .forEach(j -> data[i][j] = inData[i][j]));
    }

    public Character[][] getData() {
        return data;
    }

    public List<Node> getChildren () {
        return children;
    }
    /**
     * This method is to generate children for an existing node using the given values
     * @param row target row
     * @param col target column
     * @param values possible values
     */
    @SuppressWarnings({"unused"})
    public void generateChildrenForValues(final int row, final int col, final Set<Character> values) throws Exception{
        if (children == null) {
            children = new ArrayList<Node>();
        }
        children.clear();
        for (Character value : values) {
            if (value != data[row][col]) {
                final Node childNode = new Node(data);
                childNode.setValue(row, col, value);
                children.add(childNode);
            }
        }
        if (children.size() > 8) {
            throw new Exception("invalid children count");
        }
    }

    /**
     * This method is to generate all possible children for an existing node
     * @param row target row
     * @param col target col
     */
    public void generateChildren(final int row, final int col) throws Exception{
        if (children == null) {
            children = new ArrayList<Node>();
        }
        children.clear();

        IntStream.rangeClosed(1, LENGTH)
                .forEach(i -> {
                    final Character value = ("" + i).charAt(0);
                    if (value != data[row][col]) {
                        final boolean isValid = Board.isValidEntry(data, value, row, col);
                        if (isValid) {
                            final Node childNode = new Node(data);
                            childNode.setValue(row, col, value);
                            children.add(childNode);
                        }
                    }
                });

    }

    /**
     * convenient method for setting cell values
     * @param row target row
     * @param col target column
     * @param value input value
     */
    private void setValue(final int row, final int col, final Character value) {
        data[row][col] = value;
    }

    public boolean isSolved () {
        return Board.isSolved(data);
    }

}
