package sudokujava.algorithm;

import java.util.ArrayList;

// FOR ALL METHODS, REQUIRES tiles/candidates PARAMETER to be VALID SUDOKU PUZZLE/CANDIDATES
// Represents general convenience methods for all algorithms
public final class General {
    public static boolean arrayContains(byte[] array, byte test) {
        for (byte b : array) {
            if (b == test) {
                return true;
            }
        }
        return false;
    }

    /**
     * Concatenates all the Arrays of ArrayLists of candidates (used in Hidden
     * Singles)
     *
     * @param a array of list of candidates
     * @return concatenated candidates
     */
    static ArrayList<Byte> concatCandidates(ArrayList<Byte>[] a) {
        ArrayList<Byte> temp = new ArrayList<>();
        for (ArrayList<Byte> a1 : a) {
            temp.addAll(a1);
        }
        return temp;
    }

    /**
     * Returns the array of arraylists of candidates in a given column.
     *
     * @param colnum column number to access
     * @return array of ArrayList of candidates.
     */
    static ArrayList<Byte>[] candidatesColumn(ArrayList<Byte>[][] candidates, int colnum) {
        if (colnum < 1 || colnum > 9) {
            throw new IllegalArgumentException("candidatesColumn called with invalid arugment: " + colnum);
        }
        @SuppressWarnings("unchecked")
        ArrayList<Byte>[] lol = (ArrayList<Byte>[]) new ArrayList[9];
        for (int i = 0; i < 9; i++) {
            lol[i] = candidates[i][colnum - 1];
            lol[i].trimToSize();
        }
        return lol;
    }

    /**
     * Returns the array of arraylists of candidates in a given square
     *
     * @param squarenum square num to access
     * @return array of ArrayList of candidates.
     */
    static ArrayList<Byte>[] candidatesSquare(ArrayList<Byte>[][] candidates, int squarenum) {
        if (squarenum < 1 || squarenum > 9) {
            throw new IllegalArgumentException("candidatesSquare called with invalid arugment: " + squarenum);
        }
        @SuppressWarnings("unchecked")
        ArrayList<Byte>[] lol = (ArrayList<Byte>[]) new ArrayList[9];
        int temp = (int) Math.floor(((float) (squarenum - 1)) / 3);
        int temp1 = (squarenum - 1) % 3;
        for (int i = 0; i < 3; i++) {
            System.arraycopy(candidates[i + temp * 3], temp1 * 3, lol, i * 3, 3);
        }
        return lol;
    }

    /**
     * Returns the number of non-zero values in a row in tiles
     *
     * @param rownum row number of row (1-9)
     * @return number of non-zero values in row
     */
    static int numbersInRow(byte[][] tiles, int rownum) {
        if (rownum < 1 || rownum > 9) {
            throw new IllegalArgumentException("NumbersInRow called with invalid arugment: " + rownum);
        }
        int temp = 0;
        for (int i = 0; i < 9; i++) {
            if (tiles[rownum - 1][i] != 0) {
                temp++;
            }
        }
        return temp;
    }

    /**
     * Returns number of non-zero values in column of tiles
     *
     * @param colnum column number (1-9)
     * @return number of non-zero values
     */
    static int numbersInColumn(byte[][] tiles, int colnum) {
        if (colnum < 1 || colnum > 9) {
            throw new IllegalArgumentException("NumbersInColumn called with invalid arugment: " + colnum);
        }
        int temp = 0;
        for (int i = 0; i < 9; i++) {
            if (tiles[i][colnum - 1] != 0) {
                temp++;
            }
        }
        return temp;
    }

    /**
     * Returns number of non-zero value in box of tiles
     *
     * @param squarenum square number to access
     * @return number of non-zero values in box of tiles
     */
    static int numbersInSquare(byte[][] tiles, int squarenum) {
        if (squarenum < 1 || squarenum > 9) {
            throw new IllegalArgumentException("NumbersInSquare called with invalid arugment: " + squarenum);
        }
        int temp = 0;
        byte[] array = findSquare(tiles, squarenum);
        for (int i = 0; i < 9; i++) {
            if (array[i] != 0) {
                temp++;
            }
        }
        return temp;
    }

    /**
     * returns array of numbers in column
     *
     * @param col column number from 1-9
     * @return byte array of contents of column
     */
    public static byte[] findColumn(byte[][] tiles, int col) {
        if (col < 1 || col > 9) {
            throw new IllegalArgumentException("findColumn called with invalid number " + col);
        }
        byte[] temp = new byte[9];
        for (int i = 0; i < 9; i++) {
            temp[i] = tiles[i][col - 1];
        }
        return temp;
    }

