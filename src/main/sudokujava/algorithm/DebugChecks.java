package sudokujava.algorithm;

import sudokujava.SolverMode;

import java.util.ArrayList;

import static sudokujava.algorithm.General.crash;

public final class DebugChecks {
    /**
     * Simple check to determine if tile array is 0 but has 0 candidates
     *
     * @param tiles      tile array
     * @param candidates candidate array
     * @param mode       solver mode
     */
    public static void simpleCheckValid(byte[][] tiles, ArrayList<Byte>[][] candidates, SolverMode mode) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (tiles[i][j] == 0 && candidates[i][j].isEmpty()) {
                    crash(tiles, candidates, mode);
                }
            }
        }
    }

    /**
     * Advanced check valid - does not check whether number exists in block
     */
    public static void checkValid(byte[][] tiles, ArrayList<Byte>[][] candidates, SolverMode mode) {
        simpleCheckValid(tiles, candidates, mode);
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                if (tiles[i][j] == 0) {
                    continue;
                }
                //check column
                byte num = tiles[i][j];
                for (int row = 0; row < 9; row++) {
                    if (row == i) {
                        continue;
                    }
                    if (tiles[row][j] == num) {
                        System.err.println("Duplicate number in column");
                        crash(tiles, candidates, mode);
                    }
                }
                for (int col = 0; col < 9; col++) {
                    if (col == j) {
                        continue;
                    }
                    if (tiles[i][col] == num) {
                        System.err.println("Duplicate number in row");
                        crash(tiles, candidates, mode);
                    }
                }
            }
        }
    }
}
