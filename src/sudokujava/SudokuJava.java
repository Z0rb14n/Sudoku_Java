package sudokujava;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import sudokujava.SolverMode.Speed;
import sudokujava.algorithm.*;
import util.RobotWrapper;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import static sudokujava.SolverMode.Speed.*;

public class SudokuJava {
    //<editor-fold desc="global variables" defaultstate="collapsed">
    final static String INPUT_FILE = "./data/input.txt";
    private final static String OUTPUT_FILE = "./data/output.txt";
    private byte[][] tiles = new byte[9][9];
    private ArrayList<Byte>[][] candidates = (ArrayList<Byte>[][]) new ArrayList[9][9];
    private boolean isComplete = false;
    private boolean isValid = true;
    private boolean doRecurse = false;
    private final static boolean AUTOTYPE = true;
    private final static boolean DEBUG = false;
    final static Speed DEFAULT = Speed.getSpeed('2');
    private static final int LOOP_LIMIT = 200;
    private long startTime;
    private long setupFinish;
    private long solveTime;
    private long solveFinish;
    private long setupTime;
    private final static int IMAGE_OFFSET = 2;
    public SolverMode mode;
    private RobotWrapper bot;

    //</editor-fold>
    
    public SudokuJava() {
        startTime = System.nanoTime();
        setupRobot();
        fileSetup();
        setupFinish = System.nanoTime();
        setupTime = setupFinish - startTime;
    }
    
    public SudokuJava (byte[][] inputTileArray,
                       boolean writeToFile,
                       boolean hideNotFoundCandidateMsg,
                       boolean showCandidateRemovalMsg,
                       boolean hideNoBlankWereFound,
                       Speed speed) {
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
        calculateCandidates();
        this.setupFinish = System.nanoTime();
        this.setupTime = setupFinish - startTime;
    }
    
    public SudokuJava (int topLeftX,
                       int topLeftY,
                       int imageWidth,
                       int imageHeight,
                       int imageDelay) {
        this.startTime = System.nanoTime();
        setupRobot();
        this.mode = new SolverMode(topLeftX, topLeftY, imageWidth, imageHeight, imageDelay);
        try {
            Thread.sleep(imageDelay);
        } catch (InterruptedException ex) {
            System.err.println("Interrupted.");
        }
        readTilesImage();
        calculateCandidates();
        this.setupFinish = System.nanoTime();
        this.setupTime = setupFinish - startTime;
    }

    private void run() {
        final long startRunTime = System.nanoTime();
        if (!isValid) {
            return;
        }
        int loopNum = 0;
        if (doRecurse) {
            Recursive.solve(tiles);
        } else {
            while (!isComplete && isValid) {
                if (mode.speed.isGreaterThan(VERY_SLOW)) {
                    XWing.solve(tiles, candidates, mode); //double check that it works
                }
                if (mode.speed.isGreaterThan(SLOW)) {
                    HiddenPair.solve(candidates, mode); //double check that itworks
                }
                if (mode.speed.isGreaterThan(SLOW)) {
                    Omission.solve(tiles, candidates, mode);
                }
                if (mode.speed.isGreaterThan(MEDIUM)) {
                    NakedPair.solve(tiles, candidates, mode);
                }
                if (DEBUG) {
                    DebugChecks.checkValid(tiles, candidates, mode);
                }
                OpenSingle.solve(tiles, candidates, mode);
                NakedSingle.solve(tiles, candidates, mode);
                HiddenSingle.solve(tiles, candidates, mode);
                isDone();
                loopNum++;
                if (loopNum > LOOP_LIMIT) {
                    System.out.println("Sorry, either we're bad or the loop limit is too low.");
                    System.out.println("Resorting to recursive solution.");
                    break;
                }
            }
            if (!isComplete) {
                Recursive.solve(tiles);
            }
        }
        solveFinish = System.nanoTime();
        solveTime = solveFinish - startRunTime;
        onFinish();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SudokuJava sj = new SudokuJava();
        sj.run();
    }