    /**
     * returns array of numbers in square
     *
     * @param squarenum square number (top left is 1, bottom left is 9)
     * @return byte array of contents of square
     */
    public static byte[] findSquare(byte[][] tiles, int squarenum) {
        if (squarenum < 1 || squarenum > 9) {
            throw new IllegalArgumentException("findSquare called with invalid number " + squarenum);
        }
        byte[] temp = new byte[9];
        byte temp2 = (byte) Math.floor(((double) squarenum - 1) / 3);
        byte temp3 = (byte) ((squarenum - 1) % 3);
        for (byte F = 0; F < 3; F++) {
            System.arraycopy(tiles[F + temp2 * 3], temp3 * 3, temp, F * 3, 3);
        }
        return temp;
    }

    static boolean arrayDoesNotContain(byte[] a, byte b) {
        for (byte b1 : a) {
            if (b1 == b) {
                return false;
            }
        }
        return true;
    }

    /**
     * Finds a given row number of a tile in a given square and index
     *
     * @param squarenum square number (1-9)
     * @param num       index in square (0-8)
     * @return returns row number
     */
    static int findRowNumInSquare(int squarenum, int num) {
        if (squarenum < 1 || squarenum > 9 || num < 0 || num > 8) {
            throw new IllegalArgumentException("findRowNumInSquare called with invalid arguments " + squarenum + "," + num);
        }
        int temp;
        if (squarenum < 4) {
            temp = 0;
        } else if (squarenum < 7) {
            temp = 3;
        } else {
            temp = 6;
        }
        if (Math.floorDiv(num, 3) == 0) {
            temp++;
        } else if (Math.floorDiv(num, 3) == 1) {
            temp += 2;
        } else if (Math.floorDiv(num, 3) == 2) {
            temp += 3;
        }
        return temp;
    }

    /**
     * Finds the column num of a given tile in a square and index
     *
     * @param squarenum square number (1-9)
     * @param num       index in square (0-8)
     * @return column number
     */
    static int findColumnNumInSquare(int squarenum, int num) {
        if (squarenum < 1 || squarenum > 9 || num < 0 || num > 8) {
            throw new IllegalArgumentException("findColumnNumInSquare called with invalid arguments " + squarenum + "," + num);
        }
        int temp = 0;
        if (squarenum % 3 == 2) {
            temp = 3;
        } else if (squarenum % 3 == 0) {
            temp = 6;
        }

        if ((num + 1) % 3 == 1) {
            temp++;
        } else if ((num + 1) % 3 == 2) {
            temp += 2;
        } else if ((num + 1) % 3 == 0) {
            temp += 3;
        }
        return temp;
    }

    /**
     * Finds square number of row/col
     *
     * @param row row number (1-9)
     * @param col column number (1-9)
     * @return integer of square number
     */
    public static int findSquareNum(int row, int col) {
        int colSquare = (int) Math.ceil(col / 3.0f);
        int rowSquare = (int) Math.ceil(row / 3.0f) - 1;
        return rowSquare * 3 + colSquare;
    }

    /**
     * Fills number and remove candidates
     *
     * @param num number to remove
     * @param row row of number (1-9)
     * @param col column of number (1-9)
     */
    static void fillNumber(byte[][] tiles, ArrayList<Byte>[][] candidates, byte num, int row, int col) {
        if (num < 1 || num > 9 || row < 1 || row > 9 || col < 1 || col > 9) {
            throw new IllegalArgumentException("fillNumber called with invalid row and column number " + row + "," + col);
        }
        if (tiles[row - 1][col - 1] != 0) {
            throw new IllegalArgumentException("fillNumber called on non-zero tile.");
        }
        tiles[row - 1][col - 1] = num;
        candidates[row - 1][col - 1].clear();
        removeCandidateRow(candidates, num, row);
        removeCandidateCol(candidates, num, col);
        removeCandidateBox(candidates, num, findSquareNum(row, col));
    }

    /**
     * Removes a candidate num at position row/col
     *
     * @param num number to remove
     * @param row row number (1-9)
     * @param col column number (1-9)
     */
    static void removeCandidate(ArrayList<Byte>[][] candidates, byte num, int row, int col) {
        if (num < 1 || num > 9 || row < 1 || row > 9 || col < 1 || col > 9) {
            throw new IllegalArgumentException("RemoveCandidate called with invalid row and column number " + row + "," + col);
        }
        if (candidates[row - 1][col - 1].isEmpty()) {
            if (AlgorithmLogSettings.getInstance().shouldShowCandidateNotFound()) {
                System.out.println("Candidate " + num + " did not exist at row " + row + ", column " + col);
            }
        }
        if (!candidates[row - 1][col - 1].contains(num)) {
            if (AlgorithmLogSettings.getInstance().shouldShowCandidateNotFound()) {
                System.out.println("Candidate " + num + " did not exist at row " + row + ", column " + col);
            }
        } else {
            candidates[row - 1][col - 1].remove((Byte) num);
            candidates[row - 1][col - 1].trimToSize();
            if (AlgorithmLogSettings.getInstance().shouldPrintCandidateRemoval()) {
                System.out.println("Candidate " + num + " removed from (" + row + "," + col + ")");
            }
        }
    }

