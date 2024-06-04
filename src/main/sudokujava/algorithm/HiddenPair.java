package sudokujava.algorithm;

import java.util.ArrayList;

import static sudokujava.algorithm.General.*;

public final class HiddenPair {
    /**
     * Only pairs of candidates in row/col/block
     */
    public static void solve(Candidates[][] candidates) {
        boolean filled = false;
        //row
        for (int row = 1; row < 10; row++) {
            byte[] counts = concatCandidates(candidates[row - 1]);
            ArrayList<Byte> nums = new ArrayList<>();
            for (int i = 0; i < 9; i++) {
                if (counts[i + 1] == 2) {
                    nums.add((byte) (i + 1));
                }
            }
            if (nums.size() <= 1) {
                continue;
            }
            int[][] kek = new int[nums.size()][2];
            for (int col = 0; col < 9; col++) {
                for (int i = 0; i < nums.size(); i++) {
                    if (candidates[row - 1][col].contains(nums.get(i))) {
                        if (kek[i][0] != 0) {
                            kek[i][1] = col;
                        } else {
                            kek[i][0] = col;
                        }
                        break;
                    }
                }
            }
            //you know what columns they're in, just do stuff
            for (int i = 0; i < kek.length; i++) {
                for (int j = 1; j < kek.length && j > i; j++) {
                    if (kek[i][0] == kek[j][0] && kek[i][1] == kek[j][1]) {
                        filled = true;
                        System.out.println("Hidden Pair " + nums.get(i) + "," + nums.get(j) + " found at row " + row + ", columns " + (kek[i][0] + 1) + "," + (kek[i][1] + 1));
                        removeCandidateExcept(candidates, new byte[]{nums.get(i), nums.get(j)}, row, kek[i][0] + 1);
                        removeCandidateExcept(candidates, new byte[]{nums.get(i), nums.get(j)}, row, kek[i][1] + 1);
                    }
                }
            }
        }
        //column
        for (int col = 1; col < 10; col++) {
            byte[] counts = concatCandidates(candidatesColumn(candidates, col));
            ArrayList<Byte> nums = new ArrayList<>();
            for (int i = 0; i < 9; i++) {
                if (counts[i + 1] == 2) {
                    nums.add((byte) (i + 1));
                }
            }
            if (nums.size() <= 1) {
                continue;
            }
            int[][] kek = new int[nums.size()][2];
            for (int row = 0; row < 9; row++) {
                for (int i = 0; i < nums.size(); i++) {
                    if (candidates[row][col - 1].contains(nums.get(i))) {
                        if (kek[i][0] != 0) {
                            kek[i][1] = row;
                        } else {
                            kek[i][0] = row;
                        }
                        break;
                    }
                }
            }
            //you know what rows they're in, just do stuff
            for (int i = 0; i < kek.length; i++) {
                for (int j = 1; j < kek.length && j > i; j++) {
                    if (kek[i][0] == kek[j][0] && kek[i][1] == kek[j][1]) {
                        filled = true;
                        System.out.println("Hidden Pair " + nums.get(i) + "," + nums.get(j) + " found at column " + col + ", rows " + (kek[i][0] + 1) + "," + (kek[i][1] + 1));
                        removeCandidateExcept(candidates, new byte[]{nums.get(i), nums.get(j)}, kek[i][0] + 1, col);
                        removeCandidateExcept(candidates, new byte[]{nums.get(i), nums.get(j)}, kek[i][1] + 1, col);
                    }
                }
            }
        }
        for (int sq = 1; sq < 10; sq++) {
            byte[] counts = concatCandidates(candidatesSquare(candidates, sq));
            ArrayList<Byte> nums = new ArrayList<>();
            for (int i = 0; i < 9; i++) {
                if (counts[i + 1] == 2) {
                    nums.add((byte) (i + 1));
                }
            }
            if (nums.size() <= 1) {
                continue;
            }
            int[][] kek = new int[nums.size()][2];
            for (int index = 0; index < 9; index++) {
                for (int i = 0; i < nums.size(); i++) {
                    if (candidates[findRowNumInSquare(sq, index) - 1][findColumnNumInSquare(sq, index) - 1].contains(nums.get(i))) {
                        if (kek[i][0] != 0) {
                            kek[i][1] = index;
                        } else {
                            kek[i][0] = index;
                        }
                        break;
                    }
                }
            }
            //you know where they are, just do stuff
            for (int i = 0; i < kek.length; i++) {
                for (int j = 1; j < kek.length && j > i; j++) {
                    if (kek[i][0] == kek[j][0] && kek[i][1] == kek[j][1]) {
                        filled = true;
                        System.out.println("Hidden Pair " + nums.get(i) + "," + nums.get(j) + " found at square " + sq + ", indexes " + kek[i][0] + "," + kek[i][1]);
                        removeCandidateExcept(candidates, new byte[]{nums.get(i), nums.get(j)}, findRowNumInSquare(sq, kek[i][0]), findColumnNumInSquare(sq, kek[i][0]));
                        removeCandidateExcept(candidates, new byte[]{nums.get(i), nums.get(j)}, findRowNumInSquare(sq, kek[i][1]), findColumnNumInSquare(sq, kek[i][1]));
                    }
                }
            }
        }
        if (!filled && AlgorithmLogSettings.getInstance().shouldPrintAlgorithmUnused()) {
            System.out.println("No Hidden Pairs were found.");
        }
    }

    /**
     * Remove all candidates except num
     *
     * @param num    numbers exceptions
     * @param rownum rownumber (1-9)
     * @param colnum colnumber (1-9)
     */
    private static void removeCandidateExcept(Candidates[][] candidates, byte[] num, int rownum, int colnum) {
        Candidates numSet = new Candidates(num);
        for (Byte cand : candidates[rownum - 1][colnum - 1]) {
            if (!numSet.contains(cand)) {
                removeCandidate(candidates, cand, rownum, colnum);
            }
        }
    }
}
