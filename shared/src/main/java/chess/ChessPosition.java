package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private final int row;
    private final int column;

    public static final int MIN_POS = 1;
    public static final int MAX_POS = 8;

    public ChessPosition(int row, int col) {
        if (row < MIN_POS || row > MAX_POS || col < MIN_POS || col > MAX_POS)
            throw new IndexOutOfBoundsException("row and/or col out of range: inputted row = " + row +
                                                                ", inputted column = " + col);

        this.row = row;
        column = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return column;
    }

    @Override
    public String toString() {
        return "ChessPosition{" +
                "row=" + row +
                ", column=" + column +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPosition that = (ChessPosition) o;
        return row == that.row
            && column == that.column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }
}
