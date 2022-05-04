package sudokujava.algorithm;

import static sudokujava.algorithm.General.*;

// Represents a Recursive solver for a Sudoku puzzle
public final class Recursive {
    private static boolean solutionFound = false;

    // MODIFIES: tiles
    public static void solve(byte[][] tiles) {
        solutionFound = false;
        recursivelySolve(tiles);
        if (!solutionFound) System.err.println("No Solution...?");
    }

    // MODIFIES: tiles
    private static void recursivelySolve(byte[][] tiles) {
        if (solutionFound) return;
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (tiles[row][col] == 0) {
                    for (byte num = 1; num < 10; num++) {
                        if (isPossible(tiles, num, row + 1, col + 1)) {
                            tiles[row][col] = num;
                            recursivelySolve(tiles);
                            if (!solutionFound) tiles[row][col] = 0;
                        }
                    }
                    return;
                }
            }
        }
        solutionFound = true;
    }

    private static boolean isPossible(byte[][] tiles, byte num, int rownum, int colnum) {
        if (tiles[rownum - 1][colnum - 1] != 0) {
            return false;
        }
        byte[] col = findColumn(tiles, colnum);
        byte[] row = tiles[rownum - 1];
        byte[] square = findSquare(tiles, findSquareNum(rownum, colnum));
        return (arrayDoesNotContain(row, num) && arrayDoesNotContain(col, num) && arrayDoesNotContain(square, num));
    }
}
