package cli;

import org.junit.jupiter.api.Test;
import sudokujava.SolverMode;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CLISettingsTest {
    @Test
    public void TestGenerate() {
        String argStr = "-f ./data/input.txt -s 3 -i 1 2 3 4 -d 1000 -p -o ./data/output.txt";
        String[] parameters = argStr.split(" ");
        CLISettings expected = new CLISettings();
        expected.inputFile = "./data/input.txt";
        expected.speed = SolverMode.Speed.getSpeed('3');
        expected.topLeftX = 1;
        expected.topLeftY = 2;
        expected.imgWidth = 3;
        expected.imgHeight = 4;
        expected.imgDelay = 1000;
        expected.debugPrinted = true;
        expected.outputFile = "./data/output.txt";
        assertEquals(expected, SudokuCLI.generateSettings(parameters));
    }

}