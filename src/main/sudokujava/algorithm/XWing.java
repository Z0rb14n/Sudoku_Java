package sudokujava.algorithm;

import java.util.ArrayList;

import static sudokujava.algorithm.General.*;

public final class XWing {
    /**
     * X-Wing solver - essentially pairs in rows, kill everything in column
     */
    public static void solve(byte[][] tiles, ArrayList<Byte>[][] candidates) {
        boolean rowT = false;
        boolean colT = false;
        for (int row = 1; row < 10; row++) {
            if (numbersInRow(tiles, row) > 7) {
                continue;
            }
            ArrayList<Byte> thing = concatCandidates(candidates[row - 1]);
            byte[] count = new byte[9];
            int[] col1 = new int[2];
            for (Byte lol : thing) {
                count[lol - 1]++;
            }
            for (byte num = 1; num < 10; num++) {
                if (count[num - 1] != 2) {
                    continue;
                }
                for (int col = 1; col < 10; col++) {
                    if (candidates[row - 1][col - 1].contains(num)) {
                        if (col1[0] == 0) {
                            col1[0] = col;
                        } else {
                            col1[1] = col;
                        }
                    }
                }
                for (int row2 = 2; row2 < 10 && row2 > row; row2++) {
                    if (numbersInRow(tiles, row2) > 7) {
                        continue;
                    }
                    ArrayList<Byte> cat = concatCandidates(candidates[row2]);
                    byte[] counts = new byte[9];
                    for (Byte lol : cat) {
                        counts[lol - 1]++;
                    }
                    if (counts[num - 1] != 2) {
                        break;
                    }
                    int[] col2 = new int[2];
                    for (int col = 1; col < 10; col++) {
                        if (candidates[row2 - 1][col - 1].contains(num)) {
                            if (col2[0] == 0) {
                                col2[0] = col;
                            } else {
                                col2[1] = col;
                            }
                        }
                    }
                    if (col1[0] != col2[0] || col1[1] != col2[1]) {
                        continue;
                    }
                    rowT = true;
                    System.out.println("X-Wing " + num + " found at columns " + col1[0] + "," + col1[1] + ", rows " + row + "," + row2);
                    removeCandidatesColumn(candidates, num, col1[0], row, row2);
                    removeCandidatesColumn(candidates, num, col1[1], row, row2);
                }
            }
        }
        for (int col = 1; col < 10; col++) {
            if (numbersInColumn(tiles, col) > 7) {
                continue;
            }
            ArrayList<Byte> thing = concatCandidates(candidatesColumn(candidates, col));
            byte[] count = new byte[9];
            int[] row1 = new int[2];
            for (Byte lol : thing) {
                count[lol - 1]++;
            }
            for (byte num = 1; num < 10; num++) {
                if (count[num - 1] != 2) {
                    continue;
                }
                for (int row = 1; row < 10; row++) {
                    if (candidates[row - 1][col - 1].contains(num)) {
                        if (row1[0] == 0) {
                            row1[0] = row;
                        } else {
                            row1[1] = row;
                        }
                    }
                }
                for (int col2 = 2; col2 < 10 && col2 > col; col2++) {
                    if (numbersInRow(tiles, col2) > 7) {
                        continue;
                    }
                    ArrayList<Byte> cat = concatCandidates(candidatesColumn(candidates, col2));
                    byte[] counts = new byte[9];
                    for (Byte lol : cat) {
                        counts[lol - 1]++;
                    }
                    if (counts[num - 1] != 2) {
                        break;
                    }
                    int[] row2 = new int[2];
                    for (int row = 1; row < 10; row++) {
                        if (candidates[row - 1][col2 - 1].contains(num)) {
                            if (row2[0] == 0) {
                                row2[0] = row;
                            } else {
                                row2[1] = row;
                            }
                        }
                    }
                    if (row2[0] != row1[0] || row2[1] != row1[1]) {
                        continue;
                    }
                    colT = true;
                    System.out.println("X-Wing " + num + " found at rows " + row1[0] + "," + row1[1] + ", columns " + col + "," + col2);
                    removeCandidatesRow(candidates, num, row1[0], col, col2);
                    removeCandidatesRow(candidates, num, row1[1], col, col2);
                }
            }
        }
        if (!rowT && !colT && AlgorithmLogSettings.getInstance().shouldPrintAlgorithmUnused()) {
            System.out.println("No X-Wings were found.");
        }
    }
}
