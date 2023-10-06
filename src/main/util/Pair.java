package util;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Utility to contain a point or pair with x/y coordinates.
 */
public class Pair {
    private final int x;
    private final int y;

    public Pair(ArrayList<Byte> lol) {
        if (lol == null || lol.size() != 2)
            throw new IllegalArgumentException("Pair initializer called with invalid arraylist param.");
        x = lol.get(0);
        y = lol.get(1);
    }

    public Pair(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pair)) return false;
        Pair temp = (Pair) o;
        return temp.x == x && temp.y == y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
