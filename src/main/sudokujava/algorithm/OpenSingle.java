package sudokujava.algorithm;

import util.Pair;

import java.util.ArrayList;
import java.util.Stack;

import static sudokujava.algorithm.General.*;

public final class OpenSingle {
    /**
     * Fill open singles (i.e. 8 numbers in row/col/squ)
     */
    public static void solve(byte[][] tiles, ArrayList<Byte>[][] candidates) {
        boolean filled = false;
        Stack<Integer> rows = new Stack<>();
        for (int i = 1; i < 10; i++) {
            if (numbersInRow(tiles, i) == 8) {
                rows.push(i);
            }
        }
        if (!rows.isEmpty()) filled = true;
        while (!rows.isEmpty()) {
            int row = rows.pop();
            Pair pair = whichMissing(tiles[row - 1]);
            byte number = (byte) pair.getX();
            int index = pair.getY();
            System.out.println("Filled Open Single " + number + ", row " + row + ", column " + (index + 1));
            fillNumber(tiles, candidates, number, row, index + 1);
        }
        Stack<Integer> cols = new Stack<>();
        for (int i = 1; i < 10; i++) {
            if (numbersInColumn(tiles, i) == 8) {
                cols.push(i);
            }
        }
        if (!cols.isEmpty()) filled = true;
        while (!cols.isEmpty()) {
            int col = cols.pop();
            byte[] column = findColumn(tiles, col);
            Pair pair = whichMissing(column);
            byte number = (byte) pair.getX();
            int index = pair.getY();
            System.out.println("Filled Open Single " + number + ", row " + (index + 1) + ", column " + col);
            fillNumber(tiles, candidates, number, index + 1, col);
        }
        Stack<Integer> boxs = new Stack<>();
        for (int i = 1; i < 10; i++) {
            if (numbersInSquare(tiles, i) == 8) {
                boxs.push(i);
            }
        }
        if (!boxs.isEmpty()) filled = true;
        while (!boxs.isEmpty()) {
            int box = boxs.pop();
            byte[] square = findSquare(tiles, box);
            Pair pair = whichMissing(square);
            byte number = (byte) pair.getX();
            int index = pair.getY();
            int row = findRowNumInSquare(box, index);
            int col = findColumnNumInSquare(box, index);
            System.out.println("Filled Open Single " + number + ", row " + row + ", column " + col);
            fillNumber(tiles, candidates, number, row, col);
        }
        if (!filled && AlgorithmLogSettings.getInstance().shouldPrintAlgorithmUnused()) {
            System.out.println("No Open Singles were found.");
        }
    }

    /**
     * Given an array of values from [1-9] with exactly one value 0, find the missing value.
     *
     * @param data Unique values of 0-9; exactly one value 0
     * @return Pair of (missing value, index)
     */
    private static Pair whichMissing(byte[] data) {
        int sum = 0;
        int index = -1;
        for (int i = 0; i < data.length; i++) {
            byte b = data[i];
            if (b == 0) index = i;
            sum += b;
        }
        return new Pair(45 - sum, index);
    }
}
