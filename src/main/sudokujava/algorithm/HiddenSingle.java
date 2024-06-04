package sudokujava.algorithm;

import util.Triple;

import java.util.ArrayList;
import java.util.Stack;

import static sudokujava.algorithm.General.*;

public final class HiddenSingle {
    /**
     * Hidden Single (i.e. one of candidate in row/column/box (e.g. candidate 9 only appears once in the box))
     *
     * @param tiles      tile array
     * @param candidates candidate array
     */
    public static void solve(byte[][] tiles, ArrayList<Byte>[][] candidates) {
        Stack<Triple> nums = new Stack<>();
        for (int row = 0; row < 9; row++) {
            ArrayList<Byte> thing = concatCandidates(candidates[row]);
            for (byte num = 1; num < 10; num++) {
                if (thing.contains(num) && thing.indexOf(num) == thing.lastIndexOf(num)) {
                    boolean GIT = false;
                    for (int column = 0; column < 9; column++) {
                        if (candidates[row][column].contains(num)) {
                            nums.push(new Triple(num, row + 1, column + 1));
                            GIT = true;
                            break;
                        }
                    }
                    if (!GIT) {
                        throw new IllegalArgumentException("AAAAAAAAAAAAAAAAA");
                    }
                }
            }
        }
        for (int column = 0; column < 9; column++) {
            ArrayList<Byte> thing = concatCandidates(candidatesColumn(candidates, column + 1));
            for (byte num = 1; num < 10; num++) {
                if (thing.contains(num) && thing.indexOf(num) == thing.lastIndexOf(num)) {
                    boolean GIT = false;
                    for (int row = 0; row < 9; row++) {
                        if (candidates[row][column].contains(num)) {
                            Triple temp = new Triple(num, row + 1, column + 1);
                            if (!nums.contains(temp)) {
                                nums.push(temp);
                            }
                            GIT = true;
                            break;
                        }
                    }
                    if (!GIT) {
                        throw new IllegalArgumentException("AAAAAAAAAAAAAAAAA");
                    }
                }
            }
        }
        for (int box = 1; box < 10; box++) {
            ArrayList<Byte> thing = concatCandidates(candidatesSquare(candidates, box));
            for (byte num = 1; num < 10; num++) {
                if (thing.contains(num) && thing.indexOf(num) == thing.lastIndexOf(num)) {
                    boolean GIT = false;
                    for (int index = 0; index < 9; index++) {
                        int row = findRowNumInSquare(box, index);
                        int column = findColumnNumInSquare(box, index);
                        if (candidates[row - 1][column - 1].contains(num)) {
                            Triple temp = new Triple(num, row, column);
                            if (!nums.contains(temp)) {
                                nums.push(temp);
                            }
                            GIT = true;
                            break;
                        }
                    }
                    if (!GIT) {
                        throw new IllegalArgumentException("AAAAAAAAAAAAAAAAA");
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
            fillNumber(tiles, candidates, number, row, col);
            System.out.println("Filled Hidden Single " + number + " at " + row + "," + col);
        }
    }
}
