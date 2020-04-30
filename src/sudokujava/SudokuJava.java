package sudokujava;

import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import util.ParsingException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Stack;
import net.sourceforge.tess4j.*;
import sudokujava.SolverMode.Speed;
import static sudokujava.SolverMode.Speed.*;
import util.Pair;
import util.Triple;
import util.JavaRobot;

/**
 *
 * @author adminasaurus
 */

public class SudokuJava {

    //<editor-fold desc="global variables" defaultstate="collapsed">
    public final static String INPUT_FILE = "src/input.txt";
    public final static String OUTPUT_FILE = "src/output.txt";
    public byte[][] tiles = new byte[9][9];
    @SuppressWarnings("unchecked")
    public ArrayList<Byte>[][] candidates = (ArrayList<Byte>[][]) new ArrayList[9][9];
    public boolean isComplete = false;
    public boolean isValid = true;
    public boolean doRecurse = false;
    public boolean recursiveSolutionFound = false;
    public final static boolean AUTOTYPE = true;
    public final static boolean DEBUG = false;
    public final static Speed DEFAULT = Speed.getSpeed('2');
    public static final int LOOP_LIMIT = 200;
    public long startTime = System.nanoTime();
    public long setupFinish = 0;
    public long solveTime = 0;
    public long solveFinish = 0;
    public long setupTime = 0;
    public final static int IMAGE_OFFSET = 2;
    public SolverMode mode;
    public JavaRobot bot = new JavaRobot();

    //</editor-fold>
    
    public SudokuJava() {
        setup();
        setupFinish = System.nanoTime();
        setupTime = setupFinish - startTime;
    }
    
    public SudokuJava (byte[][] inputTileArray,
                       boolean writeToFile,
                       boolean hideNotFoundCandidateMsg,
                       boolean showCandidateRemovalMsg,
                       boolean hideNoBlankWereFound,
                       char speed) {
        startTime = System.nanoTime();
        if (inputTileArray.length != 9)  {
            throw new IllegalArgumentException();
        }
        for (byte[] bytes : inputTileArray) {
            if (bytes.length != 9) {
                throw new IllegalArgumentException();
            }
        }
        this.mode = new SolverMode(inputTileArray,writeToFile,hideNotFoundCandidateMsg,showCandidateRemovalMsg,hideNoBlankWereFound,speed);
        this.setupFinish = System.nanoTime();
        this.setupTime = setupFinish - startTime;
    }
    
    public SudokuJava (int topLeftX,
                       int topLeftY,
                       int imageWidth,
                       int imageHeight,
                       int imageDelay) {
        this.startTime = System.nanoTime();this.setupFinish = System.nanoTime();
        this.setupTime = setupFinish - startTime;
        this.mode = new SolverMode(topLeftX,topLeftY,imageWidth,imageHeight,imageDelay);
    }
    
    public void run() {
        if (!isValid) {
            System.exit(0);
        }
        int loopNum = 0;
        if (doRecurse) {
            recursiveSolver();
        } else {
            while (!isComplete && isValid) {
                if (mode.speed.isGreaterThan(VERY_SLOW)) {
                    fillXWing(); //double check that it works
                }
                if (mode.speed.isGreaterThan(SLOW)) {
                    fillHiddenPairs(); //double check that itworks
                }
                if (mode.speed.isGreaterThan(SLOW)) {
                    fillOmissions();
                }
                if (mode.speed.isGreaterThan(MEDIUM)) {
                    fillNakedPairs();
                }
                if (DEBUG) {
                    debugCheckValid();
                }
                fillOpenSingles();
                fillNakedSingles();
                fillHiddenSingles();
                isDone();
                loopNum++;
                if (loopNum > LOOP_LIMIT) {
                    System.out.println("Sorry, either we're bad or the loop limit is too low.");
                    System.out.println("Resorting to recursive solution.");
                    break;
                }
            }
            if (!isComplete) {
                recursiveSolver();
            }
        }
        solveFinish = System.nanoTime();
        solveTime = solveFinish - setupFinish;
        onFinish();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SudokuJava sj = new SudokuJava();
        sj.run();
    }

