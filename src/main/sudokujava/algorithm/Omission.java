package sudokujava.algorithm;

import util.Pair;

import java.util.ArrayList;

import static sudokujava.algorithm.General.*;

public final class Omission {
    /**
     * essentially, if candidates are concentrated in same block in same row,
     * clear row. Same with columns. Conversely, if the row is concentrated in
     * one block, clear the block.
     */
    public static void solve(byte[][] tiles, Candidates[][] candidates) {
        boolean filled = false;
        //perspective of row
        for (int row = 1; row < 10; row++) {
            if (numbersInRow(tiles, row) > 7) {
                continue;
            }
            byte[] counts = concatCandidates(candidates[row - 1]);
            for (byte num = 1; num < 10; num++) {
                if (counts[num] != 2 && counts[num] != 3) {
                    continue;
                }
                ArrayList<Integer> columns = new ArrayList<>();
                for (int column = 1; column < 10; column++) {
                    if (candidates[row - 1][column - 1].contains(num)) {
                        columns.add(column);
                    }
                }
                if (columns.size() != counts[num]) {
                    throw new IllegalArgumentException("AAAAAAAAAAAAA");
                }
                if (findSquareNum(row, columns.get(0)) != findSquareNum(row, columns.get(columns.size() - 1))) {
                    continue;
                }
                filled = true;
                System.out.println("Row Omission " + num + " at row " + row + ", columns " + columns.get(0) + "-" + columns.get(columns.size() - 1) + ".");
                ArrayList<Pair> kek = new ArrayList<>();
                for (int lolmao : columns) {
                    kek.add(new Pair(row, lolmao));
                }
                removeCandidatesSquare(candidates, num, findSquareNum(row, columns.get(0)), kek);
            }
        }
        for (int column = 1; column < 10; column++) {
            if (numbersInColumn(tiles, column) > 7) {
                continue;
            }
            byte[] counts = concatCandidates(candidatesColumn(candidates, column));
            for (byte num = 1; num < 10; num++) {
                if (counts[num] < 2 || counts[num] > 3) {
                    continue;
                }
                ArrayList<Integer> rows = new ArrayList<>();
                for (int row = 1; row < 10; row++) {
                    if (candidates[row - 1][column - 1].contains(num)) {
                        rows.add(row);
                    }
                }
                if (rows.size() != counts[num]) {
                    throw new IllegalArgumentException("AAAAAAAAAAAAA");
                }
                if ((int) Math.ceil((float) rows.get(0) / 3) != (int) Math.ceil((float) rows.get(rows.size() - 1) / 3)) {
                    continue;
                }
                filled = true;
                System.out.println("Column Omission " + num + " at column " + column + ", rows " + rows.get(0) + "-" + rows.get(rows.size() - 1) + ".");
                ArrayList<Pair> kek = new ArrayList<>();
                for (int lolmao : rows) {
                    kek.add(new Pair(lolmao, column));
                }
                removeCandidatesSquare(candidates, num, findSquareNum(rows.get(0), column), kek);
            }
        }
        for (int squarenum = 1; squarenum < 10; squarenum++) {
            if (numbersInSquare(tiles, squarenum) > 7) {
                continue;
            }
            byte[] counts = concatCandidates(candidatesSquare(candidates, squarenum));
            for (byte num = 1; num < 10; num++) {
                if (counts[num] < 2 || counts[num] > 3) {
                    continue;
                }
                ArrayList<Integer> indexes = new ArrayList<>();
                for (int index = 0; index < 9; index++) {
                    if (candidates[findRowNumInSquare(squarenum, index) - 1][findColumnNumInSquare(squarenum, index) - 1].contains(num)) {
                        indexes.add(index);
                    }
                }
                if (indexes.size() != counts[num]) {
                    throw new IllegalArgumentException("AAAAAAAAAAAAA");
                }
                boolean rowR = false;
                if (Math.floor((float) indexes.get(indexes.size() - 1) / 3) == Math.floor((float) indexes.get(0) / 3)) {
                    filled = true;
                    rowR = true;
                    int rowNum = findRowNumInSquare(squarenum, indexes.get(0));
                    int columnMin = findColumnNumInSquare(squarenum, indexes.get(0));
                    int columnMax = findColumnNumInSquare(squarenum, indexes.get(indexes.size() - 1));
                    System.out.println("Block/Row Omission " + num + " in block " + squarenum + ", row " + rowNum + ", columns " + columnMin + "-" + columnMax + ".");
                    for (int col = 1; col < 10; col++) {
                        if (col >= columnMin && col <= columnMax) {
                            continue;
                        }
                        removeCandidate(candidates, num, rowNum, col);
                    }
                }
                if (rowR) {
                    continue;
                }
                if (indexes.get(indexes.size() - 1) % 3 == indexes.get(0) % 3) {
                    if (indexes.size() == 3 && !indexes.get(1).equals(indexes.get(0))) {
                        continue;
                    }
                    filled = true;
                    int colNum = findColumnNumInSquare(squarenum, indexes.get(0));
                    int rowMin = findRowNumInSquare(squarenum, indexes.get(0));
                    int rowMax = findRowNumInSquare(squarenum, indexes.get(indexes.size() - 1));
                    System.out.println("Column Omission " + num + " in block " + squarenum + ", column " + colNum + ", rows " + rowMin + "-" + rowMax + ".");
                    for (int row = 1; row < 10; row++) {
                        if (row >= rowMin && row <= rowMax) {
                            continue;
                        }
                        removeCandidate(candidates, num, row, colNum);
                    }
                }
            }
        }
        if (!filled && AlgorithmLogSettings.getInstance().shouldPrintAlgorithmUnused()) {
            System.out.println("No omissions were found.");
        }
    }

    /**
     * Removes all candidates of num from square with Pair exception
     *
     * @param num        number to remove
     * @param squarenum  square number (1-9) to remove from
     * @param exceptions exceptions (i.e. numbers to leave out)
     */
    private static void removeCandidatesSquare(Candidates[][] candidates, byte num, int squarenum, ArrayList<Pair> exceptions) {
        if (num < 1 || num > 9) {
            throw new IllegalArgumentException("Called removeCandidatesSquare with invalid param num: " + num);
        }
        if (squarenum < 1 || squarenum > 9) {
            throw new IllegalArgumentException("Called removeCandidatesSquare with invalid param squarenum: " + squarenum);
        }
        for (int index = 0; index < 9; index++) {
            int row = findRowNumInSquare(squarenum, index);
            int column = findColumnNumInSquare(squarenum, index);
            boolean getOut = false;
            for (Pair lol : exceptions) {
                if (lol.getX() == row && lol.getY() == column) {
                    getOut = true;
                    break;
                }
            }
            if (getOut) {
                continue;
            }
            removeCandidate(candidates, num, row, column);
        }
    }
}
