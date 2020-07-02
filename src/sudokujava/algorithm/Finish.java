package sudokujava.algorithm;

public class Finish {
    /**
     * returns true if the sudoku is done
     *
     * @param tiles valid sudoku puzzle
     */
    public static boolean isFinished(byte[][] tiles) {
        for (byte[] tile : tiles) {
            if (General.arrayContains(tile, (byte) 0)) {
                return false;
            }
        }
        return true;
    }
}
