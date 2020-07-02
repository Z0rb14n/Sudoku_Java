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
    private final static String INPUT_FILE = "./data/input.txt";
    private final static String OUTPUT_FILE = "./data/output.txt";
    private byte[][] tiles = new byte[9][9];
    private ArrayList<Byte>[][] candidates = (ArrayList<Byte>[][]) new ArrayList[9][9];
    private boolean isComplete = false;
    private boolean isValid = true;
    private final static boolean AUTOTYPE = true;
    private final static boolean DEBUG = false;
    private static final int LOOP_LIMIT = 200;
    private long startTime;
    private long setupFinish;
    private long solveTime;
    private long solveFinish;
    private long setupTime;
    private final static int IMAGE_OFFSET = 2;
    private static final ITesseract instance = new Tesseract();
    public SolverMode mode;
    private RobotWrapper bot;
    
    public SudokuJava() {
        startTime = System.nanoTime();
        instance.setDatapath("tessdata");
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
        instance.setDatapath("tessdata");
        this.mode = new SolverMode(inputTileArray,writeToFile,hideNotFoundCandidateMsg,showCandidateRemovalMsg,hideNoBlankWereFound,speed);
        CandidateGeneration.generate(tiles, candidates);
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
        instance.setDatapath("tessdata");
        try {
            Thread.sleep(imageDelay);
        } catch (InterruptedException ex) {
            System.err.println("Interrupted.");
        }
        readTilesImage();
        CandidateGeneration.generate(tiles, candidates);
        this.setupFinish = System.nanoTime();
        this.setupTime = setupFinish - startTime;
    }

    private void run() {
        if (!isValid) return;
        final long startRunTime = System.nanoTime();
        if (mode.speed == RECURSE) {
            Recursive.solve(tiles);
        } else {
            for (int loopNum = 0; !isComplete && isValid && loopNum <= LOOP_LIMIT; loopNum++) {
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
                isComplete = Finish.isFinished(tiles);
            }
            if (!isComplete) {
                System.out.println("Sorry, either we're bad or the loop limit is too low.");
                System.out.println("Resorting to recursive solution.");
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

    private void readTilesImage() {
        BufferedImage[][] lol = new BufferedImage[9][9];
        int x = mode.getTopLeftX();
        int y = mode.getTopLeftY();
        bot.mouseMove(x, y);
        bot.delay(200);
        bot.mouseMove(x + mode.imageWidth, y);
        bot.delay(200);
        bot.mouseMove(x + mode.imageWidth, y + mode.imageHeight);
        bot.delay(200);
        bot.mouseMove(x, y + mode.imageHeight);
        int width = Math.floorDiv(mode.imageWidth, 9);
        int height = Math.floorDiv(mode.imageHeight, 9);
        BufferedImage bigBoi = bot.screenShot(x + IMAGE_OFFSET, y + IMAGE_OFFSET, mode.imageWidth - (2 * IMAGE_OFFSET), mode.imageHeight - (2 * IMAGE_OFFSET));
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                lol[i][j] = bigBoi.getSubimage((width*j),(height*i),width - (2 * IMAGE_OFFSET),height - (2 * IMAGE_OFFSET));
            }
        }
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                try {
                    String result = instance.doOCR(lol[i][j]).trim();
                    result = result.replaceAll("([\\s]+|[\\n]+|[|]+)", "");
                    if (result.isEmpty()) {
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
                    e.printStackTrace();
                }
            }
        }
    }

    private void fileSetup() {
        mode = SudokuFileParser.parse(new File(INPUT_FILE));
        if (mode == null) {
            isValid = false;
            return;
        }
        if (mode.isImage()) {
            try {
                Thread.sleep(mode.imageCaptureDelay);
                readTilesImage();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        } else {
            tiles = mode.tileArray;
        }
        CandidateGeneration.generate(tiles, candidates);
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

    /**
     * Writes to output + type values if finished
     */
    private void onFinish() {
        if (mode.doWriteToFile()) writeToFile();
        if (AUTOTYPE && mode.isImage()) typeValues();
        General.printTiles(tiles);
        long end = System.nanoTime();
        long outputTime = end - solveFinish;
        System.out.println("Completed in " + (end - startTime) / 1000000.0 + " milliseconds.");
        System.out.printf("Setup:  %010.3fms%n", setupTime / 1000000.0);
        System.out.printf("Solve:  %010.3fms%n", solveTime / 1000000.0);
        System.out.printf("Output: %010.3fms%n", outputTime / 1000000.0);
        System.exit(0);
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
        int x = mode.getTopLeftX() + halfWidth;
        int y = mode.getTopLeftY() + halfHeight;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                bot.mouseMove(x + (j * width), y + (i * height));
                bot.button1();
                bot.delay(100);
                bot.simplerType("" + tiles[i][j]);
            }
        }
    }
}
