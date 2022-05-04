package util;

public final class Triple {
    private byte num;
    private int row;
    private int col;

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
        int hash = 7;
        hash = 13 * hash + this.num;
        hash = 13 * hash + this.row;
        hash = 13 * hash + this.col;
        return hash;
    }
}