    //<editor-fold desc="Algorithm Codes" defaultstate="collapsed">
    /**
     * Recursively solves for a solution if (no blank numbers found) - GG EZ
     * else (fill in random candidates) until we have a solution
     */
    public void recursiveSolver() {
        if (recursiveSolutionFound) return;
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (tiles[row][col] == 0) {
                    for (byte num = 1; num < 10; num++) {
                        if (isPossible(num, row + 1, col + 1)) {
                            tiles[row][col] = num;
                            recursiveSolver();
                            if (!recursiveSolutionFound) tiles[row][col] = 0;
                        }
                    }
                    return;
                }
            }
        }
        recursiveSolutionFound = true;
    }

    /**
     * Fill open singles (i.e. 8 numbers in row/col/squ)
     */
    public void fillOpenSingles() {
        boolean[] tests = new boolean[3];
        Stack<Integer> rows = new Stack<>();
        for (int i = 1; i < 10; i++) {
            if (numbersInRow(i) == 8) {
                rows.push(i);
            }
        }
        if (rows.isEmpty()) {
            tests[0] = false;
        } else {
            tests[0] = true;
            do {
                int row = rows.pop();
                boolean broken = false;
                byte number = 0;
                for (byte num = 1; num < 10; num++) {
                    if (!arrayContains(tiles[row - 1], num)) {
                        number = num;
                        broken = true;
                        break;
                    }
                }
                if (!broken) {
                    throw new IllegalArgumentException("AAAAAAAAAAAAAAA");
                }
                for (int i = 0; i < 9; i++) {
                    if (tiles[row - 1][i] == 0) {
                        System.out.println("Filled Open Single " + number + ", row " + row + ", column " + (i + 1));
                        fillNumber(number, row, i + 1);
                        break;
                    }
                }
            } while (!rows.isEmpty());
        }
        Stack<Integer> cols = new Stack<>();
        for (int i = 1; i < 10; i++) {
            if (numbersInColumn(i) == 8) {
                cols.push(i);
            }
        }
        if (cols.isEmpty()) {
            tests[1] = false;
        } else {
            tests[1] = true;
            do {
                int col = cols.pop();
                boolean broken = false;
                byte number = 0;
                byte[] thing = findColumn(col);
                for (byte num = 1; num < 10; num++) {
                    if (!arrayContains(thing, num)) {
                        number = num;
                        broken = true;
                        break;
                    }
                }
                if (!broken) {
                    throw new IllegalArgumentException("AAAAAAAAAAAAAAA");
                }
                for (int i = 0; i < 9; i++) {
                    if (tiles[i][col - 1] == 0) {
                        System.out.println("Filled Open Single " + number + ", row " + (i + 1) + ", column " + col);
                        fillNumber(number, i + 1, col);
                        break;
                    }
                }
            } while (!cols.isEmpty());
        }
        Stack<Integer> boxs = new Stack<>();
        for (int i = 1; i < 10; i++) {
            if (numbersInSquare(i) == 8) {
                boxs.push(i);
            }
        }
        if (boxs.isEmpty()) {
            tests[2] = false;
        } else {
            tests[2] = true;
            do {
                int box = boxs.pop();
                boolean broken = false;
                byte number = 0;
                byte[] thing = findSquare(box);
                for (byte num = 1; num < 10; num++) {
                    if (!arrayContains(thing, num)) {
                        number = num;
                        broken = true;
                        break;
                    }
                }
                if (!broken) {
                    throw new IllegalArgumentException("AAAAAAAAAAAAAAA");
                }
                for (int i = 0; i < 9; i++) {
                    if (tiles[findRowNumInSquare(box, i) - 1][findColumnNumInSquare(box, i) - 1] == 0) {
                        int row = findRowNumInSquare(box, i);
                        int col = findColumnNumInSquare(box, i);
                        System.out.println("Filled Open Single " + number + ", row " + row + ", column " + col);
                        fillNumber(number, row, col);
                        break;
                    }
                }
            } while (!cols.isEmpty());
        }
        if (!tests[0] && !tests[1] && !tests[2]) {
            if (!mode.hideNoBlankWereFound) {
                System.out.println("No Open Singles were found.");
            }
        }
    }

    /**
     * Fill Naked Singles (i.e. only 1 Candidate)
     */
    public void fillNakedSingles() {
        Stack<Pair> numbers = new Stack<>();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (candidates[i][j].size() == 1) {
                    numbers.push(new Pair(i + 1, j + 1));
                }
            }
        }
        if (numbers.isEmpty()) {
            if (!mode.hideNoBlankWereFound) {
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
            byte number = -69;
            try {
                number = candidates[row - 1][column - 1].get(0);
                fillNumber(number, row, column);
                System.out.println("Naked Single " + number + " at " + row + "," + column);
            } catch (IndexOutOfBoundsException e) {
                AAAAAAAAAAAAAA();
            }
        }
    }

    /**
     * The only candidate number in row/col/box
     */
    public void fillHiddenSingles() {
        Stack<Triple> nums = new Stack<>();
        for (int row = 0; row < 9; row++) {
            ArrayList<Byte> thing = concatCandidates(candidates[row]);
            for (byte num = 1; num < 10; num++) {
                if (thing.indexOf(num) != -1 && thing.indexOf(num) == thing.lastIndexOf(num)) {
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
            ArrayList<Byte> thing = concatCandidates(candidatesColumn(column + 1));
            for (byte num = 1; num < 10; num++) {
                if (thing.indexOf(num) != -1 && thing.indexOf(num) == thing.lastIndexOf(num)) {
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
            ArrayList<Byte> thing = concatCandidates(candidatesSquare(box));
            for (byte num = 1; num < 10; num++) {
                if (thing.indexOf(num) != -1 && thing.indexOf(num) == thing.lastIndexOf(num)) {
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
            if (!mode.hideNoBlankWereFound) {
                System.out.println("No Hidden Singles were found.");
            }
            return;
        }
        while (!nums.isEmpty()) {
            Triple lol = nums.pop();
            byte number = lol.getNum();
            int row = lol.getX();
            int col = lol.getY();
            fillNumber(number, row, col);
            System.out.println("Filled Hidden Single " + number + " at " + row + "," + col);
        }
    }

    /**
     * Two tiles having the same candidates (pairs of candidates)
     */
    public void fillNakedPairs() {
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
            removeCandidatesRow((byte) correct.getX(), row, cols.get(index1) + 1, cols.get(index2) + 1);
            removeCandidatesRow((byte) correct.getY(), row, cols.get(index1) + 1, cols.get(index2) + 1);
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
                AAAAAAAAAAAAAA();
            }
            System.out.println("Naked Pair " + correct.getX() + "," + correct.getY() + " found at column " + column + ", rows " + (rows.get(index1) + 1) + "," + (rows.get(index2) + 1));
            removeCandidatesColumn((byte) correct.getX(), column, rows.get(index1) + 1, rows.get(index2) + 1);
            removeCandidatesColumn((byte) correct.getY(), column, rows.get(index1) + 1, rows.get(index2) + 1);
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
                AAAAAAAAAAAAAA();
            }
            System.out.println("Naked Pair " + correct.getX() + "," + correct.getY() + " found at square " + square + ", indexes " + indexes.get(index1) + "," + indexes.get(index2));
            removeCandidatesSquare((byte) correct.getX(), square, indexes.get(index1), indexes.get(index2));
            removeCandidatesSquare((byte) correct.getY(), square, indexes.get(index1), indexes.get(index2));
        }
        if (!(test[0] || test[1] || test[2]) && !mode.hideNoBlankWereFound) {
            System.out.println("No Naked Pairs were found.");
        }
    }

    /**
     * essentially, if candidates are concentrated in same block in same row,
     * clear row. Same with columns. Conversely, if the row is concentrated in
     * one block, clear the block.
     */
    public void fillOmissions() {
        boolean[] test = new boolean[3];
        //perspective of row
        for (int row = 1; row < 10; row++) {
            if (numbersInRow(row) > 7) {
                continue;
            }
            ArrayList<Byte> lol = concatCandidates(candidates[row - 1]);
            byte[] count = new byte[9];
            for (Byte c : lol) {
                count[c - 1]++;
            }
            for (byte num = 1; num < 10; num++) {
                if (count[num - 1] != 2 && count[num - 1] != 3) {
                    continue;
                }
                ArrayList<Integer> columns = new ArrayList<>();
                for (int column = 1; column < 10; column++) {
                    if (candidates[row - 1][column - 1].contains(num)) {
                        columns.add(column);
                    }
                }
                if (columns.size() != count[num - 1]) {
                    throw new IllegalArgumentException("AAAAAAAAAAAAA");
                }
                if (findSquareNum(row, columns.get(0)) != findSquareNum(row, columns.get(columns.size() - 1))) {
                    continue;
                }
                test[0] = true;
                System.out.println("Row Omission " + num + " at row " + row + ", columns " + columns.get(0) + "-" + columns.get(columns.size() - 1) + ".");
                ArrayList<Pair> kek = new ArrayList<>();
                for (int lolmao : columns) {
                    kek.add(new Pair(row, lolmao));
                }
                removeCandidatesSquare(num, findSquareNum(row, columns.get(0)), kek);
            }
        }
        for (int column = 1; column < 10; column++) {
            if (numbersInColumn(column) > 7) {
                continue;
            }
            ArrayList<Byte> lol = concatCandidates(candidatesColumn(column));
            byte[] count = new byte[9];
            for (Byte c : lol) {
                count[c - 1]++;
            }
            for (byte num = 1; num < 10; num++) {
                if (count[num - 1] < 2 || count[num - 1] > 3) {
                    continue;
                }
                ArrayList<Integer> rows = new ArrayList<>();
                for (int row = 1; row < 10; row++) {
                    if (candidates[row - 1][column - 1].contains(num)) {
                        rows.add(row);
                    }
                }
                if (rows.size() != count[num - 1]) {
                    throw new IllegalArgumentException("AAAAAAAAAAAAA");
                }
                if ((int) Math.ceil((float) rows.get(0) / 3) != (int) Math.ceil((float) rows.get(rows.size() - 1) / 3)) {
                    continue;
                }
                test[1] = true;
                System.out.println("Column Omission " + num + " at column " + column + ", rows " + rows.get(0) + "-" + rows.get(rows.size() - 1) + ".");
                ArrayList<Pair> kek = new ArrayList<Pair>();
                for (int lolmao : rows) {
                    kek.add(new Pair(lolmao, column));
                }
                removeCandidatesSquare(num, findSquareNum(rows.get(0), column), kek);
            }
        }
        for (int squarenum = 1; squarenum < 10; squarenum++) {
            if (numbersInSquare(squarenum) > 7) {
                continue;
            }
            ArrayList<Byte> lol = concatCandidates(candidatesSquare(squarenum));
            byte[] count = new byte[9];
            for (Byte c : lol) {
                count[c - 1]++;
            }
            for (byte num = 1; num < 10; num++) {
                if (count[num - 1] < 2 || count[num - 1] > 3) {
                    continue;
                }
                ArrayList<Integer> indexes = new ArrayList<>();
                for (int index = 0; index < 9; index++) {
                    if (candidates[findRowNumInSquare(squarenum, index) - 1][findColumnNumInSquare(squarenum, index) - 1].contains(num)) {
                        indexes.add(index);
                    }
                }
                if (indexes.size() != count[num - 1]) {
                    throw new IllegalArgumentException("AAAAAAAAAAAAA");
                }
                boolean rowR = false;
                if (Math.floor((float) indexes.get(indexes.size() - 1) / 3) == Math.floor((float) indexes.get(0) / 3)) {
                    test[2] = true;
                    rowR = true;
                    int rowNum = findRowNumInSquare(squarenum, indexes.get(0));
                    int columnMin = findColumnNumInSquare(squarenum, indexes.get(0));
                    int columnMax = findColumnNumInSquare(squarenum, indexes.get(indexes.size() - 1));
                    System.out.println("Block/Row Omission " + num + " in block " + squarenum + ", row " + rowNum + ", columns " + columnMin + "-" + columnMax + ".");
                    for (int col = 1; col < 10; col++) {
                        if (col >= columnMin && col <= columnMax) {
                            continue;
                        }
                        removeCandidate(num, rowNum, col);
                    }
                }
                if (rowR) {
                    continue;
                }
                if (indexes.get(indexes.size() - 1) % 3 == indexes.get(0) % 3) {
                    if (indexes.size() == 3 && indexes.get(1) != indexes.get(0)) {
                        continue;
                    }
                    test[2] = true;
                    int colNum = findColumnNumInSquare(squarenum, indexes.get(0));
                    int rowMin = findRowNumInSquare(squarenum, indexes.get(0));
                    int rowMax = findRowNumInSquare(squarenum, indexes.get(indexes.size() - 1));
                    System.out.println("Column Omission " + num + " in block " + squarenum + ", column " + colNum + ", rows " + rowMin + "-" + rowMax + ".");
                    for (int row = 1; row < 10; row++) {
                        if (row >= rowMin && row <= rowMax) {
                            continue;
                        }
                        removeCandidate(num, row, colNum);
                    }
                }
            }
        }
        if (!test[0] && !test[1] && !test[2] && !mode.hideNoBlankWereFound) {
            System.out.println("No omissions were found.");
        }
    }

    /**
     * Only pairs of candidates in row/col/block
     */
    public void fillHiddenPairs() {
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
                        removeCandidateExcept(new byte[]{nums.get(i), nums.get(j)}, row, kek[i][0] + 1);
                        removeCandidateExcept(new byte[]{nums.get(i), nums.get(j)}, row, kek[i][1] + 1);
                    }
                }
            }
        }
        //column
        for (int col = 1; col < 10; col++) {
            ArrayList<Byte> lol = concatCandidates(candidatesColumn(col));
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
                        removeCandidateExcept(new byte[]{nums.get(i), nums.get(j)}, kek[i][0] + 1, col);
                        removeCandidateExcept(new byte[]{nums.get(i), nums.get(j)}, kek[i][1] + 1, col);
                    }
                }
            }
        }
        for (int sq = 1; sq < 10; sq++) {
            ArrayList<Byte> lol = concatCandidates(candidatesSquare(sq));
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
                        removeCandidateExcept(new byte[]{nums.get(i), nums.get(j)}, findRowNumInSquare(sq, kek[i][0]), findColumnNumInSquare(sq, kek[i][0]));
                        removeCandidateExcept(new byte[]{nums.get(i), nums.get(j)}, findRowNumInSquare(sq, kek[i][1]), findColumnNumInSquare(sq, kek[i][1]));
                    }
                }
            }
        }
        if (!test[0] && !test[1] && !test[2] && !mode.hideNoBlankWereFound) {
            System.out.println("No Hidden Pairs were found.");
        }
    }

    /**
     * X-Wing solver - essentially pairs in rows, kill everything in column
     */
    public void fillXWing() {
        boolean rowT = false;
        boolean colT = false;
        for (int row = 1; row < 10; row++) {
            if (numbersInRow(row) > 7) {
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
                    if (numbersInRow(row2) > 7) {
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
                    removeCandidatesColumn(num, col1[0], row, row2);
                    removeCandidatesColumn(num, col1[1], row, row2);
                }
            }
        }
        for (int col = 1; col < 10; col++) {
            if (numbersInColumn(col) > 7) {
                continue;
            }
            ArrayList<Byte> thing = concatCandidates(candidatesColumn(col));
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
                    if (numbersInRow(col2) > 7) {
                        continue;
                    }
                    ArrayList<Byte> cat = concatCandidates(candidatesColumn(col2));
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
                    removeCandidatesRow(num, row1[0], col, col2);
                    removeCandidatesRow(num, row1[1], col, col2);
                }
            }
        }
        if (!rowT && !colT && !mode.hideNoBlankWereFound) {
            System.out.println("No X-Wings were found.");
        }
    }

    //</editor-fold>
    //<editor-fold desc="Debug stuffs" defaultstate="collapsed">
    /**
     * Advanced check valid - does not check whether number exists in block
     */
    public void debugCheckValid() {
        checkValid();
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                if (tiles[i][j] == 0) {
                    continue;
                }
                //check column
                byte num = tiles[i][j];
                for (int row = 0; row < 9; row++) {
                    if (row == i) {
                        continue;
                    }
                    if (tiles[row][j] == num) {
                        AAAAAAAAAAAAAA();
                    }
                }
                for (int col = 0; col < 9; col++) {
                    if (col == j) {
                        continue;
                    }
                    if (tiles[i][col] == num) {
                        AAAAAAAAAAAAAA();
                    }
                }
            }
        }
    }

    /**
     * Checks whether the puzzle is still valid, and if it isn't, DIE!
     */
    public void checkValid() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (tiles[i][j] == 0 && candidates[i][j].isEmpty()) {
                    AAAAAAAAAAAAAA();
                }
            }
        }
    }

    /**
     * Crash and burn!
     */
    public void AAAAAAAAAAAAAA() {
        printTiles();
        printCandidates();
        throw new IllegalArgumentException("AAAAAAAAAAAAAA");
    }

    //</editor-fold>
    //<editor-fold desc="Convenience Code" defaultstate="collapsed">
    /**
     * Returns whether a number could potentially fill in the slot
     *
     * @param num - number
     * @param rownum - row number (1-9)
     * @param colnum - column number (1-9)
     * @return true if it is a candidate, false if not
     */
    public boolean isPossible(byte num, int rownum, int colnum) {
        if (tiles[rownum - 1][colnum - 1] != 0) {
            return false;
        }
        byte[] col = findColumn(colnum);
        byte[] row = tiles[rownum - 1];
        byte[] square = findSquare(findSquareNum(rownum, colnum));
        return (!arrayContains(row, num) && !arrayContains(col, num) && !arrayContains(square, num));
    }

    /**
     * Remove all candidates except num
     *
     * @param num numbers exceptions
     * @param rownum rownumber (1-9)
     * @param colnum colnumber (1-9)
     */
    public void removeCandidateExcept(byte[] num, int rownum, int colnum) {
        ArrayList<Byte> lel = new ArrayList<>(candidates[rownum - 1][colnum - 1]);
        for (Byte lol : lel) {
            boolean getOut = false;
            for (Byte kek : num) {
                if (kek == lol) {
                    getOut = true;
                    break;
                }
            }
            if (getOut) {
                continue;
            }
            removeCandidate(lol, rownum, colnum);
        }
    }

    /**
     * Remove all candidates except num
     *
     * @param num numbers exceptions
     * @param rownum rownumber (1-9)
     * @param colnum colnumber (1-9)
     */
    public void removeCandidateExcept(ArrayList<Byte> num, int rownum, int colnum) {
        for (Byte lol : candidates[rownum - 1][colnum - 1]) {
            boolean getOut = false;
            for (Byte kek : num) {
                if (Objects.equals(kek, lol)) {
                    getOut = true;
                    break;
                }
            }
            if (getOut) {
                continue;
            }
            removeCandidate(lol, rownum, colnum);
        }
    }

    /**
     * Removes candidate from row, excluding column number exceptions
     *
     * @param num number to remove
     * @param rownum row number (1-9)
     * @param exceptions list of column number exceptions, (1-9)
     */
    public void removeCandidatesRow(byte num, int rownum, int... exceptions) {
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
            if (getOut) {
                continue;
            } else {
                removeCandidate(num, rownum, col);
            }
        }
    }

    /**
     * Removes candidate from row, excluding column number exceptions
     *
     * @param num number to remove
     * @param rownum row number (1-9)
     * @param exceptions List of column number to not remove from (1-9)
     */
    public void removeCandidatesRow(byte num, int rownum, ArrayList<Integer> exceptions) {
        if (num < 1 || num > 9) {
            throw new IllegalArgumentException("Called removeCandidatesRow with invalid param num: " + num);
        }
        if (rownum < 1 || rownum > 9) {
            throw new IllegalArgumentException("Called removeCandidatesRow with invalid param rownum: " + rownum);
        }
        for (int col = 1; col < 10; col++) {
            boolean getOut = false;
            if (!exceptions.contains(col)) {
                removeCandidate(num, rownum, col);
            }
        }
    }

    /**
     * Removes candidate from column, excluding row number exceptions
     *
     * @param num number to remove
     * @param colnum column number (1-9)
     * @param exceptions Row number exceptions (1-9)
     */
    public void removeCandidatesColumn(byte num, int colnum, int... exceptions) {
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
            removeCandidate(num, row, colnum);
        }
    }

    /**
     * Removes candidate from column, excluding row number exceptions
     *
     * @param num number to remove
     * @param colnum column number (1-9)
     * @param exceptions Row number exceptions (1-9)
     */
    public void removeCandidatesColumn(byte num, int colnum, ArrayList<Integer> exceptions) {
        if (num < 1 || num > 9) {
            throw new IllegalArgumentException("Called removeCandidatesColumn with invalid param num: " + num);
        }
        if (colnum < 1 || colnum > 9) {
            throw new IllegalArgumentException("Called removeCandidatesColumn with invalid param colnum: " + colnum);
        }
        for (int row = 1; row < 10; row++) {
            boolean getOut = false;
            if (exceptions.contains(row)) {
                continue;
            }
            removeCandidate(num, row, colnum);
        }
    }

    /**
     * Removes all candidates of num from square with Pair exception
     *
     * @param num number to remove
     * @param squarenum square number (1-9) to remove from
     * @param exceptions exceptions (i.e. numbers to leave out)
     */
    public void removeCandidatesSquare(byte num, int squarenum, Pair... exceptions) {
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
            removeCandidate(num, row, column);
        }
    }

    /**
     * Removes all candidates of num from square with Pair exception
     *
     * @param num number to remove
     * @param squarenum square number (1-9) to remove from
     * @param exceptions exceptions (i.e. numbers to leave out)
     */
    public void removeCandidatesSquare(byte num, int squarenum, ArrayList<Pair> exceptions) {
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
            removeCandidate(num, row, column);
        }
    }

    /**
     * Removes all candidates of num from square with Pair exception
     *
     * @param num number to remove
     * @param squarenum square number (1-9) to remove from
     * @param exceptions exceptions (i.e. numbers to leave out, 0-8)
     */
    public void removeCandidatesSquare(byte num, int squarenum, int... exceptions) {
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
            removeCandidate(num, row, column);
        }
    }

    /**
     * Prints all the tiles
     */
    public void printTiles() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                System.out.print(tiles[i][j]);
            }
            System.out.println();
        }
    }

    /**
     * Concatenates all the Arrays of ArrayLists of candidates (used in Hidden
     * Singles)
     *
     * @param a array of list of candidates
     * @return
     */
    public static ArrayList<Byte> concatCandidates(ArrayList<Byte>[] a) {
        ArrayList<Byte> temp = new ArrayList<>();
        for (ArrayList<Byte> a1 : a) {
            temp.addAll(a1);
        }
        return temp;
    }

    /**
     * Concatenates all the Arrays of ArrayLists of candidates (used in Hidden
     * Singles)
     *
     * @param a array of list of candidates
     * @return
     */
    public static ArrayList<Byte> concatCandidates(ArrayList<ArrayList<Byte>> a) {
        ArrayList<Byte> temp = new ArrayList<>();
        for (ArrayList<Byte> a1 : a) {
            temp.addAll(a1);
        }
        return temp;
    }

    /**
     * Prints all the candidates
     */
    public void printCandidates() {
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

    /**
     * returns array of numbers in column
     *
     * @param col column number from 1-9
     * @return byte array of contents of column
     */
    public byte[] findColumn(int col) {
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
    public byte[] findSquare(int squarenum) {
        if (squarenum < 1 || squarenum > 9) {
            throw new IllegalArgumentException("findSquare called with invalid number " + squarenum);
        }
        byte[] temp = new byte[9];
        byte temp2 = (byte) Math.floor(((double) squarenum - 1) / 3);
        byte temp3 = (byte) ((squarenum - 1) % 3);
        for (byte F = 0; F < 3; F++) {
            for (byte L = 0; L < 3; L++) {
                temp[F * 3 + L] = tiles[F + temp2 * 3][L + temp3 * 3];
            }
        }
        return temp;
    }

    public static boolean arrayContains(byte[] a, byte b) {
        for (int i = 0; i < a.length; i++) {
            if (a[i] == b) {
                return true;
            }
        }
        return Arrays.asList(a).contains(b);
    }

    /**
     * Finds a given row number of a tile in a given square and index
     *
     * @param squarenum square number (1-9)
     * @param num index in square (0-8)
     * @return returns row number
     */
    public static int findRowNumInSquare(int squarenum, int num) {
        if (squarenum < 1 || squarenum > 9 || num < 0 || num > 8) {
            throw new IllegalArgumentException("findRowNumInSquare called with invalid arguments " + squarenum + "," + num);
        }
        int temp = 0;
        if (squarenum < 4) {
            temp = 0;
        } else if (squarenum < 7) {
            temp = 3;
        } else if (squarenum < 10) {
            temp = 6;
        }
        if (Math.floor(num / 3) == 0) {
            temp++;
        } else if (Math.floor(num / 3) == 1) {
            temp += 2;
        } else if (Math.floor(num / 3) == 2) {
            temp += 3;
        }
        return temp;
    }

    /**
     * Finds the column num of a given tile in a square and index
     *
     * @param squarenum square number (1-9)
     * @param num index in square (0-8)
     * @return column number
     */
    public static int findColumnNumInSquare(int squarenum, int num) {
        if (squarenum < 1 || squarenum > 9 || num < 0 || num > 8) {
            throw new IllegalArgumentException("findColumnNumInSquare called with invalid arguments " + squarenum + "," + num);
        }
        int temp = 0;
        if (squarenum % 3 == 1); else if (squarenum % 3 == 2) {
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
     * Returns the array of arraylists of candidates in a given square
     *
     * @param squarenum square num to access
     * @return array of ArrayList of candidates.
     */
    public ArrayList<Byte>[] candidatesSquare(int squarenum) {
        if (squarenum < 1 || squarenum > 9) {
            throw new IllegalArgumentException("candidatesSquare called with invalid arugment: " + squarenum);
        }
        ArrayList<Byte>[] lol = (ArrayList<Byte>[]) new ArrayList[9];
        int temp = (int) Math.floor(((float) (squarenum - 1)) / 3);
        int temp1 = (squarenum - 1) % 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                lol[i * 3 + j] = candidates[i + temp * 3][j + temp1 * 3];
            }
        }
        return lol;
    }

    /**
     * Returns the array of arraylists of candidates in a given column.
     *
     * @param colnum column number to access
     * @return array of ArrayList of candidates.
     */
    public ArrayList<Byte>[] candidatesColumn(int colnum) {
        if (colnum < 1 || colnum > 9) {
            throw new IllegalArgumentException("candidatesColumn called with invalid arugment: " + colnum);
        }
        ArrayList<Byte>[] lol = (ArrayList<Byte>[]) new ArrayList[9];
        for (int i = 0; i < 9; i++) {
            lol[i] = candidates[i][colnum - 1];
            lol[i].trimToSize();
        }
        return lol;
    }

    /**
     * Returns the number of non-zero values in a row in tiles
     *
     * @param rownum row number of row (1-9)
     * @return number of non-zero values in row
     */
    public int numbersInRow(int rownum) {
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
    public int numbersInColumn(int colnum) {
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
    public int numbersInSquare(int squarenum) {
        if (squarenum < 1 || squarenum > 9) {
            throw new IllegalArgumentException("NumbersInSquare called with invalid arugment: " + squarenum);
        }
        int temp = 0;
        byte[] array = findSquare(squarenum);
        for (int i = 0; i < 9; i++) {
            if (array[i] != 0) {
                temp++;
            }
        }
        return temp;
    }

    /**
     * Removes a candidate num at position row/col
     *
     * @param num number to remove
     * @param row row number (1-9)
     * @param col column number (1-9)
     */
    public void removeCandidate(byte num, int row, int col) {
        if (num < 1 || num > 9 || row < 1 || row > 9 || col < 1 || col > 9) {
            throw new IllegalArgumentException("RemoveCandidate called with invalid row and column number " + row + "," + col);
        }
        if (candidates[row - 1][col - 1].size() == 0) {
            
            if (!mode.hideNotFoundCandidateMsg) {
                System.out.println("Candidate " + num + " did not exist at row " + row + ", column " + col);
            }
        }
        if (!candidates[row - 1][col - 1].contains(num)) {
            if (!mode.hideNotFoundCandidateMsg) {
                System.out.println("Candidate " + num + " did not exist at row " + row + ", column " + col);
            }
        } else {
            candidates[row - 1][col - 1].remove((Byte) num);
            candidates[row - 1][col - 1].trimToSize();
            if (mode.showCandidateRemovalMsg) {
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
    public void removeCandidateRow(byte num, int row) {
        if (num < 1 || num > 9 || row < 1 || row > 9) {
            throw new IllegalArgumentException("RemoveCandidateRow called with invalid row number " + row);
        }
        for (int i = 1; i < 9 + 1; i++) {
            removeCandidate(num, row, i);
        }
    }

    /**
     * Removes candidate num from specific column
     *
     * @param num number to remove
     * @param col column number (1-9)
     */
    public void removeCandidateCol(byte num, int col) {
        if (num < 1 || num > 9 || col < 1 || col > 9) {
            throw new IllegalArgumentException("RemoveCandidateCol called with invalid column number " + col);
        }
        for (int i = 1; i < 9 + 1; i++) {
            removeCandidate(num, i, col);
        }
    }

    /**
     * Removes candidate num from specific box
     *
     * @param num number to remove
     * @param squarenum square number of box (1-9)
     */
    public void removeCandidateBox(byte num, int squarenum) {
        if (num < 1 || num > 9 || squarenum < 1 || squarenum > 9) {
            throw new IllegalArgumentException("RemoveCandidateBox called with invalid square number " + squarenum);
        }
        for (int i = 0; i < 9; i++) {
            removeCandidate(num, findRowNumInSquare(squarenum, i), findColumnNumInSquare(squarenum, i));
        }
    }

    /**
     * Fills number and remove candidates
     *
     * @param num number to remove
     * @param row row of number (1-9)
     * @param col column of number (1-9)
     */
    public void fillNumber(byte num, int row, int col) {
        if (num < 1 || num > 9 || row < 1 || row > 9 || col < 1 || col > 9) {
            throw new IllegalArgumentException("fillNumber called with invalid row and column number " + row + "," + col);
        }
        if (tiles[row - 1][col - 1] != 0) {
            throw new IllegalArgumentException("fillNumber called on non-zero tile.");
        }
        tiles[row - 1][col - 1] = num;
        candidates[row - 1][col - 1].clear();
        removeCandidateRow(num, row);
        removeCandidateCol(num, col);
        removeCandidateBox(num, findSquareNum(row, col));
    }

    /**
     * Finds square number of row/col
     *
     * @param row row number (1-9)
     * @param col column number (1-9)
     * @return integer of square number
     */
    public static int findSquareNum(int row, int col) {
        if (row < 4 && col < 4) {
            return 1;
        } else if (row < 4 && col < 7 && 3 < col) {
            return 2;
        } else if (row < 4 && 6 < col) {
            return 3;
        } else if (row < 7 && 3 < row && col < 4) {
            return 4;
        } else if (row < 7 && 3 < row && col < 7 && 3 < col) {
            return 5;
        } else if (row < 7 && 3 < row && 6 < col) {
            return 6;
        } else if (row > 6 && col < 4) {
            return 7;
        } else if (row > 6 && col < 7 && 3 < col) {
            return 8;
        } else if (row > 6 && 6 < col) {
            return 9;
        } else {
            throw new IllegalArgumentException("findSquareNum called with invalid argument " + row + "," + col);
        }
    }

    //</editor-fold>
    //<editor-fold desc="Setup" defaultstate="collapsed">
    public void calculateCandidates() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                ArrayList<Byte> temp = new ArrayList<>();
                if (tiles[i][j] != 0) {
                    candidates[i][j] = new ArrayList<>(0);
                    continue;
                }
                byte[] row = tiles[i];
                byte[] col = findColumn(j + 1);
                byte[] square = findSquare(findSquareNum(i + 1, j + 1));
                for (byte kek = 1; kek < 10; kek++) {
                    if (!arrayContains(row, kek) && !arrayContains(col, kek) && !arrayContains(square, kek)) {
                        temp.add(kek);
                    }
                }
                candidates[i][j] = temp;
            }
        }
    }

    public void readTilesImage() {
        ITesseract instance = new Tesseract();
        instance.setDatapath("tessdata");
        BufferedImage[][] lol = new BufferedImage[9][9];
        int x = (int) mode.topLeft.getX();
        int y = (int) mode.topLeft.getY();
        bot.mouseMove(x, y);
        bot.delay(200);
        bot.mouseMove(x + mode.imageWidth, y);
        bot.delay(200);
        bot.mouseMove(x + mode.imageWidth, y + mode.imageHeight);
        bot.delay(200);
        bot.mouseMove(x, y + mode.imageHeight);
        int width = (int) Math.floor(((float) mode.imageWidth) / 9);
        int height = (int) Math.floor(((float) mode.imageHeight) / 9);
        BufferedImage bigBoi = bot.screenShot(x + IMAGE_OFFSET, y + IMAGE_OFFSET, mode.imageWidth - (2 * IMAGE_OFFSET), mode.imageHeight - (2 * IMAGE_OFFSET));
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                // lol[i][j] = bot.screenShot(x + (width * j) + IMAGE_OFFSET, y + (height * i) + IMAGE_OFFSET, width - (2 * IMAGE_OFFSET), height - (2 * IMAGE_OFFSET));
                lol[i][j] = bigBoi.getSubimage((width*j),(height*i),width - (2 * IMAGE_OFFSET),height - (2 * IMAGE_OFFSET));
            }
        }
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                try {
                    String result = instance.doOCR(lol[i][j]);
                    result = result.replaceAll("[\\s]+", "");
                    result = result.replaceAll("[\\n]+", "");
                    result = result.replaceAll("[|]+", "");
                    if (result.compareToIgnoreCase("") == 0) {
                        System.out.print("Blank char.");
                        tiles[i][j] = 0;
                        continue;
                    }
                    try {
                        tiles[i][j] = Byte.parseByte(result);
                    } catch (NumberFormatException e) {
                        isValid = false;
                        System.out.println("Invalid characters in OCR: " + result + ", length: " + result.length());
                        Toolkit.getDefaultToolkit().beep();
                        System.exit(-1);
                    }
                    System.out.println(result);
                } catch (TesseractException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }
    
    public void setup() {
        startTime = System.nanoTime();
        try {
            mode = SudokuFileParser.parse();
        } catch (ParsingException e) {
            isValid = false;
            return;
        }
        if (mode.isImage) {
            try {
                Thread.sleep(mode.imageCaptureDelay);
                readTilesImage();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        } else {
            tiles = mode.tileArray;
        }
        calculateCandidates();
    }

    //</editor-fold>
    //<editor-fold desc="On finish/test of finish" defaultstate="collapsed">
    /**
     * Writes to output + type values if finished
     */
    public void onFinish() {
        if (mode.writeToFile) {
            writeToFile();
        }
        if (AUTOTYPE && mode.isImage) {
            typeValues();
        }
        printTiles();
        long end = System.nanoTime();
        long outputTime = end - solveFinish;
        System.out.println("Completed in " + ((double) (end - startTime)) / 1000000 + " milliseconds.");
        System.out.printf("Setup:  %010.3fms%n", setupTime / 1000000.0);
        System.out.printf("Solve:  %010.3fms%n", solveTime / 1000000.0);
        System.out.printf("Output: %010.3fms%n", outputTime / 1000000.0);
        System.exit(0);
    }

    /**
     * Checks if the program is done if it is, change isComplete to true.
     */
    public void isDone() {
        for (int i = 0; i < tiles.length; i++) {
            if (arrayContains(tiles[i], (byte) 0)) {
                isComplete = false;
                return;
            }
        }
        isComplete = true;
    }

    /**
     * Writes the tiles to OUTPUT_FILE.
     */
    public void writeToFile() {
        File output = new File(OUTPUT_FILE);
        try {
            if (output.createNewFile()) {
                System.out.println("File created: " + OUTPUT_FILE);
            } else {
                System.out.println("File " + OUTPUT_FILE + " already exists.");
            }
            FileWriter writer = new FileWriter(OUTPUT_FILE);
            for (int i = 0; i < tiles.length; i++) {
                for (int j = 0; j < tiles[i].length; j++) {
                    writer.write("" + tiles[i][j]);
                }
                writer.write("\n");
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Could not create or write to file " + OUTPUT_FILE);
        }
    }

    public void typeValues() {
        int width = (int) Math.floor(((float) mode.imageWidth) / 9);
        int half_width = (int) Math.floor(((float) width) / 2);
        int height = (int) Math.floor(((float) mode.imageHeight) / 9);
        int half_height = (int) Math.floor(((float) height) / 2);
        int x = (int) mode.topLeft.getX() + half_width;
        int y = (int) mode.topLeft.getY() + half_height;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                bot.clickAt(x + (j * width), y + (i * height), false);
                bot.delay(100);
                bot.simplerType(tiles[i][j]);
            }
        }
    }
    //</editor-fold>
}
