package sudokujava;

import sudokujava.SolverMode.Speed;
import sudokujava.algorithm.*;
import util.RobotWrapper;
import util.SudokuScreenIO;

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
    private final static boolean AUTOTYPE_DEFAULT = true;
    private final static boolean DEBUG = false;
    private static final int LOOP_LIMIT = 200;
    private long startTime;
    private long setupFinish;
    private long solveTime;
    private long solveFinish;
    private long setupTime;
    private boolean autoType = AUTOTYPE_DEFAULT;
    public SolverMode mode;
    private RobotWrapper bot;
    private SudokuScreenIO ssio;

    public SudokuJava() {
        this(INPUT_FILE);
    }

    public SudokuJava(String file) {
        startTime = System.nanoTime();
        ssio = new SudokuScreenIO();
        fileSetup(new File(file));
        setupFinish = System.nanoTime();
        setupTime = setupFinish - startTime;
    }

    public SudokuJava(byte[][] array, Speed speed) {
        startTime = System.nanoTime();
        if (array.length != 9) {
            throw new IllegalArgumentException();
        }
        for (byte[] bytes : array) {
            if (bytes.length != 9) {
                throw new IllegalArgumentException();
            }
        }
        ssio = new SudokuScreenIO();
        this.mode = new SolverMode(array, speed);
        tiles = mode.tileArray;
        CandidateGeneration.generate(tiles, candidates);
        this.setupFinish = System.nanoTime();
        this.setupTime = setupFinish - startTime;
    }

    public SudokuJava(int topLeftX,
                      int topLeftY,
                      int imgWidth,
                      int imgHeight,
                      boolean autoType,
                      Speed speed) {
        this(topLeftX, topLeftY, imgWidth, imgHeight, 3000, autoType, speed);
    }

    public SudokuJava(int topLeftX,
                      int topLeftY,
                      int imageWidth,
                      int imageHeight,
                      int imageDelay,
                      boolean autoType,
                      Speed speed) {
        this.startTime = System.nanoTime();
        ssio = new SudokuScreenIO();
        this.mode = new SolverMode(topLeftX, topLeftY, imageWidth, imageHeight, imageDelay, speed);
        try {
            Thread.sleep(imageDelay);
        } catch (InterruptedException ex) {
            System.err.println("Interrupted.");
        }
        this.autoType = autoType;
        tiles = ssio.readTiles(mode);
        CandidateGeneration.generate(tiles, candidates);
        this.setupFinish = System.nanoTime();
        this.setupTime = setupFinish - startTime;
    }

    public void run() {
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

    private void fileSetup(File file) {
        mode = SudokuFileParser.parse(file);
        if (mode == null) {
            isValid = false;
            return;
        }
        if (mode.isImage()) {
            try {
                Thread.sleep(mode.imageCaptureDelay);
                tiles = ssio.readTiles(mode);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        } else {
            tiles = mode.tileArray;
        }
        CandidateGeneration.generate(tiles, candidates);
    }

    /**
     * Writes to output + type values if finished
     */
    private void onFinish() {
        if (mode.doWriteToFile()) writeToFile();
        if (autoType && mode.isImage()) ssio.typeValues(mode, tiles);
        General.printTiles(tiles);
        long end = System.nanoTime();
        long outputTime = end - solveFinish;
        System.out.println("Completed in " + (end - startTime) / 1000000.0 + " milliseconds.");
        System.out.printf("Setup:  %010.3fms%n", setupTime / 1000000.0);
        System.out.printf("Solve:  %010.3fms%n", solveTime / 1000000.0);
        System.out.printf("Output: %010.3fms%n", outputTime / 1000000.0);
    }

    public byte[][] getTiles() {
        return tiles;
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
}