    /**
     * Prints all the tiles
     */
    private void printTiles() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                System.out.print(tiles[i][j]);
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
    private byte[] findColumn(int col) {
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
    private byte[] findSquare(int squarenum) {
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

    private static boolean arrayContains(byte[] a, byte b) {
        for (byte b1 : a) {
            if (b1 == b) {
                return true;
            }
        }
        return false;
    }

    /**
     * Finds square number of row/col
     *
     * @param row row number (1-9)
     * @param col column number (1-9)
     * @return integer of square number
     */
    private static int findSquareNum(int row, int col) {
        int colSquare = (int) Math.ceil(col / 3.0);
        int rowSquare = (int) Math.ceil(row / 3.0) - 1;
        return rowSquare * 3 + colSquare;
    }

    //</editor-fold>
    //<editor-fold desc="Setup" defaultstate="collapsed">
    private void calculateCandidates() {
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

    private void readTilesImage() {
        ITesseract instance = new Tesseract();
        instance.setDatapath("tessdata");
        BufferedImage[][] lol = new BufferedImage[9][9];
        int x = mode.topLeft.getX();
        int y = mode.topLeft.getY();
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

    private void fileSetup() {
        mode = SudokuFileParser.parse();
        if (mode == null) {
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

    private void setupRobot() {
        try {
            bot = new RobotWrapper();
        } catch (java.awt.AWTException e) {
            System.err.println("Could not initialize robot.");
            e.printStackTrace();
            System.exit(2);
        }
    }

    //</editor-fold>
    //<editor-fold desc="On finish/test of finish" defaultstate="collapsed">
    /**
     * Writes to output + type values if finished
     */
    private void onFinish() {
        if (mode.writeToFile) {
            writeToFile();
        }
        if (AUTOTYPE && mode.isImage) {
            typeValues();
        }
        printTiles();
        long end = System.nanoTime();
        long outputTime = end - solveFinish;
        System.out.println("Completed in " + (end - startTime) / 1000000.0 + " milliseconds.");
        System.out.printf("Setup:  %010.3fms%n", setupTime / 1000000.0);
        System.out.printf("Solve:  %010.3fms%n", solveTime / 1000000.0);
        System.out.printf("Output: %010.3fms%n", outputTime / 1000000.0);
        System.exit(0);
    }

    /**
     * Checks if the program is done if it is, change isComplete to true.
     */
    private void isDone() {
        for (byte[] tile : tiles) {
            if (arrayContains(tile, (byte) 0)) {
                isComplete = false;
                return;
            }
        }
        isComplete = true;
    }

    /**
     * Writes the tiles to OUTPUT_FILE.
     */
    private void writeToFile() {
        File output = new File(OUTPUT_FILE);
        try {
            if (output.createNewFile()) {
                System.out.println("File created: " + OUTPUT_FILE);
            } else {
                System.out.println("File " + OUTPUT_FILE + " already exists.");
            }
            FileWriter writer = new FileWriter(OUTPUT_FILE);
            for (byte[] tile : tiles) {
                for (byte b : tile) {
                    writer.write("" + b);
                }
                writer.write("\n");
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Could not create or write to file " + OUTPUT_FILE);
        }
    }

    private void typeValues() {
        final int width = Math.floorDiv(mode.imageWidth, 9);
        final int halfWidth = Math.floorDiv(width, 2);
        final int height = Math.floorDiv(mode.imageHeight, 9);
        final int halfHeight = Math.floorDiv(height, 2);
        int x = mode.topLeft.getX() + halfWidth;
        int y = mode.topLeft.getY() + halfHeight;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                bot.mouseMove(x + (j * width), y + (i * height));
                bot.button1();
                bot.delay(100);
                bot.simplerType("" + tiles[i][j]);
            }
        }
    }
    //</editor-fold>
}
