package sudokujava.algorithm;

import sudokujava.SolverMode;
import util.Pair;

import java.util.ArrayList;
import java.util.Stack;

import static sudokujava.algorithm.General.crash;
import static sudokujava.algorithm.General.fillNumber;

public final class NakedSingle {
    public static void solve(byte[][] tiles, ArrayList<Byte>[][] candidates, SolverMode mode) {
        Stack<Pair> numbers = new Stack<>();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (candidates[i][j].size() == 1) {
                    numbers.push(new Pair(i + 1, j + 1));
                }
            }
        }
        if (numbers.isEmpty()) {
            if (mode.showAlgorithmUnusedMessage()) {
                System.out.println("No Naked Singles are found.");
            }
            return;
        }
        while (!numbers.isEmpty()) {
            Pair lol = numbers.pop();
            int row = lol.getX();
            int column = lol.getY();
            if (tiles[row - 1][column - 1] != 0) {
                continue;
            }
            try {
                byte number = candidates[row - 1][column - 1].get(0);
                fillNumber(tiles, candidates, number, row, column, mode);
                System.out.println("Naked Single " + number + " at " + row + "," + column);
            } catch (IndexOutOfBoundsException e) {
                crash(tiles, candidates, mode);
            }
        }
    }
}
