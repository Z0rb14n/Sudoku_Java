package sudokujava.algorithm;

import sudokujava.SolverMode;

import java.util.ArrayList;

import static sudokujava.algorithm.General.*;

public final class HiddenPair {
    /**
     * Only pairs of candidates in row/col/block
     */
    public static void solve(ArrayList<Byte>[][] candidates, SolverMode mode) {
        boolean[] test = new boolean[3];
        //row
        for (int row = 1; row < 10; row++) {
            ArrayList<Byte> lol = concatCandidates(candidates[row - 1]);
            byte[] count = new byte[9];
            for (Byte b : lol) {
                count[b - 1]++;
            }
            ArrayList<Byte> nums = new ArrayList<>();
            for (int i = 0; i < 9; i++) {
                if (count[i] == 2) {
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
            //you know what coumns they're in, just do stuff
            for (int i = 0; i < kek.length; i++) {
                for (int j = 1; j < kek.length && j > i; j++) {
                    if (kek[i][0] == kek[j][0] && kek[i][1] == kek[j][1]) {
                        test[0] = true;
                        System.out.println("Hidden Pair " + nums.get(i) + "," + nums.get(j) + " found at row " + row + ", columns " + (kek[i][0] + 1) + "," + (kek[i][1] + 1));
                        removeCandidateExcept(candidates, new byte[]{nums.get(i), nums.get(j)}, row, kek[i][0] + 1, mode);
                        removeCandidateExcept(candidates, new byte[]{nums.get(i), nums.get(j)}, row, kek[i][1] + 1, mode);
                    }
                }
            }
        }
        //column
        for (int col = 1; col < 10; col++) {
            ArrayList<Byte> lol = concatCandidates(candidatesColumn(candidates, col));
            byte[] count = new byte[9];
            for (Byte b : lol) {
                count[b - 1]++;
            }
            ArrayList<Byte> nums = new ArrayList<>();
            for (int i = 0; i < 9; i++) {
                if (count[i] == 2) {
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
            //you know what coumns they're in, just do stuff
            for (int i = 0; i < kek.length; i++) {
                for (int j = 1; j < kek.length && j > i; j++) {
                    if (kek[i][0] == kek[j][0] && kek[i][1] == kek[j][1]) {
                        test[1] = true;
                        System.out.println("Hidden Pair " + nums.get(i) + "," + nums.get(j) + " found at column " + col + ", rows " + (kek[i][0] + 1) + "," + (kek[i][1] + 1));
                        removeCandidateExcept(candidates, new byte[]{nums.get(i), nums.get(j)}, kek[i][0] + 1, col, mode);
                        removeCandidateExcept(candidates, new byte[]{nums.get(i), nums.get(j)}, kek[i][1] + 1, col, mode);
                    }
                }
            }
        }
        for (int sq = 1; sq < 10; sq++) {
            ArrayList<Byte> lol = concatCandidates(candidatesSquare(candidates, sq));
            byte[] count = new byte[9];
            for (Byte b : lol) {
                count[b - 1]++;
            }
            ArrayList<Byte> nums = new ArrayList<>();
            for (int i = 0; i < 9; i++) {
                if (count[i] == 2) {
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
            //you know what coumns they're in, just do stuff
            for (int i = 0; i < kek.length; i++) {
                for (int j = 1; j < kek.length && j > i; j++) {
                    if (kek[i][0] == kek[j][0] && kek[i][1] == kek[j][1]) {
                        test[2] = true;
                        System.out.println("Hidden Pair " + nums.get(i) + "," + nums.get(j) + " found at square " + sq + ", indexes " + kek[i][0] + "," + kek[i][1]);
                        removeCandidateExcept(candidates, new byte[]{nums.get(i), nums.get(j)}, findRowNumInSquare(sq, kek[i][0]), findColumnNumInSquare(sq, kek[i][0]), mode);
                        removeCandidateExcept(candidates, new byte[]{nums.get(i), nums.get(j)}, findRowNumInSquare(sq, kek[i][1]), findColumnNumInSquare(sq, kek[i][1]), mode);
                    }
                }
            }
        }
        if (!test[0] && !test[1] && !test[2] && mode.showAlgorithmUnusedMessage()) {
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
    private static void removeCandidateExcept(ArrayList<Byte>[][] candidates, byte[] num, int rownum, int colnum, SolverMode mode) {
        ArrayList<Byte> lel = new ArrayList<>(candidates[rownum - 1][colnum - 1]);
        for (Byte lol : lel) {
            boolean getOut = false;
            for (Byte kek : num) {
                if (kek.equals(lol)) {
                    getOut = true;
                    break;
                }
            }
            if (getOut) {
                continue;
            }
            removeCandidate(candidates, lol, rownum, colnum, mode);
        }
    }
}
