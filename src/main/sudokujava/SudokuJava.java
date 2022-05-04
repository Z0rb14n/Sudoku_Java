package sudokujava;

import sudokujava.algorithm.*;

import java.util.ArrayList;

import static sudokujava.SolverSpeed.*;

public class SudokuJava {
    private byte[][] tiles = new byte[9][9];
    private ArrayList<Byte>[][] candidates = (ArrayList<Byte>[][]) new ArrayList[9][9];
    private boolean isComplete = false;
    private final static boolean DEBUG = false;
    private static final int LOOP_LIMIT = 200;
    private long startTime;
    private long setupFinish;
    private long solveTime;
    private long solveFinish;
    private long setupTime;
    public SolverMode mode;

    public SudokuJava(SolverMode mode) {
        this.mode = mode;
    }

    public void run() {
        boolean isValid = true;
        if (!isValid) return;
        final long startRunTime = System.nanoTime();
        if (mode.getSolverSpeed() == RECURSE) {
            Recursive.solve(tiles);
        } else {
            for (int loopNum = 0; !isComplete && isValid && loopNum <= LOOP_LIMIT; loopNum++) {
                if (mode.getSolverSpeed().isGreaterThan(VERY_SLOW)) {
                    XWing.solve(tiles, candidates, mode); //double check that it works
                }
                if (mode.getSolverSpeed().isGreaterThan(SLOW)) {
                    HiddenPair.solve(candidates, mode); //double check that itworks
                }
                if (mode.getSolverSpeed().isGreaterThan(SLOW)) {
                    Omission.solve(tiles, candidates, mode);
                }
                if (mode.getSolverSpeed().isGreaterThan(MEDIUM)) {
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
     * Writes to output + type values if finished
     */
    private void onFinish() {
        mode.onFinish(tiles);
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
}
