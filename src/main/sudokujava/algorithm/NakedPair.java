package sudokujava.algorithm;

import util.Pair;

import java.util.ArrayList;
import java.util.HashSet;

import static sudokujava.algorithm.General.*;

public final class NakedPair {
    /**
     * Two tiles having the same candidates (pairs of candidates)
     */
    public static void solve(byte[][] tiles, ArrayList<Byte>[][] candidates) {
        boolean[] test = new boolean[3];
        //check in row
        for (int row = 1; row < 10; row++) {
            ArrayList<Pair> lol = new ArrayList<>();
            ArrayList<Integer> cols = new ArrayList<>();
            for (int col = 0; col < 9; col++) {
                if (candidates[row - 1][col].size() == 2) {
                    lol.add(new Pair(candidates[row - 1][col].get(0), candidates[row - 1][col].get(1)));
                    cols.add(col);
                }
            }
            HashSet<Pair> pairs = new HashSet<>(lol);
            if (pairs.size() < 2 || pairs.size() >= lol.size()) {
                continue;
            }
            test[0] = true;
            Pair correct = null;
            int index1 = -1;
            int index2 = -1;
            //ASSUMPTION: THERE IS NO THIRD PAIR (there shouldn't be)
            for (Pair xd : pairs) {
                if (lol.indexOf(xd) != lol.lastIndexOf(xd)) {
                    correct = xd;
                    index1 = lol.indexOf(xd);
                    index2 = lol.lastIndexOf(xd);
                    break;
                }
            }
            if (index1 == -1 || correct == null) {
                throw new IllegalArgumentException("AAAAAAAAAAAA");
            }
            System.out.println("Naked Pair " + correct.getX() + "," + correct.getY() + " found at row " + row + ", columns " + (cols.get(index1) + 1) + "," + (cols.get(index2) + 1));
            removeCandidatesRow(candidates, (byte) correct.getX(), row, cols.get(index1) + 1, cols.get(index2) + 1);
            removeCandidatesRow(candidates, (byte) correct.getY(), row, cols.get(index1) + 1, cols.get(index2) + 1);
        }
        //check columns
        for (int column = 1; column < 10; column++) {
            ArrayList<Pair> lol = new ArrayList<>();
            ArrayList<Integer> rows = new ArrayList<>();
            for (int row = 0; row < 9; row++) {
                if (candidates[row][column - 1].size() == 2) {
                    lol.add(new Pair(candidates[row][column - 1].get(0), candidates[row][column - 1].get(1)));
                    rows.add(row);
                }
            }
            HashSet<Pair> pairs = new HashSet<>(lol);
            if (pairs.size() < 2 || pairs.size() >= lol.size()) {
                continue;
            }
            test[1] = true;
            Pair correct = null;
            int index1 = -1;
            int index2 = -1;
            //ASSUMPTION: THERE IS NO THIRD PAIR (there shouldn't be)
            for (Pair xd : pairs) {
                if (lol.indexOf(xd) != lol.lastIndexOf(xd)) {
                    correct = xd;
                    index1 = lol.indexOf(xd);
                    index2 = lol.lastIndexOf(xd);
                    break;
                }
            }
            if (index1 == -1 || correct == null) {
                crash(tiles, candidates);
            }
            System.out.println("Naked Pair " + correct.getX() + "," + correct.getY() + " found at column " + column + ", rows " + (rows.get(index1) + 1) + "," + (rows.get(index2) + 1));
            removeCandidatesColumn(candidates, (byte) correct.getX(), column, rows.get(index1) + 1, rows.get(index2) + 1);
            removeCandidatesColumn(candidates, (byte) correct.getY(), column, rows.get(index1) + 1, rows.get(index2) + 1);
        }
        for (int square = 1; square < 10; square++) {
            ArrayList<Pair> lol = new ArrayList<>();
            ArrayList<Integer> indexes = new ArrayList<>();
            for (int index = 0; index < 9; index++) {
                int row = findRowNumInSquare(square, index);
                int column = findColumnNumInSquare(square, index);
                if (candidates[row - 1][column - 1].size() == 2) {
                    lol.add(new Pair(candidates[row - 1][column - 1]));
                    indexes.add(index);
                }
            }
            HashSet<Pair> pairs = new HashSet<>(lol);
            if (pairs.size() < 2 || pairs.size() >= lol.size()) {
                continue;
            }
            test[2] = true;
            Pair correct = null;
            int index1 = -1;
            int index2 = -1;
            //ASSUMPTION: THERE IS NO THIRD PAIR (there shouldn't be)
            for (Pair xd : pairs) {
                if (lol.indexOf(xd) != lol.lastIndexOf(xd)) {
                    correct = xd;
                    index1 = lol.indexOf(xd);
                    index2 = lol.lastIndexOf(xd);
                    break;
                }
            }
            if (index1 == -1 || correct == null) {
                crash(tiles, candidates);
            }
            System.out.println("Naked Pair " + correct.getX() + "," + correct.getY() + " found at square " + square + ", indexes " + indexes.get(index1) + "," + indexes.get(index2));
            removeCandidatesSquare(candidates, (byte) correct.getX(), square, indexes.get(index1), indexes.get(index2));
            removeCandidatesSquare(candidates, (byte) correct.getY(), square, indexes.get(index1), indexes.get(index2));
        }
        if (!(test[0] || test[1] || test[2]) && AlgorithmLogSettings.getInstance().shouldPrintAlgorithmUnused()) {
            System.out.println("No Naked Pairs were found.");
        }
    }
}
