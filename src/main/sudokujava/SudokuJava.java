package sudokujava;

import sudokujava.algorithm.General;
import sudokujava.algorithm.Solver;
import util.SudokuScreenIO;

import java.io.File;

public class SudokuJava {
    private final byte[][] tiles;
    private final long startTime;
    private final long setupTime;
    private final long setupFinish;
    private final SolverSpeed speed;
    private boolean autoType = true;
    private SudokuFile sudokuFile;
    public String outputFile;

    public SudokuJava(String file, SolverSpeed speed) {
        startTime = System.nanoTime();
        sudokuFile = SudokuFileIO.parse(new File(file));
        if (sudokuFile == null) throw new RuntimeException();
        this.speed = speed;
        tiles = sudokuFile.getTiles();
        setupFinish = System.nanoTime();
        setupTime = setupFinish - startTime;
    }

    public SudokuJava(byte[][] array, SolverSpeed speed) {
        startTime = System.nanoTime();
        if (array.length != 9) {
            throw new IllegalArgumentException();
        }
        for (byte[] bytes : array) {
            if (bytes.length != 9) {
                throw new IllegalArgumentException();
            }
        }
        tiles = array;
        this.speed = speed;
        this.setupFinish = System.nanoTime();
        this.setupTime = setupFinish - startTime;
    }

    public SudokuJava(int topLeftX,
                      int topLeftY,
                      int imageWidth,
                      int imageHeight,
                      int imageDelay,
                      boolean autoType,
                      SolverSpeed speed) {
        this.startTime = System.nanoTime();
        this.autoType = autoType;
        sudokuFile = new ImageSudokuFile(topLeftX, topLeftY, imageWidth, imageHeight, imageDelay);
        tiles = sudokuFile.getTiles();
        this.speed = speed;
        this.setupFinish = System.nanoTime();
        this.setupTime = setupFinish - startTime;
    }

    public void run() {
        Solver solver = new Solver();
        solver.solve(tiles, speed);
        if (outputFile != null) SudokuFileIO.writeTiles(outputFile, tiles);
        if (autoType && sudokuFile != null && sudokuFile instanceof ImageSudokuFile)
            new SudokuScreenIO().typeValues((ImageSudokuFile) sudokuFile, tiles);
        General.printTiles(tiles);
        long end = System.nanoTime();
        long outputTime = end - solver.getSolveFinish();
        System.out.println("Completed in " + (end - startTime) / 1000000.0 + " milliseconds.");
        System.out.printf("Setup:  %010.3fms%n", setupTime / 1000000.0);
        System.out.printf("Solve:  %010.3fms%n", solver.getSolveTime() / 1000000.0);
        System.out.printf("Output: %010.3fms%n", outputTime / 1000000.0);
    }

    public byte[][] getTiles() {
        return tiles;
    }
}
