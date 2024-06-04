package sudokujava;

public class Main {
    public static void main(String[] args) {
        SudokuJava sj = new SudokuJava("./data/input.txt", SolverSpeed.VERY_SLOW);
        sj.outputFile = "./data/output.txt";
        sj.run();
    }
}
