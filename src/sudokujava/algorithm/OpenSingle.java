package sudokujava.algorithm;

import sudokujava.SolverMode;

import java.util.ArrayList;
import java.util.Stack;

import static sudokujava.algorithm.General.*;

public final class OpenSingle {
    /**
     * Fill open singles (i.e. 8 numbers in row/col/squ)
     */
    public static void solve(byte[][] tiles, ArrayList<Byte>[][] candidates, SolverMode mode) {
        boolean[] tests = new boolean[3];
        Stack<Integer> rows = new Stack<>();
        for (int i = 1; i < 10; i++) {
            if (numbersInRow(tiles, i) == 8) {
                rows.push(i);
            }
        }
        if (!rows.isEmpty()) {
            tests[0] = true;
            do {
                int row = rows.pop();
                boolean broken = false;
                byte number = 0;
                for (byte num = 1; num < 10; num++) {
                    if (arrayDoesNotContain(tiles[row - 1], num)) {
                        number = num;
                        broken = true;
                        break;
                    }
                }
                if (!broken) {
                    throw new IllegalArgumentException("AAAAAAAAAAAAAAA");
                }
                for (int i = 0; i < 9; i++) {
                    if (tiles[row - 1][i] == 0) {
                        System.out.println("Filled Open Single " + number + ", row " + row + ", column " + (i + 1));
                        fillNumber(tiles, candidates, number, row, i + 1, mode);
                        break;
                    }
                }
            } while (!rows.isEmpty());
        }
        Stack<Integer> cols = new Stack<>();
        for (int i = 1; i < 10; i++) {
            if (numbersInColumn(tiles, i) == 8) {
                cols.push(i);
            }
        }
        if (!cols.isEmpty()) {
            tests[1] = true;
            do {
                int col = cols.pop();
                boolean broken = false;
                byte number = 0;
                byte[] thing = findColumn(tiles, col);
                for (byte num = 1; num < 10; num++) {
                    if (arrayDoesNotContain(thing, num)) {
                        number = num;
                        broken = true;
                        break;
                    }
                }
                if (!broken) {
                    throw new IllegalArgumentException("AAAAAAAAAAAAAAA");
                }
                for (int i = 0; i < 9; i++) {
                    if (tiles[i][col - 1] == 0) {
                        System.out.println("Filled Open Single " + number + ", row " + (i + 1) + ", column " + col);
                        fillNumber(tiles, candidates, number, i + 1, col, mode);
                        break;
                    }
                }
            } while (!cols.isEmpty());
        }
        Stack<Integer> boxs = new Stack<>();
        for (int i = 1; i < 10; i++) {
            if (numbersInSquare(tiles, i) == 8) {
                boxs.push(i);
            }
        }
        if (!boxs.isEmpty()) {
            tests[2] = true;
            do {
                int box = boxs.pop();
                boolean broken = false;
                byte number = 0;
                byte[] thing = findSquare(tiles, box);
                for (byte num = 1; num < 10; num++) {
                    if (arrayDoesNotContain(thing, num)) {
                        number = num;
                        broken = true;
                        break;
                    }
                }
                if (!broken) {
                    throw new IllegalArgumentException("AAAAAAAAAAAAAAA");
                }
                for (int i = 0; i < 9; i++) {
                    if (tiles[findRowNumInSquare(box, i) - 1][findColumnNumInSquare(box, i) - 1] == 0) {
                        int row = findRowNumInSquare(box, i);
                        int col = findColumnNumInSquare(box, i);
                        System.out.println("Filled Open Single " + number + ", row " + row + ", column " + col);
                        fillNumber(tiles, candidates, number, row, col, mode);
                        break;
                    }
                }
            } while (!cols.isEmpty());
        }
        if (!tests[0] && !tests[1] && !tests[2]) {
            if (mode.showAlgorithmUnusedMessage()) {
                System.out.println("No Open Singles were found.");
            }
        }
    }
}
