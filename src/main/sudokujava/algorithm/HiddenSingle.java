package sudokujava.algorithm;

import util.Triple;

import java.util.Stack;

import static sudokujava.algorithm.General.*;

public final class HiddenSingle {
    /**
     * Hidden Single (i.e. one of candidate in row/column/box (e.g. candidate 9 only appears once in the box))
     *
     * @param tiles      tile array
     * @param candidates candidate array
     */
    public static void solve(byte[][] tiles, Candidates[][] candidates) {
        Stack<Triple> nums = new Stack<>();
        for (int row = 0; row < 9; row++) {
            byte[] counts = concatCandidates(candidates[row]);
            outer:
            for (byte num = 1; num < 10; num++) {
                if (counts[num] == 1) {
                    for (int column = 0; column < 9; column++) {
                        if (candidates[row][column].contains(num)) {
                            nums.push(new Triple(num, row + 1, column + 1));
                            continue outer;
                        }
                    }
                }
            }
        }
        for (int column = 0; column < 9; column++) {
            byte[] counts = concatCandidates(candidatesColumn(candidates, column + 1));
            outer:
            for (byte num = 1; num < 10; num++) {
                if (counts[num] == 1) {
                    for (int row = 0; row < 9; row++) {
                        if (candidates[row][column].contains(num)) {
                            Triple temp = new Triple(num, row + 1, column + 1);
                            if (!nums.contains(temp)) {
                                nums.push(temp);
                            }
                            continue outer;
                        }
                    }
                }
            }
        }
        for (int box = 1; box < 10; box++) {
            byte[] counts = concatCandidates(candidatesSquare(candidates, box));
            outer:
            for (byte num = 1; num < 10; num++) {
                if (counts[num] == 1) {
                    for (int index = 0; index < 9; index++) {
                        int row = findRowNumInSquare(box, index);
                        int column = findColumnNumInSquare(box, index);
                        if (candidates[row - 1][column - 1].contains(num)) {
                            Triple temp = new Triple(num, row, column);
                            if (!nums.contains(temp)) {
                                nums.push(temp);
                            }
                            continue outer;
                        }
                    }
                }
            }
        }
        if (nums.empty()) {
            if (AlgorithmLogSettings.getInstance().shouldPrintAlgorithmUnused()) {
                System.out.println("No Hidden Singles were found.");
            }
            return;
        }
        while (!nums.isEmpty()) {
            Triple lol = nums.pop();
            byte number = lol.getNum();
            int row = lol.getRow();
            int col = lol.getCol();
            if (tiles[row - 1][col - 1] != 0) continue;
            fillNumber(tiles, candidates, number, row, col);
            System.out.println("Filled Hidden Single " + number + " at " + row + "," + col);
        }
    }
}