    /**
     * Removes a candidate num at specific row
     *
     * @param num number to remove
     * @param row row number (1-9)
     */
    private static void removeCandidateRow(ArrayList<Byte>[][] candidates, byte num, int row) {
        if (num < 1 || num > 9 || row < 1 || row > 9) {
            throw new IllegalArgumentException("RemoveCandidateRow called with invalid row number " + row);
        }
        for (int i = 1; i < 9 + 1; i++) {
            removeCandidate(candidates, num, row, i);
        }
    }

    /**
     * Removes candidate from row, excluding column number exceptions
     *
     * @param num        number to remove
     * @param rownum     row number (1-9)
     * @param exceptions list of column number exceptions, (1-9)
     */
    static void removeCandidatesRow(ArrayList<Byte>[][] candidates, byte num, int rownum, int... exceptions) {
        if (num < 1 || num > 9) {
            throw new IllegalArgumentException("Called removeCandidatesRow with invalid param num: " + num);
        }
        if (rownum < 1 || rownum > 9) {
            throw new IllegalArgumentException("Called removeCandidatesRow with invalid param rownum: " + rownum);
        }
        for (int col = 1; col < 10; col++) {
            boolean getOut = false;
            for (int ex : exceptions) {
                if (ex == col) {
                    getOut = true;
                    break;
                }
            }
            if (!getOut) {
                removeCandidate(candidates, num, rownum, col);
            }
        }
    }

    /**
     * Removes candidate num from specific column
     *
     * @param num number to remove
     * @param col column number (1-9)
     */
    private static void removeCandidateCol(ArrayList<Byte>[][] candidates, byte num, int col) {
        if (num < 1 || num > 9 || col < 1 || col > 9) {
            throw new IllegalArgumentException("RemoveCandidateCol called with invalid column number " + col);
        }
        for (int i = 1; i < 9 + 1; i++) {
            removeCandidate(candidates, num, i, col);
        }
    }


    /**
     * Removes candidate from column, excluding row number exceptions
     *
     * @param num        number to remove
     * @param colnum     column number (1-9)
     * @param exceptions Row number exceptions (1-9)
     */
    static void removeCandidatesColumn(ArrayList<Byte>[][] candidates, byte num, int colnum, int... exceptions) {
        if (num < 1 || num > 9) {
            throw new IllegalArgumentException("Called removeCandidatesColumn with invalid param num: " + num);
        }
        if (colnum < 1 || colnum > 9) {
            throw new IllegalArgumentException("Called removeCandidatesColumn with invalid param colnum: " + colnum);
        }
        for (int row = 1; row < 10; row++) {
            boolean getOut = false;
            for (int lol : exceptions) {
                if (lol == row) {
                    getOut = true;
                    break;
                }
            }
            if (getOut) {
                continue;
            }
            removeCandidate(candidates, num, row, colnum);
        }
    }

    /**
     * Removes candidate num from specific box
     *
     * @param num       number to remove
     * @param squarenum square number of box (1-9)
     */
    private static void removeCandidateBox(ArrayList<Byte>[][] candidates, byte num, int squarenum) {
        if (num < 1 || num > 9 || squarenum < 1 || squarenum > 9) {
            throw new IllegalArgumentException("RemoveCandidateBox called with invalid square number " + squarenum);
        }
        for (int i = 0; i < 9; i++) {
            removeCandidate(candidates, num, findRowNumInSquare(squarenum, i), findColumnNumInSquare(squarenum, i));
        }
    }


    /**
     * Removes all candidates of num from square with Pair exception
     *
     * @param num        number to remove
     * @param squarenum  square number (1-9) to remove from
     * @param exceptions exceptions (i.e. numbers to leave out, 0-8)
     */
    static void removeCandidatesSquare(ArrayList<Byte>[][] candidates, byte num, int squarenum, int... exceptions) {
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
            for (int lol : exceptions) {
                if (index == lol) {
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

    static void crash(byte[][] tiles, ArrayList<Byte>[][] candidates) {
        printTiles(tiles);
        printCandidates(candidates);
        throw new RuntimeException();
    }

    public static void printTiles(byte[][] tiles) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                System.out.print(tiles[i][j]);
            }
            System.out.println();
        }
    }

    private static void printCandidates(ArrayList<Byte>[][] candidates) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                System.out.print("[");
                for (int k = 0; k < candidates[i][j].size(); k++) {
                    if (k != 0) {
                        System.out.print(", ");
                    }
                    System.out.print(candidates[i][j].get(k));
                }
                System.out.print("] ");
            }
            System.out.println();
        }
    }
}
