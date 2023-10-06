package util;

import java.util.Objects;

public final class Triple {
    private final byte num;
    private final int row;
    private final int col;

    public Triple(byte n, int x, int y) {
        num = n;
        row = x;
        col = y;
    }

    public byte getNum() {
        return num;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Triple)) return false;
        Triple lol = (Triple) o;
        return lol.num == num && lol.row == row && lol.col == col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(num, row, col);
    }
}
