package sudokujava.algorithm;

import sudokujava.SolverSpeed;

import static sudokujava.SolverSpeed.*;

/**
 * Class that handles the solving of tile arrays.
 */
public class Solver {
    public int loopLimit = 200;
    private long solveTime;
    private long solveFinish;

    public void solve(byte[][] tiles, SolverSpeed speed, boolean checkValidity) {
        long startTime = System.nanoTime();
        if (speed == SolverSpeed.RECURSE) {
            Recursive.solve(tiles);
            solveFinish = System.nanoTime();
            solveTime = solveFinish - startTime;
            return;
        }
        Candidates[][] candidates = new Candidates[9][9];
        CandidateGeneration.generate(tiles, candidates);
        boolean complete = false;

        for (int loopNum = 0; !complete && loopNum <= loopLimit; loopNum++) {
            if (speed.isGreaterThan(VERY_SLOW)) {
                XWing.solve(tiles, candidates); //double check that it works
            }
            if (speed.isGreaterThan(SLOW)) {
                HiddenPair.solve(candidates); //double check that it works
            }
            if (speed.isGreaterThan(SLOW)) {
                Omission.solve(tiles, candidates);
            }
            if (speed.isGreaterThan(MEDIUM)) {
                NakedPair.solve(tiles, candidates);
            }
            if (checkValidity) {
                DebugChecks.checkValid(tiles, candidates);
            }
            OpenSingle.solve(tiles, candidates);
            NakedSingle.solve(tiles, candidates);
            HiddenSingle.solve(tiles, candidates);
            complete = Finish.isFinished(tiles);
        }
        if (!complete) {
            System.out.println("Sorry, either we're bad or the loop limit is too low.");
            System.out.println("Resorting to recursive solution.");
            Recursive.solve(tiles);
        }
        solveFinish = System.nanoTime();
        solveTime = solveFinish - startTime;
    }

    public long getSolveTime() {
        return solveTime;
    }

    public long getSolveFinish() {
        return solveFinish;
    }
}
